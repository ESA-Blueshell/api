package net.blueshell.api.service;

import net.blueshell.api.business.contribution.ContributionPeriod;
import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
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

    @Value("${brevo.folders.contributionPeriodsId}")
    private Long contributionPeriodsFolder;

    private ContactsApi getContactsApi() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(this.apiKey);

        return new ContactsApi();
    }

    public User setContactId(User user) {
        if (user.getContactId() != null) {
            return user;
        }

        try {
            ContactsApi api = getContactsApi();
            // If no exception thrown, the contact exists
            GetExtendedContactDetails details = api.getContactInfo(user.getEmail(), null, null);
            user.setContactId(details.getId());
        } catch (ApiException ignored) {
        }

        return user;
    }

    public void createOrUpdateContact(User user) throws ApiException {
        user = setContactId(user);

        if (user.getContactId() != null) {
            updateContact(user);
        } else {
            createContact(user);
        }
    }

    private void createContact(User user) throws ApiException {
        ContactsApi api = getContactsApi();
        CreateContact createContact = new CreateContact();
        createContact.setEmail(user.getEmail());
        Map<String, Object> attributes = updateAttributes(user);
        createContact.setAttributes(attributes);
        CreateUpdateContactModel response = api.createContact(createContact);
        user.setContactId(response.getId());
    }

    private void updateContact(User user) throws ApiException {
        ContactsApi api = getContactsApi();
        UpdateContact updateContact = new UpdateContact();
        updateContact.setAttributes(updateAttributes(user));
        api.updateContact(user.getContactId().toString(), updateContact);
    }

    private Map<String, Object> updateAttributes(User user) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("FIRSTNAME", user.getFirstName());
        attributes.put("LASTNAME", user.getLastName());
        attributes.put("SMS", user.getPhoneNumber());
        attributes.put("WHATSAPP", user.getPhoneNumber());
        attributes.put("IS_MEMBER", user.hasRole(Role.MEMBER));
        attributes.put("EXT_ID", user.getId());
        attributes.put("NEWSLETTER", user.isNewsletter());
        attributes.put("INCASSO", user.isIncasso());
        attributes.put("MEMBER_TYPE", user.getMemberType());
        attributes.put("SURNAME_PREFIX", user.getPrefix());
        attributes.put("COUNTRY", user.getCountry());
        attributes.put("CITY", user.getCity());
        attributes.put("STREET", user.getStreet());
        attributes.put("EMAIL", user.getEmail());
        // Do not overwrite the database with empty data.
        attributes.entrySet().removeIf(entry -> entry.getValue() == null);
        return attributes;
    }

    public void createContributionPeriodList(ContributionPeriod contributionPeriod) throws ApiException {
        ContactsApi api = getContactsApi();
        CreateList createList = new CreateList();
        String periodName = String.format("Contribution Paid %d - %d", contributionPeriod.getStartDate().getYear(), contributionPeriod.getEndDate().getYear());
        createList.name(periodName);
        createList.setFolderId(contributionPeriodsFolder);
        CreateModel createModel = api.createList(createList);
        contributionPeriod.setListId(createModel.getId());
    }

    public void addToContributionPeriodList(ContributionPeriod contributionPeriod, User user) throws ApiException {
        ContactsApi api = getContactsApi();
        List<Long> ids = new ArrayList<>();
        ids.add(user.getContactId());
        AddContactToList contactEmails = new AddContactToList();
        contactEmails.setIds(ids);
        api.addContactToList(contributionPeriod.getListId(), contactEmails);
    }

    public void removeFromContributionPeriodList(ContributionPeriod contributionPeriod, User user) throws ApiException {
        ContactsApi api = getContactsApi();
        List<Long> ids = new ArrayList<>();
        ids.add(user.getContactId());
        RemoveContactFromList contactEmails = new RemoveContactFromList();
        contactEmails.setIds(ids);
        api.removeContactFromList(contributionPeriod.getListId(), contactEmails);
    }
}
