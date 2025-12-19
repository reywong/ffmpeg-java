package com.ry.example.ffmpeg.chapter05;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 音频拼接器
 * 实战项目：拼接两段音频
 */
public class AudioConcatenator {
    
    /**
     * 简单音频拼接
     */
    public static void concatenateAudioSimple(String audio1, String audio2, String outputFile) {
        try {
            // 创建临时列表文件
            String listFile = "temp_audio_list.txt";
            try (PrintWriter writer = new PrintWriter(listFile)) {
                writer.println("file '" + audio1 + "'");
                writer.println("file '" + audio2 + "'");
            }
            
            String command = String.format(
                "ffmpeg -f concat -safe 0 -i %s -c copy %s",
                listFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("简单音频拼接完成: " + outputFile);
            } else {
                System.err.println("简单音频拼接失败");
            }
            
            // 删除临时文件
            new File(listFile).delete();
            
        } catch (Exception e) {
            System.err.println("简单音频拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 带淡入淡出效果的音频拼接
     */
    public static void concatenateAudioWithFade(String audio1, String audio2, String outputFile, 
                                             double fadeOutDuration, double fadeInDuration) {
        try {
            // 创建临时文件
            String temp1 = "temp_audio1.wav";
            String temp2 = "temp_audio2.wav";
            
            // 获取第一段音频的时长
            double duration1 = getAudioDuration(audio1);
            
            // 为第一段音频添加淡出效果
            String fadeOutCommand = String.format(
                "ffmpeg -i %s -af afade=t=out:st=%.2f:d=%.2f %s",
                audio1, duration1 - fadeOutDuration, fadeOutDuration, temp1);
            
            Process fadeOutProcess = Runtime.getRuntime().exec(fadeOutCommand);
            fadeOutProcess.waitFor();
            
            // 为第二段音频添加淡入效果
            String fadeInCommand = String.format(
                "ffmpeg -i %s -af afade=t=in:st=0:d=%.2f %s",
                audio2, fadeInDuration, temp2);
            
            Process fadeInProcess = Runtime.getRuntime().exec(fadeInCommand);
            fadeInProcess.waitFor();
            
            // 拼接处理后的音频
            concatenateAudioSimple(temp1, temp2, outputFile);
            
            // 清理临时文件
            new File(temp1).delete();
            new File(temp2).delete();
            
            System.out.println("带淡入淡出效果的音频拼接完成: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("带淡入淡出效果的音频拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 交叉淡化音频拼接
     */
    public static void concatenateAudioWithCrossfade(String audio1, String audio2, String outputFile, 
                                                  double crossfadeDuration) {
        try {
            String command = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"[0:a][1:a]acrossfade=d=%.2f:c1=tri:c2=tri[audio]\" -map \"[audio]\" %s",
                audio1, audio2, crossfadeDuration, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("交叉淡化音频拼接完成: " + outputFile);
                System.out.println("交叉淡化时长: " + crossfadeDuration + " 秒");
            } else {
                System.err.println("交叉淡化音频拼接失败");
            }
            
        } catch (Exception e) {
            System.err.println("交叉淡化音频拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 音频无缝拼接
     */
    public static void concatenateAudioSeamless(String audio1, String audio2, String outputFile) {
        try {
            // 首先统一音频参数
            String temp1 = "temp_seamless1.wav";
            String temp2 = "temp_seamless2.wav";
            
            // 统一采样率和声道数
            String unifyCommand1 = String.format(
                "ffmpeg -i %s -ar 44100 -ac 2 %s", audio1, temp1);
            String unifyCommand2 = String.format(
                "ffmpeg -i %s -ar 44100 -ac 2 %s", audio2, temp2);
            
            Process process1 = Runtime.getRuntime().exec(unifyCommand1);
            Process process2 = Runtime.getRuntime().exec(unifyCommand2);
            
            process1.waitFor();
            process2.waitFor();
            
            // 使用高质量拼接
            String concatenateCommand = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"[0:a][1:a]concat=n=2:v=0:a=1[out]\" -map \"[out]\" %s",
                temp1, temp2, outputFile);
            
            Process concatProcess = Runtime.getRuntime().exec(concatenateCommand);
            concatProcess.waitFor();
            
            if (concatProcess.exitValue() == 0) {
                System.out.println("音频无缝拼接完成: " + outputFile);
            } else {
                System.err.println("音频无缝拼接失败");
            }
            
            // 清理临时文件
            new File(temp1).delete();
            new File(temp2).delete();
            
        } catch (Exception e) {
            System.err.println("音频无缝拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 多音频文件拼接
     */
    public static void concatenateMultipleAudio(List<String> audioFiles, String outputFile, 
                                             ConcatMethod method, double transitionDuration) {
        try {
            switch (method) {
                case SIMPLE:
                    concatenateMultipleSimple(audioFiles, outputFile);
                    break;
                case CROSSFADE:
                    concatenateMultipleCrossfade(audioFiles, outputFile, transitionDuration);
                    break;
                case SEAMLESS:
                    concatenateMultipleSeamless(audioFiles, outputFile);
                    break;
                default:
                    concatenateMultipleSimple(audioFiles, outputFile);
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("多音频文件拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 多文件简单拼接
     */
    private static void concatenateMultipleSimple(List<String> audioFiles, String outputFile) {
        try {
            // 创建临时列表文件
            String listFile = "temp_multi_audio_list.txt";
            try (PrintWriter writer = new PrintWriter(listFile)) {
                for (String audioFile : audioFiles) {
                    writer.println("file '" + audioFile + "'");
                }
            }
            
            String command = String.format(
                "ffmpeg -f concat -safe 0 -i %s -c copy %s",
                listFile, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("多文件简单拼接完成: " + outputFile);
            } else {
                System.err.println("多文件简单拼接失败");
            }
            
            // 删除临时文件
            new File(listFile).delete();
            
        } catch (Exception e) {
            System.err.println("多文件简单拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 多文件交叉淡化拼接
     */
    private static void concatenateMultipleCrossfade(List<String> audioFiles, String outputFile, 
                                                   double crossfadeDuration) {
        try {
            if (audioFiles.size() < 2) {
                System.err.println("至少需要两个音频文件");
                return;
            }
            
            // 递归处理多个文件
            List<String> tempFiles = new ArrayList<>();
            
            for (int i = 0; i < audioFiles.size() - 1; i++) {
                String tempFile = "temp_crossfade_" + i + ".wav";
                concatenateAudioWithCrossfade(audioFiles.get(i), audioFiles.get(i + 1), 
                                             tempFile, crossfadeDuration);
                tempFiles.add(tempFile);
            }
            
            // 如果有两个以上的文件，需要进一步处理
            if (tempFiles.size() > 1) {
                concatenateMultipleSimple(tempFiles, outputFile);
                // 清理临时文件
                for (String tempFile : tempFiles) {
                    new File(tempFile).delete();
                }
            } else if (tempFiles.size() == 1) {
                // 只有一个临时文件，重命名为输出文件
                new File(tempFiles.get(0)).renameTo(new File(outputFile));
            }
            
            System.out.println("多文件交叉淡化拼接完成: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("多文件交叉淡化拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 多文件无缝拼接
     */
    private static void concatenateMultipleSeamless(List<String> audioFiles, String outputFile) {
        try {
            // 统一所有文件的参数
            List<String> unifiedFiles = new ArrayList<>();
            
            for (int i = 0; i < audioFiles.size(); i++) {
                String tempFile = "temp_unified_" + i + ".wav";
                String command = String.format(
                    "ffmpeg -i %s -ar 44100 -ac 2 %s", 
                    audioFiles.get(i), tempFile);
                
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                
                unifiedFiles.add(tempFile);
            }
            
            // 构建concat过滤器
            StringBuilder filter = new StringBuilder();
            for (int i = 0; i < unifiedFiles.size(); i++) {
                filter.append("[").append(i).append(":a]");
            }
            filter.append("concat=n=").append(unifiedFiles.size()).append(":v=0:a=1[out]");
            
            StringBuilder inputParams = new StringBuilder();
            for (String unifiedFile : unifiedFiles) {
                inputParams.append("-i ").append(unifiedFile).append(" ");
            }
            
            String command = String.format(
                "ffmpeg %s -filter_complex \"%s\" -map \"[out]\" %s",
                inputParams.toString(), filter.toString(), outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("多文件无缝拼接完成: " + outputFile);
            } else {
                System.err.println("多文件无缝拼接失败");
            }
            
            // 清理临时文件
            for (String tempFile : unifiedFiles) {
                new File(tempFile).delete();
            }
            
        } catch (Exception e) {
            System.err.println("多文件无缝拼接异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取音频时长
     */
    private static double getAudioDuration(String audioFile) {
        try {
            String command = String.format(
                "ffprobe -v quiet -show_entries format=duration -of csv=p=0 %s",
                audioFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    return Double.parseDouble(line.trim());
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取音频时长失败: " + e.getMessage());
        }
        
        return 0.0; // 默认返回0
    }
    
    /**
     * 拼接方法枚举
     */
    public enum ConcatMethod {
        SIMPLE,      // 简单拼接
        CROSSFADE,   // 交叉淡化
        SEAMLESS     // 无缝拼接
    }
    
    /**
     * 音频信息对比
     */
    public static void compareAudioInfo(List<String> audioFiles) {
        System.out.println("=== 音频文件信息对比 ===");
        
        for (int i = 0; i < audioFiles.size(); i++) {
            String audioFile = audioFiles.get(i);
            File file = new File(audioFile);
            
            System.out.println("\n文件 " + (i + 1) + ": " + audioFile);
            System.out.println("文件大小: " + file.length() + " bytes");
            System.out.println("时长: " + String.format("%.2f", getAudioDuration(audioFile)) + " 秒");
            
            // 获取详细信息
            try {
                String command = String.format(
                    "ffprobe -v quiet -show_entries stream=sample_rate,channels,duration -of csv=p=0 %s",
                    audioFile);
                
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            System.out.println("流信息: " + line.trim());
                        }
                    }
                }
                
            } catch (Exception e) {
                System.err.println("获取音频详细信息失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 批量音频处理脚本
     */
    public static void batchConcatenateAudio(String[] audioPairs, String outputDir, ConcatMethod method) {
        for (int i = 0; i < audioPairs.length; i += 2) {
            if (i + 1 < audioPairs.length) {
                String outputFile = outputDir + "/concatenated_" + (i / 2 + 1) + ".wav";
                
                switch (method) {
                    case SIMPLE:
                        concatenateAudioSimple(audioPairs[i], audioPairs[i + 1], outputFile);
                        break;
                    case CROSSFADE:
                        concatenateAudioWithCrossfade(audioPairs[i], audioPairs[i + 1], outputFile, 1.0);
                        break;
                    case SEAMLESS:
                        concatenateAudioSeamless(audioPairs[i], audioPairs[i + 1], outputFile);
                        break;
                }
            }
        }
        
        System.out.println("批量音频拼接完成，输出目录: " + outputDir);
    }
    
    public static void main(String[] args) {
        // 示例用法
        String audio1 = "input/audio1.mp3";
        String audio2 = "input/audio2.mp3";
        String outputDir = "output/concatenated/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入文件是否存在
        if (!new File(audio1).exists() || !new File(audio2).exists()) {
            System.out.println("提示：需要提供输入音频文件");
            System.out.println("请将测试音频文件放在 input/ 目录下");
            return;
        }
        
        // 音频信息对比
        List<String> audioFiles = new ArrayList<>();
        audioFiles.add(audio1);
        audioFiles.add(audio2);
        compareAudioInfo(audioFiles);
        
        // 简单拼接
        concatenateAudioSimple(audio1, audio2, outputDir + "simple_concat.wav");
        
        // 带淡入淡出效果的拼接
        concatenateAudioWithFade(audio1, audio2, outputDir + "fade_concat.wav", 1.0, 1.0);
        
        // 交叉淡化拼接
        concatenateAudioWithCrossfade(audio1, audio2, outputDir + "crossfade_concat.wav", 2.0);
        
        // 无缝拼接
        concatenateAudioSeamless(audio1, audio2, outputDir + "seamless_concat.wav");
        
        // 多文件拼接示例
        List<String> multipleFiles = new ArrayList<>();
        multipleFiles.add(audio1);
        multipleFiles.add(audio2);
        multipleFiles.add(audio1); // 重复添加作为示例
        
        concatenateMultipleAudio(multipleFiles, outputDir + "multi_simple.wav", 
                               ConcatMethod.SIMPLE, 1.0);
        concatenateMultipleAudio(multipleFiles, outputDir + "multi_crossfade.wav", 
                               ConcatMethod.CROSSFADE, 1.5);
        
        System.out.println("音频拼接演示完成！");
    }
}