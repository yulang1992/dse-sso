package com.dse.sso.mapper;

import com.dse.sso.core.model.DseUser;

/**
 * FileName: UserMapper
 * Author:   EdwinYu
 * Date:     2018-12-19 15:03
 * Description:
 * Version:1.0.0
 */
public interface UserMapper {

    public DseUser findByLoginUser(String loginName);
}
