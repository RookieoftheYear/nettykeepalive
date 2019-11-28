package com.runtrend.realtimecomm.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author GanZY
 * @Title: AdAccountDao
 * @ProjectName httpnetty
 * @Description: TODO
 * @date 2019/10/3115:25
 */
@Repository
@Mapper
public interface AccountMapper {

    @Insert({"INSERT INTO t_ad_account(mobile,ad_account,mac) VALUES(#{mobile},#{ad_account},#{mac})"})
    int insert(@Param("mobile") String mobile, @Param("ad_account") String adAccount, @Param("mac") String mac);

    @Delete("DELETE FROM t_ad_account WHERE ad_account =  #{num}")
    int deleteByAd(String ad);

    @Select({"select mobile from t_ad_account WHERE mac =  #{mac}"})
    List<String> getMobileByMac(String mac);

}
