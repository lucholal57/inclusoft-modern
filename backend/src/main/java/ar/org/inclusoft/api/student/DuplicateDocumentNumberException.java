package ar.org.inclusoft.api.student;

class DuplicateDocumentNumberException extends RuntimeException {
    DuplicateDocumentNumberException(String documentNumber) {
        super("Ya existe un alumno con DNI " + documentNumber);
    }
}
