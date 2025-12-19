# 第 3 章 FFmpeg的编解码

## 3.1 音视频时间

### 3.1.1 帧率和采样率

#### 帧率（Frame Rate）

帧率是指视频每秒显示的帧数，单位是fps（frames per second）。常见的帧率有：

- 24fps：电影标准帧率
- 25fps：PAL制式电视标准
- 30fps：NTSC制式电视标准
- 60fps：高清视频和游戏视频

```bash
# 查看视频的帧率
ffprobe -v quiet -select_streams v:0 -show_entries stream=r_frame_rate -of csv=p=0 input.mp4

# 设置视频帧率
ffmpeg -i input.mp4 -r 30 output.mp4

# 使用滤镜调整帧率
ffmpeg -i input.mp4 -vf "fps=25" output.mp4
```

#### 采样率（Sample Rate）

采样率是指音频每秒采样的次数，单位是Hz。常见的采样率有：

- 8000Hz：电话通话质量
- 22050Hz：收音机质量
- 44100Hz：CD音质
- 48000Hz：DVD和专业音频
- 96000Hz：高清音频

```bash
# 查看音频的采样率
ffprobe -v quiet -select_streams a:0 -show_entries stream=sample_rate -of csv=p=0 input.mp4

# 设置音频采样率
ffmpeg -i input.mp4 -ar 44100 output.mp4

# 使用滤镜调整采样率
ffmpeg -i input.mp4 -af "aresample=48000" output.mp4
```

#### 帧率和采样率的关系

帧率和采样率是音视频同步的基础。在处理音视频时，需要确保音频和视频的时间戳能够正确对应。

```bash
# 同时设置视频帧率和音频采样率
ffmpeg -i input.mp4 -r 30 -ar 44100 output.mp4

# 查看音视频的详细时间信息
ffprobe -v quiet -show_entries stream=time_base,r_frame_rate,sample_rate -of csv=p=0 input.mp4
```

### 3.1.2 时间基准的设定

时间基准（Time Base）是FFmpeg中用于计算时间戳的基本单位。不同的流可能有不同的时间基准。

#### 时间基准的概念

时间基准表示时间戳的单位，例如：
- 时间基准为1/1000时，时间戳1000表示1秒
- 时间基准为1/90000时，时间戳90000表示1秒

```bash
# 查看时间基准
ffprobe -v quiet -select_streams v:0 -show_entries stream=time_base -of csv=p=0 input.mp4

# 查看所有流的时间基准
ffprobe -v quiet -show_entries stream=codec_type,time_base -of csv=p=0 input.mp4
```

#### 时间戳转换

FFmpeg提供工具来转换不同时间基准之间的时间戳：

```bash
# 使用ffprobe转换时间戳
ffprobe -v error -select_streams v:0 -show_entries packet=pts_time -of csv=p=0 input.mp4

# 计算特定时间戳对应的秒数
# 公式：时间(秒) = 时间戳 / 时间基准
# 例如：时间基准1/90000，时间戳450000 = 450000/90000 = 5秒
```

#### 时间基准设置

```bash
# 设置视频的时间基准
ffmpeg -i input.mp4 -video_track_timescale 90000 output.mp4

# 设置音频的时间基准
ffmpeg -i input.mp4 -audio_packet_rate 90000 output.mp4

# 使用滤镜设置时间基准
ffmpeg -i input.mp4 -vf "settb=1/1000" output.mp4
```

### 3.1.3 时间戳的计算

时间戳（PTS/DTS）是音视频同步的关键。PTS（Presentation Time Stamp）表示显示时间戳，DTS（Decoding Time Stamp）表示解码时间戳。

#### 时间戳查看

```bash
# 查看视频包的时间戳
ffprobe -v error -select_streams v:0 -show_entries packet=pts,pts_time,dts,dts_time -of csv=p=0 input.mp4 | head -20

# 查看音频包的时间戳
ffprobe -v error -select_streams a:0 -show_entries packet=pts,pts_time,dts,dts_time -of csv=p=0 input.mp4 | head -20

# 查看帧的时间戳
ffprobe -v error -select_streams v:0 -show_entries frame=pts,pkt_pts_time,best_effort_timestamp_time -of csv=p=0 input.mp4 | head -10
```

#### 时间戳计算示例

