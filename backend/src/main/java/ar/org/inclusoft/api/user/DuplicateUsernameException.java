package ar.org.inclusoft.api.user;

class DuplicateUsernameException extends RuntimeException {
    DuplicateUsernameException(String username) {
        super("El usuario " + username + " ya existe.");
    }
}
