# 第 1 章 FFmpeg环境搭建

FFmpeg 是一个功能强大的多媒体处理框架，本章将详细介绍 FFmpeg 的基本概念、安装方法以及开发环境的搭建。通过本章的学习，读者将能够在不同操作系统上成功安装和配置 FFmpeg 开发环境。

## 1.1 FFmpeg简介

### 1.1.1 FFmpeg是什么

FFmpeg 是一个开源的音视频处理工具集，由 Fabrice Bellard 于 2000 年发起创建。它提供了完整的音视频录制、转换、流媒体传输等功能，是目前功能最强大的多媒体处理框架之一。

**FFmpeg 的核心特性：**
- **格式支持广泛**：支持超过 100 种音视频格式
- **功能全面**：包含编解码、容器格式处理、滤镜、流媒体等
- **跨平台**：支持 Linux、Windows、macOS、Android、iOS 等
- **开源免费**：基于 LGPL/GPL 许可证，可自由使用和修改
- **高性能**：采用 C 语言编写，执行效率高

```java
// FFmpeg 版本信息检查示例
public class FFmpegVersionChecker {
    
    public static void checkFFmpegVersion() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ffmpeg version")) {
                    System.out.println("FFmpeg版本信息: " + line);
                    break;
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("FFmpeg 安装正常");
            } else {
                System.out.println("FFmpeg 安装异常");
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("检查 FFmpeg 版本失败: " + e.getMessage());
        }
    }
}
```

### 1.1.2 FFmpeg的用途

FFmpeg 在音视频领域有着广泛的应用，主要包括以下几个方面：

**1. 格式转换**
```bash
# 视频格式转换示例
ffmpeg -i input.avi -c:v libx264 -c:a aac output.mp4

# 音频格式转换示例  
ffmpeg -i input.mp3 -c:a libvorbis output.ogg
```

**2. 视频编辑**
```bash
# 视频裁剪
ffmpeg -i input.mp4 -ss 00:01:00 -t 00:00:30 -c copy output.mp4

# 视频合并
ffmpeg -f concat -i filelist.txt -c copy output.mp4
```

**3. 流媒体处理**
```bash
# 推流到 RTMP 服务器
ffmpeg -re -i input.mp4 -c copy -f flv rtmp://server/live/stream

# 从 RTMP 拉流
ffmpeg -i rtmp://server/live/stream -c copy output.flv
```

**4. 视频分析**
```bash
# 获取视频信息
ffprobe -v quiet -print_format json -show_format -show_streams input.mp4
```

**5. 实时应用场景**
- **直播平台**：视频采集、编码、推流
- **视频编辑软件**：格式转换、特效处理
- **监控系统**：录像存储、实时预览
- **在线教育**：屏幕录制、视频转码

```java
// FFmpeg 功能封装类
public class FFmpegProcessor {
    private String ffmpegPath;
    
    public FFmpegProcessor(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }
    
    // 视频转码
    public boolean convertVideo(String input, String output, String videoCodec, String audioCodec) {
        try {
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-i");
            command.add(input);
            command.add("-c:v");
            command.add(videoCodec);
            command.add("-c:a");
            command.add(audioCodec);
            command.add(output);
            
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取视频信息
    public String getVideoInfo(String input) {
        try {
            List<String> command = new ArrayList<>();
            command.add("ffprobe");
            command.add("-v");
            command.add("quiet");
            command.add("-print_format");
            command.add("json");
            command.add("-show_format");
            command.add("-show_streams");
            command.add(input);
            
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### 1.1.3 FFmpeg的发展历程

FFmpeg 的发展经历了从个人项目到行业标准的重要历程：

**早期阶段（2000-2004）**
- 2000年：Fabrice Bellard 开始开发 FFmpeg
- 2001年：第一个版本发布，支持基本的视频编解码
- 2002年：增加了音频支持和更多视频格式

**成长阶段（2005-2010）**
- 2005年：开始支持 H.264 编解码
- 2007年：发布了 libavfilter 过滤器框架
- 2009年：增加了硬件加速支持

**成熟阶段（2011-2016）**
- 2011年：libav 项目分离（后重新合并）
- 2013年：H.265/HEVC 解码器支持
- 2015年：增加了 VP9 编解码器

**现代阶段（2017至今）**
- 2018年：AV1 解码器支持
- 2020年：H.266/VVC 解码器预览
- 2022年：增加了更多的 AI 相关功能

```java
// FFmpeg 版本历史查询工具
public class FFmpegHistory {
    
    public static void displayVersionHistory() {
        Map<String, String> versionHistory = new LinkedHashMap<>();
        
        versionHistory.put("0.3", "2001年 - 初始版本，支持基本编解码");
        versionHistory.put("0.4.9", "2004年 - 稳定版本，功能完善");
        versionHistory.put("0.5", "2009年 - 支持H.264编码");
        versionHistory.put("0.6", "2010年 - 增加滤镜系统");
        versionHistory.put("0.8", "2011年 - libav项目分离");
        versionHistory.put("0.9", "2011年 - 项目重新合并");
        versionHistory.put("1.0", "2013年 - H.265/HEVC支持");
        versionHistory.put("2.0", "2013年 - 新版本号系统");
        versionHistory.put("3.0", "2016年 - VP9编码器完善");
        versionHistory.put("4.0", "2018年 - AV1解码器支持");
        versionHistory.put("5.0", "2022年 - H.266/VVC支持");
        
        System.out.println("FFmpeg 版本发展历程：");
        System.out.println("==================");
        
        for (Map.Entry<String, String> entry : versionHistory.entrySet()) {
            System.out.printf("%-6s - %s%n", entry.getKey(), entry.getValue());
        }
    }
    
    public static void checkSupportedCodecs() {
        System.out.println("支持的编解码器：");
        System.out.println("================");
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-codecs");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("D.V.L.") || line.startsWith(" DEV.L.") || 
                    line.startsWith("DEV.LS.") || line.startsWith("D.VI.S")) {
                    System.out.println(line);
                }
            }
            
        } catch (IOException e) {
            System.err.println("获取编解码器列表失败: " + e.getMessage());
        }
    }
}
```

## 1.2 Linux系统安装FFmpeg

### 1.2.1 Linux开发机配置要求

**硬件要求**

**最低配置：**
- CPU: 双核 2.0GHz 以上
- 内存: 4GB RAM
- 硬盘: 20GB 可用空间
- 网络: 稳定的互联网连接

**推荐配置：**
- CPU: 四核 3.0GHz 以上
- 内存: 8GB RAM 或更多
- 硬盘: 50GB SSD
- GPU: 支持 CUDA 或 OpenCL（用于硬件加速）

**操作系统要求**
- Ubuntu 18.04 LTS 或更高版本
- CentOS 7 或更高版本
- Debian 9 或更高版本
- Fedora 30 或更高版本

```bash
# 系统信息检查脚本
#!/bin/bash

echo "=== 系统配置检查 ==="
echo "操作系统: $(uname -a)"
echo "CPU信息: $(grep 'model name' /proc/cpuinfo | head -1)"
echo "内存信息: $(free -h | grep Mem)"
echo "磁盘空间: $(df -h /)"

echo -e "\n=== 开发环境检查 ==="
# 检查编译器
if command -v gcc &> /dev/null; then
    echo "GCC版本: $(gcc --version | head -1)"
else
    echo "GCC未安装"
fi

# 检查make
if command -v make &> /dev/null; then
    echo "Make版本: $(make --version | head -1)"
else
    echo "Make未安装"
fi

# 检查cmake
if command -v cmake &> /dev/null; then
    echo "CMake版本: $(cmake --version | head -1)"
else
    echo "CMake未安装"
fi

echo -e "\n=== 网络连接检查 ==="
if ping -c 1 google.com &> /dev/null; then
    echo "网络连接正常"
else
    echo "网络连接异常"
fi
```

### 1.2.2 安装已编译的FFmpeg及其SO库

**Ubuntu/Debian 系统**

```bash
# 1. 更新软件包列表
sudo apt update

# 2. 安装 FFmpeg
sudo apt install ffmpeg

# 3. 安装开发库
sudo apt install libavcodec-dev libavformat-dev libavutil-dev libswscale-dev
sudo apt install libswresample-dev libavfilter-dev libavdevice-dev
sudo apt install libpostproc-dev libavresample-dev

# 4. 安装额外的编解码器
sudo apt install libx264-dev libx265-dev libvpx-dev
sudo apt install libmp3lame-dev libopus-dev libvorbis-dev

# 5. 验证安装
ffmpeg -version
```

**CentOS/RHEL 系统**

```bash
# 1. 启用 EPEL 仓库
sudo yum install epel-release

# 2. 启用 RPM Fusion 仓库
sudo yum localinstall --nogpgcheck https://download1.rpmfusion.org/free/el/rpmfusion-free-release-7.noarch.rpm

# 3. 安装 FFmpeg
sudo yum install ffmpeg

# 4. 安装开发库
sudo yum install ffmpeg-devel

# 5. 验证安装
ffmpeg -version
```

**验证安装**

```java
// Linux 环境下的 FFmpeg 安装验证器
public class LinuxFFmpegVerifier {
    
