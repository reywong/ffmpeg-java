package com.ry.example.ffmpeg.chapter12;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * AudioTrack音频播放器
 * 使用Android原生AudioTrack播放PCM音频数据
 */
public class AudioTrackPlayer {
    private AudioTrack audioTrack;
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isPlaying = false;
    
    /**
     * 初始化AudioTrack
     */
    public void init() {
        int bufferSize = AudioTrack.getMinBufferSize(
            sampleRate, channelConfig, audioFormat);
        
        audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM
        );
    }
    
    /**
     * 开始播放
     */
    public void play() {
        if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.play();
            isPlaying = true;
        }
    }
    
    /**
     * 播放音频数据
     * @param audioData 音频数据
     */
    public void writeData(byte[] audioData) {
        if (audioTrack != null && isPlaying) {
            audioTrack.write(audioData, 0, audioData.length);
        }
    }
    
    /**
     * 停止播放
     */
    public void stop() {
        isPlaying = false;
        if (audioTrack != null) {
            audioTrack.stop();
        }
    }
    
    /**
     * 释放资源
     */
    public void release() {
        isPlaying = false;
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
    
    /**
     * 检查是否正在播放
     * @return 是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}