package ar.org.inclusoft.api.user;

class UserOperationNotAllowedException extends RuntimeException {
    UserOperationNotAllowedException(String message) { super(message); }
}