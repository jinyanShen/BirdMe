package com.java.birdme.service;

import com.java.birdme.bean.ForumPost;
import com.java.birdme.bean.ForumReply;

import java.util.List;

public interface ForumService {
    List<ForumPost> getPostsByCategory(String category);
    List<ForumPost> getPinnedPosts();
    ForumPost getPostById(Integer id);
    void incrementViewCount(Integer id);
    ForumPost createPost(ForumPost post);
    ForumPost updatePost(ForumPost post);
    List<ForumReply> getRepliesByPostId(Integer postId);
    ForumReply createReply(ForumReply reply);
    void updateReplyCount(Integer postId, int count);
    List<ForumPost> searchPosts(String keyword, String category);
}
