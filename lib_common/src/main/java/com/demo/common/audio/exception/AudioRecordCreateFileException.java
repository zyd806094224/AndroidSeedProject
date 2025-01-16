package com.demo.common.audio.exception;

/**
 * @Description:
 * @Date: 2024/1/5 17:23
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioRecordCreateFileException extends AudioException{
    @Override
    public int getCode() {
        return 1007;
    }

    @Override
    public String getMsg() {
        return "创建文件创建失败异常";
    }
}