```bash
# 计算视频的总时长
ffmpeg -i input.mp4 2>&1 | grep "Duration"

# 精确计算视频帧数
ffprobe -v quiet -select_streams v:0 -count_frames -show_entries frame=n_pts -of csv=p=0 input.mp4 | wc -l

# 计算平均帧率
ffprobe -v quiet -select_streams v:0 -show_entries stream=avg_frame_rate -of csv=p=0 input.mp4
```

#### 时间戳操作

```bash
# 重新计算时间戳
ffmpeg -i input.mp4 -fflags +genpts output.mp4

# 修复时间戳问题
ffmpeg -i input.mp4 -vf "setpts=0.5*PTS" output.mp4  # 2倍速播放
ffmpeg -i input.mp4 -vf "setpts=2.0*PTS" output.mp4   # 0.5倍速播放

# 音频时间戳调整
ffmpeg -i input.mp3 -af "atempo=2.0" output.mp3       # 2倍速
ffmpeg -i input.mp3 -af "atempo=0.5" output.mp3       # 0.5倍速
```

## 3.2 分离音视频

### 3.2.1 原样复制视频文件

原样复制（Stream Copy）是指不重新编码，直接复制音视频流到新文件中。这种方式速度快且不损失质量。

#### 视频流复制

```bash
# 复制视频流到新文件
ffmpeg -i input.mp4 -c:v copy -an video_only.mp4

# 复制音频流到新文件
ffmpeg -i input.mp4 -c:a copy -vn audio_only.mp4

# 同时复制视频和音频流
ffmpeg -i input.mp4 -c copy output.mp4
```

#### 选择特定流

```bash
# 选择第一个视频流
ffmpeg -i input.mp4 -map 0:v:0 -c copy output.mp4

# 选择第一个音频流
ffmpeg -i input.mp4 -map 0:a:0 -c copy output.mp4

# 选择特定语言音轨
ffmpeg -i input.mkv -map 0:a:m:language:eng -c copy audio_eng.mp4
```

#### 流信息查看

```bash
# 查看所有流的信息
ffprobe -v quiet -show_entries stream=index,codec_name,codec_type,language -of csv=p=0 input.mp4

# 详细查看流信息
ffprobe -v quiet -show_streams input.mp4

# 查看容器格式信息
ffprobe -v quiet -show_format input.mp4
```

### 3.2.2 从视频文件剥离音频流

从视频文件中提取音频流是常见的操作，可以根据需要选择不同的音频格式。

#### 提取为MP3

```bash
# 提取音频为MP3格式
ffmpeg -i input.mp4 -q:a 2 audio.mp3

# 指定比特率
ffmpeg -i input.mp4 -b:a 192k audio.mp3

# 使用高质量设置
ffmpeg -i input.mp4 -codec:a libmp3lame -q:a 0 audio.mp3
```

#### 提取为AAC

```bash
# 提取音频为AAC格式
ffmpeg -i input.mp4 -c:a aac audio.aac

# 指定AAC编码器
ffmpeg -i input.mp4 -c:a libfdk_aac -b:a 128k audio.aac

# 使用默认AAC设置
ffmpeg -i input.mp4 -c:a aac -ar 44100 audio.aac
```

#### 提取为WAV

```bash
# 提取为无损WAV格式
ffmpeg -i input.mp4 audio.wav

# 指定采样率和位深度
ffmpeg -i input.mp4 -ar 48000 -sample_fmt s16 audio.wav

# 提取为PCM原始数据
ffmpeg -i input.mp4 -c:a pcm_s16le audio.pcm
```

### 3.2.3 切割视频文件

视频切割是指从视频文件中提取特定时间段的内容。

#### 按时间切割

```bash
# 从10秒开始，持续30秒
ffmpeg -i input.mp4 -ss 10 -t 30 output.mp4

# 从开始到30秒
ffmpeg -i input.mp4 -t 30 output.mp4

# 从1分钟开始到结束
ffmpeg -i input.mp4 -ss 01:00 output.mp4
```

#### 精确切割

```bash
# 使用更精确的时间戳
ffmpeg -ss 00:01:30 -i input.mp4 -t 00:00:30 -c copy output.mp4

# 分段切割视频
ffmpeg -i input.mp4 -f segment -segment_time 60 -c copy output_%03d.mp4

# 按关键帧切割
ffmpeg -i input.mp4 -f segment -segment_time 30 -segment_time_delta 0.01 -c copy output_%03d.mp4
```

