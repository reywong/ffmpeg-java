package com.ry.example.ffmpeg.chapter12;

/**
 * Chapter12演示类
 * 展示FFmpeg在Android平台上的各种应用场景
 */
public class Chapter12Demo {
    
    /**
     * 演示基本视频剪辑功能
     */
    public static void demonstrateVideoEditing() {
        System.out.println("=== 视频剪辑演示 ===");
        
        // 创建视频编辑器
        VideoEditor editor = new VideoEditor();
        
        // 添加视频片段
        VideoClip clip1 = new VideoClip("/sdcard/video1.mp4");
        clip1.setStartTime(0);
        clip1.setTrimStart(5000);  // 从5秒开始
        clip1.setTrimEnd(15000);   // 到15秒结束
        editor.addVideoClip(clip1);
        
        VideoClip clip2 = new VideoClip("/sdcard/video2.mp4");
        clip2.setStartTime(10000); // 从10秒开始添加
        clip2.setTrimStart(0);
        clip2.setTrimEnd(8000);    // 8秒长度
        editor.addVideoClip(clip2);
        
        // 应用滤镜
        editor.applyFilter(VideoEditor.FILTER_SEPIA);
        
        // 开始播放
        editor.startPlayback();
        
        // 模拟播放进度
        for (int i = 0; i < 50; i++) {
            editor.updateTime(1000); // 每次更新1秒
            System.out.printf("播放进度: %d/%dms%n", 
                            editor.getCurrentTime(), editor.getTotalDuration());
            
            if (!editor.isPlaying()) {
                break;
            }
            
            try {
                Thread.sleep(100); // 模拟延迟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("视频剪辑演示完成");
    }
    
    /**
     * 演示音频播放功能
     */
    public static void demonstrateAudioPlayback() {
        System.out.println("\n=== 音频播放演示 ===");
        
        AudioTrackPlayer player = new AudioTrackPlayer();
        
        // 初始化播放器
        player.init();
        
        // 设置音频参数
        player.setAudioConfig(44100, 
                             android.media.AudioFormat.CHANNEL_OUT_STEREO, 
                             android.media.AudioFormat.ENCODING_PCM_16BIT);
        
        // 开始播放
        player.play();
        
        // 模拟音频数据播放
        byte[] audioData = new byte[1024];
        for (int i = 0; i < 10; i++) {
            // 模拟音频数据
            for (int j = 0; j < audioData.length; j++) {
                audioData[j] = (byte) (Math.sin(j * 0.1) * 127);
            }
            
            player.writeData(audioData);
            
            System.out.printf("播放音频数据块 %d%n", i + 1);
            
            try {
                Thread.sleep(100); // 模拟音频间隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 停止并释放
        player.stop();
        player.release();
        
        System.out.println("音频播放演示完成");
    }
    
    /**
     * 演示FFmpeg管理器功能
     */
    public static void demonstrateFFmpegManager() {
        System.out.println("\n=== FFmpeg管理器演示 ===");
        
        FFmpegManager ffmpeg = new FFmpegManager();
        
        // 初始化FFmpeg
        int result = ffmpeg.initFFmpeg();
        System.out.println("FFmpeg初始化结果: " + result);
        
        // 获取视频信息
        String videoPath = "/sdcard/sample.mp4";
        long duration = ffmpeg.getVideoDuration(videoPath);
        String videoInfo = ffmpeg.getVideoInfo(videoPath);
        
        System.out.println("视频路径: " + videoPath);
        System.out.println("视频时长: " + duration + "ms");
        System.out.println("视频信息: " + videoInfo);
        
        System.out.println("FFmpeg管理器演示完成");
    }
    
    /**
     * 主演示方法
     */
    public static void main(String[] args) {
        System.out.println("Chapter12 - FFmpeg移动开发演示");
        System.out.println("================================");
        
        // 注意：在实际Android环境中运行
        System.out.println("注意：此演示需要在Android环境中运行，以下为模拟演示\n");
        
        demonstrateFFmpegManager();
        demonstrateVideoEditing();
        demonstrateAudioPlayback();
        
        System.out.println("\n================================");
        System.out.println("所有演示完成！");
        
        System.out.println("\n实际使用说明:");
        System.out.println("1. 在Android项目中集成FFmpeg库");
        System.out.println("2. 确保拥有必要的文件读写权限");
        System.out.println("3. 根据实际需求调整参数和配置");
        System.out.println("4. 注意内存管理和资源释放");
    }
}