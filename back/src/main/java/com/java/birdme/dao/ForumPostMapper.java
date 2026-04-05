package com.java.birdme.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.birdme.bean.ForumPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {
    List<ForumPost> selectByCategory(String category);
    List<ForumPost> selectPinned();
    void incrementViewCount(Integer id);
}
