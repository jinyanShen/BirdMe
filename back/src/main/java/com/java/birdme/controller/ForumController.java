package com.java.birdme.controller;

import com.java.birdme.bean.ForumPost;
import com.java.birdme.bean.ForumReply;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum")
@CrossOrigin
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/posts")
    public ReturnResp getPosts(@RequestParam(required = false) String category,
                               @RequestParam(required = false) String keyword) {
        try {
            List<ForumPost> posts;
            if (keyword != null && !keyword.trim().isEmpty()) {
                // If search keyword exists, use search function
                posts = forumService.searchPosts(keyword, category);
            } else {
                // Otherwise get by category
                posts = forumService.getPostsByCategory(category);
            }
            return ReturnResp.success(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get posts: " + e.getMessage());
        }
    }

    @GetMapping("/post/{id}")
    public ReturnResp getPost(@PathVariable Integer id) {
        try {
            forumService.incrementViewCount(id);
            ForumPost post = forumService.getPostById(id);
            return ReturnResp.success(post);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get post: " + e.getMessage());
        }
    }

    @GetMapping("/replies")
    public ReturnResp getReplies(@RequestParam Integer postId) {
        try {
            List<ForumReply> replies = forumService.getRepliesByPostId(postId);
            return ReturnResp.success(replies);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get replies: " + e.getMessage());
        }
    }

    @PostMapping("/post")
    public ReturnResp createPost(@RequestBody ForumPost post) {
        try {
            ForumPost createdPost = forumService.createPost(post);
            return ReturnResp.success(createdPost);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to create post: " + e.getMessage());
        }
    }

    @PutMapping("/post/{id}")
    public ReturnResp updatePost(@PathVariable Integer id, @RequestBody ForumPost post) {
        try {
            post.setId(id);
            ForumPost updatedPost = forumService.updatePost(post);
            return ReturnResp.success(updatedPost);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to update post: " + e.getMessage());
        }
    }

    @PostMapping("/reply")
    public ReturnResp createReply(@RequestBody ForumReply reply) {
        try {
            ForumReply createdReply = forumService.createReply(reply);
            return ReturnResp.success(createdReply);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to create reply: " + e.getMessage());
        }
    }
}
