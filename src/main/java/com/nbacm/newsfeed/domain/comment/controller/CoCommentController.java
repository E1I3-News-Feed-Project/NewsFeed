package com.nbacm.newsfeed.domain.comment.controller;

import com.nbacm.newsfeed.domain.comment.dto.request.CoCommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CoCommentResponseDto;
import com.nbacm.newsfeed.domain.comment.service.CoCommentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cocomments")
public class CoCommentController {
    private final CoCommentServiceImpl coCommentService;

    @PostMapping("/{commentId}")
    ResponseEntity<CoCommentResponseDto> createCoComment (@PathVariable Long commentId,
                                                          HttpServletRequest request,
                                                          @RequestBody CoCommentRequestDto commentRequestDto){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            CoCommentResponseDto comment = coCommentService.createCoComments(commentId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{commentId}")
    ResponseEntity<CoCommentResponseDto> updateCoComment(@PathVariable Long commentId, HttpServletRequest request,@RequestBody CoCommentRequestDto commentRequestDto){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try {
            CoCommentResponseDto comment = coCommentService.updateCoComments(commentId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{commentId}")
    ResponseEntity<List<CoCommentResponseDto>> getCoComment(@PathVariable Long commentId){
        try{
            List<CoCommentResponseDto> getAllCoComment = coCommentService.getAllCoComment(commentId);
            return ResponseEntity.ok(getAllCoComment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{cocommentId}")
    ResponseEntity<CoCommentResponseDto> deleteCoComment(@PathVariable Long cocommentId, HttpServletRequest request){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            CoCommentResponseDto comment = coCommentService.deleteCoComment(cocommentId,email);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
