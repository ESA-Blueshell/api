package net.blueshell.api.business.user;

import net.blueshell.api.business.signature.Signature;
import net.blueshell.api.business.user.request.ActivateMemberRequest;
import net.blueshell.api.business.user.request.PasswordResetRequest;
import net.blueshell.api.storage.StorageService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.function.BiConsumer;

public class UserModule {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()-_=+<>?";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            password.append(CHAR_SET.charAt(index));
        }

        return password.toString();
    }

    public static void applyEditableFields(User user, AdvancedUserDTO dto) {
        applyIfFieldIsNotNull(user, dto.getDiscord(), User::setDiscord);
        applyIfFieldIsNotNull(user, dto.getSteamid(), User::setSteamid);
        applyIfFieldIsNotNull(user, dto.getPhoneNumber(), User::setPhoneNumber);
        applyIfFieldIsNotNull(user, dto.getPostalCode(), User::setPostalCode);
        applyIfFieldIsNotNull(user, dto.getAddress(), User::setAddress);
        applyIfFieldIsNotNull(user, dto.getCity(), User::setCity);
        applyIfFieldIsNotNull(user, dto.getCountry(), User::setCountry);
        applyIfFieldIsNotNull(user, dto.isNewsletter(), User::setNewsletter);
        applyIfFieldIsNotNull(user, dto.isPhotoConsent(), User::setPhotoConsent);
        applyIfFieldIsNotNull(user, dto.getNationality(), User::setNationality);
        user.setEhbo(dto.isEhbo());
        user.setBhv(dto.isBhv());
    }

    public static void applyCreationFields(User user, AdvancedUserDTO dto) {
        applyIfFieldIsNotNull(user, dto.getUsername(), User::setUsername);
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(generatePassword()));
        }
    }

    public static void applyCreationFields(User user, ActivateMemberRequest dto) {
        applyIfFieldIsNotNull(user, dto.getUsername(), User::setUsername);
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(generatePassword()));
        }
    }

    // Fields that can only be updated by a secretary or admin
    public static void applyAdminFields(User user, AdvancedUserDTO dto) {
        applyIfFieldIsNotNull(user, dto.getInitials(), User::setInitials);
        applyIfFieldIsNotNull(user, dto.getFirstName(), User::setFirstName);
        applyIfFieldIsNotNull(user, dto.getPrefix(), User::setPrefix);
        applyIfFieldIsNotNull(user, dto.getLastName(), User::setLastName);
        applyIfFieldIsNotNull(user, dto.getEmail().toLowerCase(), User::setEmail);
        applyIfFieldIsNotNull(user, dto.getDateOfBirth(), User::setDateOfBirth);
        applyIfFieldIsNotNull(user, dto.getGender(), User::setGender);
        applyIfFieldIsNotNull(user, dto.getStudentNumber(), User::setStudentNumber);
        applyIfFieldIsNotNull(user, dto.getStudy(), User::setStudy);
        applyIfFieldIsNotNull(user, dto.getStartStudyYear(), User::setStartStudyYear);
    }

    public static void activateAccount(User user) {
        user.setResetKey(null);
        user.setResetType(null);
        user.setResetKeyValidUntil(null);
        user.setEnabled(true);
    }

    private static <T> void applyIfFieldIsNotNull(User user, T obj, BiConsumer<User, T> applier) {
        if (obj != null) {
            applier.accept(user, obj);
        }
    }

    public static void setPassword(User user, PasswordResetRequest request) {
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetKey(null);
        user.setResetKeyValidUntil(null);
        user.setResetType(null);
    }

    public static void storeSignature(User user, AdvancedUserDTO dto, StorageService storageService) {
        if (user.getSignature() == null && dto.getSignature() != null) {
            Signature signature = dto.getSignature().mapToSignature(storageService);
            user.setSignature(signature);
        }
    }
}
