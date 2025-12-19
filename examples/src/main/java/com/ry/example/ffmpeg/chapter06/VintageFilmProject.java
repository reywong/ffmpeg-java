package com.ry.example.ffmpeg.chapter06;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 实战项目：老电影怀旧风
 * 创建完整的老电影怀旧效果，包括多个滤镜的组合
 */
public class VintageFilmProject {
    
    /**
     * 创建完整的老电影效果
     */
    public static void createVintageFilm(String inputFile, String outputFile, 
                                       VintageStyle style) {
        try {
            String filterChain = buildVintageFilterChain(style);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filterChain, outputFile);
            
            System.out.println("正在创建老电影效果...");
            System.out.println("滤镜链: " + filterChain);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("老电影效果创建完成: " + outputFile);
                System.out.println("风格类型: " + style);
            } else {
                System.err.println("老电影效果创建失败");
            }
            
        } catch (Exception e) {
            System.err.println("老电影效果创建异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 构建怀旧滤镜链
     */
    private static String buildVintageFilterChain(VintageStyle style) {
        StringBuilder filterChain = new StringBuilder();
        
        switch (style) {
            case CLASSIC_FILM:
                // 经典电影效果
                filterChain.append("eq=brightness=-0.1:contrast=1.1:saturation=0.8,");  // 色彩调整
                filterChain.append("colorchannelmixer=.393:.769:.189:.349:.686:.168:.272:.534:.131,");  // 棕褐色调
                filterChain.append("gblur=sigma=1,");  // 轻微模糊
                filterChain.append("boxblur=1:1:0.5:1:1:0.5,");  // 额外模糊
                filterChain.append("noise=alls=15:allf=t+u,");  // 添加噪点
                filterChain.append("vignette=angle=PI/6:mode=backward:eval=init,");  // 暗角效果
                break;
                
            case SEPIA_TONE:
                // 棕褐色调效果
                filterChain.append("colorchannelmixer=.393:.769:.189:.349:.686:.168:.272:.534:.131,");  // 棕褐色调
                filterChain.append("eq=brightness=-0.05:contrast=1.05:saturation=0.6,");  // 轻微调整
                filterChain.append("gblur=sigma=0.5,");  // 轻微模糊
                filterChain.append("vignette=angle=PI/4:mode=backward:eval=init,");  // 暗角
                break;
                
            case BLACK_WHITE:
                // 黑白电影效果
                filterChain.append("colorchannelmixer=.299:.587:.114:.299:.587:.114:.299:.587:.114,");  // 黑白转换
                filterChain.append("eq=brightness=-0.1:contrast=1.2,");  // 提高对比度
                filterChain.append("gblur=sigma=0.8,");  // 轻微模糊
                filterChain.append("noise=alls=10:allf=t+u,");  // 添加噪点
                filterChain.append("vignette=angle=PI/5:mode=backward:eval=init,");  // 暗角
                break;
                
            case VINTAGE_COLOR:
                // 复古色彩效果
                filterChain.append("eq=brightness=0.1:contrast=0.9:saturation=1.2,");  // 色彩调整
                filterChain.append("colorchannelmixer=1.1:0:0:0:1.1:0:0:0:1.2,");  // 色彩偏移
                filterChain.append("gblur=sigma=0.5,");  // 轻微模糊
                filterChain.append("vignette=angle=PI/6:mode=backward:eval=init,");  // 暗角
                break;
                
            case AGED_FILM:
                // 老旧胶片效果
                filterChain.append("eq=brightness=-0.15:contrast=1.15:saturation=0.7,");  // 色彩老化
                filterChain.append("colorchannelmixer=.393:.769:.189:.349:.686:.168:.272:.534:.131,");  // 棕褐
                filterChain.append("gblur=sigma=1.2,");  // 模糊
                filterChain.append("boxblur=2:1:1:2:1:1,");  // 额外模糊
                filterChain.append("noise=alls=20:allf=t+u,");  // 更多噪点
                filterChain.append("vignette=angle=PI/4:mode=backward:eval=init,");  // 强暗角
                filterChain.append("curves=all='0/0 0.5/0.45 1/1',");  // S曲线调整
                break;
                
            case NOIR_STYLE:
                // 黑色电影风格
                filterChain.append("colorchannelmixer=.299:.587:.114:.299:.587:.114:.299:.587:.114,");  // 黑白
                filterChain.append("eq=brightness=-0.2:contrast=1.5,");  // 高对比度
                filterChain.append("curves=all='0/0 0.3/0.2 0.7/0.8 1/1',");  // 对比度曲线
                filterChain.append("vignette=angle=PI/3:mode=backward:eval=init,");  // 暗角
                break;
        }
        
        // 移除最后的逗号
        String result = filterChain.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    /**
     * 创建带有划痕效果的老电影
     */
    public static void createFilmWithScratches(String inputFile, String outputFile, 
                                              VintageStyle style) {
        try {
            // 创建划痕蒙版
            String scratchMask = "scratch_mask.png";
            createScratchMask(scratchMask);
            
            // 构建滤镜链
            String vintageFilter = buildVintageFilterChain(style);
            String fullFilter = String.format("%s,overlay=%s", vintageFilter, scratchMask);
            
            String command = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"[0:v]%s[main];[main][1:v]overlay=format=auto:repeat_last=0\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, scratchMask, vintageFilter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("带划痕的老电影效果创建完成: " + outputFile);
            } else {
                System.err.println("带划痕的老电影效果创建失败");
            }
            
            // 清理临时文件
            new File(scratchMask).delete();
            
        } catch (Exception e) {
            System.err.println("带划痕的老电影效果创建异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建划痕蒙版图像
     */
    private static void createScratchMask(String outputFile) {
        try {
            // 使用FFmpeg创建随机线条蒙版
            String command = String.format(
                "ffmpeg -f lavfi -i \"geq=random(1)*255:128:128:128\" -frames:v 1 -s 1920x1080 %s",
                outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
        } catch (Exception e) {
            System.err.println("创建划痕蒙版失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量创建不同风格的怀旧效果
     */
    public static void batchCreateVintageEffects(String inputFile, String outputDir) {
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        VintageStyle[] styles = VintageStyle.values();
        
        for (VintageStyle style : styles) {
            String outputFile = outputDir + "/vintage_" + style.name().toLowerCase() + ".mp4";
            createVintageFilm(inputFile, outputFile, style);
            
            try {
                Thread.sleep(1000); // 避免同时处理过多进程
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("批量怀旧效果创建完成，输出目录: " + outputDir);
    }
    
    /**
     * 创建对比效果（原始 vs 怀旧）
     */
    public static void createComparisonVideo(String inputFile, String outputFile, 
                                           VintageStyle style) {
        try {
            String vintageFilter = buildVintageFilterChain(style);
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"[0:v]split=2[original][vintage];[vintage]%s[vintage_filtered];[original]scale=960:540[orig_scaled];[vintage_filtered]scale=960:540[vint_scaled];[orig_scaled][vint_scaled]hstack\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, vintageFilter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("对比效果创建完成: " + outputFile);
            } else {
                System.err.println("对比效果创建失败");
            }
            
        } catch (Exception e) {
            System.err.println("对比效果创建异常: " + e.getMessage());
        }
    }
    
    /**
     * 怀旧风格枚举
     */
    public enum VintageStyle {
        CLASSIC_FILM("经典电影"),
        SEPIA_TONE("棕褐色调"),
        BLACK_WHITE("黑白电影"),
        VINTAGE_COLOR("复古色彩"),
        AGED_FILM("老旧胶片"),
        NOIR_STYLE("黑色电影");
        
        private final String description;
        
        VintageStyle(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    /**
     * 获取所有可用风格的描述
     */
    public static void listAllStyles() {
        System.out.println("=== 可用的怀旧风格 ===");
        for (VintageStyle style : VintageStyle.values()) {
            System.out.printf("%d. %s (%s)%n", 
                style.ordinal() + 1, style.getDescription(), style.name());
        }
        System.out.println("======================");
    }
    
    /**
     * 测试和演示所有功能
     */
    public static void main(String[] args) {
        String inputVideo = "input.mp4";
        String outputDir = "output/vintage_project/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 显示所有可用风格
        listAllStyles();
        
        // 创建各种风格的怀旧效果
        System.out.println("\n=== 创建各种怀旧效果 ===");
        for (VintageStyle style : VintageStyle.values()) {
            String outputFile = outputDir + style.name().toLowerCase() + "_film.mp4";
            createVintageFilm(inputVideo, outputFile, style);
        }
        
        // 创建带划痕的效果
        System.out.println("\n=== 创建带划痕的老电影效果 ===");
        createFilmWithScratches(inputVideo, outputDir + "film_with_scratches.mp4", VintageStyle.CLASSIC_FILM);
        
        // 创建对比效果
        System.out.println("\n=== 创建对比效果 ===");
        createComparisonVideo(inputVideo, outputDir + "comparison_sepia.mp4", VintageStyle.SEPIA_TONE);
        
        // 批量处理
        System.out.println("\n=== 批量创建怀旧效果 ===");
        batchCreateVintageEffects(inputVideo, outputDir + "batch/");
        
        System.out.println("\n=== 所有怀旧效果创建完成 ===");
        System.out.println("输出目录: " + outputDir);
        System.out.println("\n生成的文件:");
        listGeneratedFiles(outputDir);
    }
    
    /**
     * 列出生成的文件
     */
    private static void listGeneratedFiles(String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        long sizeKB = file.length() / 1024;
                        System.out.printf("  %-40s %,d KB%n", file.getName(), sizeKB);
                    }
                }
            }
        }
    }
}