package com.ratingsystem.service;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.response.GameDTO;
import com.ratingsystem.entity.Game;

import java.util.List;

public interface GameService {
    Game saveGame(RegisterSellerOfferDTO registerSellerOfferDTO);
    List<GameDTO> findAllGames();
    List<GameDTO> findAllGamesByIsVerified(boolean isVerified);
    GameDTO findGameById(Integer id);
    Game findGameByNameNormalized(String name);
    ReviewStatus reviewGame(Integer id, boolean decision);
}


