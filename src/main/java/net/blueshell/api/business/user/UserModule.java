package net.blueshell.api.business.user;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UserModule {

    private static final UserDao dao = new UserDao();

    public static void applyUserDtoToUser(AdvancedUserDTO dto, User user) {
        applyIfFieldIsNull(user, dto.getGender(), User::setGender);
        applyIfFieldIsNull(user, dto.getDateOfBirth(), User::setDateOfBirth);
        applyIfFieldIsNull(user, dto.getDiscordTag(), User::setDiscord);
        applyIfFieldIsNullAndPassesVerifyCheck(user, dto.getEmail(), User::setEmail, UserModule::verifyEmail);
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
    }

    private static Boolean verifyEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", email);
    }

    private static <T> void applyIfFieldIsNullAndPassesVerifyCheck(User user, T obj, BiConsumer<User, T> applier, Function<T, Boolean> verifier) {
        if (obj != null && verifier.apply(obj)) {
            applier.accept(user, obj);
        }
    }

    private static <T> void applyIfFieldIsNull(User user, T obj, BiConsumer<User, T> applier) {
        if (obj != null) {
            applier.accept(user, obj);
        }
    }

}
