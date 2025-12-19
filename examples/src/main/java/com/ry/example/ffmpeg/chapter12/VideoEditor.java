package com.ry.example.ffmpeg.chapter12;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 视频编辑器
 * 管理多个视频片段，提供时间轴渲染和剪辑功能
 */
public class VideoEditor {
    private static final String TAG = "VideoEditor";
    
    // 滤镜类型常量
    public static final int FILTER_NONE = 0;
    public static final int FILTER_GRAYSCALE = 1;
    public static final int FILTER_SEPIA = 2;
    public static final int FILTER_BRIGHTNESS = 3;
    public static final int FILTER_CONTRAST = 4;
    public static final int FILTER_SATURATION = 5;
    
    private List<VideoClip> clips;
    private int currentFilter = FILTER_NONE;
    private boolean isPlaying = false;
    private long currentTime = 0;
    
    public VideoEditor() {
        clips = new ArrayList<>();
    }
    
    /**
     * 添加视频片段
     * @param clip 视频片段
     */
    public void addVideoClip(VideoClip clip) {
        clips.add(clip);
        sortClips();
    }
    
    /**
     * 移除视频片段
     * @param index 索引
     */
    public void removeVideoClip(int index) {
        if (index >= 0 && index < clips.size()) {
            clips.remove(index);
        }
    }
    
    /**
     * 应用滤镜
     * @param filterType 滤镜类型
     */
    public void applyFilter(int filterType) {
        this.currentFilter = filterType;
    }
    
    /**
     * 渲染时间轴
     * @param canvas 画布
     * @param width 宽度
     * @param height 高度
     */
    public void renderTimeline(Canvas canvas, int width, int height) {
        // 绘制背景
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#2C2C2C"));
        canvas.drawRect(0, 0, width, height, paint);
        
        float totalTime = getTotalDuration();
        float clipHeight = height;
        
        // 绘制视频片段
        for (int i = 0; i < clips.size(); i++) {
            VideoClip clip = clips.get(i);
            float clipWidth = (float) clip.getActualDuration() / totalTime * width;
            float clipX = (float) clip.getStartTime() / totalTime * width;
            
            // 绘制片段背景
            paint.setColor(Color.parseColor("#4CAF50"));
            canvas.drawRect(clipX, 0, clipX + clipWidth, clipHeight, paint);
            
            // 绘制选中状态
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            canvas.drawRect(clipX, 0, clipX + clipWidth, clipHeight, paint);
            
            // 绘制片段标题
            paint.setColor(Color.WHITE);
            paint.setTextSize(24);
            String title = clip.getTitle();
            if (title.length() > 15) {
                title = title.substring(0, 12) + "...";
            }
            float textX = clipX + 10;
            float textY = clipHeight / 2 + 8;
            canvas.drawText(title, textX, textY, paint);
        }
        
        // 绘制播放进度线
        if (isPlaying) {
            float progressX = (float) currentTime / totalTime * width;
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            canvas.drawLine(progressX, 0, progressX, height, paint);
        }
    }
    
    /**
     * 获取总时长
     * @return 总时长（毫秒）
     */
    public long getTotalDuration() {
        long total = 0;
        for (VideoClip clip : clips) {
            total = Math.max(total, clip.getEndTime());
        }
        return total;
    }
    
    /**
     * 开始播放
     */
    public void startPlayback() {
        isPlaying = true;
        currentTime = 0;
    }
    
    /**
     * 停止播放
     */
    public void stopPlayback() {
        isPlaying = false;
        currentTime = 0;
    }
    
    /**
     * 更新播放时间
     * @param deltaTime 时间增量（毫秒）
     */
    public void updateTime(long deltaTime) {
        if (isPlaying) {
            currentTime += deltaTime;
            if (currentTime >= getTotalDuration()) {
                stopPlayback();
            }
        }
    }
    
    /**
     * 获取当前时间
     * @return 当前时间（毫秒）
     */
    public long getCurrentTime() {
        return currentTime;
    }
    
    /**
     * 检查是否正在播放
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * 获取当前滤镜
     * @return 滤镜类型
     */
    public int getCurrentFilter() {
        return currentFilter;
    }
    
    /**
     * 对片段进行排序
     */
    private void sortClips() {
        Collections.sort(clips, (a, b) -> 
            Long.compare(a.getStartTime(), b.getStartTime()));
    }
}