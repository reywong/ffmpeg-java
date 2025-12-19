package com.ry.example.ffmpeg.chapter10;

/**
 * 音视频播放器
 * 涵盖SDL播放、推流拉流、线程同步等功能
 */
public class AudioVideoPlayer {
    
    /**
     * SDL播放视频命令
     * @param inputFile 输入视频文件路径
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @return SDL播放FFmpeg命令
     */
    public static String playVideoWithSDL(String inputFile, int screenWidth, int screenHeight) {
        return String.format(
            "ffplay -f sdl2 -window_title \"Video Player\" -window_size %dx%d \"%s\"",
            screenWidth, screenHeight, inputFile
        );
    }
    
    /**
     * SDL播放音频命令
     * @param inputFile 输入音频文件路径
     * @return SDL播放FFmpeg命令
     */
    public static String playAudioWithSDL(String inputFile) {
        return String.format(
            "ffplay -f sdl2 -nodisp 2>/dev/null \"%s\"",
            inputFile
        );
    }
    
    /**
     * 同步播放音视频
     * @param videoFile 视频文件路径
     * @param audioFile 音频文件路径
     * @return 同步播放FFmpeg命令
     */
    public static String syncPlayAudioVideo(String videoFile, String audioFile) {
        return String.format(
            "ffplay -i \"%s\" -i \"%s\" -filter_complex \"[0:v][1:a]amerge=inputs=2[a]\" -map 0:v -map \"[a]\"",
            videoFile, audioFile
        );
    }
    
    /**
     * 推流到RTMP服务器
     * @param inputFile 输入文件路径
     * @param rtmpUrl RTMP服务器地址
     * @return 推流FFmpeg命令
     */
    public static String pushToRTMP(String inputFile, String rtmpUrl) {
        return String.format(
            "ffmpeg -re -i \"%s\" -c copy -f flv \"%s\"",
            inputFile, rtmpUrl
        );
    }
    
    /**
     * 实时推流（摄像头+麦克风）
     * @param videoDevice 视频设备索引
     * @param audioDevice 音频设备索引
     * @param rtmpUrl RTMP服务器地址
     * @return 实时推流FFmpeg命令
     */
    public static String liveStreamToRTMP(int videoDevice, int audioDevice, String rtmpUrl) {
        return String.format(
            "ffmpeg -f dshow -i video=\"Camera %d\" -f dshow -i audio=\"Microphone %d\" " +
            "-vcodec libx264 -acodec aac -f flv \"%s\"",
            videoDevice, audioDevice, rtmpUrl
        );
    }
    
    /**
     * 从RTMP服务器拉流
     * @param rtmpUrl RTMP服务器地址
     * @param outputFile 输出文件路径
     * @return 拉流FFmpeg命令
     */
    public static String pullFromRTMP(String rtmpUrl, String outputFile) {
        return String.format(
            "ffmpeg -i \"%s\" -c copy \"%s\"",
            rtmpUrl, outputFile
        );
    }
    
    /**
     * 网络流播放
     * @param streamUrl 网络流地址
     * @param bufferTime 缓冲时间（秒）
     * @return 播放网络流FFmpeg命令
     */
    public static String playNetworkStream(String streamUrl, int bufferTime) {
        return String.format(
            "ffplay -fflags nobuffer -rtsp_transport tcp -buffer %d \"%s\"",
            bufferTime, streamUrl
        );
    }
    
    /**
     * HLS流播放
     * @param hlsUrl HLS流地址
     * @return HLS播放FFmpeg命令
     */
    public static String playHLSStream(String hlsUrl) {
        return String.format(
            "ffplay -fflags nobuffer -c:v libx264 -c:a aac \"%s\"",
            hlsUrl
        );
    }
    
