package com.jlmasa.pms_backend.identity.service;

import com.jlmasa.pms_backend.identity.domain.UserAccount;
import com.jlmasa.pms_backend.identity.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

@Service
public class AccountService {

    private static final Set<String> COMMON_PASSWORD_BLOCKLIST = Set.of("password", "password1", "12345678", "qwerty123", "letmein1","admin123");
    private static final int MIN_PASSWORD_LENGTH = 10;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, Clock clock){
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public UserAccount createAccount(String email, String displayName, String plainPassword){
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if(userAccountRepository.existsByEmail(normalizedEmail)){
            throw new IllegalStateException("An account with this email already exists");
        }
        validatePassword(plainPassword);
        Instant now = clock.instant();
        UserAccount account = new UserAccount(normalizedEmail, displayName, now);
        account.changePasswordHash(passwordEncoder.encode(plainPassword),now);
        return userAccountRepository.save(account);
    }
    @Transactional
    public void changePassword(UserAccount account, String newPlainPassword){
        validatePassword(newPlainPassword);
        account.changePasswordHash(passwordEncoder.encode(newPlainPassword), clock.instant());
    }

    @Transactional
    public void disable(UserAccount account){
        account.disabled(clock.instant());
    }

    private void validatePassword(String plainPassword){
        if(plainPassword == null || plainPassword.length() < MIN_PASSWORD_LENGTH){
            throw new IllegalArgumentException("Password must be at least "+ MIN_PASSWORD_LENGTH + "characters.");
        }
        if(COMMON_PASSWORD_BLOCKLIST.contains(plainPassword.toLowerCase(Locale.ROOT))){
            throw new IllegalArgumentException("This password is too common. Choose a less predictable one.");
        }
    }
}
