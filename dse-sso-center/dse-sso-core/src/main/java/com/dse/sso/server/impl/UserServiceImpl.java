package com.dse.sso.server.impl;

import com.dse.sso.core.model.DseUser;
import com.dse.sso.mapper.UserMapper;
import com.dse.sso.server.UserService;
import com.dse.sso.util.DecriptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * FileName: UserServiceImpl
 * Author:   EdwinYu
 * Date:     2018-12-19 15:02
 * Description:
 * Version:1.0.0
 */
@Service
public class UserServiceImpl  implements UserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录信息验证
     * @param loginName
     * @param password
     * @return
     */
    @Override
    public DseUser findByLoginUser(String loginName, String password) {
        Assert.isTrue(StringUtils.isNotBlank(loginName),"登录名不能为空！");
        Assert.isTrue(StringUtils.isNotBlank(password),"登录密码不能为空！");
        DseUser dseUser = userMapper.findByLoginUser(loginName);
        Assert.isTrue(null !=dseUser,"用户不存在，请联系管理员！");
        if(!StringUtils.equalsIgnoreCase(dseUser.getPassword(), DecriptUtil.MD5(password))){
          throw new RuntimeException("登录密码不正确，请重新输入！");
        }
        dseUser.setPassword("");
        return dseUser;
    }
}
