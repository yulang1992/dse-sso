package com.dse.sso.conf;


import com.dse.sso.util.JedisUtil;
import com.dse.sso.util.SsoLoginStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * sso redis inti
 * add 2018-12-20
 */
@Configuration
@Slf4j
public class DseSsoConfig implements InitializingBean, DisposableBean {

    @Value("${dse.sso.redis.address}")
    private String redisAddress;

    @Value("${dse.sso.redis.expireMinite}")
    private int redisExpireMinite;

    @Value("${dse.sso.redis.password}")
    private String  password;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[redisAddress]:" +redisAddress+"->[redisExpireMinite]:"+redisExpireMinite+"->[password]:"+password);
        SsoLoginStore.setRedisExpireMinite(redisExpireMinite);
        JedisUtil.init(redisAddress,password);
    }

    @Override
    public void destroy() throws Exception {
        JedisUtil.close();
    }

}
