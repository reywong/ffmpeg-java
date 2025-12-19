# 第8章 FFmpeg自定义滤镜 - Java代码示例

本章包含FFmpeg源码编译、自定义滤镜开发和高级滤镜应用的Java实现示例，涵盖编译配置、性能优化和实战项目。

## 📁 文件结构

```
chapter08/
├── FFmpegCustomFilter.java    # 自定义滤镜处理器
├── FFmpegCompiler.java         # FFmpeg编译配置工具
├── Chapter08Demo.java          # 综合演示类
└── README.md                   # 使用说明
```

## 🎯 主要功能

### 1. 自定义滤镜 (FFmpegCustomFilter)
- **侧边模糊滤镜**: 画面两侧渐变模糊效果，支持自定义宽度和过渡
- **模糊锐化组合**: 模拟自定义的模糊和锐化组合滤镜
- **自定义翻转**: 水平、垂直、对角线翻转效果
- **复杂滤镜链**: 多种滤镜效果的组合应用
- **滤镜支持检查**: 检查FFmpeg是否支持特定滤镜
- **性能测试**: 生成滤镜性能测试报告
- **配置管理**: 创建和管理自定义滤镜配置

### 2. 编译配置 (FFmpegCompiler)
- **Windows编译脚本**: 生成Windows环境下的FFmpeg编译脚本
- **Linux编译脚本**: 生成Linux环境下的FFmpeg编译脚本
- **x264编译脚本**: 专门用于x264编码器的编译配置
- **FFmpeg配置生成**: 自动生成configure脚本参数
- **环境检查**: 检查编译环境的完整性
- **编译报告**: 生成详细的编译步骤和时间报告

## 🚀 快速开始

### 1. 环境准备
```bash
# 检查基本工具
gcc --version
cmake --version
git --version
make --version

# 检查FFmpeg是否已安装
ffmpeg -version
ffprobe -version
```

### 2. 运行综合演示
```bash
# 编译项目
mvn compile

# 运行完整演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter08.Chapter08Demo"

# 运行自定义滤镜演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter08.FFmpegCustomFilter"

# 运行编译配置演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter08.FFmpegCompiler"
```

## 📖 使用示例

### 自定义滤镜应用
```java
// 侧边模糊效果
FFmpegCustomFilter.applySideBlurSimulated(
    "input.mp4",           // 输入视频
    "output.mp4",          // 输出视频
    100, 100,              // 左右侧模糊宽度
    50,                    // 过渡宽度
    3.0f                   // 最大模糊强度
);

// 自定义模糊锐化
FFmpegCustomFilter.applySimulatedBlurSharp(
    "input.mp4",
    "output.mp4", 
    2.0f,                  // 模糊强度
    1.5f                   // 锐化强度
);

// 自定义翻转
FFmpegCustomFilter.applyCustomFlip(
    "input.mp4",
    "output.mp4",
    true, false, false      // 水平翻转
);
```

### 复杂滤镜链组合
```java
// 构建复杂滤镜链
FFmpegCustomFilter.FilterChainConfig config = 
    new FFmpegCustomFilter.FilterChainConfig()
        .blur(1.5f)                    // 添加模糊
        .sharp(1.0f)                   // 添加锐化
        .flip(true, false)             // 水平翻转
        .color(0.1f, 1.2f, 1.1f)       // 色彩调整
        .vignette(true);               // 添加暗角

// 应用滤镜链
FFmpegCustomFilter.applyComplexFilterChain(
    "input.mp4", 
    "output.mp4", 
    config
);
```

### 编译配置生成
```java
// 生成Windows编译脚本
FFmpegCompiler.generateWindowsBuildScript("output/build/");

// 生成Linux编译脚本
FFmpegCompiler.generateLinuxBuildScript("output/build/");

// 生成x264编译脚本
FFmpegCompiler.generateX264BuildScript("output/build/");

// 检查编译环境
FFmpegCompiler.BuildEnvironment env = 
    FFmpegCompiler.checkBuildEnvironment();
FFmpegCompiler.printBuildEnvironment(env);

// 生成FFmpeg配置
FFmpegCompiler.BuildConfig config = new FFmpegCompiler.BuildConfig();
config.enableX264 = true;
config.enableShared = true;
config.enableGpl = true;

FFmpegCompiler.generateFFmpegConfig("output/build/", config);
```

### 滤镜性能测试
```java
// 生成性能测试报告
FFmpegCustomFilter.generateFilterPerformanceReport(
    "input.mp4", 
    "output/performance/"
);

// 检查特定滤镜支持
boolean supported = FFmpegCustomFilter.checkFilterSupport("gblur");

// 获取所有支持的滤镜
Map<String, String> filters = FFmpegCustomFilter.getSupportedFilters();
```

## 🎨 自定义滤镜配置

### 创建自定义滤镜配置
```java
// 创建自定义滤镜配置
FFmpegCustomFilter.CustomFilterConfig customConfig = 
    new FFmpegCustomFilter.CustomFilterConfig(
        "sideblur",           // 滤镜名称
        "video",               // 滤镜类型
        "侧边模糊滤镜"         // 描述
    );

// 添加参数
customConfig.addParameter("left_width", "100");
customConfig.addParameter("right_width", "100");
customConfig.addParameter("transition_width", "50");
customConfig.addParameter("max_blur", "3.0");

// 设置滤镜表达式
customConfig.setFilterExpression(
    "sideblur=left_width=100:right_width=100:transition_width=50:max_blur=3.0"
);

// 生成配置文件
FFmpegCustomFilter.createCustomFilterConfig(
    "custom_filter.txt", 
    customConfig
);
```

