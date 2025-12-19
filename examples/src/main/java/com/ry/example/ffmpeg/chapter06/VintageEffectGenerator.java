package com.ry.example.ffmpeg.chapter06;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 怀旧效果生成器
 * 实战项目：老电影怀旧风
 */
public class VintageEffectGenerator {
    
    /**
     * 基础怀旧效果
     */
    public static void applyBasicVintage(String inputFile, String outputFile) {
        try {
            String filter = "hue=s=0,eq=contrast=1.1:brightness=-0.05,noise=alls=20:allf=t+u";
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("基础怀旧效果应用完成: " + outputFile);
            } else {
                System.err.println("基础怀旧效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("基础怀旧效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 完整怀旧效果
     */
    public static void applyFullVintage(String inputFile, String outputFile) {
        try {
            String filter = "[0:v]hue=s=0,eq=contrast=1.2:brightness=-0.05:saturation=0," +
                           "curves=all='0/0 0.5/0.58 1/1'," +
                           "noise=alls=20:allf=t+u," +
                           "boxblur=1:1:cr=0:ar=0," +
                           "gblur=sigma=1+0.5*sin(t*2)[v]";
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"%s\" -map \"[v]\" -map 0:a? -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("完整怀旧效果应用完成: " + outputFile);
            } else {
                System.err.println("完整怀旧效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("完整怀旧效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 褐色怀旧效果
     */
    public static void applySepiaVintage(String inputFile, String outputFile) {
        try {
            String filter = "colorchannelmixer=.393:.769:.189:.349:.686:.168:.272:.534:.131," +
                           "eq=contrast=1.1:brightness=0.05," +
                           "noise=alls=15:allf=t+u";
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("褐色怀旧效果应用完成: " + outputFile);
            } else {
                System.err.println("褐色怀旧效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("褐色怀旧效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 80年代录像带效果
     */
    public static void apply80sVHSEffect(String inputFile, String outputFile) {
        try {
            String filter = "[0:v]hue=s=0.5:h=1.5," +
                           "eq=contrast=1.2:brightness=0.1:saturation=1.5," +
                           "noise=alls=30:allf=t+u," +
                           "boxblur=2:1:cr=0:ar=0," +
                           "gblur=sigma=2+sin(t*3)," +
                           "drawgrid=width=2:height=2:color=black@0.1:thickness=1," +
                           "unsharp=5:5:-0.5:5:5:-0.5[v]";
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"%s\" -map \"[v]\" -map 0:a? -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("80年代录像带效果应用完成: " + outputFile);
            } else {
                System.err.println("80年代录像带效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("80年代录像带效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 老电影胶片效果
     */
    public static void applyFilmEffect(String inputFile, String outputFile) {
        try {
            String filter = "[0:v]hue=s=0," +
                           "eq=contrast=1.15:brightness=-0.02," +
                           "curves=all='0/0 0.5/0.58 1/1'," +
                           "noise=alls=25:allf=t+u," +
                           "boxblur=0.5:0.5:cr=0:ar=0," +
                           "gblur=sigma=0.8+0.3*sin(t*2)," +
                           "drawbox=x=0:y=0:w=iw:h=2:color=black," +
                           "drawbox=x=0:y=ih-2:w=iw:h=2:color=black[v]";
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"%s\" -map \"[v]\" -map 0:a? -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("老电影胶片效果应用完成: " + outputFile);
            } else {
                System.err.println("老电影胶片效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("老电影胶片效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 黑白默片效果
     */
    public static void applySilentFilmEffect(String inputFile, String outputFile) {
        try {
            String filter = "[0:v]hue=s=0," +
                           "eq=contrast=1.3:brightness=-0.1," +
                           "curves=all='0/0 0.2/0.4 0.5/0.7 0.8/0.9 1/1'," +
                           "noise=alls=35:allf=t+u," +
                           "boxblur=1:1:cr=0:ar=0," +
                           "gblur=sigma=1.5[v];" +
                           "[0:a]afade=in:0:30,afade=out:240:30[a]";
            
            String command = String.format(
                "ffmpeg -i %s -filter_complex \"%s\" -map \"[v]\" -map \"[a]\" -c:v libx264 -crf 23 -preset veryfast -c:a aac -b:a 128k %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("黑白默片效果应用完成: " + outputFile);
            } else {
                System.err.println("黑白默片效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("黑白默片效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 复古棕褐色调
     */
    public static void applyRetroSepia(String inputFile, String outputFile, double intensity) {
        try {
            String filter = String.format(
                "colorchannelmixer=%.3f:%.3f:%.3f:%.3f:%.3f:%.3f:%.3f:%.3f:%.3f," +
                "eq=contrast=%.1f:brightness=%.2f," +
                "noise=alls=%.0f:allf=t+u",
                0.393 * intensity, 0.769 * intensity, 0.189 * intensity,
                0.349 * intensity, 0.686 * intensity, 0.168 * intensity,
                0.272 * intensity, 0.534 * intensity, 0.131 * intensity,
                1.0 + intensity * 0.1, intensity * 0.05,
                intensity * 15);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("复古棕褐色调应用完成: " + outputFile);
                System.out.println("强度: " + intensity);
            } else {
                System.err.println("复古棕褐色调应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("复古棕褐色调应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 胶片颗粒效果
     */
    public static void addFilmGrain(String inputFile, String outputFile, int grainStrength) {
        try {
            String filter = String.format("noise=alls=%d:allf=t+u", grainStrength);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("胶片颗粒效果添加完成: " + outputFile);
                System.out.println("颗粒强度: " + grainStrength);
            } else {
                System.err.println("胶片颗粒效果添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("胶片颗粒效果添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 添加边框效果
     */
    public static void addVintageBorder(String inputFile, String outputFile, int borderWidth, String color) {
        try {
            String filter = String.format(
                "drawbox=x=0:y=0:w=iw:h=%d:color=%s," +
                "drawbox=x=0:y=0:w=%d:h=ih:color=%s," +
                "drawbox=x=iw-%d:y=0:w=%d:h=ih:color=%s," +
                "drawbox=x=0:y=ih-%d:w=iw:h=%d:color=%s",
                borderWidth, color, borderWidth, color, borderWidth, borderWidth, color, 
                borderWidth, borderWidth, color);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("怀旧边框添加完成: " + outputFile);
            } else {
                System.err.println("怀旧边框添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("怀旧边框添加异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量应用怀旧效果
     */
    public static void batchApplyVintage(List<String> inputFiles, String outputDir, 
                                       VintageEffectType effectType) {
        File outputDirectory = new File(outputDir);
        outputDirectory.mkdirs();
        
        for (int i = 0; i < inputFiles.size(); i++) {
            String inputFile = inputFiles.get(i);
            String outputFile = outputDir + "/vintage_" + (i + 1) + ".mp4";
            
            switch (effectType) {
                case BASIC:
                    applyBasicVintage(inputFile, outputFile);
                    break;
                case FULL:
                    applyFullVintage(inputFile, outputFile);
                    break;
                case SEPIA:
                    applySepiaVintage(inputFile, outputFile);
                    break;
                case VHS_80S:
                    apply80sVHSEffect(inputFile, outputFile);
                    break;
                case FILM:
                    applyFilmEffect(inputFile, outputFile);
                    break;
                case SILENT_FILM:
                    applySilentFilmEffect(inputFile, outputFile);
                    break;
                default:
                    applyBasicVintage(inputFile, outputFile);
                    break;
            }
        }
        
        System.out.println("批量怀旧效果处理完成，输出目录: " + outputDir);
    }
    
    /**
     * 自定义怀旧效果
     */
    public static void applyCustomVintage(String inputFile, String outputFile, VintageConfig config) {
        try {
            StringBuilder filter = new StringBuilder();
            
            // 黑白或彩色
            if (config.grayscale) {
                filter.append("hue=s=0,");
            }
            
            // 色彩调整
            if (config.contrast != 1.0 || config.brightness != 0.0 || config.saturation != 1.0) {
                filter.append(String.format("eq=contrast=%.2f:brightness=%.2f:saturation=%.2f,", 
                                           config.contrast, config.brightness, config.saturation));
            }
            
            // 曲线调整
            if (config.useCurves) {
                filter.append("curves=all='0/0 0.5/0.58 1/1',");
            }
            
            // 噪点
            if (config.noiseStrength > 0) {
                filter.append(String.format("noise=alls=%d:allf=t+u,", config.noiseStrength));
            }
            
            // 模糊
            if (config.blurStrength > 0) {
                filter.append(String.format("gblur=sigma=%.2f,", config.blurStrength));
            }
            
            // 边框
            if (config.borderWidth > 0) {
                filter.append(String.format(
                    "drawbox=x=0:y=0:w=iw:h=%d:color=%s,drawbox=x=0:y=0:w=%d:h=ih:color=%s,drawbox=x=iw-%d:y=0:w=%d:h=ih:color=%s,drawbox=x=0:y=ih-%d:w=iw:h=%d:color=%s,",
                    config.borderWidth, config.borderColor, config.borderWidth, config.borderColor, 
                    config.borderWidth, config.borderWidth, config.borderColor, 
                    config.borderWidth, config.borderWidth, config.borderColor));
            }
            
            // 移除最后的逗号
            if (filter.length() > 0 && filter.charAt(filter.length() - 1) == ',') {
                filter.deleteCharAt(filter.length() - 1);
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:v libx264 -crf 23 -preset veryfast %s",
                inputFile, filter.toString(), outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("自定义怀旧效果应用完成: " + outputFile);
            } else {
                System.err.println("自定义怀旧效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("自定义怀旧效果应用异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 怀旧效果类型枚举
     */
    public enum VintageEffectType {
        BASIC, FULL, SEPIA, VHS_80S, FILM, SILENT_FILM
    }
    
    /**
     * 怀旧效果配置类
     */
    public static class VintageConfig {
        public boolean grayscale = true;
        public double contrast = 1.1;
        public double brightness = -0.05;
        public double saturation = 0.0;
        public boolean useCurves = true;
        public int noiseStrength = 20;
        public double blurStrength = 0.5;
        public int borderWidth = 0;
        public String borderColor = "black";
        
        public VintageConfig() {}
        
        public VintageConfig(boolean grayscale, double contrast, double brightness, 
                          int noiseStrength, double blurStrength) {
            this.grayscale = grayscale;
            this.contrast = contrast;
            this.brightness = brightness;
            this.noiseStrength = noiseStrength;
            this.blurStrength = blurStrength;
        }
    }
    
    public static void main(String[] args) {
        // 示例用法
        String inputVideo = "input.mp4";
        String outputDir = "output/vintage/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        // 基础怀旧效果
        applyBasicVintage(inputVideo, outputDir + "basic_vintage.mp4");
        
        // 完整怀旧效果
        applyFullVintage(inputVideo, outputDir + "full_vintage.mp4");
        
        // 褐色怀旧效果
        applySepiaVintage(inputVideo, outputDir + "sepia_vintage.mp4");
        
        // 80年代录像带效果
        apply80sVHSEffect(inputVideo, outputDir + "80s_vhs.mp4");
        
        // 老电影胶片效果
        applyFilmEffect(inputVideo, outputDir + "film_effect.mp4");
        
        // 黑白默片效果
        applySilentFilmEffect(inputVideo, outputDir + "silent_film.mp4");
        
        // 复古棕褐色调
        applyRetroSepia(inputVideo, outputDir + "retro_sepia.mp4", 0.8);
        
        // 胶片颗粒效果
        addFilmGrain(inputVideo, outputDir + "film_grain.mp4", 25);
        
        // 边框效果
        addVintageBorder(inputVideo, outputDir + "vintage_border.mp4", 20, "black");
        
        // 自定义怀旧效果
        VintageConfig customConfig = new VintageConfig(true, 1.2, -0.1, 25, 0.8);
        customConfig.borderWidth = 15;
        customConfig.borderColor = "brown";
        applyCustomVintage(inputVideo, outputDir + "custom_vintage.mp4", customConfig);
        
        // 批量处理
        List<String> inputFiles = new ArrayList<>();
        inputFiles.add(inputVideo);
        inputFiles.add(inputVideo);
        
        batchApplyVintage(inputFiles, outputDir + "batch/", VintageEffectType.FULL);
        
        System.out.println("怀旧效果演示完成！");
    }
}