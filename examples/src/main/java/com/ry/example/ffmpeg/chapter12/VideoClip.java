package com.ry.example.ffmpeg.chapter12;

import android.media.MediaMetadataRetriever;
import java.io.File;

/**
 * 视频片段类
 * 用于管理单个视频片段的属性和操作
 */
public class VideoClip {
    private String filePath;
    private String title;
    private long startTime;
    private long duration;
    private long trimStart;
    private long trimEnd;
    private float volume = 1.0f;
    private boolean muted = false;
    private int rotation = 0;
    
    /**
     * 构造函数
     * @param filePath 视频文件路径
     */
    public VideoClip(String filePath) {
        this.filePath = filePath;
        this.title = new File(filePath).getName();
        this.startTime = 0;
        
        // 获取视频信息
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            String durationStr = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION);
            String rotationStr = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            
            this.duration = Long.parseLong(durationStr);
            this.trimStart = 0;
            this.trimEnd = this.duration;
            
            if (rotationStr != null) {
                this.rotation = Integer.parseInt(rotationStr);
            }
        } finally {
            retriever.release();
        }
    }
    
    /**
     * 获取裁剪后的实际时长
     * @return 实际时长（毫秒）
     */
    public long getActualDuration() {
        return trimEnd - trimStart;
    }
    
    /**
     * 获取结束时间
     * @return 结束时间（毫秒）
     */
    public long getEndTime() {
        return startTime + getActualDuration();
    }
    
    // Getter和Setter方法
    public String getFilePath() { return filePath; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getDuration() { return duration; }
    public long getTrimStart() { return trimStart; }
    public void setTrimStart(long trimStart) { 
        this.trimStart = Math.max(0, Math.min(trimStart, trimEnd)); 
    }
    
    public long getTrimEnd() { return trimEnd; }
    public void setTrimEnd(long trimEnd) { 
        this.trimEnd = Math.max(trimStart, Math.min(trimEnd, duration)); 
    }
    
    public float getVolume() { return volume; }
    public void setVolume(float volume) { 
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); 
    }
    
    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
    
    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation; }
}