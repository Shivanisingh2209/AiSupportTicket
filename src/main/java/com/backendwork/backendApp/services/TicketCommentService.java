package com.backendwork.backendApp.services;

import com.backendwork.backendApp.dto.CreateCommentRequest;
import com.backendwork.backendApp.entity.TicketComment;
import com.backendwork.backendApp.repository.TicketCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TicketCommentService {

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    public TicketComment addComment(
            String ticketId,
            CreateCommentRequest request
    ) {

        TicketComment comment = new TicketComment();

        comment.setTicketId(ticketId);
        comment.setUserId(request.getUserId());
        comment.setUserName(request.getUserName());
        comment.setMessage(request.getMessage());
        comment.setCreatedAt(new Date().toString());

        return ticketCommentRepository.save(comment);
    }

    public List<TicketComment> getCommentsByTicketId(String ticketId) {
        return ticketCommentRepository.findByTicketId(ticketId);
    }

}
