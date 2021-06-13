package com.github.community.dao;

import com.github.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {

    List<Comment> findCommentsByEntityTypeAndId(Integer entityType, Integer entityId, int offset, int limit);

    int findCommentsCountByEntityTypeAndId(Integer entityType, Integer entityId);
}
