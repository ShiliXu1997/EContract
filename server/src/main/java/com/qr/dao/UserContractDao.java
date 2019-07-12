package com.qr.dao;

import com.qr.entity.UserContract;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserContractDao {

    @Insert("insert into user_contract values(#{userId1},#{userId2},#{agreementId})")
    public int insert(long userId1,long userId2,String agreementId);

    @Select("select contractid from user_contract where userid1=#{userId1}")
    public List<String> getConstractIdByUserId1(long userId1);

    @Select("select contractid from user_contract where userid2=#{userId2}")
    public List<String> getContractIdByUserId2(long userId2);

    @Select("select * from user_contract where contractid=#{id}")
    @Results({
            @Result(property = "userId1",column = "userid1"),
            @Result(property = "userId2",column = "userid2"),
            @Result(property = "contractId",column = "contractid"),
    })
    public UserContract getUserContractById(String id);

    @Delete("delete from user_contract where contractid=#{contractId}")
    public int deleteContractByContractId(String contractId);
}
