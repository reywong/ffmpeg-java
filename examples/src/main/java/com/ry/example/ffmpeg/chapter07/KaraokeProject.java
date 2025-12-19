package com.ry.example.ffmpeg.chapter07;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实战项目：卡拉OK音乐短片
 * 创建完整的卡拉OK字幕效果
 */
public class KaraokeProject {
    
    /**
     * 创建完整的卡拉OK视频
     */
    public static void createKaraokeVideo(String inputVideo, String outputVideo, 
                                        List<KaraokeLyric> lyrics) {
        try {
            // 生成卡拉OK字幕文件
            String assFile = generateKaraokeASS(lyrics);
            
            // 应用字幕到视频
            String command = String.format(
                "ffmpeg -i %s -vf \"ass=%s\" -c:a copy %s",
                inputVideo, assFile, outputVideo);
            
            System.out.println("正在创建卡拉OK视频...");
            System.out.println("歌词数量: " + lyrics.size());
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("卡拉OK视频创建成功: " + outputVideo);
            } else {
                System.err.println("卡拉OK视频创建失败");
            }
            
            // 清理临时文件
            new File(assFile).delete();
            
        } catch (Exception e) {
            System.err.println("创建卡拉OK视频失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成卡拉OK格式的ASS字幕
     */
    private static String generateKaraokeASS(List<KaraokeLyric> lyrics) {
        StringBuilder assContent = new StringBuilder();
        
        // 文件头
        assContent.append("[Script Info]\n");
        assContent.append("Title: Karaoke Subtitles\n");
        assContent.append("ScriptType: v4.00+\n");
        assContent.append("WrapStyle: 0\n");
        assContent.append("ScaledBorderAndShadow: yes\n");
        assContent.append("PlayDepth: 0\n\n");
        
        // 样式定义
        assContent.append("[V4+ Styles]\n");
        assContent.append("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
        
        // 卡拉OK样式
        assContent.append("Style: KaraokeMain,Microsoft YaHei,48,&H00FFFFFF,&H0000FFFF,&H00000000,&H80000000,1,0,0,0,100,100,0,0,1,3,2,2,0,0,0,1\n");
        assContent.append("Style: KaraokeEffect,Microsoft YaHei,48,&H00FFFF00,&H00FF00FF,&H00000000,&H00000000,1,0,0,0,100,100,0,0,1,3,2,2,0,0,0,1\n");
        assContent.append("Style: KaraokeShadow,Microsoft YaHei,48,&H80808080,&H80008080,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,0,0,2,0,0,0,1\n\n");
        
        // 事件
        assContent.append("[Events]\n");
        assContent.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");
        
        // 生成歌词行
        for (int i = 0; i < lyrics.size(); i++) {
            KaraokeLyric lyric = lyrics.get(i);
            String startTime = formatTime(lyric.getStartTime());
            String endTime = formatTime(lyric.getEndTime());
            
            // 生成卡拉OK效果文本
            String karaokeText = generateKaraokeText(lyric);
            
            assContent.append(String.format("Dialogue: 0,%s,%s,KaraokeMain,,0,0,0,,%s\n", 
                startTime, endTime, karaokeText));
        }
        
        String assFile = "karaoke_" + System.currentTimeMillis() + ".ass";
        try (FileWriter writer = new FileWriter(assFile)) {
            writer.write(assContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return assFile;
    }
    
    /**
     * 生成卡拉OK效果文本
     */
    private static String generateKaraokeText(KaraokeLyric lyric) {
        StringBuilder karaokeText = new StringBuilder();
        
        // 背景文本（灰色）
        karaokeText.append("{\\b0").append(getPositionStyle(lyric.getPosition()));
        karaokeText.append(getColorStyle("&H80808080"));
        karaokeText.append("}").append(lyric.getText());
        
        // 彩色文本（从左到右填充）
        karaokeText.append("\\N"); // 换行
        karaokeText.append("{\\b1").append(getPositionStyle(lyric.getPosition()));
        karaokeText.append(getKaraokeProgress(lyric));
        karaokeText.append("}");
        
        return karaokeText.toString();
    }
    
    /**
     * 生成卡拉OK进度效果
     */
    private static String getKaraokeProgress(KaraokeLyric lyric) {
        // 简化的卡拉OK效果，使用\\k标记
        StringBuilder progress = new StringBuilder();
        
        String text = lyric.getText();
        int textLength = text.length();
        int duration = lyric.getDuration();
        
        // 计算每个字符的时间
        int charDuration = duration / textLength;
        
        for (int i = 0; i < textLength; i++) {
            progress.append("{\\k").append(charDuration / 10).append("}");
            progress.append(text.charAt(i));
        }
        
        return progress.toString();
    }
    
    /**
     * 获取位置样式
     */
    private static String getPositionStyle(LyricPosition position) {
        switch (position) {
            case TOP:
                return "\\pos(960,100)";
            case CENTER:
                return "\\pos(960,540)";
            case BOTTOM:
                return "\\pos(960,900)";
            default:
                return "\\pos(960,540)";
        }
    }
    
    /**
     * 获取颜色样式
     */
    private static String getColorStyle(String color) {
        return "\\c" + color;
    }
    
    /**
     * 格式化时间
     */
    private static String formatTime(double timeInSeconds) {
        int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) ((timeInSeconds % 3600) / 60);
        int seconds = (int) (timeInSeconds % 60);
        int milliseconds = (int) ((timeInSeconds % 1) * 100);
        
        return String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds);
    }
    
    /**
     * 创建音乐视频（音频+歌词背景）
     */
    public static void createMusicVideo(String audioFile, String backgroundImage, 
                                      String outputVideo, List<KaraokeLyric> lyrics) {
        try {
            // 创建带字幕的视频
            String assFile = generateKaraokeASS(lyrics);
            
            String command = String.format(
                "ffmpeg -loop 1 -i %s -i %s -vf \"ass=%s\" -c:v libx264 -t %.2f -pix_fmt yuv420p %s",
                backgroundImage, audioFile, assFile, 
                lyrics.get(lyrics.size() - 1).getEndTime(), outputVideo);
            
            System.out.println("正在创建音乐视频...");
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("音乐视频创建成功: " + outputVideo);
            } else {
                System.err.println("音乐视频创建失败");
            }
            
            // 清理临时文件
            new File(assFile).delete();
            
        } catch (Exception e) {
            System.err.println("创建音乐视频失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建多语言卡拉OK字幕
     */
    public static void createMultiLanguageKaraoke(String inputVideo, String outputVideo, 
                                                 List<MultilingualLyric> lyrics) {
        try {
            String assFile = generateMultiLanguageASS(lyrics);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"ass=%s\" -c:a copy %s",
                inputVideo, assFile, outputVideo);
            
            System.out.println("正在创建多语言卡拉OK视频...");
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("多语言卡拉OK视频创建成功: " + outputVideo);
            } else {
                System.err.println("多语言卡拉OK视频创建失败");
            }
            
            new File(assFile).delete();
            
        } catch (Exception e) {
            System.err.println("创建多语言卡拉OK视频失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成多语言ASS字幕
     */
    private static String generateMultiLanguageASS(List<MultilingualLyric> lyrics) {
        StringBuilder assContent = new StringBuilder();
        
        // 文件头
        assContent.append("[Script Info]\n");
        assContent.append("Title: Multi-language Karaoke\n");
        assContent.append("ScriptType: v4.00+\n\n");
        
        // 样式定义
        assContent.append("[V4+ Styles]\n");
        assContent.append("Format: Name, Fontname, Fontsize, PrimaryColour, OutlineColour, BackColour, Alignment, MarginV\n");
        assContent.append("Style: Chinese,Microsoft YaHei,36,&H00FFFFFF,&H00000000,&H80000000,2,30\n");
        assContent.append("Style: English,Arial,32,&H00FFFF00,&H00000000,&H80000000,8,30\n\n");
        
        // 事件
        assContent.append("[Events]\n");
        assContent.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");
        
        for (MultilingualLyric lyric : lyrics) {
            String startTime = formatTime(lyric.getStartTime());
            String endTime = formatTime(lyric.getEndTime());
            
            // 中文歌词
            assContent.append(String.format("Dialogue: 0,%s,%s,Chinese,,0,0,0,,%s\n", 
                startTime, endTime, lyric.getChineseText()));
            
            // 英文歌词
            assContent.append(String.format("Dialogue: 0,%s,%s,English,,0,0,0,,%s\n", 
                startTime, endTime, lyric.getEnglishText()));
        }
        
        String assFile = "multilingual_karaoke_" + System.currentTimeMillis() + ".ass";
        try (FileWriter writer = new FileWriter(assFile)) {
            writer.write(assContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return assFile;
    }
    
    /**
     * 分析音频节奏并同步歌词
     */
    public static List<KaraokeLyric> analyzeAudioAndSync(String audioFile, 
                                                        List<String> lyrics) {
        List<KaraokeLyric> syncedLyrics = new ArrayList<>();
        
        try {
            // 简化实现：平均分配时间
            // 实际应用中应该使用音频分析库
            String command = String.format(
                "ffprobe -v quiet -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 %s",
                audioFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            double totalDuration = 5.0; // 默认5秒
            try {
                String durationStr = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())).readLine();
                totalDuration = Double.parseDouble(durationStr.trim());
            } catch (Exception e) {
                System.err.println("无法获取音频时长，使用默认值");
            }
            
            double segmentDuration = totalDuration / lyrics.size();
            
            for (int i = 0; i < lyrics.size(); i++) {
                double startTime = i * segmentDuration;
                double endTime = (i + 1) * segmentDuration;
                
                syncedLyrics.add(new KaraokeLyric(
                    lyrics.get(i), 
                    startTime, 
                    endTime, 
                    LyricPosition.CENTER
                ));
            }
            
            System.out.println("音频分析完成，同步了 " + syncedLyrics.size() + " 行歌词");
            
        } catch (Exception e) {
            System.err.println("音频分析失败: " + e.getMessage());
        }
        
        return syncedLyrics;
    }
    
    /**
     * 卡拉OK歌词类
     */
    public static class KaraokeLyric {
        private String text;
        private double startTime;
        private double endTime;
        private LyricPosition position;
        
        public KaraokeLyric(String text, double startTime, double endTime, LyricPosition position) {
            this.text = text;
            this.startTime = startTime;
            this.endTime = endTime;
            this.position = position;
        }
        
        public String getText() { return text; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
        public LyricPosition getPosition() { return position; }
        public int getDuration() { return (int) (endTime - startTime); }
    }
    
    /**
     * 多语言歌词类
     */
    public static class MultilingualLyric {
        private String chineseText;
        private String englishText;
        private double startTime;
        private double endTime;
        
        public MultilingualLyric(String chineseText, String englishText, 
                                 double startTime, double endTime) {
            this.chineseText = chineseText;
            this.englishText = englishText;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public String getChineseText() { return chineseText; }
        public String getEnglishText() { return englishText; }
        public double getStartTime() { return startTime; }
        public double getEndTime() { return endTime; }
    }
    
    /**
     * 歌词位置枚举
     */
    public enum LyricPosition {
        TOP, CENTER, BOTTOM
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String audioFile = "background_music.mp3";
        String backgroundImage = "background.jpg";
        String outputDir = "output/karaoke/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入文件
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 创建测试歌词
        List<KaraokeLyric> lyrics = new ArrayList<>();
        lyrics.add(new KaraokeLyric("第一句歌词", 0.0, 3.0, LyricPosition.CENTER));
        lyrics.add(new KaraokeLyric("第二句歌词", 3.5, 6.5, LyricPosition.CENTER));
        lyrics.add(new KaraokeLyric("第三句歌词", 7.0, 10.0, LyricPosition.CENTER));
        lyrics.add(new KaraokeLyric("第四句歌词", 10.5, 13.5, LyricPosition.CENTER));
        lyrics.add(new KaraokeLyric("第五句歌词", 14.0, 17.0, LyricPosition.CENTER));
        
        // 创建卡拉OK视频
        createKaraokeVideo(inputVideo, outputDir + "karaoke_video.mp4", lyrics);
        
        // 创建多语言歌词
        List<MultilingualLyric> multilingualLyrics = new ArrayList<>();
        multilingualLyrics.add(new MultilingualLyric("你好世界", "Hello World", 0.0, 3.0));
        multilingualLyrics.add(new MultilingualLyric("学习编程", "Learn Programming", 3.5, 6.5));
        multilingualLyrics.add(new MultilingualLyric("创造未来", "Create Future", 7.0, 10.0));
        
        createMultiLanguageKaraoke(inputVideo, outputDir + "multilingual_karaoke.mp4", multilingualLyrics);
        
        // 如果有音频文件，创建音乐视频
        if (new File(audioFile).exists() && new File(backgroundImage).exists()) {
            createMusicVideo(audioFile, backgroundImage, outputDir + "music_video.mp4", lyrics);
        } else {
            System.out.println("提示：需要音频文件 " + audioFile + " 和背景图片 " + backgroundImage + " 来创建音乐视频");
        }
        
        // 音频分析和歌词同步示例
        List<String> simpleLyrics = List.of("歌词1", "歌词2", "歌词3", "歌词4", "歌词5");
        if (new File(audioFile).exists()) {
            List<KaraokeLyric> syncedLyrics = analyzeAudioAndSync(audioFile, simpleLyrics);
            if (!syncedLyrics.isEmpty()) {
                createKaraokeVideo(inputVideo, outputDir + "synced_karaoke.mp4", syncedLyrics);
            }
        }
        
        System.out.println("\n所有卡拉OK项目示例运行完成！");
        System.out.println("输出目录: " + outputDir);
    }
}