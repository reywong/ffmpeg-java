# 第 6 章 FFmpeg加工视频

## 6.1 滤波加工

### 6.1.1 简单的视频滤镜

FFmpeg提供了丰富的视频滤镜，可以对视频进行各种加工处理。基础滤镜包括：

**色彩调整滤镜：**
```bash
# 亮度调整
ffmpeg -i input.mp4 -vf "eq=brightness=0.2" output.mp4

# 对比度调整
ffmpeg -i input.mp4 -vf "eq=contrast=2" output.mp4

# 饱和度调整
ffmpeg -i input.mp4 -vf "eq=saturation=2" output.mp4

# 色调调整
ffmpeg -i input.mp4 -vf "eq=gamma=2:contrast=1.5:brightness=0.1" output.mp4
```

**图像变换滤镜：**
```bash
# 水平翻转
ffmpeg -i input.mp4 -vf "hflip" output.mp4

# 垂直翻转
ffmpeg -i input.mp4 -vf "vflip" output.mp4

# 旋转90度
ffmpeg -i input.mp4 -vf "transpose=1" output.mp4

# 旋转180度
ffmpeg -i input.mp4 -vf "transpose=2,transpose=2" output.mp4
```

**模糊和锐化滤镜：**
```bash
# 高斯模糊
ffmpeg -i input.mp4 -vf "gblur=sigma=2" output.mp4

# 盒子模糊
ffmpeg -i input.mp4 -vf "boxblur=luma_radius=2:luma_power=2" output.mp4

# 锐化
ffmpeg -i input.mp4 -vf "unsharp=5:5:1.0:5:5:0.0" output.mp4

# 边缘检测
ffmpeg -i input.mp4 -vf "edgedetect=low=0.1:high=0.4" output.mp4
```

**色彩空间转换：**
```bash
# 转换为灰度
ffmpeg -i input.mp4 -vf "colorchannelmixer=rr=0.3:rg=0.59:rb=0.11:gr=0.3:gg=0.59:gb=0.11:br=0.3:bg=0.59:bb=0.11" output.mp4

# 简单灰度转换
ffmpeg -i input.mp4 -vf "hue=s=0" output.mp4

# 色彩反转
ffmpeg -i input.mp4 -vf "negate" output.mp4

# 褐色调效果
ffmpeg -i input.mp4 -vf "colorchannelmixer=.393:.769:.189:.349:.686:.168:.272:.534:.131" output.mp4
```

### 6.1.2 简单的音频滤镜

音频滤镜主要用于音频处理和增强：

**音量控制滤镜：**
```bash
# 音量放大
ffmpeg -i input.mp4 -af "volume=2.0" output.mp4

# 音量减小
ffmpeg -i input.mp4 -af "volume=0.5" output.mp4

# 分贝调整
ffmpeg -i input.mp4 -af "volume=10dB" output.mp4

# 音量标准化
ffmpeg -i input.mp4 -af "loudnorm=I=-16:TP=-1.5:LRA=11" output.mp4
```

**均衡器滤镜：**
```bash
# 三段均衡器
ffmpeg -i input.mp4 -af "equalizer=f=1000:width_type=h:width=100:g=5" output.mp4

# 多段均衡器
ffmpeg -i input.mp4 -af "equalizer=f=100:width_type=o:width=2:g=2,equalizer=f=1000:width_type=o:width=2:g=-2" output.mp4

# 低音增强
ffmpeg -i input.mp4 -af "bass=g=10" output.mp4

# 高音增强
ffmpeg -i input.mp4 -af "treble=g=5" output.mp4
```

**音效处理滤镜：**
```bash
# 回声效果
ffmpeg -i input.mp4 -af "aecho=0.8:0.9:1000:0.3" output.mp4

# 混响效果
ffmpeg -i input.mp4 -af "aecho=0.6:0.3:1000:0.5" output.mp4

# 颤音效果
ffmpeg -i input.mp4 -af "tremolo=f=5:d=0.5" output.mp4

# 变声效果
ffmpeg -i input.mp4 -af "rubberband=pitch=1.5" output.mp4
```

