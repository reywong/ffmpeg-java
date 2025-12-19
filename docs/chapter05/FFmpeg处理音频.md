# 第 5 章 FFmpeg处理音频

## 5.1 PCM音频

### 5.1.1 为什么要用PCM格式

PCM（Pulse Code Modulation，脉冲编码调制）是最基础的数字音频格式，具有以下特点：

**1. 原始音频数据**
- PCM是未经压缩的原始音频数据
- 直接存储音频采样的数字值
- 保持了音频的完整质量

**2. 广泛的兼容性**
- 几乎所有的音频处理工具都支持PCM
- 是其他音频格式的基础
- 便于音频算法的研究和开发

**3. 精确的时间同步**
- 每个采样点都有明确的时间位置
- 便于与视频帧进行精确同步
- 适合音频分析和处理

**FFmpeg处理PCM的基本命令：**
```bash
# 将其他格式转换为PCM
ffmpeg -i input.mp3 -f s16le -ar 44100 -ac 1 output.pcm

# 查看PCM文件信息
ffprobe -v quiet -print_format json -show_format -show_streams input.pcm

# 播放PCM文件
ffplay -f s16le -ar 44100 -ac 1 input.pcm
```

### 5.1.2 把音频流保存为PCM文件

**基础PCM保存：**
```bash
# 保存为16位有符号PCM
ffmpeg -i input.mp3 -f s16le -ar 44100 -ac 2 output.pcm

# 保存为32位浮点PCM
ffmpeg -i input.mp3 -f f32le -ar 48000 -ac 2 output.pcm

# 保存为8位无符号PCM
ffmpeg -i input.mp3 -f u8 -ar 22050 -ac 1 output.pcm
```

**从视频中提取PCM音频：**
```bash
# 提取视频中的音频为PCM
ffmpeg -i video.mp4 -vn -f s16le -ar 44100 -ac 2 audio.pcm

# 提取特定时间段的音频
ffmpeg -i video.mp4 -ss 00:01:00 -t 00:00:30 -vn -f s16le -ar 44100 -ac 2 audio.pcm

# 提取特定音频流（多音轨视频）
ffmpeg -i video.mp4 -map 0:a:1 -vn -f s16le -ar 44100 -ac 2 audio2.pcm
```

**批量PCM处理：**
```bash
# 将多个文件转换为PCM
for file in *.mp3; do ffmpeg -i "$file" -f s16le -ar 44100 -ac 2 "${file%.mp3}.pcm"; done

# 按目录批量转换
find input_dir -name "*.wav" -exec ffmpeg -i {} -f s16le -ar 44100 -ac 2 output_dir/{\}.pcm \;
```

### 5.1.3 PCM波形查看工具

**使用FFmpeg显示波形：**
```bash
# 显示音频波形
ffplay -f s16le -ar 44100 -ac 1 audio.pcm -showmode 1

# 显示频谱
ffplay -f s16le -ar 44100 -ac 1 audio.pcm -showmode 2

# 显示音频强度
ffplay -f s16le -ar 44100 -ac 1 audio.pcm -af "volumedetect"
```

**生成波形图像：**
```bash
# 生成波形图
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -filter_complex "showwavespic=s=1920x1080" waveform.png

# 生成频谱图
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -filter_complex "showspectrum=s=1920x1080" spectrum.png

# 生成CQT频谱（恒定Q变换）
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -filter_complex " showcqt=s=1920x1080" cqt.png
```

**音频分析工具：**
```bash
# 音频音量分析
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -af "volumedetect" -f null -

# 音频统计信息
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -af "astats=metadata=1:reset=1" -f null -

# 静音检测
ffmpeg -f s16le -ar 44100 -ac 1 -i audio.pcm -af "silencedetect=noise=-50dB:d=0.5" -f null -
```

## 5.2 MP3音频

### 5.2.1 为什么要用MP3格式

MP3是最流行的音频压缩格式，具有以下优势：

**1. 高压缩比**
- 通常能实现10:1到12:1的压缩比
- 在保持较好音质的同时大幅减少文件大小
- 适合网络传输和存储

**2. 广泛支持**
- 几乎所有设备和软件都支持MP3格式
- 是互联网音频的标准格式
- 兼容性极佳

**3. 可变比特率**
- 支持CBR（恒定比特率）和VBR（可变比特率）
- 可以在文件大小和音质之间找到平衡
- 支持不同的音质级别

**FFmpeg处理MP3的基本命令：**
```bash
# 设置MP3质量（0-9，0为最高质量）
ffmpeg -i input.wav -q:a 2 output.mp3

# 设置比特率
ffmpeg -i input.wav -b:a 192k output.mp3

# 设置可变比特率
ffmpeg -i input.wav -q:a 2 -vbr on output.mp3
```

### 5.2.2 Linux环境集成mp3lame

**安装mp3lame：**
```bash
# Ubuntu/Debian
sudo apt-get install libmp3lame-dev

# CentOS/RHEL
sudo yum install lame-devel

# Fedora
sudo dnf install lame-devel

# 源码编译
wget http://downloads.sourceforge.net/project/lame/lame/3.100/lame-3.100.tar.gz
tar -xzf lame-3.100.tar.gz
cd lame-3.100
./configure
make
sudo make install
```

