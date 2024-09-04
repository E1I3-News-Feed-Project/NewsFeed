package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;

public interface CommentLikesService {
    CommentLikesResponse likeComment(Long commentId, String email);
    CommentLikesResponse unlikeComment(Long commentId, String email);
}