### 滤镜参数说明
```java
// 模糊相关参数
- blur_strength: 模糊强度 (0.0 - 10.0)
- blur_radius:   模糊半径 (1 - 20)
- blur_type:     模糊类型 (gaussian, box, motion)

// 锐化相关参数
- sharp_strength: 锐化强度 (0.0 - 10.0)
- sharp_radius:   锐化半径 (1 - 20)

// 侧边模糊参数
- left_width:     左侧模糊宽度 (像素)
- right_width:    右侧模糊宽度 (像素)
- transition_width: 过渡区域宽度 (像素)
- max_blur:       最大模糊强度 (0.0 - 10.0)
```

## 🔧 编译环境配置

### Windows环境
```bash
# 安装MSYS2
# 下载: https://www.msys2.org/

# 安装编译工具
pacman -Syu
pacman -Su
pacman -S mingw-w64-x86_64-gcc
pacman -S mingw-w64-x86_64-yasm
pacman -S mingw-w64-x86_64-nasm
pacman -S make
pacman -S git

# 运行编译脚本
./build_ffmpeg_windows.bat
```

### Linux环境
```bash
# 安装依赖
sudo apt update
sudo apt install build-essential cmake git yasm nasm \
    libtool pkg-config libavformat-dev libavcodec-dev

# 运行编译脚本
chmod +x build_ffmpeg_linux.sh
./build_ffmpeg_linux.sh
```

### 编译参数说明
```bash
# 基本配置
--prefix=/usr/local/ffmpeg          # 安装路径
--enable-gpl                        # 启用GPL许可
--enable-nonfree                    # 启用非自由组件
--enable-shared                      # 编译共享库
--disable-static                    # 禁用静态库

# 编码器支持
--enable-libx264                    # H.264编码器
--enable-libx265                    # H.265编码器
--enable-libmp3lame                 # MP3编码器
--enable-libavs2                    # AVS2编码器

# 滤镜支持
--enable-libfreetype                # 字体渲染
--enable-libass                      # 字幕渲染
--enable-fontconfig                 # 字体配置

# 其他功能
--enable-openssl                    # OpenSSL加密
--enable-gnutls                     # GnuTLS加密
--enable-avresample                 # 音频重采样
```

## 📊 输出结果

运行示例后，会在以下目录生成相关文件：

```
output/chapter08_demo/
├── 01_side_blur.mp4              # 侧边模糊效果
├── 02_blur_sharp.mp4             # 模糊锐化效果
├── 03_custom_flip.mp4            # 自定义翻转
├── 04_custom_rotate.mp4          # 自定义旋转
├── 05_complex_filter.mp4         # 复杂滤镜链
├── 06_artistic_effect.mp4        # 艺术效果
├── 07_restoration_effect.mp4      # 修复效果
├── 08_creative_effect.mp4        # 创意效果
├── 09_preprocessing.mp4          # 预处理效果
├── 10_stylization.mp4            # 风格化效果
├── 11_repair.mp4                 # 修复效果
├── 12_effects.mp4                # 特效效果
├── custom_filter_config.txt       # 自定义滤镜配置
├── compiler/                      # 编译配置文件
│   ├── build_ffmpeg_windows.bat   # Windows编译脚本
│   ├── build_ffmpeg_linux.sh     # Linux编译脚本
│   ├── build_x264.sh             # x264编译脚本
│   ├── ffmpeg_config.sh           # FFmpeg配置
│   └── build_report.md            # 编译报告
└── performance/                   # 性能测试结果
    └── filter_performance_report.md # 性能报告
```

## ⚠️ 注意事项

1. **编译时间**: FFmpeg完整编译需要30-60分钟，取决于硬件性能
2. **磁盘空间**: 编译过程需要5-10GB可用空间
3. **依赖安装**: 确保所有依赖库正确安装和配置
4. **权限要求**: 编译安装需要管理员权限
5. **环境变量**: 编译后需要更新PATH和LD_LIBRARY_PATH
6. **滤镜支持**: 某些自定义滤镜需要重新编译FFmpeg

## 🛠 故障排除

### 编译问题
1. **依赖缺失**: 检查所有开发包是否正确安装
2. **权限错误**: 使用sudo或管理员权限运行
3. **路径错误**: 确保安装目录有写入权限
4. **版本冲突**: 检查GCC、CMake等工具版本兼容性

### 滤镜问题
1. **滤镜不存在**: 检查FFmpeg是否支持该滤镜
2. **参数错误**: 验证滤镜参数的正确性
3. **性能问题**: 优化滤镜链和参数设置
4. **内存不足**: 减少滤镜复杂度或降低分辨率

### 调试技巧
```bash
# 检查FFmpeg编译配置
ffmpeg -version | grep configuration

# 检查滤镜支持
ffmpeg -filters | grep filter_name

# 查看滤镜详细信息
ffmpeg -h filter=filter_name

# 调试滤镜链
ffmpeg -loglevel debug -i input.mp4 -vf "filter1,filter2" output.mp4
```

## 📚 相关文档

- [第8章文档](../../../docs/chapter08/FFmpeg自定义滤镜.md)
- [FFmpeg编译指南](https://trac.ffmpeg.org/wiki/CompilationGuide)
- [FFmpeg滤镜文档](https://ffmpeg.org/ffmpeg-filters.html)
- [自定义滤镜开发](https://ffmpeg.org/developer.html#Filter-HOWTO)

## 🎯 学习目标

通过本章学习，你将掌握：
1. FFmpeg源码编译和配置方法
2. 自定义滤镜的设计和实现
3. 滤镜性能优化和测试技术
4. 复杂滤镜链的构建和调试
5. FFmpeg架构和扩展机制

## 📞 技术支持

如有问题，请参考：
1. FFmpeg官方文档和Wiki
2. 编译脚本和配置文件
3. 滤镜性能测试报告
4. 错误日志和调试信息