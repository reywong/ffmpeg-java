# 第 2 章 FFmpeg开发基础

FFmpeg 是一个功能强大的多媒体处理框架，掌握其开发基础对于进行音视频处理至关重要。本章将介绍音视频编码标准、FFmpeg 的主要数据结构、查看音视频信息的方法以及常见的处理流程。

## 2.1 音视频的编码标准

### 2.1.1 音视频编码的发展历程

#### 视频编码标准的发展

视频编码技术的发展经历了从模拟到数字、从低效到高效的演进过程：

**第一代：MPEG-1/MPEG-2 时代**
- MPEG-1 (1993年)：主要用于 VCD，分辨率 352×288，比特率约 1.5Mbps
- MPEG-2 (1995年)：用于 DVD 和数字电视，支持 SDTV 和 HDTV

**第二代：MPEG-4/H.264 时代**
- MPEG-4 Part 2 (1998年)：DivX、XviD 等编码格式的基础
- H.264/AVC (2003年)：革命性突破，在相同质量下比特率降低50%

**第三代：H.265/HEVC 及新一代标准**
- H.265/HEVC (2013年)：比 H.264 再降低50%比特率
- H.266/VVC (2020年)：支持 8K 超高清，进一步压缩效率提升

```java
// 不同编码标准的配置对比
public class VideoStandards {
    // H.264 标准配置
    public static final String H264_PROFILE = "high";
    public static final String H264_LEVEL = "4.0";
    public static final int H264_MAX_BITRATE = 5000000; // 5Mbps
    
    // H.265 标准配置
    public static final String H265_PROFILE = "main";
    public static final String H265_LEVEL = "5.0";
    public static final int H265_MAX_BITRATE = 8000000; // 8Mbps
}
```

#### 音频编码标准的发展

**有损音频编码**
- MP3 (1991年)：首个广泛应用的数字音频压缩格式
- AAC (1997年)：比 MP3 效率更高，成为移动设备主流
- Opus (2012年)：低延迟、高质量，适合实时通信

**无损音频编码**
- FLAC (2000年)：开源无损压缩，压缩率约50%
- ALAC (2004年)：苹果开发的无损格式

### 2.1.2 音视频文件的封装格式

#### 主流封装格式

**MP4 (MPEG-4 Part 14)**
- 最通用的容器格式
- 支持多种音视频编码
- 适合流媒体传输

```java
// MP4 封装格式分析示例
public class MP4Analyzer {
    public static void analyzeMP4(String filePath) {
        FFmpeg ffmpeg = new FFmpeg();
        
        // 获取 MP4 文件信息
        String info = ffmpeg.execute("ffprobe -v quiet -print_format json -show_format -show_streams " + filePath);
        
        // 解析 MP4 盒子结构
        JSONObject json = new JSONObject(info);
        JSONObject format = json.getJSONObject("format");
        
        System.out.println("Format: " + format.getString("format_name"));
        System.out.println("Duration: " + format.getDouble("duration"));
        System.out.println("Bit rate: " + format.getString("bit_rate"));
    }
}
```

**AVI (Audio Video Interleave)**
- 微软开发的早期格式
- 结构简单但效率较低
- 不支持流式播放

**MKV (Matroska)**
- 开源容器格式
- 支持多音轨、多字幕
- 功能强大但体积较大

#### 封装格式的选择

| 格式 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| MP4 | 兼容性好、体积小 | 功能相对简单 | 网络传输、移动设备 |
| MKV | 功能强大、开放 | 兼容性一般 | 本地存储、多音轨 |
| AVI | 结构简单 | 体积大、效率低 | 兼容旧系统 |

### 2.1.3 国家数字音视频标准AVS

#### AVS标准概述

AVS (Audio Video coding Standard) 是中国自主制定的数字音视频编解码标准：

**AVS1.0 (2006年)**
- 面向标清和高清电视
- 压缩效率相当于 H.264 基准档次
- 在广播电视系统广泛应用

**AVS2 (2016年)**
- 支持 4K 超高清
- 压缩效率比 AVS1 提高 50%
- 面向高清和超高清应用

**AVS3 (2022年)**
- 支持 8K 超高清
- 压缩效率比 AVS2 提高 40%
- 面向未来超高清应用

```java
// AVS 编码器配置
public class AVSEncoder {
    private AVSContext context;
    
    public void configureAVS2() {
        context = new AVSContext();
        context.setProfile("main");
        context.setLevel("5.0");
        context.setWidth(3840);  // 4K
        context.setHeight(2160);
        context.setBitrate(15000000); // 15Mbps
        context.setFrameRate(30);
        
        // AVS2 特有参数
        context.setUseRDO(true);  // 率失真优化
        context.setEnableSAO(true); // 样点自适应偏移
    }
}
```

#### AVS与H.264/H.265对比

| 标准 | 发布年份 | 压缩效率 | 应用领域 | 专利费用 |
|------|----------|----------|----------|----------|
| H.264 | 2003 | 基准 | 全球通用 | 较高 |
| H.265 | 2013 | 2倍H.264 | 4K/8K视频 | 很高 |
| AVS2 | 2016 | 1.5倍H.264 | 中国广电 | 免费 |
| AVS3 | 2022 | 2倍AVS2 | 8K视频 | 免费 |

## 2.2 FFmpeg的主要数据结构

### 2.2.1 FFmpeg的编码与封装

#### 核心数据结构

**AVFormatContext**
- 封装格式的上下文
- 管理整个媒体文件的读写
- 包含所有流的信息

