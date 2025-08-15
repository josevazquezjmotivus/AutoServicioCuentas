package com.autoserviciosap.logic;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.model.ActiveDirectoryEnvironment;
import com.autoserviciosap.model.Enterprise;
import com.autoserviciosap.resources.ActiveDirectoryConfiguration;
import com.autoserviciosap.resources.ActiveDirectoryResource;
import com.autoserviciosap.resources.CaptchaValidatorResource;

@Stateless
public class ActiveDirectoryEnvironmentLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Inject
	private SessionLogic sessionLogic;

	@Inject
	private EnterprisesLogic enterprisesLogic;

	@Inject
	private ActiveDirectoryResource activeDirectoryResource;

	@Inject
	private CaptchaValidatorResource captchaValidatorResource;
	
	@Inject
	private EventoLogic eventoLogica;

	public List<ActiveDirectoryEnvironment> obtenerAmbientesDeActiveDirectoryDeTodasLasEmpresas() {
		return entityManager.createQuery("" //
				+ "SELECT e " //
				+ "FROM ActiveDirectoryEnvironment AS e " //
				+ "", ActiveDirectoryEnvironment.class).getResultList();
	}

	public ActiveDirectoryEnvironment createActiveDirectoryEnvironment(String enterpriseUuid,
			ActiveDirectoryEnvironment e) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);

		Enterprise f = enterprisesLogic.obtenerEmpresa(enterpriseUuid);
		e.setUuid(UUID.randomUUID().toString());
		e.setEnterprise(f);
		if (e.getDomains() != null) e.getDomains().forEach(g -> {
			g.setUuid(UUID.randomUUID().toString());
			g.setEnvironment(e);
		});

		ApiException.validateBean(e);

		entityManager.persist(e);
		return e;
	}

	public ActiveDirectoryEnvironment obtenerAmbienteDeActiveDirectory(String uuid) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		List<ActiveDirectoryEnvironment> results = entityManager.createQuery("" //
				+ "SELECT e " //
				+ "FROM ActiveDirectoryEnvironment AS e " //
				+ "LEFT JOIN FETCH e.domains " //
				+ "WHERE e.uuid = :uuid " //
				+ "", ActiveDirectoryEnvironment.class) //
				.setParameter("uuid", uuid) //
				.getResultList();
		ActiveDirectoryEnvironment e = results.isEmpty() ? null : results.get(0);
		if (e == null) throw new ApiException(404, "No se encontró un dominio con uuid \"" + uuid + "\"");
		return e;
	}

	public ActiveDirectoryEnvironment actualizarAmbienteDeActiveDirectory(String uuid, ActiveDirectoryEnvironment in) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		ActiveDirectoryEnvironment e = obtenerAmbienteDeActiveDirectory(uuid);

		in.setEnterprise(e.getEnterprise());
		if (in.getDomains() != null) {
			in.getDomains().forEach(f -> f.setEnvironment(in));
			in.getDomains().forEach(f -> {
				if (f.getUuid() == null || f.getUuid().isEmpty()) {
					f.setUuid(UUID.randomUUID().toString());
				}
			});
		}
		ApiException.validateBean(in);

		entityManager.merge(in);

		return e;
	}

	public void eliminarDominio(String uuid) throws ApiException {
		ActiveDirectoryEnvironment e = obtenerAmbienteDeActiveDirectory(uuid);
		if (e.getDomains() != null) e.getDomains().forEach(f -> entityManager.remove(f));
		entityManager.remove(e);
	}

	public void desbloquearUsuarioPorEmail(String domainUuid, String email, String captcha) {

		captchaValidatorResource.validateCaptchaIfEnabled(captcha);

		char[] invalidCharacters = { '\\', ',', '+', '"', '<', '>', ';', '*', '(', ')', '\u0000' };
		for (char invalidCharacter : invalidCharacters) {
			for (char c : email.toCharArray()) {
				if (c == invalidCharacter) throw new ApiException(409, "Caracter invalido '" + c + "'");
			}
		}

		ActiveDirectoryEnvironment e = entityManager.find(ActiveDirectoryEnvironment.class, domainUuid);
		if (e == null) throw new ApiException(404, "No existe dominio con esa uuid");

		List<ActiveDirectoryConfiguration> configurations = e.getDomains().stream().map(f -> {
			ActiveDirectoryConfiguration p = new ActiveDirectoryConfiguration();
			p.setLdapURL(f.getUrl());
			p.setLdapBindDN(f.getBindDn());
			p.setLdapBindPW(f.getBindPw());
			p.setLdapSearchBase(f.getSearchBase());
			p.setLdapSearchFilterTemplate(f.getSearchFilter());
			return p;
		}).collect(Collectors.toList());

		activeDirectoryResource.desbloquearUsuario(configurations, email);
		eventoLogica.crearEvento(GeneralConstants.DES_US_AD, "Se desbloqueo el usuario "+email.toUpperCase(), email.toUpperCase());
	}

}
