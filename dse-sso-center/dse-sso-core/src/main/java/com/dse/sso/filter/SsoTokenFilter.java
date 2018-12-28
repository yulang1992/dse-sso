package com.dse.sso.filter;

import com.dse.sso.core.Constants;
import com.dse.sso.core.SsoTokenLoginHelper;
import com.dse.sso.core.model.DseUser;
import com.dse.sso.core.path.AntPathMatcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * sso tpken Filter
 * add  2018-12-19
 */
public class SsoTokenFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SsoTokenFilter.class);

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private SsoTokenLoginHelper ssoTokenLoginHelper;

    private String ssoServer;
    private String logoutPath;
    private String excludedPaths;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("SsoTokenFilter init start...");
        ssoServer = filterConfig.getInitParameter(Constants.SSO_SERVER);
        logoutPath = filterConfig.getInitParameter(Constants.SSO_LOGOUT_PATH);
        excludedPaths = filterConfig.getInitParameter(Constants.SSO_EXCLUDED_PATHS);
        logger.info("SsoTokenFilter init.  end....");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // make url
        String servletPath = req.getServletPath();
        logger.info("servletPath : "+ servletPath);
        // excluded path check
         if(StringUtils.isNotBlank(servletPath) && servletPath.trim().length() >0){
            for (String excludedPath:excludedPaths.split(",")) {
                String uriPattern = excludedPath.trim();
                // 支持ANT表达式
                if (antPathMatcher.match(uriPattern, servletPath)) {
                    logger.info("servletPath : "+ servletPath +"---uriPattern :"+uriPattern);
                    // excluded path, allow
                    chain.doFilter(request, response);
                    return;
                }

            }
        }

        logger.info("logoutPath : "+ logoutPath);

        // 登出 信息过滤
        if(StringUtils.isNotBlank(logoutPath) && StringUtils.equals(logoutPath,servletPath)
                && logoutPath.trim().length() >0){
            logger.info("logout  kaishi .....");
            // logout
            ssoTokenLoginHelper.logout(req,res);
          //  SsoSessionIdHelper.logout(req,res);
            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":"+Constants.CONTROLLER_RESULT.SUCCESS+", \"msg\":\"\"}");

            return;
        }

        // 登录信息过滤
        DseUser ssoUser = ssoTokenLoginHelper.loginCheck(req);
        logger.info("user data :" +ssoUser.toString());
        if (ssoUser == null) {

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println("{\"code\":"+Constants.CONTROLLER_RESULT.LOGIN_FAIL.intValue()+", \"msg\":\""+ "SSO NO LOGIN !" +"\"}");
            return;
        }

        request.setAttribute(Constants.SSO_USER, ssoUser);
        ssoTokenLoginHelper.loginCheck(req,res);

        /**已经登录运行通过*/
        chain.doFilter(request, response);
        return;
    }


}
