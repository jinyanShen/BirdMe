package com.java.birdme.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.birdme.bean.ForumReply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ForumReplyMapper extends BaseMapper<ForumReply> {
    List<ForumReply> selectByPostId(Integer postId);
}
