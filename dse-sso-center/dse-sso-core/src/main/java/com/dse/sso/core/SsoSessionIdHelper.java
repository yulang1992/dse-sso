package com.dse.sso.core;

import com.dse.sso.core.model.DseUser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FileName: SsoSessionIdHelper
 * Author:   EdwinYu
 * Date:     2018-12-19 14:29
 * Description: token id生成帮助类
 * Version:1.0.0
 */
public class SsoSessionIdHelper {


    /**
     * 通过request获取token信息
     * @param request
     * @return
     */
    public static String getRequestToken(HttpServletRequest request){
        String token = request.getHeader(Constants.SSO_SESSIONID);
        if(StringUtils.isNotBlank(token)){
           return null;
        }
        return token;
    }


    /**
     * make client sessionId
     * 直接用用户id作为可以 ，因为id是UUID生成的
     * @param dseUser
     * @return
     */
    public static String makeTokenId(DseUser dseUser){
        if(dseUser !=null){
           return dseUser.getId();
        }
        return null;
    }

    /**
     * 移除token信息
     * @param req
     * @param res
     */
    public static HttpServletResponse logout(HttpServletRequest req, HttpServletResponse res) {
        res.setHeader(Constants.SSO_SESSIONID,"");
        return res;
    }
}
