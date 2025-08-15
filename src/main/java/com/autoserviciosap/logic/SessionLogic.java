package com.autoserviciosap.logic;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.endpoints.ConfigurationEndpoints;
import com.autoserviciosap.model.Permission;
import com.autoserviciosap.model.User;
import com.autoserviciosap.resources.InternationalizationStateless;

@RequestScoped
public class SessionLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Context
	private HttpServletRequest request;

	@Inject
	private UsersLogic usersLogic;
	
	@Inject 
	InternationalizationStateless i18n;

	private User user;

	/**
	 * Almacena el uuid del usuario como variable de sesión en el caso de una
	 * autenticación éxitosa, de lo contrario deja intactas las variables de
	 * sesión.
	 * 
	 * @param username
	 *            nombre de usuario
	 * @param password
	 *            contraseña del usuario
	 * @throws ApiException
	 *             con el código 401 en caso de que las credenciales no
	 *             coincidan con ningún usuario
	 */
	public void iniciarSesion(String username, String password) throws ApiException {
		User user = usersLogic.encontrarUsuarioPorCredenciales(username, password);
		if (user == null) throw new ApiException(401, i18n.get("error-login"));
		
		String PassEncoded = UsersLogic.SHA256(password + user.getUuid());
		//System.out.println("pass:"+user.getPassword());
		//System.out.println("pass2:"+PassEncoded);
		if(!user.getPassword().equals(PassEncoded)) {
			 throw new ApiException(401, i18n.get("error-login"));
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("user-uuid", user.getUuid());
		session.setAttribute(ConfigurationEndpoints.CODIGO_DE_IDIOMA, null);
	}

	/**
	 * En caso de que exista una sesión, la invalida
	 */
	public void cerrarSesion() throws ApiException {
		HttpSession session = request.getSession(false);
		if (session != null) session.invalidate();
		// else throw new ApiException(404, "No hay una sesión que cerrar");
	}

	/**
	 * Evalua si el usuario autenticado tiene por lo menos uno de los permisos
	 * recibidos y lo retorna
	 * 
	 * @param permissions
	 * @return el usuario autenticado que tiene ala menos uno de los permisos
	 *         recibidos
	 * @throws ApiException
	 *             con un 401 si el usuario no está autenticado (vease
	 *             {@link #obtenerUsuarioAutenticado()}) o 403 si el usuario
	 *             está autenticado pero carece de alguno de los permisos
	 *             recibidos
	 */
	public User obtenerUsuarioAutorizado(PermissionEnum... permissions) throws ApiException {
		User u = obtenerUsuarioAutenticado();
		for (PermissionEnum e : permissions)
			for (Permission p : u.getPermissions())
				if (e.getId().equals(p.getId())) return u;
		throw new ApiException(403, "Se requiere un permiso especial para realizar esta operación");
	}

	/**
	 * Es el equivalente a invocar <code>obtenerUsuarioAutenticado(false)</code>
	 * 
	 * @return vea {@link #obtenerUsuarioAutenticado(boolean)}.
	 */
	public User obtenerUsuarioAutenticado() throws ApiException {
		return obtenerUsuarioAutenticado(true);
	}

	/**
	 * Retorna el usuario autenticado
	 * 
	 * @return el usuario autenticado
	 * @throws ApiException
	 *             con un 401 si no hay un usuario autenticado o 500 si sucedió
	 *             un error extraño
	 */
	public User obtenerUsuarioAutenticado(boolean conPermisos) throws ApiException {

		if (user == null) {

			HttpSession session = request.getSession(false);
			if (session == null) throw new ApiException(401,
					"Es necesario que el usuario esté autenticado para realizar esta operación");

			String idioma = (String) session.getAttribute(ConfigurationEndpoints.CODIGO_DE_IDIOMA);
			String uuid = "";
			//Se valida idioma ya que si se seleciono luego tiene sesion y no permite entrar
			if(idioma == null) {
				uuid = (String) session.getAttribute("user-uuid");
				if (uuid == null) throw new ApiException(500,
						"Por algún motivo la sesión carece de valores indispensables para la autenticación, "
								+ "cierre e inicie sesión de nuevo");
			}else {
				throw new ApiException(401,
						"Es necesario que el usuario esté autenticado para realizar esta operación");
			}
			
			

			if (conPermisos) {
				System.out.println("uuid user:"+uuid);
				List<User> list = entityManager //
						.createQuery( //
								"SELECT u FROM User AS u LEFT JOIN FETCH u.permissions WHERE u.uuid = :uuid", //
								User.class) //
						.setParameter("uuid", uuid) //
						.getResultList();
				user = list.isEmpty() ? null : list.get(0);
			} else {
				user = entityManager.find(User.class, uuid);
			}

			if (user == null)
				throw new ApiException(500, "Por algún motivo no se encontró el usuario presuntamente autenticado, "
						+ "cierre e inicie sesión de nuevo");

		}

		return user;
	}

	/**
	 * Valida que exista una sesión y que tenga un userUuid almacenado, de lo
	 * contrario arroja un 404
	 * 
	 * @throws ApiException
	 */
	public void validarQueExisteSesion() throws ApiException {
		HttpSession session = request.getSession(false);
		if (session == null) throw new ApiException(404, "");
		String userUuid = (String) session.getAttribute("user-uuid");
		if (userUuid == null) throw new ApiException(404, "");
	}

	public String obtenerUuidDeUsuarioAutenticado() throws ApiException {

		String uuid0 = null;

		if (uuid0 == null) {

			HttpSession session = request.getSession(false);
			if (session == null) throw new ApiException(401,
					"Es necesario que el usuario esté autenticado para realizar esta operación");

			String uuid = (String) session.getAttribute("user-uuid");
			if (uuid == null) throw new ApiException(500,
					"Por algún motivo la sesión carece de valores indispensables para la autenticación, "
							+ "cierre e inicie sesión de nuevo");

		}

		return uuid0;
	}

	public void reestablecerContraseña(String currentPassword, String newPassword, String newPasswordRepeat) {
		if (newPassword == null || newPasswordRepeat == null || currentPassword == null)
			throw new ApiException(400, "Faltan campos para cambiar la contraseña");
		if (!newPassword.equals(newPasswordRepeat)) throw new ApiException(400, "Las contraseñas no coinciden");
		User user = obtenerUsuarioAutenticado();
		usersLogic.cambiarContraseña(user.getUsername(), currentPassword, newPassword);
	}

	public void reestablecerEmail(String currentPassword, String newEmail, String newEmailRepeat) {
		if (newEmail == null || newEmailRepeat == null || currentPassword == null)
			throw new ApiException(400, "Faltan campos para cambiar el email");
		if (!newEmail.equals(newEmailRepeat)) throw new ApiException(400, "Los email no coinciden");
		User user = obtenerUsuarioAutenticado();
		usersLogic.cambiarEmail(user.getUsername(), currentPassword, newEmail);
	}

	public void enviarCorreoDeRecuperacionDeContraseña(String email) {
		usersLogic.enviarCorreoDeRecuperacionDeContraseña(email);
	}
}
