package com.jlmasa.pms_backend.security;

import com.jlmasa.pms_backend.identity.domain.MembershipStatus;
import com.jlmasa.pms_backend.identity.domain.OrganizationMembership;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MembershipAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final OrganizationMembershipRepository membershipRepository;

    public MembershipAuthenticationConverter(OrganizationMembershipRepository membershipRepository){
        this.membershipRepository  = membershipRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt){
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID organizationId = UUID.fromString(jwt.getClaimAsString("org"));
        UUID membershipId = UUID.fromString(jwt.getClaimAsString("mem"));

        OrganizationMembership membership = membershipRepository.findByIdAndOrganizationId(membershipId, organizationId)
                .orElseThrow(() -> new BadCredentialsException("Membership not found."));
        if(membership.getMembershipStatus() != MembershipStatus.ACTIVE){
            throw new BadCredentialsException("Membership is not active.");
        }
        if (!membership.getUser().getId().equals(userId) || !membership.getUser().isEnabled()) {
            throw new BadCredentialsException("Account is disabled or token does not match this membership.");
        }
        PmsPrincipal principal = new PmsPrincipal(userId, organizationId, membershipId);
        var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, jwt, List.of());
        return authToken;
    }
}
