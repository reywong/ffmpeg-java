package com.ry.example.ffmpeg.chapter08;

import java.io.File;
import java.util.List;

/**
 * 第8章综合演示类
 * 演示FFmpeg自定义滤镜和编译配置的所有功能
 */
public class Chapter08Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第8章 FFmpeg自定义滤镜 - 综合演示 ===\n");
        
        String inputVideo = "input.mp4";
        String outputDir = "output/chapter08_demo/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入文件
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
        }
        
        // 1. 演示FFmpeg编译配置
        demonstrateFFmpegCompiler(outputDir);
        
        // 2. 演示自定义滤镜
        demonstrateCustomFilters(inputVideo, outputDir);
        
        // 3. 演示高级滤镜效果
        demonstrateAdvancedFilters(inputVideo, outputDir);
        
        // 4. 演示滤镜性能测试
        demonstrateFilterPerformance(inputVideo, outputDir);
        
        // 5. 演示滤镜链组合
        demonstrateFilterChains(inputVideo, outputDir);
        
        System.out.println("\n=== 演示完成 ===");
        System.out.println("所有示例文件已生成到: " + outputDir);
        listGeneratedFiles(outputDir);
    }
    
    /**
     * 演示FFmpeg编译配置
     */
    private static void demonstrateFFmpegCompiler(String outputDir) {
        System.out.println("--- 8.1 FFmpeg编译配置演示 ---");
        
        // 生成编译脚本
        FFmpegCompiler.generateWindowsBuildScript(outputDir + "compiler/");
        FFmpegCompiler.generateLinuxBuildScript(outputDir + "compiler/");
        FFmpegCompiler.generateX264BuildScript(outputDir + "compiler/");
        
        // 检查编译环境
        FFmpegCompiler.BuildEnvironment env = FFmpegCompiler.checkBuildEnvironment();
        FFmpegCompiler.printBuildEnvironment(env);
        
        // 生成FFmpeg配置
        FFmpegCompiler.BuildConfig config = new FFmpegCompiler.BuildConfig();
        FFmpegCompiler.generateFFmpegConfig(outputDir + "compiler/", config);
        
        // 生成编译报告
        List<FFmpegCompiler.BuildStep> steps = List.of(
            new FFmpegCompiler.BuildStep("环境检查", "检查编译工具和依赖库", "1-2分钟"),
            new FFmpegCompiler.BuildStep("依赖编译", "编译x264等依赖库", "20-30分钟"),
            new FFmpegCompiler.BuildStep("FFmpeg配置", "运行configure脚本", "2-5分钟"),
            new FFmpegCompiler.BuildStep("FFmpeg编译", "编译FFmpeg源码", "30-60分钟"),
            new FFmpegCompiler.BuildStep("安装配置", "安装到指定路径", "2-5分钟")
        );
        
        FFmpegCompiler.generateBuildReport(outputDir + "compiler/", steps);
    }
    
    /**
     * 演示自定义滤镜
     */
    private static void demonstrateCustomFilters(String inputVideo, String outputDir) {
        System.out.println("\n--- 8.2 自定义滤镜演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过自定义滤镜演示（缺少视频文件）");
            return;
        }
        
        // 1. 侧边模糊效果（模拟）
        System.out.println("1. 侧边模糊效果");
        FFmpegCustomFilter.applySideBlurSimulated(inputVideo, 
            outputDir + "01_side_blur.mp4", 100, 100, 50, 3.0f);
        
        // 2. 自定义模糊锐化
        System.out.println("2. 自定义模糊锐化");
        FFmpegCustomFilter.applySimulatedBlurSharp(inputVideo, 
            outputDir + "02_blur_sharp.mp4", 2.0f, 1.5f);
        
        // 3. 自定义翻转
        System.out.println("3. 自定义翻转效果");
        FFmpegCustomFilter.applyCustomFlip(inputVideo, 
            outputDir + "03_custom_flip.mp4", true, false, false);
        
        // 4. 自定义旋转
        System.out.println("4. 自定义旋转效果");
        FFmpegCustomFilter.applyCustomFlip(inputVideo, 
            outputDir + "04_custom_rotate.mp4", false, false, true);
    }
    
    /**
     * 演示高级滤镜效果
     */
    private static void demonstrateAdvancedFilters(String inputVideo, String outputDir) {
        System.out.println("\n--- 8.3 高级滤镜效果演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过高级滤镜演示（缺少视频文件）");
            return;
        }
        
        // 1. 复杂滤镜链
        System.out.println("1. 复杂滤镜链组合");
        FFmpegCustomFilter.FilterChainConfig complexConfig = 
            new FFmpegCustomFilter.FilterChainConfig()
                .blur(1.5f)
                .sharp(1.0f)
                .flip(true, false)
                .color(0.1f, 1.2f, 1.1f)
                .vignette(true);
        
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "05_complex_filter.mp4", complexConfig);
        
        // 2. 艺术效果滤镜
        System.out.println("2. 艺术效果滤镜");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "06_artistic_effect.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .color(-0.1f, 1.3f, 0.8f)
                .vignette(true));
        
        // 3. 修复效果滤镜
        System.out.println("3. 视频修复效果");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "07_restoration_effect.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .sharp(2.0f)
                .color(0.05f, 1.1f, 1.05f));
        
        // 4. 创意效果滤镜
        System.out.println("4. 创意效果");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "08_creative_effect.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .blur(1.0f)
                .flip(true, true)
                .color(0.2f, 0.9f, 1.2f));
    }
    
    /**
     * 演示滤镜性能测试
     */
    private static void demonstrateFilterPerformance(String inputVideo, String outputDir) {
        System.out.println("\n--- 8.4 滤镜性能测试 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过性能测试演示（缺少视频文件）");
            return;
        }
        
        // 生成性能测试报告
        System.out.println("1. 生成滤镜性能测试报告");
        FFmpegCustomFilter.generateFilterPerformanceReport(inputVideo, outputDir + "performance/");
        
        // 检查滤镜支持
        System.out.println("2. 检查滤镜支持情况");
        String[] testFilters = {"gblur", "unsharp", "hflip", "vflip", "eq", "vignette"};
        
        for (String filter : testFilters) {
            boolean supported = FFmpegCustomFilter.checkFilterSupport(filter);
            System.out.println("  " + filter + ": " + (supported ? "✓ 支持" : "✗ 不支持"));
        }
        
        // 获取所有支持的滤镜
        System.out.println("3. 获取支持的滤镜列表");
        var filters = FFmpegCustomFilter.getSupportedFilters();
        System.out.println("  总共支持 " + filters.size() + " 个滤镜");
        
        // 显示几个常用滤镜
        System.out.println("  常用滤镜示例:");
        String[] commonFilters = {"scale", "crop", "pad", "overlay", "drawtext", "subtitles"};
        for (String filter : commonFilters) {
            if (filters.containsKey(filter)) {
                System.out.println("    " + filter + " - " + filters.get(filter));
            }
        }
    }
    
    /**
     * 演示滤镜链组合
     */
    private static void demonstrateFilterChains(String inputVideo, String outputDir) {
        System.out.println("\n--- 8.5 滤镜链组合演示 ---");
        
        if (!new File(inputVideo).exists()) {
            System.out.println("跳过滤镜链演示（缺少视频文件）");
            return;
        }
        
        // 1. 预处理链
        System.out.println("1. 预处理滤镜链");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "09_preprocessing.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .sharp(1.5f)
                .color(0.0f, 1.1f, 1.0f));
        
        // 2. 风格化链
        System.out.println("2. 风格化滤镜链");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "10_stylization.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .blur(0.8f)
                .color(-0.2f, 1.2f, 0.7f)
                .vignette(true));
        
        // 3. 修复链
        System.out.println("3. 修复滤镜链");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "11_repair.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .sharp(2.5f)
                .blur(0.5f)
                .color(0.1f, 1.15f, 1.05f));
        
        // 4. 特效链
        System.out.println("4. 特效滤镜链");
        FFmpegCustomFilter.applyComplexFilterChain(inputVideo, 
            outputDir + "12_effects.mp4", 
            new FFmpegCustomFilter.FilterChainConfig()
                .blur(1.2f)
                .flip(true, false)
                .color(0.3f, 0.9f, 1.3f)
                .vignette(true));
        
        // 5. 创建自定义滤镜配置
        System.out.println("5. 创建自定义滤镜配置");
        FFmpegCustomFilter.CustomFilterConfig customConfig = 
            new FFmpegCustomFilter.CustomFilterConfig("myfilter", "video", "我的自定义滤镜");
        
        customConfig.addParameter("blur", "1.5");
        customConfig.addParameter("sharp", "1.0");
        customConfig.addParameter("brightness", "0.1");
        customConfig.addParameter("contrast", "1.2");
        
        customConfig.setFilterExpression("gblur=sigma=1.5,unsharp=5:5:1.0,eq=brightness=0.1:contrast=1.2");
        
        FFmpegCustomFilter.createCustomFilterConfig(outputDir + "custom_filter_config.txt", customConfig);
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
                        System.out.printf("  %-30s %,8d KB%n", file.getName(), sizeKB);
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
                    System.out.printf("%s  %-25s %,8d KB%n", prefix, file.getName(), sizeKB);
                }
            }
        }
    }
}