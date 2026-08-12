package ar.org.inclusoft.api.student;

public record StudentHealthRecordResponse(String medicalReferences, String medications, String allergies, String healthInsurance, String treatingProfessionals, String supportGuidelines, String observations) {
    static StudentHealthRecordResponse from(StudentHealthRecord record) { return new StudentHealthRecordResponse(record.getMedicalReferences(), record.getMedications(), record.getAllergies(), record.getHealthInsurance(), record.getTreatingProfessionals(), record.getSupportGuidelines(), record.getObservations()); }
    static StudentHealthRecordResponse empty() { return new StudentHealthRecordResponse(null, null, null, null, null, null, null); }
}
