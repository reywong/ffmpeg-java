package com.ry.example.ffmpeg.chapter05;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 第5章演示类
 * FFmpeg处理音频功能演示
 */
public class Chapter05Demo {
    
    public static void main(String[] args) {
        System.out.println("=== 第5章：FFmpeg处理音频演示 ===\n");
        
        // 创建必要的目录
        createDirectories();
        
        // 演示PCM音频处理
        demonstratePCMProcessing();
        
        // 演示MP3音频处理
        demonstrateMP3Processing();
        
        // 演示音频格式转换
        demonstrateAudioFormatConversion();
        
        // 演示音频拼接
        demonstrateAudioConcatenation();
        
        System.out.println("第5章演示完成！");
    }
    
    /**
     * 创建必要的目录
     */
    private static void createDirectories() {
        String[] directories = {
            "input/audio/",
            "input_audio/",
            "output/pcm/",
            "output/mp3/",
            "output/audio/",
            "output/concatenated/"
        };
        
        for (String dir : directories) {
            new File(dir).mkdirs();
        }
    }
    
    /**
     * 演示PCM音频处理
     */
    private static void demonstratePCMProcessing() {
        System.out.println("=== 5.1 PCM音频处理演示 ===");
        
        String inputAudio = "input.mp3";
        String inputVideo = "input.mp4";
        
        // 检查输入文件是否存在
        if (!new File(inputAudio).exists()) {
            System.out.println("提示：需要提供输入音频文件 " + inputAudio);
            System.out.println("请将测试音频文件放在项目根目录下");
        } else {
            // 从音频提取PCM
            PCMAudioProcessor.extractPCMFromAudio(inputAudio, "output/pcm/audio.pcm", 44100, 2, "s16le");
            
            // 分析PCM音频
            PCMAudioProcessor.analyzePCMAudio("output/pcm/audio.pcm", 44100, 2);
            
            // PCM格式转换
            PCMAudioProcessor.convertPCMFormat("output/pcm/audio.pcm", "output/pcm/audio_converted.pcm", 
                                              44100, 22050, 2, 1);
        }
        
        // 生成测试正弦波
        PCMAudioProcessor.generateSineWavePCM("output/pcm/sine_440hz.pcm", 44100, 440, 3, 0.8);
        
        // 分析生成的正弦波
        PCMAudioProcessor.analyzePCMAudio("output/pcm/sine_440hz.pcm", 44100, 1);
        
        System.out.println("PCM音频处理演示完成\n");
    }
    
    /**
     * 演示MP3音频处理
     */
    private static void demonstrateMP3Processing() {
        System.out.println("=== 5.2 MP3音频处理演示 ===");
        
        String inputAudio = "input.wav";
        
        if (!new File(inputAudio).exists()) {
            System.out.println("提示：需要提供输入音频文件 " + inputAudio);
            System.out.println("请将测试音频文件放在项目根目录下");
        } else {
            // 基础MP3转换
            MP3AudioProcessor.convertToMP3(inputAudio, "output/mp3/audio_high.mp3", 0.8f);
            MP3AudioProcessor.convertToMP3(inputAudio, "output/mp3/audio_medium.mp3", 0.5f);
            MP3AudioProcessor.convertToMP3(inputAudio, "output/mp3/audio_low.mp3", 0.3f);
            
            // 设置比特率转换
            MP3AudioProcessor.convertToMP3WithBitrate(inputAudio, "output/mp3/audio_128kbps.mp3", 128);
            MP3AudioProcessor.convertToMP3WithBitrate(inputAudio, "output/mp3/audio_320kbps.mp3", 320);
            
            // VBR转换
            MP3AudioProcessor.convertToVBRMP3(inputAudio, "output/mp3/audio_vbr.mp3", 2);
            
            // ABR转换
            MP3AudioProcessor.convertToABRMP3(inputAudio, "output/mp3/audio_abr.mp3", 192);
            
            // 音量标准化
            MP3AudioProcessor.normalizeMP3Volume("output/mp3/audio_high.mp3", "output/mp3/audio_normalized.mp3");
            
            // 截取片段
            MP3AudioProcessor.cutMP3Segment("output/mp3/audio_high.mp3", "output/mp3/audio_segment.mp3", 10, 30);
            
            // 添加标签
            MP3AudioProcessor.addMP3Tags("output/mp3/audio_high.mp3", "output/mp3/audio_tagged.mp3", 
                                        "测试音频", "测试艺术家", "测试专辑");
            
            // 获取MP3信息
            MP3AudioProcessor.getMP3Info("output/mp3/audio_high.mp3");
            
            // 质量对比
            MP3AudioProcessor.compareMP3Quality(inputAudio, "output/mp3/audio_high.mp3");
        }
        
        System.out.println("MP3音频处理演示完成\n");
    }
    
