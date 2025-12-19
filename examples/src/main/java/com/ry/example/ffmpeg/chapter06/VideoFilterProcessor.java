package com.ry.example.ffmpeg.chapter06;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频滤镜处理器
 * 处理各种视频滤镜效果
 */
public class VideoFilterProcessor {
    
    /**
     * 应用色彩调整滤镜
     */
    public static void applyColorAdjustment(String inputFile, String outputFile, 
                                          double brightness, double contrast, double saturation) {
        try {
            String filter = String.format("eq=brightness=%.2f:contrast=%.2f:saturation=%.2f", 
                                        brightness, contrast, saturation);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("色彩调整完成: " + outputFile);
                System.out.println("亮度: " + brightness + ", 对比度: " + contrast + ", 饱和度: " + saturation);
            } else {
                System.err.println("色彩调整失败");
            }
            
        } catch (Exception e) {
            System.err.println("色彩调整异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用图像变换滤镜
     */
    public static void applyImageTransform(String inputFile, String outputFile, 
                                         TransformType transformType) {
        try {
            String filter;
            
            switch (transformType) {
                case HORIZONTAL_FLIP:
                    filter = "hflip";
                    break;
                case VERTICAL_FLIP:
                    filter = "vflip";
                    break;
                case ROTATE_90:
                    filter = "transpose=1";
                    break;
                case ROTATE_180:
                    filter = "transpose=2,transpose=2";
                    break;
                case ROTATE_270:
                    filter = "transpose=2";
                    break;
                default:
                    filter = "hflip";
                    break;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("图像变换完成: " + outputFile);
                System.out.println("变换类型: " + transformType);
            } else {
                System.err.println("图像变换失败");
            }
            
        } catch (Exception e) {
            System.err.println("图像变换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用模糊和锐化滤镜
     */
    public static void applyBlurSharpness(String inputFile, String outputFile, 
                                        FilterType filterType, double strength) {
        try {
            String filter;
            
            switch (filterType) {
                case GAUSSIAN_BLUR:
                    filter = String.format("gblur=sigma=%.2f", strength);
                    break;
                case BOX_BLUR:
                    filter = String.format("boxblur=luma_radius=%.2f:luma_power=2", strength);
                    break;
                case UNSHARP:
                    filter = String.format("unsharp=5:5:%.2f:5:5:0.0", strength);
                    break;
                case EDGE_DETECT:
                    filter = String.format("edgedetect=low=0.1:high=%.2f", strength);
                    break;
                default:
                    filter = "gblur=sigma=2";
                    break;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("滤镜效果应用完成: " + outputFile);
                System.out.println("滤镜类型: " + filterType + ", 强度: " + strength);
            } else {
                System.err.println("滤镜效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("滤镜效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用色彩空间转换
     */
    public static void applyColorSpaceConversion(String inputFile, String outputFile, 
                                               String inputSpace, String outputSpace) {
        try {
            String filter = String.format("colorspace=%s:%s", inputSpace, outputSpace);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("色彩空间转换完成: " + outputFile);
                System.out.println("转换: " + inputSpace + " -> " + outputSpace);
            } else {
                System.err.println("色彩空间转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("色彩空间转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 添加渐变背景
     */
    public static void addGradientBackground(String inputFile, String outputFile, 
                                           String color, int width, int height) {
        try {
            String command = String.format(
                "ffmpeg -f lavfi -i color=c=%s:size=%dx%d -i %s -filter_complex \"[0:v][1:v]overlay=x=(W-w)/2:y=(H-h)/2\" -c:v libx264 -crf 23 -preset veryfast %s",
                color, width, height, inputFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("渐变背景添加完成: " + outputFile);
                System.out.println("背景颜色: " + color + ", 尺寸: " + width + "x" + height);
            } else {
                System.err.println("渐变背景添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("渐变背景添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 色彩替换（绿幕抠图）
     */
    public static void chromaKeyReplacement(String inputFile, String outputFile, 
                                          String color, double similarity, double blend) {
        try {
            String filter = String.format("colorkey=%s:%.2f:%.2f", color, similarity, blend);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("色彩替换完成: " + outputFile);
                System.out.println("颜色: " + color + ", 相似度: " + similarity + ", 混合度: " + blend);
            } else {
                System.err.println("色彩替换失败");
            }
            
        } catch (Exception e) {
            System.err.println("色彩替换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用明暗对比调整
     */
    public static void applyBrightnessContrast(String inputFile, String outputFile, 
                                            double brightness, double contrast, double gamma) {
        try {
            String filter = String.format("eq=brightness=%.2f:contrast=%.2f:gamma=%.2f", 
                                        brightness, contrast, gamma);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("明暗对比调整完成: " + outputFile);
                System.out.println("亮度: " + brightness + ", 对比度: " + contrast + ", Gamma: " + gamma);
            } else {
                System.err.println("明暗对比调整失败");
            }
            
        } catch (Exception e) {
            System.err.println("明暗对比调整异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用淡入淡出效果
     */
    public static void applyFadeEffect(String inputFile, String outputFile, 
                                      FadeType fadeType, int startFrame, int duration) {
        try {
            String filter;
            
            switch (fadeType) {
                case FADE_IN:
                    filter = String.format("fade=in:%d:%d", startFrame, duration);
                    break;
                case FADE_OUT:
                    filter = String.format("fade=out:%d:%d", startFrame, duration);
                    break;
                case FADE_IN_OUT:
                    filter = String.format("fade=in:%d:%d,fade=out:90:%d", startFrame, duration, duration);
                    break;
                default:
                    filter = String.format("fade=in:0:30");
                    break;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("淡入淡出效果应用完成: " + outputFile);
                System.out.println("效果类型: " + fadeType + ", 起始帧: " + startFrame + ", 持续时间: " + duration);
            } else {
                System.err.println("淡入淡出效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("淡入淡出效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量应用滤镜
     */
    public static void batchApplyFilters(List<String> inputFiles, String outputDir, 
                                       FilterConfig config) {
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        for (int i = 0; i < inputFiles.size(); i++) {
            String inputFile = inputFiles.get(i);
            String outputFile = outputDir + "/processed_" + (i + 1) + ".mp4";
            
            // 根据配置应用不同的滤镜
            if (config.brightness != 0 || config.contrast != 0 || config.saturation != 0) {
                applyColorAdjustment(inputFile, outputFile, config.brightness, 
                                   config.contrast, config.saturation);
            } else if (config.transformType != null) {
                applyImageTransform(inputFile, outputFile, config.transformType);
            } else if (config.filterType != null) {
                applyBlurSharpness(inputFile, outputFile, config.filterType, config.strength);
            }
        }
        
        System.out.println("批量滤镜处理完成，输出目录: " + outputDir);
    }
    
    /**
     * 获取视频信息
     */
    public static void getVideoInfo(String videoFile) {
        try {
            String command = String.format(
                "ffprobe -v quiet -print_format json -show_format -show_streams %s", videoFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                System.out.println("=== 视频信息: " + videoFile + " ===");
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取视频信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 变换类型枚举
     */
    public enum TransformType {
        HORIZONTAL_FLIP, VERTICAL_FLIP, ROTATE_90, ROTATE_180, ROTATE_270
    }
    
    /**
     * 滤镜类型枚举
     */
    public enum FilterType {
        GAUSSIAN_BLUR, BOX_BLUR, UNSHARP, EDGE_DETECT
    }
    
    /**
     * 淡入淡出类型枚举
     */
    public enum FadeType {
        FADE_IN, FADE_OUT, FADE_IN_OUT
    }
    
    /**
     * 滤镜配置类
     */
    public static class FilterConfig {
        public double brightness = 0;
        public double contrast = 0;
        public double saturation = 0;
        public TransformType transformType = null;
        public FilterType filterType = null;
        public double strength = 1.0;
        
        public FilterConfig() {}
        
        public FilterConfig(double brightness, double contrast, double saturation) {
            this.brightness = brightness;
            this.contrast = contrast;
            this.saturation = saturation;
        }
        
        public FilterConfig(TransformType transformType) {
            this.transformType = transformType;
        }
        
        public FilterConfig(FilterType filterType, double strength) {
            this.filterType = filterType;
            this.strength = strength;
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/filters/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 获取视频信息
        getVideoInfo(inputVideo);
        
        // 色彩调整
        applyColorAdjustment(inputVideo, outputDir + "brightness_adjusted.mp4", 0.2, 1.2, 1.5);
        
        // 图像变换
        applyImageTransform(inputVideo, outputDir + "horizontal_flip.mp4", TransformType.HORIZONTAL_FLIP);
        applyImageTransform(inputVideo, outputDir + "rotate_90.mp4", TransformType.ROTATE_90);
        
        // 模糊和锐化
        applyBlurSharpness(inputVideo, outputDir + "gaussian_blur.mp4", FilterType.GAUSSIAN_BLUR, 2.0);
        applyBlurSharpness(inputVideo, outputDir + "unsharp.mp4", FilterType.UNSHARP, 1.5);
        
        // 色彩空间转换
        applyColorSpaceConversion(inputVideo, outputDir + "grayscale.mp4", "rgb", "hsv");
        
        // 添加渐变背景
        addGradientBackground(inputVideo, outputDir + "gradient_bg.mp4", "blue", 1920, 1080);
        
        // 色彩替换（绿幕抠图）
        chromaKeyReplacement(inputVideo, outputDir + "chroma_key.mp4", "green", 0.3, 0.1);
        
        // 明暗对比调整
        applyBrightnessContrast(inputVideo, outputDir + "brightness_contrast.mp4", 0.1, 1.2, 1.1);
        
        // 淡入淡出效果
        applyFadeEffect(inputVideo, outputDir + "fade_in.mp4", FadeType.FADE_IN, 0, 30);
        applyFadeEffect(inputVideo, outputDir + "fade_out.mp4", FadeType.FADE_OUT, 120, 30);
        
        // 批量处理
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add(inputVideo);
        inputFiles.add(inputVideo);
        
        FilterConfig colorConfig = new FilterConfig(0.2, 1.2, 1.5);
        batchApplyFilters(inputFiles, outputDir + "batch/", colorConfig);
    }
}