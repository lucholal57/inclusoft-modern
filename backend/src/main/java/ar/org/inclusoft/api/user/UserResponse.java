package ar.org.inclusoft.api.user;

import java.util.UUID;

public record UserResponse(UUID id, String username, String displayName, UserRole role, boolean enabled, boolean mustChangePassword, UUID staffMemberId, String staffMemberName) {
    static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole(), user.isEnabled(), user.isMustChangePassword(), user.getStaffMember() == null ? null : user.getStaffMember().getId(), user.getStaffMember() == null ? null : user.getStaffMember().getFullName());
    }
}
