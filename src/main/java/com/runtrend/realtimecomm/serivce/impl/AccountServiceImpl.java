package com.runtrend.realtimecomm.serivce.impl;

import com.runtrend.realtimecomm.mapper.AccountMapper;
import com.runtrend.realtimecomm.netty.utils.ConstantUtiles;
import com.runtrend.realtimecomm.netty.utils.FullHttpUtiles;
import com.runtrend.realtimecomm.serivce.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * @author GanZY
 * @Title: AccountServiceImpl
 * @ProjectName realtimecomm
 * @Description: TODO
 * @date 2019/11/2520:24
 */
@Service
@Slf4j

public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Override
    public void sendMessage(String mac) {

        accountMapper.getMobileByMac(mac).forEach(mobileByMac ->{
            String msg = MessageFormat.format(ConstantUtiles.REGISTER_MSG, mobileByMac);
            try {
                msg = URLEncoder.encode(msg, "UTF-8");
            } catch (UnsupportedEncodingException e) {

                log.debug("Encode Message to User Fail : {}",e.getMessage());
            }
            String url = MessageFormat.format(ConstantUtiles.POST_URL, mobileByMac, msg);
            String rep = null;
            try {
                rep = FullHttpUtiles.sendPost(url);
            } catch (IOException e) {
                log.debug("Send Message to User error: {}",e.getMessage());
            }
            log.debug("=== Send Message to User {} , status : {}",mac,rep);
        });
    }
}
