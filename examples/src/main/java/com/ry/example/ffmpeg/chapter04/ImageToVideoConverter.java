package com.ry.example.ffmpeg.chapter04;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片转视频转换器
 * 实战项目：将静态图片序列转换为动态视频
 */
public class ImageToVideoConverter {
    
    /**
     * 基础图片转视频
     */
    public static void convertImagesToVideo(List<String> imagePaths, String outputFile, 
                                         int frameRate, int width, int height) {
        try {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height);
            recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
            recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameRate);
            recorder.setGopSize(frameRate * 2);
            recorder.setBitrate(2000000);
            
            recorder.start();
            
            Java2DFrameConverter converter = new Java2DFrameConverter();
            
            for (String imagePath : imagePaths) {
                try {
                    BufferedImage image = ImageIO.read(new File(imagePath));
                    if (image != null) {
                        // 调整图片尺寸
                        BufferedImage resizedImage = resizeImage(image, width, height);
                        Frame frame = converter.getFrame(resizedImage);
                        
                        // 每张图片重复指定帧数
                        int repeatFrames = frameRate * 3; // 每张图片显示3秒
                        for (int i = 0; i < repeatFrames; i++) {
                            recorder.record(frame);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("处理图片失败: " + imagePath + ", " + e.getMessage());
                }
            }
            
            recorder.stop();
            System.out.println("图片转视频完成: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("图片转视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 调整图片尺寸
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    
    /**
     * 创建带转场效果的视频
     */
    public static void createVideoWithTransitions(List<String> imagePaths, String outputFile, 
                                                int frameRate, int width, int height) {
        try {
            // 首先使用FFmpeg创建基础视频
            createBasicVideoFromImages(imagePaths, outputFile.replace(".mp4", "_basic.mp4"), 
                                      frameRate, width, height);
            
            // 然后使用FFmpeg添加转场效果
            String command = String.format(
                "ffmpeg -i %s -vf \"fade=in:0:30,fade=out:30:30\" -c:a copy %s",
                outputFile.replace(".mp4", "_basic.mp4"), outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("带转场效果的视频创建成功: " + outputFile);
                // 删除临时文件
                new File(outputFile.replace(".mp4", "_basic.mp4")).delete();
            } else {
                System.err.println("创建带转场效果的视频失败");
            }
            
        } catch (Exception e) {
            System.err.println("创建带转场效果的视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 使用FFmpeg创建基础视频
     */
    private static void createBasicVideoFromImages(List<String> imagePaths, String outputFile, 
                                                 int frameRate, int width, int height) {
        try {
            String inputPattern = imagePaths.get(0).replaceAll("\\d+", "%03d");
            String command = String.format(
                "ffmpeg -framerate %d -i %s -c:v libx264 -preset slow -crf 18 -pix_fmt yuv420p -s %dx%d %s",
                frameRate, inputPattern, width, height, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
        } catch (Exception e) {
            System.err.println("创建基础视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建带背景音乐的视频
     */
    public static void createVideoWithMusic(List<String> imagePaths, String outputFile, 
                                          String musicFile, int frameRate, int width, int height) {
        try {
            // 首先创建无音视频
            convertImagesToVideo(imagePaths, outputFile.replace(".mp4", "_silent.mp4"), 
                               frameRate, width, height);
            
            // 然后添加背景音乐
            String command = String.format(
                "ffmpeg -i %s -i %s -c:v copy -c:a aac -b:a 192k -shortest %s",
                outputFile.replace(".mp4", "_silent.mp4"), musicFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("带背景音乐的视频创建成功: " + outputFile);
                // 删除临时文件
                new File(outputFile.replace(".mp4", "_silent.mp4")).delete();
            } else {
                System.err.println("创建带背景音乐的视频失败");
            }
            
        } catch (Exception e) {
            System.err.println("创建带背景音乐的视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建带水印的视频
     */
    public static void createVideoWithWatermark(List<String> imagePaths, String outputFile, 
                                             String watermarkFile, int frameRate, int width, int height) {
        try {
            // 首先创建基础视频
            convertImagesToVideo(imagePaths, outputFile.replace(".mp4", "_basic.mp4"), 
                               frameRate, width, height);
            
            // 然后添加水印
            String command = String.format(
                "ffmpeg -i %s -i %s -filter_complex overlay=W-w-10:H-h-10 -c:a copy %s",
                outputFile.replace(".mp4", "_basic.mp4"), watermarkFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("带水印的视频创建成功: " + outputFile);
                // 删除临时文件
                new File(outputFile.replace(".mp4", "_basic.mp4")).delete();
            } else {
                System.err.println("创建带水印的视频失败");
            }
            
        } catch (Exception e) {
            System.err.println("创建带水印的视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建幻灯片效果的视频
     */
    public static void createSlideshowVideo(List<String> imagePaths, String outputFile, 
                                          int frameRate, int width, int height, String effect) {
        try {
            String command;
            
            switch (effect.toLowerCase()) {
                case "zoom":
                    command = String.format(
                        "ffmpeg -framerate %d -i %s -vf zoompan=z='if(lte(on,1),1,1.5)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125 -s %dx%d %s",
                        frameRate, imagePaths.get(0).replaceAll("\\d+", "%03d"), width, height, outputFile);
                    break;
                case "pan":
                    command = String.format(
                        "ffmpeg -framerate %d -i %s -vf zoompan=z=1:x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=1 -s %dx%d %s",
                        frameRate, imagePaths.get(0).replaceAll("\\d+", "%03d"), width, height, outputFile);
                    break;
                default:
                    convertImagesToVideo(imagePaths, outputFile, frameRate, width, height);
                    return;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("幻灯片视频创建成功，效果: " + effect + ", 文件: " + outputFile);
            } else {
                System.err.println("创建幻灯片视频失败");
            }
            
        } catch (Exception e) {
            System.err.println("创建幻灯片视频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量处理图片尺寸统一
     */
    public static List<String> normalizeImageSizes(List<String> imagePaths, int width, int height, String outputDir) {
        List<String> normalizedPaths = new ArrayList<>();
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        for (int i = 0; i < imagePaths.size(); i++) {
            String inputPath = imagePaths.get(i);
            String outputPath = outputDir + "/normalized_" + String.format("%03d", i) + ".jpg";
            
            try {
                String command = String.format("ffmpeg -i %s -s %dx%d -q:v 2 %s", 
                    inputPath, width, height, outputPath);
                
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                
                if (process.exitValue() == 0) {
                    normalizedPaths.add(outputPath);
                    System.out.println("图片尺寸调整完成: " + outputPath);
                }
            } catch (Exception e) {
                System.err.println("调整图片尺寸失败: " + inputPath);
                e.printStackTrace();
            }
        }
        
        return normalizedPaths;
    }
    
    /**
     * 扫描目录中的图片文件
     */
    public static List<String> scanImageFiles(String directory) {
        List<String> imagePaths = new ArrayList<>();
        File dir = new File(directory);
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                        name.endsWith(".png") || name.endsWith(".bmp")) {
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        
        // 按文件名排序
        imagePaths.sort(String::compareTo);
        return imagePaths;
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputDir = "input/images/";
        String outputDir = "output/video/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 扫描图片文件
        List<String> imagePaths = scanImageFiles(inputDir);
        
        if (imagePaths.isEmpty()) {
            System.out.println("未找到图片文件，请检查输入目录: " + inputDir);
            return;
        }
        
        System.out.println("找到 " + imagePaths.size() + " 个图片文件");
        
        // 统一图片尺寸
        List<String> normalizedPaths = normalizeImageSizes(imagePaths, 1920, 1080, outputDir + "normalized/");
        
        // 创建基础视频
        convertImagesToVideo(normalizedPaths, outputDir + "basic_video.mp4", 25, 1920, 1080);
        
        // 创建带转场效果的视频
        createVideoWithTransitions(normalizedPaths, outputDir + "transition_video.mp4", 25, 1920, 1080);
        
        // 创建幻灯片效果的视频（如果有背景音乐）
        // createVideoWithMusic(normalizedPaths, outputDir + "music_video.mp4", "background.mp3", 25, 1920, 1080);
        
        // 创建幻灯片效果的视频
        createSlideshowVideo(normalizedPaths, outputDir + "slideshow_zoom.mp4", 1, 1920, 1080, "zoom");
        
        System.out.println("所有视频创建完成！");
    }
}