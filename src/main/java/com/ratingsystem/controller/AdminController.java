package com.ratingsystem.controller;


import com.ratingsystem.dto.request.DecisionOnReviewDTO;
import com.ratingsystem.dto.response.CommentDTO;
import com.ratingsystem.dto.response.GameDTO;
import com.ratingsystem.dto.response.SellerDTO;
import com.ratingsystem.entity.Role;
import com.ratingsystem.entity.User;
import com.ratingsystem.security.CustomUserDetails;
import com.ratingsystem.service.CommentService;
import com.ratingsystem.service.GameService;
import com.ratingsystem.service.ReviewStatus;
import com.ratingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CommentService commentService;
    private final GameService gameService;

    public AdminController(UserService userService, CommentService commentService, GameService gameService) {
        this.userService = userService;
        this.commentService = commentService;
        this.gameService = gameService;
    }

    @GetMapping("/sellers")
    public List<SellerDTO> findNonVerifiedSellers(){
        return userService.findAllUsersByIsVerified(Role.SELLER,false);
    }

    @GetMapping("/admins")
    public List<SellerDTO> findNonVerifiedAdmins(){
        return userService.findAllUsersByIsVerified(Role.ADMIN,false);
    }

    @GetMapping("/comments")
    public List<CommentDTO> findNonVerifiedComments(){
        return commentService.findAllCommentsByIsVerified(false);
    }

    @GetMapping("/games")
    public List<GameDTO> findNonVerifiedGames(){
        return gameService.findAllGamesByIsVerified(false);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<String> reviewUser(@PathVariable(name="id") Integer id,
                                             @RequestBody @Valid DecisionOnReviewDTO decisionOnReviewDTO,
                                             Authentication authentication
    ){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User admin = customUserDetails.getUser();

        if (!admin.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not verified to do that. Please contact Head Admin");
        }

        if (userService.reviewUser(id, decisionOnReviewDTO.decision()) == ReviewStatus.ALREADY_VERIFIED){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is already verified");
        }

        return ResponseEntity.ok("Successfully reviewed");
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<String> reviewComment(@PathVariable Integer id,
                                                @RequestBody @Valid DecisionOnReviewDTO decisionOnReviewDTO,
                                                Authentication authentication
    ){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User admin = customUserDetails.getUser();

        if (!admin.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not verified to do that. Please contact Head Admin");
        }

        if (commentService.reviewComment(id, decisionOnReviewDTO.decision()) == ReviewStatus.ALREADY_VERIFIED) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Comment is already verified");
        }
        return ResponseEntity.ok("Successfully reviewed");
    }

    @PostMapping("/game/{id}")
    public ResponseEntity<String> reviewGame(@PathVariable Integer id,
                                             @RequestBody @Valid DecisionOnReviewDTO decisionOnReviewDTO,
                                             Authentication authentication
    ){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User admin = customUserDetails.getUser();

        if (!admin.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not verified to do that. Please contact Head Admin");
        }

        if (gameService.reviewGame(id, decisionOnReviewDTO.decision()) == ReviewStatus.ALREADY_VERIFIED){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Game is already verified");
        }

        return ResponseEntity.ok("Successfully reviewed");
    }
}