**FFmpeg集成mp3lame：**
```bash
# 检查FFmpeg是否支持mp3lame
ffmpeg -codecs | grep mp3

# 如果不支持，需要重新编译FFmpeg
./configure --enable-libmp3lame
make
sudo make install
```

**验证安装：**
```bash
# 查看可用的mp3编码器
ffmpeg -encoders | grep mp3

# 测试mp3编码
ffmpeg -f lavfi -i anullsrc=r=44100:cl=stereo -t 5 -c:a libmp3lame -q:a 2 test.mp3
```

### 5.2.3 把音频流保存为MP3文件

**基础MP3保存：**
```bash
# 高质量MP3
ffmpeg -i input.wav -c:a libmp3lame -q:a 2 output.mp3

# 设置特定比特率
ffmpeg -i input.wav -c:a libmp3lame -b:a 192k output.mp3

# 立体声转单声道
ffmpeg -i input.wav -c:a libmp3lame -ac 1 -q:a 2 output_mono.mp3
```

**高级MP3选项：**
```bash
# 设置编码预设
ffmpeg -i input.wav -c:a libmp3lame -preset medium output.mp3

# 设置VBR模式
ffmpeg -i input.wav -c:a libmp3lame -q:a 0 -vbr on output_vbr.mp3

# 设置ABR模式（平均比特率）
ffmpeg -i input.wav -c:a libmp3lame -b:a 128k -abr 1 output_abr.mp3
```

**从视频提取MP3：**
```bash
# 提取视频中的音频为MP3
ffmpeg -i video.mp4 -vn -c:a libmp3lame -q:a 2 audio.mp3

# 批量提取视频音频
for file in *.mp4; do ffmpeg -i "$file" -vn -c:a libmp3lame -q:a 2 "${file%.mp4}.mp3"; done
```

## 5.3 其他音频格式

### 5.3.1 把音频流保存为WAV文件

WAV是无压缩的音频格式，适合音频编辑和处理：

**基础WAV保存：**
```bash
# 保存为标准WAV格式
ffmpeg -i input.mp3 output.wav

# 设置WAV参数
ffmpeg -i input.mp3 -ar 44100 -ac 2 -sample_fmt s16 output.wav

# 保存为32位浮点WAV
ffmpeg -i input.mp3 -ar 48000 -ac 2 -sample_fmt f32le output_32bit.wav
```

**高质量WAV：**
```bash
# 保存为24位WAV
ffmpeg -i input.flac -ar 96000 -ac 2 -sample_fmt s24le output_24bit.wav

# 保存为多声道WAV
ffmpeg -i input.flac -ar 48000 -ac 6 output_5dot1.wav

# 保持原始质量
ffmpeg -i input.wav -c:a pcm_s16le -ar 48000 output_copy.wav
```

**WAV格式处理：**
```bash
# 批量转换为WAV
for file in *.mp3; do ffmpeg -i "$file" "${file%.mp3}.wav"; done

# WAV音量标准化
ffmpeg -i input.wav -af "loudnorm=I=-16:TP=-1.5:LRA=11" output_norm.wav

# WAV去噪
ffmpeg -i input.wav -af "afftdn=nf=-25" output_denoised.wav
```

### 5.3.2 把音频流保存为AAC文件

AAC是更现代的音频编码格式，音质更好：

**基础AAC保存：**
```bash
# 高质量AAC
ffmpeg -i input.wav -c:a aac -b:a 256k output.aac

# 标准AAC（LC）
ffmpeg -i input.wav -c:a aac -profile:a aac_low -b:a 128k output.aac

# HE-AAC（高效AAC）
ffmpeg -i input.wav -c:a libfdk_aac -profile:a aac_he -b:a 64k output.aac
```

**AAC高级选项：**
```bash
# 设置AAC编码器
ffmpeg -i input.wav -c:a libfdk_aac -b:a 192k output.aac

# 设置采样率
ffmpeg -i input.wav -c:a aac -ar 48000 -b:a 128k output.aac

# 多声道AAC
ffmpeg -i input.wav -c:a aac -ac 6 -b:a 384k output_5dot1.aac
```

### 5.3.3 音频重采样

**基础重采样：**
```bash
# 16kHz重采样
ffmpeg -i input.wav -ar 16000 output_16k.wav

# 8kHz重采样
ffmpeg -i input.wav -ar 8000 output_8k.wav

# 48kHz重采样
ffmpeg -i input.wav -ar 48000 output_48k.wav
```

**高质量重采样：**
```bash
# 使用高质量重采样器
ffmpeg -i input.wav -af aresample=out_sample_rate=48000:resampler=soxr output_48k_hq.wav

# 设置重采样参数
ffmpeg -i input.wav -af aresample=48000:osrflt=2:tsf=2:tsr=1 output.wav

# 多通道重采样
ffmpeg -i input.wav -af aresample=48000:out_channel_layout=stereo output_stereo.wav
```

