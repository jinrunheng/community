package com.github.community.service;

import com.github.community.dao.CommentDao;
import com.github.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    public List<Comment> getCommentsByEntityTypeAndId(Integer entityType, Integer entityId, int offset, int limit) {
        return commentDao.findCommentsByEntityTypeAndId(entityType, entityId, offset, limit);
    }

    public int getCommentsCountByEntityTypeAndId(Integer entityType, Integer entityId) {
        return commentDao.findCommentsCountByEntityTypeAndId(entityType, entityId);
    }
}
