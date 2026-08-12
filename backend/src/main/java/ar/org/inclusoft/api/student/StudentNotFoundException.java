package ar.org.inclusoft.api.student;

import java.util.UUID;

class StudentNotFoundException extends RuntimeException {
    StudentNotFoundException() { super("El alumno no existe o no está disponible."); }
    StudentNotFoundException(UUID id) {
        super("No existe un alumno con identificador " + id);
    }
}
