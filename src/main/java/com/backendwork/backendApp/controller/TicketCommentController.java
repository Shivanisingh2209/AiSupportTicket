package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.CreateCommentRequest;
import com.backendwork.backendApp.entity.TicketComment;
import com.backendwork.backendApp.services.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets/{ticketId}/comments")
public class TicketCommentController {

    @Autowired
    private TicketCommentService ticketCommentService;

    @PostMapping
    public ResponseEntity<TicketComment> addComment(
            @PathVariable String ticketId,
            @Valid @RequestBody CreateCommentRequest request
    ) {

        TicketComment comment =
                ticketCommentService.addComment(ticketId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(comment);
    }

    @GetMapping
    public ResponseEntity<List<TicketComment>> getComments(
            @PathVariable String ticketId
    ) {

        return ResponseEntity.ok(
                ticketCommentService.getCommentsByTicketId(ticketId)
        );
    }
}
