package com.ry.example.ffmpeg.chapter05;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * PCM音频处理器
 * 处理PCM格式的音频转换和保存
 */
public class PCMAudioProcessor {
    
    /**
     * 从音频文件中提取PCM数据
     */
    public static void extractPCMFromAudio(String inputFile, String outputFile, 
                                         int sampleRate, int channels, String format) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
            grabber.start();
            
            // 设置PCM格式参数
            int sampleFormat = getSampleFormat(format);
            
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 0, 0);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setAudioBitrate(avutil.AV_SAMPLE_FMT_S16);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioChannels(channels);
            recorder.setFormat("s16le");
            recorder.start();
            
            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
            
            recorder.stop();
            grabber.stop();
            
            System.out.println("PCM提取完成: " + outputFile);
            System.out.println("采样率: " + sampleRate + "Hz, 声道数: " + channels);
            
        } catch (Exception e) {
            System.err.println("PCM提取失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从视频中提取PCM音频
     */
    public static void extractPCMFromVideo(String videoFile, String outputFile, 
                                         int sampleRate, int channels) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();
            
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 0, channels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setSampleRate(sampleRate);
            recorder.setFormat("s16le");
            recorder.start();
            
            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
            
            recorder.stop();
            grabber.stop();
            
            System.out.println("从视频中提取PCM完成: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("从视频提取PCM失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量转换为PCM格式
     */
    public static void batchConvertToPCM(String inputDir, String outputDir, 
                                        int sampleRate, int channels, String format) {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("输入目录不存在: " + inputDir);
            return;
        }
        
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        File[] files = dir.listFiles((file, name) -> 
            name.toLowerCase().endsWith(".mp3") || 
            name.toLowerCase().endsWith(".wav") || 
            name.toLowerCase().endsWith(".flac"));
        
        if (files != null) {
            for (File file : files) {
                String outputFileName = file.getName().replaceAll("\\.[^.]+$", ".pcm");
                String outputPath = outputDir + "/" + outputFileName;
                
                extractPCMFromAudio(file.getAbsolutePath(), outputPath, sampleRate, channels, format);
            }
        }
    }
    
    /**
     * 分析PCM音频数据
     */
    public static void analyzePCMAudio(String pcmFile, int sampleRate, int channels) {
        try {
            File file = new File(pcmFile);
            long fileSize = file.length();
            
            System.out.println("=== PCM音频分析 ===");
            System.out.println("文件路径: " + pcmFile);
            System.out.println("文件大小: " + fileSize + " bytes");
            System.out.println("采样率: " + sampleRate + " Hz");
            System.out.println("声道数: " + channels);
            
            // 计算音频时长（假设16位PCM）
            int bytesPerSample = 2; // 16位 = 2字节
            int samplesPerFrame = channels * bytesPerSample;
            long totalSamples = fileSize / samplesPerFrame;
            double duration = (double) totalSamples / sampleRate;
            
            System.out.println("样本总数: " + totalSamples);
            System.out.println("音频时长: " + String.format("%.2f", duration) + " 秒");
            
            // 读取部分数据进行分析
            analyzePCMData(pcmFile, sampleRate, channels);
            
        } catch (Exception e) {
            System.err.println("分析PCM音频失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 分析PCM数据内容
     */
    private static void analyzePCMData(String pcmFile, int sampleRate, int channels) {
        try {
            File file = new File(pcmFile);
            byte[] buffer = new byte[4096];
            
            try (FileInputStream fis = new FileInputStream(file)) {
                int bytesRead;
                long maxSample = 0;
                long minSample = 0;
                int sampleCount = 0;
                
                while ((bytesRead = fis.read(buffer)) != null && bytesRead > 0) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    
                    while (byteBuffer.remaining() >= 2) {
                        short sample = byteBuffer.getShort();
                        if (sample > maxSample) maxSample = sample;
                        if (sample < minSample) minSample = sample;
                        sampleCount++;
                    }
                }
                
                System.out.println("最大采样值: " + maxSample);
                System.out.println("最小采样值: " + minSample);
                System.out.println("采样点总数: " + sampleCount);
                System.out.println("动态范围: " + (maxSample - minSample));
                
                // 计算平均音量
                double avgVolume = (maxSample - minSample) / 2.0 / 32768.0 * 100;
                System.out.println("平均音量: " + String.format("%.2f", avgVolume) + "%");
            }
            
        } catch (IOException e) {
            System.err.println("读取PCM数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成正弦波PCM文件
     */
    public static void generateSineWavePCM(String outputFile, int sampleRate, int frequency, 
                                         int duration, double amplitude) {
        try {
            int channels = 1; // 单声道
            int bitsPerSample = 16;
            int bytesPerSample = bitsPerSample / 8;
            int totalSamples = sampleRate * duration;
            int bufferSize = totalSamples * channels * bytesPerSample;
            
            byte[] buffer = new byte[bufferSize];
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
            for (int i = 0; i < totalSamples; i++) {
                double time = (double) i / sampleRate;
                double angle = 2 * Math.PI * frequency * time;
                short sample = (short) (amplitude * Short.MAX_VALUE * Math.sin(angle));
                
                byteBuffer.putShort(sample);
            }
            
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(buffer);
            }
            
            System.out.println("正弦波PCM生成完成: " + outputFile);
            System.out.println("频率: " + frequency + "Hz, 时长: " + duration + "秒");
            
        } catch (IOException e) {
            System.err.println("生成正弦波PCM失败: " + e.getMessage());
        }
    }
    
    /**
     * PCM格式转换
     */
    public static void convertPCMFormat(String inputFile, String outputFile, 
                                      int inputSampleRate, int outputSampleRate, 
                                      int inputChannels, int outputChannels) {
        try {
            String command = String.format(
                "ffmpeg -f s16le -ar %d -ac %d -i %s -ar %d -ac %d -f s16le %s",
                inputSampleRate, inputChannels, inputFile, outputSampleRate, outputChannels, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("PCM格式转换完成: " + outputFile);
                System.out.println("输入: " + inputSampleRate + "Hz " + inputChannels + "声道");
                System.out.println("输出: " + outputSampleRate + "Hz " + outputChannels + "声道");
            } else {
                System.err.println("PCM格式转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("PCM格式转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取采样格式
     */
    private static int getSampleFormat(String format) {
        switch (format.toLowerCase()) {
            case "s16le":
                return avutil.AV_SAMPLE_FMT_S16;
            case "f32le":
                return avutil.AV_SAMPLE_FMT_FLT;
            case "u8":
                return avutil.AV_SAMPLE_FMT_U8;
            default:
                return avutil.AV_SAMPLE_FMT_S16;
        }
    }
    
    /**
     * 添加PCM效果
     */
    public static void applyPCMEffect(String inputFile, String outputFile, String effect) {
        try {
            String command;
            
            switch (effect.toLowerCase()) {
                case "amplify":
                    command = String.format(
                        "ffmpeg -f s16le -ar 44100 -ac 2 -i %s -af volume=2.0 -f s16le %s",
                        inputFile, outputFile);
                    break;
                case "fade":
                    command = String.format(
                        "ffmpeg -f s16le -ar 44100 -ac 2 -i %s -af afade=t=out:st=3:d=1 -f s16le %s",
                        inputFile, outputFile);
                    break;
                case "reverse":
                    command = String.format(
                        "ffmpeg -f s16le -ar 44100 -ac 2 -i %s -af areverse -f s16le %s",
                        inputFile, outputFile);
                    break;
                default:
                    System.err.println("不支持的效果: " + effect);
                    return;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("PCM效果应用完成: " + effect);
            } else {
                System.err.println("应用PCM效果失败");
            }
            
        } catch (Exception e) {
            System.err.println("应用PCM效果异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputAudio = "input.mp3";
        String inputVideo = "input.mp4";
        String outputDir = "output/pcm/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 从音频提取PCM
        extractPCMFromAudio(inputAudio, outputDir + "audio.pcm", 44100, 2, "s16le");
        
        // 从视频提取PCM
        extractPCMFromVideo(inputVideo, outputDir + "video_audio.pcm", 44100, 2);
        
        // 分析PCM音频
        analyzePCMAudio(outputDir + "audio.pcm", 44100, 2);
        
        // 生成测试正弦波
        generateSineWavePCM(outputDir + "sine_440hz.pcm", 44100, 440, 3, 0.8);
        
        // PCM格式转换
        convertPCMFormat(outputDir + "audio.pcm", outputDir + "audio_converted.pcm", 
                        44100, 22050, 2, 1);
        
        // 应用效果
        applyPCMEffect(outputDir + "sine_440hz.pcm", outputDir + "sine_fade.pcm", "fade");
        
        // 批量转换
        batchConvertToPCM("input_audio/", outputDir + "batch/", 44100, 2, "s16le");
    }
}