```c
// AVFormatContext 的核心字段
typedef struct AVFormatContext {
    const AVClass *av_class;
    struct AVInputFormat *iformat;  // 输入格式
    struct AVOutputFormat *oformat; // 输出格式
    void *priv_data;
    AVIOContext *pb;                // I/O 上下文
    int nb_streams;                 // 流数量
    AVStream **streams;             // 流数组
    char filename[1024];            // 文件名
    int64_t start_time;             // 开始时间
    int64_t duration;               // 持续时间
    int bit_rate;                   // 比特率
    AVDictionary *metadata;         // 元数据
} AVFormatContext;
```

**AVStream**
- 表示音视频流
- 包含编码参数和时间基信息

```c
// AVStream 的核心字段
typedef struct AVStream {
    int index;                      // 流索引
    AVCodecParameters *codecpar;   // 编码参数
    AVCodecContext *codec;         // 编码器上下文（已废弃）
    AVRational time_base;           // 时间基
    int64_t start_time;            // 流开始时间
    int64_t duration;              // 流持续时间
    int64_t nb_frames;             // 帧数
    AVDictionary *metadata;        // 流元数据
    AVRational r_frame_rate;        // 帧率
} AVStream;
```

#### 编码封装流程

```java
// FFmpeg 封装处理的 Java 封装
public class FFmpegMuxer {
    private long formatContext;
    private List<Long> streams;
    
    public boolean openFile(String filename, String formatName) {
        // 分配输出格式上下文
        formatContext = FFmpegNative.avformat_alloc_output_context2(
            null, formatName, null, filename);
        
        if (formatContext == 0) {
            return false;
        }
        
        streams = new ArrayList<>();
        return true;
    }
    
    public long addStream(String codecName) {
        // 查找编码器
        long codec = FFmpegNative.avcodec_find_encoder_by_name(codecName);
        if (codec == 0) return -1;
        
        // 创建新流
        long stream = FFmpegNative.avformat_new_stream(formatContext, codec);
        if (stream == 0) return -1;
        
        streams.add(stream);
        return stream;
    }
    
    public boolean writeHeader() {
        return FFmpegNative.avformat_write_header(formatContext, null) >= 0;
    }
    
    public boolean writePacket(long stream, long packet) {
        return FFmpegNative.av_interleaved_write_frame(formatContext, packet) >= 0;
    }
    
    public boolean writeTrailer() {
        return FFmpegNative.av_write_trailer(formatContext) >= 0;
    }
}
```

### 2.2.2 FFmpeg的数据包样式

#### AVPacket 结构

AVPacket 是 FFmpeg 中传输压缩数据的基本单位：

```c
// AVPacket 核心结构
typedef struct AVPacket {
    AVBufferRef *buf;              // 缓冲区引用
    int64_t pts;                   // 显示时间戳
    int64_t dts;                   // 解码时间戳
    uint8_t *data;                 // 数据指针
    int size;                      // 数据大小
    int stream_index;              // 流索引
    int flags;                     // 标志位
    AVPacketSideData *side_data;   // 附加数据
    int side_data_elems;           // 附加数据数量
    int64_t duration;              // 包持续时间
    int64_t pos;                   // 文件位置
} AVPacket;
```

#### 时间戳处理

时间戳是音视频同步的关键：

```java
// 时间戳转换工具类
public class TimestampConverter {
    private AVRational timeBase;
    
    public TimestampConverter(AVRational timeBase) {
        this.timeBase = timeBase;
    }
    
    // 将秒数转换为时间戳
    public long secondsToTimestamp(double seconds) {
        return (long)(seconds * timeBase.den / timeBase.num);
    }
    
    // 将时间戳转换为秒数
    public double timestampToSeconds(long timestamp) {
        return timestamp * (double)timeBase.num / timeBase.den;
    }
    
    // 时间戳基转换
    public long rescaleTimestamp(long timestamp, AVRational from, AVRational to) {
        return (long)(timestamp * (double)from.num * to.den / 
                     (from.den * to.num));
    }
}
```

#### 包的创建和销毁

```java
// AVPacket 操作封装
public class PacketManager {
    static {
        System.loadLibrary("ffmpeg-wrapper");
    }
    
    // 创建新包
    public static long createPacket() {
        return nativeCreatePacket();
    }
    
    // 释放包
    public static void freePacket(long packet) {
        nativeFreePacket(packet);
    }
    
    // 克隆包
    public static long clonePacket(long src) {
        return nativeClonePacket(src);
    }
    
    // 获取包数据
    public static byte[] getPacketData(long packet) {
        return nativeGetPacketData(packet);
    }
    
    private static native long nativeCreatePacket();
    private static native void nativeFreePacket(long packet);
    private static native long nativeClonePacket(long src);
    private static native byte[] nativeGetPacketData(long packet);
}
```

### 2.2.3 FFmpeg的过滤器类型

#### 过滤器基础概念

FFmpeg 过滤器框架提供强大的音视频处理能力：

**过滤器类型**
- 音频过滤器：处理音频流
- 视频过滤器：处理视频流  
- 多媒体过滤器：同时处理音频和视频

#### 常用视频过滤器

**scale 过滤器**
```java
// 视频缩放过滤器配置
public class VideoScaleFilter {
    private String filterGraph;
    
    public void setupScale(int width, int height) {
        filterGraph = String.format("scale=%d:%d", width, height);
    }
    
    public void setupScaleWithAspect(int width, int height, String aspect) {
        filterGraph = String.format("scale=%d:%d:aspect=%s", width, height, aspect);
    }
    
    public void setupScaleForceOriginal(String size) {
        filterGraph = String.format("scale=trunc(iw*%s):trunc(ih*%s)", size, size);
    }
    
    public String getFilterGraph() {
        return filterGraph;
    }
}
```

