package com.ry.example.ffmpeg.chapter03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 3.3 合并音视频示例
 * 演示如何合并视频和音频流、重新编码、合并多个视频文件等操作
 */
public class MergeAudioVideo {

    /**
     * 合并视频流和音频流
     * @param videoPath 视频文件路径
     * @param audioPath 音频文件路径
     * @param outputPath 输出文件路径
     * @param copyVideo 是否复制视频流（不重新编码）
     * @param audioCodec 音频编码器 (如 "aac", "mp3", "copy")
     * @return 是否成功
     */
    public static boolean mergeVideoAudio(String videoPath, String audioPath, String outputPath, 
                                        boolean copyVideo, String audioCodec) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(videoPath);
            command.add("-i");
            command.add(audioPath);
            
            if (copyVideo) {
                command.add("-c:v");
                command.add("copy");
            } else {
                command.add("-c:v");
                command.add("libx264");
            }
            
            command.add("-c:a");
            command.add(audioCodec);
            command.add(outputPath);
            
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 重新编码视频流
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param codec 视频编码器 (如 "libx264", "libx265", "libvpx")
     * @param crf 质量参数 (18-28, 18为最高质量)
     * @param preset 编码速度预设 (如 "ultrafast", "fast", "medium", "slow")
     * @return 是否成功
     */
    public static boolean reencodeVideo(String inputPath, String outputPath, String codec, 
                                       int crf, String preset) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-c:v", codec, 
                               "-crf", String.valueOf(crf), "-preset", preset, 
                               "-c:a", "copy", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 调整视频分辨率
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param width 目标宽度
     * @param height 目标高度 (-1表示保持宽高比)
     * @return 是否成功
     */
    public static boolean resizeVideo(String inputPath, String outputPath, int width, int height) {
        try {
            String scaleFilter = height == -1 ? "scale=" + width + ":-1" : "scale=" + width + ":" + height;
            String[] command = {"ffmpeg", "-i", inputPath, "-vf", scaleFilter, 
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
     * 格式转换
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param videoCodec 视频编码器
     * @param audioCodec 音频编码器
     * @return 是否成功
     */
    public static boolean convertFormat(String inputPath, String outputPath, 
                                        String videoCodec, String audioCodec) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-c:v", videoCodec, 
                               "-c:a", audioCodec, outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用concat协议合并视频文件
     * @param videoFiles 视频文件路径数组
     * @param outputPath 输出文件路径
     * @param copyStream 是否复制流（不重新编码）
     * @return 是否成功
     */
    public static boolean concatVideos(String[] videoFiles, String outputPath, boolean copyStream) {
        try {
            // 创建文件列表
            String fileListPath = "concat_list.txt";
            FileWriter writer = new FileWriter(fileListPath);
            
            for (String videoFile : videoFiles) {
                // 转义文件路径中的特殊字符
                String escapedPath = videoFile.replace("'", "'\"'\"'");
                writer.write("file '" + escapedPath + "'\n");
            }
            writer.close();
            
            // 构建命令
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-f");
            command.add("concat");
            command.add("-safe");
            command.add("0");
            command.add("-i");
            command.add(fileListPath);
            
            if (copyStream) {
                command.add("-c");
                command.add("copy");
            }
            
            command.add(outputPath);
            
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            
            // 删除临时文件列表
            new File(fileListPath).delete();
            
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用concat滤镜合并视频（适用于不同格式的视频）
     * @param videoFiles 视频文件路径数组
     * @param outputPath 输出文件路径
     * @return 是否成功
     */
    public static boolean concatVideosWithFilter(String[] videoFiles, String outputPath) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            
            // 添加所有输入文件
            for (String videoFile : videoFiles) {
                command.add("-i");
                command.add(videoFile);
            }
            
            // 构建滤镜
            StringBuilder filterBuilder = new StringBuilder();
            StringBuilder mapBuilder = new StringBuilder();
            
            for (int i = 0; i < videoFiles.length; i++) {
                filterBuilder.append("[").append(i).append(":v]").append("[").append(i).append(":a]");
                mapBuilder.append(" -map \"[v]\" -map \"[a]\"");
            }
            
            filterBuilder.append("concat=n=").append(videoFiles.length)
                        .append(":v=1:a=1[v][a]");
            
            command.add("-filter_complex");
            command.add(filterBuilder.toString());
            command.add("-map");
            command.add("[v]");
            command.add("-map");
            command.add("[a]");
            command.add("-c:v");
            command.add("libx264");
            command.add("-c:a");
            command.add("aac");
            command.add(outputPath);
            
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加音频延迟
     * @param videoPath 视频文件路径
     * @param audioPath 音频文件路径
     * @param outputPath 输出文件路径
     * @param delayMs 延迟时间（毫秒）
     * @return 是否成功
     */
    public static boolean addAudioDelay(String videoPath, String audioPath, 
                                       String outputPath, int delayMs) {
        try {
            String[] command = {"ffmpeg", "-i", videoPath, "-i", audioPath, 
                               "-c:v", "copy", "-c:a", "aac", 
                               "-af", "adelay=" + delayMs + "|" + delayMs, 
                               outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 混合多个音频流
     * @param videoPath 视频文件路径
     * @param audioFiles 音频文件路径数组
     * @param outputPath 输出文件路径
     * @return 是否成功
     */
    public static boolean mixMultipleAudio(String videoPath, String[] audioFiles, String outputPath) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(videoPath);
            
            // 添加所有音频文件
            for (String audioFile : audioFiles) {
                command.add("-i");
                command.add(audioFile);
            }
            
            // 构建混合滤镜
            StringBuilder filterBuilder = new StringBuilder();
            for (int i = 0; i < audioFiles.length; i++) {
                filterBuilder.append("[").append(i + 1).append(":a]");
            }
            filterBuilder.append("amix=inputs=").append(audioFiles.length).append("[a]");
            
            command.add("-filter_complex");
            command.add(filterBuilder.toString());
            command.add("-map");
            command.add("0:v");
            command.add("-map");
            command.add("[a]");
            command.add("-c:v");
            command.add("copy");
            command.add("-c:a");
            command.add("aac");
            command.add(outputPath);
            
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成多种分辨率的输出
     * @param inputPath 输入文件路径
     * @param outputPrefix 输出文件前缀
     * @return 是否成功
     */
    public static boolean generateMultipleResolutions(String inputPath, String outputPrefix) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(inputPath);
            
            // 1080p
            command.add("-vf");
            command.add("scale=1920:1080");
            command.add("-c:v");
            command.add("libx264");
            command.add("-b:v");
            command.add("5M");
            command.add(outputPrefix + "_1080p.mp4");
            
            // 720p
            command.add("-vf");
            command.add("scale=1280:720");
            command.add("-c:v");
            command.add("libx264");
            command.add("-b:v");
            command.add("2.5M");
            command.add(outputPrefix + "_720p.mp4");
            
            // 480p
            command.add("-vf");
            command.add("scale=854:480");
            command.add("-c:v");
            command.add("libx264");
            command.add("-b:v");
            command.add("1M");
            command.add(outputPrefix + "_480p.mp4");
            
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 主方法示例
     */
    public static void main(String[] args) {
        String videoPath = "video_only.mp4";
        String audioPath = "audio.mp3";
        
        System.out.println("=== FFmpeg 合并音视频示例 ===\n");
        
        // 合并视频和音频
        String mergedPath = "merged_output.mp4";
        if (mergeVideoAudio(videoPath, audioPath, mergedPath, true, "aac")) {
            System.out.println("音视频合并成功: " + mergedPath);
        }
        
        // 重新编码视频
        String reencodedPath = "reencoded_output.mp4";
        if (reencodeVideo(mergedPath, reencodedPath, "libx264", 23, "medium")) {
            System.out.println("视频重新编码成功: " + reencodedPath);
        }
        
        // 调整分辨率
        String resizedPath = "resized_output.mp4";
        if (resizeVideo(reencodedPath, resizedPath, 1280, -1)) {
            System.out.println("视频分辨率调整成功: " + resizedPath);
        }
        
        // 格式转换
        String convertedPath = "converted_output.avi";
        if (convertFormat(reencodedPath, convertedPath, "libxvid", "mp3")) {
            System.out.println("格式转换成功: " + convertedPath);
        }
        
        // 合并多个视频文件示例
        String[] videoFiles = {"video1.mp4", "video2.mp4", "video3.mp4"};
        String concatenatedPath = "concatenated_output.mp4";
        if (concatVideos(videoFiles, concatenatedPath, true)) {
            System.out.println("视频合并成功: " + concatenatedPath);
        }
        
        // 生成多分辨率输出
        if (generateMultipleResolutions(reencodedPath, "multi_resolution")) {
            System.out.println("多分辨率输出生成成功");
        }
    }
}