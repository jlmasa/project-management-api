package com.jlmasa.pms_backend.messaging.web;

import com.jlmasa.pms_backend.messaging.service.ReactionService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages/{messageId}/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService){
        this.reactionService = reactionService;
    }

    @PutMapping("/{emoji}")
    public void react(PmsPrincipal principal, @PathVariable UUID messageId, @PathVariable String emoji){
        reactionService.react(principal.membershipId(), messageId, emoji);
    }

    @DeleteMapping("/{emoji}")
    public void unreact(PmsPrincipal principal, @PathVariable UUID messageId, @PathVariable String emoji){
        reactionService.unreact(principal.membershipId(), messageId, emoji);
    }
}