**crop 过滤器**
```java
// 视频裁剪过滤器
public class VideoCropFilter {
    private String filterGraph;
    
    public void setupCrop(int x, int y, int width, int height) {
        filterGraph = String.format("crop=%d:%d:%d:%d", width, height, x, y);
    }
    
    public void setupCropFromCenter(int width, int height) {
        filterGraph = String.format("crop=%d:%d:(iw-%d)/2:(ih-%d)/2", 
                                   width, height, width, height);
    }
    
    public String getFilterGraph() {
        return filterGraph;
    }
}
```

#### 常用音频过滤器

**volume 过滤器**
```java
// 音量调节过滤器
public class VolumeFilter {
    private String filterGraph;
    
    public void setVolume(double volume) {
        filterGraph = String.format("volume=%.2f", volume);
    }
    
    public void setVolumeDB(double db) {
        filterGraph = String.format("volume=%.2fdB", db);
    }
    
    public void setVolumeReplayGain() {
        filterGraph = "volume=replaygain";
    }
    
    public String getFilterGraph() {
        return filterGraph;
    }
}
```

**aresample 过滤器**
```java
// 音频重采样过滤器
public class AudioResampleFilter {
    private String filterGraph;
    
    public void setSampleRate(int sampleRate) {
        filterGraph = String.format("aresample=%d", sampleRate);
    }
    
    public void setSampleRateWithChannels(int sampleRate, int channels) {
        filterGraph = String.format("aresample=%d:out_channel_layout=%d", 
                                   sampleRate, channels == 2 ? 3 : 4);
    }
    
    public String getFilterGraph() {
        return filterGraph;
    }
}
```

## 2.3 FFmpeg查看音视频信息

### 2.3.1 打开与关闭音视频文件

#### 文件打开流程

```java
// 音视频文件打开器
public class MediaFileOpener {
    private long formatContext;
    private String filename;
    
    public boolean open(String filename) {
        this.filename = filename;
        
        // 分配格式上下文
        formatContext = FFmpegNative.avformat_alloc_context();
        if (formatContext == 0) {
            return false;
        }
        
        // 打开输入文件
        int result = FFmpegNative.avformat_open_input(
            new PointerPointer(formatContext), filename, null, null);
        
        if (result < 0) {
            close();
            return false;
        }
        
        // 查找流信息
        result = FFmpegNative.avformat_find_stream_info(formatContext, null);
        if (result < 0) {
            close();
            return false;
        }
        
        return true;
    }
    
    public void close() {
        if (formatContext != 0) {
            FFmpegNative.avformat_close_input(new PointerPointer(formatContext));
            FFmpegNative.avformat_free_context(formatContext);
            formatContext = 0;
        }
    }
    
    public long getFormatContext() {
        return formatContext;
    }
}
```

#### 错误处理机制

```java
// 错误处理工具类
public class FFmpegErrorHandler {
    private static final Map<Integer, String> ERROR_MESSAGES = new HashMap<>();
    
    static {
        ERROR_MESSAGES.put(-2, "No such file or directory");
        ERROR_MESSAGES.put(-22, "Invalid argument");
        ERROR_MESSAGES.put(-13, "Permission denied");
        ERROR_MESSAGES.put(-1094995529, "Invalid data found when processing input");
        ERROR_MESSAGES.put(-542398533, "Operation not permitted");
    }
    
    public static String getErrorMessage(int errorCode) {
        return ERROR_MESSAGES.getOrDefault(errorCode, "Unknown error: " + errorCode);
    }
    
    public static void checkError(int result, String operation) throws FFmpegException {
        if (result < 0) {
            throw new FFmpegException(operation + " failed: " + getErrorMessage(result));
        }
    }
}
```

### 2.3.2 查看音视频的信息

#### 基本信息获取

```java
// 媒体信息查看器
public class MediaInfoViewer {
    private long formatContext;
    
    public MediaInfoViewer(long formatContext) {
        this.formatContext = formatContext;
    }
    
    // 获取文件基本信息
    public Map<String, String> getBasicInfo() {
        Map<String, String> info = new HashMap<>();
        
        // 文件名
        info.put("filename", FFmpegNative.getFilename(formatContext));
        
        // 格式名称
        info.put("format", FFmpegNative.getFormatName(formatContext));
        
        // 持续时间
        long duration = FFmpegNative.getDuration(formatContext);
        info.put("duration", formatDuration(duration));
        
        // 比特率
        long bitrate = FFmpegNative.getBitrate(formatContext);
        info.put("bitrate", String.valueOf(bitrate));
        
        // 流数量
        int streamCount = FFmpegNative.getStreamCount(formatContext);
        info.put("stream_count", String.valueOf(streamCount));
        
        return info;
    }
    
    // 获取流信息
    public List<Map<String, String>> getStreamInfo() {
        List<Map<String, String>> streams = new ArrayList<>();
        int streamCount = FFmpegNative.getStreamCount(formatContext);
        
        for (int i = 0; i < streamCount; i++) {
            long stream = FFmpegNative.getStream(formatContext, i);
            Map<String, String> streamInfo = analyzeStream(stream, i);
            streams.add(streamInfo);
        }
        
        return streams;
    }
    
    private Map<String, String> analyzeStream(long stream, int index) {
        Map<String, String> info = new HashMap<>();
        
        // 获取编码器参数
        long codecpar = FFmpegNative.getStreamCodecpar(stream);
        
        // 编码类型
        int codecType = FFmpegNative.getCodecType(codecpar);
        info.put("codec_type", getCodecTypeName(codecType));
        
        // 编码器名称
        String codecName = FFmpegNative.getCodecName(codecpar);
        info.put("codec_name", codecName);
        
        // 分辨率（视频流）
        if (codecType == 0) { // AVMEDIA_TYPE_VIDEO
            int width = FFmpegNative.getWidth(codecpar);
            int height = FFmpegNative.getHeight(codecpar);
            info.put("resolution", width + "x" + height);
            
            // 帧率
            AVRational frameRate = FFmpegNative.getFrameRate(stream);
            double fps = (double)frameRate.num / frameRate.den;
            info.put("frame_rate", String.format("%.2f", fps));
        }
        
        // 采样率（音频流）
        if (codecType == 1) { // AVMEDIA_TYPE_AUDIO
            int sampleRate = FFmpegNative.getSampleRate(codecpar);
            info.put("sample_rate", String.valueOf(sampleRate));
            
            int channels = FFmpegNative.getChannels(codecpar);
            info.put("channels", String.valueOf(channels));
        }
        
        info.put("index", String.valueOf(index));
        return info;
    }
    
    private String formatDuration(long duration) {
        long seconds = duration / 1000000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    private String getCodecTypeName(int codecType) {
        switch (codecType) {
            case 0: return "video";
            case 1: return "audio";
            case 2: return "subtitle";
            default: return "unknown";
        }
    }
}
```

