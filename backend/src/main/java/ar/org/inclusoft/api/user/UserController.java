package ar.org.inclusoft.api.user;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    UserController(UserService userService) { this.userService = userService; }

    @GetMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'VICE_DIRECTOR')")
    public List<UserResponse> findAll() { return userService.findAll(); }

    @PostMapping @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request)); }

    @PutMapping("/{username}") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public UserResponse update(@PathVariable String username, @Valid @RequestBody UpdateUserRequest request, Authentication authentication) { return userService.update(username, authentication.getName(), roleOf(authentication), request); }

    @PatchMapping("/{username}/deactivate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public UserResponse deactivate(@PathVariable String username, Authentication authentication) { return userService.deactivate(username, authentication.getName(), roleOf(authentication)); }

    @PatchMapping("/{username}/activate") @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public UserResponse activate(@PathVariable String username, Authentication authentication) { return userService.activate(username, authentication.getName(), roleOf(authentication)); }

    private UserRole roleOf(Authentication authentication) { return authentication.getAuthorities().stream().map(authority -> authority.getAuthority()).filter(authority -> authority.startsWith("ROLE_")).findFirst().map(authority -> UserRole.valueOf(authority.substring(5))).orElseThrow(); }
    @ExceptionHandler(DuplicateUsernameException.class) ResponseEntity<Map<String, String>> duplicateUsername(DuplicateUsernameException exception) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage())); }
    @ExceptionHandler(UserNotFoundException.class) ResponseEntity<Map<String, String>> userNotFound(UserNotFoundException exception) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage())); }
    @ExceptionHandler(UserOperationNotAllowedException.class) ResponseEntity<Map<String, String>> operationNotAllowed(UserOperationNotAllowedException exception) { return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage())); }
}