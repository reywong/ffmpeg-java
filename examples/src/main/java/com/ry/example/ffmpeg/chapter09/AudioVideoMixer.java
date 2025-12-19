package com.ry.example.ffmpeg.chapter09;

/**
 * 音视频混合处理器
 * 涵盖多路音频、多路视频、转场动画等功能
 */
public class AudioVideoMixer {
    
    /**
     * 多路音频混音
     * @param mainAudio 主音频文件路径
     * @param backgroundAudio 背景音频文件路径
     * @param outputFile 输出文件路径
     * @param backgroundVolume 背景音量比例 (0.0-1.0)
     * @return 混音FFmpeg命令
     */
    public static String mixAudio(String mainAudio, String backgroundAudio, String outputFile, double backgroundVolume) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[0:a][1:a]amix=inputs=2:weights=1 %.2f[a]\" -map \"[a]\" -c:a libmp3lame -q:a 2 \"%s\"",
            mainAudio, backgroundAudio, backgroundVolume, outputFile
        );
    }
    
    /**
     * 多通道音频混音
     * @param audioFiles 音频文件数组
     * @param outputFile 输出文件路径
     * @param weights 权重数组
     * @return 混音FFmpeg命令
     */
    public static String mixMultipleAudio(String[] audioFiles, String outputFile, double[] weights) {
        StringBuilder inputs = new StringBuilder();
        StringBuilder filterComplex = new StringBuilder();
        
        // 构建输入文件
        for (int i = 0; i < audioFiles.length; i++) {
            inputs.append(String.format("-i \"%s\" ", audioFiles[i]));
        }
        
        // 构建滤镜链
        filterComplex.append("[0:a]");
        for (int i = 1; i < audioFiles.length; i++) {
            filterComplex.append(String.format("[%d:a]", i));
        }
        filterComplex.append(String.format("amix=inputs=%d:weights=", audioFiles.length));
        
        for (int i = 0; i < weights.length; i++) {
            if (i > 0) filterComplex.append(" ");
            filterComplex.append(weights[i]);
        }
        filterComplex.append("[a]");
        
        return String.format(
            "ffmpeg %s -filter_complex \"%s\" -map \"[a]\" -c:a libmp3lame -q:a 2 \"%s\"",
            inputs.toString(), filterComplex.toString(), outputFile
        );
    }
    
    /**
     * 添加背景音乐到视频
     * @param videoFile 视频文件路径
     * @param backgroundMusic 背景音乐文件路径
     * @param outputFile 输出文件路径
     * @param musicVolume 背景音乐音量
     * @return 添加背景音乐的FFmpeg命令
     */
    public static String addBackgroundMusic(String videoFile, String backgroundMusic, String outputFile, double musicVolume) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[1:a]volume=%.2f[music];[0:a][music]amix=inputs=2:duration=first:dropout_transition=2[a]\" -map 0:v -map \"[a]\" -c:v copy -c:a aac -b:a 192k -shortest \"%s\"",
            videoFile, backgroundMusic, musicVolume, outputFile
        );
    }
    
    /**
     * 画中画效果
     * @param mainVideo 主视频文件路径
     * @param subVideo 子视频文件路径
     * @param outputFile 输出文件路径
     * @param x 子视频X坐标
     * @param y 子视频Y坐标
     * @param subWidth 子视频宽度
     * @param subHeight 子视频高度
     * @return 画中画FFmpeg命令
     */
    public static String pictureInPicture(String mainVideo, String subVideo, String outputFile, 
                                        int x, int y, int subWidth, int subHeight) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[1:v]scale=%d:%d[sub];[0:v][sub]overlay=%d:%d[v]\" -map \"[v]\" -map 0:a? -c:v libx264 -preset fast -crf 23 -c:a copy \"%s\"",
            mainVideo, subVideo, subWidth, subHeight, x, y, outputFile
        );
    }
    
    /**
     * 四宫格效果
     * @param topLeftVideo 左上角视频路径
     * @param topRightVideo 右上角视频路径
     * @param bottomLeftVideo 左下角视频路径
     * @param bottomRightVideo 右下角视频路径
     * @param outputFile 输出文件路径
     * @param gridSize 网格大小
     * @return 四宫格FFmpeg命令
     */
    public static String fourGrid(String topLeftVideo, String topRightVideo, String bottomLeftVideo, 
                                String bottomRightVideo, String outputFile, int gridSize) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -i \"%s\" -i \"%s\" " +
            "-filter_complex \"[0:v]scale=%d:%d[pad1];[1:v]scale=%d:%d[pad2];[2:v]scale=%d:%d[pad3];[3:v]scale=%d:%d[pad4];" +
            "[pad1][pad2]hstack=2[top];[pad3][pad4]hstack=2[bottom];[top][bottom]vstack=2[v]\" " +
            "-map \"[v]\" -c:v libx264 -preset fast -crf 23 -pix_fmt yuv420p \"%s\"",
            topLeftVideo, topRightVideo, bottomLeftVideo, bottomRightVideo,
            gridSize, gridSize, gridSize, gridSize, gridSize, gridSize, gridSize, gridSize, gridSize, gridSize,
            outputFile
        );
    }
    
    /**
     * 透视混合效果
     * @param video1 第一个视频路径
     * @param video2 第二个视频路径
     * @param outputFile 输出文件路径
     * @return 透视混合FFmpeg命令
     */
    public static String perspectiveBlend(String video1, String video2, String outputFile) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[0:v][1:v]blend=all_mode=multiply\" -c:v libx264 -preset fast -crf 23 \"%s\"",
            video1, video2, outputFile
        );
    }
    
    /**
     * 转场动画 - 淡入淡出
     * @param video1 第一个视频路径
     * @param video2 第二个视频路径
     * @param outputFile 输出文件路径
     * @param duration 转场持续时间（秒）
     * @return 转场动画FFmpeg命令
     */
    public static String fadeTransition(String video1, String video2, String outputFile, int duration) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[0:v]trim=end=5,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=start=0,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=fade:duration=%d:offset=5[v]\" " +
            "-map \"[v]\" -c:v libx264 -preset fast -crf 23 \"%s\"",
            video1, video2, duration, outputFile
        );
    }
    
    /**
     * 斜边转场动画
     * @param video1 第一个视频路径
     * @param video2 第二个视频路径
     * @param outputFile 输出文件路径
     * @param duration 转场持续时间（秒）
     * @return 斜边转场FFmpeg命令
     */
    public static String slidedirectionTransition(String video1, String video2, String outputFile, int duration) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[0:v]trim=end=10,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=start=0,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=slidedown:duration=%d:offset=10[v]\" " +
            "-map \"[v]\" -c:v libx264 -preset fast -crf 23 \"%s\"",
            video1, video2, duration, outputFile
        );
    }
    
    /**
     * 翻书转场动画
     * @param video1 第一个视频路径
     * @param video2 第二个视频路径
     * @param outputFile 输出文件路径
     * @return 翻书转场FFmpeg命令
     */
    public static String pageFlipTransition(String video1, String video2, String outputFile) {
        return String.format(
            "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"[0:v]trim=end=8,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=start=0,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=pagecurl:duration=2:offset=8[v]\" " +
            "-map \"[v]\" -c:v libx264 -preset fast -crf 23 \"%s\"",
            video1, video2, outputFile
        );
    }
    
    /**
     * 多路视频同步处理
     * @param videoFiles 视频文件数组
     * @param outputFile 输出文件路径
     * @param layout 布局类型 (grid, hstack, vstack)
     * @return 多路视频同步FFmpeg命令
     */
    public static String multiVideoSync(String[] videoFiles, String outputFile, String layout) {
        StringBuilder inputs = new StringBuilder();
        StringBuilder filterComplex = new StringBuilder();
        
        // 构建输入文件
        for (int i = 0; i < videoFiles.length; i++) {
            inputs.append(String.format("-i \"%s\" ", videoFiles[i]));
        }
        
        // 根据布局类型构建滤镜
        if ("grid".equals(layout)) {
            filterComplex.append("[0:v][1:v][2:v][3:v]xstack=inputs=4:layout=0_0|0_h0|w0_0|w0_h0[v]");
        } else if ("hstack".equals(layout)) {
            filterComplex.append("[0:v][1:v][2:v][3:v]hstack=inputs=4[v]");
        } else if ("vstack".equals(layout)) {
            filterComplex.append("[0:v][1:v][2:v][3:v]vstack=inputs=4[v]");
        }
        
        return String.format(
            "ffmpeg %s -filter_complex \"%s\" -map \"[v]\" -c:v libx264 -preset fast -crf 23 -pix_fmt yuv420p \"%s\"",
            inputs.toString(), filterComplex.toString(), outputFile
        );
    }
}