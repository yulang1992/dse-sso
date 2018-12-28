package com.dse.sso.core;


import com.dse.sso.core.model.DseUser;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息存储类
 */
@Slf4j
public class SsoLoginStore {

    private static  int REDISEXPIREMINITE = 1440;    // 1440 minite, 24 hour
    public static void setRedisExpireMinite(int redisExpireMinite) {
        if (redisExpireMinite < 30) {
            redisExpireMinite = 30;
        }
        SsoLoginStore.REDISEXPIREMINITE = redisExpireMinite;
    }
    public static int getRedisExpireMinite() {
        return REDISEXPIREMINITE;
    }

    /**
     * get
     *
     * @param tokenId
     * @return
     */
    public static DseUser get(String tokenId,Class classz) {

        String redisKey = redisKey(tokenId);
        Object objectValue = JedisUtil.getObjectValue(redisKey,classz);
        if (objectValue != null) {
            DseUser dseUser=null;
            try{
                dseUser = (DseUser) objectValue;
            }catch(Exception e){
                log.error("获取用户信息失败："+e.getMessage(),e);
                return dseUser;
            }

            return dseUser;
        }
        return null;
    }

    /**
     * remove
     *
     * @param token
     */
    public static void remove(String token) {
        String redisKey = redisKey(token);
        JedisUtil.del(redisKey);
    }

    /**
     * put
     *
     * @param storeKey
     * @param dseUser
     */
    public static void put(String storeKey, DseUser dseUser) {
        String redisKey = redisKey(storeKey);
        JedisUtil.setObjectValue(redisKey, dseUser, REDISEXPIREMINITE * 60);  // minite to second
    }

    private static String redisKey(String sessionId){
        return Constants.SSO_SESSIONID.concat("#").concat(sessionId);
    }

}
