package net.blueshell.api.service.brevo;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.ContributionPeriod;
import net.blueshell.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.ContactsApi;
import sibModel.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactService {

    private static final Map<String, String> attributeToFieldMap = new HashMap<>();

    static {
        attributeToFieldMap.put("FIRSTNAME", "firstName");
        attributeToFieldMap.put("LASTNAME", "lastName");
        attributeToFieldMap.put("SMS", "phoneNumber");
        attributeToFieldMap.put("WHATSAPP", "phoneNumber");
        attributeToFieldMap.put("EXT_ID", "id");
        attributeToFieldMap.put("NEWSLETTER", "newsletter");
        attributeToFieldMap.put("INCASSO", "incasso");
        attributeToFieldMap.put("MEMBER_TYPE", "memberType");
        attributeToFieldMap.put("SURNAME_PREFIX", "prefix");
        attributeToFieldMap.put("COUNTRY", "country");
        attributeToFieldMap.put("CITY", "city");
        attributeToFieldMap.put("STREET", "street");
        attributeToFieldMap.put("EMAIL", "email");
    }

    @Value("${brevo.apiKey}")
    private String apiKey;
    @Value("${brevo.folders.contributionPeriodsId}")
    private Long contributionPeriodsFolder;

    private Map<String, Object> mapUserToBrevoContact(User user) {
        Map<String, Object> attributes = new HashMap<>();
        for (Map.Entry<String, String> entry : attributeToFieldMap.entrySet()) {
            String brevoAttribute = entry.getKey();
            String userField = entry.getValue();

            try {
                Field field = User.class.getDeclaredField(userField);
                field.setAccessible(true);
                Object value = field.get(user);

                if (value != null) {
                    if ("IS_MEMBER".equals(brevoAttribute)) {
                        value = user.hasRole(Role.MEMBER);
                    } else if ("EMAIL".equals(brevoAttribute)) {
                        value = user.getEmail();
                    }
                    attributes.put(brevoAttribute, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return attributes;
    }

    private User mapBrevoContactToUser(GetExtendedContactDetails contact) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> attributes = (Map<String, Object>) contact.getAttributes();
        User user = new User();

        for (Map.Entry<String, String> entry : attributeToFieldMap.entrySet()) {
            String brevoAttribute = entry.getKey();
            String userField = entry.getValue();

            if (attributes.containsKey(brevoAttribute)) {
                Field field = User.class.getDeclaredField(userField);
                field.setAccessible(true);
                Object currentValue = field.get(user);
                Object brevoValue = attributes.get(brevoAttribute);

                if (currentValue == null && brevoValue != null) {
                    if (!"EMAIL".equals(brevoAttribute)) {
                        Object valueToSet = convertValue(brevoValue, field.getType());
                        field.set(user, valueToSet);
                    }
                }
            }
        }

        return user;
    }

    public User getUserFromBrevo(String email) throws ApiException, NoSuchFieldException, IllegalAccessException {
        ContactsApi api = getContactsApi();
        GetExtendedContactDetails details = api.getContactInfo(email, null, null);
        return mapBrevoContactToUser(details);
    }

    private Object convertValue(Object brevoValue, Class<?> fieldType) {
        if (brevoValue == null) {
            return null;
        }

        if (fieldType.isAssignableFrom(brevoValue.getClass())) {
            return brevoValue;
        }

        if (fieldType == String.class) {
            return brevoValue.toString();
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.valueOf(brevoValue.toString());
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.valueOf(brevoValue.toString());
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.valueOf(brevoValue.toString());
        }

        return null;
    }

    private ContactsApi getContactsApi() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(this.apiKey);
        return new ContactsApi();
    }

    public User getUpdate(User user) throws ApiException {
        if (user.getContactId() != null) {
            return user;
        }

        try {
            ContactsApi api = getContactsApi();
            GetExtendedContactDetails details = api.getContactInfo(user.getEmail(), null, null);
            user.setContactId(details.getId());
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void updateContact(User user) throws ApiException {
        user = getUpdate(user);

        if (user.getContactId() != null) {
            sendUpdate(user);
        } else {
            createContact(user);
        }
    }

    private void createContact(User user) throws ApiException {
        ContactsApi api = getContactsApi();
        CreateContact createContact = new CreateContact();
        createContact.setEmail(user.getEmail());
        Map<String, Object> attributes = mapUserToBrevoContact(user);
        createContact.setAttributes(attributes);
        CreateUpdateContactModel response = api.createContact(createContact);
        user.setContactId(response.getId());
    }

    private void sendUpdate(User user) throws ApiException {
        ContactsApi api = getContactsApi();
        UpdateContact updateContact = new UpdateContact();
        updateContact.setAttributes(mapUserToBrevoContact(user));
        api.updateContact(user.getContactId().toString(), updateContact);
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
