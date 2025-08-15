package com.autoserviciosap.resources;

import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.autoserviciosap.ApiException;
import com.novell.ldapchai.ChaiUser;
import com.novell.ldapchai.exception.ChaiOperationException;
import com.novell.ldapchai.exception.ChaiUnavailableException;
import com.novell.ldapchai.provider.ChaiProvider;
import com.novell.ldapchai.provider.ChaiProviderFactory;

@Stateless
public class ActiveDirectoryResource {

	private static final boolean debug = true;

	private LdapContext getLdapContext(String ldapURL, String ldapBindDN, String ldapBindPW) {

		try {

			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");

			// te los dá el administrador del LDAP
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_PRINCIPAL, ldapBindDN);
			env.put(Context.SECURITY_CREDENTIALS, ldapBindPW);

			if (debug) {
				System.out.println("getLdapContext(");
				System.out.println("    ldapURL = " + ldapURL);
				System.out.println("    ldapBindDN = " + ldapBindDN);
				System.out.println("    ldapBindPW = " + ldapBindPW);
				System.out.println("	) {");
				System.out.println(
						"  env(Context.INITIAL_CONTEXT_FACTORY) = " + env.get(Context.INITIAL_CONTEXT_FACTORY));
				System.out.println(
						"  env(Context.SECURITY_AUTHENTICATION) = " + env.get(Context.SECURITY_AUTHENTICATION));
				System.out.println("  env(Context.PROVIDER_URL)            = " + env.get(Context.PROVIDER_URL));
				System.out.println("  env(Context.SECURITY_PRINCIPAL)      = " + env.get(Context.SECURITY_PRINCIPAL));
				System.out.println("  env(Context.SECURITY_CREDENTIALS)    = " + env.get(Context.SECURITY_CREDENTIALS));
				System.out.println("}");
			}

			return new InitialLdapContext(env, null);

		} catch (NamingException e) {
			e.printStackTrace();
			throw new ApiException(500, "" //
					+ "Ocurrió un error al crear el contexto de Active Directory. "
					+ "Consulte con el administrador del sistema para que verifique la configuración del dominio.");
		}
	}

	private ChaiProvider getChaiProvider(String ldapURL, String ldapBindDN, String ldapBindPW) {

		try {
			final ChaiProviderFactory chaiProviderFactory = ChaiProviderFactory.newProviderFactory();

			if (debug) {
				System.out.println("getLdapContext(");
				System.out.println("    ldapURL = " + ldapURL);
				System.out.println("    ldapBindDN = " + ldapBindDN);
				System.out.println("    ldapBindPW = " + ldapBindPW);
				System.out.println("	) {");
				System.out.println("  newProvider(" + ldapURL + ", " + ldapBindDN + ", " + ldapBindPW + ")");
				System.out.println("}");
			}

			return chaiProviderFactory.newProvider(ldapURL, ldapBindDN, ldapBindPW);
		} catch (ChaiUnavailableException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error al obtener el ChaiProvider");
		}
	}

	private Optional<String> getNombreLdap(ActiveDirectoryConfiguration config, String account) {

		final String ACCOUNT_TOKEN = "___$account___";

		String ldapURL = config.getLdapURL();
		String ldapBindDN = config.getLdapBindDN();
		String ldapBindPW = config.getLdapBindPW();
		String ldapSearchBase = config.getLdapSearchBase();

		account = escapeForDN(escapeForSearchFilter(account));

		String ldapSearchFilterTemplate = config.getLdapSearchFilterTemplate();

		System.out.println("ldapSearchFilterTemplate = " + ldapSearchFilterTemplate);

		if (ldapSearchFilterTemplate == null
				|| !ldapSearchFilterTemplate.contains(ACCOUNT_TOKEN)) throw new ApiException(500,
						"Ocurrió un error al reemplazar el token de cuenta en el filtro de ldap. "
								+ "Consulte con el administrador del sistema para que verifique la configuración del dominio.");

		String ldapSearchFilter = ldapSearchFilterTemplate.replace(ACCOUNT_TOKEN, account);

		try {

			LdapContext ctx = getLdapContext(ldapURL, ldapBindDN, ldapBindPW);

			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			if (debug) {
				System.out.println("getNombreLdap(");
				System.out.println("    config = " + config.toString());
				System.out.println("    account = " + account);
				System.out.println("	) {");
				System.out.println("  LdapContext.search(" + ldapSearchBase + ", " + ldapSearchFilter + ", "
						+ searchControls + ")");
				System.out.println("}");
			}

			NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, ldapSearchFilter, searchControls);

			if (!results.hasMoreElements()) return Optional.empty();

			SearchResult searchResult = (SearchResult) results.nextElement();
			String nombreLdap = searchResult.getNameInNamespace();
			return Optional.of(nombreLdap);

		} catch (NamingException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error al obtener el nombre LDAP del usuario con el email dado");
		}
	}

	private void desbloquarUsuarioConChai(String ldapURL, String ldapBindDN, String ldapBindPW, String nombreLdap) {
		try {
			ChaiProvider chaiProvider = getChaiProvider(ldapURL, ldapBindDN, ldapBindPW);
			ChaiUser user = chaiProvider.getEntryFactory().newChaiUser(nombreLdap);
			user.unlockPassword();
		} catch (ChaiUnavailableException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error (E1) al desbloquear el usuario");
		} catch (ChaiOperationException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error (E2) al desbloquear el usuario");
		}
	}

	public void desbloquearUsuario(List<ActiveDirectoryConfiguration> configurations, String email) {

		String nombreLdap = null;
		ActiveDirectoryConfiguration config = null;

		for (ActiveDirectoryConfiguration e : configurations) {
			Optional<String> optional = getNombreLdap(e, email);
			if (!optional.isPresent()) continue;
			nombreLdap = optional.get();
			config = e;
			break;
		}

		if (nombreLdap == null) new ApiException(404, "No se encontró el usuario de active directory");

		String ldapURL = config.getLdapURL();
		String ldapBindDN = config.getLdapBindDN();
		String ldapBindPW = config.getLdapBindPW();

		desbloquarUsuarioConChai(ldapURL, ldapBindDN, ldapBindPW, nombreLdap);

	}

	/**
	 * Escape a string for usage in an LDAP DN to prevent LDAP injection
	 * attacks. There are certain characters that are considered special
	 * characters in a DN. The exhaustive list is the following:
	 * ',','\','#','+','<','>',';','"','=', and leading or trailing spaces
	 * 
	 * @param name
	 * @return
	 */
	public static String escapeForDN(String name) {
		StringBuilder sb = new StringBuilder();

		if (name.length() > 0 && ((name.charAt(0) == ' ') || (name.charAt(0) == '#'))) {
			sb.append('\\'); // add the leading backslash if needed
		}

		for (int i = 0; i < name.length(); i++) {
			char curChar = name.charAt(i);
			switch (curChar) {
			case '\\':
				sb.append("\\\\");
				break;
			case ',':
				sb.append("\\,");
				break;
			case '+':
				sb.append("\\+");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '<':
				sb.append("\\<");
				break;
			case '>':
				sb.append("\\>");
				break;
			case ';':
				sb.append("\\;");
				break;
			default:
				sb.append(curChar);
				break;
			}
		}

		if (name.length() > 1 && name.charAt(name.length() - 1) == ' ') {
			sb.insert(sb.length() - 1, '\\'); // add the trailing backslash if
												// needed
		}

		return sb.toString();
	}

	/**
	 * Escape a string for usage in an LDAP DN to prevent LDAP injection
	 * attacks.
	 * 
	 * @param filter
	 * @return
	 */
	public static String escapeForSearchFilter(String filter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < filter.length(); i++) {
			char curChar = filter.charAt(i);
			switch (curChar) {
			case '\\':
				sb.append("\\5c");
				break;
			case '*':
				sb.append("\\2a");
				break;
			case '(':
				sb.append("\\28");
				break;
			case ')':
				sb.append("\\29");
				break;
			case '\u0000':
				sb.append("\\00");
				break;
			default:
				sb.append(curChar);
				break;
			}
		}
		return sb.toString();
	}

	public static void main(String... strings) {
		System.out.println(escapeForDN(escapeForSearchFilter("bardackx@gmail.com")));
	}
}
