package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.*;
import com.ratingsystem.dto.response.CommentDTO;
import com.ratingsystem.dto.response.SellerDTO;
import com.ratingsystem.dto.response.SellerOfferDTO;
import com.ratingsystem.dto.response.ShowTopSellersResponseDTO;
import com.ratingsystem.entity.Role;
import com.ratingsystem.entity.User;
import com.ratingsystem.exception.EmailAlreadyTakenException;
import com.ratingsystem.exception.NicknameAlreadyTakenException;
import com.ratingsystem.exception.TokenNotValidException;
import com.ratingsystem.exception.UserNotFoundException;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.*;
import com.ratingsystem.util.AesEncryptionUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceJPAImpl implements UserService {
    private final UserRepository userRepository;
    private final SellerOfferService sellerOfferService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AesEncryptionUtil aesEncryptionUtil;

    public UserServiceJPAImpl(
            UserRepository userRepository,
            SellerOfferService sellerOfferService, CommentService commentService,
            PasswordEncoder passwordEncoder, EmailService emailService, AesEncryptionUtil aesEncryptionUtil) {
        this.userRepository = userRepository;
        this.sellerOfferService = sellerOfferService;
        this.commentService = commentService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    @Override
    public void registerSeller(RegisterSellerRequestDTO registerSellerRequestDTO) {
        if (userRepository.findByEmail(registerSellerRequestDTO.email()).isPresent()){
            throw new EmailAlreadyTakenException(registerSellerRequestDTO.email());
        }
        if (userRepository.findByNickname(registerSellerRequestDTO.nickname()).isPresent()){
            throw new NicknameAlreadyTakenException(registerSellerRequestDTO.nickname());
        }

        User seller = new User();
        seller.setNickname(registerSellerRequestDTO.nickname());
        seller.setFirstName(registerSellerRequestDTO.firstName());
        seller.setLastName(registerSellerRequestDTO.lastName());
        seller.setEmail(registerSellerRequestDTO.email());
        seller.setPassword(passwordEncoder.encode(registerSellerRequestDTO.password()));
        seller.setRole(Role.valueOf("SELLER"));
        seller.setVerified(false);
        seller.setEnabled(false);
        seller = userRepository.save(seller);

        for (RegisterSellerOfferDTO registerSellerOfferDTO : registerSellerRequestDTO.games()) {
            sellerOfferService.saveSellerOffer(seller, registerSellerOfferDTO);
        }

        String tokenData = "{\"user_id\": " + seller.getId() + ", \"time\": " + Instant.now().getEpochSecond() + "}";
        String encryptedToken = aesEncryptionUtil.encrypt(tokenData);
        String confirmationLink = "http://localhost:8080/confirm_registration/" + encryptedToken;
        emailService.sendEmail(
                seller.getEmail(),
                "Confirm your registration",
                "To confirm your registration, click the link: " + confirmationLink);
    }

    @Override
    public void registerSellerByAnonUser(AddCommentWithRegRequestDTO addCommentWithRegRequestDTO) {
        User seller = new User();
        seller.setNickname(addCommentWithRegRequestDTO.sellerNickname());
        seller.setFirstName(addCommentWithRegRequestDTO.sellerFirstName());
        seller.setLastName(addCommentWithRegRequestDTO.sellerLastName());
        seller.setRole(Role.valueOf("SELLER"));
        seller.setVerified(false);
        seller.setEnabled(false);
        seller = userRepository.save(seller);
        for (String gameName : addCommentWithRegRequestDTO.sellerGames()) {
            sellerOfferService.saveSellerOffer(seller, new RegisterSellerOfferDTO(gameName, null));
        }

        commentService.saveCommentByAnonUser(seller,
                addCommentWithRegRequestDTO.commentMessage(),
                addCommentWithRegRequestDTO.commentRating()
        );
    }

    @Override
    public void leaveCommentToSellerByAnonUser(Integer id, AddCommentRequestDTO addCommentRequestDTO) {
        User seller = userRepository.findByIdAndRole(id, Role.SELLER)
                .orElseThrow(() -> new UserNotFoundException(id));
        commentService.saveCommentByAnonUser(
                seller,
                addCommentRequestDTO.commentMessage(),
                addCommentRequestDTO.commentRating()
        );
    }

    @Override
    public void registerAdmin(RegisterAdminRequestDTO registerAdminRequestDTO){
        if (userRepository.findByEmail(registerAdminRequestDTO.email()).isPresent()){
            throw new EmailAlreadyTakenException(registerAdminRequestDTO.email());
        }
        if (userRepository.findByNickname(registerAdminRequestDTO.nickname()).isPresent()){
            throw new NicknameAlreadyTakenException(registerAdminRequestDTO.nickname());
        }

        User admin = new User();
        admin.setNickname(registerAdminRequestDTO.nickname());
        admin.setFirstName(registerAdminRequestDTO.firstName());
        admin.setLastName(registerAdminRequestDTO.lastName());
        admin.setEmail(registerAdminRequestDTO.email());
        admin.setPassword(passwordEncoder.encode(registerAdminRequestDTO.password()));
        admin.setRole(Role.valueOf("ADMIN"));
        admin.setVerified(false);
        admin.setEnabled(false);
        userRepository.save(admin);

        String tokenData = "{\"user_id\": " + admin.getId() + ", \"time\": " + Instant.now().getEpochSecond() + "}";
        String encryptedToken = aesEncryptionUtil.encrypt(tokenData);
        String confirmationLink = "http://localhost:8080/confirm_registration/" + encryptedToken;
        emailService.sendEmail(
                admin.getEmail(),
                "Confirm your registration",
                "To confirm your registration, click the link: " + confirmationLink);
    }

    @Override
    public boolean checkRegistrationTokenDate(String token){
        String decryptedToken = aesEncryptionUtil.decrypt(token);
        long tokenTime = Long.parseLong(decryptedToken.split("\"time\":")[1].split("}")[0].trim());

        return Instant.now().getEpochSecond() - tokenTime <= 86400;
    }

    @Override
    public void confirmRegistration(String token) {
        String decryptedToken = aesEncryptionUtil.decrypt(token);
        Integer userId = Integer.parseInt(decryptedToken.split("\"user_id\":")[1].split(",")[0].trim());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TokenNotValidException(token));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void sendRestorationCodeToEmail(String email) {
        if (userRepository.findByEmail(email).isEmpty()){
            throw new UserNotFoundException(email);
        }

        String code = "{\"email\": " + email + ", \"time\": " + Instant.now().getEpochSecond() + "}";
        String encryptedCode = aesEncryptionUtil.encrypt(code);
        emailService.sendEmail(
                email,
                "Code for password restoration",
                "To restore your password, use the code " +
                        encryptedCode + " on the http://localhost:8080/help/reset");
    }

    @Override
    public boolean checkRestorationCode(String code, String email) {
        String decryptedToken = aesEncryptionUtil.decrypt(code);
        long tokenTime = Long.parseLong(decryptedToken.split("\"time\":")[1].split("}")[0].trim());
        String emailFromCode = decryptedToken.split("\"email\":")[1].split(",")[0].trim();

        return Instant.now().getEpochSecond() - tokenTime <= 86400 && email.equals(emailFromCode);
    }

    @Override
    public void resetUserPassword(String email, String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<SellerDTO> findAllUsersByIsVerified(Role role, boolean isVerified) {
        List<SellerDTO> usersDTO = new ArrayList<>();
        for (User user : userRepository.findAllByRoleAndIsVerified(role, isVerified)) {

            List<SellerOfferDTO> sellerOfferDTOs = null;
            List<CommentDTO> commentDTOs = null;
            if (isVerified) {
                sellerOfferDTOs = sellerOfferService.findAllVerifiedSellerOffers(user);
                commentDTOs = commentService.findAllVerifiedCommentsBySeller(user);
            } else {
                sellerOfferDTOs = sellerOfferService.findAllSellerOffersDTO(user);
                commentDTOs = commentService.findAllCommentsBySellerId(user.getId());
            }
            usersDTO.add(new SellerDTO(
                    user.getId(),
                    user.getNickname(),
                    user.getFirstName(),
                    user.getLastName(),
                    sellerOfferDTOs,
                    commentDTOs
            ));
        }
        return usersDTO;
    }

    @Override
    public ReviewStatus reviewUser(Integer id, boolean decision){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.isVerified()){
           return ReviewStatus.ALREADY_VERIFIED;
        }
        if (decision) {
            user.setVerified(true);
            userRepository.save(user);
        } else {
            userRepository.delete(user);
        }
        return ReviewStatus.SUCCESS;
    }

    @Override
    public SellerDTO findVerifiedSellerById(Integer id) {
        return userRepository.findAllByIdAndIsVerified(id, true)
                .map(seller -> new SellerDTO(
                        seller.getId(),
                        seller.getNickname(),
                        seller.getFirstName(),
                        seller.getLastName(),
                        sellerOfferService.findAllVerifiedSellerOffers(seller),
                        commentService.findAllVerifiedCommentsBySeller(seller)
                )).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<ShowTopSellersResponseDTO> sortSellersByRating() {
        List<ShowTopSellersResponseDTO> sellersByRating = new ArrayList<>();
        List<User> sellers = userRepository.findAllByRole(Role.SELLER);
        Float avgRating;
        SellerDTO sellerDTO;
        for (User seller : sellers){
            sellerDTO = new SellerDTO(seller.getId(),
                    seller.getNickname(),
                    seller.getFirstName(),
                    seller.getLastName(),
                    sellerOfferService.findAllVerifiedSellerOffers(seller),
                    commentService.findAllVerifiedCommentsBySeller(seller));

            avgRating = commentService.findAverageRatingBySellerId(seller.getId());
            if (avgRating == null) {
                avgRating = 0F;
            }
            avgRating = Math.round(avgRating * 10) / 10.0f;
            sellersByRating.add(new ShowTopSellersResponseDTO(sellerDTO, avgRating));
        }

        sellersByRating.sort(Comparator.comparing(ShowTopSellersResponseDTO::avgRating).reversed());
        return sellersByRating;
    }

    @Override
    public List<ShowTopSellersResponseDTO> sortSellersByRatingInRange(Float min, Float max) {
        return sortSellersByRating()
                .stream()
                .filter(seller -> (seller.avgRating() >= min && seller.avgRating() <= max))
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerDTO> findSellerByGameId(Integer id) {
        return userRepository.findAllByRole(Role.SELLER)
                .stream()
                .filter(seller -> sellerOfferService.isVerifiedSellerOfferExistByGameId(seller, id))
                .map(seller -> new SellerDTO(
                        seller.getId(),
                        seller.getNickname(),
                        seller.getFirstName(),
                        seller.getLastName(),
                        sellerOfferService.findAllVerifiedSellerOffers(seller),
                        commentService.findAllVerifiedCommentsBySeller(seller)))
                .collect(Collectors.toList());
    }

}
