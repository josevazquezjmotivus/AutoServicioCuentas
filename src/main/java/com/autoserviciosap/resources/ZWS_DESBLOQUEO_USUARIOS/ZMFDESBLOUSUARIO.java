
package com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EMAIL" type="{urn:sap-com:document:sap:rfc:functions}char241" minOccurs="0"/&gt;
 *         &lt;element name="NEW_PASSWORD" type="{urn:sap-com:document:sap:rfc:functions}char40" minOccurs="0"/&gt;
 *         &lt;element name="OPCION" type="{urn:sap-com:document:sap:rfc:functions}char1" minOccurs="0"/&gt;
 *         &lt;element name="PASSWORD" type="{urn:sap-com:document:sap:rfc:functions}char40" minOccurs="0"/&gt;
 *         &lt;element name="USERNAME" type="{urn:sap-com:document:sap:rfc:functions}char12" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"destino",
    "email",
    "newpassword",
    "opcion",
    "password",
    "username"
})
@XmlRootElement(name = "ZMF_DESBLO_USUARIO")
public class ZMFDESBLOUSUARIO {

	@XmlElement(name = "DESTINO")
	private String destino;
    @XmlElement(name = "EMAIL")
    protected String email;
    @XmlElement(name = "NEW_PASSWORD")
    protected String newpassword;
    @XmlElement(name = "OPCION")
    protected String opcion;
    @XmlElement(name = "PASSWORD")
    protected String password;
    @XmlElement(name = "USERNAME")
    protected String username;

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMAIL() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMAIL(String value) {
        this.email = value;
    }

    /**
     * Obtiene el valor de la propiedad newpassword.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNEWPASSWORD() {
        return newpassword;
    }

    /**
     * Define el valor de la propiedad newpassword.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNEWPASSWORD(String value) {
        this.newpassword = value;
    }

    /**
     * Obtiene el valor de la propiedad opcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPCION() {
        return opcion;
    }

    /**
     * Define el valor de la propiedad opcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPCION(String value) {
        this.opcion = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPASSWORD() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPASSWORD(String value) {
        this.password = value;
    }

    /**
     * Obtiene el valor de la propiedad username.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSERNAME() {
        return username;
    }

    /**
     * Define el valor de la propiedad username.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSERNAME(String value) {
        this.username = value;
    }

	public String getDESTINO() {
		return destino;
	}

	public void setDESTINO(String destino) {
		this.destino = destino;
	}

}
