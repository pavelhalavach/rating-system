package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.RegisterSellerOfferDTO;
import com.ratingsystem.dto.response.GameDTO;
import com.ratingsystem.entity.Game;
import com.ratingsystem.exception.GameNotFoundException;
import com.ratingsystem.repository.GameRepository;
import com.ratingsystem.service.ReviewStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceJPAImplTest {
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameServiceJPAImpl gameService;

    @Test
    public void shouldSaveGame(){
        RegisterSellerOfferDTO registerSellerOfferDTO =
                new RegisterSellerOfferDTO("GameName", "Description");
        gameService.saveGame(registerSellerOfferDTO);

        verify(gameRepository, times(1)).save(argThat(game ->
                "GameName".equals(game.getName())
                        && !game.isVerified()
        ));
    }

    @Test
    public void shouldFindAllGames(){
        Game game1 = new Game(1, "game1", false);
        Game game2 = new Game(2, "game2", true);
        when(gameRepository.findAll()).thenReturn(Arrays.asList(game1, game2));
        List<GameDTO> result = gameService.findAllGames();

        assertEquals(2, result.size());
    }

    @Test
    public void shouldFindAllGamesByIsVerified(){
        Game game1 = new Game(1, "game1", false);
        Game game2 = new Game(2, "game2", true);
        when(gameRepository.findAllByIsVerified(true)).thenReturn(Arrays.asList(game2));
        when(gameRepository.findAllByIsVerified(false)).thenReturn(Arrays.asList(game1));

        List<GameDTO> resultWhenTrue = gameService.findAllGamesByIsVerified(true);
        List<GameDTO> resultWhenFalse = gameService.findAllGamesByIsVerified(false);

        assertEquals(1, resultWhenTrue.size());
        assertEquals(1, resultWhenFalse.size());
    }

    @Test
    public void shouldFindGameById(){
        Game game1 = new Game(1, "game1", false);
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        when(gameRepository.findById(2)).thenReturn(Optional.empty());

        GameDTO resultFound = gameService.findGameById(1);
        assertEquals(1, resultFound.id());
        assertEquals("game1", resultFound.name());
        assertThrows(GameNotFoundException.class, () -> gameService.findGameById(2));
    }

    @Test
    public void shouldFindGameByNameNormalized(){
        Game game1 = new Game(1, "game1", false);
        when(gameRepository.findByNameNormalized("game1")).thenReturn(game1);
        when(gameRepository.findByNameNormalized("game2")).thenReturn(null);

        assertEquals(game1, gameService.findGameByNameNormalized("game1"));
        assertNull(gameService.findGameByNameNormalized("game2"));
    }

    @Test
    public void shouldReviewGameAndApprove() {
        Game game1 = new Game(1, "game1", false);
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        ReviewStatus result = gameService.reviewGame(1, true);

        verify(gameRepository, times(1)).findById(1);
        verify(gameRepository, times(1)).save(game1);
        verify(gameRepository, times(0)).delete(game1);
        assertTrue(game1.isVerified());
        assertEquals(ReviewStatus.SUCCESS, result);
    }

    @Test
    public void shouldReviewGameAndReject() {
        Game game1 = new Game(1, "game1", false);
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        ReviewStatus result = gameService.reviewGame(1, false);

        verify(gameRepository, times(1)).findById(1);
        verify(gameRepository, times(1)).delete(game1);
        verify(gameRepository, times(0)).save(game1);
        assertEquals(ReviewStatus.SUCCESS, result);
    }

    @Test
    public void shouldNotReviewVerifiedGame(){
        Game game1 = new Game(1, "game1", true);
        when(gameRepository.findById(1)).thenReturn(Optional.of(game1));
        ReviewStatus resultTrue = gameService.reviewGame(1, true);
        ReviewStatus resultFalse = gameService.reviewGame(1, false);

        verify(gameRepository, times(2)).findById(1);
        verify(gameRepository, times(0)).save(game1);
        verify(gameRepository, times(0)).delete(game1);
        assertEquals(ReviewStatus.ALREADY_VERIFIED, resultTrue);
        assertEquals(ReviewStatus.ALREADY_VERIFIED, resultFalse);
    }

    @Test
    public void shouldNotReviewGameWithWrongId(){
        when(gameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.reviewGame(1, true));
        assertThrows(GameNotFoundException.class, () -> gameService.reviewGame(1, false));
    }

}