    /**
     * 演示音频格式转换
     */
    private static void demonstrateAudioFormatConversion() {
        System.out.println("=== 5.3 音频格式转换演示 ===");
        
        String inputAudio = "input.mp3";
        
        if (!new File(inputAudio).exists()) {
            System.out.println("提示：需要提供输入音频文件 " + inputAudio);
            System.out.println("请将测试音频文件放在项目根目录下");
        } else {
            // WAV转换
            AudioFormatConverter.convertToWAV(inputAudio, "output/audio/audio_16bit.wav", 44100, 2, "s16le");
            AudioFormatConverter.convertToHighQualityWAV(inputAudio, "output/audio/audio_24bit.wav", 48000, 2, 24);
            AudioFormatConverter.convertToHighQualityWAV(inputAudio, "output/audio/audio_32bit.wav", 96000, 2, 32);
            
            // AAC转换
            AudioFormatConverter.convertToAAC(inputAudio, "output/audio/audio_aac.aac", 128000, "lc");
            AudioFormatConverter.convertToAAC(inputAudio, "output/audio/audio_heaac.aac", 64000, "he");
            
            // 重采样
            AudioFormatConverter.resampleAudio(inputAudio, "output/audio/audio_22khz.wav", 44100, 22050, 2, 2);
            AudioFormatConverter.resampleAudio(inputAudio, "output/audio/audio_8khz_mono.wav", 44100, 8000, 2, 1);
            
            // 高质量重采样
            AudioFormatConverter.highQualityResample(inputAudio, "output/audio/audio_hq.wav", 48000, 2);
            
            // 声道转换
            AudioFormatConverter.convertChannels(inputAudio, "output/audio/audio_mono.wav", 1);
            
            // 音频标准化
            AudioFormatConverter.normalizeAudio(inputAudio, "output/audio/audio_normalized.wav", -16.0, -1.5);
            
            // 音频降噪
            AudioFormatConverter.denoiseAudio(inputAudio, "output/audio/audio_denoised.wav", -25.0);
            
            // 获取音频信息
            AudioFormatConverter.getAudioInfo(inputAudio);
        }
        
        // 批量转换（如果有输入目录）
        AudioFormatConverter.AudioConversionParams params = 
            new AudioFormatConverter.AudioConversionParams(48000, 2, "s16le", 192000, "lc");
        AudioFormatConverter.batchConvertFormat("input_audio/", "output/audio/batch/", "wav", params);
        
        System.out.println("音频格式转换演示完成\n");
    }
    