**降噪处理：**
```bash
# 基础降噪
ffmpeg -i input.mp4 -af "afftdn=nf=-25" output.mp4

# 高质量降噪
ffmpeg -i input.mp4 -af "afftdn=nf=-25:nt=w" output.mp4

# 门限降噪
ffmpeg -i input.mp4 -af "agate=threshold=-40dB:ratio=4" output.mp4

# 压缩器
ffmpeg -i input.mp4 -af "acompressor=threshold=-20dB:ratio=4:attack=5:release=50" output.mp4
```

### 6.1.3 利用滤镜切割视频

使用滤镜可以实现精确的视频切割：

**时间切割：**
```bash
# 截取前10秒
ffmpeg -i input.mp4 -t 10 -c copy output.mp4

# 从30秒开始截取15秒
ffmpeg -i input.mp4 -ss 30 -t 15 -c copy output.mp4

# 使用滤镜精确切割
ffmpeg -i input.mp4 -vf "trim=start=30:end=45" -af "atrim=start=30:end=45" output.mp4

# 多段切割
ffmpeg -i input.mp4 -filter_complex "[0:v]trim=10:20,setpts=PTS-STARTPTS[v1];[0:a]atrim=10:20,asetpts=PTS-STARTPTS[a1];[0:v]trim=30:40,setpts=PTS-STARTPTS[v2];[0:a]atrim=30:40,asetpts=PTS-STARTPTS[a2];[v1][v2]concat=n=2[v];[a1][a2]concat=n=2:a=1[a]" -map "[v]" -map "[a]" output.mp4
```

**场景切割：**
```bash
# 检测场景变化
ffmpeg -i input.mp4 -vf "select='gt(scene,0.4)',showinfo" -f null -

# 场景切割输出
ffmpeg -i input.mp4 -vf "select='gt(scene,0.4)',showinfo" -vsync vfr scene_%04d.jpg

# 基于场景的视频分段
ffmpeg -i input.mp4 -filter_complex "select='gt(scene,0.4)',setpts=N/FRAME_RATE/TB" -f segment -segment_time 10 -reset_timestamps 1 output_%03d.mp4
```

**智能切割：**
```bash
# 静音检测切割
ffmpeg -i input.mp4 -af "silencedetect=noise=-30dB:d=2" -f null -

# 基于静音检测切割
ffmpeg -i input.mp4 -filter_complex "[0:a]silencedetect=noise=-30dB:d=2[silence]" -map "[silence]" -f null -

# 自动分割静音段
ffmpeg -i input.mp4 -filter_complex "[0:v]select='not(mod(n\,30))',setpts=N/FRAME_RATE/TB[v];[0:a]aselect='not(mod(n\,30))',asetpts=N/SR/TB[a]" -map "[v]" -map "[a]" output.mp4
```

### 6.1.4 给视频添加方格

方格效果常用于视频制作和设计：

**基础方格：**
```bash
# 添加方格背景
ffmpeg -i input.mp4 -vf "drawgrid=width=100:height=100:thickness=2:color=red@0.5" output.mp4

# 添加自定义方格
ffmpeg -i input.mp4 -vf "drawgrid=x=iw/2-100:y=ih/2-100:width=200:height=200:thickness=3:color=blue" output.mp4

# 虚线方格
ffmpeg -i input.mp4 -vf "drawgrid=width=50:height=50:thickness=1:color=black@0.3" output.mp4
```

**高级方格效果：**
```bash
# 动态方格
ffmpeg -i input.mp4 -vf "drawgrid=width=50+sin(t)*10:height=50+cos(t)*10:thickness=2:color=red" output.mp4

# 彩虹方格
ffmpeg -i input.mp4 -vf "drawgrid=width=80:height=80:thickness=2:color=HUE(sin(t*2))" output.mp4

# 渐变方格
ffmpeg -i input.mp4 -vf "drawgrid=width=120:height=120:thickness=3:color=0xFF0000@0.3:replace=0x000000@0.1" output.mp4
```

