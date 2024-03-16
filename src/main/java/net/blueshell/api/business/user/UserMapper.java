package net.blueshell.api.business.user;

public class UserMapper
{

	public static User fromRecord(net.blueshell.api.tables.records.UsersRecord record) {
		if (record == null) return null;

		var user = new User();
		user.setId(record.getId());
		user.setUsername(record.getUsername());
		user.setPassword(record.getPassword());
		user.setFirstName(record.getFirstName());
		user.setLastName(record.getLastName());
		user.setPrefix(record.getPrefix());
		user.setInitials(record.getInitials());
		user.setAddress(record.getAddress());
		user.setHouseNumber(record.getHouseNumber());
		user.setPostalCode(record.getPostalCode());
		user.setCity(record.getCity());
		user.setPhoneNumber(record.getPhoneNumber());
		user.setEmail(record.getEmail());
		user.setStudentNumber(record.getStudentNumber());
		user.setDateOfBirth(record.getDateOfBirth());
		user.setCreatedAt(record.getCreatedAt());
		user.setMemberSince(record.getMemberSince());
		user.setDiscord(record.getDiscord());
		user.setSteamid(record.getSteamid());
		user.setNewsletter(record.getNewsletter() != 0);
		user.setEnabled(record.getEnabled() != 0);
		user.setResetKey(record.getResetKey());
		user.setResetKeyValidUntil(record.getResetKeyValidUntil());
		user.setResetType(ResetType.valueOf(record.getResetType()));
		user.setContributionPaid(record.getContributionPaid() != 0);
		user.setConsentPrivacy(record.getConsentPrivacy() != 0);
		user.setConsentGdpr(record.getConsentGdpr() != 0);
		user.setGender(record.getGender());
		user.setStreet(record.getStreet());
		user.setCountry(record.getCountry());
		user.setPhotoConsent(record.getPhotoConsent() != 0);
		user.setNationality(record.getNationality());
		user.setStudy(record.getStudy());
		user.setStartStudyYear(record.getStartStudyYear());
		user.setDeletedAt(record.getDeletedAt());
//		user.setProfilePicture(record.getProfilePicture());
//		user.setCommitteeMembership>record.getCommitteeMembership() comMitteeMemberships();
//		user.setRole>record.getRole() rolEs();

		return user;
	}

}
