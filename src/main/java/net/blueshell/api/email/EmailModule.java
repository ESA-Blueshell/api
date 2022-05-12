package net.blueshell.api.email;

import net.blueshell.api.business.user.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailModule {

    private static final String EMAIL = "swordieserver@gmail.com";
    private static final String EMAIL_PASSWORD = "4K2QpKkOnGOEsWGUz5s9";

    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "Blueshell esports password reset";
    private static final String PASSWORD_RESET_EMAIL_CONTENT = "Hello %s, <br /><br />" +
            "Your password has been reset, you can create a new password by going to <a href=\"https://www.swordie.net/reset-password?username=%s&token=%s\">this link</a>.<br /><br />" +
            "If you did not perform this action, please make sure that no one else but you has access to your account.<br /><br />" +
            "Please do not reply to this email, as this is a generated email. Any responses will be ignored.<br /><br />" +
            "Kind regards," +
            "<br /><br />" +
            "Blueshell Esports";

    private static final String USERNAME_EMAIL_SUBJECT = "Blueshell esports username";
    private static final String USERNAME_EMAIL_CONTENT = "Hello Swordie player,<br /><br />" +
            "Your username is as follows:<br /><br />" +
            "<b>%s</b> <br /><br />" +
            "If you did not perform this action, please make sure that no one else but you has access to your account.<br /><br />" +
            "Please do not reply to this email, as this is a generated email. Any responses will be ignored.<br /><br />" +
            "Kind regards," +
            "<br /><br />" +
            "Blueshell Esports";

    private static final Authenticator auth = new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(EMAIL, EMAIL_PASSWORD);
        }
    };

    public static void sendPasswordResetEmail(User user) {
        sendEmail(user, PASSWORD_RESET_EMAIL_SUBJECT, String.format(PASSWORD_RESET_EMAIL_CONTENT, user.getFirstName(), user.getFirstName(), user.getResetKey()));
    }

    public static void sendUsernameEmail(User user) {
        sendEmail(user, USERNAME_EMAIL_SUBJECT, String.format(USERNAME_EMAIL_CONTENT, user.getFirstName()));
    }

    private static void sendEmail(User user, String subject, String content) {
        var userEmail = user.getEmail();

        var properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        var session = Session.getDefaultInstance(properties, auth);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