    /**
     * 多线程处理视频
     * @param inputFile 输入文件路径
     * @param outputFile 输出文件路径
     * @param threadCount 线程数量
     * @return 多线程处理FFmpeg命令
     */
    public static String multiThreadProcess(String inputFile, String outputFile, int threadCount) {
        return String.format(
            "ffmpeg -threads %d -i \"%s\" -c:v libx264 -preset ultrafast -crf 23 -c:a copy \"%s\"",
            threadCount, inputFile, outputFile
        );
    }
    
    /**
     * 互斥锁保护的音视频处理
     * @param inputFile 输入文件路径
     * @param outputFile 输出文件路径
     * @param timeout 超时时间（秒）
     * @return 互斥保护处理FFmpeg命令
     */
    public static String mutexProtectedProcess(String inputFile, String outputFile, int timeout) {
        return String.format(
            "timeout %d ffmpeg -i \"%s\" -c:v libx264 -c:a aac -b:a 128k \"%s\"",
            timeout, inputFile, outputFile
        );
    }
    
    /**
     * 信号量同步的批量处理
     * @param inputFiles 输入文件数组
     * @param outputDir 输出目录
     * @param maxConcurrent 最大并发数
     * @return 批量处理命令数组
     */
    public static String[] semaphoreBatchProcess(String[] inputFiles, String outputDir, int maxConcurrent) {
        String[] commands = new String[inputFiles.length];
        
        for (int i = 0; i < inputFiles.length; i++) {
            String inputFile = inputFiles[i];
            String fileName = inputFile.substring(inputFile.lastIndexOf("/") + 1);
            String outputFile = outputDir + "/processed_" + fileName;
            
            // 使用xargs进行并发控制
            if (i % maxConcurrent == 0) {
                int endIndex = Math.min(i + maxConcurrent, inputFiles.length);
                StringBuilder fileList = new StringBuilder();
                
                for (int j = i; j < endIndex; j++) {
                    if (j > i) fileList.append(" ");
                    fileList.append("\"").append(inputFiles[j]).append("\"");
                }
                
                commands[i] = String.format(
                    "echo %s | xargs -P %d -I {} ffmpeg -i {} -c:v libx264 -c:a aac \"%s/output_%d.mp4\"",
                    fileList.toString(), maxConcurrent, outputDir, i
                );
            }
        }
        
        return commands;
    }
    
    /**
     * 音视频时钟同步
     * @param videoFile 视频文件路径
     * @param audioFile 音频文件路径
     * @param outputFile 输出文件路径
     * @param videoDelay 视频延迟（毫秒）
     * @param audioDelay 音频延迟（毫秒）
     * @return 时钟同步FFmpeg命令
     */
    public static String clockSync(String videoFile, String audioFile, String outputFile, 
                                 int videoDelay, int audioDelay) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex " +
            "\"[0:v]setpts=PTS-STARTPTS+%dmsTB[v];" +
            "[1:a]asetpts=PTS-STARTPTS+%dmsTB[a]\" " +
            "-map \"[v]\" -map \"[a]\" -c:v libx264 -c:a aac \"%s\"",
            videoFile, audioFile, videoDelay, audioDelay, outputFile
        );
    }
    
    /**
     * 优化音视频同步播放
     * @param inputFile 输入文件路径
     * @param syncThreshold 同步阈值（毫秒）
     * @return 优化同步播放FFmpeg命令
     */
    public static String optimizedSyncPlay(String inputFile, int syncThreshold) {
        return String.format(
            "ffplay -sync ext -framedrop -autoexit \"%s\"",
            inputFile
        );
    }
    
    /**
     * 实时音视频同步处理
     * @param inputUrl 输入流地址
     * @param outputUrl 输出流地址
     * @return 实时同步处理FFmpeg命令
     */
    public static String realTimeSyncProcess(String inputUrl, String outputUrl) {
        return String.format(
            "ffmpeg -re -i \"%s\" -c:v libx264 -preset ultrafast -tune zerolatency " +
            "-c:a aac -b:a 128k -f flv \"%s\"",
            inputUrl, outputUrl
        );
    }
}