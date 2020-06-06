package com.syc.blog.mapper.info;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syc.blog.entity.info.Banner;
import com.syc.blog.entity.info.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
