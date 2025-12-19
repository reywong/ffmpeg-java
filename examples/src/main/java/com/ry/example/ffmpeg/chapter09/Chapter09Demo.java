package com.ry.example.ffmpeg.chapter09;

/**
 * 第9章综合演示类
 * FFmpeg混合音视频功能演示
 */
public class Chapter09Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第9章 FFmpeg混合音视频功能演示 ===\n");
        
        // 9.1 多路音频演示
        demonstrateMultiAudio();
        
        // 9.2 多路视频演示
        demonstrateMultiVideo();
        
        // 9.3 转场动画演示
        demonstrateTransitions();
        
        // 9.4 实战项目：翻书转场动画
        demonstratePageFlipProject();
    }
    
    /**
     * 9.1 多路音频演示
     */
    private static void demonstrateMultiAudio() {
        System.out.println("--- 9.1 多路音频功能演示 ---");
        
        // 9.1.1 同时过滤视频和音频
        System.out.println("同时过滤视频和音频:");
        String filterVideoAudio = AudioVideoMixer.mixAudio(
            "input/main_audio.mp3",
            "input/background_music.mp3",
            "output/mixed_audio.mp3",
            0.3
        );
        System.out.println(filterVideoAudio);
        System.out.println();
        
        // 9.1.2 利用多通道实现混音
        System.out.println("多通道实现混音:");
        String[] audioFiles = {
            "input/vocal.mp3",
            "input/guitar.mp3",
            "input/drums.mp3"
        };
        double[] weights = {1.0, 0.7, 0.8};
        String multiChannelMix = AudioVideoMixer.mixMultipleAudio(
            audioFiles, "output/multi_channel_mix.mp3", weights
        );
        System.out.println(multiChannelMix);
        System.out.println();
        
        // 9.1.3 给视频添加背景音乐
        System.out.println("给视频添加背景音乐:");
        String addBgMusic = AudioVideoMixer.addBackgroundMusic(
            "input/speech_video.mp4",
            "input/background_music.mp3",
            "output/video_with_bgm.mp4",
            0.2
        );
        System.out.println(addBgMusic);
        System.out.println();
    }
    
    /**
     * 9.2 多路视频演示
     */
    private static void demonstrateMultiVideo() {
        System.out.println("--- 9.2 多路视频功能演示 ---");
        
        // 9.2.1 通过叠加视频实现画中画
        System.out.println("画中画效果:");
        String pipEffect = AudioVideoMixer.pictureInPicture(
            "input/main_video.mp4",
            "input/sub_video.mp4",
            "output/pip_video.mp4",
            50, 50, 320, 240
        );
        System.out.println(pipEffect);
        System.out.println();
        
        // 9.2.2 多路视频实现四宫格效果
        System.out.println("四宫格效果:");
        String fourGridEffect = AudioVideoMixer.fourGrid(
            "input/top_left.mp4",
            "input/top_right.mp4",
            "input/bottom_left.mp4",
            "input/bottom_right.mp4",
            "output/four_grid.mp4",
            320
        );
        System.out.println(fourGridEffect);
        System.out.println();
        
        // 9.2.3 透视两路视频的混合画面
        System.out.println("透视混合效果:");
        String perspectiveBlend = AudioVideoMixer.perspectiveBlend(
            "input/video1.mp4",
            "input/video2.mp4",
            "output/perspective_blend.mp4"
        );
        System.out.println(perspectiveBlend);
        System.out.println();
    }
    
    /**
     * 9.3 转场动画演示
     */
    private static void demonstrateTransitions() {
        System.out.println("--- 9.3 转场动画功能演示 ---");
        
        // 9.3.1 给视频添加转场动画
        System.out.println("淡入淡出转场:");
        String fadeTransition = AudioVideoMixer.fadeTransition(
            "input/video1.mp4",
            "input/video2.mp4",
            "output/fade_transition.mp4",
            2
        );
        System.out.println(fadeTransition);
        System.out.println();
        
        // 9.3.2 自定义斜边转场动画
        System.out.println("斜边转场:");
        String slideTransition = AudioVideoMixer.slidedirectionTransition(
            "input/video1.mp4",
            "input/video2.mp4",
            "output/slide_transition.mp4",
            3
        );
        System.out.println(slideTransition);
        System.out.println();
        
        // 9.3.3 翻书转场动画
        System.out.println("翻书转场:");
        String pageFlip = AudioVideoMixer.pageFlipTransition(
            "input/video1.mp4",
            "input/video2.mp4",
            "output/page_flip.mp4"
        );
        System.out.println(pageFlip);
        System.out.println();
    }
    
    /**
     * 9.4 实战项目：翻书转场动画
     */
    private static void demonstratePageFlipProject() {
        System.out.println("--- 9.4 实战项目：翻书转场动画 ---");
        
        // 多视频同步处理
        String[] videoFiles = {
            "input/scene1.mp4",
            "input/scene2.mp4",
            "input/scene3.mp4",
            "input/scene4.mp4"
        };
        
        // 网格布局
        String gridLayout = AudioVideoMixer.multiVideoSync(
            videoFiles, "output/grid_layout.mp4", "grid"
        );
        System.out.println("网格布局:");
        System.out.println(gridLayout);
        System.out.println();
        
        // 水平堆叠
        String hstackLayout = AudioVideoMixer.multiVideoSync(
            videoFiles, "output/hstack_layout.mp4", "hstack"
        );
        System.out.println("水平堆叠:");
        System.out.println(hstackLayout);
        System.out.println();
        
        // 垂直堆叠
        String vstackLayout = AudioVideoMixer.multiVideoSync(
            videoFiles, "output/vstack_layout.mp4", "vstack"
        );
        System.out.println("垂直堆叠:");
        System.out.println(vstackLayout);
        System.out.println();
    }
    
    /**
     * 创建复杂的多视频混音项目
     */
    public static void createComplexMixProject() {
        System.out.println("--- 复杂多视频混音项目 ---");
        
        // 混合多个音频文件
        String[] audioTracks = {
            "input/narration.mp3",
            "input/background_music.mp3",
            "input/sound_effects.mp3"
        };
        double[] audioWeights = {1.0, 0.3, 0.5};
        
        String mixedAudio = AudioVideoMixer.mixMultipleAudio(
            audioTracks, "output/final_mix.mp3", audioWeights
        );
        System.out.println("混合多个音频轨道:");
        System.out.println(mixedAudio);
        System.out.println();
        
        // 创建带背景音乐的视频
        String videoWithAudio = AudioVideoMixer.addBackgroundMusic(
            "input/main_video.mp4",
            "output/final_mix.mp3",
            "output/final_video_with_audio.mp4",
            0.8
        );
        System.out.println("添加混音到视频:");
        System.out.println(videoWithAudio);
        System.out.println();
    }
    
    /**
     * 批量转场处理
     */
    public static void batchTransitionProcess() {
        System.out.println("--- 批量转场处理 ---");
        
        String[] videoClips = {
            "input/clip1.mp4",
            "input/clip2.mp4",
            "input/clip3.mp4",
            "input/clip4.mp4"
        };
        
        for (int i = 0; i < videoClips.length - 1; i++) {
            String transitionVideo = AudioVideoMixer.fadeTransition(
                videoClips[i],
                videoClips[i + 1],
                String.format("output/transition_%d_%d.mp4", i + 1, i + 2),
                2
            );
            System.out.printf("转场 %d -> %d:%n%s%n%n", i + 1, i + 2, transitionVideo);
        }
    }
}