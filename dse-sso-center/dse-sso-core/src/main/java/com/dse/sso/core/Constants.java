package com.dse.sso.core;

/**
 * FileName: 系统常量配置类
 * Author:   EdwinYu
 * Date:     2018-12-19 13:46
 * Description:
 * Version:1.0.0
 */
public class Constants {


    public static final String SSO_LOGIN = "/login";

    public static final String SSO_LOGOUT = "/logout";

    public static final String SSO_SERVER = "sso_server";

    public static final String SSO_LOGOUT_PATH = "SSO_LOGOUT_PATH";

    public static final String SSO_EXCLUDED_PATHS = "SSO_EXCLUDED_PATHS";

    public static final String SSO_USER = "DSE_sso_user";
    public static final String SSO_TKEN = "DSE_SSO_TOKEN";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String SSO_SESSIONID = "DSE_SSO_SESSIONID";

    /**
     * controller返回值状态
     */
    public enum CONTROLLER_RESULT {
        /** 成功 */
        SUCCESS(1),

        /** 未知错误 */
        ERROR(-999),

        /** 业务异常 */
        SERVICE_EXCEPTION(-998),

        /** 未登录 */
        NOT_LOGIN(-1),

        /** 参数为空 */
        NULL_PARAMETER(-2),

        /** 对象为空 */
        NULL_OBJECT(-3),

        /** 没有操作权限 */
        UNAUTHORIZED(-4),

        /** 用户名错误 */
        ERROR_USERNAME(-5),

        /** 密码错误 */
        ERROR_PASSWORD(-6),

        /** 对象已存在 */
        ISEXIST(-7),

        /** 已离职 */
        IS_QUIT(-8),

        /** 验证码错误 */
        ERROR_VERIFICATION(-9),

        /** 密码不一致 */
        ERROR_REPASSWORD(-10),

        /** 短信验证码错误 */
        ERROR_MESSAGE_VERIFICATION(-11),

        /** 图片过大 */
        ERROR_IMAGE_SIZE(-12),

        /** 图片上传失败 */
        ERROR_IMAGE(-13),

        /** 短信发送失败 */
        FAIL_VERIFICATION(-14),

        /**登录失败*/
        LOGIN_FAIL(-15);

        private final Integer value;

        CONTROLLER_RESULT(Integer value) {
            this.value = value;
        }

        public int intValue() {
            return this.value;
        }
    }

}
