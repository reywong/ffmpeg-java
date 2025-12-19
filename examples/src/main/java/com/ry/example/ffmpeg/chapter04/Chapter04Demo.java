package com.ry.example.ffmpeg.chapter04;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 第4章演示类
 * FFmpeg处理图像功能演示
 */
public class Chapter04Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第4章：FFmpeg处理图像演示 ===\n");
        
        // 创建必要的目录
        createDirectories();
        
        // 演示YUV图像处理
        demonstrateYUVProcessing();
        
        // 演示JPEG图像处理
        demonstrateJPEGProcessing();
        
        // 演示图像格式转换
        demonstrateImageFormatConversion();
        
        // 演示图片转视频
        demonstrateImageToVideo();
        
        System.out.println("第4章演示完成！");
    }
    
    /**
     * 创建必要的目录
     */
    private static void createDirectories() {
        String[] directories = {
            "input/images/",
            "output/yuv/",
            "output/jpeg/",
            "output/images/",
            "output/video/",
            "output/video/normalized/"
        };
        
        for (String dir : directories) {
            new File(dir).mkdirs();
        }
    }
    
    /**
     * 演示YUV图像处理
     */
    private static void demonstrateYUVProcessing() {
        System.out.println("=== 4.1 YUV图像处理演示 ===");
        
        String inputVideo = "input.mp4";
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 保存单帧为YUV
        YUVImageProcessor.saveFrameAsYUV(inputVideo, "output/yuv/frame_0.yuv", 0);
        
        // 批量保存YUV文件
        YUVImageProcessor.batchSaveYUV(inputVideo, "output/yuv/frame_%03d.yuv", 30);
        
        // 分析YUV文件
        YUVImageProcessor.analyzeYUVFile("output/yuv/frame_0.yuv", 1920, 1080);
        
        // YUV转PNG
        YUVImageProcessor.convertYUVToRGB("output/yuv/frame_0.yuv", "output/yuv/frame_0.png", 1920, 1080);
        
        System.out.println("YUV图像处理演示完成\n");
    }
    
    /**
     * 演示JPEG图像处理
     */
    private static void demonstrateJPEGProcessing() {
        System.out.println("=== 4.2 JPEG图像处理演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            return;
        }
        
        // 提取单帧为JPEG
        JPEGImageProcessor.extractFrameAsJPEG(inputVideo, "output/jpeg/frame_0.jpg", 0, 0.8f);
        
        // 批量提取JPEG
        JPEGImageProcessor.batchExtractJPEG(inputVideo, "output/jpeg/frame_%03d.jpg", 1);
        
        // 优化JPEG质量
        JPEGImageProcessor.optimizeJPEG("output/jpeg/frame_0.jpg", "output/jpeg/frame_0_optimized.jpg", 0.6f);
        
        // 创建渐进式JPEG
        JPEGImageProcessor.createProgressiveJPEG("output/jpeg/frame_0.jpg", "output/jpeg/frame_0_progressive.jpg");
        
        // 图像处理
        JPEGImageProcessor.processImage("output/jpeg/frame_0.jpg", "output/jpeg/frame_0_scaled.jpg", 800, 600, "scale");
        
        System.out.println("JPEG图像处理演示完成\n");
    }
    
    /**
     * 演示图像格式转换
     */
    private static void demonstrateImageFormatConversion() {
        System.out.println("=== 4.3 图像格式转换演示 ===");
        
        String inputVideo = "input.mp4";
        
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            return;
        }
        
        // 提取PNG图片
        ImageFormatConverter.extractFrameAsPNG(inputVideo, "output/images/frame.png", 0, 6);
        
        // 提取BMP图片
        ImageFormatConverter.extractFrameAsBMP(inputVideo, "output/images/frame.bmp", 0);
        
        // 创建GIF动画
        ImageFormatConverter.createGIFAnimation(inputVideo, "output/images/animation.gif", 10, 320, 240);
        
        // 创建高质量GIF
        ImageFormatConverter.createHighQualityGIF(inputVideo, "output/images/high_quality.gif", 15, 480, 360);
        
        // 创建循环GIF
        ImageFormatConverter.createLoopedGIF(inputVideo, "output/images/looped.gif", 5, 10, 8);
        
        // 格式转换
        ImageFormatConverter.convertImageFormat("output/images/frame.png", "output/images/frame_converted.jpg", "jpg");
        
        // 图像优化
        ImageFormatConverter.optimizeImage("output/images/frame.png", "output/images/frame_optimized.png", "png");
        
        // 批量转换
        ImageFormatConverter.batchConvertFormat("output/jpeg/", "output/images/converted/", "jpg", "png");
        
        System.out.println("图像格式转换演示完成\n");
    }
    
    /**
     * 演示图片转视频
     */
    private static void demonstrateImageToVideo() {
        System.out.println("=== 4.4 图片转视频演示 ===");
        
        // 创建一些测试图片（如果没有的话）
        createTestImages();
        
        // 扫描图片文件
        List<String> imagePaths = ImageToVideoConverter.scanImageFiles("output/test_images/");
        
        if (imagePaths.isEmpty()) {
            System.out.println("未找到测试图片，跳过图片转视频演示");
            return;
        }
        
        System.out.println("找到 " + imagePaths.size() + " 个测试图片");
        
        // 统一图片尺寸
        List<String> normalizedPaths = ImageToVideoConverter.normalizeImageSizes(
            imagePaths, 1920, 1080, "output/video/normalized/");
        
        // 创建基础视频
        ImageToVideoConverter.convertImagesToVideo(normalizedPaths, "output/video/basic_video.mp4", 25, 1920, 1080);
        
        // 创建带转场效果的视频
        ImageToVideoConverter.createVideoWithTransitions(normalizedPaths, "output/video/transition_video.mp4", 25, 1920, 1080);
        
        // 创建幻灯片效果的视频
        ImageToVideoConverter.createSlideshowVideo(normalizedPaths, "output/video/slideshow_zoom.mp4", 1, 1920, 1080, "zoom");
        
        System.out.println("图片转视频演示完成\n");
    }
    
    /**
     * 创建测试图片
     */
    private static void createTestImages() {
        System.out.println("创建测试图片...");
        
        // 这里可以创建一些简单的测试图片
        // 实际项目中，用户应该提供自己的图片文件
        for (int i = 0; i < 5; i++) {
            String testImagePath = "output/test_images/test_" + String.format("%03d", i) + ".jpg";
            if (!new File(testImagePath).exists()) {
                System.out.println("提示：请将测试图片放在 output/test_images/ 目录下");
                break;
            }
        }
    }
    
    /**
     * 显示功能菜单
     */
    public static void showMenu() {
        System.out.println("请选择要演示的功能：");
        System.out.println("1. YUV图像处理");
        System.out.println("2. JPEG图像处理");
        System.out.println("3. 图像格式转换");
        System.out.println("4. 图片转视频");
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
                        demonstrateYUVProcessing();
                        break;
                    case 2:
                        demonstrateJPEGProcessing();
                        break;
                    case 3:
                        demonstrateImageFormatConversion();
                        break;
                    case 4:
                        demonstrateImageToVideo();
                        break;
                    case 5:
                        createDirectories();
                        demonstrateYUVProcessing();
                        demonstrateJPEGProcessing();
                        demonstrateImageFormatConversion();
                        demonstrateImageToVideo();
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
}