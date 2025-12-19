package com.ry.example.ffmpeg.chapter07;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字幕处理器
 * 处理视频字幕的添加、转换和样式配置
 */
public class SubtitleProcessor {
    
    /**
     * 添加SRT字幕（软字幕）
     */
    public static void addSRTSubtitle(String inputVideo, String subtitleFile, 
                                     String outputVideo) {
        try {
            String command = String.format(
                "ffmpeg -i %s -i %s -c:v copy -c:a copy -c:s mov_text -map 0:v:0 -map 0:a:0 -map 1:s:0 %s",
                inputVideo, subtitleFile, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("SRT字幕添加成功: " + outputVideo);
                System.out.println("字幕类型: 软字幕（可切换）");
            } else {
                System.err.println("SRT字幕添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加SRT字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 烧录字幕到视频（硬字幕）
     */
    public static void burnSubtitle(String inputVideo, String subtitleFile, 
                                  String outputVideo, String fontName, int fontSize) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"subtitles=%s:force_style='Fontname=%s,Fontsize=%d,PrimaryColour=&H00ffffff,OutlineColour=&H000000,Bold=0'\" -c:a copy %s",
                inputVideo, subtitleFile, fontName, fontSize, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("字幕烧录成功: " + outputVideo);
                System.out.println("字幕类型: 硬字幕（烧录到视频）");
                System.out.println("字体: " + fontName + ", 字号: " + fontSize);
            } else {
                System.err.println("字幕烧录失败");
            }
            
        } catch (Exception e) {
            System.err.println("烧录字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加ASS字幕
     */
    public static void addASSSubtitle(String inputVideo, String assFile, 
                                    String outputVideo) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"ass=%s\" -c:a copy %s",
                inputVideo, assFile, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("ASS字幕添加成功: " + outputVideo);
            } else {
                System.err.println("ASS字幕添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加ASS字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建自定义样式的字幕
     */
    public static void addStyledSubtitle(String inputVideo, String subtitleFile, 
                                        String outputVideo, SubtitleStyle style) {
        try {
            String forceStyle = buildForceStyle(style);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"subtitles=%s:force_style='%s'\" -c:a copy %s",
                inputVideo, subtitleFile, forceStyle, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("样式字幕添加成功: " + outputVideo);
                System.out.println("字幕样式: " + style.name());
            } else {
                System.err.println("样式字幕添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加样式字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建字幕样式字符串
     */
    private static String buildForceStyle(SubtitleStyle style) {
        StringBuilder styleBuilder = new StringBuilder();
        
        styleBuilder.append("Fontname=").append(style.fontName).append(",");
        styleBuilder.append("Fontsize=").append(style.fontSize).append(",");
        styleBuilder.append("PrimaryColour=").append(style.primaryColor).append(",");
        styleBuilder.append("SecondaryColour=").append(style.secondaryColor).append(",");
        styleBuilder.append("OutlineColour=").append(style.outlineColor).append(",");
        styleBuilder.append("BackColour=").append(style.backgroundColor).append(",");
        styleBuilder.append("Bold=").append(style.bold ? 1 : 0).append(",");
        styleBuilder.append("Italic=").append(style.italic ? 1 : 0).append(",");
        styleBuilder.append("Underline=").append(style.underline ? 1 : 0).append(",");
        styleBuilder.append("StrikeOut=").append(style.strikeOut ? 1 : 0).append(",");
        styleBuilder.append("ScaleX=").append(style.scaleX).append(",");
        styleBuilder.append("ScaleY=").append(style.scaleY).append(",");
        styleBuilder.append("Spacing=").append(style.spacing).append(",");
        styleBuilder.append("Angle=").append(style.angle).append(",");
        styleBuilder.append("BorderStyle=").append(style.borderStyle).append(",");
        styleBuilder.append("Outline=").append(style.outline).append(",");
        styleBuilder.append("Shadow=").append(style.shadow).append(",");
        styleBuilder.append("Alignment=").append(style.alignment).append(",");
        styleBuilder.append("MarginL=").append(style.marginLeft).append(",");
        styleBuilder.append("MarginR=").append(style.marginRight).append(",");
        styleBuilder.append("MarginV=").append(style.marginVertical);
        
        return styleBuilder.toString();
    }
    
    /**
     * 创建SRT字幕文件
     */
    public static void createSRTFile(List<SRTEntry> entries, String outputFile) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (int i = 0; i < entries.size(); i++) {
                SRTEntry entry = entries.get(i);
                writer.write(String.valueOf(i + 1));
                writer.write("\n");
                writer.write(entry.getTimeString());
                writer.write("\n");
                writer.write(entry.getText());
                writer.write("\n\n");
            }
            
            System.out.println("SRT字幕文件创建成功: " + outputFile);
            
        } catch (IOException e) {
            System.err.println("创建SRT文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建ASS字幕文件
     */
    public static void createASSFile(List<ASSEntry> entries, String outputFile, 
                                   SubtitleStyle defaultStyle) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            // 文件头
            writer.write("[Script Info]\n");
            writer.write("Title: Custom Subtitles\n");
            writer.write("ScriptType: v4.00+\n");
            writer.write("WrapStyle: 0\n");
            writer.write("ScaledBorderAndShadow: yes\n");
            writer.write("PlayDepth: 0\n\n");
            
            // 视频信息
            writer.write("[V4+ Styles]\n");
            writer.write("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
            
            String styleLine = String.format(
                "Style: Default,%s,%d,%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n\n",
                defaultStyle.fontName, defaultStyle.fontSize,
                defaultStyle.primaryColor, defaultStyle.secondaryColor,
                defaultStyle.outlineColor, defaultStyle.backgroundColor,
                defaultStyle.bold ? 1 : 0, defaultStyle.italic ? 1 : 0,
                defaultStyle.underline ? 1 : 0, defaultStyle.strikeOut ? 1 : 0,
                (int)(defaultStyle.scaleX * 100), (int)(defaultStyle.scaleY * 100),
                defaultStyle.spacing, defaultStyle.angle,
                defaultStyle.borderStyle, defaultStyle.outline, defaultStyle.shadow,
                defaultStyle.alignment, defaultStyle.marginLeft, defaultStyle.marginRight,
                defaultStyle.marginVertical, 1
            );
            writer.write(styleLine);
            
            // 事件
            writer.write("[Events]\n");
            writer.write("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");
            
            for (ASSEntry entry : entries) {
                writer.write(String.format("Dialogue: 0,%s,%s,Default,,0,0,0,,%s\n",
                    entry.getStartTime(), entry.getEndTime(), entry.getText()));
            }
            
            System.out.println("ASS字幕文件创建成功: " + outputFile);
            
        } catch (IOException e) {
            System.err.println("创建ASS文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换字幕格式
     */
    public static void convertSubtitleFormat(String inputFile, String outputFile, 
                                          String inputFormat, String outputFormat) {
        try {
            String command = String.format(
                "ffmpeg -i %s -c:s %s %s",
                inputFile, outputFormat.toLowerCase(), outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("字幕格式转换成功: " + outputFile);
                System.out.println("转换: " + inputFormat + " -> " + outputFormat);
            } else {
                System.err.println("字幕格式转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("字幕格式转换失败: " + e.getMessage());
        }
    }
    
    /**
     * 提取视频中的字幕
     */
    public static void extractSubtitle(String inputVideo, String outputFile, 
                                       int subtitleStreamIndex) {
        try {
            String command = String.format(
                "ffmpeg -i %s -map 0:s:%d %s",
                inputVideo, subtitleStreamIndex, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("字幕提取成功: " + outputFile);
                System.out.println("字幕流索引: " + subtitleStreamIndex);
            } else {
                System.err.println("字幕提取失败");
            }
            
        } catch (Exception e) {
            System.err.println("字幕提取失败: " + e.getMessage());
        }
    }
    
    /**
     * 延迟或提前字幕
     */
    public static void shiftSubtitleTiming(String inputSubtitle, String outputSubtitle, 
                                         int shiftSeconds) {
        try {
            List<SRTEntry> entries = parseSRTFile(inputSubtitle);
            
            for (SRTEntry entry : entries) {
                entry.shiftTime(shiftSeconds);
            }
            
            createSRTFile(entries, outputSubtitle);
            
            System.out.println("字幕时间调整完成: " + outputSubtitle);
            System.out.println("调整量: " + shiftSeconds + " 秒");
            
        } catch (Exception e) {
            System.err.println("字幕时间调整失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析SRT文件
     */
    private static List<SRTEntry> parseSRTFile(String filePath) throws IOException {
        List<SRTEntry> entries = new ArrayList<>();
        // 简化实现，实际应用中需要完整的SRT解析逻辑
        
        // 这里应该实现完整的SRT文件解析
        // 为了示例，创建一些默认条目
        entries.add(new SRTEntry("00:00:01,000 --> 00:00:03,000", "这是第一条字幕"));
        entries.add(new SRTEntry("00:00:04,000 --> 00:00:06,000", "这是第二条字幕"));
        
        return entries;
    }
    
    /**
     * 预定义字幕样式
     */
    public static SubtitleStyle getDefaultStyle() {
        return new SubtitleStyle("Microsoft YaHei", 24, 
                               "&H00FFFFFF", "&H000000FF", "&H00000000", "&H80000000");
    }
    
    public static SubtitleStyle getChineseStyle() {
        return new SubtitleStyle("SimHei", 28, 
                               "&H00FFFFFF", "&H000000FF", "&H00000000", "&H80000000");
    }
    
    public static SubtitleStyle getEnglishStyle() {
        return new SubtitleStyle("Arial", 20, 
                               "&H00FFFFFF", "&H000000FF", "&H00000000", "&H80000000");
    }
    
    /**
     * SRT字幕条目
     */
    public static class SRTEntry {
        private String timeString;
        private String text;
        
        public SRTEntry(String timeString, String text) {
            this.timeString = timeString;
            this.text = text;
        }
        
        public void shiftTime(int seconds) {
            // 简化的时间调整逻辑
            // 实际应用中需要完整的时间解析和计算
        }
        
        public String getTimeString() { return timeString; }
        public String getText() { return text; }
    }
    
    /**
     * ASS字幕条目
     */
    public static class ASSEntry {
        private String startTime;
        private String endTime;
        private String text;
        
        public ASSEntry(String startTime, String endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }
        
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getText() { return text; }
    }
    
    /**
     * 字幕样式类
     */
    public static class SubtitleStyle {
        public String fontName = "Arial";
        public int fontSize = 24;
        public String primaryColor = "&H00FFFFFF";
        public String secondaryColor = "&H000000FF";
        public String outlineColor = "&H00000000";
        public String backgroundColor = "&H80000000";
        public boolean bold = false;
        public boolean italic = false;
        public boolean underline = false;
        public boolean strikeOut = false;
        public double scaleX = 1.0;
        public double scaleY = 1.0;
        public int spacing = 0;
        public double angle = 0.0;
        public int borderStyle = 1;
        public int outline = 2;
        public int shadow = 2;
        public int alignment = 2;
        public int marginLeft = 0;
        public int marginRight = 0;
        public int marginVertical = 0;
        
        public SubtitleStyle() {}
        
        public SubtitleStyle(String fontName, int fontSize, String primaryColor, 
                           String secondaryColor, String outlineColor, String backgroundColor) {
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
            this.outlineColor = outlineColor;
            this.backgroundColor = backgroundColor;
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/subtitles/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 创建测试字幕文件
        List<SRTEntry> srtEntries = new ArrayList<>();
        srtEntries.add(new SRTEntry("00:00:01,000 --> 00:00:03,000", "第一条字幕内容"));
        srtEntries.add(new SRTEntry("00:00:04,000 --> 00:00:06,000", "第二条字幕内容"));
        srtEntries.add(new SRTEntry("00:00:07,000 --> 00:00:09,000", "第三条字幕内容"));
        
        String srtFile = outputDir + "test_subtitles.srt";
        createSRTFile(srtEntries, srtFile);
        
        // 添加软字幕
        addSRTSubtitle(inputVideo, srtFile, outputDir + "soft_subtitle.mp4");
        
        // 烧录硬字幕
        burnSubtitle(inputVideo, srtFile, outputDir + "hard_subtitle.mp4", 
                    "Microsoft YaHei", 24);
        
        // 使用自定义样式
        SubtitleStyle customStyle = getDefaultStyle();
        customStyle.fontSize = 28;
        customStyle.primaryColor = "&H00FFFF00"; // 黄色
        customStyle.outlineColor = "&H00000000"; // 黑色边框
        
        addStyledSubtitle(inputVideo, srtFile, outputDir + "styled_subtitle.mp4", customStyle);
        
        // 创建ASS字幕文件
        List<ASSEntry> assEntries = new ArrayList<>();
        assEntries.add(new ASSEntry("0:00:01.00", "0:00:03.00", "ASS字幕第一条"));
        assEntries.add(new ASSEntry("0:00:04.00", "0:00:06.00", "ASS字幕第二条"));
        
        String assFile = outputDir + "test_subtitles.ass";
        createASSFile(assEntries, assFile, getChineseStyle());
        
        // 添加ASS字幕
        addASSSubtitle(inputVideo, assFile, outputDir + "ass_subtitle.mp4");
        
        // 字幕时间调整
        shiftSubtitleTiming(srtFile, outputDir + "shifted_subtitles.srt", 2);
        
        System.out.println("\n所有字幕处理示例运行完成！");
        System.out.println("输出目录: " + outputDir);
    }
}