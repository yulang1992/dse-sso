package com.dse.sso.server;

import com.dse.sso.core.model.DseUser;

/**
 * FileName: UserService
 * Author:   EdwinYu
 * Date:     2018-12-19 13:42
 * Description:
 * Version:1.0.0
 */
public interface UserService {

    public DseUser findByLoginUser(String loginName,String password);
}
