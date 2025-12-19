package com.ry.example.ffmpeg.chapter10;

/**
 * 第10章综合演示类
 * FFmpeg播放音视频功能演示
 */
public class Chapter10Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第10章 FFmpeg播放音视频功能演示 ===\n");
        
        // 10.1 通过SDL播放音视频
        demonstrateSDLPlayback();
        
        // 10.2 FFmpeg推流和拉流
        demonstrateStreamProcessing();
        
        // 10.3 SDL处理线程间同步
        demonstrateThreadSync();
        
        // 10.4 实战项目：同步播放音视频
        demonstrateSyncPlayback();
    }
    
    /**
     * 10.1 通过SDL播放音视频演示
     */
    private static void demonstrateSDLPlayback() {
        System.out.println("--- 10.1 SDL播放音视频演示 ---");
        
        // 10.1.1 FFmpeg集成SDL
        System.out.println("SDL播放视频:");
        String playVideo = AudioVideoPlayer.playVideoWithSDL(
            "input/sample_video.mp4",
            1280, 720
        );
        System.out.println(playVideo);
        System.out.println();
        
        // 10.1.2 利用SDL播放视频
        System.out.println("SDL播放音频:");
        String playAudio = AudioVideoPlayer.playAudioWithSDL(
            "input/sample_audio.mp3"
        );
        System.out.println(playAudio);
        System.out.println();
        
        // 10.1.3 利用SDL播放音频
        System.out.println("同步播放音视频:");
        String syncPlay = AudioVideoPlayer.syncPlayAudioVideo(
            "input/video.mp4",
            "input/audio.mp3"
        );
        System.out.println(syncPlay);
        System.out.println();
    }
    
    /**
     * 10.2 FFmpeg推流和拉流演示
     */
    private static void demonstrateStreamProcessing() {
        System.out.println("--- 10.2 推流和拉流演示 ---");
        
        // 10.2.1 什么是推拉流
        System.out.println("推拉流概念:");
        System.out.println("推流(Push): 将本地音视频流推送到服务器");
        System.out.println("拉流(Pull): 从服务器拉取音视频流");
        System.out.println();
        
        // 10.2.2 FFmpeg向网络推流
        System.out.println("推流到RTMP服务器:");
        String pushRtmp = AudioVideoPlayer.pushToRTMP(
            "input/live_video.mp4",
            "rtmp://server.live/live/stream_key"
        );
        System.out.println(pushRtmp);
        System.out.println();
        
        System.out.println("实时摄像头推流:");
        String liveStream = AudioVideoPlayer.liveStreamToRTMP(
            0, 0, "rtmp://server.live/live/camera_stream"
        );
        System.out.println(liveStream);
        System.out.println();
        
        // 10.2.3 FFmpeg从网络拉流
        System.out.println("从RTMP服务器拉流:");
        String pullRtmp = AudioVideoPlayer.pullFromRTMP(
            "rtmp://server.live/live/stream_key",
            "output/downloaded_stream.mp4"
        );
        System.out.println(pullRtmp);
        System.out.println();
        
        System.out.println("播放网络流:");
        String playStream = AudioVideoPlayer.playNetworkStream(
            "rtmp://server.live/live/stream",
            5
        );
        System.out.println(playStream);
        System.out.println();
        
        System.out.println("播放HLS流:");
        String playHLS = AudioVideoPlayer.playHLSStream(
            "https://example.com/live/playlist.m3u8"
        );
        System.out.println(playHLS);
        System.out.println();
    }
    
    /**
     * 10.3 SDL处理线程间同步演示
     */
    private static void demonstrateThreadSync() {
        System.out.println("--- 10.3 线程间同步演示 ---");
        
        // 10.3.1 SDL的线程
        System.out.println("多线程视频处理:");
        String multiThread = AudioVideoPlayer.multiThreadProcess(
            "input/large_video.mp4",
            "output/processed_video.mp4",
            4
        );
        System.out.println(multiThread);
        System.out.println();
        
        // 10.3.2 SDL的互斥锁
        System.out.println("互斥锁保护处理:");
        String mutexProcess = AudioVideoPlayer.mutexProtectedProcess(
            "input/important_video.mp4",
            "output/protected_video.mp4",
            300
        );
        System.out.println(mutexProcess);
        System.out.println();
        
        // 10.3.3 SDL的信号量
        System.out.println("信号量批量处理:");
        String[] inputFiles = {
            "input/video1.mp4",
            "input/video2.mp4",
            "input/video3.mp4",
            "input/video4.mp4"
        };
        String[] batchCommands = AudioVideoPlayer.semaphoreBatchProcess(
            inputFiles, "output/batch", 2
        );
        
        for (int i = 0; i < batchCommands.length && batchCommands[i] != null; i++) {
            System.out.printf("批量处理命令 %d:%n%s%n%n", i + 1, batchCommands[i]);
        }
    }
    
    /**
     * 10.4 实战项目：同步播放音视频
     */
    private static void demonstrateSyncPlayback() {
        System.out.println("--- 10.4 实战项目：同步播放音视频 ---");
        
        // 10.4.1 同步音视频的播放时钟
        System.out.println("音视频时钟同步:");
        String clockSync = AudioVideoPlayer.clockSync(
            "input/video_stream.mp4",
            "input/audio_stream.mp3",
            "output/synced_video.mp4",
            100,  // 视频延迟100ms
            50    // 音频延迟50ms
        );
        System.out.println(clockSync);
        System.out.println();
        
        // 10.4.2 优化音视频的同步播放
        System.out.println("优化同步播放:");
        String optimizedPlay = AudioVideoPlayer.optimizedSyncPlay(
            "input/sync_test_video.mp4",
            40  // 同步阈值40ms
        );
        System.out.println(optimizedPlay);
        System.out.println();
        
        System.out.println("实时同步处理:");
        String realTimeSync = AudioVideoPlayer.realTimeSyncProcess(
            "rtmp://input.server/live/input_stream",
            "rtmp://output.server/live/output_stream"
        );
        System.out.println(realTimeSync);
        System.out.println();
    }
    
    /**
     * 创建完整的直播系统演示
     */
    public static void createLiveStreamingSystem() {
        System.out.println("--- 完整直播系统演示 ---");
        
        // 摄像头采集和推流
        System.out.println("1. 摄像头采集推流:");
        String cameraPush = AudioVideoPlayer.liveStreamToRTMP(
            0, 0, "rtmp://live.server.com/app/stream_key"
        );
        System.out.println(cameraPush);
        System.out.println();
        
        // 流媒体服务器处理
        System.out.println("2. 流媒体转码处理:");
        String transcode = AudioVideoPlayer.realTimeSyncProcess(
            "rtmp://live.server.com/app/stream_key",
            "rtmp://cdn.server.com/live/hd_stream"
        );
        System.out.println(transcode);
        System.out.println();
        
        // CDN分发和播放
        System.out.println("3. CDN分发播放:");
        String cdnPlay = AudioVideoPlayer.playHLSStream(
            "https://cdn.server.com/live/playlist.m3u8"
        );
        System.out.println(cdnPlay);
        System.out.println();
    }
    
    /**
     * 多源混流演示
     */
    public static void multiSourceMixing() {
        System.out.println("--- 多源混流演示 ---");
        
        // 多个音视频源混合
        System.out.println("多视频源混合:");
        String[] sources = {
            "rtmp://source1.com/live/stream1",
            "rtmp://source2.com/live/stream2",
            "rtmp://source3.com/live/stream3"
        };
        
        for (int i = 0; i < sources.length; i++) {
            String mixStream = AudioVideoPlayer.realTimeSyncProcess(
                sources[i],
                "rtmp://output.server.com/live/mixed_stream_" + (i + 1)
            );
            System.out.printf("混流源 %d:%n%s%n%n", i + 1, mixStream);
        }
    }
    
    /**
     * 录制和回放系统
     */
    public static void recordingPlaybackSystem() {
        System.out.println("--- 录制回放系统演示 ---");
        
        // 录制直播流
        System.out.println("录制直播流:");
        String record = AudioVideoPlayer.pullFromRTMP(
            "rtmp://live.server.com/recording/stream",
            "recordings/archive_" + System.currentTimeMillis() + ".mp4"
        );
        System.out.println(record);
        System.out.println();
        
        // 回放录制内容
        System.out.println("回放录制内容:");
        String playback = AudioVideoPlayer.playVideoWithSDL(
            "recordings/archive_1234567890.mp4",
            1280, 720
        );
        System.out.println(playback);
        System.out.println();
    }
}