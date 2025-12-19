package com.ry.example.ffmpeg.chapter06;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 第6章演示类
 * FFmpeg加工视频功能演示
 */
public class Chapter06Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第6章：FFmpeg加工视频演示 ===\n");
        
        // 创建必要的目录
        createDirectories();
        
        // 演示视频滤镜处理
        demonstrateVideoFilters();
        
        // 演示视频变换
        demonstrateVideoTransformation();
        
        // 演示怀旧效果生成
        demonstrateVintageEffects();
        
        // 演示组合效果
        demonstrateCombinedEffects();
        
        System.out.println("第6章演示完成！");
    }
    
    /**
     * 创建必要的目录
     */
    private static void createDirectories() {
        String[] directories = {
            "output/filters/",
            "output/transform/",
            "output/vintage/",
            "output/combined/"
        };
        
        for (String dir : directories) {
            new File(dir).mkdirs();
        }
    }
    
    /**
     * 演示视频滤镜处理
     */
    private static void demonstrateVideoFilters() {
        System.out.println("=== 6.1 视频滤镜处理演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
        } else {
            // 色彩调整
            VideoFilterProcessor.applyColorAdjustment(inputVideo, "output/filters/brightness_adjusted.mp4", 
                                                    0.2, 1.2, 1.5);
            
            // 图像变换
            VideoFilterProcessor.applyImageTransform(inputVideo, "output/filters/horizontal_flip.mp4", 
                                                    VideoFilterProcessor.TransformType.HORIZONTAL_FLIP);
            VideoFilterProcessor.applyImageTransform(inputVideo, "output/filters/rotate_90.mp4", 
                                                    VideoFilterProcessor.TransformType.ROTATE_90);
            
            // 模糊和锐化
            VideoFilterProcessor.applyBlurSharpness(inputVideo, "output/filters/gaussian_blur.mp4", 
                                                   VideoFilterProcessor.FilterType.GAUSSIAN_BLUR, 2.0);
            VideoFilterProcessor.applyBlurSharpness(inputVideo, "output/filters/unsharp.mp4", 
                                                   VideoFilterProcessor.FilterType.UNSHARP, 1.5);
            
            // 色彩空间转换
            VideoFilterProcessor.applyColorSpaceConversion(inputVideo, "output/filters/grayscale.mp4", 
                                                         "rgb", "hsv");
            
            // 明暗对比调整
            VideoFilterProcessor.applyBrightnessContrast(inputVideo, "output/filters/brightness_contrast.mp4", 
                                                      0.1, 1.2, 1.1);
            
            // 淡入淡出效果
            VideoFilterProcessor.applyFadeEffect(inputVideo, "output/filters/fade_in.mp4", 
                                                VideoFilterProcessor.FadeType.FADE_IN, 0, 30);
            VideoFilterProcessor.applyFadeEffect(inputVideo, "output/filters/fade_out.mp4", 
                                                VideoFilterProcessor.FadeType.FADE_OUT, 120, 30);
            
            // 添加渐变背景
            VideoFilterProcessor.addGradientBackground(inputVideo, "output/filters/gradient_bg.mp4", 
                                                     "blue", 1920, 1080);
            
            // 色彩替换
            VideoFilterProcessor.chromaKeyReplacement(inputVideo, "output/filters/chroma_key.mp4", 
                                                    "green", 0.3, 0.1);
        }
        
        System.out.println("视频滤镜处理演示完成\n");
    }
    
    /**
     * 演示视频变换
     */
    private static void demonstrateVideoTransformation() {
        System.out.println("=== 6.3 视频变换演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
        } else {
            // 缩放视频
            VideoTransformer.scaleVideo(inputVideo, "output/transform/scaled_640x360.mp4", 640, 360);
            VideoTransformer.scaleVideoKeepAspectRatio(inputVideo, "output/transform/scaled_keep_aspect.mp4", 800, 600);
            
            // 裁剪视频
            VideoTransformer.cropVideo(inputVideo, "output/transform/cropped_400x300.mp4", 400, 300, 100, 50);
            VideoTransformer.centerCropVideo(inputVideo, "output/transform/center_cropped.mp4", 800, 600);
            
            // 填充视频
            VideoTransformer.padVideo(inputVideo, "output/transform/padded_black.mp4", 1920, 1080, 560, 260, "black");
            VideoTransformer.padVideo(inputVideo, "output/transform/padded_blue.mp4", 1920, 1080, 560, 260, "blue");
            
            // 旋转视频
            VideoTransformer.rotateVideo(inputVideo, "output/transform/rotated_90.mp4", 
                                       VideoTransformer.RotationType.CLOCKWISE_90);
            VideoTransformer.rotateVideo(inputVideo, "output/transform/rotated_180.mp4", 
                                       VideoTransformer.RotationType.CLOCKWISE_180);
            
            // 自定义角度旋转
            VideoTransformer.rotateVideoCustom(inputVideo, "output/transform/rotated_45.mp4", Math.PI / 4);
            
            // 翻转视频
            VideoTransformer.flipVideo(inputVideo, "output/transform/flipped_horizontal.mp4", 
                                      VideoTransformer.FlipType.HORIZONTAL);
            VideoTransformer.flipVideo(inputVideo, "output/transform/flipped_vertical.mp4", 
                                      VideoTransformer.FlipType.VERTICAL);
            
            // 组合变换
            VideoTransformer.scaleAndCrop(inputVideo, "output/transform/scale_and_crop.mp4", 800, 600);
            
            // 镜头移动效果
            VideoTransformer.addPanEffect(inputVideo, "output/transform/pan_effect.mp4", 400, 300, 0, 0, 100, 50);
            
            // 缩放镜头效果
            VideoTransformer.addZoomEffect(inputVideo, "output/transform/zoom_effect.mp4", 1, 2);
        }
        
        System.out.println("视频变换演示完成\n");
    }
    
    /**
     * 演示怀旧效果生成
     */
    private static void demonstrateVintageEffects() {
        System.out.println("=== 6.4 怀旧效果生成演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
        } else {
            // 基础怀旧效果
            VintageEffectGenerator.applyBasicVintage(inputVideo, "output/vintage/basic_vintage.mp4");
            
            // 完整怀旧效果
            VintageEffectGenerator.applyFullVintage(inputVideo, "output/vintage/full_vintage.mp4");
            
            // 褐色怀旧效果
            VintageEffectGenerator.applySepiaVintage(inputVideo, "output/vintage/sepia_vintage.mp4");
            
            // 80年代录像带效果
            VintageEffectGenerator.apply80sVHSEffect(inputVideo, "output/vintage/80s_vhs.mp4");
            
            // 老电影胶片效果
            VintageEffectGenerator.applyFilmEffect(inputVideo, "output/vintage/film_effect.mp4");
            
            // 黑白默片效果
            VintageEffectGenerator.applySilentFilmEffect(inputVideo, "output/vintage/silent_film.mp4");
            
            // 复古棕褐色调
            VintageEffectGenerator.applyRetroSepia(inputVideo, "output/vintage/retro_sepia.mp4", 0.8);
            
            // 胶片颗粒效果
            VintageEffectGenerator.addFilmGrain(inputVideo, "output/vintage/film_grain.mp4", 25);
            
            // 边框效果
            VintageEffectGenerator.addVintageBorder(inputVideo, "output/vintage/vintage_border.mp4", 20, "black");
            
            // 自定义怀旧效果
            VintageEffectGenerator.VintageConfig customConfig = new VintageEffectGenerator.VintageConfig(
                true, 1.2, -0.1, 25, 0.8);
            customConfig.borderWidth = 15;
            customConfig.borderColor = "brown";
            VintageEffectGenerator.applyCustomVintage(inputVideo, "output/vintage/custom_vintage.mp4", customConfig);
        }
        
        System.out.println("怀旧效果生成演示完成\n");
    }
    
    /**
     * 演示组合效果
     */
    private static void demonstrateCombinedEffects() {
        System.out.println("=== 6.5 组合效果演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
        } else {
            // 组合1：缩放 + 怀旧效果
            String tempScaled = "output/combined/temp_scaled.mp4";
            VideoTransformer.scaleVideo(inputVideo, tempScaled, 640, 480);
            VintageEffectGenerator.applyBasicVintage(tempScaled, "output/combined/scaled_vintage.mp4");
            new File(tempScaled).delete();
            
            // 组合2：裁剪 + 色彩调整
            String tempCropped = "output/combined/temp_cropped.mp4";
            VideoTransformer.cropVideo(inputVideo, tempCropped, 400, 300, 100, 50);
            VideoFilterProcessor.applyColorAdjustment(tempCropped, "output/combined/cropped_colored.mp4", 
                                                    0.3, 1.5, 2.0);
            new File(tempCropped).delete();
            
            // 组合3：旋转 + 淡入淡出
            String tempRotated = "output/combined/temp_rotated.mp4";
            VideoTransformer.rotateVideo(inputVideo, tempRotated, VideoTransformer.RotationType.CLOCKWISE_90);
            VideoFilterProcessor.applyFadeEffect(tempRotated, "output/combined/rotated_fade.mp4", 
                                                VideoFilterProcessor.FadeType.FADE_IN, 0, 30);
            new File(tempRotated).delete();
            
            // 组合4：怀旧 + 边框 + 颗粒
            String tempVintage = "output/combined/temp_vintage.mp4";
            VintageEffectGenerator.applySepiaVintage(inputVideo, tempVintage);
            VintageEffectGenerator.addVintageBorder(tempVintage, "output/combined/vintage_bordered.mp4", 25, "brown");
            VintageEffectGenerator.addFilmGrain(tempVintage, "output/combined/vintage_grain.mp4", 30);
            new File(tempVintage).delete();
            
            // 组合5：复杂滤镜链
            applyComplexFilterChain(inputVideo, "output/combined/complex_chain.mp4");
        }
        
        System.out.println("组合效果演示完成\n");
    }
    
    /**
     * 应用复杂滤镜链
     */
    private static void applyComplexFilterChain(String inputFile, String outputFile) {
        try {
            String filter = "[0:v]hue=s=0.7," +
                           "eq=contrast=1.1:brightness=0.05:saturation=1.2," +
                           "scale=800:600," +
                           "crop=700:500:50:50," +
                           "boxblur=1:1:cr=0:ar=0," +
                           "noise=alls=15:allf=t+u," +
                           "drawbox=x=0:y=0:w=iw:h=10:color=brown," +
                           "drawbox=x=0:y=ih-10:w=iw:h=10:color=brown," +
                           "fade=in:0:30,fade=out:120:30[v];" +
                           "[0:a]afade=in:0:30,afade=out:120:30[a]";
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"%s\" -map \"[v]\" -map \"[a]\" -c:v libx264 -crf 23 -preset veryfast -c:a aac -b:a 128k %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("复杂滤镜链应用完成: " + outputFile);
            } else {
                System.err.println("复杂滤镜链应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("复杂滤镜链应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 显示功能菜单
     */
    public static void showMenu() {
        System.out.println("请选择要演示的功能：");
        System.out.println("1. 视频滤镜处理");
        System.out.println("2. 视频变换");
        System.out.println("3. 怀旧效果生成");
        System.out.println("4. 组合效果");
        System.out.println("5. 运行所有演示");
        System.out.println("0. 退出");
    }
    
    /**
     * 交互式演示
     */
    public static void interactiveDemo() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        while (true) {
            showMenu();
            System.out.print("请输入选择：");
            
            try {
                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1:
                        demonstrateVideoFilters();
                        break;
                    case 2:
                        demonstrateVideoTransformation();
                        break;
                    case 3:
                        demonstrateVintageEffects();
                        break;
                    case 4:
                        demonstrateCombinedEffects();
                        break;
                    case 5:
                        createDirectories();
                        demonstrateVideoFilters();
                        demonstrateVideoTransformation();
                        demonstrateVintageEffects();
                        demonstrateCombinedEffects();
                        break;
                    case 0:
                        System.out.println("退出演示");
                        return;
                    default:
                        System.out.println("无效选择，请重新输入");
                        break;
                }
            } catch (Exception e) {
                System.out.println("输入错误，请重新输入");
                scanner.nextLine(); // 清除错误的输入
            }
        }
    }
    
    /**
     * 性能测试
     */
    public static void performanceTest() {
        System.out.println("=== 性能测试 ===");
        
        String inputVideo = "input.mp4";
        if (!new File(inputVideo).exists()) {
            System.out.println("需要输入视频文件进行性能测试");
            return;
        }
        
        // 测试不同滤镜的处理时间
        System.out.println("测试不同滤镜的处理时间...");
        
        long startTime, endTime;
        
        // 基础滤镜测试
        startTime = System.currentTimeMillis();
        VideoFilterProcessor.applyColorAdjustment(inputVideo, "output/perf/color_adjust.mp4", 0.2, 1.2, 1.5);
        endTime = System.currentTimeMillis();
        System.out.println("色彩调整耗时: " + (endTime - startTime) + " ms");
        
        // 模糊滤镜测试
        startTime = System.currentTimeMillis();
        VideoFilterProcessor.applyBlurSharpness(inputVideo, "output/perf/blur.mp4", 
                                              VideoFilterProcessor.FilterType.GAUSSIAN_BLUR, 2.0);
        endTime = System.currentTimeMillis();
        System.out.println("模糊滤镜耗时: " + (endTime - startTime) + " ms");
        
        // 怀旧效果测试
        startTime = System.currentTimeMillis();
        VintageEffectGenerator.applyBasicVintage(inputVideo, "output/perf/vintage.mp4");
        endTime = System.currentTimeMillis();
        System.out.println("怀旧效果耗时: " + (endTime - startTime) + " ms");
        
        // 变换测试
        startTime = System.currentTimeMillis();
        VideoTransformer.scaleVideo(inputVideo, "output/perf/scale.mp4", 640, 360);
        endTime = System.currentTimeMillis();
        System.out.println("视频缩放耗时: " + (endTime - startTime) + " ms");
        
        System.out.println("性能测试完成");
    }
    
    /**
     * 创建演示视频
     */
    public static void createDemoVideo() {
        System.out.println("创建演示视频...");
        
        try {
            // 创建一个彩条测试视频
            String command = "ffmpeg -f lavfi -i testsrc=duration=10:size=640x480:rate=30 -c:v libx264 -preset veryfast -crf 23 input.mp4";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("演示视频创建完成: input.mp4");
            } else {
                System.err.println("演示视频创建失败");
            }
            
        } catch (Exception e) {
            System.err.println("创建演示视频异常: " + e.getMessage());
        }
    }
}