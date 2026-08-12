package ar.org.inclusoft.api.auth;

import ar.org.inclusoft.api.security.JwtTokenService;
import ar.org.inclusoft.api.user.AppUser;
import ar.org.inclusoft.api.user.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(request.username().trim())
                .filter(AppUser::isEnabled)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return responseFor(user);
    }

    @Transactional
    LoginResponse changePassword(String username, ChangePasswordRequest request) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .filter(AppUser::isEnabled)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        return responseFor(user);
    }

    private LoginResponse responseFor(AppUser user) {
        return new LoginResponse(
                jwtTokenService.createToken(user.getUsername(), user.getRole(), user.isMustChangePassword()),
                user.getUsername(), user.getDisplayName(), user.getRole(), user.isMustChangePassword());
    }
}