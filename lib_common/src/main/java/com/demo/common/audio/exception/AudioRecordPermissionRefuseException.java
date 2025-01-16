package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/11 10:21
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordPermissionRefuseException extends AudioException{
    @Override
    public int getCode() {
        return 1003;
    }

    @Override
    public String getMsg() {
        return "权限未允许";
    }
}
