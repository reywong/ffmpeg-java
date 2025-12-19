package com.ry.example.ffmpeg.chapter12;

/**
 * FFmpeg管理器 - Android版本
 * 提供FFmpeg库的初始化和基本操作
 */
public class FFmpegManager {
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
    }
    
    /**
     * 初始化FFmpeg库
     * @return 0表示成功，负数表示失败
     */
    public native int initFFmpeg();
    
    /**
     * 播放视频文件
     * @param filename 视频文件路径
     * @return 0表示成功，负数表示失败
     */
    public native int playVideo(String filename);
    
    /**
     * 停止播放
     * @return 0表示成功，负数表示失败
     */
    public native int stopPlayback();
    
    /**
     * 获取视频时长
     * @param filename 视频文件路径
     * @return 视频时长（毫秒）
     */
    public native long getVideoDuration(String filename);
    
    /**
     * 获取视频信息
     * @param filename 视频文件路径
     * @return 视频信息JSON字符串
     */
    public native String getVideoInfo(String filename);
}