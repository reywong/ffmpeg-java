# Chapter 12 - FFmpeg的移动开发

本目录包含第12章"FFmpeg的移动开发"的相关代码示例和演示程序。

## 文件结构

```
chapter12/
├── FFmpegManager.java      # FFmpeg管理器，提供基本操作接口
├── AudioTrackPlayer.java   # AudioTrack音频播放器
├── VideoEditor.java        # 视频编辑器核心类
├── VideoClip.java          # 视频片段数据模型
├── Chapter12Demo.java      # 演示程序
└── README.md               # 本文档
```

## 主要功能

### 1. FFmpegManager
- 初始化FFmpeg库
- 视频播放控制
- 视频信息获取
- 资源管理

### 2. AudioTrackPlayer
- Android原生音频播放
- 支持多种音频格式
- 音量控制
- 播放状态管理

### 3. VideoEditor
- 多视频片段管理
- 时间轴渲染
- 滤镜效果应用
- 转场效果
- 播放控制

### 4. 数据模型
- VideoClip: 视频片段属性管理
- 支持裁剪、音量、静音等属性

## 使用示例

### 基本视频编辑

```java
// 创建视频编辑器
VideoEditor editor = new VideoEditor();

// 添加视频片段
VideoClip clip = new VideoClip("/sdcard/video.mp4");
clip.setStartTime(0);
clip.setTrimStart(5000);  // 从5秒开始
clip.setTrimEnd(15000);   // 到15秒结束
editor.addVideoClip(clip);

// 应用滤镜
editor.applyFilter(VideoEditor.FILTER_SEPIA);

// 开始播放
editor.startPlayback();
```

### 音频播放

```java
// 创建音频播放器
AudioTrackPlayer player = new AudioTrackPlayer();
player.init();
player.setAudioConfig(44100, CHANNEL_OUT_STEREO, ENCODING_PCM_16BIT);

// 播放音频数据
player.play();
player.writeData(audioData);
```

### FFmpeg初始化

```java
// 初始化FFmpeg
FFmpegManager ffmpeg = new FFmpegManager();
int result = ffmpeg.initFFmpeg();

// 获取视频信息
long duration = ffmpeg.getVideoDuration("/sdcard/video.mp4");
String info = ffmpeg.getVideoInfo("/sdcard/video.mp4");
```

## 编译说明

### 环境要求
- Android Studio 4.0+
- Android NDK 21+
- Android SDK API 21+

### 依赖库
- FFmpeg 4.4+
- libx264 (H.264编码)
- libmp3lame (MP3编码)
- libfreetype (字体渲染)

### 编译步骤

1. **编译FFmpeg库**
   ```bash
   # 下载FFmpeg源码
   wget https://ffmpeg.org/releases/ffmpeg-4.4.tar.gz
   tar -zxvf ffmpeg-4.4.tar.gz
   cd ffmpeg-4.4
   
   # 使用提供的build_android.sh脚本编译
   chmod +x build_android.sh
   ./build_android.sh
   ```

2. **集成到Android项目**
   - 复制编译好的SO库到 `app/src/main/jniLibs/`
   - 配置CMakeLists.txt
   - 更新build.gradle

## 权限配置

在AndroidManifest.xml中添加必要权限：

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
```

## 注意事项

1. 本示例代码需要在真实Android设备上测试
2. 某些功能需要特定的Android版本支持
3. 注意处理各种异常情况
4. 在生产环境中需要更完善的错误处理机制