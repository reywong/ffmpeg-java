package com.ry.example.ffmpeg.chapter03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3.1 音视频时间处理示例
 * 演示如何获取和处理视频的帧率、采样率、时间戳等时间相关信息
 */
public class VideoAudioTime {

    /**
     * 获取视频的帧率
     * @param inputPath 视频文件路径
     * @return 帧率字符串
     */
    public static String getVideoFrameRate(String inputPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", "v:0", 
                               "-show_entries", "stream=r_frame_rate", "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String frameRate = reader.readLine();
            reader.close();
            
            return frameRate != null ? frameRate.trim() : "Unknown";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * 获取音频的采样率
     * @param inputPath 音视频文件路径
     * @return 采样率
     */
    public static String getAudioSampleRate(String inputPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", "a:0", 
                               "-show_entries", "stream=sample_rate", "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String sampleRate = reader.readLine();
            reader.close();
            
            return sampleRate != null ? sampleRate.trim() : "Unknown";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * 获取时间基准
     * @param inputPath 文件路径
     * @param streamType 流类型 (v=视频, a=音频)
     * @return 时间基准
     */
    public static String getTimeBase(String inputPath, String streamType) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", streamType + ":0", 
                               "-show_entries", "stream=time_base", "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String timeBase = reader.readLine();
            reader.close();
            
            return timeBase != null ? timeBase.trim() : "Unknown";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * 计算视频总时长（秒）
     * @param inputPath 视频文件路径
     * @return 总时长（秒）
     */
    public static double getVideoDuration(String inputPath) {
        try {
            String[] command = {"ffprobe", "-v", "error", "-show_entries", 
                               "format=duration", "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String durationStr = reader.readLine();
            reader.close();
            
            if (durationStr != null && !durationStr.isEmpty()) {
                return Double.parseDouble(durationStr.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * 计算视频总帧数
     * @param inputPath 视频文件路径
     * @return 总帧数
     */
    public static int getTotalFrames(String inputPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", "v:0", 
                               "-count_frames", "-show_entries", "stream=nb_frames", 
                               "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String framesStr = reader.readLine();
            reader.close();
            
            if (framesStr != null && !framesStr.isEmpty()) {
                return Integer.parseInt(framesStr.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 解析帧率字符串并计算实际帧率
     * @param frameRateStr 帧率字符串，如 "30/1" 或 "25/1"
     * @return 实际帧率
     */
    public static double parseFrameRate(String frameRateStr) {
        if (frameRateStr == null || frameRateStr.equals("Unknown") || frameRateStr.equals("Error")) {
            return 0.0;
        }
        
        try {
            // 解理 "30/1" 格式
            if (frameRateStr.contains("/")) {
                String[] parts = frameRateStr.split("/");
                if (parts.length == 2) {
                    double numerator = Double.parseDouble(parts[0]);
                    double denominator = Double.parseDouble(parts[1]);
                    return numerator / denominator;
                }
            } else {
                // 直接是数字格式
                return Double.parseDouble(frameRateStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * 设置视频帧率
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param targetFrameRate 目标帧率
     * @return 是否成功
     */
    public static boolean setFrameRate(String inputPath, String outputPath, int targetFrameRate) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-r", String.valueOf(targetFrameRate), 
                               "-c:v", "libx264", "-c:a", "copy", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置音频采样率
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param targetSampleRate 目标采样率
     * @return 是否成功
     */
    public static boolean setSampleRate(String inputPath, String outputPath, int targetSampleRate) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-ar", String.valueOf(targetSampleRate), 
                               "-c:v", "copy", "-c:a", "aac", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取详细的音视频时间信息
     * @param inputPath 文件路径
     * @return 时间信息字符串
     */
    public static String getTimeInfo(String inputPath) {
        StringBuilder info = new StringBuilder();
        
        String videoFrameRate = getVideoFrameRate(inputPath);
        String audioSampleRate = getAudioSampleRate(inputPath);
        String videoTimeBase = getTimeBase(inputPath, "v");
        String audioTimeBase = getTimeBase(inputPath, "a");
        double duration = getVideoDuration(inputPath);
        int totalFrames = getTotalFrames(inputPath);
        double actualFrameRate = parseFrameRate(videoFrameRate);
        
        info.append("=== 音视频时间信息 ===\n");
        info.append("文件路径: ").append(inputPath).append("\n");
        info.append("视频帧率: ").append(videoFrameRate).append(" (").append(actualFrameRate).append(" fps)\n");
        info.append("音频采样率: ").append(audioSampleRate).append(" Hz\n");
        info.append("视频时间基准: ").append(videoTimeBase).append("\n");
        info.append("音频时间基准: ").append(audioTimeBase).append("\n");
        info.append("总时长: ").append(String.format("%.2f", duration)).append(" 秒\n");
        info.append("总帧数: ").append(totalFrames).append("\n");
        
        if (actualFrameRate > 0) {
            double calculatedDuration = totalFrames / actualFrameRate;
            info.append("计算时长: ").append(String.format("%.2f", calculatedDuration)).append(" 秒\n");
        }
        
        return info.toString();
    }

    /**
     * 主方法示例
     */
    public static void main(String[] args) {
        // 示例：获取视频文件的时间信息
        String videoPath = "input.mp4";
        
        System.out.println("=== FFmpeg 音视频时间处理示例 ===\n");
        
        // 获取并显示时间信息
        String timeInfo = getTimeInfo(videoPath);
        System.out.println(timeInfo);
        
        // 示例：设置新的帧率
        String outputPath = "output_30fps.mp4";
        boolean success = setFrameRate(videoPath, outputPath, 30);
        
        if (success) {
            System.out.println("帧率设置成功！输出文件: " + outputPath);
            
            // 显示修改后的信息
            String newTimeInfo = getTimeInfo(outputPath);
            System.out.println("\n" + newTimeInfo);
        } else {
            System.out.println("帧率设置失败！");
        }
    }
}