    /**
     * 演示音频拼接
     */
    private static void demonstrateAudioConcatenation() {
        System.out.println("=== 5.4 音频拼接演示 ===");
        
        String audio1 = "input/audio1.mp3";
        String audio2 = "input/audio2.mp3";
        
        // 检查输入文件是否存在
        if (!new File(audio1).exists() || !new File(audio2).exists()) {
            System.out.println("提示：需要提供两个输入音频文件");
            System.out.println("请将测试音频文件放在 input/ 目录下");
            System.out.println("文件名应为 audio1.mp3 和 audio2.mp3");
            
            // 创建测试音频（使用之前生成的正弦波）
            System.out.println("使用测试正弦波进行演示...");
            audio1 = "output/pcm/sine_440hz.pcm";
            audio2 = "output/pcm/sine_440hz.pcm";
            
            if (!new File(audio1).exists()) {
                PCMAudioProcessor.generateSineWavePCM(audio1, 44100, 440, 3, 0.8);
                PCMAudioProcessor.generateSineWavePCM(audio2, 44100, 880, 3, 0.8); // 不同频率
            }
        } else {
            // 音频信息对比
            List<String> audioFiles = new ArrayList<>();
            audioFiles.add(audio1);
            audioFiles.add(audio2);
            AudioConcatenator.compareAudioInfo(audioFiles);
        }
        
        // 简单拼接
        AudioConcatenator.concatenateAudioSimple(audio1, audio2, "output/concatenated/simple_concat.wav");
        
        // 带淡入淡出效果的拼接
        AudioConcatenator.concatenateAudioWithFade(audio1, audio2, "output/concatenated/fade_concat.wav", 1.0, 1.0);
        
        // 交叉淡化拼接
        AudioConcatenator.concatenateAudioWithCrossfade(audio1, audio2, "output/concatenated/crossfade_concat.wav", 2.0);
        
        // 无缝拼接
        AudioConcatenator.concatenateAudioSeamless(audio1, audio2, "output/concatenated/seamless_concat.wav");
        
        // 多文件拼接示例
        List<String> multipleFiles = new ArrayList<>();
        multipleFiles.add(audio1);
        multipleFiles.add(audio2);
        multipleFiles.add(audio1); // 重复添加作为示例
        
        AudioConcatenator.concatenateMultipleAudio(multipleFiles, "output/concatenated/multi_simple.wav", 
                                                   AudioConcatenator.ConcatMethod.SIMPLE, 1.0);
        AudioConcatenator.concatenateMultipleAudio(multipleFiles, "output/concatenated/multi_crossfade.wav", 
                                                   AudioConcatenator.ConcatMethod.CROSSFADE, 1.5);
        
        System.out.println("音频拼接演示完成\n");
    }
    
    /**
     * 显示功能菜单
     */
    public static void showMenu() {
        System.out.println("请选择要演示的功能：");
        System.out.println("1. PCM音频处理");
        System.out.println("2. MP3音频处理");
        System.out.println("3. 音频格式转换");
        System.out.println("4. 音频拼接");
        System.out.println("5. 运行所有演示");
        System.out.println("0. 退出");
    }
    
    /**
     * 交互式演示
     */
    public static void interactiveDemo() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        while (true) {
            showMenu();
            System.out.print("请输入选择：");
            
            try {
                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1:
                        demonstratePCMProcessing();
                        break;
                    case 2:
                        demonstrateMP3Processing();
                        break;
                    case 3:
                        demonstrateAudioFormatConversion();
                        break;
                    case 4:
                        demonstrateAudioConcatenation();
                        break;
                    case 5:
                        createDirectories();
                        demonstratePCMProcessing();
                        demonstrateMP3Processing();
                        demonstrateAudioFormatConversion();
                        demonstrateAudioConcatenation();
                        break;
                    case 0:
                        System.out.println("退出演示");
                        return;
                    default:
                        System.out.println("无效选择，请重新输入");
                        break;
                }
            } catch (Exception e) {
                System.out.println("输入错误，请重新输入");
                scanner.nextLine(); // 清除错误的输入
            }
        }
    }
    
    /**
     * 性能测试
     */
    public static void performanceTest() {
        System.out.println("=== 性能测试 ===");
        
        String inputAudio = "input.mp3";
        if (!new File(inputAudio).exists()) {
            System.out.println("需要输入音频文件进行性能测试");
            return;
        }
        
        // 测试不同质量设置的转换时间
        System.out.println("测试不同MP3质量设置的转换时间...");
        
        long startTime, endTime;
        
        // 高质量转换
        startTime = System.currentTimeMillis();
        MP3AudioProcessor.convertToMP3(inputAudio, "output/perf/high_quality.mp3", 0.9f);
        endTime = System.currentTimeMillis();
        System.out.println("高质量转换耗时: " + (endTime - startTime) + " ms");
        
        // 中等质量转换
        startTime = System.currentTimeMillis();
        MP3AudioProcessor.convertToMP3(inputAudio, "output/perf/medium_quality.mp3", 0.5f);
        endTime = System.currentTimeMillis();
        System.out.println("中等质量转换耗时: " + (endTime - startTime) + " ms");
        
        // 低质量转换
        startTime = System.currentTimeMillis();
        MP3AudioProcessor.convertToMP3(inputAudio, "output/perf/low_quality.mp3", 0.2f);
        endTime = System.currentTimeMillis();
        System.out.println("低质量转换耗时: " + (endTime - startTime) + " ms");
        
        System.out.println("性能测试完成");
    }
}