package com.zhang.huinongplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.huinongplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可以添加自定义的查询方法
}