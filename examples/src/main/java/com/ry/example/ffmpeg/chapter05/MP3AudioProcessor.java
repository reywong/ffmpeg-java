package com.ry.example.ffmpeg.chapter05;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MP3音频处理器
 * 处理MP3格式的音频转换和优化
 */
public class MP3AudioProcessor {
    
    /**
     * 将音频转换为MP3格式
     */
    public static void convertToMP3(String inputFile, String outputFile, float quality) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
            grabber.start();
            
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 0, grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
            recorder.setAudioBitrate((int)(quality * 320000)); // 质量转换为比特率
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFormat("mp3");
            recorder.start();
            
            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
            
            recorder.stop();
            grabber.stop();
            
            System.out.println("MP3转换完成: " + outputFile);
            System.out.println("质量设置: " + quality);
            
        } catch (Exception e) {
            System.err.println("MP3转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从视频中提取MP3音频
     */
    public static void extractMP3FromVideo(String videoFile, String outputFile, float quality) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();
            
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 0, grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
            recorder.setAudioBitrate((int)(quality * 320000));
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFormat("mp3");
            recorder.start();
            
            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
            
            recorder.stop();
            grabber.stop();
            
            System.out.println("从视频提取MP3完成: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("从视频提取MP3失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量转换为MP3格式
     */
    public static void batchConvertToMP3(String inputDir, String outputDir, float quality) {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("输入目录不存在: " + inputDir);
            return;
        }
        
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        File[] files = dir.listFiles((file, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".wav") || lowerName.endsWith(".flac") || 
                   lowerName.endsWith(".aac") || lowerName.endsWith(".ogg");
        });
        
        if (files != null) {
            for (File file : files) {
                String outputFileName = file.getName().replaceAll("\\.[^.]+$", ".mp3");
                String outputPath = outputDir + "/" + outputFileName;
                
                convertToMP3(file.getAbsolutePath(), outputPath, quality);
            }
        }
    }
    
    /**
     * 设置MP3比特率
     */
    public static void convertToMP3WithBitrate(String inputFile, String outputFile, int bitrate) {
        try {
            String command = String.format(
                "ffmpeg -i %s -c:a libmp3lame -b:a %dk %s",
                inputFile, bitrate, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("MP3转换完成: " + outputFile);
                System.out.println("比特率: " + bitrate + " kbps");
            } else {
                System.err.println("MP3转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("MP3转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建VBR模式的MP3
     */
    public static void convertToVBRMP3(String inputFile, String outputFile, int quality) {
        try {
            String command = String.format(
                "ffmpeg -i %s -c:a libmp3lame -q:a %d -vbr on %s",
                inputFile, quality, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("VBR MP3转换完成: " + outputFile);
                System.out.println("VBR质量: " + quality + " (0-9, 0为最高质量)");
            } else {
                System.err.println("VBR MP3转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("VBR MP3转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建ABR模式的MP3
     */
    public static void convertToABRMP3(String inputFile, String outputFile, int bitrate) {
        try {
            String command = String.format(
                "ffmpeg -i %s -c:a libmp3lame -b:a %dk -abr 1 %s",
                inputFile, bitrate, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("ABR MP3转换完成: " + outputFile);
                System.out.println("ABR比特率: " + bitrate + " kbps");
            } else {
                System.err.println("ABR MP3转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("ABR MP3转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * MP3音量标准化
     */
    public static void normalizeMP3Volume(String inputFile, String outputFile) {
        try {
            String command = String.format(
                "ffmpeg -i %s -af loudnorm=I=-16:TP=-1.5:LRA=11 -c:a libmp3lame -q:a 2 %s",
                inputFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("MP3音量标准化完成: " + outputFile);
            } else {
                System.err.println("MP3音量标准化失败");
            }
            
        } catch (Exception e) {
            System.err.println("MP3音量标准化异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 连接多个MP3文件
     */
    public static void concatenateMP3Files(List<String> inputFiles, String outputFile) {
        try {
            // 创建临时列表文件
            String listFile = "temp_mp3_list.txt";
            try (java.io.PrintWriter writer = new java.io.PrintWriter(listFile)) {
                for (String inputFile : inputFiles) {
                    writer.println("file '" + inputFile + "'");
                }
            }
            
            String command = String.format(
                "ffmpeg -f concat -safe 0 -i %s -c copy %s",
                listFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("MP3文件连接完成: " + outputFile);
                System.out.println("连接文件数量: " + inputFiles.size());
            } else {
                System.err.println("MP3文件连接失败");
            }
            
            // 删除临时文件
            new File(listFile).delete();
            
        } catch (Exception e) {
            System.err.println("MP3文件连接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 添加MP3标签信息
     */
    public static void addMP3Tags(String inputFile, String outputFile, 
                                 String title, String artist, String album) {
        try {
            String command = String.format(
                "ffmpeg -i %s -c copy -metadata title=\"%s\" -metadata artist=\"%s\" -metadata album=\"%s\" %s",
                inputFile, title, artist, album, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("MP3标签添加完成: " + outputFile);
            } else {
                System.err.println("MP3标签添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("MP3标签添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 截取MP3片段
     */
    public static void cutMP3Segment(String inputFile, String outputFile, 
                                   int startTime, int duration) {
        try {
            String command = String.format(
                "ffmpeg -i %s -ss %d -t %d -c copy %s",
                inputFile, startTime, duration, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("MP3片段截取完成: " + outputFile);
                System.out.println("起始时间: " + startTime + "秒, 时长: " + duration + "秒");
            } else {
                System.err.println("MP3片段截取失败");
            }
            
        } catch (Exception e) {
            System.err.println("MP3片段截取异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取MP3文件信息
     */
    public static void getMP3Info(String mp3File) {
        try {
            File file = new File(mp3File);
            long fileSize = file.length();
            
            System.out.println("=== MP3文件信息 ===");
            System.out.println("文件路径: " + mp3File);
            System.out.println("文件大小: " + fileSize + " bytes (" + String.format("%.2f", fileSize / 1024.0 / 1024.0) + " MB)");
            
            // 使用FFprobe获取详细信息
            String command = String.format("ffprobe -v quiet -print_format json -show_format -show_streams %s", mp3File);
            Process process = Runtime.getRuntime().exec(command);
            
            StringBuilder output = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("详细信息: " + output.toString());
            }
            
        } catch (Exception e) {
            System.err.println("获取MP3信息失败: " + e.getMessage());
        }
    }
    
    /**
     * MP3质量对比
     */
    public static void compareMP3Quality(String originalFile, String compressedFile) {
        System.out.println("=== MP3质量对比 ===");
        
        getMP3Info(originalFile);
        System.out.println();
        getMP3Info(compressedFile);
        
        // 计算压缩比
        File original = new File(originalFile);
        File compressed = new File(compressedFile);
        
        if (original.exists() && compressed.exists()) {
            double compressionRatio = (1.0 - (double)compressed.length() / original.length()) * 100;
            System.out.println("压缩比: " + String.format("%.2f", compressionRatio) + "%");
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputAudio = "input.wav";
        String inputVideo = "input.mp4";
        String outputDir = "output/mp3/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 基础MP3转换
        convertToMP3(inputAudio, outputDir + "audio_high.mp3", 0.8f);
        convertToMP3(inputAudio, outputDir + "audio_medium.mp3", 0.5f);
        convertToMP3(inputAudio, outputDir + "audio_low.mp3", 0.3f);
        
        // 从视频提取MP3
        extractMP3FromVideo(inputVideo, outputDir + "video_audio.mp3", 0.8f);
        
        // 设置比特率转换
        convertToMP3WithBitrate(inputAudio, outputDir + "audio_128kbps.mp3", 128);
        convertToMP3WithBitrate(inputAudio, outputDir + "audio_320kbps.mp3", 320);
        
        // VBR转换
        convertToVBRMP3(inputAudio, outputDir + "audio_vbr.mp3", 2);
        
        // ABR转换
        convertToABRMP3(inputAudio, outputDir + "audio_abr.mp3", 192);
        
        // 音量标准化
        normalizeMP3Volume(outputDir + "audio_high.mp3", outputDir + "audio_normalized.mp3");
        
        // 截取片段
        cutMP3Segment(outputDir + "audio_high.mp3", outputDir + "audio_segment.mp3", 10, 30);
        
        // 添加标签
        addMP3Tags(outputDir + "audio_high.mp3", outputDir + "audio_tagged.mp3", 
                  "测试音频", "测试艺术家", "测试专辑");
        
        // 连接MP3文件
        List<String> mp3Files = new ArrayList<>();
        mp3Files.add(outputDir + "audio_segment.mp3");
        mp3Files.add(outputDir + "audio_segment.mp3");
        concatenateMP3Files(mp3Files, outputDir + "audio_concatenated.mp3");
        
        // 获取MP3信息
        getMP3Info(outputDir + "audio_high.mp3");
        
        // 质量对比
        compareMP3Quality(inputAudio, outputDir + "audio_high.mp3");
        
        // 批量转换
        batchConvertToMP3("input_audio/", outputDir + "batch/", 0.7f);
    }
}