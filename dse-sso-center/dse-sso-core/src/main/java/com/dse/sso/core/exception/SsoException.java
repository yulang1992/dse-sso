package com.dse.sso.core.exception;

/**
 * FileName: SsoException
 * Author:   EdwinYu
 * Date:     2018-12-19 16:51
 * Description:
 * Version:1.0.0
 */
public class SsoException extends RuntimeException {

    private static final long serialVersionUID = 42L;

    public SsoException(String msg) {
        super(msg);
    }

    public SsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SsoException(Throwable cause) {
        super(cause);
    }

}