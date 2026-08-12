package ar.org.inclusoft.api.workshop;

import java.util.UUID;
class WorkshopNotFoundException extends RuntimeException {
    WorkshopNotFoundException() { super("El taller no existe o no está disponible."); }
    WorkshopNotFoundException(UUID id) { super("No encontramos el taller solicitado."); }
}