**专业方格效果：**
```bash
# 三分法则网格
ffmpeg -i input.mp4 -vf "drawgrid=width=iw/3:height=ih/3:thickness=1:color=white@0.5" output.mp4

# 黄金比例网格
ffmpeg -i input.mp4 -vf "drawgrid=width=iw/1.618:height=ih/1.618:thickness=2:color=yellow@0.3" output.mp4

# 九宫格
ffmpeg -i input.mp4 -vf "drawgrid=width=iw/3:height=ih/3:thickness=1:color=black@0.2" output.mp4
```

## 6.2 添加特效

### 6.2.1 转换图像色度坐标

色度坐标转换用于颜色空间处理：

**RGB转换：**
```bash
# RGB到HSV转换
ffmpeg -i input.mp4 -vf "colorspace=rgb:hsv" output.mp4

# RGB到YUV转换
ffmpeg -i input.mp4 -vf "colorspace=rgb:yuv" output.mp4

# RGB到LAB转换
ffmpeg -i input.mp4 -vf "colorspace=rgb:lab" output.mp4
```

**色彩空间优化：**
```bash
# BT.601到BT.709转换
ffmpeg -i input.mp4 -vf "colorspace=all=bt709:iall=bt601:fast=1" output.mp4

# SDR到HDR转换
ffmpeg -i input.mp4 -vf "zscale=transfer=linear,tonemap=tonemap=hable:desat=0,zscale=transfer=bt709" output.mp4

# 色彩范围转换
ffmpeg -i input.mp4 -vf "colorspace=range=pc:range=tv" output.mp4
```

**色彩校正：**
```bash
# 白平衡校正
ffmpeg -i input.mp4 -vf "colormatrix=src=bt709:dst=bt601" output.mp4

# 色温调整
ffmpeg -i input.mp4 -vf "colortemperature=6500" output.mp4

# 色彩平衡
ffmpeg -i input.mp4 -vf "colorbalance=rs=0.1:gs=0.2:bs=0.3" output.mp4
```

### 6.2.2 添加色彩转换特效

色彩特效创造独特的视觉效果：

**渐变效果：**
```bash
# 线性渐变背景
ffmpeg -f lavfi -i color=c=red:size=1920x1080 -i input.mp4 -filter_complex "[0:v][1:v]overlay=x=W-w-10:y=H-h-10" output.mp4

# 径向渐变
ffmpeg -i input.mp4 -vf "geq=lum='p(X,Y)':cb='128+128*cos(2*PI*(X/W-0.5))*sin(2*PI*(Y/H-0.5))':cr='128+128*sin(2*PI*(X/W-0.5))*cos(2*PI*(Y/H-0.5))'" output.mp4

# 彩虹渐变
ffmpeg -i input.mp4 -vf "hue=H=2*S*sin(2*PI*t/5)" output.mp4
```

**色彩替换：**
```bash
# 绿幕抠图
ffmpeg -i input.mp4 -vf "colorkey=green:0.3:0.1" -c:v libx264 -crf 23 -preset veryfast output.mp4

# 蓝幕抠图
ffmpeg -i input.mp4 -vf "colorkey=blue:0.2:0.1" output.mp4

# 自定义颜色替换
ffmpeg -i input.mp4 -vf "colorkey=0x00FF00:0.3:0.1" output.mp4
```

**色彩映射：**
```bash
# 调色板映射
ffmpeg -i input.mp4 -i palette.png -filter_complex "paletteuse" output.mp4

# 色彩量化
ffmpeg -i input.mp4 -vf "lutyuv='y=val*val/255'" output.mp4

# 色彩反转映射
ffmpeg -i input.mp4 -vf "negate" output.mp4
```

### 6.2.3 调整明暗对比效果

明暗对比调整影响画面的整体观感：

