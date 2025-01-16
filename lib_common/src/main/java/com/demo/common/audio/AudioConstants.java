package com.demo.common.audio;

/**
 * @Description:
 * @Date: 2024/1/5 11:20
 * @author: zhaoyudong
 * @version: 1.0
 */
public class AudioConstants {

    /** 录音文件所在目录*/
    public static final String RECORDS_DIR = "dj_records";
    public static final String EXTENSION_SEPARATOR = ".";
    /** 文件格式后缀 amr*/
    public static final String FORMAT_AMR = "amr";
    /** 文件格式后缀 m4a*/
    public static final String FORMAT_M4A = "m4a";
    /** 延时发送录音时长记录时间、单位毫秒*/
    public final static int RECORDING_VISUALIZATION_INTERVAL = 13; //单位毫秒
    /** 最小可用存储空间 发起录音*/
    public static final int MIN_AVAILABLE_SPACE = 10; //单位 MB
    /** 允许最大录音时长*/
    public static final long MAX_RECORDING_DURATION = 60 * 1000; //单位毫秒
    /** 录音通道数 1或者2*/
    public static final int CHANNEL_COUNT = 1;
    /** 录音采样率*/
    public static final int SAMPLE_RATE = 8000; //8000  44100
    /** 录音比特率*/
    public static final int BITRATE = 16000; //128000 16000

}
