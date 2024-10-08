package net.blueshell.api.service;

import net.blueshell.api.business.contribution.ContributionDao;
import net.blueshell.api.business.contribution.ContributionPeriod;
import net.blueshell.api.business.contribution.ContributionPeriodDao;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.ContactsApi;
import sibModel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BrevoContactService {

    @Value("${brevo.apiKey}")
    private String apiKey;

    @Autowired
    private UserDao dao;

    @Autowired
    private ContributionDao contributionDao;

    @Autowired
    private ContributionPeriodDao contributionPeriodDao;
    @Autowired
    private UserDao userDao;

    private ContactsApi getContactsApi() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(this.apiKey);

        return new ContactsApi();
    }

    private Map<String, Object> updateAttributes(User user) {
        Map<String, Object> attributes = new HashMap<>();
        if (user.getFirstName() != null) {
            attributes.put("FIRSTNAME", user.getFirstName());
        }
        if (user.getLastName() != null) {
            attributes.put("LASTNAME", user.getLastName());
        }
        if (user.getPhoneNumber() != null) {
            attributes.put("SMS", user.getPhoneNumber());
            attributes.put("WHATSAPP", user.getPhoneNumber());
        }
        if (user.hasRole(Role.MEMBER)) {
            attributes.put("IS_MEMBER", user.hasRole(Role.MEMBER));
        }
        attributes.put("EXT_ID", user.getId());
        attributes.put("NEWSLETTER", user.isNewsletter());
        attributes.put("ONLINE_SIGNUP", user.isOnlineSignup());
        return attributes;
    }

    public void createOrUpdateContact(User user) {
        user = setInBrevo(user);

        if (user.isInBrevo()) {
            updateContact(user);
        } else {
            createContact(user);
        }
    }

    public User setInBrevo(User user) {
        if (user.isInBrevo()) {
            return user;
        }

        try {
            ContactsApi api = getContactsApi();
            // If no exception thrown, the contact exists
            api.getContactInfo(user.getEmail(), null, null);
            user.setInBrevo(true);
        } catch (ApiException e) {
            user.setInBrevo(false);
        }
        userDao.update(user);
        return userDao.getById(user.getId());
    }

    public void createContact(User user) {
        try {
            ContactsApi api = getContactsApi();
            CreateContact createContact = new CreateContact();
            createContact.setEmail(user.getEmail());
            Map<String, Object> attributes = updateAttributes(user);
            createContact.setAttributes(attributes);
            api.createContact(createContact);
            user.setInBrevo(true);
            dao.update(user);
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
        }
    }

    public void createListForContributionPeriod(ContributionPeriod contributionPeriod) {
        try {
            ContactsApi api = getContactsApi();
            CreateList createList = new CreateList();
            String periodName = String.format("Contribution Paid %s - %s", contributionPeriod.getStartDate().toString(), contributionPeriod.getEndDate().toString());
            createList.name(periodName);
            createList.setFolderId(18L);
            CreateModel createModel = api.createList(createList);
            contributionPeriod.setListId(createModel.getId());
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
        }
    }

    public void addToPaidList(ContributionPeriod contributionPeriod, User user) {
        try {
            ContactsApi api = getContactsApi();
            List<String> emails = new ArrayList<String>();
            emails.add(user.getEmail());
            AddContactToList contactEmails = new AddContactToList();
            contactEmails.setEmails(emails);
            api.addContactToList(contributionPeriod.getListId(), contactEmails);
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
        }
    }

    public void removeFromPaidList(ContributionPeriod contributionPeriod, User user) {
        try {
            ContactsApi api = getContactsApi();
            List<String> emails = new ArrayList<String>();
            emails.add(user.getEmail());
            RemoveContactFromList contactEmails = new RemoveContactFromList();
            contactEmails.setEmails(emails);
            api.removeContactFromList(contributionPeriod.getListId(), contactEmails);
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
        }
    }

    public void updateContact(User user) {
        try {
            ContactsApi api = getContactsApi();
            UpdateContact updateContact = new UpdateContact();
            updateContact.setAttributes(updateAttributes(user));
            api.updateContact(user.getEmail(), updateContact);
        } catch (ApiException e) {
            System.out.println(e.getResponseBody());
        }
    }
}
