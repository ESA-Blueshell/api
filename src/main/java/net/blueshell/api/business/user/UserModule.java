package net.blueshell.api.business.user;

import net.blueshell.api.tables.records.UsersRecord;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UserModule {

    private static final UserDao dao = new UserDao();

    public static void applyUserDtoToUser(AdvancedUserDTO dto, UsersRecord user) {
        applyIfFieldIsNotNull(user, dto.getGender(), UsersRecord::setGender);
        applyIfFieldIsNotNull(user, dto.getDateOfBirth(), UsersRecord::setDateOfBirth);
        applyIfFieldIsNotNull(user, dto.getDiscord(), UsersRecord::setDiscord);
        applyIfFieldIsNotNullAndPassesVerifyCheck(user, dto.getEmail(), UsersRecord::setEmail, UserModule::verifyEmail);
        applyIfFieldIsNotNull(user, dto.getPhoneNumber(), UsersRecord::setPhoneNumber);
        applyIfFieldIsNotNull(user, dto.getAddress(), UsersRecord::setAddress);
        applyIfFieldIsNotNull(user, dto.getSteamId(), UsersRecord::setSteamid);
        applyIfFieldIsNotNull(user, dto.getPostalCode(), UsersRecord::setPostalCode);
        applyIfFieldIsNotNull(user, dto.getCity(), UsersRecord::setCity);
        applyIfFieldIsNotNull(user, dto.getCountry(), UsersRecord::setCountry);
        applyIfFieldIsNotNull(user, dto.isNewsletter() ? (byte) 1 : (byte) 0, UsersRecord::setNewsletter);
        applyIfFieldIsNotNull(user, dto.isPhotoConsent() ? (byte) 1 : (byte) 0, UsersRecord::setPhotoConsent);
        applyIfFieldIsNotNull(user, dto.getNationality(), UsersRecord::setNationality);
        applyIfFieldIsNotNull(user, dto.getStudentNumber(), UsersRecord::setStudentNumber);
        applyIfFieldIsNotNull(user, dto.getStudy(), UsersRecord::setStudy);
        applyIfFieldIsNotNull(user, dto.getStartStudyYear(), UsersRecord::setStartStudyYear);

        dao.update(user);
    }

    private static Boolean verifyEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", email);
    }

    private static <T> void applyIfFieldIsNotNullAndPassesVerifyCheck(UsersRecord user, T obj, BiConsumer<UsersRecord, T> applier, Function<T, Boolean> verifier) {
        if (obj != null && verifier.apply(obj)) {
            applier.accept(user, obj);
        }
    }

    private static <T> void applyIfFieldIsNotNull(UsersRecord user, T obj, BiConsumer<UsersRecord, T> applier) {
        if (obj != null) {
            applier.accept(user, obj);
        }
    }

}
