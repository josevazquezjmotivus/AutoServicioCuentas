
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
 *         &lt;element name="MESSAGE" type="{urn:sap-com:document:sap:rfc:functions}char220"/&gt;
 *         &lt;element name="PASSWORD2" type="{urn:sap-com:document:sap:rfc:functions}char40"/&gt;
 *         &lt;element name="TIPO" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
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
    "message",
    "password2",
    "tipo",
    "titulo"
})
@XmlRootElement(name = "ZMF_DESBLO_USUARIOResponse")
public class ZMFDESBLOUSUARIOResponse {

    @XmlElement(name = "MESSAGE", required = true)
    protected String message;
    @XmlElement(name = "PASSWORD2", required = true)
    protected String password2;
    @XmlElement(name = "TIPO", required = true)
    protected String tipo;
    @XmlElement(name = "TITULO", required = true)
    protected String titulo;

    /**
     * Obtiene el valor de la propiedad message.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMESSAGE() {
        return message;
    }

    /**
     * Define el valor de la propiedad message.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMESSAGE(String value) {
        this.message = value;
    }

    /**
     * Obtiene el valor de la propiedad password2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPASSWORD2() {
        return password2;
    }

    /**
     * Define el valor de la propiedad password2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPASSWORD2(String value) {
        this.password2 = value;
    }

    /**
     * Obtiene el valor de la propiedad tipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIPO() {
        return tipo;
    }

    /**
     * Define el valor de la propiedad tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIPO(String value) {
        this.tipo = value;
    }

	public String getTITULO() {
		return titulo;
	}

	public void setTITULO(String tITULO) {
		titulo = tITULO;
	}

    
}
