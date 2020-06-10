package com.syc.blog.mapper.comment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syc.blog.entity.comment.UserComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCommentMapper extends BaseMapper<UserComment> {
}
