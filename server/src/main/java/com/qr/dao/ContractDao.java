package com.qr.dao;

import com.qr.entity.Contract;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ContractDao {

    @Insert("insert into contract values(#{id},#{status},#{title},#{path},#{fileSize},#{sign1},#{sign2},#{agree},#{lastModified})")
    public int insert(String id, int status, String title, String path,long fileSize, int sign1, int sign2,int agree,long lastModified);

    @Select("select * from contract where id=#{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "type",column = "status"),
            @Result(property = "title",column = "title"),
            @Result(property = "path",column = "path"),
            @Result(property = "fileSize",column = "filesize"),
            @Result(property = "sign1",column = "sign1"),
            @Result(property = "sign2",column = "sign2"),
            @Result(property = "agree",column = "agree"),
            @Result(property = "lastModified",column = "lastmodified")
    })
    public Contract getContractByContractId(String id);

    @Update("update contract set status=#{status} where id=#{id}")
    public int updateStatusById(String id,int status);

    @Update("update contract set sign1=1 where id=#{id}")
    public int updateSign1ById(String id);

    @Update("update contract set sign2=1 where id=#{id}")
    public int updateSign2ById(String id);

    @Select("select path from contract where id=#{id}")
    public String getFilePathById(String id);

    @Update("update contract set agree=#{agree} where id=#{id}")
    public int updateAgree(String id,int agree);

    @Delete("delete from contract where id=#{id}")
    public int delectContractById(String id);

    @Update("update contract set lastmodified=#{timeStamp} where id=#{id}")
    public int updateTimeStampById(String id,long timeStamp);

    @Update("update contract set status=#{status} where id=#{id}")
    public int updateStatus(String id,int status);
}
