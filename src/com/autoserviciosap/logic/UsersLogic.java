package com.autoserviciosap.logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.AppProperties;
import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.model.Permission;
import com.autoserviciosap.model.User;
import com.autoserviciosap.resources.EmailResource;
import com.autoserviciosap.resources.InternationalizationStateless;

@Stateless
public class UsersLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Inject
	private SessionLogic sessionLogic;

	@Inject
	private EmailResource emailLogic;

	@Inject
	private AppProperties appProperties;
	
	@Inject 
	InternationalizationStateless i18n;

	private void fixParaEvitarPermisosDuplicadosYFalsosReseteos(User user) {

		// PERMISOS DUPLICADOS
		List<Permission> permissions = user.getPermissions();
		if (permissions == null) return;
		if (permissions.size() == 1) return;
		List<Permission> permissionsUnique = permissions.stream().collect(Collectors.groupingBy(e -> e.getId()))
				.values().stream().map(e -> e.get(0)).collect(Collectors.toList());
		user.setPermissions(permissionsUnique);

		// FALSOS RESETEOS
		user.setPasswordRecoveryKey(null);
		user.setPasswordRecoveryExpirationDate(null);

	}

	public List<User> obtenerUsuarios() throws ApiException {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);

		return entityManager.createQuery("SELECT u FROM User AS u", User.class).getResultList();
	}

	public User obtenerUsuarioPorUsername(String username) {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);

		List<User> resultList = entityManager
				.createQuery("SELECT u FROM User AS u LEFT JOIN FETCH u.permissions WHERE u.username = :username",
						User.class)
				.setParameter("username", username).getResultList();
		if (resultList.isEmpty()) throw new ApiException(404,
				"No se encontró un usuario con el nombre de usuario \"" + username + "\"");
		return resultList.get(0);
	}

	private User obtenerUsuarioPorEmail(String email) {
		List<User> resultList = entityManager
				.createQuery("SELECT u FROM User AS u LEFT JOIN FETCH u.permissions WHERE u.email = :email", User.class)
				.setParameter("email", email).getResultList();
		if (resultList
				.isEmpty()) throw new ApiException(404, "No se encontró un usuario con el email \"" + email + "\"");
		return resultList.get(0);
	}

	private User obtenerUsuarioPorPasswordRecoveryKey(String passwordRecoveryKey) {
		List<User> resultList = entityManager.createQuery(
				"SELECT u FROM User AS u LEFT JOIN FETCH u.permissions WHERE u.passwordRecoveryKey = :passwordRecoveryKey",
				User.class).setParameter("passwordRecoveryKey", passwordRecoveryKey).getResultList();
		if (resultList.isEmpty()) throw new ApiException(404,
				"No se encontró un usuario con el passwordRecoveryKey \"" + passwordRecoveryKey + "\"");
		return resultList.get(0);
	}

	public User crearUsuario(User user) throws ApiException {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);

		try {
			obtenerUsuarioPorUsername(user.getUsername());
			throw new ApiException(409, "Ya existe un usuario con el nombre de usuario \"" + user.getUsername() + "\"");
		} catch (ApiException e) {
			if (e.getStatus() != 404) throw e;
		}

		user.setUuid(UUID.randomUUID().toString());
		user.setPassword(SHA256(user.getPassword() + user.getUuid()));
		ApiException.validateBean(user);

		fixParaEvitarPermisosDuplicadosYFalsosReseteos(user);

		entityManager.merge(user); 

		return user;
	}

	public User actualizarUsuario(String username, User newUser) throws ApiException {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);

		User oldUser = obtenerUsuarioPorUsername(username);

		if (!oldUser.getUsername().equals(newUser.getUsername())) {
			try {
				obtenerUsuarioPorUsername(newUser.getUsername());
				throw new ApiException(409,
						"Ya existe un usuario con el nombre de usuario \"" + newUser.getUsername() + "\"");
			} catch (ApiException e) {
				if (e.getStatus() != 404) throw e;
			}
		}

		newUser.setUuid(oldUser.getUuid());

		if (newUser.getPassword() == null) newUser.setPassword(oldUser.getPassword());

		ApiException.validateBean(newUser);

		fixParaEvitarPermisosDuplicadosYFalsosReseteos(newUser);

		return entityManager.merge(newUser);
	}

	public void eliminarUsuario(String username) throws ApiException {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);

		User user = obtenerUsuarioPorUsername(username);
		entityManager.remove(user);
	}

	public void borrarUsuarios(String[] usernames) {
		for (String username : usernames) {
			eliminarUsuario(username);
		}
	}

	/**
	 * Actualiza la contraseña del usuario con el username proporcionado despues
	 * de confirmar que la contraseña anterior proporcionada y la contraseña
	 * actual del usuario son las mismas
	 * 
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 * @throws ApiException
	 *             con 409 si no coincide oldPassword con la contraseña actual
	 *             del usuario en cuestion
	 */
	public void cambiarContraseña(String username, String oldPassword, String newPassword) throws ApiException {
		User user = encontrarUsuarioPorCredenciales(username, oldPassword);
		if (user == null) throw new ApiException(409,
				"La contraseña anterior proporcionada no coincide con la contraseña actual del usuario");
		user.setPassword(SHA256(newPassword + user.getUuid()));
	}

	/**
	 * Actualiza la dirección de correo electrónico del usuario con el username
	 * proporcionado despues de confirmar que la contraseña proporcionada y la
	 * contraseña del usuario son las mismas
	 * 
	 * @param username
	 * @param password
	 * @param newEmail
	 * @throws ApiException
	 *             con 401 si no coincide password con la contraseña del usuario
	 *             en cuestion
	 */
	public void cambiarEmail(String username, String password, String newEmail) throws ApiException {
		User user = encontrarUsuarioPorCredenciales(username, password);
		if (user == null) throw new ApiException(401, "La contraseña no coincide con la del usuario");
		user.setEmail(newEmail);
	}

	/**
	 * Encuentra un usuario a partir de sus credenciales
	 * 
	 * @param username
	 * @param password
	 * @return el usuario cuyas credenciales coinciden con los parametros
	 *         recibidos o null
	 */
	public User encontrarUsuarioPorCredenciales(String username, String password) {
		List<User> resultList = entityManager
				.createQuery("SELECT u FROM User AS u WHERE u.username = :username", User.class)
				.setParameter("username", username).getResultList();
		User user = resultList.isEmpty() ? null : resultList.get(0);
		return user;
	}

	/**
	 * Calcula el hash con SHA-256 de una entrada en forma de texto
	 * 
	 * @param input
	 *            una cadena de texto que será hasheada
	 * @return la representación hexadecimal en texto del resultado de hasheo
	 *         con SHA-256
	 */
	public static String SHA256(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(input.getBytes());
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < digest.length; i++)
				builder.append(String.format("%02X", digest[i] & 0x000000FF));
			return builder.toString().toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void enviarCorreoDeRecuperacionDeContraseña(String email) {
		
		String idioma = i18n.getSessionLanguage();
		if(idioma == null)
			idioma = "es";
		if("".equals(idioma))
			idioma = "es";

		String uuid = UUID.randomUUID().toString();

		String url = appProperties.getApiUrl() + "/session/password-recovery/" + uuid;

		User user = obtenerUsuarioPorEmail(email);
		user.setPasswordRecoveryKey(uuid);
		user.setPasswordRecoveryExpirationDate(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));

		Map<String, Object> params = new HashMap<>();
		params.put("${password-reset-url}", url);

		if(idioma.equals("es")) {
		emailLogic.enviarTemplate( //
				email, //
				"Recuperación de contraseña de portal de auto servicio SAP", //
				"email-templates/email-portal-password-reset.html", //
				params);
		}
		if(idioma.equals("en")) {
			emailLogic.enviarTemplate( //
					email, //
					"SAP Self Service Portal Password Recovery", //
					"email-templates/email-portal-password-reset_en.html", //
					params);	
		}
	}

	public void actualizarContraseñaPorRecuperacion(String passwordRecoveryKey, String newPassword,
			String newPasswordRepeat) {

		if (newPassword == null || newPasswordRepeat == null
				|| passwordRecoveryKey == null) throw new ApiException(400, "Faltan campos");
		if (!newPassword.equals(newPasswordRepeat)) throw new ApiException(400,
				"Las contraseñas no coinciden, asegurese de escribirlas correctamente y pruebe de nuevo");
		User user = obtenerUsuarioPorPasswordRecoveryKey(passwordRecoveryKey);
		Timestamp expirationDate = user.getPasswordRecoveryExpirationDate();
		Timestamp currentDate = Timestamp.valueOf(LocalDateTime.now());
		if (currentDate.compareTo(expirationDate) > 0) throw new ApiException(403,
				"La clave de recuperación ha expirado, solicita otra y pruebe de nuevo");

		user.setPassword(SHA256(newPassword + user.getUuid()));
		user.setPasswordRecoveryExpirationDate(null);
		user.setPasswordRecoveryKey(null);
	}

}
