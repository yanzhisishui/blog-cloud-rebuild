package com.syc.blog.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syc.blog.entity.admin.AdminUser;
import com.syc.blog.entity.config.BaseConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseConfigMapper extends BaseMapper<BaseConfig> {
}
