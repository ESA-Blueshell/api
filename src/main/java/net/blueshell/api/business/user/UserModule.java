package net.blueshell.api.business.user;

import java.util.function.BiConsumer;

public class UserModule {

    private static final UserDao dao = new UserDao();

    public static User applyUserDtoToUser(AdvancedUserDTO dto, User user) {
        applyIfFieldIsNull(user, dto.getGender(), User::setGender);
        applyIfFieldIsNull(user, dto.getDateOfBirth(), User::setDateOfBirth);
        applyIfFieldIsNull(user, dto.getDiscordTag(), User::setDiscord);
        applyIfFieldIsNull(user, dto.getEmail(), User::setEmail);
        applyIfFieldIsNull(user, dto.getPhoneNumber(), User::setPhoneNumber);
        applyIfFieldIsNull(user, dto.getStreet(), User::setStreet);
        applyIfFieldIsNull(user, dto.getHouseNumber(), User::setHouseNumber);
        applyIfFieldIsNull(user, dto.getPostalCode(), User::setPostalCode);
        applyIfFieldIsNull(user, dto.getCity(), User::setCity);
        applyIfFieldIsNull(user, dto.getCountry(), User::setCountry);
        applyIfFieldIsNull(user, dto.isWantsNewsletter(), User::setNewsletter);
        applyIfFieldIsNull(user, dto.isPhotoConsent(), User::setPhotoConsent);
        applyIfFieldIsNull(user, dto.getNationality(), User::setNationality);
        applyIfFieldIsNull(user, dto.getStudentNumber(), User::setStudentNumber);
        applyIfFieldIsNull(user, dto.getStudy(), User::setStudy);
        applyIfFieldIsNull(user, dto.getStartStudyYear(), User::setStartStudyYear);

        dao.update(user);

        return user;
    }

    private static <T> void applyIfFieldIsNull(User user, T obj, BiConsumer<User, T> applier) {
        if (obj != null) {
            applier.accept(user, obj);
        }
    }

}