    public static class InstallationInfo {
        public String version;
        public List<String> codecs;
        public List<String> formats;
        public List<String> filters;
        public String libraryPath;
        public boolean is64Bit;
    }
    
    public static InstallationInfo verifyInstallation() {
        InstallationInfo info = new InstallationInfo();
        
        // 获取版本信息
        info.version = getFFmpegVersion();
        
        // 获取支持的编解码器
        info.codecs = getSupportedCodecs();
        
        // 获取支持的格式
        info.formats = getSupportedFormats();
        
        // 获取支持的滤镜
        info.filters = getSupportedFilters();
        
        // 获取库文件路径
        info.libraryPath = getLibraryPath();
        
        // 检查系统架构
        info.is64Bit = System.getProperty("os.arch").contains("64");
        
        return info;
    }
    
    private static String getFFmpegVersion() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String firstLine = reader.readLine();
            if (firstLine != null && firstLine.contains("ffmpeg version")) {
                return firstLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "未知";
    }
    
    private static List<String> getSupportedCodecs() {
        List<String> codecs = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-codecs");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(" DEV.L.") || line.startsWith("D.V.L.") || 
                    line.startsWith("D.VI.S")) {
                    codecs.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return codecs;
    }
    
    private static List<String> getSupportedFormats() {
        List<String> formats = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-formats");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            boolean startReading = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("--")) {
                    startReading = true;
                    continue;
                }
                if (startReading && line.trim().length() > 0) {
                    formats.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return formats;
    }
    
    private static List<String> getSupportedFilters() {
        List<String> filters = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-filters");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            boolean startReading = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("--")) {
                    startReading = true;
                    continue;
                }
                if (startReading && line.trim().length() > 0) {
                    filters.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return filters;
    }
    
    private static String getLibraryPath() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ldconfig", "-p");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("libavcodec")) {
                    return line.trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "未找到库文件";
    }
    
    public static void printInstallationReport(InstallationInfo info) {
        System.out.println("=== FFmpeg 安装验证报告 ===");
        System.out.println("版本信息: " + info.version);
        System.out.println("系统架构: " + (info.is64Bit ? "64位" : "32位"));
        System.out.println("库文件路径: " + info.libraryPath);
        
        System.out.println("\n支持的编解码器数量: " + info.codecs.size());
        System.out.println("支持的格式数量: " + info.formats.size());
        System.out.println("支持的滤镜数量: " + info.filters.size());
        
        System.out.println("\n关键编解码器:");
        String[] keyCodecs = {"h264", "hevc", "aac", "mp3", "opus"};
        for (String codec : keyCodecs) {
            boolean found = info.codecs.stream().anyMatch(c -> c.contains(codec));
            System.out.println("  " + codec + ": " + (found ? "✓" : "✗"));
        }
    }
}
```

### 1.2.3 自行编译与安装FFmpeg

**编译环境准备**

```bash
#!/bin/bash
# FFmpeg 编译环境准备脚本

# 1. 安装基本编译工具
sudo apt update
sudo apt install -y build-essential cmake pkg-config

# 2. 安装 FFmpeg 依赖库
sudo apt install -y \
    libavcodec-dev \
    libavformat-dev \
    libavutil-dev \
    libswscale-dev \
    libswresample-dev \
    libavfilter-dev \
    libavdevice-dev

# 3. 安装额外的编解码器依赖
sudo apt install -y \
    libx264-dev \
    libx265-dev \
    libvpx-dev \
    libmp3lame-dev \
    libopus-dev \
    libvorbis-dev \
    libtheora-dev \
    libspeex-dev

# 4. 安装图像处理库
sudo apt install -y \
    libpng-dev \
    libjpeg-dev \
    libtiff-dev \
    libwebp-dev

# 5. 安装字体库（用于字幕）
sudo apt install -y \
    libfreetype6-dev \
    libfontconfig1-dev

# 6. 安装硬件加速支持
sudo apt install -y \
    libva-dev \
    libvdpau-dev

echo "编译环境准备完成"
```

**下载源码**

```bash
#!/bin/bash
# FFmpeg 源码下载脚本

FFMPEG_VERSION="4.4"
INSTALL_DIR="/usr/local/ffmpeg"
SOURCE_DIR="/tmp/ffmpeg_source"

# 创建源码目录
mkdir -p $SOURCE_DIR
cd $SOURCE_DIR

# 下载 FFmpeg 源码
if [ ! -d "ffmpeg" ]; then
    echo "下载 FFmpeg 源码..."
    git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg
    cd ffmpeg
    git checkout n$FFMPEG_VERSION
else
    echo "FFmpeg 源码已存在，更新..."
    cd ffmpeg
    git pull
fi

echo "FFmpeg 源码准备完成"
```

**编译配置**

```bash
#!/bin/bash
# FFmpeg 编译配置脚本

FFMPEG_VERSION="4.4"
INSTALL_DIR="/usr/local/ffmpeg"
SOURCE_DIR="/tmp/ffmpeg_source/ffmpeg"

cd $SOURCE_DIR

# 创建安装目录
sudo mkdir -p $INSTALL_DIR

# 配置编译选项
./configure \
    --prefix=$INSTALL_DIR \
    --enable-gpl \
    --enable-version3 \
    --enable-nonfree \
    --enable-shared \
    --enable-static \
    --enable-libx264 \
    --enable-libx265 \
    --enable-libvpx \
    --enable-libmp3lame \
    --enable-libopus \
    --enable-libvorbis \
    --enable-libtheora \
    --enable-libspeex \
    --enable-libfreetype \
    --enable-libfontconfig \
    --enable-libv4l2 \
    --enable-libpulse \
    --enable-libx11 \
    --enable-libxcb \
    --enable-libxcb-shape \
    --enable-libxcb-xfixes \
    --enable-libxcb-shm \
    --enable-zlib \
    --enable-bzlib \
    --enable-libopenjpeg \
    --enable-libwebp \
    --enable-libtiff \
    --enable-libpng \
    --enable-indev=v4l2 \
    --enable-outdev=v4l2 \
    --enable-vaapi \
    --enable-vdpau \
    --enable-ffmpeg \
    --enable-ffplay \
    --enable-ffprobe \
    --enable-optimizations \
    --enable-mmx \
    --enable-sse2 \
    --enable-ssse3 \
    --enable-avx \
    --enable-avx2 \
    --enable-pic \
    --enable-pthreads \
    --disable-debug \
    --disable-stripping

echo "FFmpeg 配置完成"
```

**编译和安装**

```bash
#!/bin/bash
# FFmpeg 编译和安装脚本

SOURCE_DIR="/tmp/ffmpeg_source/ffmpeg"
INSTALL_DIR="/usr/local/ffmpeg"

cd $SOURCE_DIR

# 清理之前的编译
make clean

# 开始编译（使用多核心加速编译）
echo "开始编译 FFmpeg..."
make -j$(nproc)

# 编译完成，检查结果
if [ $? -eq 0 ]; then
    echo "编译成功，开始安装..."
    sudo make install
    sudo ldconfig
    
    echo "安装完成！"
    echo "FFmpeg 安装位置: $INSTALL_DIR"
    
    # 验证安装
    $INSTALL_DIR/bin/ffmpeg -version
    
else
    echo "编译失败！"
    exit 1
fi
```

**环境变量配置**

```bash
#!/bin/bash
# 环境变量配置脚本

INSTALL_DIR="/usr/local/ffmpeg"
PROFILE_FILE="$HOME/.bashrc"

# 检查是否已经配置
if grep -q "$INSTALL_DIR" $PROFILE_FILE; then
    echo "环境变量已配置"
else
    echo "配置环境变量..."
    
    # 添加 FFmpeg 到 PATH
    echo "" >> $PROFILE_FILE
    echo "# FFmpeg 环境变量" >> $PROFILE_FILE
    echo "export FFMPEG_HOME=$INSTALL_DIR" >> $PROFILE_FILE
    echo "export PATH=\$FFMPEG_HOME/bin:\$PATH" >> $PROFILE_FILE
    echo "export LD_LIBRARY_PATH=\$FFMPEG_HOME/lib:\$LD_LIBRARY_PATH" >> $PROFILE_FILE
    
    # 立即生效
    source $PROFILE_FILE
    
    echo "环境变量配置完成"
    echo "请运行 'source ~/.bashrc' 使配置生效"
fi

# 验证环境变量
echo "当前环境变量:"
echo "FFMPEG_HOME: $FFMPEG_HOME"
echo "PATH: $PATH"
echo "LD_LIBRARY_PATH: $LD_LIBRARY_PATH"
```

**编译验证脚本**

```java
// FFmpeg 编译验证工具
public class FFmpegCompileVerifier {
    
    public static class CompileInfo {
        public String compileTime;
        public String configuration;
        public List<String> libraries;
        public List<String> enabledFeatures;
        public boolean isSharedBuild;
        public boolean isStaticBuild;
    }
    
    public static CompileInfo verifyCompile() {
        CompileInfo info = new CompileInfo();
        
        try {
            // 获取编译配置信息
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("configuration:")) {
                    info.configuration = line;
                } else if (line.contains("libavutil") && line.contains("built")) {
                    info.compileTime = line;
                }
            }
            
            // 解析配置信息
            parseConfiguration(info);
            
