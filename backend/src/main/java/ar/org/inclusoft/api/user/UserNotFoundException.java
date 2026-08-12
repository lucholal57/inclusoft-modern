package ar.org.inclusoft.api.user;

class UserNotFoundException extends RuntimeException {
    UserNotFoundException(String username) { super("No se encontró el usuario " + username + "."); }
}