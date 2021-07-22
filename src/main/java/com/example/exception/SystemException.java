package com.example.exception;

/**
 * 系统异常(SystemException)
 */
public class SystemException extends RuntimeException {

    public SystemException(String msg){
        super(msg);
    }

    public SystemException() {
        super();
    }
}
