package com.dse.sso.core;

import com.dse.sso.core.model.DseUser;
import com.dse.sso.util.SsoLoginStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * sso token
 * add yulang 2018-12-19
 */
//@Configuration
public class SsoTokenLoginHelper {


    private static final Logger log = LoggerFactory.getLogger(SsoTokenLoginHelper.class);

    //@Autowired
   // private StringRedisTemplate redisTemplate;

    public static void login(String token, DseUser dseUser) {
        if (dseUser == null) {
            throw new RuntimeException("parseStoreKey Fail, sessionId:" + dseUser);
        }
        SsoLoginStore.put(StringUtils.isNoneBlank(token) ? token : dseUser.getId(), dseUser);
    }

    /**
     * client logout
     *
     * @param token
     */
    public static void logout(String token) {

        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("parseStoreKey Fail, token must be not null ");
        }
       // redisTemplate.opsForValue().getOperations().delete(token);
        SsoLoginStore.remove(token);
    }

    /**
     * client logout
     * 登录退出
     *
     * @param request
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        logout(SsoSessionIdHelper.getRequestToken(request));
        response.setHeader(Constants.SSO_SESSIONID, "");
    }


    /**
     * login check
     *
     * @param token
     * @return
     */
    public static DseUser loginCheck(String token) {
        log.info("token value is :" + token + " [print time :] " + System.currentTimeMillis());
        if (StringUtils.isBlank(token)) {
           // throw new RuntimeException(" Token is failure");
            return null;
        }
       // DseUser dseUser = JSON.parseObject(redisTemplate.opsForValue().get(token), DseUser.class);
        DseUser dseUser =SsoLoginStore.get(token,DseUser.class);
        log.info("dseuser is :" + dseUser.toString());
        return dseUser;
    }


    /**
     * 从request中获取用户信息
     *
     * @param request
     * @return
     */
    public static DseUser loginCheck(HttpServletRequest request) {
        return loginCheck(SsoSessionIdHelper.getRequestToken(request));
    }


    public static void loginCheck(HttpServletRequest request, HttpServletResponse response) {
        DseUser dseUser = loginCheck(SsoSessionIdHelper.getRequestToken(request));
        response.setHeader(Constants.SSO_SESSIONID, dseUser.getId());
    }

    public static void login(HttpServletResponse response, String tokenId, DseUser dseUser) {
        log.info("token id :" + tokenId + "--> dseuser :" + dseUser.toString());
        login(tokenId, dseUser);
        response.setHeader(Constants.SSO_SESSIONID, StringUtils.isNotBlank(tokenId) ? tokenId : dseUser.getId());
    }
}