**亮度调整：**
```bash
# 全局亮度调整
ffmpeg -i input.mp4 -vf "eq=brightness=0.2" output.mp4

# 局部亮度调整
ffmpeg -i input.mp4 -vf "crop=iw/2:ih:0:0,eq=brightness=0.3[v1];crop=iw/2:ih:iw/2:0,eq=brightness=-0.1[v2];[v1][v2]hstack" output.mp4

# 动态亮度
ffmpeg -i input.mp4 -vf "eq=brightness=0.2*sin(t)" output.mp4
```

**对比度调整：**
```bash
# 对比度增强
ffmpeg -i input.mp4 -vf "eq=contrast=2" output.mp4

# 对比度降低
ffmpeg -i input.mp4 -vf "eq=contrast=0.5" output.mp4

# 局部对比度
ffmpeg -i input.mp4 -vf "unsharp=5:5:1.5:5:5:0.0" output.mp4
```

**gamma校正：**
```bash
# Gamma调整
ffmpeg -i input.mp4 -vf "eq=gamma=1.5" output.mp4

# 局部Gamma
ffmpeg -i input.mp4 -vf "curves=all='0/0 0.5/0.58 1/1'" output.mp4

# 自定义曲线
ffmpeg -i input.mp4 -vf "curves=m='0/0 0.25/0.2 0.5/0.5 0.75/0.8 1/1'" output.mp4
```

**混合调整：**
```bash
# 综合调整
ffmpeg -i input.mp4 -vf "eq=brightness=0.1:contrast=1.2:gamma=0.8:saturation=1.3" output.mp4

# 分区域调整
ffmpeg -i input.mp4 -filter_complex "[0:v]split=3[v1][v2][v3];[v1]crop=iw/3:ih:0:0,eq=brightness=0.2[v1f];[v2]crop=iw/3:ih:iw/3:0,eq=contrast=1.5[v2f];[v3]crop=iw/3:ih:2*iw/3:0,eq=saturation=2[v3f];[v1f][v2f][v3f]hstack=3" output.mp4
```

### 6.2.4 添加淡入淡出特效

淡入淡出是视频编辑中的基础转场：

**视频淡入淡出：**
```bash
# 视频淡入
ffmpeg -i input.mp4 -vf "fade=in:0:30" output.mp4

# 视频淡出
ffmpeg -i input.mp4 -vf "fade=out:120:30" output.mp4

# 淡入淡出组合
ffmpeg -i input.mp4 -vf "fade=in:0:30,fade=out:90:30" output.mp4

# 交叉淡化
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex "[0:v][1:v]xfade=transition=fade:duration=1:offset=3" output.mp4
```

**音频淡入淡出：**
```bash
# 音频淡入
ffmpeg -i input.mp4 -af "afade=in:0:30" output.mp4

# 音频淡出
ffmpeg -i input.mp4 -af "afade=out:120:30" output.mp4

# 音视频同步淡入淡出
ffmpeg -i input.mp4 -vf "fade=in:0:30,fade=out:90:30" -af "afade=in:0:30,afade=out:90:30" output.mp4
```

**高级淡化效果：**
```bash
# 渐变淡入
ffmpeg -i input.mp4 -vf "fade=in:0:30:alpha=1" output.mp4

# 黑白淡入
ffmpeg -i input.mp4 -vf "fade=in:0:30,curves=all='0/0 0.5/0.58 1/1'" output.mp4

# 模糊淡入
ffmpeg -i input.mp4 -vf "gblur=sigma=10+10*(1-t/3),fade=in:0:90" output.mp4
```

## 6.3 变换方位

### 6.3.1 翻转视频的方向

视频方向调整是基础的视频处理功能：

**基础翻转：**
```bash
# 水平翻转（镜像）
ffmpeg -i input.mp4 -vf "hflip" output.mp4

# 垂直翻转（上下颠倒）
ffmpeg -i input.mp4 -vf "vflip" output.mp4

# 180度旋转
ffmpeg -i input.mp4 -vf "hflip,vflip" output.mp4

# 双重镜像效果
ffmpeg -i input.mp4 -vf "split=2[v1][v2];[v1]hflip[v1h];[v1h][v2]hstack" output.mp4
```

