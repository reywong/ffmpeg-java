package com.ry.example.ffmpeg.chapter03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3.4 视频浏览与格式分析示例
 * 演示如何使用ffplay播放视频、使用ffprobe分析视频格式、封装H264为MP4等操作
 */
public class VideoAnalysisPlayer {

    /**
     * 使用ffplay播放视频
     * @param videoPath 视频文件路径
     * @return 进程对象，可用于控制播放
     */
    public static Process playVideo(String videoPath) {
        try {
            String[] command = {"ffplay", videoPath};
            Process process = new ProcessBuilder(command).start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 播放视频的特定时间段
     * @param videoPath 视频文件路径
     * @param startTime 开始时间（秒）
     * @param duration 持续时间（秒）
     * @return 进程对象
     */
    public static Process playVideoSegment(String videoPath, int startTime, int duration) {
        try {
            String[] command = {"ffplay", "-ss", String.valueOf(startTime), 
                               "-t", String.valueOf(duration), videoPath};
            Process process = new ProcessBuilder(command).start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 循环播放视频
     * @param videoPath 视频文件路径
     * @param loopCount 循环次数 (-1表示无限循环)
     * @return 进程对象
     */
    public static Process playVideoLoop(String videoPath, int loopCount) {
        try {
            String[] command = {"ffplay", "-loop", String.valueOf(loopCount), videoPath};
            Process process = new ProcessBuilder(command).start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 播放音频文件
     * @param audioPath 音频文件路径
     * @param volume 音量 (0-100)
     * @return 进程对象
     */
    public static Process playAudio(String audioPath, int volume) {
        try {
            String[] command = {"ffplay", "-vn", "-volume", String.valueOf(volume), audioPath};
            Process process = new ProcessBuilder(command).start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用滤镜播放视频
     * @param videoPath 视频文件路径
     * @param filterGraph 滤镜图 (如 "scale=640:480", "eq=brightness=0.1")
     * @return 进程对象
     */
    public static Process playVideoWithFilter(String videoPath, String filterGraph) {
        try {
            String[] command = {"ffplay", "-vf", filterGraph, videoPath};
            Process process = new ProcessBuilder(command).start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取视频的基本信息
     * @param videoPath 视频文件路径
     * @return 基本信息
     */
    public static String getBasicInfo(String videoPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-show_format", "-show_streams", videoPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            
            info.append("=== 基本信息 ===\n");
            while ((line = reader.readLine()) != null) {
                info.append(line).append("\n");
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取基本信息失败";
        }
    }

    /**
     * 获取视频的容器格式信息
     * @param videoPath 视频文件路径
     * @return 容器格式信息
     */
    public static String getFormatInfo(String videoPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-show_format", "-of", "csv=p=0", videoPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            
            info.append("=== 容器格式信息 ===\n");
            while ((line = reader.readLine()) != null) {
                info.append(line).append("\n");
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取格式信息失败";
        }
    }

    /**
     * 获取视频的流信息
     * @param videoPath 视频文件路径
     * @return 流信息
     */
    public static String getStreamInfo(String videoPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-show_streams", "-of", "csv=p=0", videoPath};
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
     * 获取视频的帧信息
     * @param videoPath 视频文件路径
     * @param frameCount 要获取的帧数
     * @return 帧信息
     */
    public static String getFrameInfo(String videoPath, int frameCount) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", "v:0", 
                               "-show_frames", "-of", "csv=p=0", videoPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            int count = 0;
            
            info.append("=== 视频帧信息 (前").append(frameCount).append("帧) ===\n");
            while ((line = reader.readLine()) != null && count < frameCount) {
                info.append(line).append("\n");
                count++;
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取帧信息失败";
        }
    }

    /**
     * 获取视频的数据包信息
     * @param videoPath 视频文件路径
     * @param packetCount 要获取的数据包数
     * @return 数据包信息
     */
    public static String getPacketInfo(String videoPath, int packetCount) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-show_packets", "-of", "csv=p=0", videoPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            int count = 0;
            
            info.append("=== 数据包信息 (前").append(packetCount).append("包) ===\n");
            while ((line = reader.readLine()) != null && count < packetCount) {
                info.append(line).append("\n");
                count++;
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取数据包信息失败";
        }
    }

    /**
     * 解析视频参数（分辨率、比特率等）
     * @param videoPath 视频文件路径
     * @return 视频参数信息
     */
    public static String parseVideoParams(String videoPath) {
        try {
            String[] command = {"ffprobe", "-v", "quiet", "-select_streams", "v:0", 
                               "-show_entries", "stream=width,height,bit_rate,r_frame_rate,duration", 
                               "-of", "csv=p=0", videoPath};
            Process process = new ProcessBuilder(command).start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder info = new StringBuilder();
            String line;
            
            info.append("=== 视频参数解析 ===\n");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    switch (key) {
                        case "width":
                            info.append("视频宽度: ").append(value).append(" 像素\n");
                            break;
                        case "height":
                            info.append("视频高度: ").append(value).append(" 像素\n");
                            break;
                        case "bit_rate":
                            info.append("视频比特率: ").append(value).append(" bps\n");
                            break;
                        case "r_frame_rate":
                            info.append("帧率: ").append(value).append(" fps\n");
                            break;
                        case "duration":
                            info.append("时长: ").append(value).append(" 秒\n");
                            break;
                    }
                }
            }
            reader.close();
            
            return info.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "解析视频参数失败";
        }
    }

    /**
     * 将H.264裸流封装为MP4格式
     * @param h264Path H.264文件路径
     * @param outputPath 输出MP4文件路径
     * @param frameRate 帧率
     * @return 是否成功
     */
    public static boolean encapsulateH264ToMp4(String h264Path, String outputPath, int frameRate) {
        try {
            String[] command = {"ffmpeg", "-i", h264Path, "-r", String.valueOf(frameRate), 
                               "-c:v", "copy", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将H.264裸流封装为MP4格式（自动生成时间戳）
     * @param h264Path H.264文件路径
     * @param outputPath 输出MP4文件路径
     * @return 是否成功
     */
    public static boolean encapsulateH264WithTimestamps(String h264Path, String outputPath) {
        try {
            String[] command = {"ffmpeg", "-fflags", "+genpts", "-i", h264Path, 
                               "-r", "25", "-c:v", "copy", outputPath};
            Process process = new ProcessBuilder(command).start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加音频流到H.264封装的MP4
     * @param h264Path H.264文件路径
     * @param audioPath 音频文件路径
     * @param outputPath 输出MP4文件路径
     * @return 是否成功
     */
    public static boolean encapsulateH264WithAudio(String h264Path, String audioPath, String outputPath) {
        try {
            String[] command = {"ffmpeg", "-i", h264Path, "-i", audioPath, 
                               "-r", "25", "-c:v", "copy", "-c:a", "aac", outputPath};
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
        String videoPath = "input.mp4";
        String h264Path = "input.h264";
        String audioPath = "audio.mp3";
        
        System.out.println("=== FFmpeg 视频分析播放示例 ===\n");
        
        // 获取视频信息
        System.out.println(getBasicInfo(videoPath));
        System.out.println(getFormatInfo(videoPath));
        System.out.println(getStreamInfo(videoPath));
        System.out.println(parseVideoParams(videoPath));
        
        // 获取帧信息和数据包信息
        System.out.println(getFrameInfo(videoPath, 5));
        System.out.println(getPacketInfo(videoPath, 10));
        
        // 封装H.264为MP4
        if (encapsulateH264ToMp4(h264Path, "output_mp4.mp4", 25)) {
            System.out.println("H.264封装为MP4成功");
        }
        
        if (encapsulateH264WithAudio(h264Path, audioPath, "output_with_audio.mp4")) {
            System.out.println("H.264带音频封装成功");
        }
        
        // 播放示例（注释掉，避免实际启动播放器）
        /*
        Process player = playVideo(videoPath);
        if (player != null) {
            System.out.println("视频播放器已启动");
            // 可以调用 player.destroy() 来停止播放
        }
        
        Process audioPlayer = playAudio(audioPath, 50);
        if (audioPlayer != null) {
            System.out.println("音频播放器已启动");
        }
        */
        
        System.out.println("示例执行完成");
    }
}