package com.ratingsystem.controller;

import com.ratingsystem.dto.request.EmailDTO;
import com.ratingsystem.dto.request.ResetPasswordDTO;
import com.ratingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/help")
public class PasswordResetController {
    private final UserService userService;

    public PasswordResetController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<String> sendRestorationCodeToEmail(@RequestBody @Valid EmailDTO emailDTO){
        userService.sendRestorationCodeToEmail(emailDTO.email());
        return ResponseEntity.ok("Check the provided email for restoration code");
    }

    @PostMapping("/check_code/{code}")
    public ResponseEntity<String> checkCode(
            @PathVariable String code,
            @RequestBody @Valid EmailDTO emailDTO
    ){
        if (userService.checkRestorationCode(code, emailDTO.email())){
            return ResponseEntity.ok("Code is valid");
        }
        return ResponseEntity.badRequest().body("Code is not valid");
    }

    @PostMapping("/reset/{code}")
    public ResponseEntity<String> resetPassword(
            @PathVariable String code,
            @RequestBody @Valid ResetPasswordDTO resetPasswordDTO
    ){
        if (userService.checkRestorationCode(code, resetPasswordDTO.email())){
            userService.resetUserPassword(resetPasswordDTO.email(), resetPasswordDTO.newPassword());
        }
        return ResponseEntity.ok("Password successfully changed");
    }
}
