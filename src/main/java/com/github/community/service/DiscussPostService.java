package com.github.community.service;

import com.duby.util.TrieFilter.TrieFilter;
import com.github.community.dao.DiscussPostDao;
import com.github.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private TrieFilter trieFilter;

    public List<DiscussPost> getDiscussPosts(Integer userId, Integer offset, Integer limit) {
        return discussPostDao.findDiscussPosts(userId, offset, limit);
    }

    public int getDiscussPostCount(Integer userId) {
        return discussPostDao.findDiscussPostCount(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能唯空");
        }
        // 转义HTML标记,去标签化
        // 例如 <script>
        // 变为 &lt;script&gt;
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤敏感词
        discussPost.setTitle(trieFilter.filter(discussPost.getTitle(), '*'));
        discussPost.setContent(trieFilter.filter(discussPost.getContent(), '*'));
        return discussPostDao.insertDiscussPost(discussPost);
    }

    public DiscussPost getDiscussPostById(Integer id) {
        return discussPostDao.findDiscussPostById(id);
    }

    public int updateDiscussPostCommentCount(Integer id, int commentCount) {
        return discussPostDao.updateDiscussPostCommentCount(id, commentCount);
    }
}
