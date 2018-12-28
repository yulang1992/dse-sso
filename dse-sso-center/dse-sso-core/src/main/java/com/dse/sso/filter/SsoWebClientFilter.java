package com.dse.sso.filter;

import com.dse.sso.core.Constants;
import com.dse.sso.core.SsoTokenLoginHelper;
import com.dse.sso.core.model.DseUser;
import com.dse.sso.core.path.AntPathMatcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FileName: SsoWebClientFilter
 * Author:   EdwinYu
 * Date:     2018-12-20 9:44
 * Description: 客户端sso过滤器
 * Version:1.0.0
 */
public class SsoWebClientFilter extends HttpServlet implements Filter{

    private static Logger logger = LoggerFactory.getLogger(SsoWebClientFilter.class);

   // @Autowired
   // private SsoTokenLoginHelper ssoTokenLoginHelper;


    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String ssoServer;
    private String logoutPath;
    private String excludedPaths;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ssoServer = filterConfig.getInitParameter(Constants.SSO_SERVER);
        logoutPath = filterConfig.getInitParameter(Constants.SSO_LOGOUT_PATH);
        excludedPaths = filterConfig.getInitParameter(Constants.SSO_EXCLUDED_PATHS);

        logger.info("SsoWebClientFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // make url
        String servletPath = req.getServletPath();

        logger.info("servletPath->" +servletPath);


        // excluded path check
        if (StringUtils.isNotBlank(excludedPaths) && excludedPaths.trim().length() >0) {
            for (String excludedPath:excludedPaths.split(",")) {
                String uriPattern = excludedPath.trim();
                logger.info("uriPattern: " +uriPattern);
                // 支持ANT表达式
                if (antPathMatcher.match(uriPattern, servletPath)) {
                    // excluded path, allow
                    chain.doFilter(request, response);
                    return;
                }

            }
        }

        // logout path check
        if(StringUtils.isNotBlank(logoutPath) && StringUtils.equals(logoutPath,servletPath)
                && logoutPath.trim().length() >0){

            // remove token
            SsoTokenLoginHelper.logout(req,res);

            // redirect logout
            String logoutPageUrl = ssoServer.concat(Constants.SSO_LOGOUT);
            logger.info("[logoutPageUrl url is] : "+logoutPageUrl);
            res.sendRedirect(logoutPageUrl);
            return;
        }

        // valid login user, token + redirect

        DseUser dselUser = SsoTokenLoginHelper.loginCheck(req);
        // valid login fail
        if (dselUser == null) {
            String header = req.getHeader("content-type");
            boolean isJson=  header!=null && header.contains("json");
            if (isJson) {
                // json msg
                res.setContentType("application/json;charset=utf-8");
                res.getWriter().println("{\"code\":"+Constants.CONTROLLER_RESULT.LOGIN_FAIL.intValue()+", \"msg\":\""+ "user is not login!" +"\"}");
                return;
            } else {

                // total link
                String link = req.getRequestURL().toString();

                // redirect logout
                String loginPageUrl = ssoServer.concat(Constants.SSO_LOGIN)
                        + "?" + Constants.REDIRECT_URL + "=" + link;

                res.sendRedirect(loginPageUrl);
                return;
            }

        }

        // ser sso user
        request.setAttribute(Constants.SSO_USER, dselUser);
        SsoTokenLoginHelper.loginCheck(req,res);

        // already login, allow
        chain.doFilter(request, response);
        return;
    }

}
