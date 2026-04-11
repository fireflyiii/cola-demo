package com.alibaba.cola.demo.infrastructure.mapper;

import com.alibaba.cola.demo.infrastructure.dataobject.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    @Select("SELECT u.* FROM sys_user u WHERE u.username = #{username} AND u.deleted = 0")
    UserEntity selectByUsername(@Param("username") String username);

    @Select("SELECT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "INNER JOIN sys_user u ON u.id = ur.user_id " +
            "WHERE u.username = #{username} AND u.deleted = 0 AND r.deleted = 0 AND ur.deleted = 0")
    List<String> findRoleCodesByUsername(@Param("username") String username);

    @Select("<script>" +
            "SELECT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_role r ON r.id = rp.role_id " +
            "WHERE r.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "AND p.deleted = 0 AND rp.deleted = 0 AND r.deleted = 0" +
            "</script>")
    List<String> findPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}
