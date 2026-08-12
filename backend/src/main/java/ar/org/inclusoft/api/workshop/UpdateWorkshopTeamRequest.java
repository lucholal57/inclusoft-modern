package ar.org.inclusoft.api.workshop;

import java.util.List;
import java.util.UUID;

public record UpdateWorkshopTeamRequest(List<UUID> staffMemberIds) { }