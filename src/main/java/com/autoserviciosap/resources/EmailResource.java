package com.autoserviciosap.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.logic.TemplatesLogic;

@Stateless
public class EmailResource {

	@Inject
	private TemplatesLogic templatesLogic;

	private void enviarCorreo(String toEmail, String subject, String html) {
		Multipart m = generarMultipartConAttachments(html);
		enviarCorreo(toEmail, subject, m);
	}

	private static final Logger LOGGER = Logger.getLogger(EmailResource.class.getName());

	private void enviarCorreo(String toEmail, String subject, Multipart content) throws ApiException {

		LOGGER.info(Json.createObjectBuilder().add("email", toEmail).add("subject", subject).build().toString());

		File emailProperties = new File("autoserviciosap-email.properties");
		System.out.println(emailProperties.getAbsolutePath());

		if (!emailProperties.exists()) {
			throw new ApiException(500, "No se encontró el archivo " + emailProperties.getName() + " en la ubicación <"
					+ emailProperties.getAbsolutePath() + "> contacte al administrador");
		}

		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(emailProperties)) {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(500, e.getMessage());
		}

		String from = properties.getProperty("sssap.from");

		String mockup = (String) properties.get("sssap.mail.mockup");
		StringBuilder sb = new StringBuilder();
		if ("true".equalsIgnoreCase(mockup)) {
			sb.append("=========================================================");
			sb.append("from    : " + from + ";\n");
			sb.append("to      : " + toEmail + ";\n");
			sb.append("subject : " + subject + ";\n");
			sb.append("content : \n");
			if (content instanceof MimeMultipart) {
				MimeMultipart mm = (MimeMultipart) content;
				try {
					int n = mm.getCount();
					for (int i = 0; i < n; i++) {
						BodyPart bodyPart = mm.getBodyPart(i);
						sb.append(bodyPart.getContentType()).append('\n');
						sb.append(bodyPart.getContent()).append('\n');
					}
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			} else {
				sb.append(content + "\n");
			}
			sb.append("=========================================================");
			System.out.println(sb);
			return;
		} else {

			if (content instanceof MimeMultipart) {
				MimeMultipart mm = (MimeMultipart) content;
				try {
					int n = mm.getCount();
					for (int i = 0; i < n; i++) {
						BodyPart bodyPart = mm.getBodyPart(i);
						// sb.append(bodyPart.getContentType()).append('\n');
						sb.append(bodyPart.getContent()).append('\n');
					}
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			} else {
				sb.append(content + "\n");
			}
			System.out.println(sb);
		}

		String isAuthenticatorEnabled = properties.getProperty("sssap.authenticator.enabled");

		Session session = null;
		if ("true".equalsIgnoreCase(("" + isAuthenticatorEnabled).trim())) {
			String username = properties.getProperty("sssap.authenticator.username");
			String passwordB64 = properties.getProperty("sssap.authenticator.password");
			System.out.println("PasswordB64:" + passwordB64);
			String password = new String(Base64.getDecoder().decode(passwordB64), StandardCharsets.UTF_8);
			System.out.println("Password:" + password);

			if (username == null || username.isEmpty())
				throw new ApiException(500,
						"Falta la propiedad <sssap.authenticator.username> en el archivo de configuración de correos <"
								+ emailProperties.getName() + ">");

			if (password == null || password.isEmpty())
				throw new ApiException(500,
						"Falta la propiedad <sssap.authenticator.password> en el archivo de configuración de correos <"
								+ emailProperties.getName() + ">");

			session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		} else
			session = Session.getInstance(properties);

		if (from == null || from.isEmpty())
			throw new ApiException(500, "Falta la propiedad <sssap.from> en el archivo de configuración de correos <"
					+ emailProperties.getName() + ">");

		try {

			Address toAddress = InternetAddress.parse(toEmail)[0];

			Message mimeMessage = new MimeMessage(session);
			mimeMessage.addRecipient(Message.RecipientType.TO, toAddress);
			mimeMessage.setFrom(new InternetAddress(from));
			mimeMessage.setSubject(subject);
			System.out.println("Contenido:" + content);
			// mimeMessage.setText(content.toString());
			mimeMessage.setContent(sb.toString(), "text/html; charset=utf-8");
			//mimeMessage.setContent(content, "text/html; charset=utf-8");

			System.out.println("enviando correo '" + subject + "' a " + toAddress);
			Transport.send(mimeMessage);
			System.out.println("correo '" + subject + "' enviado a " + toAddress);

		} catch (MessagingException e) {
			e.printStackTrace();
			throw new ApiException(500, e.getMessage());
		}

	}

	private Multipart generarMultipartConAttachments(String htmlText) {
		try {

			File ASSETS_FOLDER = new File("assets");

			Set<BodyPart> imageBodyParts = new HashSet<>();

			Pattern pattern = Pattern.compile("\"cid:(.*)\"");
			Matcher matcher = pattern.matcher(htmlText);
			while (matcher.find()) {

				String imagePathWithinAssetsFolder = matcher.group(1);
				File imageFile = new File(ASSETS_FOLDER, imagePathWithinAssetsFolder);
				if (!imageFile.exists())
					throw new ApiException(500,
							"La imagen <> no se encontró en el folder de assets, verifique la plantilla de correos");

				String imagePath = imageFile.getAbsolutePath();
				String fileName = imagePath.substring(imagePath.lastIndexOf('\\') + 1);
				fileName = fileName.substring(imagePath.lastIndexOf('/') + 1);
				BodyPart imageBodyPart = new MimeBodyPart();
				DataSource fds = new FileDataSource(imagePath);
				imageBodyPart.setDataHandler(new DataHandler(fds));
				imageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
				imageBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
				imageBodyPart.setFileName(fileName);
				imageBodyParts.add(imageBodyPart);
			}

			MimeMultipart multipart = new MimeMultipart("related");

			BodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setContent(htmlText, "text/html; charset=utf-8");
			multipart.addBodyPart(htmlBodyPart);

			if (!imageBodyParts.isEmpty())
				for (BodyPart e : imageBodyParts)
					multipart.addBodyPart(e);

			return multipart;

		} catch (ApiException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApiException(500, ex.getMessage());
		}
	}

	public static void main(String[] args) throws MessagingException {

		EmailResource emailResource = new EmailResource();
		emailResource.templatesLogic = new TemplatesLogic();
		TemplatesLogic.ASSETS_FOLDER = new File("C:/assets");

		Map<String, Object> params = new HashMap<>();
		// params.put("${password-reset-url}", "https://www.google.com/");
		params.put("${sap-user}", "ANGLOBAL_EXPRESS");
		params.put("${portal-url}", "url");
		params.put("${api-url}", "api url");
		params.put("${soporte-operacion}", "soporte");

		emailResource.enviarTemplate("jose.vazquezj@agilethought.com", "prueba",
				"email-templates/email-successful-unlock-confirmation.html", params);
	}

	public void enviarTemplate(String email, String subject, String templateName, Map<String, Object> params) {
		String template = templatesLogic.solveTemplate(templateName, params);
		enviarCorreo(email, subject, template);
	}

	@Asynchronous
	public void enviarTemplateParalelo(String email, String subject, String templateName, Map<String, Object> params) {
		enviarTemplate(email, subject, templateName, params);
	}

}
