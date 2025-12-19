package com.ry.example.ffmpeg.chapter03;

/**
 * 第3章 FFmpeg编解码综合示例
 * 演示本章所有主要功能的综合使用
 */
public class Chapter03Demo {

    public static void main(String[] args) {
        System.out.println("=== 第3章 FFmpeg编解码综合示例 ===\n");
        
        // 演示3.1 音视频时间处理
        demonstrateTimeHandling();
        
        // 演示3.2 分离音视频
        demonstrateSeparation();
        
        // 演示3.3 合并音视频
        demonstrateMerging();
        
        // 演示3.4 视频浏览与格式分析
        demonstrateAnalysis();
        
        System.out.println("=== 第3章示例演示完成 ===");
    }
    
    /**
     * 演示音视频时间处理功能
     */
    private static void demonstrateTimeHandling() {
        System.out.println("--- 3.1 音视频时间处理演示 ---");
        
        String inputPath = "input.mp4";
        
        // 获取音视频时间信息
        String timeInfo = VideoAudioTime.getTimeInfo(inputPath);
        System.out.println(timeInfo);
        
        // 解析帧率
        String frameRate = VideoAudioTime.getVideoFrameRate(inputPath);
        double actualFrameRate = VideoAudioTime.parseFrameRate(frameRate);
        System.out.println("解析的实际帧率: " + actualFrameRate + " fps");
        
        // 获取视频时长
        double duration = VideoAudioTime.getVideoDuration(inputPath);
        System.out.println("视频总时长: " + String.format("%.2f", duration) + " 秒");
        
        System.out.println();
    }
    
    /**
     * 演示分离音视频功能
     */
    private static void demonstrateSeparation() {
        System.out.println("--- 3.2 分离音视频演示 ---");
        
        String inputPath = "input.mp4";
        
        // 获取流信息
        String streamInfo = SeparateAudioVideo.getStreamInfo(inputPath);
        System.out.println(streamInfo);
        
        // 提取视频流
        String videoOnlyPath = "video_only.mp4";
        boolean videoSuccess = SeparateAudioVideo.copyVideoStream(inputPath, videoOnlyPath);
        System.out.println("视频流提取" + (videoSuccess ? "成功" : "失败") + ": " + videoOnlyPath);
        
        // 提取音频为不同格式
        String[] audioFormats = {"mp3", "aac", "wav"};
        for (String format : audioFormats) {
            String outputPath = "audio." + format;
            boolean success = false;
            
            switch (format) {
                case "mp3":
                    success = SeparateAudioVideo.extractAudioToMp3(inputPath, outputPath, 2);
                    break;
                case "aac":
                    success = SeparateAudioVideo.extractAudioToAac(inputPath, outputPath, "192k");
                    break;
                case "wav":
                    success = SeparateAudioVideo.extractAudioToWav(inputPath, outputPath, 44100);
                    break;
            }
            
            System.out.println("音频提取为" + format.toUpperCase() + (success ? "成功" : "失败") + ": " + outputPath);
        }
        
        // 切割视频
        String clipPath = "video_clip.mp4";
        boolean clipSuccess = SeparateAudioVideo.cutVideo(inputPath, clipPath, 10, 30, true);
        System.out.println("视频切割" + (clipSuccess ? "成功" : "失败") + ": " + clipPath);
        
        System.out.println();
    }
    
    /**
     * 演示合并音视频功能
     */
    private static void demonstrateMerging() {
        System.out.println("--- 3.3 合并音视频演示 ---");
        
        String videoPath = "video_only.mp4";
        String audioPath = "audio.mp3";
        
        // 合并视频和音频
        String mergedPath = "merged_output.mp4";
        boolean mergeSuccess = MergeAudioVideo.mergeVideoAudio(videoPath, audioPath, mergedPath, true, "aac");
        System.out.println("音视频合并" + (mergeSuccess ? "成功" : "失败") + ": " + mergedPath);
        
        // 重新编码视频
        String reencodedPath = "reencoded_output.mp4";
        boolean reencodeSuccess = MergeAudioVideo.reencodeVideo(mergedPath, reencodedPath, "libx264", 23, "medium");
        System.out.println("视频重新编码" + (reencodeSuccess ? "成功" : "失败") + ": " + reencodedPath);
        
        // 调整分辨率
        String resizedPath = "resized_output.mp4";
        boolean resizeSuccess = MergeAudioVideo.resizeVideo(reencodedPath, resizedPath, 1280, -1);
        System.out.println("视频分辨率调整" + (resizeSuccess ? "成功" : "失败") + ": " + resizedPath);
        
        // 格式转换
        String convertedPath = "converted_output.avi";
        boolean convertSuccess = MergeAudioVideo.convertFormat(reencodedPath, convertedPath, "libxvid", "mp3");
        System.out.println("格式转换" + (convertSuccess ? "成功" : "失败") + ": " + convertedPath);
        
        // 混合多个音频
        String[] audioFiles = {"audio1.mp3", "audio2.mp3"};
        String mixedPath = "mixed_audio_output.mp4";
        boolean mixSuccess = MergeAudioVideo.mixMultipleAudio(videoPath, audioFiles, mixedPath);
        System.out.println("多音频混合" + (mixSuccess ? "成功" : "失败") + ": " + mixedPath);
        
        System.out.println();
    }
    
    /**
     * 演示视频浏览与格式分析功能
     */
    private static void demonstrateAnalysis() {
        System.out.println("--- 3.4 视频浏览与格式分析演示 ---");
        
        String videoPath = "input.mp4";
        
        // 获取基本信息
        String basicInfo = VideoAnalysisPlayer.getBasicInfo(videoPath);
        System.out.println(basicInfo);
        
        // 获取格式信息
        String formatInfo = VideoAnalysisPlayer.getFormatInfo(videoPath);
        System.out.println(formatInfo);
        
        // 解析视频参数
        String videoParams = VideoAnalysisPlayer.parseVideoParams(videoPath);
        System.out.println(videoParams);
        
        // 获取帧信息
        String frameInfo = VideoAnalysisPlayer.getFrameInfo(videoPath, 5);
        System.out.println(frameInfo);
        
        // 封装H264为MP4（模拟操作）
        String h264Path = "input.h264";
        String mp4Path = "output_from_h264.mp4";
        boolean encapsulateSuccess = VideoAnalysisPlayer.encapsulateH264ToMp4(h264Path, mp4Path, 25);
        System.out.println("H.264封装为MP4" + (encapsulateSuccess ? "成功" : "失败") + ": " + mp4Path);
        
        // 带音频的H264封装
        String audioPath = "audio.mp3";
        String mp4WithAudioPath = "output_with_audio.mp4";
        boolean encapsulateWithAudioSuccess = VideoAnalysisPlayer.encapsulateH264WithAudio(h264Path, audioPath, mp4WithAudioPath);
        System.out.println("H.264带音频封装" + (encapsulateWithAudioSuccess ? "成功" : "失败") + ": " + mp4WithAudioPath);
        
        System.out.println();
        
        // 注意：ffplay播放功能在实际环境中需要用户交互，这里只作演示说明
        System.out.println("播放器功能说明:");
        System.out.println("- 基本播放: ffplay input.mp4");
        System.out.println("- 播放片段: ffplay -ss 10 -t 30 input.mp4");
        System.out.println("- 循环播放: ffplay -loop 0 input.mp4");
        System.out.println("- 音频播放: ffplay -vn input.mp3");
        System.out.println("- 带滤镜播放: ffplay -vf 'scale=640:480' input.mp4");
        System.out.println();
    }
}