            // 获取库信息
            info.libraries = getLibraryVersions();
            
            // 检查编译类型
            info.isSharedBuild = info.configuration.contains("--enable-shared");
            info.isStaticBuild = info.configuration.contains("--enable-static");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    private static void parseConfiguration(CompileInfo info) {
        if (info.configuration == null) return;
        
        String[] configParts = info.configuration.split(" ");
        info.enabledFeatures = new ArrayList<>();
        
        for (String part : configParts) {
            if (part.startsWith("--enable-") && !part.contains("shared") && 
                !part.contains("static")) {
                info.enabledFeatures.add(part.substring(9));
            }
        }
    }
    
    private static List<String> getLibraryVersions() {
        List<String> versions = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("libav") && !line.contains("built")) {
                    versions.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return versions;
    }
    
    public static void printCompileReport(CompileInfo info) {
        System.out.println("=== FFmpeg 编译验证报告 ===");
        System.out.println("编译时间: " + info.compileTime);
        
        System.out.println("\n编译类型:");
        System.out.println("  共享库: " + (info.isSharedBuild ? "✓" : "✗"));
        System.out.println("  静态库: " + (info.isStaticBuild ? "✓" : "✗"));
        
        System.out.println("\n启用的功能 (" + info.enabledFeatures.size() + " 个):");
        for (String feature : info.enabledFeatures) {
            System.out.println("  " + feature);
        }
        
        System.out.println("\n库版本信息:");
        for (String library : info.libraries) {
            System.out.println("  " + library);
        }
    }
}
```

## 1.3 在Windows系统下安装FFmpeg

### 1.3.1 Windows开发机配置要求

**硬件要求**

**最低配置：**
- CPU: 双核 2.0GHz 以上
- 内存: 4GB RAM
- 硬盘: 10GB 可用空间
- 网络: 稳定的互联网连接

**推荐配置：**
- CPU: 四核 3.0GHz 以上（支持 AVX 指令集）
- 内存: 8GB RAM 或更多
- 硬盘: 30GB SSD
- GPU: 支持 NVIDIA CUDA 或 Intel Quick Sync

**软件要求**
- Windows 10/11 (64位)
- Visual Studio 2019 或更高版本（用于编译）
- Git for Windows
- CMake 3.15 或更高版本

```batch
@echo off
REM Windows 系统信息检查脚本

echo === 系统配置检查 ===
echo 操作系统: %OS%
echo 处理器: %PROCESSOR_IDENTIFIER%
echo 内存: 
wmic OS get TotalVisibleMemorySize,FreePhysicalMemory /format:table | findstr /v /C:"TotalVisibleMemorySize"
echo 磁盘空间:
wmic logicaldisk get size,freespace,caption /format:table

echo.
echo === 开发环境检查 ===
REM 检查 Visual Studio
if exist "C:\Program Files (x86)\Microsoft Visual Studio\2019" (
    echo Visual Studio 2019: 已安装
) else if exist "C:\Program Files\Microsoft Visual Studio\2022" (
    echo Visual Studio 2022: 已安装
) else (
    echo Visual Studio: 未安装
)

REM 检查 Git
git --version >nul 2>&1
if %errorlevel% equ 0 (
    echo Git: 已安装
) else (
    echo Git: 未安装
)

REM 检查 CMake
cmake --version >nul 2>&1
if %errorlevel% equ 0 (
    echo CMake: 已安装
) else (
    echo CMake: 未安装
)

echo.
echo === 网络连接检查 ===
ping -n 1 google.com >nul 2>&1
if %errorlevel% equ 0 (
    echo 网络连接: 正常
) else (
    echo 网络连接: 异常
)

pause
```

### 1.3.2 安装依赖的Windows软件

**1. 安装 Visual Studio**

下载并安装 Visual Studio 2019 或 2022：
- 访问 https://visualstudio.microsoft.com/zh-hans/
- 下载 Community 版本（免费）
- 安装时选择"使用 C++ 的桌面开发"工作负载
- 包含以下组件：
  - MSVC v143 或 v142 编译器工具集
  - Windows 10/11 SDK
  - CMake 工具

**2. 安装 Git for Windows**

```batch
REM 下载并安装 Git
REM 下载地址: https://git-scm.com/download/win

REM 静默安装命令
git installer.exe /VERYSILENT /NORESTART
```

**3. 安装 CMake**

```batch
REM 下载并安装 CMake
REM 下载地址: https://cmake.org/download/

REM 静默安装命令
cmake installer.exe /S
```

**4. 安装 MSYS2**

MSYS2 提供了 Linux 工具链在 Windows 上的支持：

```batch
REM 下载并安装 MSYS2
REM 下载地址: https://www.msys2.org/

REM 安装后更新包管理器
pacman -Syu
pacman -Su

REM 安装开发工具
pacman -S --needed base-devel mingw-w64-x86_64-toolchain
pacman -S mingw-w64-x86_64-yasm
pacman -S mingw-w64-x86_64-nasm
pacman -S mingw-w64-x86_64-pkg-config
```

**5. 环境变量配置**

```java
// Windows 环境变量管理器
public class WindowsEnvironmentManager {
    
    public static class EnvironmentConfig {
        public String ffmpegPath;
        public String includePath;
        public String libPath;
        public String binPath;
    }
    
