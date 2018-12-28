package com.dse.sso.core.exception;


import com.alibaba.fastjson.JSON;
import com.dse.sso.core.Constants;
import com.dse.sso.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.SQLTimeoutException;
import java.util.Map;

/**
 * 统一异常处理（Controller切面方式实现）
 *
 *      1、@ControllerAdvice：扫描所有Controller；
 *      2、@ControllerAdvice(annotations=RestController.class)：扫描指定注解类型的Controller；
 *      3、@ControllerAdvice(basePackages={"com.aaa","com.bbb"})：扫描指定package下的Controller
 *
 */

@Component
public class WebExceptionResolver implements HandlerExceptionResolver {

    // 日志记录
    private final Logger logger = LoggerFactory.getLogger(WebExceptionResolver.class);

    private final String errorMsgFormat = "Exception ===== [File][%s][Method][%s][Line][%d] ===== [%s].";

    /**
     * 处理异常核心方法
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        StackTraceElement element = ex.getStackTrace()[0];
        String errorMsg = String.format(errorMsgFormat, element.getFileName(), element.getMethodName(), element.getLineNumber(), ex
                .getLocalizedMessage());
        logger.error(errorMsg);

        // 设置异常类型
        Result result = new Result();
        result.setErr(Constants.CONTROLLER_RESULT.ERROR.intValue(), ex.getMessage());
        if (ex instanceof NullPointerException){
            result.setErr(Constants.CONTROLLER_RESULT.ERROR.intValue(), "操作中发生了异常[NULL],请联系系统管理员!");
        }
        if (ex instanceof SsoException && ex.getCause() instanceof SQLTimeoutException){
            result.setErr(Constants.CONTROLLER_RESULT.ERROR.intValue(), "网络超时，建议您尝试减少数据或稍候重试!");
        }

        if (ex instanceof SsoException) {
            result.setErr(Constants.CONTROLLER_RESULT.SERVICE_EXCEPTION.intValue(), ex.getMessage());
            // 用户为空
            if (isAjaxMethod(handler)) {
                // ajax请求中统一返回json格式
                return getAjaxResultMav(result);
            } else {
                ModelAndView mav =new ModelAndView();
                mav.addObject("exceptionMsg", JSON.toJSONString(result));
                mav.setViewName("/common/common.exception");
                return mav;
                // 直接跳转到提示页面
                //Map<String, Result> map = new HashMap<String, Result>();
              //  map.put("exception", result);
              //  return new ModelAndView("/common/common.exception", map);
            }

        } else {
            // 其他异常
            if (isAjaxMethod(handler)) {
                // ajax请求中统一返回json格式
                return getAjaxResultMav(result);
            } else {
                // 直接跳转到提示页面
//                Map<String, Result> map = new HashMap<String, Result>();
//                map.put("exception", result);
                ModelAndView mav =new ModelAndView();
                mav.addObject("exceptionMsg", JSON.toJSONString(result));
                mav.setViewName("/common/common.exception");
                  return mav;
               // return new ModelAndView("/common/common.exception", map);
            }
        }
    }

    /**
     * <必须约定,controller中的ajax请求处理方法都必须在方法上加上ResponseBody注解><br>
     * 判断是否是异步方法,true:是异步,false:不是异步
     *
     * @param handler
     * @return
     */
    private boolean isAjaxMethod(Object handler) {
        if (handler instanceof HandlerMethod) {
            // 如果当前请求是HandlerMethod,需要判断controller方法上的注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 如果method被ResponseBody注解标注,则说明当前方法是异步方法
            if (method.isAnnotationPresent(ResponseBody.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取异步返回结果ModelAndView
     *
     */
    private ModelAndView getAjaxResultMav(final Result result) {
        ModelAndView ajaxMav = new ModelAndView(new View() {
            @Override
            public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(JSON.toJSONString(result));
            }

            @Override
            public String getContentType() {
                return "application/json;charset=UTF-8";
            }
        });

        return ajaxMav;
    }

 /*   @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        logger.error("WebExceptionResolver:{}", ex);

        // if json
        boolean isJson = false;
        HandlerMethod method = (HandlerMethod)handler;
        ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
        if (responseBody != null) {
            isJson = true;
        }

        // error result
        Result result = new Result();
        if (ex instanceof SsoException) {
            result.setErr(Constants.CONTROLLER_RESULT.ERROR.intValue(),"Gload error :"+ ex.getMessage());
        } else {
            result.setErr(Constants.CONTROLLER_RESULT.ERROR.intValue(),"Gload error :"+ ex.toString().replaceAll("\n", "<br/>"));
        }

        // response
        ModelAndView mv = new ModelAndView();
        if (isJson) {
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print("{\"code\":"+Constants.CONTROLLER_RESULT.ERROR.intValue()+", \"msg\":\""+ "请求非法！" +"\"}");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return mv;
        } else {

            mv.addObject("exceptionMsg", JSON.toJSON(result));
            mv.setViewName("/common/common.exception");
            return mv;
        }
    }*/

}