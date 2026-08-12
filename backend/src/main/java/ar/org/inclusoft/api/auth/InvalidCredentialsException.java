package ar.org.inclusoft.api.auth;

class InvalidCredentialsException extends RuntimeException {
    InvalidCredentialsException() {
        super("Usuario o contraseña incorrectos.");
    }
}
