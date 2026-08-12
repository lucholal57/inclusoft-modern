package ar.org.inclusoft.api.auth;

import ar.org.inclusoft.api.user.UserRole;

public record LoginResponse(String token, String username, String displayName, UserRole role, boolean mustChangePassword) {}