package com.ratingsystem.exception;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(Integer commentId) {
        super("Comment was not found with id " + commentId);
    }
    public CommentNotFoundException(Integer sellerId, Integer commentId) {
        super("Comment with id " + commentId + " was not found for seller id " + sellerId);
    }
}
