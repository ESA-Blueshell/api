package net.blueshell.api.email;

import net.blueshell.api.business.eventsignups.EventSignUp;
import net.blueshell.api.business.user.User;
import net.blueshell.api.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailModule {

    @Value("${email.address}")
    private String email;

    @Value("${email.password}")
    private String password; // App password

    private static final String INITIAL_EMAIL_SUBJECT = "Blueshell esports account creation";
    private static final String INITIAL_EMAIL_CONTENT = "Hello %s, <br /><br />" +
            "Welcome to Blueshell Esports! You can activate your account by clicking on <a href=\"https://esa-blueshell.nl/account/enable?username=%s&token=%s\">this link</a>.<br /><br />" +
            "For information on events and our general community, either check out either our discord or <a href=\"https://esa-blueshell.nl\">website</a>. Enjoy your stay!<br /><br />" +
            "Please do not reply to this email, as this is a generated email. Any responses will be ignored.<br /><br />" +
            "Kind regards," +
            "<br /><br />" +
            "Blueshell Esports";

    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "Blueshell esports password reset";
    private static final String PASSWORD_RESET_EMAIL_CONTENT = "Hello %s, <br /><br />" +
            "Your password has been reset, you can create a new password by going to <a href=\"https://esa-blueshell.nl/login/reset-password?username=%s&token=%s\">this link</a>.<br /><br />" +
            "If you did not perform this action, please make sure that no one else but you has access to your account.<br /><br />" +
            "Please do not reply to this email, as this is a generated email. Any responses will be ignored.<br /><br />" +
            "Kind regards," +
            "<br /><br />" +
            "Blueshell Esports";

    private static final String GUEST_SIGNUP_EMAIL_SUBJECT = "Blueshell esports event signup";
    private static final String GUEST_SIGNUP_EMAIL_CONTENT = "Hello %s, <br /><br />" +
            "Thank you for signing up for %s! You can edit or cancel your signup by going to <a href=\"https://esa-blueshell.nl/events/signups/edit/%s\">this link</a>.<br /><br />" +
            "If you have questions about the event, feel free to ask them in <a href=\"https://discord.com/channels/324285132133629963/633720092244312095\">the discord server</a>.<br /><br />" +
            "Please do not reply to this email, as this is a generated email. Any responses will be ignored.<br /><br />" +
            "Kind regards," +
            "<br /><br />" +
            "Blueshell Esports";

    private final Authenticator auth = new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(email, password);
        }
    };

    public void sendInitialKeyEmail(User user) {
        sendEmail(user.getEmail(), INITIAL_EMAIL_SUBJECT, String.format(INITIAL_EMAIL_CONTENT, user.getUsername(), user.getUsername(), user.getResetKey()));
    }

    public void sendPasswordResetEmail(User user) {
        sendEmail(user.getEmail(), PASSWORD_RESET_EMAIL_SUBJECT, String.format(PASSWORD_RESET_EMAIL_CONTENT, user.getFirstName(), user.getUsername(), user.getResetKey()));
    }

    public void sendGuestSignUpEmail(EventSignUp signUp) {
        sendEmail(signUp.getGuest().getEmail(), GUEST_SIGNUP_EMAIL_SUBJECT, String.format(GUEST_SIGNUP_EMAIL_CONTENT, signUp.getGuest().getName(), signUp.getEvent().getTitle(), Util.getMd5Hash(String.valueOf(signUp.getId()))));
    }

    private void sendEmail(String toEmail, String subject, String content) {

        var properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        var session = Session.getDefaultInstance(properties, auth);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
