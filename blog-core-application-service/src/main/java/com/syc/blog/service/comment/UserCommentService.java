package com.syc.blog.service.comment;


import com.syc.blog.entity.comment.UserComment;
import com.syc.blog.mapper.comment.UserCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommentService {

    @Autowired
    UserCommentMapper userCommentMapper;
    public List<UserComment> selectListLatest(int i) {
        return userCommentMapper.selectListLatest(i);
    }
}