**组合翻转：**
```bash
# 分屏翻转效果
ffmpeg -i input.mp4 -filter_complex "[0:v]split=4[v1][v2][v3][v4];[v1]crop=iw/2:ih:0:0[v1t];[v2]crop=iw/2:ih:iw/2:0[v2t];[v3]crop=iw/2:ih:0:ih/2[v3t];[v4]crop=iw/2:ih:iw/2:ih/2[v4t];[v1t]hflip[v1h];[v3t]vflip[v3v];[v1h][v2t]vstack[v12];[v3v][v4t]vstack[v34];[v12][v34]hstack" output.mp4

# 四分屏翻转
ffmpeg -i input.mp4 -filter_complex "[0:v]split=4[v1][v2][v3][v4];[v1]crop=iw/2:ih:0:0[v1c];[v2]crop=iw/2:ih:iw/2:0[v2c];[v3]crop=iw/2:ih:0:ih/2[v3c];[v4]crop=iw/2:ih:iw/2:ih/2[v4c];[v1c][v2c]vstack[v12];[v3c][v4c]vstack[v34];[v12][v34]hstack" output.mp4
```

### 6.3.2 缩放和旋转视频

缩放和旋转改变视频的尺寸和方向：

**缩放处理：**
```bash
# 按比例缩放
ffmpeg -i input.mp4 -vf "scale=640:360" output.mp4

# 保持宽高比缩放
ffmpeg -i input.mp4 -vf "scale=640:-1" output.mp4

# 双倍缩放
ffmpeg -i input.mp4 -vf "scale=iw*2:ih*2" output.mp4

# 智能缩放
ffmpeg -i input.mp4 -vf "scale=1920:1080:flags=lanczos" output.mp4
```

**旋转处理：**
```bash
# 顺时针旋转90度
ffmpeg -i input.mp4 -vf "transpose=1" output.mp4

# 逆时针旋转90度
ffmpeg -i input.mp4 -vf "transpose=2" output.mp4

# 旋转180度
ffmpeg -i input.mp4 -vf "transpose=2,transpose=2" output.mp4

# 任意角度旋转
ffmpeg -i input.mp4 -vf "rotate=PI/6" output.mp4
```

**组合变换：**
```bash
# 缩放+旋转
ffmpeg -i input.mp4 -vf "scale=800:600,transpose=1" output.mp4

# 动态缩放
ffmpeg -i input.mp4 -vf "scale=800+200*sin(t):600" output.mp4

# 动态旋转
ffmpeg -i input.mp4 -vf "rotate=PI*sin(t)" output.mp4
```

### 6.3.3 裁剪和填充视频

裁剪和填充调整视频的构图：

**裁剪处理：**
```bash
# 中心裁剪
ffmpeg -i input.mp4 -vf "crop=800:600" output.mp4

# 指定位置裁剪
ffmpeg -i input.mp4 -vf "crop=800:600:100:50" output.mp4

# 智能裁剪
ffmpeg -i input.mp4 -vf "cropdetect=24:16:0" -f null -

# 动态裁剪
ffmpeg -i input.mp4 -vf "crop=800+100*sin(t):600:100:50" output.mp4
```

**填充处理：**
```bash
# 黑色填充
ffmpeg -i input.mp4 -vf "pad=1920:1080:560:260:black" output.mp4

# 白色填充
ffmpeg -i input.mp4 -vf "pad=1920:1080:560:260:white" output.mp4

# 彩色填充
ffmpeg -i input.mp4 -vf "pad=1920:1080:560:260:red" output.mp4

# 渐变填充
ffmpeg -i input.mp4 -vf "pad=1920:1080:560:260:0x00FF00" output.mp4
```

**复杂变换：**
```bash
# 先裁剪后填充
ffmpeg -i input.mp4 -vf "crop=800:600,stylegrad=i=1920x1080:x=560:y=260" output.mp4

# 镜头移动效果
ffmpeg -i input.mp4 -vf "crop=800:600:100+50*sin(t):50+30*cos(t)" output.mp4

# 缩放镜头效果
ffmpeg -i input.mp4 -vf "scale=800+200*sin(t*2):600+150*sin(t*2)" output.mp4
```

