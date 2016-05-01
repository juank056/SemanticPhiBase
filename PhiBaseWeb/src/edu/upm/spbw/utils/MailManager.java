package edu.upm.spbw.utils;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 
 * Clase encargada del envío de correos electrónicos de la aplicación
 * 
 * @author Juan Camilo Mesa Polo.
 * @version 1.0
 */
public class MailManager {

	/**
	 * Localhost
	 */
	private static final String LOCALHOST = "localhost";

	private Session session;
	private String username;
	private String password;
	private boolean authrequired;
	private String mailSmtpHost;

	/**
	 * Instancia del singleton
	 */
	private static MailManager instance;

	/**
	 * Obtiene la instancia del manager de correo
	 * 
	 * @return Instancia para administrar el correo
	 * @throws MailException
	 *             Error iniciando instancia de correo
	 */
	public static MailManager getInstance() throws MailException {
		if (instance == null)
			instance = new MailManager();
		return instance;
	}

	/**
	 * Se encarga de construir el Manager para el envío de correos electrónicos
	 * 
	 * @throws MailException
	 *             Error creando el Mail Manager
	 */
	private MailManager() throws MailException {
		try {
			// Tomamos las propiedades del sistema
			Properties props = System.getProperties();
			// Obtiene de acuerdo a parámetros
			// Nombre de usuario
			this.username = DBDataChiper.decrypt(ConfigApplicationManager
					.getParameter("edu.upm.spbw.mail.account").trim());
			// Contraseña
			this.password = DBDataChiper.decrypt(ConfigApplicationManager
					.getParameter("edu.upm.spbw.mail.password").trim());
			// Host
			this.mailSmtpHost = DBDataChiper.decrypt(ConfigApplicationManager
					.getParameter("edu.upm.spbw.mail.host").trim());
			// Puerto
			props.setProperty(
					"mail.smtp.port",
					DBDataChiper.decrypt(ConfigApplicationManager.getParameter(
							"edu.upm.spbw.mail.port").trim()));
			// Autenticación requerida
			this.authrequired = Constants.ONE.equals(DBDataChiper
					.decrypt(ConfigApplicationManager.getParameter(
							"edu.upm.spbw.mail.auth").trim()));
			if (this.authrequired) {/* Requerida */
				props.setProperty("mail.smtp.auth", "true");
			} else {/* No requerida */
				props.setProperty("mail.smtp.auth", "false");
			}
			// TLS enable
			boolean tls = Constants.ONE.equals(DBDataChiper
					.decrypt(ConfigApplicationManager.getParameter(
							"edu.upm.spbw.mail.tls").trim()));
			if (tls) {/* Activado */
				props.setProperty("mail.smtp.starttls.enable", "true");
			} else {/* Desactivado */
				props.setProperty("mail.smtp.starttls.enable", "false");
			}
			// Adicionamos las propiedades
			// Nombre del host
			props.setProperty("mail.smtp.localhost", LOCALHOST);
			// Nombre del usuario
			props.setProperty("mail.smtp.user", username);
			// Inicializamos el servidor saliente
			props.setProperty("mail.smtp.host", mailSmtpHost);
			// Llamamos la sesión actual
			this.session = Session.getDefaultInstance(props, null);
			// No debug
			session.setDebug(false);
		} catch (Exception e) {/* Ocurrio error */
			throw new MailException(e);
		}
	}

