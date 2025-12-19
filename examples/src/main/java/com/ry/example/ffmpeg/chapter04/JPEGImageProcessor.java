package com.ry.example.ffmpeg.chapter04;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * JPEG图像处理器
 * 处理JPEG格式的图像转换和优化
 */
public class JPEGImageProcessor {
    
    /**
     * 从视频中提取单帧保存为JPEG
     */
    public static void extractFrameAsJPEG(String inputVideo, String outputFile, int frameIndex, float quality) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            // 跳转到指定帧
            grabber.setVideoFrameNumber(frameIndex);
            Frame frame = grabber.grab();
            
            if (frame != null && frame.image != null) {
                saveFrameAsJPEG(frame, outputFile, quality);
                System.out.println("成功提取第 " + frameIndex + " 帧为JPEG: " + outputFile);
            }
            
            grabber.stop();
        } catch (Exception e) {
            System.err.println("提取JPEG帧失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存帧为JPEG格式
     */
    private static void saveFrameAsJPEG(Frame frame, String outputFile, float quality) throws IOException {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage image = converter.getBufferedImage(frame);
        
        // 获取JPEG写入器
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPEG writers found");
        }
        
        ImageWriter writer = writers.next();
        JPEGImageWriteParam param = new JPEGImageWriteParam(null);
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        
        // 写入文件
        File file = new File(outputFile);
        file.getParentFile().mkdirs();
        
        ImageIO.write(image, "jpeg", file);
    }
    
    /**
     * 批量提取视频帧为JPEG
     */
    public static void batchExtractJPEG(String inputVideo, String outputPattern, int fps) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            // 计算间隔帧数
            double grabberFPS = grabber.getVideoFrameRate();
            int interval = (int) (grabberFPS / fps);
            
            Frame frame;
            int frameIndex = 0;
            int savedCount = 0;
            
            while ((frame = grabber.grab()) != null) {
                if (frameIndex % interval == 0 && frame.image != null) {
                    String outputFile = String.format(outputPattern, savedCount);
                    saveFrameAsJPEG(frame, outputFile, 0.8f);
                    savedCount++;
                    System.out.println("保存JPEG: " + outputFile);
                }
                frameIndex++;
            }
            
            grabber.stop();
            System.out.println("批量提取完成，共保存 " + savedCount + " 个JPEG文件");
        } catch (Exception e) {
            System.err.println("批量提取JPEG失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * JPEG质量优化
     */
    public static void optimizeJPEG(String inputFile, String outputFile, float quality) {
        try {
            BufferedImage image = ImageIO.read(new File(inputFile));
            
            // 使用不同的质量保存
            BufferedImage optimizedImage = optimizeImageQuality(image, quality);
            
            File output = new File(outputFile);
            output.getParentFile().mkdirs();
            
            ImageIO.write(optimizedImage, "jpeg", output);
            System.out.println("JPEG优化完成: " + outputFile);
            
            // 显示文件大小对比
            File originalFile = new File(inputFile);
            File newFile = new File(outputFile);
            System.out.println("原始大小: " + originalFile.length() + " bytes");
            System.out.println("优化后大小: " + newFile.length() + " bytes");
            System.out.println("压缩比: " + String.format("%.2f%%", 
                (1.0 - (double)newFile.length() / originalFile.length()) * 100));
            
        } catch (Exception e) {
            System.err.println("JPEG优化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 图像质量优化
     */
    private static BufferedImage optimizeImageQuality(BufferedImage image, float quality) {
        // 这里可以添加图像优化算法
        // 例如：锐化、降噪、色彩调整等
        return image;
    }
    
    /**
     * 创建渐进式JPEG
     */
    public static void createProgressiveJPEG(String inputFile, String outputFile) {
        try {
            // 使用FFmpeg创建渐进式JPEG
            String command = String.format("ffmpeg -i %s -q:v 2 -progressive %s", 
                inputFile, outputFile);
            Runtime.getRuntime().exec(command);
            System.out.println("创建渐进式JPEG: " + outputFile);
        } catch (Exception e) {
            System.err.println("创建渐进式JPEG失败: " + e.getMessage());
        }
    }
    
    /**
     * JPEG格式转换
     */
    public static void convertJPEGFormat(String inputFile, String outputFile, String targetFormat) {
        try {
            BufferedImage image = ImageIO.read(new File(inputFile));
            File output = new File(outputFile);
            output.getParentFile().mkdirs();
            
            ImageIO.write(image, targetFormat, output);
            System.out.println("格式转换完成: " + inputFile + " -> " + outputFile);
        } catch (Exception e) {
            System.err.println("格式转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 图像处理（缩放、裁剪、旋转）
     */
    public static void processImage(String inputFile, String outputFile, int width, int height, String operation) {
        try {
            String command;
            switch (operation.toLowerCase()) {
                case "scale":
                    command = String.format("ffmpeg -i %s -vf scale=%d:%d -q:v 2 %s", 
                        inputFile, width, height, outputFile);
                    break;
                case "crop":
                    command = String.format("ffmpeg -i %s -vf crop=%d:%d %s", 
                        inputFile, width, height, outputFile);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的操作: " + operation);
            }
            
            Runtime.getRuntime().exec(command);
            System.out.println("图像处理完成: " + operation);
        } catch (Exception e) {
            System.err.println("图像处理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 添加水印
     */
    public static void addWatermark(String inputFile, String watermarkFile, String outputFile, int x, int y) {
        try {
            String command = String.format("ffmpeg -i %s -i %s -filter_complex overlay=%d:%d -q:v 2 %s", 
                inputFile, watermarkFile, x, y, outputFile);
            Runtime.getRuntime().exec(command);
            System.out.println("添加水印完成: " + outputFile);
        } catch (Exception e) {
            System.err.println("添加水印失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/jpeg/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 提取单帧为JPEG
        extractFrameAsJPEG(inputVideo, outputDir + "frame_0.jpg", 0, 0.8f);
        
        // 批量提取JPEG
        batchExtractJPEG(inputVideo, outputDir + "frame_%03d.jpg", 1);
        
        // 优化JPEG质量
        optimizeJPEG(outputDir + "frame_0.jpg", outputDir + "frame_0_optimized.jpg", 0.6f);
        
        // 创建渐进式JPEG
        createProgressiveJPEG(outputDir + "frame_0.jpg", outputDir + "frame_0_progressive.jpg");
        
        // 图像处理
        processImage(outputDir + "frame_0.jpg", outputDir + "frame_0_scaled.jpg", 800, 600, "scale");
        
        // 添加水印（假设有水印文件）
        // addWatermark(outputDir + "frame_0.jpg", "watermark.png", outputDir + "frame_0_watermarked.jpg", 10, 10);
    }
}