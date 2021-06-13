package com.github.community.dao;

import com.github.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;

import java.util.List;

@Mapper
public interface DiscussPostDao {
    List<DiscussPost> findDiscussPosts(@Nullable Integer userId, Integer offset, Integer limit);

    // @Param 注解用于给参数取别名
    // 如果只有一个参数，并且在动态sql中(<if>)使用，则必须取一个别名
    int findDiscussPostCount(@Nullable @Param("userId") Integer userId);

    // 发布帖子
    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(Integer id);

    int updateDiscussPostCommentCount(Integer id,int commentCount);
}
