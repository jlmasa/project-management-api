package com.jlmasa.pms_backend.identity.service;

import com.jlmasa.pms_backend.identity.domain.MembershipStatus;
import com.jlmasa.pms_backend.identity.domain.OrganizationMembership;
import com.jlmasa.pms_backend.identity.domain.UserAccount;
import com.jlmasa.pms_backend.identity.dto.LoginResponse;
import com.jlmasa.pms_backend.identity.dto.MembershipSummary;
import com.jlmasa.pms_backend.identity.repository.AuditLogRepository;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import com.jlmasa.pms_backend.identity.repository.UserAccountRepository;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import com.jlmasa.pms_backend.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {


    private static final String GENERIC_LOGIN_FAILURE = "Invalid email, password, or organization.";
    private final UserAccountRepository userAccountRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;
    public AuthService(UserAccountRepository userAccountRepository, OrganizationMembershipRepository membershipRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService, AuditLogRepository auditLogRepository, Clock clock) {
        this.userAccountRepository = userAccountRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }


    @Transactional
    public LoginResponse login(String email, String plainPassword, String organizationSlug) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Optional<UserAccount> accountOpt = userAccountRepository.findByEmail(normalizedEmail);

        String hashToCheck = accountOpt.map(UserAccount::getPasswordHash)
                .orElse("$2a$12$invalidinvalidinvalidinvalidinvalidinvalidinvalidinval");
        boolean passwordMatches = passwordEncoder.matches(plainPassword, hashToCheck);
        if (accountOpt.isEmpty() || !passwordMatches) {
            recordFailure(null, "LOGIN_FAILED_BAD_CREDENTIALS");
            throw new BadCredentialsException(GENERIC_LOGIN_FAILURE);
        }
        UserAccount account = accountOpt.get();
        if (!account.isEnabled()) {
            recordFailure(account.getId(), "LOGIN_FAILED_ACCOUNT_DISABLED");
            throw new BadCredentialsException(GENERIC_LOGIN_FAILURE);
        }
            OrganizationMembership membership = resolveMembership(account, organizationSlug);

            String token = jwtService.issueAccessToken(account.getId(), membership.getOrganization().getId(), membership.getId());
            recordSuccess(membership);

            return new LoginResponse(token , "Bearer", jwtService.accessTtlSeconds(),
                    new MembershipSummary(membership.getId(), membership.getOrganization().getId(), membership.getOrganization().getName(), account.getDisplayName()));
        }
    private OrganizationMembership resolveMembership(UserAccount account, String organizationSlug) {
        List<OrganizationMembership> active = membershipRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(account.getId()) && m.getMembershipStatus() == MembershipStatus.ACTIVE)
                .toList();

        if (active.isEmpty()) {
            recordFailure(account.getId(), "LOGIN_FAILED_NO_ACTIVE_MEMBERSHIP");
            throw new BadCredentialsException(GENERIC_LOGIN_FAILURE);
        }
        if (active.size() == 1 && organizationSlug == null) {
            return active.get(0);
        }
        return active.stream()
                .filter(m -> m.getOrganization().getSlug().equalsIgnoreCase(organizationSlug))
                .findFirst()
                .orElseThrow(() -> {
                    recordFailure(account.getId(), "LOGIN_FAILED_WRONG_ORGANIZATION");
                    return new BadCredentialsException(GENERIC_LOGIN_FAILURE);
                });
    }
    private void recordFailure(java.util.UUID userId, String reason) {
        AuditLog event = new AuditLog(
                userId != null ? userId : new java.util.UUID(0, 0), reason, "users", userId, clock.instant());
        auditLogRepository.save(event);
    }
    private void recordSuccess(OrganizationMembership membership) {
        AuditLog event = new AuditLog(membership.getOrganization().getId(), "LOGIN_SUCCEEDED", "organization_memberships",
                membership.getId(), clock.instant());
        event.withActor(membership.getId());
        auditLogRepository.save(event);
    }
}

