package net.blueshell.api.business.user;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UserModule {

    private static final UserDao dao = new UserDao();

    public static void applyUserDtoToUser(AdvancedUserDTO dto, User user) {
        applyIfFieldIsNotNull(user, dto.getGender(), User::setGender);
        applyIfFieldIsNotNull(user, dto.getDateOfBirth(), User::setDateOfBirth);
        applyIfFieldIsNotNull(user, dto.getDiscordTag(), User::setDiscord);
        applyIfFieldIsNotNullAndPassesVerifyCheck(user, dto.getEmail(), User::setEmail, UserModule::verifyEmail);
        applyIfFieldIsNotNull(user, dto.getPhoneNumber(), User::setPhoneNumber);
        applyIfFieldIsNotNull(user, dto.getStreet(), User::setStreet);
        applyIfFieldIsNotNull(user, dto.getHouseNumber(), User::setHouseNumber);
        applyIfFieldIsNotNull(user, dto.getPostalCode(), User::setPostalCode);
        applyIfFieldIsNotNull(user, dto.getCity(), User::setCity);
        applyIfFieldIsNotNull(user, dto.getCountry(), User::setCountry);
        applyIfFieldIsNotNull(user, dto.isWantsNewsletter(), User::setNewsletter);
        applyIfFieldIsNotNull(user, dto.isPhotoConsent(), User::setPhotoConsent);
        applyIfFieldIsNotNull(user, dto.getNationality(), User::setNationality);
        applyIfFieldIsNotNull(user, dto.getStudentNumber(), User::setStudentNumber);
        applyIfFieldIsNotNull(user, dto.getStudy(), User::setStudy);
        applyIfFieldIsNotNull(user, dto.getStartStudyYear(), User::setStartStudyYear);

        dao.update(user);
    }

    private static Boolean verifyEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", email);
    }

    private static <T> void applyIfFieldIsNotNullAndPassesVerifyCheck(User user, T obj, BiConsumer<User, T> applier, Function<T, Boolean> verifier) {
        if (obj != null && verifier.apply(obj)) {
            applier.accept(user, obj);
        }
    }

    private static <T> void applyIfFieldIsNotNull(User user, T obj, BiConsumer<User, T> applier) {
        if (obj != null) {
            applier.accept(user, obj);
        }
    }

}
