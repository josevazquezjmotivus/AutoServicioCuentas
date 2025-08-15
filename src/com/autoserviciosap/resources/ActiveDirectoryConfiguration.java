package com.autoserviciosap.resources;

public class ActiveDirectoryConfiguration {

	private String ldapURL;
	private String ldapBindDN;
	private String ldapBindPW;
	private String ldapSearchBase;
	private String ldapSearchFilterTemplate;

	public String getLdapURL() {
		return ldapURL;
	}

	public void setLdapURL(String ldapURL) {
		this.ldapURL = ldapURL;
	}

	public String getLdapBindDN() {
		return ldapBindDN;
	}

	public void setLdapBindDN(String ldapBindDN) {
		this.ldapBindDN = ldapBindDN;
	}

	public String getLdapBindPW() {
		return ldapBindPW;
	}

	public void setLdapBindPW(String ldapBindPW) {
		this.ldapBindPW = ldapBindPW;
	}

	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	public String getLdapSearchFilterTemplate() {
		return ldapSearchFilterTemplate;
	}

	public void setLdapSearchFilterTemplate(String ldapSearchFilterTemplate) {
		this.ldapSearchFilterTemplate = ldapSearchFilterTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ldapBindDN == null) ? 0 : ldapBindDN.hashCode());
		result = prime * result + ((ldapBindPW == null) ? 0 : ldapBindPW.hashCode());
		result = prime * result + ((ldapSearchBase == null) ? 0 : ldapSearchBase.hashCode());
		result = prime * result + ((ldapSearchFilterTemplate == null) ? 0 : ldapSearchFilterTemplate.hashCode());
		result = prime * result + ((ldapURL == null) ? 0 : ldapURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ActiveDirectoryConfiguration other = (ActiveDirectoryConfiguration) obj;
		if (ldapBindDN == null) {
			if (other.ldapBindDN != null) return false;
		} else if (!ldapBindDN.equals(other.ldapBindDN)) return false;
		if (ldapBindPW == null) {
			if (other.ldapBindPW != null) return false;
		} else if (!ldapBindPW.equals(other.ldapBindPW)) return false;
		if (ldapSearchBase == null) {
			if (other.ldapSearchBase != null) return false;
		} else if (!ldapSearchBase.equals(other.ldapSearchBase)) return false;
		if (ldapSearchFilterTemplate == null) {
			if (other.ldapSearchFilterTemplate != null) return false;
		} else if (!ldapSearchFilterTemplate.equals(other.ldapSearchFilterTemplate)) return false;
		if (ldapURL == null) {
			if (other.ldapURL != null) return false;
		} else if (!ldapURL.equals(other.ldapURL)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ActiveDirectoryConfiguration [ldapURL=" + ldapURL + ", ldapBindDN=" + ldapBindDN + ", ldapBindPW="
				+ ldapBindPW + ", ldapSearchBase=" + ldapSearchBase + ", ldapSearchFilterTemplate="
				+ ldapSearchFilterTemplate + "]";
	}

}
