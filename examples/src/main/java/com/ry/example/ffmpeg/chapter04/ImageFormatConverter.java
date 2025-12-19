package com.ry.example.ffmpeg.chapter04;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.plugins.png.PNGImageWriteParam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * 图像格式转换器
 * 支持PNG、BMP、GIF等格式的转换和处理
 */
public class ImageFormatConverter {
    
    /**
     * 从视频中提取PNG图片
     */
    public static void extractFrameAsPNG(String inputVideo, String outputFile, int frameIndex, int compressionLevel) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            grabber.setVideoFrameNumber(frameIndex);
            Frame frame = grabber.grab();
            
            if (frame != null && frame.image != null) {
                saveFrameAsPNG(frame, outputFile, compressionLevel);
                System.out.println("成功提取第 " + frameIndex + " 帧为PNG: " + outputFile);
            }
            
            grabber.stop();
        } catch (Exception e) {
            System.err.println("提取PNG帧失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存帧为PNG格式
     */
    private static void saveFrameAsPNG(Frame frame, String outputFile, int compressionLevel) throws IOException {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage image = converter.getBufferedImage(frame);
        
        File file = new File(outputFile);
        file.getParentFile().mkdirs();
        
        ImageIO.write(image, "png", file);
        
        // 设置PNG压缩级别（需要通过ImageIO参数）
        System.out.println("PNG压缩级别: " + compressionLevel + " (0=无压缩, 9=最大压缩)");
    }
    
    /**
     * 从视频中提取BMP图片
     */
    public static void extractFrameAsBMP(String inputVideo, String outputFile, int frameIndex) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            grabber.setVideoFrameNumber(frameIndex);
            Frame frame = grabber.grab();
            
            if (frame != null && frame.image != null) {
                saveFrameAsBMP(frame, outputFile);
                System.out.println("成功提取第 " + frameIndex + " 帧为BMP: " + outputFile);
            }
            
            grabber.stop();
        } catch (Exception e) {
            System.err.println("提取BMP帧失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存帧为BMP格式
     */
    private static void saveFrameAsBMP(Frame frame, String outputFile) throws IOException {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage image = converter.getBufferedImage(frame);
        
        File file = new File(outputFile);
        file.getParentFile().mkdirs();
        
        ImageIO.write(image, "bmp", file);
    }
    
    /**
     * 创建GIF动画
     */
    public static void createGIFAnimation(String inputVideo, String outputFile, int fps, int width, int height) {
        try {
            // 使用FFmpeg创建GIF
            String command = String.format(
                "ffmpeg -i %s -vf fps=%d,scale=%d:%d:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse -loop 0 %s",
                inputVideo, fps, width, height, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("GIF动画创建成功: " + outputFile);
            } else {
                System.err.println("GIF动画创建失败");
            }
        } catch (Exception e) {
            System.err.println("创建GIF动画失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建高质量GIF
     */
    public static void createHighQualityGIF(String inputVideo, String outputFile, int fps, int width, int height) {
        try {
            // 生成调色板
            String paletteFile = outputFile.replace(".gif", "_palette.png");
            String paletteCommand = String.format(
                "ffmpeg -i %s -vf fps=%d,scale=%d:%d:flags=lanczos,palettegen -y %s",
                inputVideo, fps, width, height, paletteFile);
            
            Process paletteProcess = Runtime.getRuntime().exec(paletteCommand);
            paletteProcess.waitFor();
            
            if (paletteProcess.exitValue() == 0) {
                // 使用调色板创建GIF
                String gifCommand = String.format(
                    "ffmpeg -i %s -i %s -lavfi fps=%d,scale=%d:%d:flags=lanczos[x];[x][1:v]paletteuse -y %s",
                    inputVideo, paletteFile, fps, width, height, outputFile);
                
                Process gifProcess = Runtime.getRuntime().exec(gifCommand);
                gifProcess.waitFor();
                
                if (gifProcess.exitValue() == 0) {
                    System.out.println("高质量GIF创建成功: " + outputFile);
                    // 删除临时调色板文件
                    new File(paletteFile).delete();
                } else {
                    System.err.println("高质量GIF创建失败");
                }
            }
        } catch (Exception e) {
            System.err.println("创建高质量GIF失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量格式转换
     */
    public static void batchConvertFormat(String inputDir, String outputDir, String sourceFormat, String targetFormat) {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("输入目录不存在: " + inputDir);
            return;
        }
        
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        File[] files = dir.listFiles((file, name) -> name.toLowerCase().endsWith("." + sourceFormat.toLowerCase()));
        
        if (files != null) {
            for (File file : files) {
                String outputFileName = file.getName().replace("." + sourceFormat, "." + targetFormat);
                String outputPath = outputDir + "/" + outputFileName;
                
                convertImageFormat(file.getAbsolutePath(), outputPath, targetFormat);
            }
        }
    }
    
    /**
     * 单个图像格式转换
     */
    public static void convertImageFormat(String inputFile, String outputFile, String targetFormat) {
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
     * 图像优化
     */
    public static void optimizeImage(String inputFile, String outputFile, String format) {
        try {
            String command;
            switch (format.toLowerCase()) {
                case "png":
                    command = String.format("ffmpeg -i %s -compression_level 9 -pred all %s", inputFile, outputFile);
                    break;
                case "gif":
                    command = String.format("ffmpeg -i %s -vf split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse %s", inputFile, outputFile);
                    break;
                default:
                    command = String.format("ffmpeg -i %s %s", inputFile, outputFile);
                    break;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("图像优化完成: " + outputFile);
                
                // 显示文件大小对比
                File originalFile = new File(inputFile);
                File optimizedFile = new File(outputFile);
                System.out.println("原始大小: " + originalFile.length() + " bytes");
                System.out.println("优化后大小: " + optimizedFile.length() + " bytes");
            }
        } catch (Exception e) {
            System.err.println("图像优化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建循环GIF
     */
    public static void createLoopedGIF(String inputVideo, String outputFile, int startTime, int duration, int fps) {
        try {
            String command = String.format(
                "ffmpeg -i %s -ss %d -t %d -vf fps=%d,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse -loop 0 %s",
                inputVideo, startTime, duration, fps, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("循环GIF创建成功: " + outputFile);
            } else {
                System.err.println("循环GIF创建失败");
            }
        } catch (Exception e) {
            System.err.println("创建循环GIF失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 添加文字到GIF
     */
    public static void addTextToGIF(String inputGIF, String outputFile, String text) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf drawtext=text='%s':fontfile=arial.ttf:x=10:y=10:fontsize=24:fontcolor=white %s",
                inputGIF, text, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("添加文字到GIF成功: " + outputFile);
            } else {
                System.err.println("添加文字到GIF失败");
            }
        } catch (Exception e) {
            System.err.println("添加文字到GIF失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/images/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 提取PNG图片
        extractFrameAsPNG(inputVideo, outputDir + "frame.png", 0, 6);
        
        // 提取BMP图片
        extractFrameAsBMP(inputVideo, outputDir + "frame.bmp", 0);
        
        // 创建GIF动画
        createGIFAnimation(inputVideo, outputDir + "animation.gif", 10, 320, 240);
        
        // 创建高质量GIF
        createHighQualityGIF(inputVideo, outputDir + "high_quality.gif", 15, 480, 360);
        
        // 创建循环GIF
        createLoopedGIF(inputVideo, outputDir + "looped.gif", 5, 10, 8);
        
        // 格式转换
        convertImageFormat(outputDir + "frame.png", outputDir + "frame_converted.jpg", "jpg");
        
        // 图像优化
        optimizeImage(outputDir + "frame.png", outputDir + "frame_optimized.png", "png");
        
        // 批量转换
        batchConvertFormat(outputDir, outputDir + "converted/", "png", "jpg");
    }
}