    public static boolean configureEnvironment(EnvironmentConfig config) {
        try {
            // 获取当前环境变量
            String path = System.getenv("PATH");
            String include = System.getenv("INCLUDE");
            String lib = System.getenv("LIB");
            
            // 添加 FFmpeg 路径
            String newPath = config.binPath + ";" + path;
            
            // 设置用户环境变量
            boolean success = true;
            
            success &= setEnvironmentVariable("PATH", newPath);
            success &= setEnvironmentVariable("FFMPEG_HOME", config.ffmpegPath);
            success &= setEnvironmentVariable("FFMPEG_INCLUDE", config.includePath);
            success &= setEnvironmentVariable("FFMPEG_LIB", config.libPath);
            
            return success;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean setEnvironmentVariable(String name, String value) {
        try {
            // 使用 reg 命令设置环境变量
            String command = String.format(
                "reg add \"HKEY_CURRENT_USER\\Environment\" /v %s /t REG_EXPAND_SZ /d \"%s\" /f",
                name, value);
            
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
            Process process = pb.start();
            
            return process.waitFor() == 0;
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean verifyEnvironment() {
        try {
            // 检查 FFmpeg 是否在 PATH 中
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            
            boolean success = process.waitFor() == 0;
            
            if (success) {
                // 读取输出验证
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                
                String firstLine = reader.readLine();
                return firstLine != null && firstLine.contains("ffmpeg version");
            }
            
            return false;
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

### 1.3.3 安装已编译的FFmpeg及其DLL库

**方法一：下载预编译版本**

```powershell
# PowerShell 脚本：下载 FFmpeg 预编译版本

$FFMPEG_VERSION = "4.4"
$DOWNLOAD_DIR = "C:\ffmpeg"
$DOWNLOAD_URL = "https://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-$FFMPEG_VERSION-win64-shared.zip"

# 创建下载目录
if (-not (Test-Path $DOWNLOAD_DIR)) {
    New-Item -ItemType Directory -Path $DOWNLOAD_DIR
}

# 下载 FFmpeg
Write-Host "下载 FFmpeg..."
Invoke-WebRequest -Uri $DOWNLOAD_URL -OutFile "$DOWNLOAD_DIR\ffmpeg.zip"

# 解压文件
Write-Host "解压文件..."
Expand-Archive -Path "$DOWNLOAD_DIR\ffmpeg.zip" -DestinationPath $DOWNLOAD_DIR -Force

# 移动文件到目标目录
$extractedDir = Get-ChildItem -Path $DOWNLOAD_DIR -Directory | Where-Object { $_.Name -like "ffmpeg*" }
if ($extractedDir) {
    Move-Item -Path "$($extractedDir.FullName)\*" -Destination $DOWNLOAD_DIR -Force
    Remove-Item -Path $extractedDir.FullName -Recurse -Force
}

# 清理
Remove-Item "$DOWNLOAD_DIR\ffmpeg.zip"

Write-Host "FFmpeg 安装完成！"
Write-Host "安装路径: $DOWNLOAD_DIR"
```

**方法二：使用 Chocolatey 安装**

```powershell
# 使用 Chocolatey 包管理器安装 FFmpeg

# 安装 Chocolatey（如果未安装）
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

# 安装 FFmpeg
choco install ffmpeg

# 安装开发库
choco install ffmpeg-full-shared
```

**DLL 库配置**

```java
// Windows DLL 库管理器
public class WindowsDllManager {
    
    public static class LibraryInfo {
        public String libraryName;
        public String version;
        public String filePath;
        public boolean isLoaded;
        public long size;
    }
    
    // 检查必要的 DLL 文件
    public static List<LibraryInfo> checkRequiredLibraries(String ffmpegPath) {
        List<LibraryInfo> libraries = new ArrayList<>();
        
        String[] requiredDlls = {
            "avcodec-59.dll",
            "avformat-59.dll", 
            "avutil-57.dll",
            "swscale-6.dll",
            "swresample-4.dll"
        };
        
        for (String dllName : requiredDlls) {
            LibraryInfo info = checkLibrary(ffmpegPath, dllName);
            libraries.add(info);
        }
        
        return libraries;
    }
    
    private static LibraryInfo checkLibrary(String ffmpegPath, String dllName) {
        LibraryInfo info = new LibraryInfo();
        info.libraryName = dllName;
        info.isLoaded = false;
        
        String dllPath = ffmpegPath + "\\" + dllName;
        File dllFile = new File(dllPath);
        
        if (dllFile.exists()) {
            info.filePath = dllPath;
            info.size = dllFile.length();
            
            // 尝试加载 DLL
            try {
                System.load(dllPath);
                info.isLoaded = true;
                info.version = getDllVersion(dllPath);
            } catch (UnsatisfiedLinkError e) {
                System.err.println("无法加载 " + dllName + ": " + e.getMessage());
            }
        }
        
        return info;
    }
    
    private static String getDllVersion(String dllPath) {
        try {
            // 使用 JNA 获取 DLL 版本信息
            // 这里简化处理，返回文件版本
            File dllFile = new File(dllPath);
            return "文件大小: " + dllFile.length() + " bytes";
        } catch (Exception e) {
            return "未知版本";
        }
    }
    
    // 注册 DLL 到系统
    public static boolean registerDlls(String ffmpegPath) {
        try {
            String[] requiredDlls = {
                "avcodec-59.dll",
                "avformat-59.dll", 
                "avutil-57.dll",
                "swscale-6.dll",
                "swresample-4.dll"
            };
            
            for (String dllName : requiredDlls) {
                String dllPath = ffmpegPath + "\\" + dllName;
                
                // 使用 regsvr32 注册（如果适用）
                ProcessBuilder pb = new ProcessBuilder("regsvr32", "/s", dllPath);
                Process process = pb.start();
                
                if (process.waitFor() != 0) {
                    System.err.println("注册 " + dllName + " 失败");
                    return false;
                }
            }
            
            return true;
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 验证 DLL 依赖关系
    public static boolean verifyDependencies(String ffmpegPath) {
        try {
            String[] requiredDlls = {
                "avcodec-59.dll",
                "avformat-59.dll", 
                "avutil-57.dll",
                "swscale-6.dll",
                "swresample-4.dll"
            };
            
            // 使用 dumpbin 检查依赖
            for (String dllName : requiredDlls) {
                String dllPath = ffmpegPath + "\\" + dllName;
                
                ProcessBuilder pb = new ProcessBuilder(
                    "dumpbin", "/DEPENDENTS", dllPath);
                Process process = pb.start();
                
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
                
                boolean hasMissingDeps = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Summary")) {
                        break;
                    }
                    // 检查是否有缺失的依赖
                }
                
                if (hasMissingDeps) {
                    System.err.println(dllName + " 存在缺失依赖");
                    return false;
                }
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

**Windows 验证工具**

```java
// Windows FFmpeg 安装验证器
public class WindowsFFmpegVerifier {
    
    public static class WindowsInstallInfo {
        public String installationPath;
        public List<String> availableCodecs;
        public List<String> availableFormats;
        public List<WindowsDllManager.LibraryInfo> dllLibraries;
        public boolean pathConfigured;
        public boolean dllsLoaded;
        public String systemInfo;
    }
    
    public static WindowsInstallInfo verifyInstallation() {
        WindowsInstallInfo info = new WindowsInstallInfo();
        
        // 获取安装路径
        info.installationPath = getFFmpegInstallationPath();
        
        // 检查环境变量配置
        info.pathConfigured = isPathConfigured();
        
        // 检查 DLL 库
        if (info.installationPath != null) {
            info.dllLibraries = WindowsDllManager.checkRequiredLibraries(info.installationPath);
            info.dllsLoaded = info.dllLibraries.stream().anyMatch(lib -> lib.isLoaded);
        }
        
        // 获取系统信息
        info.systemInfo = getSystemInfo();
        
        // 如果 FFmpeg 可用，获取详细信息
        if (info.pathConfigured && info.dllsLoaded) {
            info.availableCodecs = getSupportedCodecs();
            info.availableFormats = getSupportedFormats();
        }
        
        return info;
    }
    
    private static String getFFmpegInstallationPath() {
        try {
            // 从 PATH 中查找 ffmpeg.exe
            String pathEnv = System.getenv("PATH");
            String[] paths = pathEnv.split(";");
            
            for (String path : paths) {
                if (path.toLowerCase().contains("ffmpeg")) {
                    String ffmpegExe = path + "\\ffmpeg.exe";
                    if (new File(ffmpegExe).exists()) {
                        return path;
                    }
                }
            }
            
            // 尝试常见安装路径
            String[] commonPaths = {
                "C:\\ffmpeg\\bin",
                "C:\\Program Files\\ffmpeg\\bin",
                "C:\\tools\\ffmpeg\\bin"
            };
            
            for (String path : commonPaths) {
                String ffmpegExe = path + "\\ffmpeg.exe";
                if (new File(ffmpegExe).exists()) {
                    return path;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static boolean isPathConfigured() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
    
    private static String getSystemInfo() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("操作系统: ").append(System.getProperty("os.name")).append("\n");
        sb.append("版本: ").append(System.getProperty("os.version")).append("\n");
        sb.append("架构: ").append(System.getProperty("os.arch")).append("\n");
        sb.append("Java版本: ").append(System.getProperty("java.version")).append("\n");
        
        return sb.toString();
    }
    
    private static List<String> getSupportedCodecs() {
        List<String> codecs = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-codecs");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(" DEV.L.") || line.startsWith("D.V.L.") || 
                    line.startsWith("D.VI.S")) {
                    codecs.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return codecs;
    }
    
    private static List<String> getSupportedFormats() {
        List<String> formats = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-formats");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            boolean startReading = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("--")) {
                    startReading = true;
                    continue;
                }
                if (startReading && line.trim().length() > 0) {
                    formats.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return formats;
    }
    
    public static void printVerificationReport(WindowsInstallInfo info) {
        System.out.println("=== Windows FFmpeg 安装验证报告 ===");
        System.out.println("安装路径: " + info.installationPath);
        System.out.println("环境变量配置: " + (info.pathConfigured ? "✓" : "✗"));
        System.out.println("DLL库加载: " + (info.dllsLoaded ? "✓" : "✗"));
        
        System.out.println("\n系统信息:");
        System.out.println(info.systemInfo);
        
        if (info.dllLibraries != null) {
            System.out.println("\nDLL库状态:");
            for (WindowsDllManager.LibraryInfo lib : info.dllLibraries) {
                System.out.printf("  %-20s: %s (%d bytes)%n", 
                    lib.libraryName, 
                    lib.isLoaded ? "已加载" : "未加载",
                    lib.size);
            }
        }
        
        if (info.availableCodecs != null && info.availableFormats != null) {
            System.out.println("\n功能支持:");
            System.out.println("支持的编解码器数量: " + info.availableCodecs.size());
            System.out.println("支持的格式数量: " + info.availableFormats.size());
        }
        
        System.out.println("\n建议:");
        if (!info.pathConfigured) {
            System.out.println("- 请将 FFmpeg 安装路径添加到系统 PATH 环境变量");
        }
        if (!info.dllsLoaded) {
            System.out.println("- 请检查必要的 DLL 文件是否存在");
        }
    }
}
```

## 1.4 FFmpeg的开发框架

### 1.4.1 可执行程序

FFmpeg 提供了三个主要的可执行程序，每个程序都有其特定的功能：

**1. ffmpeg - 音视频转换工具**

```bash
# 基本语法
ffmpeg [全局选项] [输入选项] -i [输入文件] [输出选项] [输出文件]

# 常用全局选项
-y              # 覆盖输出文件
-n              # 不覆盖输出文件
-loglevel level # 设置日志级别
-v level        # 同上
-hide_banner    # 隐藏版本信息
```

**常用 ffmpeg 命令示例：**

```bash
# 1. 视频格式转换
ffmpeg -i input.avi -c:v libx264 -c:a aac output.mp4

# 2. 提取音频
ffmpeg -i input.mp4 -vn -acodec copy output.aac

# 3. 提取视频
ffmpeg -i input.mp4 -an -vcodec copy output.mp4

# 4. 视频截图
ffmpeg -i input.mp4 -ss 00:01:00 -vframes 1 screenshot.png

# 5. 视频裁剪
ffmpeg -i input.mp4 -ss 00:01:00 -t 00:00:30 -c copy output.mp4

# 6. 视频缩放
ffmpeg -i input.mp4 -vf scale=1280:720 output.mp4

# 7. 添加水印
ffmpeg -i input.mp4 -i watermark.png -filter_complex "overlay=10:10" output.mp4

# 8. 合并视频
ffmpeg -f concat -i filelist.txt -c copy output.mp4

# 9. 推流
ffmpeg -re -i input.mp4 -c copy -f flv rtmp://server/live/stream

# 10. 录屏
ffmpeg -f gdigrab -i desktop -r 30 -vcodec libx264 output.mp4
```

```java
// FFmpeg 命令执行器
public class FFmpegExecutor {
    
    public static class ExecutionResult {
        public int exitCode;
        public String output;
        public String error;
        public long executionTime;
        public boolean success;
    }
    
    public static ExecutionResult executeCommand(String... command) {
        ExecutionResult result = new ExecutionResult();
        long startTime = System.currentTimeMillis();
        
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // 读取输出
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            result.output = output.toString();
            result.exitCode = process.waitFor();
            result.success = result.exitCode == 0;
            
        } catch (IOException | InterruptedException e) {
            result.error = e.getMessage();
            result.success = false;
        } finally {
            result.executionTime = System.currentTimeMillis() - startTime;
        }
        
        return result;
    }
    
    // 视频转码
    public static boolean convertVideo(String input, String output, String videoCodec, String audioCodec) {
        ExecutionResult result = executeCommand(
            "ffmpeg", "-i", input, 
            "-c:v", videoCodec, 
            "-c:a", audioCodec, 
            "-y", output
        );
        
        return result.success;
    }
    
    // 视频截图
    public static boolean captureFrame(String input, String output, String time) {
        ExecutionResult result = executeCommand(
            "ffmpeg", "-i", input,
            "-ss", time,
            "-vframes", "1",
            "-y", output
        );
        
        return result.success;
    }
    
    // 获取视频信息
    public static String getVideoInfo(String input) {
        ExecutionResult result = executeCommand(
            "ffprobe", "-v", "quiet",
            "-print_format", "json",
            "-show_format",
            "-show_streams",
            input
        );
        
        return result.success ? result.output : null;
    }
    
    // 推流
    public static boolean streamVideo(String input, String rtmpUrl) {
        ExecutionResult result = executeCommand(
            "ffmpeg", "-re", "-i", input,
            "-c", "copy",
            "-f", "flv",
            rtmpUrl
        );
        
        return result.success;
    }
    
    // 监控执行进度
    public static void executeWithProgress(String input, String output, ProgressCallback callback) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", input,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-y", output
            );
            
            Process process = pb.start();
            
            // 读取进度信息
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("time=")) {
                    // 解析进度信息
                    String timeStr = line.substring(line.indexOf("time=") + 5, 
                                                   line.indexOf("bitrate=")).trim();
                    
                    if (callback != null) {
                        callback.onProgressUpdate(timeStr);
                    }
                }
            }
            
            int exitCode = process.waitFor();
            if (callback != null) {
                callback.onExecutionComplete(exitCode == 0);
            }
            
        } catch (IOException | InterruptedException e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }
    
    public interface ProgressCallback {
        void onProgressUpdate(String currentTime);
        void onExecutionComplete(boolean success);
        void onError(Exception e);
    }
}
```

**2. ffprobe - 媒体信息分析工具**

```bash
# 基本用法
ffprobe [选项] [输入文件]

# 常用选项
-v level          # 日志级别
-print_format fmt # 输出格式: default, json, xml, csv
-show_format      # 显示格式信息
-show_streams     # 显示流信息
-show_frames      # 显示帧信息
-show_packets     # 显示包信息
-select_streams s # 选择特定流
-count_frames     # 计算帧数
-count_packets    # 计算包数
```

**ffprobe 使用示例：**

```bash
# 1. 显示基本信息
ffprobe -v quiet -show_format input.mp4

# 2. 显示流信息
ffprobe -v quiet -show_streams input.mp4

# 3. JSON 格式输出
ffprobe -v quiet -print_format json -show_format -show_streams input.mp4

# 4. 获取视频分辨率
ffprobe -v quiet -select_streams v:0 -show_entries stream=width,height input.mp4

# 5. 获取音频采样率
ffprobe -v quiet -select_streams a:0 -show_entries stream=sample_rate input.mp4

# 6. 计算视频帧数
ffprobe -v quiet -select_streams v:0 -count_frames -show_entries stream=nb_frames input.mp4

# 7. 获取视频时长
ffprobe -v quiet -show_entries format=duration input.mp4

# 8. 显示章节信息
ffprobe -v quiet -show_chapters input.mp4

# 9. 显示像素格式
ffprobe -v quiet -select_streams v:0 -show_entries stream=pix_fmt input.mp4

# 10. 分析视频比特率
ffprobe -v quiet -show_entries format=bit_rate input.mp4
```

```java
// FFprobe 信息解析器
public class FFprobeAnalyzer {
    
    public static class MediaInfo {
        public Map<String, String> format;
        public List<StreamInfo> streams;
        public List<ChapterInfo> chapters;
        public PacketInfo packetInfo;
        public FrameInfo frameInfo;
    }
    
    public static class StreamInfo {
        public int index;
        public String codecName;
        public String codecType;
        public int width;
        public int height;
        public int sampleRate;
        public int channels;
        public String pixelFormat;
        public double duration;
        public long bitrate;
        public String language;
    }
    
    public static class ChapterInfo {
        public int id;
        public long startTime;
        public long endTime;
        public String title;
    }
    
    // 解析 JSON 格式的媒体信息
    public static MediaInfo parseMediaInfo(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            MediaInfo info = new MediaInfo();
            
            // 解析格式信息
            if (json.has("format")) {
                JSONObject format = json.getJSONObject("format");
                info.format = parseMap(format);
            }
            
            // 解析流信息
            if (json.has("streams")) {
                JSONArray streams = json.getJSONArray("streams");
                info.streams = new ArrayList<>();
                
                for (int i = 0; i < streams.length(); i++) {
                    JSONObject stream = streams.getJSONObject(i);
                    info.streams.add(parseStreamInfo(stream));
                }
            }
            
            // 解析章节信息
            if (json.has("chapters")) {
                JSONArray chapters = json.getJSONArray("chapters");
                info.chapters = new ArrayList<>();
                
                for (int i = 0; i < chapters.length(); i++) {
                    JSONObject chapter = chapters.getJSONObject(i);
                    info.chapters.add(parseChapterInfo(chapter));
                }
            }
            
            return info;
            
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static StreamInfo parseStreamInfo(JSONObject stream) {
        StreamInfo info = new StreamInfo();
        
        info.index = stream.optInt("index", -1);
        info.codecName = stream.optString("codec_name", "");
        info.codecType = stream.optString("codec_type", "");
        info.width = stream.optInt("width", 0);
        info.height = stream.optInt("height", 0);
        info.sampleRate = stream.optInt("sample_rate", 0);
        info.channels = stream.optInt("channels", 0);
        info.pixelFormat = stream.optString("pix_fmt", "");
        info.duration = stream.optDouble("duration", 0);
        info.bitrate = stream.optLong("bit_rate", 0);
        info.language = stream.optString("tags", "").contains("language") ? 
                       stream.getJSONObject("tags").optString("language", "") : "";
        
        return info;
    }
    
    private static ChapterInfo parseChapterInfo(JSONObject chapter) {
        ChapterInfo info = new ChapterInfo();
        
        info.id = chapter.optInt("id", -1);
        info.startTime = chapter.optLong("start", 0);
        info.endTime = chapter.optLong("end", 0);
        
        if (chapter.has("tags")) {
            JSONObject tags = chapter.getJSONObject("tags");
            info.title = tags.optString("title", "");
        }
        
        return info;
    }
    
    private static Map<String, String> parseMap(JSONObject json) {
        Map<String, String> map = new HashMap<>();
        
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, json.optString(key, ""));
        }
        
        return map;
    }
    
    // 获取视频基本信息
    public static MediaInfo getVideoInfo(String filePath) {
        try {
            String jsonStr = FFmpegExecutor.getVideoInfo(filePath);
            return parseMediaInfo(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 格式化输出媒体信息
    public static void formatMediaInfo(MediaInfo info) {
        if (info == null) return;
        
        System.out.println("=== 媒体文件信息 ===");
        
        // 格式信息
        if (info.format != null) {
            System.out.println("\n格式信息:");
            for (Map.Entry<String, String> entry : info.format.entrySet()) {
                System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
            }
        }
        
        // 流信息
        if (info.streams != null) {
            System.out.println("\n流信息:");
            for (StreamInfo stream : info.streams) {
                System.out.printf("  流 %d - %s:%n", stream.index, stream.codecType);
                System.out.printf("    编解码器: %s%n", stream.codecName);
                
                if ("video".equals(stream.codecType)) {
                    System.out.printf("    分辨率: %dx%d%n", stream.width, stream.height);
                    System.out.printf("    像素格式: %s%n", stream.pixelFormat);
                } else if ("audio".equals(stream.codecType)) {
                    System.out.printf("    采样率: %d Hz%n", stream.sampleRate);
                    System.out.printf("    声道数: %d%n", stream.channels);
                }
                
                System.out.printf("    时长: %.2f 秒%n", stream.duration);
                System.out.printf("    比特率: %d bps%n", stream.bitrate);
                
                if (!stream.language.isEmpty()) {
                    System.out.printf("    语言: %s%n", stream.language);
                }
                System.out.println();
            }
        }
        
        // 章节信息
        if (info.chapters != null && !info.chapters.isEmpty()) {
            System.out.println("章节信息:");
            for (ChapterInfo chapter : info.chapters) {
                System.out.printf("  章节 %d: %s%n", chapter.id, chapter.title);
                System.out.printf("    开始时间: %d%n", chapter.startTime);
                System.out.printf("    结束时间: %d%n", chapter.endTime);
            }
        }
    }
}
```

**3. ffplay - 音视频播放器**

```bash
# 基本用法
ffplay [选项] [输入文件]

# 常用选项
-x width         # 设置窗口宽度
-y height        # 设置窗口高度
-fs              # 全屏播放
-an              # 不播放音频
-vn              # 不播放视频
-ss pos          # 从指定位置开始播放
-t duration      # 播放指定时长
-loop number     # 循环播放次数
-window_title title # 设置窗口标题
```

**ffplay 使用示例：**

```bash
# 1. 播放视频文件
ffplay input.mp4

# 2. 全屏播放
ffplay -fs input.mp4

# 3. 从30秒处开始播放
ffplay -ss 00:00:30 input.mp4

# 4. 播放10秒钟
ffplay -t 00:00:10 input.mp4

# 5. 只播放音频
ffplay -vn input.mp4

# 6. 只播放视频
ffplay -an input.mp4

# 7. 循环播放3次
ffplay -loop 3 input.mp4

# 8. 设置窗口大小
ffplay -x 800 -y 600 input.mp4

# 9. 播放网络流
ffplay rtmp://server/live/stream

# 10. 播放摄像头
ffplay -f dshow -i video="Integrated Camera"
```

```java
// FFplay 播放器封装
public class FFplayPlayer {
    
    public static class PlayOptions {
        public String inputFile;
        public int windowWidth = -1;
        public int windowHeight = -1;
        public boolean fullScreen = false;
        public boolean noAudio = false;
        public boolean noVideo = false;
        public String startTime = null;
        public String duration = null;
        public int loopCount = 1;
        public String windowTitle = null;
        public double volume = 1.0;
    }
    
    private Process playProcess;
    private boolean isPlaying = false;
    
    // 播放媒体文件
    public boolean play(PlayOptions options) {
        if (isPlaying) {
            stop();
        }
        
        try {
            List<String> command = buildPlayCommand(options);
            ProcessBuilder pb = new ProcessBuilder(command);
            
            playProcess = pb.start();
            isPlaying = true;
            
            // 启动监控线程
            startMonitoringThread();
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private List<String> buildPlayCommand(PlayOptions options) {
        List<String> command = new ArrayList<>();
        command.add("ffplay");
        
        // 添加选项
        if (options.windowWidth > 0) {
            command.add("-x");
            command.add(String.valueOf(options.windowWidth));
        }
        
        if (options.windowHeight > 0) {
            command.add("-y");
            command.add(String.valueOf(options.windowHeight));
        }
        
        if (options.fullScreen) {
            command.add("-fs");
        }
        
        if (options.noAudio) {
            command.add("-an");
        }
        
        if (options.noVideo) {
            command.add("-vn");
        }
        
        if (options.startTime != null) {
            command.add("-ss");
            command.add(options.startTime);
        }
        
        if (options.duration != null) {
            command.add("-t");
            command.add(options.duration);
        }
        
        if (options.loopCount > 1) {
            command.add("-loop");
            command.add(String.valueOf(options.loopCount));
        }
        
        if (options.windowTitle != null) {
            command.add("-window_title");
            command.add(options.windowTitle);
        }
        
        if (options.volume != 1.0) {
            command.add("-volume");
            command.add(String.valueOf(options.volume));
        }
        
        // 添加输入文件
        command.add(options.inputFile);
        
        return command;
    }
    
    // 停止播放
    public void stop() {
        if (playProcess != null && playProcess.isAlive()) {
            playProcess.destroyForcibly();
            try {
                playProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isPlaying = false;
    }
    
    // 暂停播放
    public void pause() {
        if (playProcess != null && playProcess.isAlive()) {
            // Windows 下暂停进程
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("taskkill /PID " + playProcess.pid() + " /T");
                } else {
                    Runtime.getRuntime().exec("kill -STOP " + playProcess.pid());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 恢复播放
    public void resume() {
        if (playProcess != null && playProcess.isAlive()) {
            try {
                if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("kill -CONT " + playProcess.pid());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void startMonitoringThread() {
        Thread monitorThread = new Thread(() -> {
            try {
                int exitCode = playProcess.waitFor();
                isPlaying = false;
                
                if (exitCode != 0) {
                    System.err.println("播放异常结束，退出码: " + exitCode);
                }
            } catch (InterruptedException e) {
                isPlaying = false;
            }
        });
        
        monitorThread.setDaemon(true);
        monitorThread.start();
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    // 简化的播放方法
    public boolean playSimple(String inputFile) {
        PlayOptions options = new PlayOptions();
        options.inputFile = inputFile;
        return play(options);
    }
    
    // 全屏播放
    public boolean playFullScreen(String inputFile) {
        PlayOptions options = new PlayOptions();
        options.inputFile = inputFile;
        options.fullScreen = true;
        return play(options);
    }
    
    // 播放指定时长
    public boolean playWithDuration(String inputFile, String duration) {
        PlayOptions options = new PlayOptions();
        options.inputFile = inputFile;
        options.duration = duration;
        return play(options);
    }
    
    // 从指定位置播放
    public boolean playFromTime(String inputFile, String startTime) {
        PlayOptions options = new PlayOptions();
        options.inputFile = inputFile;
        options.startTime = startTime;
        return play(options);
    }
}
```

### 1.4.2 动态链接库

FFmpeg 提供了丰富的动态链接库，供开发者在其应用程序中集成 FFmpeg 功能：

**核心库介绍**

**1. libavformat - 封装格式处理库**

```c
// libavformat 主要功能
// 1. 封装格式注册
void av_register_all(void);

// 2. 打开输入文件
int avformat_open_input(AVFormatContext **ps, const char *url, 
                        AVInputFormat *fmt, AVDictionary **options);

// 3. 查找流信息
int avformat_find_stream_info(AVFormatContext *ic, AVDictionary **options);

// 4. 读取数据包
int av_read_frame(AVFormatContext *s, AVPacket *pkt);

// 5. 写入文件头
int avformat_write_header(AVFormatContext *s, AVDictionary **options);

// 6. 写入数据包
int av_interleaved_write_frame(AVFormatContext *s, AVPacket *pkt);

// 7. 写入文件尾
int av_write_trailer(AVFormatContext *s);
```

**2. libavcodec - 编解码库**

```c
// libavcodec 主要功能
// 1. 查找编码器
AVCodec *avcodec_find_encoder(enum AVCodecID id);

// 2. 查找解码器
AVCodec *avcodec_find_decoder(enum AVCodecID id);

// 3. 分配编码器上下文
AVCodecContext *avcodec_alloc_context3(const AVCodec *codec);

// 4. 打开编码器
int avcodec_open2(AVCodecContext *avctx, const AVCodec *codec, 
                   AVDictionary **options);

// 5. 编码
int avcodec_encode_video2(AVCodecContext *avctx, AVPacket *avpkt, 
                          const AVFrame *frame, int *got_packet_ptr);

// 6. 解码
int avcodec_decode_video2(AVCodecContext *avctx, AVFrame *picture, 
                          int *got_picture_ptr, const AVPacket *avpkt);
```

**3. libavutil - 工具库**

```c
// libavutil 主要功能
// 1. 内存管理
void *av_malloc(size_t size);
void av_free(void *ptr);

// 2. 时间戳处理
int64_t av_rescale_q(int64_t a, AVRational bq, AVRational cq);

// 3. 错误处理
char *av_strerror(int errnum, char *errbuf, size_t errbuf_size);

// 4. 日志输出
void av_log(void *avcl, int level, const char *fmt, ...);

// 5. 数学运算
int64_t av_rescale(int64_t a, int64_t b, int64_t c);
```

**4. libswscale - 图像缩放和格式转换库**

```c
// libswscale 主要功能
// 1. 分配转换上下文
struct SwsContext *sws_getContext(int srcW, int srcH, enum AVPixelFormat srcFormat,
                                  int dstW, int dstH, enum AVPixelFormat dstFormat,
                                  int flags, SwsFilter *srcFilter, 
                                  SwsFilter *dstFilter, const double *param);

// 2. 执行图像转换
int sws_scale(struct SwsContext *c, const uint8_t *const srcSlice[],
              const int srcStride[], int srcSliceY, int srcSliceH,
              uint8_t *const dst[], const int dstStride[]);

// 3. 释放转换上下文
void sws_freeContext(struct SwsContext *swsContext);
```

**Java JNI 封装示例**

```java
// FFmpeg 库 JNI 封装基类
public class FFmpegLibrary {
    static {
        // 加载 FFmpeg 动态库
        try {
            System.loadLibrary("avutil");
            System.loadLibrary("swresample");
            System.loadLibrary("swscale");
            System.loadLibrary("avcodec");
            System.loadLibrary("avformat");
            System.loadLibrary("avfilter");
            System.loadLibrary("avdevice");
            System.loadLibrary("ffmpeg-wrapper");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("加载 FFmpeg 库失败: " + e.getMessage());
        }
    }
    
    // 注册所有格式和编解码器
    public static native void av_register_all();
    
    // 初始化网络
    public static native void avformat_network_init();
    
    // 获取错误信息
    public static native String av_strerror(int errorCode);
    
    // 打开输入文件
    public static native long avformat_open_input(String filename, String format);
    
    // 关闭输入文件
    public static native void avformat_close_input(long formatContext);
    
    // 查找流信息
    public static native int avformat_find_stream_info(long formatContext);
    
    // 读取数据包
    public static native int av_read_frame(long formatContext, long packet);
    
    // 分配数据包
    public static native long av_packet_alloc();
    
    // 释放数据包
    public static native void av_packet_free(long packet);
    
    // 查找解码器
    public static native long avcodec_find_decoder(int codecId);
    
    // 分配解码器上下文
    public static native long avcodec_alloc_context3(long codec);
    
    // 打开解码器
    public static native int avcodec_open2(long codecContext, long codec);
    
    // 分配帧
    public static native long av_frame_alloc();
    
    // 释放帧
    public static native void av_frame_free(long frame);
    
    // 解码视频
    public static native int avcodec_decode_video2(long codecContext, long frame, long packet);
    
    // 缩放图像
    public static native long sws_getContext(int srcW, int srcH, int srcFormat,
                                             int dstW, int dstH, int dstFormat);
    
    // 执行缩放
    public static native int sws_scale(long swsContext, long srcFrame, long dstFrame);
}
```

**库初始化和清理**

```java
// FFmpeg 库管理器
public class FFmpegLibraryManager {
    private static boolean initialized = false;
    private static final Object lock = new Object();
    
    // 初始化 FFmpeg 库
    public static boolean initialize() {
        synchronized (lock) {
            if (initialized) {
                return true;
            }
            
            try {
                // 注册所有格式和编解码器
                FFmpegLibrary.av_register_all();
                
                // 初始化网络功能
                FFmpegLibrary.avformat_network_init();
                
                // 设置日志级别
                setLogLevel(AV_LOG_INFO);
                
                initialized = true;
                System.out.println("FFmpeg 库初始化成功");
                
                return true;
                
            } catch (Exception e) {
                System.err.println("FFmpeg 库初始化失败: " + e.getMessage());
                return false;
            }
        }
    }
    
    // 清理 FFmpeg 库
    public static void cleanup() {
        synchronized (lock) {
            if (initialized) {
                try {
                    // 这里可以添加清理代码
                    initialized = false;
                    System.out.println("FFmpeg 库清理完成");
                } catch (Exception e) {
                    System.err.println("FFmpeg 库清理失败: " + e.getMessage());
                }
            }
        }
    }
    
    // 检查是否已初始化
    public static boolean isInitialized() {
        return initialized;
    }
    
    // 设置日志级别
    public static void setLogLevel(int level) {
        // 可以通过 JNI 调用 av_log_set_level
    }
    
    // 日志级别常量
    public static final int AV_LOG_QUIET = -8;
    public static final int AV_LOG_PANIC = 0;
    public static final int AV_LOG_FATAL = 8;
    public static final int AV_LOG_ERROR = 16;
    public static final int AV_LOG_WARNING = 24;
    public static final int AV_LOG_INFO = 32;
    public static final int AV_LOG_VERBOSE = 40;
    public static final int AV_LOG_DEBUG = 48;
    public static final int AV_LOG_TRACE = 56;
    
    // 注册关闭钩子
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cleanup();
        }));
    }
}
```

### 1.4.3 第一个FFmpeg程序

创建一个简单的 FFmpeg 程序，实现基本的视频信息读取功能：

**C 语言示例程序**

```c
#include <stdio.h>
#include <libavformat/avformat.h>

// 打印视频信息
void print_video_info(const char* filename) {
    AVFormatContext *fmt_ctx = NULL;
    int video_stream_index = -1;
    int audio_stream_index = -1;
    int ret;
    
    // 打开输入文件
    if ((ret = avformat_open_input(&fmt_ctx, filename, NULL, NULL)) < 0) {
        printf("无法打开文件: %s\n", av_err2str(ret));
        return;
    }
    
    // 查找流信息
    if ((ret = avformat_find_stream_info(fmt_ctx, NULL)) < 0) {
        printf("无法查找流信息: %s\n", av_err2str(ret));
        goto end;
    }
    
    // 打印文件信息
    printf("=== 文件信息 ===\n");
    printf("文件名: %s\n", filename);
    printf("格式: %s\n", fmt_ctx->iformat->long_name);
    printf("时长: %.2f 秒\n", (double)fmt_ctx->duration / AV_TIME_BASE);
    printf("比特率: %ld bps\n", fmt_ctx->bit_rate);
    printf("流数量: %d\n\n", fmt_ctx->nb_streams);
    
    // 查找视频和音频流
    for (int i = 0; i < fmt_ctx->nb_streams; i++) {
        AVCodecParameters *codecpar = fmt_ctx->streams[i]->codecpar;
        
        if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO && video_stream_index == -1) {
            video_stream_index = i;
            printf("=== 视频流 ===\n");
            printf("索引: %d\n", i);
            printf("编码器: %s\n", avcodec_get_name(codecpar->codec_id));
            printf("分辨率: %dx%d\n", codecpar->width, codecpar->height);
            printf("像素格式: %s\n", av_get_pix_fmt_name(codecpar->format));
            printf("帧率: %.2f fps\n", 
                   av_q2d(fmt_ctx->streams[i]->r_frame_rate));
            printf("视频比特率: %ld bps\n", codecpar->bit_rate);
            printf("\n");
            
        } else if (codecpar->codec_type == AVMEDIA_TYPE_AUDIO && audio_stream_index == -1) {
            audio_stream_index = i;
            printf("=== 音频流 ===\n");
            printf("索引: %d\n", i);
            printf("编码器: %s\n", avcodec_get_name(codecpar->codec_id));
            printf("采样率: %d Hz\n", codecpar->sample_rate);
            printf("声道数: %d\n", codecpar->channels);
            printf("采样格式: %s\n", av_get_sample_fmt_name(codecpar->format));
            printf("音频比特率: %ld bps\n", codecpar->bit_rate);
            printf("\n");
        }
    }
    
    if (video_stream_index == -1) {
        printf("未找到视频流\n");
    }
    if (audio_stream_index == -1) {
        printf("未找到音频流\n");
    }

end:
    avformat_close_input(&fmt_ctx);
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("用法: %s <视频文件>\n", argv[0]);
        return -1;
    }
    
    // 注册所有格式和编解码器
    av_register_all();
    
    // 打印视频信息
    print_video_info(argv[1]);
    
    return 0;
}
```

**编译脚本**

```bash
#!/bin/bash
# FFmpeg C 程序编译脚本

# 编译参数
CC=gcc
CFLAGS="-Wall -O2"
INCLUDES="-I/usr/local/ffmpeg/include"
LIBS="-L/usr/local/ffmpeg/lib -lavformat -lavcodec -lavutil -lswscale -lswresample"
OUTPUT=video_info
SOURCE=video_info.c

# 编译
echo "编译中..."
$CC $CFLAGS $INCLUDES $SOURCE $LIBS -o $OUTPUT

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo "可执行文件: $OUTPUT"
    echo "使用方法: ./$OUTPUT <视频文件>"
else
    echo "编译失败！"
fi
```

**Java 封装版本**

```java
// FFmpeg 视频信息读取器
public class FFmpegVideoInfoReader {
    
    static {
        // 初始化 FFmpeg 库
        FFmpegLibraryManager.initialize();
    }
    
    public static class VideoInfo {
        public String filename;
        public String format;
        public double duration;
        public long bitrate;
        public int streamCount;
        public VideoStreamInfo videoStream;
        public AudioStreamInfo audioStream;
    }
    
    public static class VideoStreamInfo {
        public int index;
        public String codec;
        public int width;
        public int height;
        public String pixelFormat;
        public double frameRate;
        public long bitrate;
    }
    
    public static class AudioStreamInfo {
        public int index;
        public String codec;
        public int sampleRate;
        public int channels;
        public String sampleFormat;
        public long bitrate;
    }
    
    // 读取视频信息
    public static VideoInfo readVideoInfo(String filename) {
        VideoInfo info = new VideoInfo();
        info.filename = filename;
        
        long formatContext = 0;
        try {
            // 打开输入文件
            formatContext = FFmpegLibrary.avformat_open_input(filename, null);
            if (formatContext == 0) {
                System.err.println("无法打开文件: " + filename);
                return null;
            }
            
            // 查找流信息
            int ret = FFmpegLibrary.avformat_find_stream_info(formatContext);
            if (ret < 0) {
                System.err.println("无法查找流信息: " + FFmpegLibrary.av_strerror(ret));
                return null;
            }
            
            // 获取格式信息
            info.format = FFmpegLibrary.getFormatName(formatContext);
            info.duration = FFmpegLibrary.getDuration(formatContext) / 1000000.0;
            info.bitrate = FFmpegLibrary.getBitrate(formatContext);
            info.streamCount = FFmpegLibrary.getStreamCount(formatContext);
            
            // 查找视频流
            info.videoStream = findVideoStream(formatContext);
            
            // 查找音频流
            info.audioStream = findAudioStream(formatContext);
            
            return info;
            
        } catch (Exception e) {
            System.err.println("读取视频信息失败: " + e.getMessage());
            return null;
        } finally {
            if (formatContext != 0) {
                FFmpegLibrary.avformat_close_input(formatContext);
            }
        }
    }
    
    private static VideoStreamInfo findVideoStream(long formatContext) {
        int streamCount = FFmpegLibrary.getStreamCount(formatContext);
        
        for (int i = 0; i < streamCount; i++) {
            long stream = FFmpegLibrary.getStream(formatContext, i);
            long codecpar = FFmpegLibrary.getStreamCodecpar(stream);
            
            int codecType = FFmpegLibrary.getCodecType(codecpar);
            if (codecType == 0) { // AVMEDIA_TYPE_VIDEO
                VideoStreamInfo videoInfo = new VideoStreamInfo();
                videoInfo.index = i;
                videoInfo.codec = FFmpegLibrary.getCodecName(codecpar);
                videoInfo.width = FFmpegLibrary.getWidth(codecpar);
                videoInfo.height = FFmpegNative.getHeight(codecpar);
                videoInfo.pixelFormat = FFmpegNative.getPixelFormat(codecpar);
                videoInfo.bitrate = FFmpegLibrary.getBitrate(codecpar);
                
                // 获取帧率
                AVRational frameRate = FFmpegLibrary.getFrameRate(stream);
                videoInfo.frameRate = (double)frameRate.num / frameRate.den;
                
                return videoInfo;
            }
        }
        
        return null;
    }
    
    private static AudioStreamInfo findAudioStream(long formatContext) {
        int streamCount = FFmpegLibrary.getStreamCount(formatContext);
        
        for (int i = 0; i < streamCount; i++) {
            long stream = FFmpegLibrary.getStream(formatContext, i);
            long codecpar = FFmpegLibrary.getStreamCodecpar(stream);
            
            int codecType = FFmpegLibrary.getCodecType(codecpar);
            if (codecType == 1) { // AVMEDIA_TYPE_AUDIO
                AudioStreamInfo audioInfo = new AudioStreamInfo();
                audioInfo.index = i;
                audioInfo.codec = FFmpegLibrary.getCodecName(codecpar);
                audioInfo.sampleRate = FFmpegLibrary.getSampleRate(codecpar);
                audioInfo.channels = FFmpegLibrary.getChannels(codecpar);
                audioInfo.sampleFormat = FFmpegLibrary.getSampleFormat(codecpar);
                audioInfo.bitrate = FFmpegLibrary.getBitrate(codecpar);
                
                return audioInfo;
            }
        }
        
        return null;
    }
    
    // 格式化输出视频信息
    public static void printVideoInfo(VideoInfo info) {
        if (info == null) return;
        
        System.out.println("=== 文件信息 ===");
        System.out.println("文件名: " + info.filename);
        System.out.println("格式: " + info.format);
        System.out.printf("时长: %.2f 秒%n", info.duration);
        System.out.printf("比特率: %d bps%n", info.bitrate);
        System.out.println("流数量: " + info.streamCount);
        System.out.println();
        
        if (info.videoStream != null) {
            System.out.println("=== 视频流 ===");
            System.out.println("索引: " + info.videoStream.index);
            System.out.println("编码器: " + info.videoStream.codec);
            System.out.printf("分辨率: %dx%d%n", info.videoStream.width, info.videoStream.height);
            System.out.println("像素格式: " + info.videoStream.pixelFormat);
            System.out.printf("帧率: %.2f fps%n", info.videoStream.frameRate);
            System.out.printf("视频比特率: %d bps%n", info.videoStream.bitrate);
            System.out.println();
        } else {
            System.out.println("未找到视频流\n");
        }
        
        if (info.audioStream != null) {
            System.out.println("=== 音频流 ===");
            System.out.println("索引: " + info.audioStream.index);
            System.out.println("编码器: " + info.audioStream.codec);
            System.out.printf("采样率: %d Hz%n", info.audioStream.sampleRate);
            System.out.println("声道数: " + info.audioStream.channels);
            System.out.println("采样格式: " + info.audioStream.sampleFormat);
            System.out.printf("音频比特率: %d bps%n", info.audioStream.bitrate);
            System.out.println();
        } else {
            System.out.println("未找到音频流\n");
        }
    }
    
    // 主程序
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("用法: java FFmpegVideoInfoReader <视频文件>");
            System.exit(1);
        }
        
        String filename = args[0];
        VideoInfo info = readVideoInfo(filename);
        
        if (info != null) {
            printVideoInfo(info);
        } else {
            System.err.println("无法读取视频信息");
            System.exit(1);
        }
    }
}
```

**完整的使用示例**

```java
// FFmpeg 使用示例程序
public class FFmpegFirstProgram {
    
    public static void main(String[] args) {
        System.out.println("=== 第一个 FFmpeg 程序 ===\n");
        
        // 检查命令行参数
        if (args.length != 1) {
            System.out.println("用法: java FFmpegFirstProgram <视频文件>");
            System.out.println("示例: java FFmpegFirstProgram sample.mp4");
            return;
        }
        
        String videoFile = args[0];
        System.out.println("分析文件: " + videoFile);
        System.out.println();
        
        // 检查文件是否存在
        if (!new File(videoFile).exists()) {
            System.err.println("错误: 文件不存在");
            return;
        }
        
        try {
            // 1. 初始化 FFmpeg 库
            System.out.println("1. 初始化 FFmpeg 库...");
            if (!FFmpegLibraryManager.initialize()) {
                System.err.println("FFmpeg 库初始化失败");
                return;
            }
            System.out.println("   ✓ FFmpeg 库初始化成功\n");
            
            // 2. 读取视频信息
            System.out.println("2. 读取视频信息...");
            FFmpegVideoInfoReader.VideoInfo info = 
                FFmpegVideoInfoReader.readVideoInfo(videoFile);
            
            if (info == null) {
                System.err.println("读取视频信息失败");
                return;
            }
            
            // 3. 显示信息
            System.out.println("   ✓ 视频信息读取成功\n");
            FFmpegVideoInfoReader.printVideoInfo(info);
            
            // 4. 执行一些基本操作
            System.out.println("3. 执行基本操作...");
            
            // 获取视频时长
            System.out.printf("   - 视频时长: %.2f 秒%n", info.duration);
            
            // 检查编解码器支持
            if (info.videoStream != null) {
                System.out.printf("   - 视频编码器: %s%n", info.videoStream.codec);
            }
            
            if (info.audioStream != null) {
                System.out.printf("   - 音频编码器: %s%n", info.audioStream.codec);
            }
            
            System.out.println("   ✓ 基本操作完成\n");
            
            // 5. 示例：获取视频截图
            System.out.println("4. 生成视频截图...");
            String screenshotFile = "screenshot_" + System.currentTimeMillis() + ".png";
            
            if (FFmpegExecutor.captureFrame(videoFile, screenshotFile, "00:00:05")) {
                System.out.println("   ✓ 截图已保存: " + screenshotFile);
            } else {
                System.out.println("   ✗ 截图生成失败");
            }
            
            System.out.println("\n=== 程序执行完成 ===");
            
        } catch (Exception e) {
            System.err.println("程序执行出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理资源
            FFmpegLibraryManager.cleanup();
        }
    }
}
```

## 1.5 小结

本章详细介绍了 FFmpeg 环境搭建的各个方面，为后续的开发工作奠定了坚实的基础。

### 主要学习内容

1. **FFmpeg 基础知识**
   - 了解了 FFmpeg 的基本概念和发展历程
   - 掌握了 FFmpeg 的主要应用场景
   - 认识了 FFmpeg 的核心特性

2. **Linux 环境安装**
   - 学习了 Linux 系统下的配置要求
   - 掌握了预编译版本和源码编译的安装方法
   - 学会了编译环境的准备和配置

3. **Windows 环境安装**
   - 了解了 Windows 开发环境的配置要求
   - 掌握了依赖软件的安装方法
   - 学会了 DLL 库的管理和配置

4. **FFmpeg 开发框架**
   - 学习了 ffmpeg、ffprobe、ffplay 三个可执行程序
   - 掌握了 FFmpeg 核心动态链接库的使用
   - 编写了第一个 FFmpeg 程序

### 实践要点

- **环境检查**：在安装前务必检查系统环境和依赖
- **版本选择**：根据项目需求选择合适的 FFmpeg 版本
- **路径配置**：正确设置环境变量和库文件路径
- **错误处理**：学会分析安装和编译过程中的错误信息
- **资源管理**：及时释放 FFmpeg 分配的资源

### 常见问题解决

1. **编译错误**
   - 检查编译器和依赖库版本
   - 确认环境变量配置正确
   - 查看详细的错误日志

2. **运行时错误**
   - 检查 DLL/SO 文件是否存在
   - 确认库文件路径在系统 PATH 中
   - 验证文件权限

3. **性能问题**
   - 使用硬件加速
   - 优化编译参数
   - 选择合适的编解码器

### 下一步建议

掌握了 FFmpeg 环境搭建后，读者可以：

1. 深入学习 FFmpeg 的 API 使用
2. 探索不同编解码器的特性和参数
3. 学习音视频同步和处理技术
4. 开发实际的多媒体应用
5. 研究流媒体传输和实时处理

FFmpeg 是一个功能强大但复杂的框架，需要不断实践和深入探索才能熟练掌握。建议读者在理解本章内容的基础上，多动手实践，为后续的学习打下坚实基础。