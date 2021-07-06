package com.github.community.service;

import com.duby.util.TrieFilter.TrieFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.community.dao.DiscussPostDao;
import com.github.community.entity.DiscussPost;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private TrieFilter trieFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数的缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误！");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误！");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        // orderMode = 1 为热门帖子查询
                        log.debug("load post list from DB.");
                        return discussPostDao.findDiscussPosts(null, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.debug("load post rows from DB.");
                        return discussPostDao.findDiscussPostCount(null);
                    }
                });
    }

    public List<DiscussPost> getDiscussPosts(Integer userId, Integer offset, Integer limit, int orderMode) {
        // 热门帖子 orderMode = 1
        // 访问首页热门帖子 用缓存处理
        if (userId == null && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        log.debug("load post from DB.");
        return discussPostDao.findDiscussPosts(userId, offset, limit, orderMode);
    }

    public int getDiscussPostCount(Integer userId) {
        if (userId == null) {
            return postRowsCache.get(0); // 要求缓存必须传入一个 key ，所以 我们只能传入一个 0，并无实际意义
        }
        log.debug("load post rows from DB.");
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

    public int updateDiscussPostType(int id, int type) {
        return discussPostDao.updateDiscussPostType(id, type);
    }

    public int updateDiscussPostStatus(int id, int status) {
        return discussPostDao.updateDiscussPostStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostDao.updateScore(id, score);
    }
}