**声道转换：**
```bash
# 立体声转单声道
ffmpeg -i input.wav -ac 1 output_mono.wav

# 单声道转立体声
ffmpeg -i input.wav -ac 2 output_stereo.wav

# 5.1声道转立体声
ffmpeg -i input.wav -af "pan=stereo|FL<FL+0.707*FC+0.707*LFE|FR<FR+0.707*FC+0.707*LFE" output_stereo.wav
```

## 5.4 实战项目：拼接两段音频

这个项目将展示如何将两段音频无缝拼接在一起。

**项目需求：**
- 支持不同格式的音频文件
- 自动处理采样率和声道数不一致
- 支持淡入淡出过渡效果
- 输出高质量的音频文件

**实现步骤：**

**1. 音频预处理：**
```bash
# 统一音频参数
ffmpeg -i audio1.mp3 -ar 44100 -ac 2 -c:a pcm_s16le temp1.wav
ffmpeg -i audio2.mp3 -ar 44100 -ac 2 -c:a pcm_s16le temp2.wav
```

**2. 简单拼接：**
```bash
# 使用concat拼接
echo "file 'temp1.wav'" > concat.txt
echo "file 'temp2.wav'" >> concat.txt
ffmpeg -f concat -safe 0 -i concat.txt -c copy output.wav
```

**3. 淡入淡出拼接：**
```bash
# 添加淡出效果到第一段音频
ffmpeg -i temp1.wav -af "afade=t=out:st=4:d=1" temp1_fade.wav

# 添加淡入效果到第二段音频
ffmpeg -i temp2.wav -af "afade=t=in:st=0:d=1" temp2_fade.wav

# 拼接处理后的音频
ffmpeg -i "concat:temp1_fade.wav|temp2_fade.wav" -c copy output_fade.wav
```

**4. 交叉淡化拼接：**
```bash
# 创建交叉淡化效果
ffmpeg -i temp1.wav -i temp2.wav -filter_complex \
"[0:a][1:a]acrossfade=d=1:c1=tri:c2=tri[audio]" \
-map "[audio]" output_crossfade.wav
```

**完整的批处理脚本：**
```bash
#!/bin/bash
# 音频拼接脚本

if [ $# -ne 3 ]; then
    echo "用法: $0 <音频1> <音频2> <输出文件>"
    exit 1
fi

AUDIO1=$1
AUDIO2=$2
OUTPUT=$3

# 创建临时目录
TEMP_DIR="temp_audio"
mkdir -p $TEMP_DIR

# 统一音频参数
echo "预处理音频文件..."
ffmpeg -i "$AUDIO1" -ar 44100 -ac 2 -c:a pcm_s16le "$TEMP_DIR/temp1.wav" -y
ffmpeg -i "$AUDIO2" -ar 44100 -ac 2 -c:a pcm_s16le "$TEMP_DIR/temp2.wav" -y

# 获取音频时长
DURATION1=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$TEMP_DIR/temp1.wav")

# 创建淡出效果
echo "添加淡化效果..."
ffmpeg -i "$TEMP_DIR/temp1.wav" -af "afade=t=out:st=$(echo "$DURATION1-1" | bc):d=1" "$TEMP_DIR/temp1_fade.wav" -y

# 创建淡入效果
ffmpeg -i "$TEMP_DIR/temp2.wav" -af "afade=t=in:st=0:d=1" "$TEMP_DIR/temp2_fade.wav" -y

# 拼接音频
echo "拼接音频..."
ffmpeg -i "concat:$TEMP_DIR/temp1_fade.wav|$TEMP_DIR/temp2_fade.wav" -c copy "$OUTPUT" -y

# 清理临时文件
rm -rf $TEMP_DIR

echo "音频拼接完成: $OUTPUT"
```

**Java实现：**
```java
public static void concatenateAudioWithCrossfade(String audio1, String audio2, String output) {
    try {
        String command = String.format(
            "ffmpeg -i %s -i %s -filter_complex \"[0:a][1:a]acrossfade=d=1:c1=tri:c2=tri[audio]\" -map \"[audio]\" %s",
            audio1, audio2, output);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("音频拼接成功: " + output);
        } else {
            System.err.println("音频拼接失败");
        }
    } catch (Exception e) {
        System.err.println("音频拼接异常: " + e.getMessage());
    }
}
```

## 5.5 小结

本章详细介绍了FFmpeg处理各种音频格式的方法：

**1. PCM音频处理**
- 理解PCM格式的特点和应用场景
- 掌握音频流保存为PCM的方法
- 学会使用PCM波形查看和分析工具

**2. MP3音频处理**
- 了解MP3格式的优势和配置选项
- 掌握Linux环境下mp3lame的集成
- 学会音频流保存为MP3文件的技巧

**3. 其他音频格式**
- WAV无压缩格式的处理方法
- AAC现代音频格式的应用
- 音频重采样和声道转换技术

**4. 实战项目**
- 音频拼接的完整实现流程
- 淡入淡出效果的添加方法
- 交叉淡化技术的应用

通过本章的学习，读者应该能够熟练使用FFmpeg处理各种音频格式，掌握音频转换、编辑和处理的核心技能。这些知识在音频编辑、音乐制作和多媒体应用开发中都非常重要。