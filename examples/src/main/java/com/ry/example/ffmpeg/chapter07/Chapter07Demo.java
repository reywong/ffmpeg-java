package com.ry.example.ffmpeg.chapter07;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 第7章综合演示类
 * 演示FFmpeg添加图文的所有功能
 */
public class Chapter07Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第7章 FFmpeg添加图文 - 综合演示 ===\n");
        
        String inputVideo = "input.mp4";
        String watermarkImage = "logo.png";
        String outputDir = "output/chapter07_demo/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查必要文件
        if (!checkRequiredFiles(inputVideo, watermarkImage)) {
            return;
        }
        
        // 1. 演示图片处理功能
        demonstrateImageProcessing(inputVideo, watermarkImage, outputDir);
        
        // 2. 演示文本添加功能
        demonstrateTextAddition(inputVideo, outputDir);
        
        // 3. 演示字幕处理功能
        demonstrateSubtitleProcessing(inputVideo, outputDir);
        
        // 4. 演示卡拉OK项目
        demonstrateKaraokeProject(inputVideo, outputDir);
        
        // 5. 演示高级功能
        demonstrateAdvancedFeatures(inputVideo, outputDir);
        
        System.out.println("\n=== 演示完成 ===");
        System.out.println("所有示例文件已生成到: " + outputDir);
        listGeneratedFiles(outputDir);
    }
    
    /**
     * 检查必要文件是否存在
     */
    private static boolean checkRequiredFiles(String inputVideo, String watermarkImage) {
        boolean hasVideo = new File(inputVideo).exists();
        boolean hasWatermark = new File(watermarkImage).exists();
        
        if (!hasVideo) {
            System.out.println("警告：未找到输入视频文件 " + inputVideo);
            System.out.println("某些演示将跳过，请将测试视频文件放在项目根目录下");
        }
        
        if (!hasWatermark) {
            System.out.println("警告：未找到水印图片 " + watermarkImage);
            System.out.println("水印相关演示将跳过，请将图片文件放在项目根目录下");
        }
        
        return hasVideo || hasWatermark;
    }
    
    /**
     * 演示图片处理功能
     */
    private static void demonstrateImageProcessing(String inputVideo, String watermarkImage, String outputDir) {
        System.out.println("\n--- 7.1 添加图片处理演示 ---");
        
        if (!new File(inputVideo).exists() || !new File(watermarkImage).exists()) {
            System.out.println("跳过图片处理演示（缺少必要文件）");
            return;
        }
        
        // 添加基本水印
        System.out.println("1. 添加基本图片水印");
        ImageTextProcessor.addImageWatermark(inputVideo, watermarkImage, 
            outputDir + "01_basic_watermark.mp4", 10, 10, 0.8f);
        
        // 右下角水印
        System.out.println("2. 添加右下角水印");
        ImageTextProcessor.addWatermarkBottomRight(inputVideo, watermarkImage, 
            outputDir + "02_bottom_right_watermark.mp4", 10, 0.7f);
        
        // 区域模糊
        System.out.println("3. 区域模糊处理");
        ImageTextProcessor.blurRegion(inputVideo, outputDir + "03_blurred_region.mp4", 
            100, 100, 200, 100, 15.0f);
        
        // 移除Logo
        System.out.println("4. 移除Logo");
        ImageTextProcessor.removeLogo(inputVideo, outputDir + "04_logo_removed.mp4", 
            50, 50, 100, 50);
        
        // 生成GIF
        System.out.println("5. 生成高质量GIF");
        ImageTextProcessor.createHighQualityGIF(inputVideo, outputDir + "05_output.gif", 
            10, 320, 5, 3);
    }
    
    /**
     * 演示文本添加功能
     */
    private static void demonstrateTextAddition(String inputVideo, String outputDir) {
        System.out.println("\n--- 7.2 文本添加演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过文本添加演示（缺少视频文件）");
            return;
        }
        
        // 英文文本
        System.out.println("1. 添加英文文本");
        ImageTextProcessor.addEnglishText(inputVideo, outputDir + "06_english_text.mp4", 
            "Hello FFmpeg!", 50, 50, 32, "white", ImageTextProcessor.TextPosition.TOP_LEFT);
        
        // 时间戳
        System.out.println("2. 添加时间戳");
        ImageTextProcessor.addTimestamp(inputVideo, outputDir + "07_timestamp.mp4", 
            10, 10, 24, "yellow");
        
        // 中文文本（如果有字体）
        String chineseFont = "C:/Windows/Fonts/simhei.ttf";
        if (new File(chineseFont).exists()) {
            System.out.println("3. 添加中文文本");
            ImageTextProcessor.addChineseText(inputVideo, outputDir + "08_chinese_text.mp4", 
                "你好，FFmpeg！", chineseFont, 100, 100, 32, "white", true);
            
            // 滚动文本
            System.out.println("4. 添加滚动文本");
            ImageTextProcessor.addScrollingText(inputVideo, outputDir + "09_scrolling_text.mp4", 
                "这是一段滚动的中文文本", chineseFont, 28, "yellow", 900);
        } else {
            System.out.println("3-4. 跳过中文文本演示（缺少中文字体）");
        }
    }
    
    /**
     * 演示字幕处理功能
     */
    private static void demonstrateSubtitleProcessing(String inputVideo, String outputDir) {
        System.out.println("\n--- 7.3 字幕处理演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过字幕处理演示（缺少视频文件）");
            return;
        }
        
        // 创建测试字幕
        System.out.println("1. 创建测试字幕文件");
        List<SubtitleProcessor.SRTEntry> srtEntries = new ArrayList<>();
        srtEntries.add(new SubtitleProcessor.SRTEntry("00:00:01,000 --> 00:00:03,000", "第一条字幕内容"));
        srtEntries.add(new SubtitleProcessor.SRTEntry("00:00:04,000 --> 00:00:06,000", "第二条字幕内容"));
        srtEntries.add(new SubtitleProcessor.SRTEntry("00:00:07,000 --> 00:00:09,000", "第三条字幕内容"));
        
        String srtFile = outputDir + "test_subtitles.srt";
        SubtitleProcessor.createSRTFile(srtEntries, srtFile);
        
        // 添加软字幕
        System.out.println("2. 添加软字幕");
        SubtitleProcessor.addSRTSubtitle(inputVideo, srtFile, 
            outputDir + "10_soft_subtitle.mp4");
        
        // 烧录硬字幕
        System.out.println("3. 烧录硬字幕");
        SubtitleProcessor.burnSubtitle(inputVideo, srtFile, 
            outputDir + "11_hard_subtitle.mp4", "Arial", 24);
        
        // 自定义样式字幕
        System.out.println("4. 添加样式字幕");
        SubtitleProcessor.SubtitleStyle customStyle = SubtitleProcessor.getDefaultStyle();
        customStyle.fontSize = 28;
        customStyle.primaryColor = "&H00FFFF00"; // 黄色
        
        SubtitleProcessor.addStyledSubtitle(inputVideo, srtFile, 
            outputDir + "12_styled_subtitle.mp4", customStyle);
        
        // 创建ASS字幕
        System.out.println("5. 创建ASS字幕");
        List<SubtitleProcessor.ASSEntry> assEntries = new ArrayList<>();
        assEntries.add(new SubtitleProcessor.ASSEntry("0:00:01.00", "0:00:03.00", "ASS字幕第一条"));
        assEntries.add(new SubtitleProcessor.ASSEntry("0:00:04.00", "0:00:06.00", "ASS字幕第二条"));
        
        String assFile = outputDir + "test_subtitles.ass";
        SubtitleProcessor.createASSFile(assEntries, assFile, SubtitleProcessor.getChineseStyle());
        
        SubtitleProcessor.addASSSubtitle(inputVideo, assFile, outputDir + "13_ass_subtitle.mp4");
    }
    
    /**
     * 演示卡拉OK项目
     */
    private static void demonstrateKaraokeProject(String inputVideo, String outputDir) {
        System.out.println("\n--- 7.4 卡拉OK项目演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过卡拉OK演示（缺少视频文件）");
            return;
        }
        
        // 创建卡拉OK歌词
        System.out.println("1. 创建卡拉OK歌词");
        List<KaraokeProject.KaraokeLyric> lyrics = new ArrayList<>();
        lyrics.add(new KaraokeProject.KaraokeLyric("第一句歌词", 0.0, 3.0, KaraokeProject.LyricPosition.CENTER));
        lyrics.add(new KaraokeProject.KaraokeLyric("第二句歌词", 3.5, 6.5, KaraokeProject.LyricPosition.CENTER));
        lyrics.add(new KaraokeProject.KaraokeLyric("第三句歌词", 7.0, 10.0, KaraokeProject.LyricPosition.CENTER));
        lyrics.add(new KaraokeProject.KaraokeLyric("第四句歌词", 10.5, 13.5, KaraokeProject.LyricPosition.CENTER));
        
        // 创建卡拉OK视频
        System.out.println("2. 创建卡拉OK视频");
        KaraokeProject.createKaraokeVideo(inputVideo, outputDir + "14_karaoke_video.mp4", lyrics);
        
        // 多语言卡拉OK
        System.out.println("3. 创建多语言卡拉OK");
        List<KaraokeProject.MultilingualLyric> multilingualLyrics = new ArrayList<>();
        multilingualLyrics.add(new KaraokeProject.MultilingualLyric("你好世界", "Hello World", 0.0, 3.0));
        multilingualLyrics.add(new KaraokeProject.MultilingualLyric("学习编程", "Learn Programming", 3.5, 6.5));
        multilingualLyrics.add(new KaraokeProject.MultilingualLyric("创造未来", "Create Future", 7.0, 10.0));
        
        KaraokeProject.createMultiLanguageKaraoke(inputVideo, outputDir + "15_multilingual_karaoke.mp4", multilingualLyrics);
    }
    
    /**
     * 演示高级功能
     */
    private static void demonstrateAdvancedFeatures(String inputVideo, String outputDir) {
        System.out.println("\n--- 高级功能演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过高级功能演示（缺少视频文件）");
            return;
        }
        
        // 批量水印处理
        System.out.println("1. 批量水印处理");
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add(inputVideo);
        inputFiles.add(inputVideo);
        
        ImageTextProcessor.WatermarkConfig config = new ImageTextProcessor.WatermarkConfig(15, 0.6f);
        ImageTextProcessor.batchAddWatermarks(inputFiles, "logo.png", outputDir + "batch_watermarks/", config);
        
        // 字幕时间调整
        System.out.println("2. 字幕时间调整");
        String srtFile = outputDir + "test_subtitles.srt";
        if (new File(srtFile).exists()) {
            SubtitleProcessor.shiftSubtitleTiming(srtFile, outputDir + "shifted_subtitles.srt", 2);
        }
        
        // 综合图文效果
        System.out.println("3. 综合图文效果演示");
        createComprehensiveDemo(inputVideo, outputDir);
    }
    
    /**
     * 创建综合演示效果
     */
    private static void createComprehensiveDemo(String inputVideo, String outputDir) {
        try {
            // 这是一个简化的综合效果演示
            // 实际应用中可以使用更复杂的滤镜链
            String command = String.format(
                "ffmpeg -i %s -vf \"drawtext=text='综合演示':x=w/2-text_w/2:y=50:fontsize=48:fontcolor=white:shadowx=3:shadowy=3:shadowcolor=black\" -c:a copy %s",
                inputVideo, outputDir + "16_comprehensive_demo.mp4");
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("综合演示效果创建成功");
            }
            
        } catch (Exception e) {
            System.err.println("综合演示效果创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 列出生成的文件
     */
    private static void listGeneratedFiles(String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                System.out.println("\n=== 生成的文件列表 ===");
                for (File file : files) {
                    if (file.isFile()) {
                        long sizeKB = file.length() / 1024;
                        System.out.printf("  %-35s %,8d KB%n", file.getName(), sizeKB);
                    } else if (file.isDirectory()) {
                        listDirectoryContents(file, "  ");
                    }
                }
                System.out.println("======================");
            }
        }
    }
    
    /**
     * 递归列出目录内容
     */
    private static void listDirectoryContents(File dir, String prefix) {
        System.out.println(prefix + "📁 " + dir.getName() + "/");
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    long sizeKB = file.length() / 1024;
                    System.out.printf("%s  %-30s %,8d KB%n", prefix, file.getName(), sizeKB);
                }
            }
        }
    }
}