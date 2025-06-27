package com.zhang.huinongplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserRoleMapper {
    @Select("SELECT ur.role_code FROM user_role ur  WHERE ur.user_type = #{userType}")
    List<String> getRoleCodesByUserId(Long userType);
} 