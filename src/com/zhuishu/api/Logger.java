package com.zhuishu.api;

/**
 * @author yuyh.
 * @date 2016/12/13.
 */
public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        System.out.println("http : " + message);
    }
}
