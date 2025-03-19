package com.ratingsystem.service.impl;

import com.ratingsystem.dto.response.CommentDTO;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.exception.CommentNotFoundException;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.service.ReviewStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceJPAImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceJPAImpl commentService;

    @Test
    public void shouldSaveCommentByAnonUser() {
        User seller = new User();
        commentService.saveCommentByAnonUser(seller, "Great product!", 5);

        verify(commentRepository, times(1)).save(argThat(comment ->
                "Great product!".equals(comment.getMessage())
                        && seller.equals(comment.getSeller())
                        && comment.getRating() == 5
        ));
    }

    @Test
    public void shouldFindAllVerifiedCommentsBySeller() {
        User seller = new User();
        Comment comment = new Comment(2, "Excellent", 5, seller, true, new Date());
        when(commentRepository.findAllBySellerAndIsVerified(seller, true)).thenReturn(Arrays.asList(comment));
        List<CommentDTO> result = commentService.findAllVerifiedCommentsBySeller(seller);

        assertEquals(1, result.size());
    }

    @Test
    public void shouldFindAllCommentsBySellerId() {
        User seller = new User();
        Comment comment1 = new Comment(1, "Good", 4, seller, false, new Date());
        Comment comment2 = new Comment(2, "Excellent", 5, seller, true, new Date());
        when(commentRepository.findAllBySellerId(seller.getId())).thenReturn(Arrays.asList(comment1, comment2));
        List<CommentDTO> result = commentService.findAllCommentsBySellerId(seller.getId());

        assertEquals(2, result.size());
    }

    @Test
    public void shouldFindAllCommentsByIsVerified() {
        Comment comment = new Comment(2, "Excellent", 5, new User(), true, new Date());
        when(commentRepository.findAllByIsVerified(true)).thenReturn(Arrays.asList(comment));
        List<CommentDTO> result = commentService.findAllCommentsByIsVerified(true);

        assertEquals(1, result.size());
    }

    @Test
    public void shouldReviewCommentAndApprove() {
        Comment comment = new Comment(1, "Good", 4, new User(), false, new Date());
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        ReviewStatus result = commentService.reviewComment(1, true);

        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).save(comment);
        verify(commentRepository, times(0)).delete(comment);
        assertTrue(comment.isVerified());
        assertEquals(ReviewStatus.SUCCESS, result);
    }

    @Test
    public void shouldReviewCommentAndReject() {
        Comment comment = new Comment(1, "Good", 4, new User(), false, new Date());
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        ReviewStatus result = commentService.reviewComment(1, false);

        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).delete(comment);
        verify(commentRepository, times(0)).save(comment);
        assertEquals(ReviewStatus.SUCCESS, result);
    }

    @Test
    public void shouldNotReviewVerifiedComment(){
        Comment comment = new Comment(1, "Good", 4, new User(), true, new Date());
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        ReviewStatus resultTrue = commentService.reviewComment(1, true);
        ReviewStatus resultFalse = commentService.reviewComment(1, false);

        verify(commentRepository, times(2)).findById(1);
        verify(commentRepository, times(0)).save(comment);
        verify(commentRepository, times(0)).delete(comment);
        assertEquals(ReviewStatus.ALREADY_VERIFIED, resultTrue);
        assertEquals(ReviewStatus.ALREADY_VERIFIED, resultFalse);
    }

    @Test
    public void shouldNotReviewCommentWithWrongId(){
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.reviewComment(1, true));
        assertThrows(CommentNotFoundException.class, () -> commentService.reviewComment(1, false));
    }

    @Test
    public void shouldFindAverageRatingBySellerId() {
        when(commentRepository.findAverageRatingBySellerId(1)).thenReturn(4.5f);
        Float result = commentService.findAverageRatingBySellerId(1);
        assertEquals(4.5f, result);
    }
}
