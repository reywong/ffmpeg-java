package com.ry.example.ffmpeg.chapter08;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FFmpeg编译配置和构建工具
 * 演示如何编译FFmpeg和集成各种编码器
 */
public class FFmpegCompiler {
    
    /**
     * 生成Windows环境下的FFmpeg编译脚本
     */
    public static void generateWindowsBuildScript(String outputDir) {
        try {
            String scriptContent = createWindowsBuildScript();
            String scriptFile = outputDir + "/build_ffmpeg_windows.bat";
            
            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent);
            }
            
            System.out.println("Windows编译脚本生成成功: " + scriptFile);
            
        } catch (IOException e) {
            System.err.println("生成Windows编译脚本失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成Linux环境下的FFmpeg编译脚本
     */
    public static void generateLinuxBuildScript(String outputDir) {
        try {
            String scriptContent = createLinuxBuildScript();
            String scriptFile = outputDir + "/build_ffmpeg_linux.sh";
            
            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent);
            }
            
            // 设置可执行权限
            new File(scriptFile).setExecutable(true);
            
            System.out.println("Linux编译脚本生成成功: " + scriptFile);
            
        } catch (IOException e) {
            System.err.println("生成Linux编译脚本失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成x264编码器编译脚本
     */
    public static void generateX264BuildScript(String outputDir) {
        try {
            String scriptContent = createX264BuildScript();
            String scriptFile = outputDir + "/build_x264.sh";
            
            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent);
            }
            
            new File(scriptFile).setExecutable(true);
            
            System.out.println("x264编译脚本生成成功: " + scriptFile);
            
        } catch (IOException e) {
            System.err.println("生成x264编译脚本失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成FFmpeg配置文件
     */
    public static void generateFFmpegConfig(String outputDir, BuildConfig config) {
        try {
            StringBuilder configBuilder = new StringBuilder();
            
            configBuilder.append("#!/bin/bash\n");
            configBuilder.append("# FFmpeg配置文件\n");
            configBuilder.append("# 生成时间: " + new java.util.Date() + "\n\n");
            
            // 基本配置
            configBuilder.append("./configure --prefix=" + config.installPrefix + "\\\n");
            
            // 许可证配置
            if (config.enableGpl) configBuilder.append("    --enable-gpl \\\n");
            if (config.enableNonfree) configBuilder.append("    --enable-nonfree \\\n");
            if (config.enableVersion3) configBuilder.append("    --enable-version3 \\\n");
            
            // 共享/静态库配置
            if (config.enableShared) configBuilder.append("    --enable-shared \\\n");
            if (config.disableStatic) configBuilder.append("    --disable-static \\\n");
            
            // 编码器配置
            if (config.enableX264) configBuilder.append("    --enable-libx264 \\\n");
            if (config.enableX265) configBuilder.append("    --enable-libx265 \\\n");
            if (config.enableMP3lame) configBuilder.append("    --enable-libmp3lame \\\n");
            if (config.enableAVS2) configBuilder.append("    --enable-libavs2 \\\n");
            
            // 滤镜配置
            if (config.enableFreeType) configBuilder.append("    --enable-libfreetype \\\n");
            if (config.enableFontConfig) configBuilder.append("    --enable-fontconfig \\\n");
            if (config.enableLibASS) configBuilder.append("    --enable-libass \\\n");
            
            // 其他配置
            if (config.enableOpenSSL) configBuilder.append("    --enable-openssl \\\n");
            if (config.enableGNUTLS) configBuilder.append("    --enable-gnutls \\\n");
            
            // 移除最后的\\\n
            String configContent = configBuilder.toString();
            if (configContent.endsWith(" \\\n")) {
                configContent = configContent.substring(0, configContent.length() - 3);
            }
            
            String configFile = outputDir + "/ffmpeg_config.sh";
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(configContent);
            }
            
            new File(configFile).setExecutable(true);
            
            System.out.println("FFmpeg配置文件生成成功: " + configFile);
            
        } catch (IOException e) {
            System.err.println("生成FFmpeg配置文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建编译报告
     */
    public static void generateBuildReport(String outputDir, List<BuildStep> steps) {
        try {
            StringBuilder report = new StringBuilder();
            
            report.append("# FFmpeg编译报告\n");
            report.append("# 生成时间: " + new java.util.Date() + "\n\n");
            
            report.append("## 编译步骤\n\n");
            
            for (int i = 0; i < steps.size(); i++) {
                BuildStep step = steps.get(i);
                report.append(String.format("### 步骤 %d: %s\n", i + 1, step.description));
                report.append("**命令:**\n```bash\n");
                report.append(step.command).append("\n```\n");
                report.append("**说明:** ").append(step.description).append("\n");
                report.append("**预期时间:** ").append(step.estimatedTime).append("\n\n");
            }
            
            report.append("## 依赖库列表\n\n");
            report.append("| 库名称 | 版本 | 用途 | 编译参数 |\n");
            report.append("|--------|------|------|----------|\n");
            report.append("| x264 | latest | H.264编码 | --enable-libx264 |\n");
            report.append("| x265 | latest | H.265编码 | --enable-libx265 |\n");
            report.append("| mp3lame | 3.100 | MP3编码 | --enable-libmp3lame |\n");
            report.append("| freetype | 2.12.1 | 字体渲染 | --enable-libfreetype |\n");
            report.append("| libass | 0.16.0 | 字幕渲染 | --enable-libass |\n");
            
            report.append("\n## 环境要求\n\n");
            report.append("- 操作系统: Windows 10/11 或 Linux (Ubuntu 18.04+)\n");
            report.append("- 编译器: GCC 7+ 或 MSVC 2017+\n");
            report.append("- CMake: 3.10+\n");
            report.append("- YASM/NASM: 2.13+\n");
            report.append("- Git: 2.0+\n");
            
            report.append("\n## 预期编译时间\n\n");
            report.append("- 依赖库编译: 30-60分钟\n");
            report.append("- FFmpeg编译: 20-40分钟\n");
            report.append("- 总计: 50-100分钟（取决于硬件性能）\n");
            
            String reportFile = outputDir + "/build_report.md";
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(report.toString());
            }
            
            System.out.println("编译报告生成成功: " + reportFile);
            
        } catch (IOException e) {
            System.err.println("生成编译报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查编译环境
     */
    public static BuildEnvironment checkBuildEnvironment() {
        BuildEnvironment env = new BuildEnvironment();
        
        // 检查必要的工具
        env.hasGit = checkCommand("git --version");
        env.hasCMake = checkCommand("cmake --version");
        env.hasYasm = checkCommand("yasm --version");
        env.hasNasm = checkCommand("nasm --version");
        env.hasGCC = checkCommand("gcc --version");
        env.hasMake = checkCommand("make --version");
        env.hasPython = checkCommand("python --version");
        
        // 检查库
        env.hasPkgConfig = checkCommand("pkg-config --version");
        env.hasLibTool = checkCommand("libtoolize --version");
        
        return env;
    }
    
    /**
     * 检查命令是否可用
     */
    private static boolean checkCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 打印编译环境检查结果
     */
    public static void printBuildEnvironment(BuildEnvironment env) {
        System.out.println("=== 编译环境检查 ===");
        System.out.println("Git: " + (env.hasGit ? "✓" : "✗"));
        System.out.println("CMake: " + (env.hasCMake ? "✓" : "✗"));
        System.out.println("YASM: " + (env.hasYasm ? "✓" : "✗"));
        System.out.println("NASM: " + (env.hasNasm ? "✓" : "✗"));
        System.out.println("GCC: " + (env.hasGCC ? "✓" : "✗"));
        System.out.println("Make: " + (env.hasMake ? "✓" : "✗"));
        System.out.println("Python: " + (env.hasPython ? "✓" : "✗"));
        System.out.println("pkg-config: " + (env.hasPkgConfig ? "✓" : "✗"));
        System.out.println("libtool: " + (env.hasLibTool ? "✓" : "✗"));
        System.out.println("===================");
    }
    
    /**
     * 创建Windows编译脚本内容
     */
    private static String createWindowsBuildScript() {
        StringBuilder script = new StringBuilder();
        
        script.append("@echo off\n");
        script.append("echo Starting FFmpeg build on Windows...\n\n");
        
        script.append("REM 设置环境变量\n");
        script.append("set MSYS2_PATH=C:\\msys64\\mingw64\\bin\n");
        script.append("set PATH=%MSYS2_PATH%;%PATH%\n\n");
        
        script.append("REM 1. 克隆FFmpeg源码\n");
        script.append("if not exist ffmpeg (\n");
        script.append("    git clone https://git.ffmpeg.org/ffmpeg.git\n");
        script.append(")\n\n");
        
        script.append("REM 2. 克隆x264源码\n");
        script.append("if not exist x264 (\n");
        script.append("    git clone https://code.videolan.org/videolan/x264.git\n");
        script.append(")\n\n");
        
        script.append("REM 3. 编译x264\n");
        script.append("cd x264\n");
        script.append("bash configure --prefix=/usr/local/x264 --enable-shared --disable-cli\n");
        script.append("make -j4\n");
        script.append("make install\n");
        script.append("cd ..\n\n");
        
        script.append("REM 4. 配置FFmpeg\n");
        script.append("cd ffmpeg\n");
        script.append("bash configure --prefix=/usr/local/ffmpeg \\\n");
        script.append("    --enable-gpl --enable-nonfree \\\n");
        script.append("    --enable-libx264 \\\n");
        script.append("    --enable-shared --disable-static \\\n");
        script.append("    --enable-avresample \\\n");
        script.append("    --enable-fontconfig \\\n");
        script.append("    --enable-libass \\\n");
        script.append("    --enable-libfreetype \\\n");
        script.append("    --enable-libmp3lame\n\n");
        
        script.append("REM 5. 编译FFmpeg\n");
        script.append("make -j4\n");
        script.append("make install\n\n");
        
        script.append("echo FFmpeg build completed!\n");
        script.append("pause\n");
        
        return script.toString();
    }
    
    /**
     * 创建Linux编译脚本内容
     */
    private static String createLinuxBuildScript() {
        StringBuilder script = new StringBuilder();
        
        script.append("#!/bin/bash\n");
        script.append("set -e\n");
        script.append("echo Starting FFmpeg build on Linux...\n\n");
        
        script.append("# 安装依赖\n");
        script.append("sudo apt update\n");
        script.append("sudo apt install -y \\\n");
        script.append("    build-essential \\\n");
        script.append("    cmake \\\n");
        script.append("    git \\\n");
        script.append("    yasm \\\n");
        script.append("    nasm \\\n");
        script.append("    libtool \\\n");
        script.append("    pkg-config \\\n");
        script.append("    libavformat-dev \\\n");
        script.append("    libavcodec-dev \\\n");
        script.append("    libavdevice-dev \\\n");
        script.append("    libavutil-dev \\\n");
        script.append("    libswscale-dev \\\n");
        script.append("    libswresample-dev \\\n");
        script.append("    libavfilter-dev\n\n");
        
        script.append("# 克隆和编译x264\n");
        script.append("if [ ! -d \"x264\" ]; then\n");
        script.append("    git clone https://code.videolan.org/videolan/x264.git\n");
        script.append("fi\n");
        script.append("cd x264\n");
        script.append("./configure --prefix=/usr/local/x264 --enable-shared --disable-cli\n");
        script.append("make -j$(nproc)\n");
        script.append("sudo make install\n");
        script.append("cd ..\n\n");
        
        script.append("# 克隆FFmpeg\n");
        script.append("if [ ! -d \"ffmpeg\" ]; then\n");
        script.append("    git clone https://git.ffmpeg.org/ffmpeg.git\n");
        script.append("fi\n");
        script.append("cd ffmpeg\n");
        
        script.append("# 配置FFmpeg\n");
        script.append("./configure --prefix=/usr/local/ffmpeg \\\n");
        script.append("    --enable-gpl --enable-nonfree \\\n");
        script.append("    --enable-libx264 \\\n");
        script.append("    --enable-shared --disable-static \\\n");
        script.append("    --enable-avresample \\\n");
        script.append("    --enable-fontconfig \\\n");
        script.append("    --enable-libass \\\n");
        script.append("    --enable-libfreetype \\\n");
        script.append("    --enable-libmp3lame \\\n");
        script.append("    --enable-openssl\n\n");
        
        script.append("# 编译安装\n");
        script.append("make -j$(nproc)\n");
        script.append("sudo make install\n\n");
        
        script.append("# 更新动态链接库缓存\n");
        script.append("sudo ldconfig\n\n");
        
        script.append("echo FFmpeg build completed successfully!\n");
        
        return script.toString();
    }
    
    /**
     * 创建x264编译脚本内容
     */
    private static String createX264BuildScript() {
        StringBuilder script = new StringBuilder();
        
        script.append("#!/bin/bash\n");
        script.append("set -e\n");
        script.append("echo Building x264 encoder...\n\n");
        
        script.append("# 克隆x264源码\n");
        script.append("if [ ! -d \"x264\" ]; then\n");
        script.append("    git clone https://code.videolan.org/videolan/x264.git\n");
        script.append("fi\n");
        script.append("cd x264\n\n");
        
        script.append("# 创建构建目录\n");
        script.append("mkdir -p build\n");
        script.append("cd build\n\n");
        
        script.append("# 配置CMake\n");
        script.append("cmake ../source \\\n");
        script.append("    -DCMAKE_INSTALL_PREFIX=/usr/local/x264 \\\n");
        script.append("    -DCMAKE_BUILD_TYPE=Release \\\n");
        script.append("    -DENABLE_SHARED=ON \\\n");
        script.append("    -DENABLE_CLI=OFF \\\n");
        script.append("    -DHIGH_BIT_DEPTH=OFF \\\n");
        script.append("    -DMAIN12=OFF\n\n");
        
        script.append("# 编译安装\n");
        script.append("make -j$(nproc)\n");
        script.append("sudo make install\n\n");
        
        script.append("# 创建符号链接\n");
        script.append("sudo ln -sf /usr/local/x264/lib/libx264.so /usr/lib/libx264.so\n");
        script.append("sudo ln -sf /usr/local/x264/lib/pkgconfig/x264.pc /usr/lib/pkgconfig/x264.pc\n\n");
        
        script.append("# 验证安装\n");
        script.append("pkg-config --modversion x264\n");
        script.append("pkg-config --libs x264\n\n");
        
        script.append("echo x264 build completed successfully!\n");
        
        return script.toString();
    }
    
    /**
     * 构建配置类
     */
    public static class BuildConfig {
        public String installPrefix = "/usr/local/ffmpeg";
        public boolean enableGpl = true;
        public boolean enableNonfree = true;
        public boolean enableVersion3 = false;
        public boolean enableShared = true;
        public boolean disableStatic = true;
        public boolean enableX264 = true;
        public boolean enableX265 = true;
        public boolean enableMP3lame = true;
        public boolean enableAVS2 = false;
        public boolean enableFreeType = true;
        public boolean enableFontConfig = true;
        public boolean enableLibASS = true;
        public boolean enableOpenSSL = true;
        public boolean enableGNUTLS = false;
    }
    
    /**
     * 编译步骤类
     */
    public static class BuildStep {
        public String description;
        public String command;
        public String estimatedTime;
        
        public BuildStep(String description, String command, String estimatedTime) {
            this.description = description;
            this.command = command;
            this.estimatedTime = estimatedTime;
        }
    }
    
    /**
     * 编译环境检查结果
     */
    public static class BuildEnvironment {
        public boolean hasGit;
        public boolean hasCMake;
        public boolean hasYasm;
        public boolean hasNasm;
        public boolean hasGCC;
        public boolean hasMake;
        public boolean hasPython;
        public boolean hasPkgConfig;
        public boolean hasLibTool;
        
        public boolean isReady() {
            return hasGit && hasCMake && (hasYasm || hasNasm) && hasGCC && hasMake;
        }
    }
    
    public static void main(String[] args) {
        String outputDir = "output/ffmpeg_build/";
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        System.out.println("=== FFmpeg编译配置生成器 ===\n");
        
        // 1. 检查编译环境
        System.out.println("1. 检查编译环境");
        BuildEnvironment env = checkBuildEnvironment();
        printBuildEnvironment(env);
        
        if (!env.isReady()) {
            System.out.println("警告：编译环境不完整，某些工具缺失");
        }
        
        // 2. 生成编译脚本
        System.out.println("\n2. 生成编译脚本");
        generateWindowsBuildScript(outputDir);
        generateLinuxBuildScript(outputDir);
        generateX264BuildScript(outputDir);
        
        // 3. 生成FFmpeg配置
        System.out.println("\n3. 生成FFmpeg配置");
        BuildConfig config = new BuildConfig();
        generateFFmpegConfig(outputDir, config);
        
        // 4. 生成编译报告
        System.out.println("\n4. 生成编译报告");
        List<BuildStep> steps = new ArrayList<>();
        steps.add(new BuildStep("安装依赖", "apt install build-essential cmake git yasm nasm", "5-10分钟"));
        steps.add(new BuildStep("克隆x264源码", "git clone https://code.videolan.org/videolan/x264.git", "1-2分钟"));
        steps.add(new BuildStep("编译x264", "make -j$(nproc)", "10-20分钟"));
        steps.add(new BuildStep("克隆FFmpeg源码", "git clone https://git.ffmpeg.org/ffmpeg.git", "2-5分钟"));
        steps.add(new BuildStep("配置FFmpeg", "./configure --enable-libx264...", "2-5分钟"));
        steps.add(new BuildStep("编译FFmpeg", "make -j$(nproc)", "20-40分钟"));
        steps.add(new BuildStep("安装FFmpeg", "make install", "1-2分钟"));
        
        generateBuildReport(outputDir, steps);
        
        System.out.println("\n=== 编译配置生成完成 ===");
        System.out.println("输出目录: " + outputDir);
        System.out.println("\n生成的文件:");
        
        // 列出生成的文件
        File dir = new File(outputDir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println("  " + file.getName());
            }
        }
    }
}