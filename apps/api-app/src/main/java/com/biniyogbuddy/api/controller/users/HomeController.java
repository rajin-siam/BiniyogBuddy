package com.biniyogbuddy.api.controller.users;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final MessageResource messageResource;

    @GetMapping("/home")
    public ResponseEntity<MessageResponse> home() {
        String message = messageResource.getMessage("general.welcome");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
