package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.response.SellerOfferDTO;
import com.ratingsystem.entity.Game;
import com.ratingsystem.entity.SellerOffer;
import com.ratingsystem.entity.User;
import com.ratingsystem.repository.SellerOfferRepository;
import com.ratingsystem.service.GameService;
import com.ratingsystem.service.SellerOfferService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerOfferServiceJPAImpl implements SellerOfferService {
    private final SellerOfferRepository sellerOfferRepository;
    private final GameService gameService;

    public SellerOfferServiceJPAImpl(SellerOfferRepository sellerOfferRepository, GameService gameService) {
        this.sellerOfferRepository = sellerOfferRepository;
        this.gameService = gameService;
    }

    @Override
    public void saveSellerOffer(User seller, RegisterSellerOfferDTO registerSellerOfferDTO) {
        Game gameFromDB = gameService.findGameByNameNormalized(registerSellerOfferDTO.name());
        if (gameFromDB == null){
            gameFromDB = gameService.saveGame(registerSellerOfferDTO);
        }
        sellerOfferRepository.save(new SellerOffer(seller, gameFromDB, registerSellerOfferDTO.description()));
    }

    @Override
    public List<SellerOfferDTO> findAllSellerOffersDTO(User seller) {
        return sellerOfferRepository.findAllBySeller(seller)
                .stream()
                .map(sellerOffer -> new SellerOfferDTO(
                        sellerOffer.getGame().getId(),
                        sellerOffer.getGame().getName(),
                        sellerOffer.getDescription()
                )).collect(Collectors.toList());
    }

    @Override
    public List<SellerOfferDTO> findAllVerifiedSellerOffers(User seller) {
        return sellerOfferRepository.findAllBySellerAndGameIsVerified(seller, true)
                .stream()
                .map(sellerOffer -> new SellerOfferDTO(
                        sellerOffer.getGame().getId(),
                        sellerOffer.getGame().getName(),
                        sellerOffer.getDescription()
                )).collect(Collectors.toList());
    }

    @Override
    public boolean isVerifiedSellerOfferExistByGameId(User seller, Integer id){
        List<SellerOffer> sellerOffers = sellerOfferRepository.findAllBySellerAndGameIsVerified(seller, true);
        for (SellerOffer sellerOffer : sellerOffers){
            if(sellerOffer.getGame().getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteBySellerAndGameId(User seller, Integer gameId){
        sellerOfferRepository.deleteBySellerAndGameId(seller, gameId);
    }

    @Override
    public void updateOfferDescription(User seller, Integer gameId, String description){
        Game game = new Game();
        game.setId(gameId);
        game.setName(gameService.findGameById(gameId).name());
        sellerOfferRepository.save(new SellerOffer(seller, game, description));
    }

}