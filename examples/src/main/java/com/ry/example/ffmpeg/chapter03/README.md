# 第3章 FFmpeg编解码示例

## 章节概述

第3章详细介绍了FFmpeg编解码的核心概念和实践技巧，包括：

- 3.1 音视频时间处理
- 3.2 分离音视频
- 3.3 合并音视频  
- 3.4 视频浏览与格式分析

## Java示例类说明

### 1. VideoAudioTime.java
处理音视频时间相关的操作：
- 获取视频帧率和音频采样率
- 时间基准和时间戳处理
- 视频时长和帧数计算
- 帧率和采样率设置

**主要方法：**
- `getVideoFrameRate()` - 获取视频帧率
- `getAudioSampleRate()` - 获取音频采样率
- `getTimeInfo()` - 获取详细时间信息
- `setFrameRate()` - 设置视频帧率
- `setSampleRate()` - 设置音频采样率

### 2. SeparateAudioVideo.java
处理音视频分离操作：
- 原样复制视频/音频流
- 从视频提取音频为不同格式
- 视频切割和分段处理
- 流选择和批量操作

**主要方法：**
- `copyVideoStream()` - 复制视频流
- `copyAudioStream()` - 复制音频流
- `extractAudioToMp3()` - 提取为MP3
- `extractAudioToAac()` - 提取为AAC
- `extractAudioToWav()` - 提取为WAV
- `cutVideo()` - 切割视频
- `segmentVideo()` - 分段切割

### 3. MergeAudioVideo.java
处理音视频合并操作：
- 合并视频和音频流
- 视频重新编码和格式转换
- 多文件合并和转场效果
- 分辨率调整和多分辨率输出

**主要方法：**
- `mergeVideoAudio()` - 合并音视频
- `reencodeVideo()` - 重新编码视频
- `resizeVideo()` - 调整视频分辨率
- `convertFormat()` - 格式转换
- `concatVideos()` - 合并多个视频
- `mixMultipleAudio()` - 混合多个音频

### 4. VideoAnalysisPlayer.java
处理视频播放和格式分析：
- 使用ffplay播放视频
- 使用ffprobe分析格式
- H.264裸流封装为MP4
- 视频参数解析

**主要方法：**
- `playVideo()` - 播放视频
- `playAudio()` - 播放音频
- `getBasicInfo()` - 获取基本信息
- `getFormatInfo()` - 获取格式信息
- `parseVideoParams()` - 解析视频参数
- `encapsulateH264ToMp4()` - 封装H264为MP4

### 5. Chapter03Demo.java
综合演示类，展示本章所有功能的使用方法。

## 环境要求

### FFmpeg安装
确保系统已安装FFmpeg并配置到PATH环境变量中：

```bash
# 验证安装
ffmpeg -version
ffprobe -version
ffplay -version
```

### Java环境
- JDK 17或更高版本
- Maven 3.6或更高版本

## 使用方法

### 1. 编译项目
```bash
cd examples
mvn clean compile
```

### 2. 运行单个示例
```bash
# 运行时间处理示例
java -cp target/classes com.ry.example.ffmpeg.chapter03.VideoAudioTime

# 运行分离音视频示例
java -cp target/classes com.ry.example.ffmpeg.chapter03.SeparateAudioVideo

# 运行合并音视频示例
java -cp target/classes com.ry.example.ffmpeg.chapter03.MergeAudioVideo

# 运行分析播放示例
java -cp target/classes com.ry.example.ffmpeg.chapter03.VideoAnalysisPlayer
```

### 3. 运行综合演示
```bash
java -cp target/classes com.ry.example.ffmpeg.chapter03.Chapter03Demo
```

## 示例文件

建议准备以下测试文件：
- `input.mp4` - 测试视频文件
- `input.h264` - H.264裸流文件
- `audio.mp3` - 音频文件

## 注意事项

1. **路径问题**：确保所有输入文件路径正确，或使用绝对路径
2. **权限问题**：确保FFmpeg有读写文件的权限
3. **依赖检查**：确保系统已安装所需的编解码器（如libx264, libx265等）
4. **内存使用**：大文件处理时注意内存使用情况
5. **进程管理**：播放器启动的进程需要适当管理

## 常见问题

### Q: ffprobe/ffmpeg命令执行失败
A: 检查FFmpeg是否正确安装并添加到PATH环境变量

### Q: 某些编解码器不支持
A: 安装相应的编解码器库，如libx264, libfdk_aac等

### Q: 视频播放器无法启动
A: 确保有图形界面环境，ffplay需要显示支持

### Q: 文件路径包含空格
A: 使用引号包围路径或对空格进行转义处理

## 扩展练习

1. 尝试不同的视频格式转换
2. 实现自定义滤镜效果
3. 添加视频元数据处理功能
4. 实现批量视频处理工具
5. 添加进度显示和错误处理

## 相关文档

- [FFmpeg官方文档](https://ffmpeg.org/documentation.html)
- [ffprobe文档](https://ffmpeg.org/ffprobe.html)
- [ffplay文档](https://ffmpeg.org/ffplay.html)
- 第3章文档：`docs/chapter03/FFmpeg的编解码.md`