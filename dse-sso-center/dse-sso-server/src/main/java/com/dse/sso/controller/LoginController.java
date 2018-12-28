package com.dse.sso.controller;

import com.dse.sso.core.Constants;
import com.dse.sso.core.SsoSessionIdHelper;
import com.dse.sso.core.SsoTokenLoginHelper;
import com.dse.sso.core.model.DseUser;
import com.dse.sso.server.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * FileName: LoginController
 * Author:   EdwinYu
 * Date:     2018-12-19 11:38
 * Description: 登录controller类
 * Version:1.0.0
 */
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

   // @Autowired
    //private SsoTokenLoginHelper ssoTokenLoginHelper;

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        DseUser dseUser = SsoTokenLoginHelper.loginCheck(request);
        if (dseUser == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("dseUser", dseUser);
            return "index";
        }
    }

    /**
     * 登录方法
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request) {

        // login check
        DseUser dseUser = SsoTokenLoginHelper.loginCheck(request);

        log.info("dseuser login :" +dseUser);
        if (dseUser != null) {
            // success redirect
            String redirectUrl = request.getParameter(Constants.REDIRECT_URL);
            if (StringUtils.isNotBlank(redirectUrl)) {
                String token = SsoSessionIdHelper.getRequestToken(request);
                String redirectUrlFinal = redirectUrl + "?" + Constants.SSO_SESSIONID + "=" + token;;
                log.info("redirectUrlFinal url is :" + redirectUrlFinal);
                return "redirect:" + redirectUrlFinal;
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("errorMsg", request.getParameter("errorMsg"));
        model.addAttribute(Constants.REDIRECT_URL, request.getParameter(Constants.REDIRECT_URL));
        return "login";
    }

    /**
     * Login
     *
     * @param request
     * @param redirectAttributes
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request,
                          HttpServletResponse response,
                          RedirectAttributes redirectAttributes,
                          String username,
                          String password,
                          String ifRemember) {

      //  boolean ifRem = (ifRemember!=null&&"on".equals(ifRemember))?true:false;

        // valid login
        DseUser dseUser = userService.findByLoginUser(username, password);
        //if (result.getCode() != ReturnT.SUCCESS_CODE) {
        if(dseUser ==null){
            redirectAttributes.addAttribute("errorMsg", "用户信息非法！");
            redirectAttributes.addAttribute(Constants.REDIRECT_URL, request.getParameter(Constants.REDIRECT_URL));
            return "redirect:/login";
        }

        // 用户信息正常获取到
        String tokenId =SsoSessionIdHelper.makeTokenId(dseUser);

        // 存储相应的token信息和用户信息
        SsoTokenLoginHelper.login(response, tokenId, dseUser);
        response.setHeader(Constants.SSO_SESSIONID,tokenId);

        // 4、return, redirect token 信息   redirectUrl 登录成功跳转的地址
        String redirectUrl = request.getParameter(Constants.REDIRECT_URL);
        if (StringUtils.isNotBlank(redirectUrl)) {
            String redirectUrlFinal = redirectUrl + "?" + Constants.SSO_SESSIONID + "=" + tokenId;
            log.info("doLogin method is redirectUrlFinal : " + redirectUrlFinal);
            return "redirect:" + redirectUrlFinal;
        } else {
            return "redirect:/";
        }

    }

    /**
     * Logout
     *
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        SsoTokenLoginHelper.logout(request,response);
        redirectAttributes.addAttribute(Constants.REDIRECT_URL, request.getParameter(Constants.REDIRECT_URL));
        return "redirect:/login";
    }

}
