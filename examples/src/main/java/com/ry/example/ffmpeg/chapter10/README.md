# 第10章 FFmpeg播放音视频

## 章节概述

第10章详细介绍了FFmpeg的音视频播放功能，包括SDL播放、推流拉流、线程同步等高级技术，以及完整的直播系统实现。

## 功能模块

### 10.1 通过SDL播放音视频
- **FFmpeg集成SDL**: SDL库的集成和配置
- **SDL播放视频**: 视频流渲染和显示
- **SDL播放音频**: 音频流播放和处理

### 10.2 FFmpeg推流和拉流
- **推拉流概念**: 网络流媒体传输基础
- **向网络推流**: 实时推送到RTMP服务器
- **从网络拉流**: 从服务器拉取和播放流

### 10.3 SDL处理线程间同步
- **SDL的线程**: 多线程音视频处理
- **SDL的互斥锁**: 资源保护和并发控制
- **SDL的信号量**: 批量处理和流量控制

### 10.4 实战项目：同步播放音视频
- **播放时钟同步**: 音视频时钟精确同步
- **优化同步播放**: 高性能同步播放算法

## 代码结构

### AudioVideoPlayer.java
主要的音视频播放处理器类，包含以下方法：

#### SDL播放方法
- `playVideoWithSDL()` - SDL播放视频
- `playAudioWithSDL()` - SDL播放音频
- `syncPlayAudioVideo()` - 同步播放音视频

#### 推流拉流方法
- `pushToRTMP()` - 推流到RTMP服务器
- `liveStreamToRTMP()` - 实时摄像头推流
- `pullFromRTMP()` - 从RTMP服务器拉流
- `playNetworkStream()` - 播放网络流
- `playHLSStream()` - 播放HLS流

#### 线程同步方法
- `multiThreadProcess()` - 多线程处理
- `mutexProtectedProcess()` - 互斥锁保护
- `semaphoreBatchProcess()` - 信号量批量处理

#### 同步播放方法
- `clockSync()` - 音视频时钟同步
- `optimizedSyncPlay()` - 优化同步播放
- `realTimeSyncProcess()` - 实时同步处理

### Chapter10Demo.java
综合演示类，展示所有功能的用法：

- `demonstrateSDLPlayback()` - SDL播放演示
- `demonstrateStreamProcessing()` - 推流拉流演示
- `demonstrateThreadSync()` - 线程同步演示
- `demonstrateSyncPlayback()` - 同步播放演示
- `createLiveStreamingSystem()` - 完整直播系统
- `multiSourceMixing()` - 多源混流演示
- `recordingPlaybackSystem()` - 录制回放系统

## 快速开始

### 1. SDL播放视频
```java
String command = AudioVideoPlayer.playVideoWithSDL(
    "input/video.mp4", 1280, 720
);
```

### 2. RTMP推流
```java
String command = AudioVideoPlayer.pushToRTMP(
    "input/live.mp4", 
    "rtmp://server.com/live/stream_key"
);
```

### 3. 实时同步处理
```java
String command = AudioVideoPlayer.realTimeSyncProcess(
    "rtmp://input.server/live/stream",
    "rtmp://output.server/live/stream"
);
```

## 使用示例

### 完整直播系统
```java
// 摄像头推流
String cameraPush = AudioVideoPlayer.liveStreamToRTMP(
    0, 0, "rtmp://live.server.com/app/stream_key"
);

// 实时转码
String transcode = AudioVideoPlayer.realTimeSyncProcess(
    "rtmp://live.server.com/app/stream_key",
    "rtmp://cdn.server.com/live/hd_stream"
);

// CDN播放
String playback = AudioVideoPlayer.playHLSStream(
    "https://cdn.server.com/live/playlist.m3u8"
);
```

### 时钟同步处理
```java
String syncCommand = AudioVideoPlayer.clockSync(
    "input/video.mp4",
    "input/audio.mp3", 
    "output/synced.mp4",
    100, 50  // 视频延迟100ms, 音频延迟50ms
);
```

## 参数说明

### SDL播放参数
- `screenWidth, screenHeight`: 播放窗口尺寸
- `window_title`: 播放窗口标题

### 推流参数
- `rtmpUrl`: RTMP服务器地址
- `videoDevice, audioDevice`: 摄像头和麦克风设备索引
- `bufferTime`: 网络缓冲时间

### 线程同步参数
- `threadCount`: 处理线程数量
- `timeout`: 超时时间（秒）
- `maxConcurrent`: 最大并发数

### 时钟同步参数
- `videoDelay, audioDelay`: 音视频延迟时间（毫秒）
- `syncThreshold`: 同步阈值

## 运行演示

```bash
# 编译项目
mvn compile

# 运行第10章演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter10.Chapter10Demo"
```

## 直播系统架构

```
[摄像头] → [FFmpeg推流] → [RTMP服务器] → [FFmpeg转码] → [CDN分发] → [客户端播放]
            ↓
        [录制存储] → [回放服务]
```

## 性能优化

### 1. 推流优化
- 使用 `-preset ultrafast` 降低延迟
- 调整 `-tune zerolatency` 参数
- 合理设置码率和分辨率

### 2. 播放优化
- 启用硬件解码
- 调整缓冲区大小
- 优化网络传输协议

### 3. 同步优化
- 精确的时钟同步算法
- 自适应缓冲策略
- 智能丢帧和补偿

## 故障排除

### 常见问题
1. **推流失败**: 检查网络连接和服务器配置
2. **播放卡顿**: 调整缓冲和网络参数
3. **音视频不同步**: 检查时间戳和延迟设置
4. **性能问题**: 优化编码参数和线程配置

### 解决方案
- 使用 `rtmp://` 或 `http://` 协议测试连接
- 调整 `-buffer` 和 `-probesize` 参数
- 使用 `-async` 和 `-vsync` 同步选项
- 启用硬件加速和多线程处理

## 学习目标

通过本章学习，您将掌握：
- SDL音视频播放技术
- RTMP推流和拉流实现
- 多线程音视频处理
- 实时同步播放算法
- 完整直播系统构建

## 扩展练习

1. 开发GUI播放器界面
2. 实现多路视频混流
3. 构建分布式直播系统
4. 集成WebRTC实时通信
5. 开发自适应码率流媒体