	/**
	 * Método que permite enviar un mensaje de correro a un destinatario
	 * 
	 * @param text
	 *            contenido del mensaje
	 * @param from
	 *            Cuenta desde donde se envía el correo
	 * @param toemail
	 *            Cuenta a quie va dirigido el mensaje
	 * @param subject
	 *            Encabezado del mensaje
	 * @param mess
	 *            Cuerpo del mensaje tipo texto
	 * @return boolean Retorna true si el mensaje fue enviado, false en otro
	 *         caso
	 * @throws MailException
	 *             Error enviando el correo electronico
	 */
	public boolean sendMessage(String htmlText, String toemail, String subject)
			throws MailException {

		try {
			// create a new MimeMessage object (using the Session created in the
			// constructor)
			Message message = new MimeMessage(this.session);
			message.setFrom(new InternetAddress(this.username));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					toemail));
			message.setSubject(subject);
			/*
			 * Parte para adicionar el logo
			 */
			Multipart multiPart = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart();
			// Adiciona contenido
			messageBodyPart.setContent(htmlText, "text/html; charset=UTF-8");
			// Adiciona bodypart al multipart
			multiPart.addBodyPart(messageBodyPart);
			/*
			 * Logo de la aplicacion
			 */
			messageBodyPart = new MimeBodyPart();
			// Datasource del logo
			DataSource fds = new FileDataSource(ConfigApplicationManager
					.getParameter("edu.upm.spbw.mail.logo").trim());
			// Datahandler al bodypart
			messageBodyPart.setDataHandler(new DataHandler(fds));
			// Header del bodypart
			messageBodyPart.setHeader("Content-ID", "<logo>");
			// Adiciona imagen al multipart
			multiPart.addBodyPart(messageBodyPart);
			/*
			 * Finalmente
			 */
			// Pone contenido en el mensaje
			message.setContent(multiPart);
			// Envía mensaje
			Transport transport = session.getTransport(Constants.SMTP);
			// Revisamos si requiere autenticacion o no
			if (authrequired)
				transport.connect(username, password);
			else
				transport.connect();
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception e) {
			throw new MailException(e);
		}
	}

	/**
	 * Método que permite enviar un mensaje de correro a varios destinatarios
	 * 
	 * @param contentType
	 *            Tipo de contenido del mensaje
	 * @param emails
	 *            Lista de las cuentas a quienes va dirigido el mensaje
	 * @param subject
	 *            Encabezado del mensaje
	 * @param mess
	 *            Cuerpo del mensaje tipo texto
	 * @return boolean Retorna true si el mensaje fue enviado, false en otro
	 *         caso
	 * @throws MailException
	 *             Error enviando mensaje
	 */

	public boolean sendMessage(String contentType, String[] emails,
			String subject, String mess) throws MailException {
		try {
			// Creamos los recipientes del mensaje
			InternetAddress[] recipients = null;
			if (emails != null) {
				recipients = new InternetAddress[emails.length];
				for (int i = 0; i < recipients.length; i++)
					recipients[i] = new InternetAddress(emails[i]);

			}
			Message message = new MimeMessage(this.session);
			message.setFrom(new InternetAddress(this.username));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subject);
			message.setContent(mess, contentType);

			Transport transport = session.getTransport(Constants.SMTP);
			// Revisamos si requiere autenticacion o no
			if (authrequired)
				transport.connect(username, password);
			else
				transport.connect();
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			return true;
		} catch (Exception e) {/* Ocurrio error enviando mensaje */
			throw new MailException(e);
		}
	}

	/**
	 * Envia un email con archivos adjuntos
	 * 
	 * @param path
	 *            Ruta del archivo Adjunto
	 * @param text
	 *            Texto del mensaje
	 * @param toemail
	 *            Email de envio
	 * @param subject
	 *            Asunto del mensaje
	 * @return Indicador de mensaje enviado correctamente
	 * @throws MailException
	 *             Excepcion enviando mensaje
	 */
	public boolean sendMessageAtt(String path, String text, String toemail,
			String subject) throws MailException {
		try {
			// Define message
			toemail = toemail.trim();
			path = path.trim();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.username));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toemail));
			message.setSubject(subject);
			// Ponemos el nombre del archivo
			String rutas[] = path.split(Constants.SLASH);
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			// Fill the message
			messageBodyPart.setText(text);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(path);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(rutas[rutas.length - 1]);
			multipart.addBodyPart(messageBodyPart);
			// Put parts in message
			message.setContent(multipart);
			// Send the message
			Transport transport = session.getTransport(Constants.SMTP);
			// Revisamos si requiere autenticacion o no
			if (authrequired)
				transport.connect(username, password);
			else
				transport.connect();
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception e) {
			throw new MailException(e);
		}
	}
}
