package com.ry.example.ffmpeg.chapter06;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频变换处理器
 * 处理视频的缩放、旋转、裁剪、填充等变换操作
 */
public class VideoTransformer {
    
    /**
     * 缩放视频
     */
    public static void scaleVideo(String inputFile, String outputFile, int width, int height) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"scale=%d:%d\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, width, height, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频缩放完成: " + outputFile);
                System.out.println("新尺寸: " + width + "x" + height);
            } else {
                System.err.println("视频缩放失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频缩放异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 按比例缩放视频
     */
    public static void scaleVideoKeepAspectRatio(String inputFile, String outputFile, int width, int height) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"scale=%d:%d:force_original_aspect_ratio=decrease,pad=%d:%d:(ow-iw)/2:(oh-ih)/2\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, width, height, width, height, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频比例缩放完成: " + outputFile);
                System.out.println("目标尺寸: " + width + "x" + height);
            } else {
                System.err.println("视频比例缩放失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频比例缩放异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 裁剪视频
     */
    public static void cropVideo(String inputFile, String outputFile, 
                               int cropWidth, int cropHeight, int x, int y) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"crop=%d:%d:%d:%d\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, cropWidth, cropHeight, x, y, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频裁剪完成: " + outputFile);
                System.out.println("裁剪区域: " + cropWidth + "x" + cropHeight + " at (" + x + "," + y + ")");
            } else {
                System.err.println("视频裁剪失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频裁剪异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 自动居中裁剪
     */
    public static void centerCropVideo(String inputFile, String outputFile, int width, int height) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"crop=%d:%d:(iw-%d)/2:(ih-%d)/2\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, width, height, width, height, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频居中裁剪完成: " + outputFile);
                System.out.println("裁剪尺寸: " + width + "x" + height);
            } else {
                System.err.println("视频居中裁剪失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频居中裁剪异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 填充视频
     */
    public static void padVideo(String inputFile, String outputFile, 
                             int newWidth, int newHeight, int x, int y, String color) {
        try {
            String command = String.format(
                "ffmpeg -i %s -vf \"pad=%d:%d:%d:%d:%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, newWidth, newHeight, x, y, color, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频填充完成: " + outputFile);
                System.out.println("新尺寸: " + newWidth + "x" + newHeight + ", 颜色: " + color);
            } else {
                System.err.println("视频填充失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频填充异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 旋转视频
     */
    public static void rotateVideo(String inputFile, String outputFile, RotationType rotationType) {
        try {
            String filter;
            
            switch (rotationType) {
                case CLOCKWISE_90:
                    filter = "transpose=1";
                    break;
                case COUNTER_CLOCKWISE_90:
                    filter = "transpose=2";
                    break;
                case CLOCKWISE_180:
                    filter = "transpose=2,transpose=2";
                    break;
                default:
                    filter = "transpose=1";
                    break;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频旋转完成: " + outputFile);
                System.out.println("旋转类型: " + rotationType);
            } else {
                System.err.println("视频旋转失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频旋转异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 任意角度旋转
     */
    public static void rotateVideoCustom(String inputFile, String outputFile, double angleRadians) {
        try {
            String filter = String.format("rotate=%.6f", angleRadians);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("视频自定义旋转完成: " + outputFile);
                System.out.println("旋转角度: " + Math.toDegrees(angleRadians) + " 度");
            } else {
                System.err.println("视频自定义旋转失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频自定义旋转异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 翻转视频
     */
    public static void flipVideo(String inputFile, String outputFile, FlipType flipType) {
        try {
            String filter;
            
            switch (flipType) {
                case HORIZONTAL:
                    filter = "hflip";
                    break;
                case VERTICAL:
                    filter = "vflip";
                    break;
                case BOTH:
                    filter = "hflip,vflip";
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
                System.out.println("视频翻转完成: " + outputFile);
                System.out.println("翻转类型: " + flipType);
            } else {
                System.err.println("视频翻转失败");
            }
            
        } catch (Exception e) {
            System.err.println("视频翻转异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 组合变换（缩放+裁剪）
     */
    public static void scaleAndCrop(String inputFile, String outputFile, 
                                  int targetWidth, int targetHeight) {
        try {
            String filter = String.format("scale=%d:%d,fill=black,crop=%d:%d", 
                                        targetWidth, targetHeight, targetWidth, targetHeight);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("组合变换完成: " + outputFile);
                System.out.println("目标尺寸: " + targetWidth + "x" + targetHeight);
            } else {
                System.err.println("组合变换失败");
            }
            
        } catch (Exception e) {
            System.err.println("组合变换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量变换处理
     */
    public static void batchTransform(List<String> inputFiles, String outputDir, 
                                   TransformConfig config) {
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        for (int i = 0; i < inputFiles.size(); i++) {
            String inputFile = inputFiles.get(i);
            String outputFile = outputDir + "/transformed_" + (i + 1) + ".mp4";
            
            switch (config.transformType) {
                case SCALE:
                    scaleVideo(inputFile, outputFile, config.width, config.height);
                    break;
                case CROP:
                    cropVideo(inputFile, outputFile, config.width, config.height, config.x, config.y);
                    break;
                case PAD:
                    padVideo(inputFile, outputFile, config.width, config.height, 
                            config.x, config.y, config.color);
                    break;
                case ROTATE:
                    rotateVideo(inputFile, outputFile, config.rotationType);
                    break;
                case FLIP:
                    flipVideo(inputFile, outputFile, config.flipType);
                    break;
                default:
                    scaleVideo(inputFile, outputFile, config.width, config.height);
                    break;
            }
        }
        
        System.out.println("批量变换处理完成，输出目录: " + outputDir);
    }
    
    /**
     * 镜头移动效果
     */
    public static void addPanEffect(String inputFile, String outputFile, 
                                  int cropWidth, int cropHeight, int startX, int startY, 
                                  int endX, int endY) {
        try {
            String filter = String.format(
                "crop=%d:%d:%d+%d*sin(t)*%d:%d+%d*cos(t)*%d",
                cropWidth, cropHeight, startX, endX - startX, endX, startY, endY - startY, endY);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("镜头移动效果添加完成: " + outputFile);
            } else {
                System.err.println("镜头移动效果添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("镜头移动效果添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 缩放镜头效果
     */
    public static void addZoomEffect(String inputFile, String outputFile, 
                                   int minScale, int maxScale) {
        try {
            String filter = String.format(
                "scale=iw*%d+%d*sin(t*2):ih*%d+%d*sin(t*2)",
                minScale, maxScale - minScale, minScale, maxScale - minScale);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("缩放镜头效果添加完成: " + outputFile);
            } else {
                System.err.println("缩放镜头效果添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("缩放镜头效果添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 旋转类型枚举
     */
    public enum RotationType {
        CLOCKWISE_90, COUNTER_CLOCKWISE_90, CLOCKWISE_180
    }
    
    /**
     * 翻转类型枚举
     */
    public enum FlipType {
        HORIZONTAL, VERTICAL, BOTH
    }
    
    /**
     * 变换类型枚举
     */
    public enum TransformType {
        SCALE, CROP, PAD, ROTATE, FLIP
    }
    
    /**
     * 变换配置类
     */
    public static class TransformConfig {
        public TransformType transformType;
        public int width, height;
        public int x, y;
        public String color;
        public RotationType rotationType;
        public FlipType flipType;
        
        public TransformConfig(TransformType transformType, int width, int height) {
            this.transformType = transformType;
            this.width = width;
            this.height = height;
        }
        
        public TransformConfig(TransformType transformType, int width, int height, int x, int y) {
            this.transformType = transformType;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
        
        public TransformConfig(TransformType transformType, int width, int height, 
                             int x, int y, String color) {
            this.transformType = transformType;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.color = color;
        }
        
        public TransformConfig(TransformType transformType, RotationType rotationType) {
            this.transformType = transformType;
            this.rotationType = rotationType;
        }
        
        public TransformConfig(TransformType transformType, FlipType flipType) {
            this.transformType = transformType;
            this.flipType = flipType;
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/transform/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 缩放视频
        scaleVideo(inputVideo, outputDir + "scaled_640x360.mp4", 640, 360);
        scaleVideoKeepAspectRatio(inputVideo, outputDir + "scaled_keep_aspect.mp4", 800, 600);
        
        // 裁剪视频
        cropVideo(inputVideo, outputDir + "cropped_400x300.mp4", 400, 300, 100, 50);
        centerCropVideo(inputVideo, outputDir + "center_cropped.mp4", 800, 600);
        
        // 填充视频
        padVideo(inputVideo, outputDir + "padded_black.mp4", 1920, 1080, 560, 260, "black");
        padVideo(inputVideo, outputDir + "padded_blue.mp4", 1920, 1080, 560, 260, "blue");
        
        // 旋转视频
        rotateVideo(inputVideo, outputDir + "rotated_90.mp4", RotationType.CLOCKWISE_90);
        rotateVideo(inputVideo, outputDir + "rotated_180.mp4", RotationType.CLOCKWISE_180);
        
        // 自定义角度旋转
        rotateVideoCustom(inputVideo, outputDir + "rotated_45.mp4", Math.PI / 4);
        
        // 翻转视频
        flipVideo(inputVideo, outputDir + "flipped_horizontal.mp4", FlipType.HORIZONTAL);
        flipVideo(inputVideo, outputDir + "flipped_vertical.mp4", FlipType.VERTICAL);
        
        // 组合变换
        scaleAndCrop(inputVideo, outputDir + "scale_and_crop.mp4", 800, 600);
        
        // 镜头移动效果
        addPanEffect(inputVideo, outputDir + "pan_effect.mp4", 400, 300, 0, 0, 100, 50);
        
        // 缩放镜头效果
        addZoomEffect(inputVideo, outputDir + "zoom_effect.mp4", 1, 2);
        
        // 批量处理
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add(inputVideo);
        inputFiles.add(inputVideo);
        
        TransformConfig scaleConfig = new TransformConfig(TransformType.SCALE, 640, 360);
        batchTransform(inputFiles, outputDir + "batch/", scaleConfig);
    }
}