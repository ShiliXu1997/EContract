package com.qr.dao;

import com.qr.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {

    @Insert("insert into user values(#{id},#{name},#{identity},0)")
    public int insert(long id, String name, String identity);

    @Select("select max(id) from user")
    public long getMax();

    @Select("select * from user where name=#{name}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "identity",column = "identity"),
            @Result(property = "verify",column = "verify")
    })
    public User getUserByName(String name);

    @Select("select * from user where id=#{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "identity",column = "identity"),
            @Result(property = "verify",column = "verify")
    })
    public User getUserById(long id);

    @Select("select * from user where verify=0")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "identity",column = "identity"),
            @Result(property = "verify",column = "verify")
    })
    public List<User> getNotVerifiedUser();

    @Select("select id from user where identity=#{identity}")
    public long getUserIdByIdentity(String identity);

    @Update("update user set verify=#{verifyId} where id=#{id}")
    public int updateVerifyById(long id,int verifyId);
}
