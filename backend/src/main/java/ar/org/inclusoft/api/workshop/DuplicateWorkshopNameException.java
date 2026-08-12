package ar.org.inclusoft.api.workshop;

class DuplicateWorkshopNameException extends RuntimeException {
    DuplicateWorkshopNameException(String name) { super("Ya existe un taller con el nombre '" + name + "'."); }
}