#### 批量切割

```bash
# 使用批处理脚本切割多个片段
for i in {0..5}; do
    ffmpeg -i input.mp4 -ss $((i*60)) -t 60 -c copy segment_$i.mp4
done

# 使用文件列表切割
while IFS= read -r line; do
    ffmpeg -i input.mp4 -ss "$line" -t 30 -c copy "clip_$line.mp4"
done < timestamps.txt
```

## 3.3 合并音视频

### 3.3.1 合并视频流和音频流

将分别的视频和音频文件合并为一个完整的音视频文件。

#### 简单合并

```bash
# 合并视频和音频文件
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac output.mp4

# 指定音频编码参数
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac -b:a 192k output.mp4

# 处理时长不匹配的情况
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac -shortest output.mp4
```

#### 处理音视频同步

```bash
# 音频延迟2秒
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac -af "adelay=2000|2000" output.mp4

# 视频延迟2秒
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac -vf "setpts=PTS+2/TB" output.mp4

# 调整音视频同步
ffmpeg -i video.mp4 -i audio.mp3 -c:v copy -c:a aac -async 1 output.mp4
```

#### 多音频轨合并

```bash
# 添加第二个音频轨
ffmpeg -i video.mp4 -i audio1.mp3 -i audio2.mp3 -map 0:v -map 1:a -map 2:a -c:v copy -c:a aac output.mkv

# 混合两个音频轨
ffmpeg -i video.mp4 -i audio1.mp3 -i audio2.mp3 -filter_complex "[1:a][2:a]amix=inputs=2[a]" -map 0:v -map "[a]" -c:v copy -c:a aac output.mp4
```

### 3.3.2 对视频流重新编码

重新编码视频流以改变格式、质量或大小。

#### 格式转换

```bash
# MP4转AVI
ffmpeg -i input.mp4 -c:v libxvid -c:a mp3 output.avi

# MP4转MOV
ffmpeg -i input.mp4 -c:v libx264 -c:a aac output.mov

# 转换为WebM
ffmpeg -i input.mp4 -c:v libvpx -c:a libvorbis output.webm
```

#### 质量调整

```bash
# 高质量编码
ffmpeg -i input.mp4 -c:v libx264 -crf 18 -preset slow output.mp4

# 中等质量
ffmpeg -i input.mp4 -c:v libx264 -crf 23 -preset medium output.mp4

# 低质量（小文件）
ffmpeg -i input.mp4 -c:v libx264 -crf 28 -preset fast output.mp4
```

#### 分辨率调整

```bash
# 调整分辨率
ffmpeg -i input.mp4 -vf "scale=1280:720" -c:a copy output.mp4

# 保持宽高比
ffmpeg -i input.mp4 -vf "scale=1280:-1" -c:a copy output.mp4

# 多种分辨率输出
ffmpeg -i input.mp4 -vf "scale=1920:1080" -c:v libx264 -b:v 5M output_1080p.mp4 \
       -vf "scale=1280:720" -c:v libx264 -b:v 2.5M output_720p.mp4 \
       -vf "scale=854:480" -c:v libx264 -b:v 1M output_480p.mp4
```

### 3.3.3 合并两个视频文件

将多个视频文件合并为一个连续的视频。

#### 使用concat协议

```bash
# 创建文件列表
echo "file 'video1.mp4'" > filelist.txt
echo "file 'video2.mp4'" >> filelist.txt
echo "file 'video3.mp4'" >> filelist.txt

# 使用concat协议合并
ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4

# 重新编码合并（不同格式时）
ffmpeg -f concat -safe 0 -i filelist.txt -c:v libx264 -c:a aac output.mp4
```

#### 使用concat滤镜

```bash
# 使用滤镜合并视频
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[outv][outa]" -map "[outv]" -map "[outa]" output.mp4

# 合并不同分辨率的视频
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v]scale=1920:1080[v0];[1:v]scale=1920:1080[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1[outv][outa]" -map "[outv]" -map "[outa]" output.mp4
```

#### 添加转场效果

```bash
# 添加淡入淡出转场
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v]fade=t=out:st=4:d=1[v1];[1:v]fade=t=in:st=0:d=1[v2];[v1][v2]concat=n=2:v=1:a=0[outv]" -map "[outv]" -c:v libx264 output.mp4

# 添加交叉淡变效果
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v][1:v]xfade=transition=fade:duration=1:offset=4[outv]" -map "[outv]" output.mp4
```

