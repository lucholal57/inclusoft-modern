package ar.org.inclusoft.api.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class BootstrapAdminUser {

    @Bean
    ApplicationRunner bootstrapAdministrator(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
            @Value("${inclusoft.bootstrap.admin-password:}") String password,
            @Value("${inclusoft.bootstrap.admin-username:admin}") String username) {
        return arguments -> {
            if (appUserRepository.count() == 0 && !password.isBlank()) {
                appUserRepository.save(new AppUser(username, "Administrador", passwordEncoder.encode(password), UserRole.ADMIN));
            }
        };
    }
}
