package ar.org.inclusoft.api.staff;

import java.util.UUID;
public record StaffMemberResponse(UUID id, String fullName, StaffProfile profile, String documentNumber, String phoneNumber, String email, String profession, boolean active) { static StaffMemberResponse from(StaffMember item) { return new StaffMemberResponse(item.getId(), item.getFullName(), item.getProfile(), item.getDocumentNumber(), item.getPhoneNumber(), item.getEmail(), item.getProfession(), item.isActive()); } }