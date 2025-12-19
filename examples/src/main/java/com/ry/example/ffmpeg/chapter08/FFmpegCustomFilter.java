package com.ry.example.ffmpeg.chapter08;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FFmpeg自定义滤镜处理器
 * 演示各种自定义滤镜的使用和配置
 */
public class FFmpegCustomFilter {
    
    /**
     * 侧边模糊滤镜
     */
    public static void applySideBlur(String inputFile, String outputFile, 
                                    int leftWidth, int rightWidth, int transitionWidth, 
                                    float maxBlur) {
        try {
            String filter = String.format(
                "sideblur=left_width=%d:right_width=%d:transition_width=%d:max_blur=%.2f",
                leftWidth, rightWidth, transitionWidth, maxBlur);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, filter, outputFile);
            
            System.out.println("应用侧边模糊滤镜...");
            System.out.println("左侧模糊宽度: " + leftWidth + "px");
            System.out.println("右侧模糊宽度: " + rightWidth + "px");
            System.out.println("过渡宽度: " + transitionWidth + "px");
            System.out.println("最大模糊强度: " + maxBlur);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("侧边模糊效果应用成功: " + outputFile);
            } else {
                System.err.println("侧边模糊效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("侧边模糊效果应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 使用现有的模糊滤镜模拟侧边模糊效果
     */
    public static void applySideBlurSimulated(String inputFile, String outputFile, 
                                            int leftWidth, int rightWidth, int transitionWidth, 
                                            float blurStrength) {
        try {
            // 使用组合滤镜模拟侧边模糊
            String filter = String.format(
                "split=2[original][blurred];[blurred]boxblur=%d:1[blur];[original][blur]blend=all_expr='if(lt(X,%d),B,if(lt(X,%d),A+(B-A)*((X-%d)/%d),if(gt(X,W-%d),B,if(gt(X,W-%d),A+(B-A)*((X-(W-%d))/%d),A))))'",
                (int)blurStrength, leftWidth, leftWidth + transitionWidth, 
                leftWidth, transitionWidth, rightWidth, rightWidth, 
                transitionWidth, transitionWidth);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("模拟侧边模糊效果应用成功: " + outputFile);
            } else {
                System.err.println("模拟侧边模糊效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("模拟侧边模糊效果应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 自定义模糊和锐化组合滤镜
     */
    public static void applyCustomBlurSharp(String inputFile, String outputFile, 
                                          float blurStrength, float sharpStrength, int radius) {
        try {
            String filter = String.format(
                "customblursharp=blur=%.2f:sharp=%.2f:radius=%d",
                blurStrength, sharpStrength, radius);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, filter, outputFile);
            
            System.out.println("应用自定义模糊锐化滤镜...");
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("自定义模糊锐化效果应用成功: " + outputFile);
            } else {
                System.err.println("自定义模糊锐化效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("自定义模糊锐化效果应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 使用现有滤镜模拟自定义效果
     */
    public static void applySimulatedBlurSharp(String inputFile, String outputFile, 
                                             float blurStrength, float sharpStrength) {
        try {
            String filter = String.format(
                "gblur=sigma=%.2f,unsharp=5:5:%.2f:5:5:0.0",
                blurStrength, sharpStrength);
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, filter, outputFile);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("模拟模糊锐化效果应用成功: " + outputFile);
            } else {
                System.err.println("模拟模糊锐化效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("模拟模糊锐化效果应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 自定义翻转滤镜
     */
    public static void applyCustomFlip(String inputFile, String outputFile, 
                                      boolean horizontal, boolean vertical, boolean diagonal) {
        try {
            String flipFilters = new StringBuilder();
            
            if (horizontal && vertical) {
                flipFilters.append("hflip,vflip");
            } else if (horizontal) {
                flipFilters.append("hflip");
            } else if (vertical) {
                flipFilters.append("vflip");
            }
            
            if (diagonal) {
                if (flipFilters.length() > 0) {
                    flipFilters.append(",");
                }
                flipFilters.append("transpose=2,transpose=2");
            }
            
            if (flipFilters.length() == 0) {
                System.out.println("没有指定翻转操作");
                return;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, flipFilters.toString(), outputFile);
            
            System.out.println("应用自定义翻转滤镜...");
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("自定义翻转效果应用成功: " + outputFile);
            } else {
                System.err.println("自定义翻转效果应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("自定义翻转效果应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建复杂的滤镜链
     */
    public static void applyComplexFilterChain(String inputFile, String outputFile, 
                                             FilterChainConfig config) {
        try {
            StringBuilder filterChain = new StringBuilder();
            
            // 添加模糊效果
            if (config.blurStrength > 0) {
                filterChain.append(String.format("gblur=sigma=%.2f", config.blurStrength));
            }
            
            // 添加锐化效果
            if (config.sharpStrength > 0) {
                if (filterChain.length() > 0) filterChain.append(",");
                filterChain.append(String.format("unsharp=5:5:%.2f", config.sharpStrength));
            }
            
            // 添加翻转效果
            if (config.flipHorizontal) {
                if (filterChain.length() > 0) filterChain.append(",");
                filterChain.append("hflip");
            }
            
            if (config.flipVertical) {
                if (filterChain.length() > 0) filterChain.append(",");
                filterChain.append("vflip");
            }
            
            // 添加色彩调整
            if (config.brightness != 0 || config.contrast != 0 || config.saturation != 0) {
                if (filterChain.length() > 0) filterChain.append(",");
                filterChain.append(String.format("eq=brightness=%.2f:contrast=%.2f:saturation=%.2f", 
                    config.brightness, config.contrast, config.saturation));
            }
            
            // 添加暗角效果
            if (config.vignette) {
                if (filterChain.length() > 0) filterChain.append(",");
                filterChain.append("vignette=angle=PI/6:mode=backward:eval=init");
            }
            
            if (filterChain.length() == 0) {
                System.out.println("没有指定滤镜效果");
                return;
            }
            
            String command = String.format(
                "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
                inputFile, filterChain.toString(), outputFile);
            
            System.out.println("应用复杂滤镜链...");
            System.out.println("滤镜链: " + filterChain.toString());
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            if (process.exitValue() == 0) {
                System.out.println("复杂滤镜链应用成功: " + outputFile);
            } else {
                System.err.println("复杂滤镜链应用失败");
            }
            
        } catch (Exception e) {
            System.err.println("复杂滤镜链应用异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查FFmpeg是否支持特定滤镜
     */
    public static boolean checkFilterSupport(String filterName) {
        try {
            String command = String.format("ffmpeg -filters | grep %s", filterName);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
            return process.exitValue() == 0;
            
        } catch (Exception e) {
            System.err.println("检查滤镜支持失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取所有支持的滤镜列表
     */
    public static Map<String, String> getSupportedFilters() {
        Map<String, String> filters = new HashMap<>();
        
        try {
            Process process = Runtime.getRuntime().exec("ffmpeg -filters");
            process.waitFor();
            
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                
                String line;
                boolean inFilterList = false;
                
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Filters:")) {
                        inFilterList = true;
                        continue;
                    }
                    
                    if (inFilterList && line.trim().isEmpty()) {
                        break;
                    }
                    
                    if (inFilterList && line.trim().length() > 50) {
                        String name = line.substring(1, 20).trim();
                        String description = line.substring(50).trim();
                        filters.put(name, description);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取滤镜列表失败: " + e.getMessage());
        }
        
        return filters;
    }
    
    /**
     * 创建自定义滤镜配置文件
     */
    public static void createCustomFilterConfig(String outputFile, CustomFilterConfig config) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("# 自定义滤镜配置文件\n");
            writer.write("# 生成时间: " + new java.util.Date() + "\n\n");
            
            writer.write("[滤镜定义]\n");
            writer.write("滤镜名称 = " + config.name + "\n");
            writer.write("滤镜类型 = " + config.type + "\n");
            writer.write("描述 = " + config.description + "\n\n");
            
            writer.write("[参数配置]\n");
            for (Map.Entry<String, String> entry : config.parameters.entrySet()) {
                writer.write(entry.getKey() + " = " + entry.getValue() + "\n");
            }
            
            writer.write("\n[滤镜链]\n");
            writer.write("滤镜表达式 = " + config.filterExpression + "\n");
            
            System.out.println("自定义滤镜配置文件创建成功: " + outputFile);
            
        } catch (IOException e) {
            System.err.println("创建配置文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成滤镜性能测试报告
     */
    public static void generateFilterPerformanceReport(String inputFile, String outputDir) {
        try {
            String[] testFilters = {
                "gblur=sigma=2",
                "unsharp=5:5:1.5",
                "hflip",
                "vflip",
                "eq=brightness=0.2:contrast=1.2",
                "vignette=angle=PI/6",
                "boxblur=2:1",
                "colorspace=rgb:hsv"
            };
            
            StringBuilder report = new StringBuilder();
            report.append("# FFmpeg滤镜性能测试报告\n");
            report.append("# 测试时间: " + new java.util.Date() + "\n");
            report.append("# 输入文件: " + inputFile + "\n\n");
            
            for (String filter : testFilters) {
                long startTime = System.currentTimeMillis();
                String outputFile = outputDir + "/perf_test_" + filter.hashCode() + ".mp4";
                
                String command = String.format(
                    "ffmpeg -i %s -vf \"%s\" -t 10 %s -y",
                    inputFile, filter, outputFile);
                
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                File outFile = new File(outputFile);
                long fileSize = outFile.exists() ? outFile.length() : 0;
                
                report.append(String.format("滤镜: %s\n", filter));
                report.append(String.format("处理时间: %d ms\n", duration));
                report.append(String.format("输出文件大小: %,d bytes\n", fileSize));
                report.append(String.format("处理速度: %.2f MB/s\n", fileSize / 1024.0 / 1024.0 / (duration / 1000.0)));
                report.append("---\n");
                
                // 清理临时文件
                if (outFile.exists()) {
                    outFile.delete();
                }
            }
            
            String reportFile = outputDir + "/filter_performance_report.md";
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(report.toString());
            }
            
            System.out.println("滤镜性能测试报告生成成功: " + reportFile);
            
        } catch (Exception e) {
            System.err.println("生成性能测试报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 滤镜链配置类
     */
    public static class FilterChainConfig {
        public float blurStrength = 0;
        public float sharpStrength = 0;
        public boolean flipHorizontal = false;
        public boolean flipVertical = false;
        public float brightness = 0;
        public float contrast = 0;
        public float saturation = 0;
        public boolean vignette = false;
        
        public FilterChainConfig() {}
        
        public FilterChainConfig blur(float strength) {
            this.blurStrength = strength;
            return this;
        }
        
        public FilterChainConfig sharp(float strength) {
            this.sharpStrength = strength;
            return this;
        }
        
        public FilterChainConfig flip(boolean horizontal, boolean vertical) {
            this.flipHorizontal = horizontal;
            this.flipVertical = vertical;
            return this;
        }
        
        public FilterChainConfig color(float brightness, float contrast, float saturation) {
            this.brightness = brightness;
            this.contrast = contrast;
            this.saturation = saturation;
            return this;
        }
        
        public FilterChainConfig vignette(boolean enable) {
            this.vignette = enable;
            return this;
        }
    }
    
    /**
     * 自定义滤镜配置类
     */
    public static class CustomFilterConfig {
        public String name;
        public String type;
        public String description;
        public Map<String, String> parameters = new HashMap<>();
        public String filterExpression;
        
        public CustomFilterConfig(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
        
        public void addParameter(String name, String value) {
            parameters.put(name, value);
        }
        
        public void setFilterExpression(String expression) {
            this.filterExpression = expression;
        }
    }
    
    public static void main(String[] args) {
        String inputVideo = "input.mp4";
        String outputDir = "output/custom_filters/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 检查输入视频是否存在
        if (!new File(inputVideo).exists()) {
            System.out.println("提示：需要提供输入视频文件 " + inputVideo);
            System.out.println("请将测试视频文件放在项目根目录下");
            return;
        }
        
        System.out.println("=== FFmpeg自定义滤镜演示 ===\n");
        
        // 1. 侧边模糊效果（模拟）
        System.out.println("1. 侧边模糊效果演示");
        applySideBlurSimulated(inputVideo, outputDir + "side_blur_simulated.mp4", 
                               100, 100, 50, 3.0f);
        
        // 2. 自定义模糊锐化
        System.out.println("\n2. 自定义模糊锐化演示");
        applySimulatedBlurSharp(inputVideo, outputDir + "blur_sharp_simulated.mp4", 
                               2.0f, 1.5f);
        
        // 3. 自定义翻转
        System.out.println("\n3. 自定义翻转演示");
        applyCustomFlip(inputVideo, outputDir + "custom_flip.mp4", 
                       true, false, false); // 水平翻转
        
        // 4. 复杂滤镜链
        System.out.println("\n4. 复杂滤镜链演示");
        FilterChainConfig complexConfig = new FilterChainConfig()
            .blur(1.5f)
            .sharp(1.0f)
            .flip(true, false)
            .color(0.1f, 1.2f, 1.1f)
            .vignette(true);
        
        applyComplexFilterChain(inputVideo, outputDir + "complex_filter.mp4", complexConfig);
        
        // 5. 获取支持的滤镜列表
        System.out.println("\n5. 获取支持的滤镜列表");
        Map<String, String> filters = getSupportedFilters();
        System.out.println("支持的滤镜数量: " + filters.size());
        
        // 显示前10个滤镜
        int count = 0;
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (count >= 10) break;
            System.out.printf("  %-15s - %s%n", entry.getKey(), entry.getValue());
            count++;
        }
        
        // 6. 创建自定义滤镜配置
        System.out.println("\n6. 创建自定义滤镜配置");
        CustomFilterConfig customConfig = new CustomFilterConfig(
            "sideblur", "video", "侧边模糊滤镜");
        customConfig.addParameter("left_width", "100");
        customConfig.addParameter("right_width", "100");
        customConfig.addParameter("transition_width", "50");
        customConfig.addParameter("max_blur", "3.0");
        customConfig.setFilterExpression("sideblur=left_width=100:right_width=100:transition_width=50:max_blur=3.0");
        
        createCustomFilterConfig(outputDir + "custom_filter_config.txt", customConfig);
        
        // 7. 生成性能测试报告
        System.out.println("\n7. 生成滤镜性能测试报告");
        generateFilterPerformanceReport(inputVideo, outputDir);
        
        System.out.println("\n=== 所有自定义滤镜演示完成 ===");
        System.out.println("输出目录: " + outputDir);
    }
}