#### 元数据提取

```java
// 元数据提取器
public class MetadataExtractor {
    private long formatContext;
    
    public MetadataExtractor(long formatContext) {
        this.formatContext = formatContext;
    }
    
    // 获取文件级元数据
    public Map<String, String> getFormatMetadata() {
        Map<String, String> metadata = new HashMap<>();
        long metadataDict = FFmpegNative.getFormatMetadata(formatContext);
        
        if (metadataDict != 0) {
            extractMetadata(metadataDict, metadata);
        }
        
        return metadata;
    }
    
    // 获取流级元数据
    public Map<String, String> getStreamMetadata(int streamIndex) {
        Map<String, String> metadata = new HashMap<>();
        long stream = FFmpegNative.getStream(formatContext, streamIndex);
        long metadataDict = FFmpegNative.getStreamMetadata(stream);
        
        if (metadataDict != 0) {
            extractMetadata(metadataDict, metadata);
        }
        
        return metadata;
    }
    
    private void extractMetadata(long metadataDict, Map<String, String> result) {
        // 遍历元数据字典
        long entry = FFmpegNative.av_dict_get(metadataDict, "", null, 0);
        while (entry != 0) {
            String key = FFmpegNative.getDictKey(entry);
            String value = FFmpegNative.getDictValue(entry);
            result.put(key, value);
            
            entry = FFmpegNative.av_dict_get(metadataDict, "", entry, 0);
        }
    }
}
```

### 2.3.3 查看编解码器的参数

#### 编解码器参数解析

```java
// 编解码器参数分析器
public class CodecParametersAnalyzer {
    private long codecpar;
    
    public CodecParametersAnalyzer(long codecpar) {
        this.codecpar = codecpar;
    }
    
    // 视频编码器参数
    public VideoCodecParams getVideoParams() {
        VideoCodecParams params = new VideoCodecParams();
        
        params.codecId = FFmpegNative.getCodecId(codecpar);
        params.codecType = FFmpegNative.getCodecType(codecpar);
        params.width = FFmpegNative.getWidth(codecpar);
        params.height = FFmpegNative.getHeight(codecpar);
        params.pixFmt = FFmpegNative.getPixelFormat(codecpar);
        params.bitrate = FFmpegNative.getBitrate(codecpar);
        
        // 获取视频特有参数
        params.profile = FFmpegNative.getProfile(codecpar);
        params.level = FFmpegNative.getLevel(codecpar);
        params.colorSpace = FFmpegNative.getColorSpace(codecpar);
        params.colorRange = FFmpegNative.getColorRange(codecpar);
        
        return params;
    }
    
    // 音频编码器参数
    public AudioCodecParams getAudioParams() {
        AudioCodecParams params = new AudioCodecParams();
        
        params.codecId = FFmpegNative.getCodecId(codecpar);
        params.codecType = FFmpegNative.getCodecType(codecpar);
        params.sampleRate = FFmpegNative.getSampleRate(codecpar);
        params.channels = FFmpegNative.getChannels(codecpar);
        params.sampleFmt = FFmpegNative.getSampleFormat(codecpar);
        params.bitrate = FFmpegNative.getBitrate(codecpar);
        
        // 获取音频特有参数
        params.channelLayout = FFmpegNative.getChannelLayout(codecpar);
        params.frameSize = FFmpegNative.getFrameSize(codecpar);
        params.initialPadding = FFmpegNative.getInitialPadding(codecpar);
        params.trailingPadding = FFmpegNative.getTrailingPadding(codecpar);
        
        return params;
    }
    
    // 通用参数信息
    public String getGeneralInfo() {
        StringBuilder sb = new StringBuilder();
        
        int codecId = FFmpegNative.getCodecId(codecpar);
        String codecName = FFmpegNative.getCodecName(codecpar);
        
        sb.append("Codec: ").append(codecName).append("\n");
        sb.append("Codec ID: ").append(codecId).append("\n");
        
        long bitrate = FFmpegNative.getBitrate(codecpar);
        if (bitrate > 0) {
            sb.append("Bitrate: ").append(bitrate / 1000).append(" kbps\n");
        }
        
        return sb.toString();
    }
    
    // 视频参数数据类
    public static class VideoCodecParams {
        public int codecId;
        public int codecType;
        public int width;
        public int height;
        public int pixFmt;
        public long bitrate;
        public int profile;
        public int level;
        public int colorSpace;
        public int colorRange;
    }
    
    // 音频参数数据类
    public static class AudioCodecParams {
        public int codecId;
        public int codecType;
        public int sampleRate;
        public int channels;
        public int sampleFmt;
        public long bitrate;
        public long channelLayout;
        public int frameSize;
        public int initialPadding;
        public int trailingPadding;
    }
}
```

