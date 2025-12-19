# 第5章：FFmpeg处理音频

本章演示了如何使用FFmpeg处理各种音频格式，包括PCM、MP3、WAV、AAC等，以及音频重采样和拼接技术。

## 功能模块

### 1. PCMAudioProcessor - PCM音频处理器
- **功能**：处理PCM格式的音频转换和保存
- **主要方法**：
  - `extractPCMFromAudio()` - 从音频文件中提取PCM数据
  - `extractPCMFromVideo()` - 从视频中提取PCM音频
  - `analyzePCMAudio()` - 分析PCM音频数据
  - `generateSineWavePCM()` - 生成正弦波PCM文件
  - `convertPCMFormat()` - PCM格式转换
  - `applyPCMEffect()` - 添加PCM效果

### 2. MP3AudioProcessor - MP3音频处理器
- **功能**：处理MP3格式的音频转换和优化
- **主要方法**：
  - `convertToMP3()` - 将音频转换为MP3格式
  - `extractMP3FromVideo()` - 从视频中提取MP3音频
  - `convertToMP3WithBitrate()` - 设置MP3比特率
  - `convertToVBRMP3()` - 创建VBR模式的MP3
  - `convertToABRMP3()` - 创建ABR模式的MP3
  - `normalizeMP3Volume()` - MP3音量标准化
  - `concatenateMP3Files()` - 连接多个MP3文件

### 3. AudioFormatConverter - 音频格式转换器
- **功能**：支持WAV、AAC、音频重采样等格式转换
- **主要方法**：
  - `convertToWAV()` - 转换为WAV格式
  - `convertToHighQualityWAV()` - 转换为高质量WAV
  - `convertToAAC()` - 转换为AAC格式
  - `resampleAudio()` - 音频重采样
  - `highQualityResample()` - 高质量重采样
  - `convertChannels()` - 声道转换
  - `normalizeAudio()` - 音频质量标准化
  - `denoiseAudio()` - 音频降噪

### 4. AudioConcatenator - 音频拼接器
- **功能**：实战项目，拼接两段音频
- **主要方法**：
  - `concatenateAudioSimple()` - 简单音频拼接
  - `concatenateAudioWithFade()` - 带淡入淡出效果的音频拼接
  - `concatenateAudioWithCrossfade()` - 交叉淡化音频拼接
  - `concatenateAudioSeamless()` - 音频无缝拼接
  - `concatenateMultipleAudio()` - 多音频文件拼接
  - `compareAudioInfo()` - 音频信息对比

## 使用示例

### 基础用法

```java
// 1. PCM音频处理
PCMAudioProcessor.extractPCMFromAudio("input.mp3", "output.pcm", 44100, 2, "s16le");
PCMAudioProcessor.analyzePCMAudio("output.pcm", 44100, 2);
PCMAudioProcessor.generateSineWavePCM("sine.pcm", 44100, 440, 3, 0.8);

// 2. MP3音频处理
MP3AudioProcessor.convertToMP3("input.wav", "output.mp3", 0.8f);
MP3AudioProcessor.convertToMP3WithBitrate("input.wav", "output.mp3", 192);
MP3AudioProcessor.convertToVBRMP3("input.wav", "output.mp3", 2);

// 3. 音频格式转换
AudioFormatConverter.convertToWAV("input.mp3", "output.wav", 44100, 2, "s16le");
AudioFormatConverter.convertToAAC("input.wav", "output.aac", 128000, "lc");
AudioFormatConverter.resampleAudio("input.wav", "output.wav", 44100, 22050, 2, 2);

// 4. 音频拼接
AudioConcatenator.concatenateAudioSimple("audio1.mp3", "audio2.mp3", "concatenated.wav");
AudioConcatenator.concatenateAudioWithCrossfade("audio1.mp3", "audio2.mp3", "crossfade.wav", 2.0);
```

### 运行演示

```bash
# 运行完整演示
java com.ry.example.ffmpeg.chapter05.Chapter05Demo

# 交互式演示
java com.ry.example.ffmpeg.chapter05.Chapter05Demo interactive

# 性能测试
java com.ry.example.ffmpeg.chapter05.Chapter05Demo performance
```

## 依赖配置

确保项目中包含以下依赖（在pom.xml中）：

```xml
<dependencies>
    <!-- JavaCV -->
    <dependency>
        <groupId>org.bytedeco</groupId>
        <artifactId>javacv-platform</artifactId>
        <version>1.5.9</version>
    </dependency>
    
    <!-- FFmpeg -->
    <dependency>
        <groupId>org.bytedeco</groupId>
        <artifactId>ffmpeg-platform</artifactId>
        <version>6.0-1.5.9</version>
    </dependency>
</dependencies>
```

