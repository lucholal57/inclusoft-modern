package ar.org.inclusoft.api.auth;

class InvalidCurrentPasswordException extends RuntimeException {
    InvalidCurrentPasswordException() {
        super("La contraseña actual no es correcta.");
    }
}