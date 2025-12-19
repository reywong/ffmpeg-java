package com.ry.example.ffmpeg.chapter04;

import org.bytedeco.ffmpeg.ffmpeg;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * YUV图像处理器
 * 处理YUV格式的图像转换和保存
 */
public class YUVImageProcessor {
    
    /**
     * 将视频帧保存为YUV格式
     */
    public static void saveFrameAsYUV(String inputVideo, String outputFile, int frameIndex) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            // 跳转到指定帧
            grabber.setVideoFrameNumber(frameIndex);
            Frame frame = grabber.grab();
            
            if (frame != null && frame.image != null) {
                // 保存为原始YUV格式
                saveYUVFile(frame, outputFile, grabber.getVideoWidth(), grabber.getVideoHeight());
                System.out.println("成功保存第 " + frameIndex + " 帧为YUV格式: " + outputFile);
            }
            
            grabber.stop();
        } catch (Exception e) {
            System.err.println("保存YUV文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存YUV文件
     */
    private static void saveYUVFile(Frame frame, String outputFile, int width, height) throws IOException {
        // 这里简化处理，实际YUV格式转换需要更复杂的逻辑
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height)) {
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_RAWVIDEO);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFrameRate(1);
            recorder.start();
            recorder.record(frame);
            recorder.stop();
        }
    }
    
    /**
     * YUV格式转换为其他格式
     */
    public static void convertYUVToRGB(String yuvFile, String outputFile, int width, int height) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(yuvFile);
            grabber.setVideoOption("pixel_format", "yuv420p");
            grabber.setVideoWidth(width);
            grabber.setVideoHeight(height);
            grabber.start();
            
            Frame frame = grabber.grab();
            
            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage image = converter.getBufferedImage(frame);
                ImageIO.write(image, "PNG", new File(outputFile));
                System.out.println("YUV转PNG成功: " + outputFile);
            }
            
            grabber.stop();
        } catch (Exception e) {
            System.err.println("YUV转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量处理视频帧为YUV格式
     */
    public static void batchSaveYUV(String inputVideo, String outputPattern, int interval) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
            grabber.start();
            
            int frameCount = 0;
            Frame frame;
            int savedCount = 0;
            
            while ((frame = grabber.grab()) != null) {
                if (frameCount % interval == 0 && frame.image != null) {
                    String outputFile = String.format(outputPattern, savedCount);
                    saveYUVFile(frame, outputFile, grabber.getVideoWidth(), grabber.getVideoHeight());
                    savedCount++;
                    System.out.println("保存YUV文件: " + outputFile);
                }
                frameCount++;
            }
            
            grabber.stop();
            System.out.println("批量保存完成，共保存 " + savedCount + " 个YUV文件");
        } catch (Exception e) {
            System.err.println("批量保存YUV失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * YUV格式信息分析
     */
    public static void analyzeYUVFile(String yuvFile, int width, int height) {
        try {
            File file = new File(yuvFile);
            long fileSize = file.length();
            
            System.out.println("=== YUV文件分析 ===");
            System.out.println("文件路径: " + yuvFile);
            System.out.println("文件大小: " + fileSize + " bytes");
            System.out.println("图像尺寸: " + width + "x" + height);
            
            // 计算YUV420格式的预期大小
            int ySize = width * height;
            int uvSize = ySize / 4;
            long expectedSize = ySize + 2 * uvSize;
            
            System.out.println("YUV420预期大小: " + expectedSize + " bytes");
            System.out.println("文件状态: " + (fileSize == expectedSize ? "正常" : "异常"));
            
        } catch (Exception e) {
            System.err.println("分析YUV文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/yuv/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 保存单帧为YUV
        saveFrameAsYUV(inputVideo, outputDir + "frame_0.yuv", 0);
        
        // 批量保存YUV文件
        batchSaveYUV(inputVideo, outputDir + "frame_%03d.yuv", 30);
        
        // 分析YUV文件
        analyzeYUVFile(outputDir + "frame_0.yuv", 1920, 1080);
        
        // YUV转PNG
        convertYUVToRGB(outputDir + "frame_0.yuv", outputDir + "frame_0.png", 1920, 1080);
    }
}