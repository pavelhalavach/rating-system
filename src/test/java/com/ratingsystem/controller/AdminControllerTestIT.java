package com.ratingsystem.controller;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.request.RegisterSellerRequestDTO;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.Game;
import com.ratingsystem.entity.Role;
import com.ratingsystem.entity.User;
import com.ratingsystem.exception.CommentNotFoundException;
import com.ratingsystem.exception.GameNotFoundException;
import com.ratingsystem.exception.UserNotFoundException;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.GameRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.CommentService;
import com.ratingsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminControllerTestIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        RegisterSellerRequestDTO registerSellerRequestDTO1 = new RegisterSellerRequestDTO(
                "seller1",
                "firstName",
                "lastName",
                "seller1@example.com",
                "123",
                sellerOffersDTO
        );
        if (userRepository.findByNickname("seller1").isEmpty()) {
            userService.registerSeller(registerSellerRequestDTO1);
            commentService.saveCommentByAnonUser(
                    userRepository.findByNickname("seller1")
                            .orElseThrow(() -> new UserNotFoundException("seller1")),
                    "Good prices!",
                    5);
            commentService.saveCommentByAnonUser(
                    userRepository.findByNickname("seller1")
                            .orElseThrow(() -> new UserNotFoundException("seller1")),
                    "Don't buy, scam!!",
                    1);
            commentService.saveCommentByAnonUser(
                    userRepository.findByNickname("seller1")
                            .orElseThrow(() -> new UserNotFoundException("seller1")),
                    "Thanks!",
                    4);
        }

        RegisterSellerRequestDTO registerSellerRequestDTO2 = new RegisterSellerRequestDTO(
                "seller2",
                "firstName",
                "lastName",
                "seller2@example.com",
                "123",
                sellerOffersDTO
        );
        if (userRepository.findByNickname("seller2").isEmpty()) {
            userService.registerSeller(registerSellerRequestDTO2);
            commentService.saveCommentByAnonUser(
                    userRepository.findByNickname("seller2")
                            .orElseThrow(() -> new UserNotFoundException("seller2")),
                    "Thank you!",
                    5);
            commentService.saveCommentByAnonUser(
                    userRepository.findByNickname("seller2")
                            .orElseThrow(() -> new UserNotFoundException("seller2")),
                    "The quality is bad",
                    3);
        }

        User verifiedAdmin = new User(
                null,
                "HeadAdmin",
                "firstName",
                "lastName",
                "admin@example.com",
                passwordEncoder.encode("123"),
                true,
                true,
                new Date(),
                Role.ADMIN
        );
        User nonVerifiedAdmin = new User(
                null,
                "admin",
                "firstName",
                "lastName",
                "admin1@example.com",
                passwordEncoder.encode("123"),
                false,
                true,
                new Date(),
                Role.ADMIN
        );

        if(userRepository.findByNickname("HeadAdmin").isEmpty()) {
            userRepository.save(verifiedAdmin);
        }
        if(userRepository.findByNickname("admin").isEmpty()) {
            userRepository.save(nonVerifiedAdmin);
        }

        User seller2 = userRepository.findByNickname("seller2")
                .orElseThrow(() -> new UserNotFoundException("seller2"));
        seller2.setVerified(false);
        userRepository.save(seller2);

        User seller1 = userRepository.findByNickname("seller1")
                .orElseThrow(() -> new UserNotFoundException("seller1"));
        seller1.setVerified(true);
        userRepository.save(seller1);

        Game game1 = gameRepository.findByNameNormalized("game1");
        game1.setVerified(true);
        gameRepository.save(game1);

        Comment comment1 = commentRepository.findById(1)
                .orElseThrow(() -> new CommentNotFoundException(1));
        comment1.setVerified(true);
        commentRepository.save(comment1);
    }

    @Test
    void shouldFindNonVerifiedSellers() throws Exception {
        HttpSession session = getAdminSession();
        mockMvc.perform(get("/admin/sellers")
                .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("seller2"));

        assertTrue(userRepository.findByNickname("seller2").isPresent());
    }

    @Test
    void shouldFindNonVerifiedAdmins() throws Exception {
        HttpSession session = getAdminSession();
        mockMvc.perform(get("/admin/admins")
                .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value("admin"));

        assertTrue(userRepository.findByNickname("admin").isPresent());
    }

    @Test
    void shouldFindNonVerifiedComments() throws Exception {
        HttpSession session = getAdminSession();
        mockMvc.perform(get("/admin/comments")
                .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(commentRepository.findAllByIsVerified(false).size()));
    }

    @Test
    void shouldFindNonVerifiedGames() throws Exception {
        HttpSession session = getAdminSession();
        mockMvc.perform(get("/admin/games")
                .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("game2"))
                .andExpect(jsonPath("$.size()").value(2));

        assertEquals(2, gameRepository.findAllByIsVerified(false).size());
    }

    @Test
    void shouldNotReviewUserByUnverifiedAdmin() throws Exception {
        HttpSession session = getAdminSession();
        String json = "{\"decision\": true}";
        User user = userRepository.findByNickname("seller1")
                .orElseThrow(() -> new UserNotFoundException("seller1"));

        mockMvc.perform(post("/admin/user/" + user.getId())
                    .session((MockHttpSession) session)
                .contentType("application/json")
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotReviewVerifiedUserByVerifiedAdmin() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        User user = userRepository.findByNickname("seller1")
                .orElseThrow(() -> new UserNotFoundException("seller1"));

        mockMvc.perform(post("/admin/user/" + + user.getId())
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReviewUserByVerifiedAdminAsApproved() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        User user = userRepository.findByNickname("seller2")
                .orElseThrow(() -> new UserNotFoundException("seller2"));

        mockMvc.perform(post("/admin/user/" + user.getId())
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        user = userRepository.findByNickname("seller2")
                .orElseThrow(() -> new UserNotFoundException("seller2"));
        assertTrue(user.isVerified());
    }

    @Test
    void shouldReviewUserByVerifiedAdminAsDeclined() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": false}";
        User user = userRepository.findByNickname("seller2")
                .orElseThrow(() -> new UserNotFoundException("seller2"));

        mockMvc.perform(post("/admin/user/" + user.getId())
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        assertTrue(userRepository.findByNickname("seller2").isEmpty());
    }

    @Test
    void shouldNotReviewNonExistingUser() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/user/1000")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReviewUserWithBadJson() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "Wrong json";
        mockMvc.perform(post("/admin/user/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotReviewCommentByUnverifiedAdmin() throws Exception {
        HttpSession session = getAdminSession();
        String json = "{\"decision\": true}";

        mockMvc.perform(post("/admin/comment/1")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotReviewVerifiedCommentByVerifiedAdmin() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/comment/1")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReviewCommentByVerifiedAdminAsApproved() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/comment/3")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        Comment comment = commentRepository.findById(3)
                .orElseThrow(() -> new CommentNotFoundException(3));
        assertTrue(comment.isVerified());
    }

    @Test
    void shouldReviewCommentByVerifiedAdminAsDeclined() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": false}";
        mockMvc.perform(post("/admin/comment/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        assertTrue(commentRepository.findById(2).isEmpty());
    }

    @Test
    void shouldNotReviewNonExistingComment() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/comment/1000")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReviewCommentWithBadJson() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "Wrong json";
        mockMvc.perform(post("/admin/comment/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotReviewGameByUnverifiedAdmin() throws Exception {
        HttpSession session = getAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/game/1")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotReviewVerifiedGameByVerifiedAdmin() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/game/1")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReviewGameByVerifiedAdminAsApproved() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/game/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        Game game = gameRepository.findById(2)
                .orElseThrow(() -> new GameNotFoundException(2));
        assertTrue(game.isVerified());
    }

    @Test
    void shouldReviewGameByVerifiedAdminAsDeclined() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": false}";
        mockMvc.perform(post("/admin/game/3")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        assertTrue(gameRepository.findById(3).isEmpty());
    }

    @Test
    void shouldNotReviewNonExistingGame() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "{\"decision\": true}";
        mockMvc.perform(post("/admin/game/1000")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotReviewGameWithBadJson() throws Exception {
        HttpSession session = getVerifiedAdminSession();
        String json = "Wrong json";
        mockMvc.perform(post("/admin/game/2")
                        .session((MockHttpSession) session)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }



    HttpSession getAdminSession() throws Exception {
        String json = "{\n" +
                "  \"email\": \"admin1@example.com\",\n" +
                "  \"password\": \"123\"\n" +
                "}";
        MvcResult result = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        return result.getRequest().getSession();
    }

    HttpSession getVerifiedAdminSession() throws Exception {
        String json = "{\n" +
                "  \"email\": \"admin@example.com\",\n" +
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
