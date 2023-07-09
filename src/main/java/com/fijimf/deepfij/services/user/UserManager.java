package com.fijimf.deepfij.services.user;

import com.fijimf.deepfij.db.model.user.AuthToken;
import com.fijimf.deepfij.db.model.user.Role;
import com.fijimf.deepfij.db.model.user.User;
import com.fijimf.deepfij.db.repo.user.AuthTokenRepo;
import com.fijimf.deepfij.db.repo.user.RoleRepo;
import com.fijimf.deepfij.db.repo.user.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserManager implements UserDetailsService, UserDetailsPasswordService {
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final AuthTokenRepo authTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final RandomStringGenerator rsg;
    public static final String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+){0,4}@(?:[a-zA-Z0-9-]+\\.){1,6}[a-zA-Z]{2,6}$";
    public static final Predicate<String> emailMatches = Pattern.compile(EMAIL_REGEX).asMatchPredicate();

    @Autowired
    public UserManager(UserRepo userRepository, RoleRepo roleRepository, AuthTokenRepo authTokenRepository, PasswordEncoder passwordEncoder, RandomStringGenerator rsg) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.rsg = rsg;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> ou = userRepository.findFirstByUsername(s);
        if (ou.isEmpty()) {
            throw new UsernameNotFoundException("Could not fine uer " + s);
        } else {
            return ou.get();
        }
    }

    public String createNewUser(String username, String password, String email, List<String> roles) {
        return createNewUser(username, password, email, roles, -1);
    }

    public String createNewUser(String username, String password, String email, List<String> roles, int expiryMinutes) {
        validateInputs(username, password, email, roles);
        User user = new User(0L, username, passwordEncoder.encode(password), email, false);
        user.setActivated(false);
        user.setLocked(false);
        if (expiryMinutes > 0) {
            user.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        }
        if (userRepository.findFirstByEmail(email).isEmpty()) {
            if (userRepository.findFirstByUsername(username).isEmpty()) {
                List<Role> rs = roles.stream().flatMap(r -> roleRepository.findFirstByName(r).stream()).collect(Collectors.toList());
                if (!rs.isEmpty()) {
                    user.setRoles(rs);
                    User u = userRepository.save(user);
                    AuthToken auth = new AuthToken(u.getId(), rsg.generate(12), LocalDateTime.now().plusHours(3));
                    authTokenRepository.save(auth);
                    return auth.getToken();
                } else {
                    throw new RuntimeException("No roles found for new user");
                }
            } else {
                throw new DuplicatedUsernameException(username);
            }
        } else {
            throw new DuplicatedEmailException(email);
        }
    }

    private void validateInputs(String username, String password, String email, List<String> roles) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("username must not be null or blank.");
        if (StringUtils.isBlank(password)) throw new IllegalArgumentException("password must not be null or blank.");
        if (StringUtils.isBlank(email)) throw new IllegalArgumentException("email must not be null or blank.");
        if (!emailMatches.test(email)) throw new IllegalArgumentException("email '" + email + "' is malformed");
        if (roles == null || roles.isEmpty()) throw new IllegalArgumentException("roles must not be null or empty.");
    }

    public Optional<User> activateUser(String token) {
        Optional<AuthToken> ot = authTokenRepository.findByToken(token);
        if (ot.isPresent()) {
            if (ot.get().getExpiresAt().isAfter(LocalDateTime.now())) {
                Optional<User> ou = userRepository.findById(ot.get().getUserId());
                if (ou.isPresent()) {
                    User user = ou.get();
                    if (!user.isActivated()) {
                        user.setActivated(true);
                        userRepository.save(user);
                    }
                    authTokenRepository.deleteById(ot.get().getId());
                }
                return ou;
            } else {
                authTokenRepository.deleteById(ot.get().getId());
            }
        }
        return Optional.empty();
    }

    public Optional<String> forgottenPassword(String email) {
        String password = rsg.generate(15);
        return userRepository.findFirstByEmail(email).map(u -> {
            if (u.isEnabled() && u.isCredentialsNonExpired()) {
                u.setPassword(passwordEncoder.encode(password));
                u.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(10));
                userRepository.save(u);
            }
            return password;
        });
    }

    public Optional<String> forgottenUser(String email) {
        return userRepository.findFirstByEmail(email).map(User::getUsername);
    }

    public Optional<User> changePassword(String principal, String oldPassword, String newPassword) {
        return userRepository.findFirstByUsername(principal).map(u -> {
            String savedCiphertext = u.getPassword();
            if (passwordEncoder.matches(oldPassword, savedCiphertext)) {
                u.setPassword(passwordEncoder.encode(newPassword));
                u.setExpireCredentialsAt(null);
                userRepository.save(u);
                return u;
            } else {
                throw new BadCredentialsException("Bad credentials for " + principal);
            }
        });
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }

}