## 6.4 实战项目：老电影怀旧风

这个项目将展示如何制作老电影风格的视频效果。

**项目需求：**
- 添加黑白效果
- 添加颗粒感
- 添加胶片划痕
- 添加色调调整
- 添加画面抖动

**实现步骤：**

**1. 黑白效果：**
```bash
# 转换为黑白
ffmpeg -i input.mp4 -vf "hue=s=0" -c:v libx264 -crf 23 -preset veryfast bw_video.mp4
```

**2. 添加颗粒感：**
```bash
# 添加胶片颗粒
ffmpeg -i bw_video.mp4 -vf "noise=alls=20:allf=t+u" grain_video.mp4
```

**3. 添加色调调整：**
```bash
# 怀旧色调
ffmpeg -i grain_video.mp4 -vf "eq=contrast=1.1:brightness=-0.1:saturation=0" sepia_video.mp4
```

**4. 完整效果组合：**
```bash
ffmpeg -i input.mp4 -filter_complex \
"[0:v]hue=s=0,eq=contrast=1.1:brightness=-0.1,noise=alls=20:allf=t+u,boxblur=1:1:cr=0:ar=0[v]" \
-map "[v]" -map 0:a? -c:v libx264 -crf 23 -preset veryfast vintage_video.mp4
```

**高级怀旧效果：**
```bash
# 完整的老电影效果
ffmpeg -i input.mp4 -filter_complex \
"[0:v]hue=s=0,eq=contrast=1.2:brightness=-0.05:saturation=0,\
curves=all='0/0 0.5/0.58 1/1',\
noise=alls=20:allf=t+u,\
boxblur=1:1:cr=0:ar=0,\
gblur=sigma=1+0.5*sin(t*2),\
overlay='if(lt(t\,2)\,mod(t\,0.1)*10\,if(lt(t\,4)\,mod(t\,0.2)*20\,0))'" \
-map "[v]" -map 0:a? -c:v libx264 -crf 23 -preset veryfast complete_vintage.mp4
```

**批处理脚本：**
```bash
#!/bin/bash
# 老电影风格处理脚本

if [ $# -ne 2 ]; then
    echo "用法: $0 <输入视频> <输出视频>"
    exit 1
fi

INPUT=$1
OUTPUT=$2

echo "开始处理老电影风格: $INPUT -> $OUTPUT"

ffmpeg -i "$INPUT" -filter_complex \
"[0:v]hue=s=0,\
eq=contrast=1.2:brightness=-0.05:saturation=0,\
curves=all='0/0 0.5/0.58 1/1',\
noise=alls=20:allf=t+u,\
boxblur=1:1:cr=0:ar=0,\
gblur=sigma=1+0.5*sin(t*2)[v]" \
-map "[v]" -map 0:a? -c:v libx264 -crf 23 -preset veryfast "$OUTPUT"

echo "处理完成: $OUTPUT"
```

## 6.5 小结

本章详细介绍了FFmpeg视频加工的各种技术：

**1. 滤波加工**
- 掌握了基础视频和音频滤镜的使用
- 学会了利用滤镜精确切割视频
- 掌握了添加方格等装饰效果的技巧

**2. 添加特效**
- 理解了图像色度坐标转换的原理
- 掌握了色彩转换特效的制作方法
- 学会了明暗对比和淡入淡出效果的制作

**3. 变换方位**
- 掌握了视频翻转、缩放、旋转的基础操作
- 学会了裁剪和填充的视频构图调整
- 理解了复杂变换效果的组合应用

**4. 实战项目**
- 完成了老电影怀旧风格的完整实现
- 掌握了多种滤镜的协同使用技巧
- 学会了批处理脚本的编写方法

通过本章的学习，读者应该能够熟练使用FFmpeg进行各种视频加工处理，创造出丰富的视觉效果。这些技能在视频编辑、特效制作和创意项目中都非常实用。