## 2.4 FFmpeg常见的处理流程

### 2.4.1 复制编解码器的参数

#### 参数复制流程

```java
// 编解码器参数复制器
public class CodecParametersCopier {
    
    // 从源流复制参数到目标流
    public static boolean copyParameters(long srcStream, long dstStream) {
        long srcCodecpar = FFmpegNative.getStreamCodecpar(srcStream);
        long dstCodecpar = FFmpegNative.getStreamCodecpar(dstStream);
        
        // 复制编解码器参数
        int result = FFmpegNative.avcodec_parameters_copy(dstCodecpar, srcCodecpar);
        if (result < 0) {
            return false;
        }
        
        // 复制流的时间基
        AVRational srcTimeBase = FFmpegNative.getStreamTimeBase(srcStream);
        FFmpegNative.setStreamTimeBase(dstStream, srcTimeBase);
        
        return true;
    }
    
    // 复制并修改参数
    public static boolean copyAndModifyParameters(long srcStream, long dstStream, 
                                                 ParameterModifier modifier) {
        if (!copyParameters(srcStream, dstStream)) {
            return false;
        }
        
        long dstCodecpar = FFmpegNative.getStreamCodecpar(dstStream);
        return modifier.modify(dstCodecpar);
    }
    
    // 参数修改器接口
    public interface ParameterModifier {
        boolean modify(long codecpar);
    }
    
    // 视频参数修改器
    public static class VideoParameterModifier implements ParameterModifier {
        private int newWidth;
        private int newHeight;
        private long newBitrate;
        
        public VideoParameterModifier(int width, int height, long bitrate) {
            this.newWidth = width;
            this.newHeight = height;
            this.newBitrate = bitrate;
        }
        
        @Override
        public boolean modify(long codecpar) {
            // 检查是否为视频流
            int codecType = FFmpegNative.getCodecType(codecpar);
            if (codecType != 0) { // AVMEDIA_TYPE_VIDEO
                return false;
            }
            
            // 修改视频参数
            FFmpegNative.setWidth(codecpar, newWidth);
            FFmpegNative.setHeight(codecpar, newHeight);
            if (newBitrate > 0) {
                FFmpegNative.setBitrate(codecpar, newBitrate);
            }
            
            return true;
        }
    }
    
    // 音频参数修改器
    public static class AudioParameterModifier implements ParameterModifier {
        private int newSampleRate;
        private int newChannels;
        private long newBitrate;
        
        public AudioParameterModifier(int sampleRate, int channels, long bitrate) {
            this.newSampleRate = sampleRate;
            this.newChannels = channels;
            this.newBitrate = bitrate;
        }
        
        @Override
        public boolean modify(long codecpar) {
            // 检查是否为音频流
            int codecType = FFmpegNative.getCodecType(codecpar);
            if (codecType != 1) { // AVMEDIA_TYPE_AUDIO
                return false;
            }
            
            // 修改音频参数
            if (newSampleRate > 0) {
                FFmpegNative.setSampleRate(codecpar, newSampleRate);
            }
            if (newChannels > 0) {
                FFmpegNative.setChannels(codecpar, newChannels);
            }
            if (newBitrate > 0) {
                FFmpegNative.setBitrate(codecpar, newBitrate);
            }
            
            return true;
        }
    }
}
```

#### 参数验证和优化

```java
// 参数验证器
public class ParameterValidator {
    
    public static ValidationResult validateVideoParams(long codecpar) {
        ValidationResult result = new ValidationResult();
        
        int width = FFmpegNative.getWidth(codecpar);
        int height = FFmpegNative.getHeight(codecpar);
        int pixFmt = FFmpegNative.getPixelFormat(codecpar);
        
        // 检查分辨率
        if (width <= 0 || height <= 0) {
            result.addError("Invalid resolution: " + width + "x" + height);
        } else {
            if (width % 2 != 0 || height % 2 != 0) {
                result.addWarning("Resolution should be multiple of 2");
            }
            
            if (width > 7680 || height > 4320) {
                result.addWarning("Resolution exceeds 8K");
            }
        }
        
        // 检查像素格式
        if (!isValidPixelFormat(pixFmt)) {
            result.addError("Invalid pixel format: " + pixFmt);
        }
        
        return result;
    }
    
    public static ValidationResult validateAudioParams(long codecpar) {
        ValidationResult result = new ValidationResult();
        
        int sampleRate = FFmpegNative.getSampleRate(codecpar);
        int channels = FFmpegNative.getChannels(codecpar);
        int sampleFmt = FFmpegNative.getSampleFormat(codecpar);
        
        // 检查采样率
        if (sampleRate <= 0) {
            result.addError("Invalid sample rate: " + sampleRate);
        } else {
            if (!isValidSampleRate(sampleRate)) {
                result.addWarning("Unusual sample rate: " + sampleRate);
            }
        }
        
        // 检查声道数
        if (channels <= 0 || channels > 8) {
            result.addError("Invalid channel count: " + channels);
        }
        
        // 检查采样格式
        if (!isValidSampleFormat(sampleFmt)) {
            result.addError("Invalid sample format: " + sampleFmt);
        }
        
        return result;
    }
    
    private static boolean isValidPixelFormat(int pixFmt) {
        return pixFmt >= 0 && pixFmt <= 100; // 简化检查
    }
    
    private static boolean isValidSampleRate(int sampleRate) {
        return sampleRate == 8000 || sampleRate == 11025 || sampleRate == 16000 ||
               sampleRate == 22050 || sampleRate == 44100 || sampleRate == 48000 ||
               sampleRate == 88200 || sampleRate == 96000 || sampleRate == 192000;
    }
    
    private static boolean isValidSampleFormat(int sampleFmt) {
        return sampleFmt >= 0 && sampleFmt <= 10; // 简化检查
    }
    
    // 验证结果类
    public static class ValidationResult {
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
    }
}
```

