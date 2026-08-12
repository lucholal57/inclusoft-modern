package ar.org.inclusoft.api.student;

import java.util.UUID;

public record StudentContactResponse(UUID id, String fullName, String relationship, String phoneNumber, String email, boolean responsible, boolean emergencyContact) {
    static StudentContactResponse from(StudentContact contact) { return new StudentContactResponse(contact.getId(), contact.getFullName(), contact.getRelationship(), contact.getPhoneNumber(), contact.getEmail(), contact.isResponsible(), contact.isEmergencyContact()); }
}
