package net.blueshell.api.business.user;

import net.blueshell.api.business.signature.Signature;
import net.blueshell.api.business.signature.SignatureDao;
import net.blueshell.api.storage.StorageService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UserModule {

    private static final UserDao dao = new UserDao();
    private static final SignatureDao signatureDao = new SignatureDao();

    public static void makeMember(AdvancedUserDTO userDto, User user, StorageService storageService) {
        // The signature comes from vue-signature-pad which always gives a PNG
        String filename = storageService.storeSignature(userDto.getSignature(), ".png");
        String downloadURL = storageService.getDownloadURI(filename);

        Signature signature = new Signature(filename, downloadURL, user, userDto.getSignatureDate(), userDto.getSignatureCity());
        signatureDao.create(signature);
        user.setSignature(signature);

        user.setMemberSince(Timestamp.from(Instant.now()));
        user.addRole(Role.MEMBER);
        user.setOnlineSignup(true);
        user.setConsentPrivacy(true);
    }


    public static void applyUserDtoToUser(AdvancedUserDTO dto, User user) {
        applyIfFieldIsNotNull(user, dto.getGender(), User::setGender);
        applyIfFieldIsNotNull(user, dto.getDateOfBirth(), User::setDateOfBirth);
        applyIfFieldIsNotNull(user, dto.getDiscord(), User::setDiscord);
        applyIfFieldIsNotNullAndPassesVerifyCheck(user, dto.getEmail(), User::setEmail, UserModule::verifyEmail);
        applyIfFieldIsNotNull(user, dto.getPhoneNumber(), User::setPhoneNumber);
        applyIfFieldIsNotNull(user, dto.getAddress(), User::setAddress);
        applyIfFieldIsNotNull(user, dto.getSteamid(), User::setSteamid);
        applyIfFieldIsNotNull(user, dto.getPostalCode(), User::setPostalCode);
        applyIfFieldIsNotNull(user, dto.getCity(), User::setCity);
        applyIfFieldIsNotNull(user, dto.getCountry(), User::setCountry);
        applyIfFieldIsNotNull(user, dto.isNewsletter(), User::setNewsletter);
        applyIfFieldIsNotNull(user, dto.isPhotoConsent(), User::setPhotoConsent);
        applyIfFieldIsNotNull(user, dto.getNationality(), User::setNationality);
        applyIfFieldIsNotNull(user, dto.getStudentNumber(), User::setStudentNumber);
        applyIfFieldIsNotNull(user, dto.getStudy(), User::setStudy);
        applyIfFieldIsNotNull(user, dto.getStartStudyYear(), User::setStartStudyYear);
        user.setEhbo(dto.isEhbo());
        user.setBhv(dto.isBhv());
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
