package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.request.UpdateSellerRequestDTO;
import com.ratingsystem.dto.response.SellerOfferDTO;
import com.ratingsystem.entity.User;
import com.ratingsystem.security.CustomUserDetails;
import com.ratingsystem.service.SellerOfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {
    private final SellerOfferService sellerOfferService;

    public SellerController(SellerOfferService sellerOfferService) {
        this.sellerOfferService = sellerOfferService;
    }

    @GetMapping("/offers")
    public List<SellerOfferDTO> findSellerOffers(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User seller = customUserDetails.getUser();
        return sellerOfferService.findAllVerifiedSellerOffers(seller);
    }

    @PostMapping("/offer")
    public ResponseEntity<String> addSellerOffer(
            @RequestBody RegisterSellerOfferDTO registerSellerOfferDTO,
            Authentication authentication
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User seller = customUserDetails.getUser();
        sellerOfferService.saveSellerOffer(seller, registerSellerOfferDTO);
        return ResponseEntity.ok("Game successfully added");
    }

    @DeleteMapping("/offer/{id}")
    public ResponseEntity<String> deleteSellerOffer(@PathVariable(name="id") Integer id, Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User seller = customUserDetails.getUser();
        sellerOfferService.deleteBySellerAndGameId(seller, id);
        return ResponseEntity.ok("Game successfully deleted");
    }

    @PatchMapping("/offer/{id}")
    public ResponseEntity<String> updateSellerOffer(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateSellerRequestDTO updateSellerRequestDTO,
            Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User seller = customUserDetails.getUser();
        sellerOfferService.updateOfferDescription(seller, id, updateSellerRequestDTO.updatedDescription());
        return ResponseEntity.ok("Game successfully updated");
    }
}
