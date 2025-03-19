package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterAdminRequestDTO;
import com.ratingsystem.dto.request.RegisterSellerRequestDTO;
import com.ratingsystem.dto.response.GameDTO;
import com.ratingsystem.dto.response.RegisterInfoDTO;
import com.ratingsystem.service.GameService;
import com.ratingsystem.service.UserService;
import com.ratingsystem.util.AesEncryptionUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {
    private final UserService userService;
    private final GameService gameService;
    private final AesEncryptionUtil aesEncryptionUtil;

    public RegistrationController(UserService userService, GameService gameService, AesEncryptionUtil aesEncryptionUtil) {
        this.userService = userService;
        this.gameService = gameService;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    @GetMapping("/registration/seller")
    public RegisterInfoDTO giveInfoForSeller(){
        List<GameDTO> games = gameService.findAllGames();
        String message = "Welcome! You can register a Seller Profile and select Games in which you sell items. " +
                "If you can't find needed Game you can also add it. " +
                "You will need to confirm your email +" +
                "It will take some time until the Administrator approves it. " +
                "Available games are: ";
        return new RegisterInfoDTO(message, games);
    }

    @PostMapping("/registration/seller")
    public ResponseEntity<String> saveSeller(@RequestBody @Valid RegisterSellerRequestDTO registerSellerRequestDTO){
        userService.registerSeller(registerSellerRequestDTO);
        return ResponseEntity.ok("Please confirm your registration by clicking the link in the email");
    }

    @PostMapping("/registration/admin")
    public ResponseEntity<String> saveAdmin(@RequestBody @Valid RegisterAdminRequestDTO registerAdminRequestDTO){
        userService.registerAdmin(registerAdminRequestDTO);
        return ResponseEntity.ok("Please confirm your registration by clicking the link in the email");
    }

    @GetMapping("/confirm_registration/{token}")
    public ResponseEntity<String> confirmRegistration(@PathVariable String token) {
        if (userService.checkRegistrationTokenDate(token)) {
            userService.confirmRegistration(token);
        } else {
            return ResponseEntity.badRequest().body("Token is expired");
        }
        return ResponseEntity.ok("Registration confirmed!");
    }
}
