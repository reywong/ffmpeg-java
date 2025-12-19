package com.ry.example.ffmpeg.chapter05;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 音频格式转换器
 * 支持WAV、AAC、音频重采样等格式转换
 */
public class AudioFormatConverter {
    
    /**
     * 转换为WAV格式
     */
    public static void convertToWAV(String inputFile, String outputFile, 
                                  int sampleRate, int channels, String sampleFormat) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
            grabber.start();
            
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 0, channels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setSampleRate(sampleRate);
            recorder.setFormat("wav");
            recorder.start();
            
            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
            
            recorder.stop();
            grabber.stop();
            
            System.out.println("WAV转换完成: " + outputFile);
            System.out.println("采样率: " + sampleRate + "Hz, 声道数: " + channels);
            
        } catch (Exception e) {
            System.err.println("WAV转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 转换为高质量WAV
     */
    public static void convertToHighQualityWAV(String inputFile, String outputFile, 
                                            int sampleRate, int channels, int bitDepth) {
        try {
            String command;
            switch (bitDepth) {
                case 24:
                    command = String.format(
                        "ffmpeg -i %s -ar %d -ac %d -c:a pcm_s24le %s",
                        inputFile, sampleRate, channels, outputFile);
                    break;
                case 32:
                    command = String.format(
                        "ffmpeg -i %s -ar %d -ac %d -c:a pcm_f32le %s",
                        inputFile, sampleRate, channels, outputFile);
                    break;
                default:
                    command = String.format(
                        "ffmpeg -i %s -ar %d -ac %d -c:a pcm_s16le %s",
                        inputFile, sampleRate, channels, outputFile);
                    break;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("高质量WAV转换完成: " + outputFile);
                System.out.println("位深度: " + bitDepth + "位");
            } else {
                System.err.println("高质量WAV转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("高质量WAV转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 转换为AAC格式
     */
    public static void convertToAAC(String inputFile, String outputFile, 
                                   int bitrate, String profile) {
        try {
            String command;
            switch (profile.toLowerCase()) {
                case "he":
                    command = String.format(
                        "ffmpeg -i %s -c:a libfdk_aac -profile:a aac_he -b:a %d %s",
                        inputFile, bitrate, outputFile);
                    break;
                case "he_v2":
                    command = String.format(
                        "ffmpeg -i %s -c:a libfdk_aac -profile:a aac_he_v2 -b:a %d %s",
                        inputFile, bitrate, outputFile);
                    break;
                default:
                    command = String.format(
                        "ffmpeg -i %s -c:a aac -profile:a aac_low -b:a %d %s",
                        inputFile, bitrate, outputFile);
                    break;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("AAC转换完成: " + outputFile);
                System.out.println("比特率: " + bitrate + " kbps, 配置: " + profile);
            } else {
                System.err.println("AAC转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("AAC转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 音频重采样
     */
    public static void resampleAudio(String inputFile, String outputFile, 
                                   int inputSampleRate, int outputSampleRate, 
                                   int inputChannels, int outputChannels) {
        try {
            String command = String.format(
                "ffmpeg -i %s -ar %d -ac %d %s",
                inputFile, outputSampleRate, outputChannels, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("音频重采样完成: " + outputFile);
                System.out.println("输入: " + inputSampleRate + "Hz " + inputChannels + "声道");
                System.out.println("输出: " + outputSampleRate + "Hz " + outputChannels + "声道");
            } else {
                System.err.println("音频重采样失败");
            }
            
        } catch (Exception e) {
            System.err.println("音频重采样异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 高质量重采样
     */
    public static void highQualityResample(String inputFile, String outputFile, 
                                         int outputSampleRate, int outputChannels) {
        try {
            String command = String.format(
                "ffmpeg -i %s -af aresample=out_sample_rate=%d:out_channel_layout=%s:resampler=soxr %s",
                inputFile, outputSampleRate, getChannelLayout(outputChannels), outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("高质量重采样完成: " + outputFile);
            } else {
                System.err.println("高质量重采样失败");
            }
            
        } catch (Exception e) {
            System.err.println("高质量重采样异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 声道转换
     */
    public static void convertChannels(String inputFile, String outputFile, int channels) {
        try {
            String command = String.format(
                "ffmpeg -i %s -ac %d %s",
                inputFile, channels, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("声道转换完成: " + outputFile);
                System.out.println("声道数: " + channels);
            } else {
                System.err.println("声道转换失败");
            }
            
        } catch (Exception e) {
            System.err.println("声道转换异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 5.1声道转立体声
     */
    public static void convert5dot1ToStereo(String inputFile, String outputFile) {
        try {
            String command = String.format(
                "ffmpeg -i %s -af \"pan=stereo|FL<FL+0.707*FC+0.707*LFE|FR<FR+0.707*FC+0.707*LFE\" %s",
                inputFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("5.1声道转立体声完成: " + outputFile);
            } else {
                System.err.println("5.1声道转立体声失败");
            }
            
        } catch (Exception e) {
            System.err.println("5.1声道转立体声异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量格式转换
     */
    public static void batchConvertFormat(String inputDir, String outputDir, 
                                        String targetFormat, AudioConversionParams params) {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("输入目录不存在: " + inputDir);
            return;
        }
        
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        File[] files = dir.listFiles((file, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".mp3") || lowerName.endsWith(".wav") || 
                   lowerName.endsWith(".flac") || lowerName.endsWith(".aac");
        });
        
        if (files != null) {
            for (File file : files) {
                String outputFileName = file.getName().replaceAll("\\.[^.]+$", "." + targetFormat);
                String outputPath = outputDir + "/" + outputFileName;
                
                switch (targetFormat.toLowerCase()) {
                    case "wav":
                        convertToWAV(file.getAbsolutePath(), outputPath, 
                                   params.sampleRate, params.channels, params.sampleFormat);
                        break;
                    case "aac":
                        convertToAAC(file.getAbsolutePath(), outputPath, 
                                    params.bitrate, params.profile);
                        break;
                    default:
                        System.err.println("不支持的目标格式: " + targetFormat);
                        break;
                }
            }
        }
    }
    
    /**
     * 音频质量标准化
     */
    public static void normalizeAudio(String inputFile, String outputFile, 
                                    double targetLUFS, double truePeak) {
        try {
            String command = String.format(
                "ffmpeg -i %s -af loudnorm=I=%.1f:TP=%.1f:LRA=11 %s",
                inputFile, targetLUFS, truePeak, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("音频标准化完成: " + outputFile);
                System.out.println("目标LUFS: " + targetLUFS);
            } else {
                System.err.println("音频标准化失败");
            }
            
        } catch (Exception e) {
            System.err.println("音频标准化异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 音频降噪
     */
    public static void denoiseAudio(String inputFile, String outputFile, double noiseLevel) {
        try {
            String command = String.format(
                "ffmpeg -i %s -af afftdn=nf=%.1f %s",
                inputFile, noiseLevel, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("音频降噪完成: " + outputFile);
                System.out.println("降噪级别: " + noiseLevel + " dB");
            } else {
                System.err.println("音频降噪失败");
            }
            
        } catch (Exception e) {
            System.err.println("音频降噪异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取声道布局
     */
    private static String getChannelLayout(int channels) {
        switch (channels) {
            case 1: return "mono";
            case 2: return "stereo";
            case 6: return "5.1";
            default: return "stereo";
        }
    }
    
    /**
     * 音频格式转换参数类
     */
    public static class AudioConversionParams {
        public int sampleRate = 44100;
        public int channels = 2;
        public String sampleFormat = "s16le";
        public int bitrate = 128000;
        public String profile = "lc";
        
        public AudioConversionParams() {}
        
        public AudioConversionParams(int sampleRate, int channels, String sampleFormat, 
                                  int bitrate, String profile) {
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.sampleFormat = sampleFormat;
            this.bitrate = bitrate;
            this.profile = profile;
        }
    }
    
    /**
     * 获取音频文件信息
     */
    public static void getAudioInfo(String audioFile) {
        try {
            File file = new File(audioFile);
            long fileSize = file.length();
            
            System.out.println("=== 音频文件信息 ===");
            System.out.println("文件路径: " + audioFile);
            System.out.println("文件大小: " + fileSize + " bytes (" + String.format("%.2f", fileSize / 1024.0 / 1024.0) + " MB)");
            
            // 使用FFprobe获取详细信息
            String command = String.format("ffprobe -v quiet -print_format json -show_format -show_streams %s", audioFile);
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
                System.out.println("流信息: " + output.toString());
            }
            
        } catch (Exception e) {
            System.err.println("获取音频信息失败: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputAudio = "input.mp3";
        String outputDir = "output/audio/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // WAV转换
        convertToWAV(inputAudio, outputDir + "audio_16bit.wav", 44100, 2, "s16le");
        convertToHighQualityWAV(inputAudio, outputDir + "audio_24bit.wav", 48000, 2, 24);
        convertToHighQualityWAV(inputAudio, outputDir + "audio_32bit.wav", 96000, 2, 32);
        
        // AAC转换
        convertToAAC(inputAudio, outputDir + "audio_aac.aac", 128000, "lc");
        convertToAAC(inputAudio, outputDir + "audio_heaac.aac", 64000, "he");
        
        // 重采样
        resampleAudio(inputAudio, outputDir + "audio_22khz.wav", 44100, 22050, 2, 2);
        resampleAudio(inputAudio, outputDir + "audio_8khz_mono.wav", 44100, 8000, 2, 1);
        
        // 高质量重采样
        highQualityResample(inputAudio, outputDir + "audio_hq.wav", 48000, 2);
        
        // 声道转换
        convertChannels(inputAudio, outputDir + "audio_mono.wav", 1);
        
        // 音频标准化
        normalizeAudio(inputAudio, outputDir + "audio_normalized.wav", -16.0, -1.5);
        
        // 音频降噪
        denoiseAudio(inputAudio, outputDir + "audio_denoised.wav", -25.0);
        
        // 获取音频信息
        getAudioInfo(inputAudio);
        
        // 批量转换
        AudioConversionParams params = new AudioConversionParams(48000, 2, "s16le", 192000, "lc");
        batchConvertFormat("input_audio/", outputDir + "batch/", "wav", params);
    }
}