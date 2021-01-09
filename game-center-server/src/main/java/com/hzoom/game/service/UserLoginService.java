package com.hzoom.game.service;

import com.hzoom.game.error.IError;
import com.hzoom.game.dao.UserAccountDao;
import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.http.request.LoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserLoginService {
    @Autowired
    private UserAccountDao userAccountDao;


    public IError verfiySdkToken(String openId, String token) {
        // 这里调用sdk服务端验证接口
        return null;
    }

    public UserAccount login(LoginParam loginParam){
        String openId = loginParam.getOpenId().intern();//放openId放入到常量池
        synchronized (openId) {// 对openId加锁，防止用户点击多次注册多次
            Optional<UserAccount> op = userAccountDao.findById(openId);
            UserAccount userAccount = null;
            if (!op.isPresent()){
                // 用户不存在，自动注册
                userAccount = this.register(loginParam);
            }else {
                userAccount = op.get();
            }
            return userAccount;
        }
    }

    private UserAccount register(LoginParam loginParam) {
        long nextUserId = userAccountDao.getNextUserId();// 使用redis自增保证userId全局唯一
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(nextUserId);
        userAccount.setOpenId(loginParam.getOpenId());
        userAccount.setCreateTime(new Date());
        userAccount.setLastLoginIp(loginParam.getIp());
        userAccount.setRegisterIp(loginParam.getIp());
        userAccountDao.saveOrUpdate(userAccount,loginParam.getOpenId());
        log.debug("user {} 注册成功", userAccount);
        return userAccount;
    }

    public Optional<UserAccount> getUserAccountByOpenId(String openId) {
        return this.userAccountDao.findById(openId);
    }

    public void updateUserAccount(UserAccount userAccount) {
        this.userAccountDao.saveOrUpdate(userAccount, userAccount.getOpenId());
    }
}