### 2.4.2 创建并写入音视频文件

#### 文件创建流程

```java
// 音视频文件创建器
public class MediaFileCreator {
    private long outputFormatContext;
    private List<Long> streams;
    private String outputPath;
    
    public boolean createFile(String outputPath, String formatName) {
        this.outputPath = outputPath;
        
        // 分配输出格式上下文
        outputFormatContext = FFmpegNative.avformat_alloc_output_context2(
            null, formatName, null, outputPath);
        
        if (outputFormatContext == 0) {
            return false;
        }
        
        streams = new ArrayList<>();
        return true;
    }
    
    // 添加视频流
    public long addVideoStream(String codecName, int width, int height, int frameRate) {
        // 查找编码器
        long codec = FFmpegNative.avcodec_find_encoder_by_name(codecName);
        if (codec == 0) {
            return -1;
        }
        
        // 创建流
        long stream = FFmpegNative.avformat_new_stream(outputFormatContext, codec);
        if (stream == 0) {
            return -1;
        }
        
        // 设置视频参数
        long codecpar = FFmpegNative.getStreamCodecpar(stream);
        FFmpegNative.setCodecType(codecpar, 0); // AVMEDIA_TYPE_VIDEO
        FFmpegNative.setWidth(codecpar, width);
        FFmpegNative.setHeight(codecpar, height);
        FFmpegNative.setPixelFormat(codecpar, 0); // AV_PIX_FMT_YUV420P
        
        // 设置帧率和时间基
        AVRational frameRateRational = new AVRational();
        frameRateRational.num = frameRate;
        frameRateRational.den = 1;
        FFmpegNative.setFrameRate(stream, frameRateRational);
        
        AVRational timeBase = new AVRational();
        timeBase.num = 1;
        timeBase.den = frameRate;
        FFmpegNative.setStreamTimeBase(stream, timeBase);
        
        streams.add(stream);
        return stream;
    }
    
    // 添加音频流
    public long addAudioStream(String codecName, int sampleRate, int channels, int bitrate) {
        // 查找编码器
        long codec = FFmpegNative.avcodec_find_encoder_by_name(codecName);
        if (codec == 0) {
            return -1;
        }
        
        // 创建流
        long stream = FFmpegNative.avformat_new_stream(outputFormatContext, codec);
        if (stream == 0) {
            return -1;
        }
        
        // 设置音频参数
        long codecpar = FFmpegNative.getStreamCodecpar(stream);
        FFmpegNative.setCodecType(codecpar, 1); // AVMEDIA_TYPE_AUDIO
        FFmpegNative.setSampleRate(codecpar, sampleRate);
        FFmpegNative.setChannels(codecpar, channels);
        FFmpegNative.setSampleFormat(codecpar, 1); // AV_SAMPLE_FMT_S16
        FFmpegNative.setBitrate(codecpar, bitrate);
        
        // 设置时间基
        AVRational timeBase = new AVRational();
        timeBase.num = 1;
        timeBase.den = sampleRate;
        FFmpegNative.setStreamTimeBase(stream, timeBase);
        
        streams.add(stream);
        return stream;
    }
    
    // 打开输出文件
    public boolean openOutput() {
        // 分配输出IO上下文
        if ((FFmpegNative.avio_open2(outputFormatContext, outputPath, 2, null, null)) < 0) {
            return false;
        }
        
        // 写入文件头
        return FFmpegNative.avformat_write_header(outputFormatContext, null) >= 0;
    }
    
    // 写入数据包
    public boolean writePacket(int streamIndex, long packet) {
        // 重新缩放时间戳
        long stream = streams.get(streamIndex);
        AVRational timeBase = FFmpegNative.getStreamTimeBase(stream);
        
        FFmpegNative.rescaleTimestamp(packet, timeBase);
        FFmpegNative.setPacketStreamIndex(packet, streamIndex);
        
        return FFmpegNative.av_interleaved_write_frame(outputFormatContext, packet) >= 0;
    }
    
    // 完成写入
    public boolean finalizeOutput() {
        return FFmpegNative.av_write_trailer(outputFormatContext) >= 0;
    }
    
    // 关闭文件
    public void close() {
        if (outputFormatContext != 0) {
            FFmpegNative.avio_closep(outputFormatContext);
            FFmpegNative.avformat_free_context(outputFormatContext);
            outputFormatContext = 0;
        }
    }
}
```

#### 数据包写入管理

