package com.ry.example.ffmpeg.chapter07;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图文处理器
 * 处理视频中的图片、文本和字幕添加功能
 */
public class ImageTextProcessor {
    
    /**
     * 在视频中添加图片水印
     */
    public static void addImageWatermark(String inputVideo, String watermarkImage, 
                                        String outputVideo, int x, int y, float opacity) {
        try {
            String command = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"[0:v][1:v]overlay=x=%d:y=%d:format=auto,alpha=%.2f\" -codec:a copy %s",
                inputVideo, watermarkImage, x, y, opacity, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("图片水印添加成功: " + outputVideo);
                System.out.println("位置: (" + x + ", " + y + "), 透明度: " + opacity);
            } else {
                System.err.println("图片水印添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加图片水印失败: " + e.getMessage());
        }
    }
    
    /**
     * 在右下角添加水印
     */
    public static void addWatermarkBottomRight(String inputVideo, String watermarkImage, 
                                               String outputVideo, int margin, float opacity) {
        try {
            String command = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"[0:v][1:v]overlay=W-w-%d:H-h-%d:format=auto,alpha=%.2f\" -codec:a copy %s",
                inputVideo, watermarkImage, margin, margin, opacity, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("右下角水印添加成功: " + outputVideo);
            } else {
                System.err.println("右下角水印添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加右下角水印失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除视频中的指定区域（模糊处理）
     */
    public static void blurRegion(String inputVideo, String outputVideo, 
                                 int x, int y, int width, int height, float blurStrength) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"boxblur=%d:1:enable='between(x,%d,%d)*between(y,%d,%d)'\" -c:a copy %s",
                inputVideo, (int)blurStrength, x, x + width, y, y + height, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("区域模糊处理成功: " + outputVideo);
                System.out.println("区域: (" + x + ", " + y + ", " + width + ", " + height + ")");
            } else {
                System.err.println("区域模糊处理失败");
            }
            
        } catch (Exception e) {
            System.err.println("区域模糊处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用delogo滤镜清除Logo
     */
    public static void removeLogo(String inputVideo, String outputVideo, 
                                 int x, int y, int width, int height) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"delogo=x=%d:y=%d:w=%d:h=%d:show=0\" -c:a copy %s",
                inputVideo, x, y, width, height, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("Logo清除成功: " + outputVideo);
            } else {
                System.err.println("Logo清除失败");
            }
            
        } catch (Exception e) {
            System.err.println("Logo清除失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成高质量GIF动画
     */
    public static void createHighQualityGIF(String inputVideo, String outputGIF, 
                                           int fps, int width, int startTime, int duration) {
        try {
            // 生成调色板
            String paletteFile = "palette_" + System.currentTimeMillis() + ".png";
            String paletteCommand = String.format(
                "ffmpeg -ss %d -t %d -i %s -vf \"fps=%d,scale=%d:-1:flags=lanczos,palettegen\" %s",
                startTime, duration, inputVideo, fps, width, paletteFile);
            
            Process paletteProcess = Runtime.getRuntime().exec(paletteCommand);
            paletteProcess.waitFor();
            
            if (paletteProcess.exitValue() == 0) {
                // 使用调色板生成GIF
                String gifCommand = String.format(
                    "ffmpeg -ss %d -t %d -i %s -i %s -filter_complex \"fps=%d,scale=%d:-1:flags=lanczos[x];[x][1:v]paletteuse\" %s",
                    startTime, duration, inputVideo, paletteFile, fps, width, outputGIF);
                
                Process gifProcess = Runtime.getRuntime().exec(gifCommand);
                gifProcess.waitFor();
                
                if (gifProcess.exitValue() == 0) {
                    System.out.println("高质量GIF生成成功: " + outputGIF);
                    System.out.println("参数: fps=" + fps + ", width=" + width + ", duration=" + duration + "s");
                } else {
                    System.err.println("GIF生成失败");
                }
            }
            
            // 清理临时文件
            new File(paletteFile).delete();
            
        } catch (Exception e) {
            System.err.println("GIF生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加英文文本
     */
    public static void addEnglishText(String inputVideo, String outputVideo, 
                                    String text, int x, int y, int fontSize, 
                                    String color, TextPosition position) {
        try {
            String positionStr = getTextPosition(position);
            String escapedText = text.replace("'", "\\'").replace(":", "\\:");
            
            String command = String.format(
                "ffmpeg -i %s -vf \"drawtext=text='%s':%s:x=%d:y=%d:fontsize=%d:fontcolor=%s\" -codec:a copy %s",
                inputVideo, escapedText, positionStr, x, y, fontSize, color, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("英文文本添加成功: " + outputVideo);
                System.out.println("文本: \"" + text + "\", 位置: (" + x + ", " + y + ")");
            } else {
                System.err.println("英文文本添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加英文文本失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加中文文本
     */
    public static void addChineseText(String inputVideo, String outputVideo, 
                                    String text, String fontFile, int x, int y, 
                                    int fontSize, String color, boolean addShadow) {
        try {
            String escapedText = text.replace("'", "\\'").replace(":", "\\:");
            String shadowFilter = addShadow ? ":shadowx=2:shadowy=2:shadowcolor=black" : "";
            
            String command = String.format(
                "ffmpeg -i %s -vf \"drawtext=text='%s':fontfile=%s:x=%d:y=%d:fontsize=%d:fontcolor=%s%s\" -codec:a copy %s",
                inputVideo, escapedText, fontFile, x, y, fontSize, color, shadowFilter, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("中文文本添加成功: " + outputVideo);
                System.out.println("文本: \"" + text + "\", 字体: " + fontFile);
            } else {
                System.err.println("中文文本添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加中文文本失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加滚动文本
     */
    public static void addScrollingText(String inputVideo, String outputVideo, 
                                      String text, String fontFile, int fontSize, 
                                      String color, int yPosition) {
        try {
            String escapedText = text.replace("'", "\\'").replace(":", "\\:");
            
            String command = String.format(
                "ffmpeg -i %s -vf \"drawtext=text='%s':fontfile=%s:x=w-tw-10*t:y=%d:fontsize=%d:fontcolor=%s\" -codec:a copy %s",
                inputVideo, escapedText, fontFile, yPosition, fontSize, color, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("滚动文本添加成功: " + outputVideo);
                System.out.println("滚动文本: \"" + text + "\"");
            } else {
                System.err.println("滚动文本添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加滚动文本失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加时间戳
     */
    public static void addTimestamp(String inputVideo, String outputVideo, 
                                   int x, int y, int fontSize, String color) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"drawtext=text='时间\: %%{pts\\:hms}':x=%d:y=%d:fontsize=%d:fontcolor=%s\" -codec:a copy %s",
                inputVideo, x, y, fontSize, color, outputVideo);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("时间戳添加成功: " + outputVideo);
            } else {
                System.err.println("时间戳添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加时间戳失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文本位置配置
     */
    private static String getTextPosition(TextPosition position) {
        switch (position) {
            case CENTER:
                return "x=(W-w)/2:y=(H-h)/2";
            case TOP_LEFT:
                return "x=10:y=10";
            case TOP_RIGHT:
                return "x=W-w-10:y=10";
            case BOTTOM_LEFT:
                return "x=10:y=H-h-10";
            case BOTTOM_RIGHT:
                return "x=W-w-10:y=H-h-10";
            default:
                return "";
        }
    }
    
    /**
     * 批量添加水印
     */
    public static void batchAddWatermarks(List<String> inputVideos, String watermarkImage, 
                                         String outputDir, WatermarkConfig config) {
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        for (int i = 0; i < inputVideos.size(); i++) {
            String inputFile = inputVideos.get(i);
            String outputFile = outputDir + "/watermarked_" + (i + 1) + ".mp4";
            
            addWatermarkBottomRight(inputFile, watermarkImage, outputFile, 
                                   config.margin, config.opacity);
        }
        
        System.out.println("批量水印添加完成，输出目录: " + outputDir);
    }
    
    /**
     * 文本位置枚举
     */
    public enum TextPosition {
        CUSTOM, CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
    
    /**
     * 水印配置类
     */
    public static class WatermarkConfig {
        public int margin = 10;
        public float opacity = 0.8f;
        
        public WatermarkConfig() {}
        
        public WatermarkConfig(int margin, float opacity) {
            this.margin = margin;
            this.opacity = opacity;
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String watermarkImage = "logo.png";
        String outputDir = "output/image_text/";
        String fontFile = "C:/Windows/Fonts/simhei.ttf"; // Windows中文字体
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查必要文件是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        if (!new File(watermarkImage).exists()) {
            System.out.println("提示：需要提供水印图片 " + watermarkImage);
            System.out.println("请将水印图片文件放在项目根目录下");
        }
        
        // 添加图片水印
        addImageWatermark(inputVideo, watermarkImage, outputDir + "image_watermark.mp4", 
                         10, 10, 0.8f);
        
        // 右下角水印
        addWatermarkBottomRight(inputVideo, watermarkImage, 
                               outputDir + "watermark_bottom_right.mp4", 10, 0.7f);
        
        // 清除区域
        blurRegion(inputVideo, outputDir + "blurred_region.mp4", 
                  100, 100, 200, 100, 15.0f);
        
        // 移除Logo
        removeLogo(inputVideo, outputDir + "logo_removed.mp4", 
                  50, 50, 100, 50);
        
        // 生成GIF
        createHighQualityGIF(inputVideo, outputDir + "output.gif", 
                            10, 320, 5, 3);
        
        // 添加英文文本
        addEnglishText(inputVideo, outputDir + "english_text.mp4", 
                      "Hello FFmpeg!", 50, 50, 32, "white", TextPosition.TOP_LEFT);
        
        // 添加时间戳
        addTimestamp(inputVideo, outputDir + "timestamp.mp4", 
                    10, 10, 24, "yellow");
        
        // 添加中文文本（如果有中文字体文件）
        if (new File(fontFile).exists()) {
            addChineseText(inputVideo, outputDir + "chinese_text.mp4", 
                          "你好，FFmpeg！", fontFile, 100, 100, 32, "white", true);
            
            // 添加滚动中文文本
            addScrollingText(inputVideo, outputDir + "scrolling_chinese.mp4", 
                           "这是一段滚动的中文文本", fontFile, 28, "yellow", H-100);
        } else {
            System.out.println("提示：未找到中文字体文件 " + fontFile);
            System.out.println("如需测试中文文本，请提供有效的中文字体文件路径");
        }
        
        // 批量处理
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add(inputVideo);
        inputFiles.add(inputVideo);
        
        WatermarkConfig config = new WatermarkConfig(15, 0.6f);
        batchAddWatermarks(inputFiles, watermarkImage, outputDir + "batch/", config);
        
        System.out.println("\n所有图文处理示例运行完成！");
        System.out.println("输出目录: " + outputDir);
    }
}