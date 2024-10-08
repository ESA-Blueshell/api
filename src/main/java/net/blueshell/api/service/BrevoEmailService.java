package net.blueshell.api.service;

import net.blueshell.api.business.contribution.ContributionPeriod;
import net.blueshell.api.business.contribution.ContributionPeriodRepository;
import net.blueshell.api.business.eventsignups.EventSignUp;
import net.blueshell.api.business.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Component
public class BrevoEmailService {

    @Value("${brevo.apiKey}")
    private String apiKey;

    @Autowired
    ContributionPeriodRepository contributionPeriodRepository;

    public void sendAccountCreationEmail(User user) {
        Properties params = new Properties();
        params.setProperty("link", String.format("https://esa-blueshell.nl/account/enable?username=%s&token=%s", user.getUsername(), user.getResetKey()));
        params.setProperty("username", user.getUsername());
        sendEmail(user.getEmail(), 61L, params);
    }

    public void sendPasswordResetEmail(User user) {
        Properties params = new Properties();
        params.setProperty("link", String.format("https://esa-blueshell.nl/login/reset-password?username=%s&token=%s", user.getUsername(), user.getResetKey()));
        params.setProperty("firstName", user.getFirstName());
        sendEmail(user.getEmail(), 63L, params);
    }

    public void sendGuestSignUpEmail(EventSignUp signUp) {
        Properties params = new Properties();
        params.setProperty("link", String.format("https://esa-blueshell.nl/events/signups/edit/%s", signUp.getGuest().getAccessToken()));
        params.setProperty("name", signUp.getGuest().getName());
        params.setProperty("eventTitle", signUp.getEvent().getTitle());
        sendEmail(signUp.getGuest().getEmail(), 62L, params);
    }

    public void sendInitialMembershipEmail(User user) {
        List<ContributionPeriod> contributionPeriods = contributionPeriodRepository.findCurrentContributionPeriod();
        Properties params = new Properties();
        if (contributionPeriods.isEmpty()) {
            params.setProperty("contributionNote", "*The prices shown are for the previous year and are subject to " +
                    "change for the coming year at the General Members Meeting in September");
            contributionPeriods = contributionPeriodRepository.findLatestContributionPeriod();
        }
        if (!contributionPeriods.isEmpty()) {
            ContributionPeriod contributionPeriod = contributionPeriods.get(0);
            params.setProperty("firstName", user.getFirstName());
            params.setProperty("startDate", contributionPeriod.getStartDate().toString());
            params.setProperty("endDate", contributionPeriod.getEndDate().toString());
            params.setProperty("halfYearFee", String.valueOf(contributionPeriod.getHalfYearFee()));
            params.setProperty("fullYearFee", String.valueOf(contributionPeriod.getFullYearFee()));
            params.setProperty("alumniFee", String.valueOf(contributionPeriod.getAlumniFee()));
            sendEmail(user.getEmail(), 65L, params);
        }
    }

    private void sendEmail(String toEmail, Long templateId, Properties params) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(this.apiKey);

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

            // Set the recipient for the mail
            List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(toEmail);
            toList.add(to);

            // Set send to, params and template id
            sendSmtpEmail.to(toList);
            sendSmtpEmail.setParams(params);
            sendSmtpEmail.setTemplateId(templateId);

            api.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
            System.out.println(e.getResponseHeaders());
            System.out.println(e.getCode());
            e.printStackTrace();
        }
    }
}