## 3.4 视频浏览与格式分析

### 3.4.1 通用音视频播放器

使用FFmpeg可以创建功能强大的音视频播放器。

#### 基本播放器

```bash
# 使用ffplay播放视频
ffplay input.mp4

# 播放特定时间段
ffplay -ss 10 -t 30 input.mp4

# 循环播放
ffplay -loop 0 input.mp4

# 无声播放
ffplay -an input.mp4

# 仅播放音频
ffplay -vn input.mp3
```

#### 播放器选项

```bash
# 设置播放窗口大小
ffplay -x 800 -y 600 input.mp4

# 强制显示比例
ffplay -aspect 16:9 input.mp4

# 音量控制
ffplay -volume 50 input.mp3

# 播放速度控制
ffplay -vf "setpts=0.5*PTS" input.mp4  # 2倍速
ffplay -af "atempo=0.5" input.mp4      # 0.5倍速
```

#### 播放器滤镜

```bash
# 实时添加滤镜
ffplay -vf "eq=brightness=0.1" input.mp4

# 实时缩放
ffplay -vf "scale=640:480" input.mp4

# 实时旋转
ffplay -vf "transpose=1" input.mp4
```

### 3.4.2 视频格式分析工具

使用ffprobe详细分析视频文件的格式和内容。

#### 基本信息

```bash
# 显示基本信息
ffprobe input.mp4

# 显示容器格式
ffprobe -v quiet -show_format input.mp4

# 显示流信息
ffprobe -v quiet -show_streams input.mp4

# 显示数据包信息
ffprobe -v quiet -show_packets input.mp4 | head -20
```

#### 详细分析

```bash
# 分析视频帧
ffprobe -v quiet -select_streams v:0 -show_frames input.mp4 | head -10

# 分析音频帧
ffprobe -v quiet -select_streams a:0 -show_frames input.mp3 | head -10

# 显示编解码器参数
ffprobe -v quiet -select_streams v:0 -show_entries stream=codec_name,width,height,bit_rate -of csv=p=0 input.mp4
```

#### 统计信息

```bash
# 计算帧率
ffprobe -v quiet -select_streams v:0 -show_entries stream=r_frame_rate -of csv=p=0 input.mp4

# 计算比特率
ffprobe -v quiet -show_entries format=bit_rate -of csv=p=0 input.mp4

# 计算总帧数
ffprobe -v quiet -select_streams v:0 -count_frames -show_entries stream=nb_frames -of csv=p=0 input.mp4
```

### 3.4.3 把原始的H264文件封装为MP4格式

将裸的H.264数据流封装到MP4容器中。

#### 基本封装

```bash
# 将H.264裸流封装为MP4
ffmpeg -i input.h264 -c:v copy output.mp4

# 添加音频流
ffmpeg -i input.h264 -i audio.mp3 -c:v copy -c:a copy output.mp4

# 设置帧率
ffmpeg -i input.h264 -r 30 -c:v copy output.mp4
```

#### 处理时间戳

```bash
# 为H.264流添加时间戳
ffmpeg -f h264 -i input.h264 -c:v copy -r 25 output.mp4

# 使用固定帧率
ffmpeg -fflags +genpts -i input.h264 -c:v copy -r 30 output.mp4

# 从文件读取帧率
ffmpeg -r 25 -i input.h264 -c:v copy output.mp4
```

#### 高级封装选项

```bash
# 设置像素格式
ffmpeg -i input.h264 -pix_fmt yuv420p -c:v copy output.mp4

# 设置色彩空间
ffmpeg -i input.h264 -colorspace bt709 -c:v copy output.mp4

# 添加元数据
ffmpeg -i input.h264 -c:v copy -metadata title="My Video" output.mp4
```

## 3.5 小结

本章详细介绍了FFmpeg编解码的核心概念和实践技巧：

1. **音视频时间**：理解帧率、采样率、时间基准和时间戳的概念和应用
2. **分离音视频**：掌握原样复制、音频提取和视频切割的技术
3. **合并音视频**：学习音视频合并、重新编码和多文件合并的方法
4. **格式分析**：使用播放器和分析工具深入了解视频格式

这些基础技能是后续章节学习的前提，熟练掌握后将能够处理各种音视频编解码任务。