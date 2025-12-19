package com.ry.example.ffmpeg.chapter03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 3.2 分离音视频示例
 * 演示如何从视频中分离音频流、复制视频流、切割视频等操作
 */
public class SeparateAudioVideo {

    /**
     * 原样复制视频流（不重新编码）
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @return 是否成功
     */
    public static boolean copyVideoStream(String inputPath, String outputPath) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-c:v", "copy", "-an", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 原样复制音频流
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @return 是否成功
     */
    public static boolean copyAudioStream(String inputPath, String outputPath) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-c:a", "copy", "-vn", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从视频提取音频为MP3格式
     * @param inputPath 输入文件路径
     * @param outputPath 输出MP3文件路径
     * @param quality 质量参数 (0-9, 0为最高质量)
     * @return 是否成功
     */
    public static boolean extractAudioToMp3(String inputPath, String outputPath, int quality) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-q:a", String.valueOf(quality), outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从视频提取音频为AAC格式
     * @param inputPath 输入文件路径
     * @param outputPath 输出AAC文件路径
     * @param bitrate 比特率 (如 "128k", "192k", "256k")
     * @return 是否成功
     */
    public static boolean extractAudioToAac(String inputPath, String outputPath, String bitrate) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-c:a", "aac", "-b:a", bitrate, outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从视频提取音频为WAV格式
     * @param inputPath 输入文件路径
     * @param outputPath 输出WAV文件路径
     * @param sampleRate 采样率 (如 44100, 48000)
     * @return 是否成功
     */
    public static boolean extractAudioToWav(String inputPath, String outputPath, int sampleRate) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-ar", String.valueOf(sampleRate), outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 切割视频文件（按时间）
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param startTime 开始时间（秒）
     * @param duration 持续时间（秒）
     * @param copyStream 是否复制流（不重新编码）
     * @return 是否成功
     */
    public static boolean cutVideo(String inputPath, String outputPath, int startTime, int duration, boolean copyStream) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-ss");
            command.add(String.valueOf(startTime));
            command.add("-i");
            command.add(inputPath);
            command.add("-t");
            command.add(String.valueOf(duration));
            
            if (copyStream) {
                command.add("-c");
                command.add("copy");
            }
            
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
     * 分段切割视频
     * @param inputPath 输入文件路径
     * @param outputPrefix 输出文件前缀
     * @param segmentDuration 每段时长（秒）
     * @return 是否成功
     */
    public static boolean segmentVideo(String inputPath, String outputPrefix, int segmentDuration) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-f", "segment", 
                               "-segment_time", String.valueOf(segmentDuration), 
                               "-c", "copy", outputPrefix + "_%03d.mp4"};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 选择特定流进行处理
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param streamIndex 流索引 (0表示第一个视频流，1表示第一个音频流等)
     * @param streamType 流类型 ("v" 视频, "a" 音频)
     * @return 是否成功
     */
    public static boolean selectStream(String inputPath, String outputPath, int streamIndex, String streamType) {
        try {
            String[] command = {"ffmpeg", "-i", inputPath, "-map", "0:" + streamType + ":" + streamIndex, 
                               "-c", "copy", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件的流信息
     * @param inputPath 文件路径
     * @return 流信息字符串
     */
    public static String getStreamInfo(String inputPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-show_entries", 
                               "stream=index,codec_name,codec_type,language", 
                               "-of", "csv=p=0", inputPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            
            info.append("=== 流信息 ===\n");
            while ((line = reader.readLine()) != null) {
                info.append(line).append("\n");
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取流信息失败";
        }
    }

    /**
     * 批量切割视频为多个片段
     * @param inputPath 输入文件路径
     * @param segments 切割片段数组，每个元素为 [开始时间, 持续时间]
     * @param outputPrefix 输出文件前缀
     * @return 成功切割的片段数量
     */
    public static int batchCutVideo(String inputPath, int[][] segments, String outputPrefix) {
        int successCount = 0;
        
        for (int i = 0; i < segments.length; i++) {
            String outputPath = outputPrefix + "_segment_" + (i + 1) + ".mp4";
            boolean success = cutVideo(inputPath, outputPath, segments[i][0], segments[i][1], true);
            
            if (success) {
                successCount++;
                System.out.println("成功切割片段 " + (i + 1) + ": " + outputPath);
            } else {
                System.out.println("切割片段 " + (i + 1) + " 失败");
            }
        }
        
        return successCount;
    }

    /**
     * 主方法示例
     */
    public static void main(String[] args) {
        String inputPath = "input.mp4";
        
        System.out.println("=== FFmpeg 分离音视频示例 ===\n");
        
        // 显示流信息
        String streamInfo = getStreamInfo(inputPath);
        System.out.println(streamInfo);
        
        // 提取视频流
        String videoOnlyPath = "video_only.mp4";
        if (copyVideoStream(inputPath, videoOnlyPath)) {
            System.out.println("视频流提取成功: " + videoOnlyPath);
        }
        
        // 提取音频为MP3
        String audioMp3Path = "audio.mp3";
        if (extractAudioToMp3(inputPath, audioMp3Path, 2)) {
            System.out.println("音频提取为MP3成功: " + audioMp3Path);
        }
        
        // 切割视频
        String clipPath = "video_clip.mp4";
        if (cutVideo(inputPath, clipPath, 10, 30, true)) {
            System.out.println("视频切割成功: " + clipPath + " (10-40秒)");
        }
        
        // 分段切割
        if (segmentVideo(inputPath, "segment", 60)) {
            System.out.println("视频分段切割成功，每段60秒");
        }
        
        // 批量切割示例
        int[][] segments = {
            {0, 30},   // 0-30秒
            {30, 30},  // 30-60秒
            {60, 60}   // 60-120秒
        };
        int successCount = batchCutVideo(inputPath, segments, "batch_cut");
        System.out.println("批量切割完成，成功 " + successCount + " 个片段");
    }
}