## 目录结构

```
examples/src/main/java/com/ry/example/ffmpeg/chapter05/
├── PCMAudioProcessor.java          # PCM音频处理器
├── MP3AudioProcessor.java          # MP3音频处理器
├── AudioFormatConverter.java       # 音频格式转换器
├── AudioConcatenator.java          # 音频拼接器
├── Chapter05Demo.java              # 演示类
└── README.md                       # 说明文档
```

## 输入文件要求

### 音频文件
- 支持格式：MP3, WAV, FLAC, AAC, OGG等
- 采样率：8kHz - 192kHz
- 声道数：单声道、立体声、5.1声道等

### 视频文件
- 支持格式：MP4, AVI, MOV, MKV等
- 需要包含音频流

## 输出文件

### PCM文件
- 格式：s16le, f32le, u8等
- 扩展名：.pcm
- 用途：音频分析、格式转换

### MP3文件
- 比特率：32kbps - 320kbps
- 模式：CBR, VBR, ABR
- 扩展名：.mp3

### WAV文件
- 位深度：16位, 24位, 32位
- 采样率：8kHz - 192kHz
- 扩展名：.wav

### AAC文件
- 配置：LC, HE, HEv2
- 比特率：8kbps - 529kbps
- 扩展名：.aac

## 性能优化建议

### 1. 内存管理
- 及时释放音频Frame和资源
- 批量处理时控制内存使用

### 2. 编码优化
- 根据用途选择合适的编码参数
- 使用硬件加速（如果支持）

### 3. 处理优化
- 使用多线程处理大文件
- 预先统一音频参数

## 常见问题

### Q1: PCM文件播放问题
A: 播放PCM文件需要指定正确的参数：
```bash
ffplay -f s16le -ar 44100 -ac 2 input.pcm
```

### Q2: MP3质量选择
A: 不同质量设置适合不同用途：
- 高质量（0-2）：音乐制作
- 中等质量（3-5）：一般用途
- 低质量（6-9）：语音传输

### Q3: 音频重采样质量
A: 使用高质量重采样器：
```bash
ffmpeg -i input.wav -af aresample=48000:resampler=soxr output.wav
```

## 扩展功能

### 1. 高级音频处理
- 音频均衡器
- 动态范围压缩
- 立体声扩展

### 2. 音频分析
- 频谱分析
- 音频特征提取
- 音质评估

### 3. 实时处理
- 实时音频流处理
- 低延迟音频转换
- 网络音频传输

## 实战项目说明

### 音频拼接项目
支持多种拼接方式：

1. **简单拼接**：直接连接两个音频文件
2. **淡入淡出拼接**：第一段音频淡出，第二段音频淡入
3. **交叉淡化拼接**：两段音频在过渡区域混合
4. **无缝拼接**：统一参数后进行高质量拼接

### 使用示例
```java
// 简单拼接
AudioConcatenator.concatenateAudioSimple("audio1.mp3", "audio2.mp3", "output.wav");

// 交叉淡化拼接
AudioConcatenator.concatenateAudioWithCrossfade("audio1.mp3", "audio2.mp3", "output.wav", 2.0);

// 多文件拼接
List<String> files = Arrays.asList("audio1.mp3", "audio2.mp3", "audio3.mp3");
AudioConcatenator.concatenateMultipleAudio(files, "output.wav", 
                                          AudioConcatenator.ConcatMethod.CROSSFADE, 1.5);
```

## 注意事项

1. **版权问题**：处理音频文件时注意版权
2. **质量损失**：有损压缩会降低音质
3. **格式兼容性**：不同设备对格式支持不同
4. **文件大小**：高质量音频文件较大
5. **处理时间**：高质量编码需要更多时间

## Linux环境配置

### 安装mp3lame
```bash
# Ubuntu/Debian
sudo apt-get install libmp3lame-dev

# CentOS/RHEL
sudo yum install lame-devel
```

### 安装AAC编码器
```bash
# Ubuntu/Debian
sudo apt-get install libfdk-aac-dev

# 或者编译安装
git clone https://github.com/mstorsjo/fdk-aac.git
cd fdk-aac
./autogen.sh
./configure
make
sudo make install
```

## 联系信息

如有问题或建议，请通过以下方式联系：
- 项目仓库：[GitHub链接]
- 文档：[文档链接]
- 邮箱：[邮箱地址]