package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/5 11:29
 * @author: zhaoyudong
 * @version: 1.0
 */
public abstract class AudioException extends Exception{

    public abstract int getCode();
    public abstract String getMsg();

}