```java
// 数据包写入管理器
public class PacketWriter {
    private MediaFileCreator fileCreator;
    private long[] lastPts;
    private long[] lastDts;
    
    public PacketWriter(MediaFileCreator fileCreator, int streamCount) {
        this.fileCreator = fileCreator;
        this.lastPts = new long[streamCount];
        this.lastDts = new long[streamCount];
        
        Arrays.fill(lastPts, -1);
        Arrays.fill(lastDts, -1);
    }
    
    // 写入视频数据包
    public boolean writeVideoPacket(byte[] data, long pts, long dts, int keyFrame) {
        long packet = createPacket();
        try {
            FFmpegNative.setPacketData(packet, data);
            FFmpegNative.setPacketSize(packet, data.length);
            FFmpegNative.setPacketPts(packet, pts);
            FFmpegNative.setPacketDts(packet, dts);
            
            if (keyFrame > 0) {
                FFmpegNative.setPacketFlag(packet, FFmpegNative.AV_PKT_FLAG_KEY);
            }
            
            return fileCreator.writePacket(0, packet);
        } finally {
            FFmpegNative.av_packet_unref(packet);
        }
    }
    
    // 写入音频数据包
    public boolean writeAudioPacket(byte[] data, long pts) {
        long packet = createPacket();
        try {
            FFmpegNative.setPacketData(packet, data);
            FFmpegNative.setPacketSize(packet, data.length);
            FFmpegNative.setPacketPts(packet, pts);
            FFmpegNative.setPacketDts(packet, pts);
            
            return fileCreator.writePacket(1, packet);
        } finally {
            FFmpegNative.av_packet_unref(packet);
        }
    }
    
    // 生成视频时间戳
    public long generateVideoTimestamp(int frameIndex, int frameRate) {
        return (long)(frameIndex * 1000000.0 / frameRate);
    }
    
    // 生成音频时间戳
    public long generateAudioTimestamp(int sampleIndex, int sampleRate) {
        return (long)(sampleIndex * 1000000.0 / sampleRate);
    }
    
    private long createPacket() {
        long packet = FFmpegNative.av_packet_alloc();
        FFmpegNative.av_init_packet(packet);
        return packet;
    }
}
```

### 2.4.3 使用滤镜加工音视频

#### 滤镜链构建

```java
// 滤镜链构建器
public class FilterChainBuilder {
    private List<FilterDefinition> filters;
    private String filterGraph;
    
    public FilterChainBuilder() {
        filters = new ArrayList<>();
    }
    
    // 添加缩放滤镜
    public FilterChainBuilder addScale(int width, int height) {
        FilterDefinition filter = new FilterDefinition();
        filter.name = "scale";
        filter.params = String.format("%d:%d", width, height);
        filters.add(filter);
        return this;
    }
    
    // 添加裁剪滤镜
    public FilterChainBuilder addCrop(int x, int y, int width, int height) {
        FilterDefinition filter = new FilterDefinition();
        filter.name = "crop";
        filter.params = String.format("%d:%d:%d:%d", width, height, x, y);
        filters.add(filter);
        return this;
    }
    
    // 添加旋转滤镜
    public FilterChainBuilder addRotate(double angle) {
        FilterDefinition filter = new FilterDefinition();
        filter.name = "rotate";
        filter.params = String.format("%.2f*PI/180", angle);
        filters.add(filter);
        return this;
    }
    
    // 添加亮度调节
    public FilterChainBuilder addBrightness(double brightness) {
        FilterDefinition filter = new FilterDefinition();
        filter.name = "eq";
        filter.params = String.format("brightness=%.2f", brightness);
        filters.add(filter);
        return this;
    }
    
    // 添加音量调节
    public FilterChainBuilder addVolume(double volume) {
        FilterDefinition filter = new FilterDefinition();
        filter.name = "volume";
        filter.params = String.format("%.2f", volume);
        filters.add(filter);
        return this;
    }
    
    // 构建滤镜图
    public String build() {
        if (filters.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filters.size(); i++) {
            FilterDefinition filter = filters.get(i);
            
            if (i > 0) {
                sb.append(",");
            }
            
            sb.append(filter.name);
            if (filter.params != null && !filter.params.isEmpty()) {
                sb.append("=").append(filter.params);
            }
        }
        
        filterGraph = sb.toString();
        return filterGraph;
    }
    
    // 滤镜定义类
    private static class FilterDefinition {
        String name;
        String params;
    }
}
```

#### 滤镜执行器

```java
// 滤镜执行器
public class FilterExecutor {
    private long filterGraph;
    private long[] filterContexts;
    private long[] sourceContexts;
    private long[] sinkContexts;
    
    public boolean initialize(String filterGraphDesc, int inputCount, int outputCount) {
        // 分配滤镜图
        filterGraph = FFmpegNative.avfilter_graph_alloc();
        if (filterGraph == 0) {
            return false;
        }
        
        // 解析滤镜图描述
        PointerPointer inputs = new PointerPointer(inputCount);
        PointerPointer outputs = new PointerPointer(outputCount);
        
        int result = FFmpegNative.avfilter_graph_parse2(
            filterGraph, filterGraphDesc, outputs, inputs);
        
        if (result < 0) {
            cleanup();
            return false;
        }
        
        // 配置滤镜图
        result = FFmpegNative.avfilter_graph_config(filterGraph, null);
        if (result < 0) {
            cleanup();
            return false;
        }
        
        // 获取输入输出上下文
        filterContexts = new long[inputCount + outputCount];
        sourceContexts = new long[inputCount];
        sinkContexts = new long[outputCount];
        
        // 这里需要根据实际情况获取滤镜上下文
        // 简化示例，实际实现需要更复杂的逻辑
        
        return true;
    }
    
    // 添加输入帧
    public boolean addInputFrame(long frame, int inputIndex) {
        if (inputIndex >= sourceContexts.length) {
            return false;
        }
        
        int result = FFmpegNative.av_buffersrc_add_frame_flags(
            sourceContexts[inputIndex], frame, 0);
        
        return result >= 0;
    }
    
    // 获取输出帧
    public boolean getOutputFrame(long frame, int outputIndex) {
        if (outputIndex >= sinkContexts.length) {
            return false;
        }
        
        int result = FFmpegNative.av_buffersink_get_frame(
            sinkContexts[outputIndex], frame);
        
        return result >= 0;
    }
    
    // 清理资源
    public void cleanup() {
        if (filterGraph != 0) {
            FFmpegNative.avfilter_graph_free(filterGraph);
            filterGraph = 0;
        }
        
        filterContexts = null;
        sourceContexts = null;
        sinkContexts = null;
    }
}
```

