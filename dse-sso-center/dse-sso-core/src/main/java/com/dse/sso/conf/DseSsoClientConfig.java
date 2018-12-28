package com.dse.sso.config;

import com.dse.sso.core.Constants;
import com.dse.sso.core.JedisUtil;
import com.dse.sso.filter.SsoWebClientFilter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  客户端需要配置的，服务端不需要这个配置 这个配置只能客户端配置
 * FileName: DseSsoClientConfig
 * Author:   EdwinYu
 * Date:     2018-12-20 9:58
 * Description: SSO过滤器配置中心
 * Version:1.0.0
 */
/*@Configuration
public class DseSsoClientConfig implements DisposableBean  {

    @Value("${dse.sso.server}")
    private String dseSsoServer;

    @Value("${dse.sso.logout.path}")
    private String dseSsoLogoutPath;

    @Value("${dse-sso.excluded.paths}")
    private String dseSsoExcludedPaths;

    @Value("${dse.sso.redis.address}")
    private String xxlSsoRedisAddress;

    @Value("${dse.sso.redis.password}")
    private String password;

    @Bean
    public FilterRegistrationBean xxlSsoFilterRegistration() {


        JedisUtil.init(xxlSsoRedisAddress,password);


        // dse-sso, filter init
        FilterRegistrationBean registration = new FilterRegistrationBean();

        registration.setName("SsoWebClientFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(new SsoWebClientFilter());
        registration.addInitParameter(Constants.SSO_SERVER, dseSsoServer);
        registration.addInitParameter(Constants.SSO_LOGOUT_PATH, dseSsoLogoutPath);
        registration.addInitParameter(Constants.SSO_EXCLUDED_PATHS, dseSsoExcludedPaths);

        return registration;
    }

    @Override
    public void destroy() throws Exception {
      JedisUtil.close();
    }
}*/
