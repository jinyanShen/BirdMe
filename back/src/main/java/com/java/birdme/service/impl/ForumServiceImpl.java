package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.java.birdme.bean.ForumPost;
import com.java.birdme.bean.ForumReply;
import com.java.birdme.dao.ForumPostMapper;
import com.java.birdme.dao.ForumReplyMapper;
import com.java.birdme.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Override
    public List<ForumPost> getPostsByCategory(String category) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        queryWrapper.orderByDesc("is_pinned", "created_at");
        return forumPostMapper.selectList(queryWrapper);
    }

    @Override
    public List<ForumPost> getPinnedPosts() {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_pinned", 1);
        queryWrapper.orderByDesc("created_at");
        return forumPostMapper.selectList(queryWrapper);
    }

    @Override
    public ForumPost getPostById(Integer id) {
        return forumPostMapper.selectById(id);
    }

    @Override
    public void incrementViewCount(Integer id) {
        UpdateWrapper<ForumPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("view_count = view_count + 1");
        forumPostMapper.update(null, updateWrapper);
    }

    @Override
    public ForumPost createPost(ForumPost post) {
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setViewCount(0);
        post.setReplyCount(0);
        if (post.getIsPinned() == null) {
            post.setIsPinned(0);
        }
        forumPostMapper.insert(post);
        return post;
    }

    @Override
    public ForumPost updatePost(ForumPost post) {
        post.setUpdatedAt(new Date());
        ForumPost existingPost = forumPostMapper.selectById(post.getId());
        if (existingPost != null) {
            post.setViewCount(existingPost.getViewCount());
            post.setReplyCount(existingPost.getReplyCount());
        }
        forumPostMapper.updateById(post);
        return post;
    }

    @Override
    public List<ForumReply> getRepliesByPostId(Integer postId) {
        QueryWrapper<ForumReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.orderByAsc("created_at");
        return forumReplyMapper.selectList(queryWrapper);
    }

    @Override
    public ForumReply createReply(ForumReply reply) {
        reply.setCreatedAt(new Date());
        forumReplyMapper.insert(reply);

        ForumPost post = forumPostMapper.selectById(reply.getPostId());
        if (post != null) {
            post.setReplyCount(post.getReplyCount() + 1);
            post.setUpdatedAt(new Date());
            forumPostMapper.updateById(post);
        }

        return reply;
    }

    @Override
    public void updateReplyCount(Integer postId, int count) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post != null) {
            post.setReplyCount(count);
            post.setUpdatedAt(new Date());
            forumPostMapper.updateById(post);
        }
    }

    @Override
    public List<ForumPost> searchPosts(String keyword, String category) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();

        queryWrapper.and(wrapper ->
            wrapper.like("title", keyword)
                   .or()
                   .like("content", keyword)
        );

        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            queryWrapper.eq("category", category);
        }

        queryWrapper.orderByDesc("is_pinned", "created_at");
        return forumPostMapper.selectList(queryWrapper);
    }
}