#### 实际滤镜应用示例

```java
// 滤镜应用示例
public class FilterApplication {
    
    // 视频处理流水线
    public void processVideo(String inputFile, String outputFile) {
        // 1. 打开输入文件
        MediaFileOpener opener = new MediaFileOpener();
        if (!opener.open(inputFile)) {
            System.err.println("Failed to open input file");
            return;
        }
        
        // 2. 创建输出文件
        MediaFileCreator creator = new MediaFileCreator();
        if (!creator.createFile(outputFile, "mp4")) {
            System.err.println("Failed to create output file");
            opener.close();
            return;
        }
        
        // 3. 构建滤镜链：缩放 -> 裁剪 -> 亮度调节
        String filterGraph = new FilterChainBuilder()
            .addScale(1280, 720)
            .addCrop(100, 100, 1080, 520)
            .addBrightness(0.1)
            .build();
        
        // 4. 初始化滤镜
        FilterExecutor filterExecutor = new FilterExecutor();
        if (!filterExecutor.initialize(filterGraph, 1, 1)) {
            System.err.println("Failed to initialize filter");
            opener.close();
            creator.close();
            return;
        }
        
        try {
            // 5. 处理每一帧
            processFrames(opener, creator, filterExecutor);
        } finally {
            // 6. 清理资源
            filterExecutor.cleanup();
            opener.close();
            creator.close();
        }
    }
    
    private void processFrames(MediaFileOpener opener, MediaFileCreator creator, 
                             FilterExecutor filterExecutor) {
        long formatContext = opener.getFormatContext();
        
        // 读取循环
        while (true) {
            long packet = FFmpegNative.av_packet_alloc();
            int result = FFmpegNative.av_read_frame(formatContext, packet);
            
            if (result < 0) {
                FFmpegNative.av_packet_unref(packet);
                break;
            }
            
            // 处理视频包
            int streamIndex = FFmpegNative.getPacketStreamIndex(packet);
            if (streamIndex == 0) { // 假设第一个流是视频
                // 解码帧
                long frame = decodeFrame(packet);
                if (frame != 0) {
                    // 应用滤镜
                    if (filterExecutor.addInputFrame(frame, 0)) {
                        long filteredFrame = FFmpegNative.av_frame_alloc();
                        if (filterExecutor.getOutputFrame(filteredFrame, 0)) {
                            // 编码并写入
                            encodeAndWriteFrame(filteredFrame, creator, 0);
                        }
                        FFmpegNative.av_frame_free(filteredFrame);
                    }
                    FFmpegNative.av_frame_free(frame);
                }
            }
            
            FFmpegNative.av_packet_unref(packet);
        }
        
        // 完成文件写入
        creator.finalizeOutput();
    }
    
    private long decodeFrame(long packet) {
        // 简化的解码实现
        // 实际需要使用完整的解码器
        return 0;
    }
    
    private void encodeAndWriteFrame(long frame, MediaFileCreator creator, int streamIndex) {
        // 简化的编码实现
        // 实际需要使用完整的编码器
    }
}
```

## 2.5 小结

本章深入介绍了 FFmpeg 开发的基础知识，为后续的音视频处理奠定了坚实的基础。

### 主要学习内容

1. **音视频编码标准**
   - 了解了视频和音频编码技术的发展历程
   - 掌握了主流编码标准的特点和应用场景
   - 认识了我国自主的 AVS 标准

2. **FFmpeg 数据结构**
   - 学习了 AVFormatContext、AVStream、AVPacket 等核心数据结构
   - 理解了 FFmpeg 的编码与封装机制
   - 掌握了数据包的创建、管理和时间戳处理

3. **音视频信息查看**
   - 学会了打开和关闭音视频文件
   - 掌握了获取基本信息和元数据的方法
   - 能够分析编解码器的详细参数

4. **常见处理流程**
   - 学习了编解码器参数的复制和修改
   - 掌握了音视频文件的创建和写入
   - 学会了使用滤镜进行音视频处理

### 实践要点

- **错误处理**：在 FFmpeg 开发中，正确处理错误和异常至关重要
- **资源管理**：及时释放分配的内存和资源，避免内存泄漏
- **时间戳同步**：正确处理时间戳是音视频同步的关键
- **滤镜使用**：灵活运用滤镜可以实现复杂的音视频效果

### 下一步

掌握了这些基础知识后，读者可以：
- 深入学习特定编解码器的使用
- 探索更复杂的滤镜组合
- 开发实际的音视频应用
- 学习流媒体传输技术

FFmpeg 是一个功能强大但复杂的框架，需要不断实践才能熟练掌握。建议读者在理解本章内容的基础上，多动手实践，加深对各个概念的理解。