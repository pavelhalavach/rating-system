package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.request.RegisterSellerRequestDTO;
import com.ratingsystem.entity.Game;
import com.ratingsystem.entity.SellerOffer;
import com.ratingsystem.entity.User;
import com.ratingsystem.exception.UserNotFoundException;
import com.ratingsystem.repository.GameRepository;
import com.ratingsystem.repository.SellerOfferRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.SellerOfferService;
import com.ratingsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SellerControllerTestIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SellerOfferService sellerOfferService;
    @Autowired
    private SellerOfferRepository sellerOfferRepository;
    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp(){
        RegisterSellerOfferDTO registerSellerOfferDTO1 =
                new RegisterSellerOfferDTO("game1", "description1");
        RegisterSellerOfferDTO registerSellerOfferDTO2 =
                new RegisterSellerOfferDTO("game2", "description2");
        RegisterSellerOfferDTO registerSellerOfferDTO3 =
                new RegisterSellerOfferDTO("game3", "description3");
        List<RegisterSellerOfferDTO> sellerOffersDTO = new ArrayList<>();
        sellerOffersDTO.add(registerSellerOfferDTO1);
        sellerOffersDTO.add(registerSellerOfferDTO2);
        sellerOffersDTO.add(registerSellerOfferDTO3);
        RegisterSellerRequestDTO registerSellerRequestDTO = new RegisterSellerRequestDTO(
                "seller1",
                "firstName",
                "lastName",
                "seller1@example.com",
                "123",
                sellerOffersDTO
        );
        if (userRepository.findByNickname("seller1").isEmpty()) {
            userService.registerSeller(registerSellerRequestDTO);
            User seller = userRepository.findByNickname("seller1")
                    .orElseThrow(() -> new UserNotFoundException("seller1"));
            seller.setEnabled(true);
            userRepository.save(seller);
        }
        Game game1 = gameRepository.findByNameNormalized("game1");
        game1.setVerified(true);
        gameRepository.save(game1);
        Game game2 = gameRepository.findByNameNormalized("game2");
        game2.setVerified(true);
        gameRepository.save(game2);
    }

    @Test
    void shouldFindAllSellerOffers() throws Exception {
        HttpSession session = getSellerSession();
        User seller = userRepository.findByNickname("seller1")
                .orElseThrow(() -> new UserNotFoundException("seller1"));

        mockMvc.perform(get("/seller/offers")
                        .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()")
                        .value(sellerOfferService.findAllVerifiedSellerOffers(seller).size()));

    }

    @Test
    void shouldAddSellerOffer() throws Exception {
        HttpSession session = getSellerSession();
        String json = "{\n" +
                "  \"name\": \"FIFA\",\n" +
                "  \"description\": \"Selling best cards\"\n" +
                "}";
        assertNull(gameRepository.findByNameNormalized("FIFA"));

        mockMvc.perform(post("/seller/offer")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        Game game = gameRepository.findByNameNormalized("FIFA");
        assertNotNull(game);
        assertTrue(sellerOfferRepository.findByGameId(game.getId()).isPresent());
    }

    @Test
    void shouldNotAddSellerOfferWithBadJson() throws Exception {
        HttpSession session = getSellerSession();
        String json = "Wrong json";

        mockMvc.perform(post("/seller/offer")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSellerOffer() throws Exception {
        HttpSession session = getSellerSession();
        Game game = gameRepository.findByNameNormalized("game1");
        assertTrue(sellerOfferRepository.findByGameId(game.getId()).isPresent());

        mockMvc.perform(delete("/seller/offer/1")
                        .session((MockHttpSession) session))
                .andExpect(status().isOk());

        assertTrue(sellerOfferRepository.findByGameId(game.getId()).isEmpty());
    }

    @Test
    void shouldUpdateSellerOffer() throws Exception {
        HttpSession session = getSellerSession();
        String json = "{\n" +
                "  \"updatedDescription\": \"new description\"\n" +
                "}";
        Game game = gameRepository.findByNameNormalized("game2");
        SellerOffer sellerOffer = sellerOfferRepository.findByGameId(game.getId())
                .orElseThrow(() -> new RuntimeException("No such Seller Offer"));
        assertNotEquals("new description", sellerOffer.getDescription());

        mockMvc.perform(patch("/seller/offer/2")
                        .session((MockHttpSession) session)
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk());

        SellerOffer sellerOfferUpdated = sellerOfferRepository.findByGameId(game.getId())
                .orElseThrow(() -> new RuntimeException("No such Seller Offer"));
        assertEquals("new description", sellerOfferUpdated.getDescription());
    }

    @Test
    void shouldNotUpdateSellerOfferWithBadJson() throws Exception {
        HttpSession session = getSellerSession();
        String json = "Wrong json";

        mockMvc.perform(patch("/seller/offer/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    HttpSession getSellerSession() throws Exception {
        String json = "{\n" +
                "  \"email\": \"seller1@example.com\",\n" +
                "  \"password\": \"123\"\n" +
                "}";
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        return result.getRequest().getSession();
    }
}
