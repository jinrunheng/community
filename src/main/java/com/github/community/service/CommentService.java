package com.github.community.service;

import com.duby.util.TrieFilter.TrieFilter;
import com.github.community.dao.CommentDao;
import com.github.community.dao.DiscussPostDao;
import com.github.community.entity.Comment;
import com.github.community.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService implements Constant {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private TrieFilter trieFilter;

    public Comment getCommentById(int id) {
        return commentDao.findCommentById(id);
    }

    public List<Comment> getCommentsByEntityTypeAndId(Integer entityType, Integer entityId, int offset, int limit) {
        return commentDao.findCommentsByEntityTypeAndId(entityType, entityId, offset, limit);
    }

    public List<Comment> getCommentsByUserId(Integer userId, int offset, int limit) {
        return commentDao.findCommentsByUserId(userId, offset, limit);
    }

    public int getCommentsCountByUserId(Integer userId) {
        return commentDao.findCommentsCountByUserId(userId);
    }

    public int getCommentsCountByEntityTypeAndId(Integer entityType, Integer entityId) {
        return commentDao.findCommentsCountByEntityTypeAndId(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (Objects.isNull(comment)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(trieFilter.filter(comment.getContent(), '*'));
        int row = commentDao.insertComment(comment);
        // 更新帖子的评论的数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentDao.findCommentsCountByEntityTypeAndId(ENTITY_TYPE_POST, comment.getEntityId());
            discussPostService.updateDiscussPostCommentCount(comment.getEntityId(), count + 1);
        }
        return row;
    }
}
