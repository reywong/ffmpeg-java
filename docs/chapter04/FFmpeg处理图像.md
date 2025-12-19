# 第 4 章 FFmpeg处理图像

## 4.1 YUV图像

### 4.1.1 为什么要用YUV格式

YUV是一种颜色编码方法，常用于视频处理和图像压缩。相比RGB格式，YUV有以下优势：

**1. 压缩效率高**
- YUV将亮度信息（Y）和色度信息（U、V）分离
- 人眼对亮度的敏感度远高于色度
- 可以对色度信息进行下采样，减少数据量

**2. 兼容性好**
- 黑白电视只显示Y分量，兼容性好
- 彩色电视可以完整显示YUV三个分量

**3. 适合压缩**
- 亮度信号和色度信号相关性较低
- 便于实现有损压缩算法

**FFmpeg处理YUV图像的基本命令：**
```bash
# 将RGB图像转换为YUV格式
ffmpeg -i input.jpg -pix_fmt yuv420p output.yuv

# 查看YUV文件的详细信息
ffprobe -v quiet -print_format json -show_format -show_streams output.yuv
```

### 4.1.2 把视频帧保存为YUV文件

**原始YUV格式保存：**
```bash
# 将视频的第一帧保存为YUV格式
ffmpeg -i input.mp4 -vf "select=eq(n\,0)" -vsync vfr output.yuv

# 保存指定的帧范围
ffmpeg -i input.mp4 -vf "select=between(n\,100,200)" -vsync vfr frames.yuv

# 每隔30帧保存一帧
ffmpeg -i input.mp4 -vf "select=not(mod(n\,30))" -vsync vfr output.yuv
```

**带格式信息的YUV保存：**
```bash
# 保存为YUV4MPEG2格式（包含头信息）
ffmpeg -i input.mp4 -pix_fmt yuv420p -f yuv4mpegpipe output.y4m

# 转换并保存
ffmpeg -i input.mp4 -c:v rawvideo -pix_fmt yuv420p output.yuv
```

**批量处理视频帧：**
```bash
# 将视频的所有帧都保存为单独的YUV文件
ffmpeg -i input.mp4 -qscale:v 2 frame_%04d.yuv

# 指定分辨率和质量
ffmpeg -i input.mp4 -s 640x480 -pix_fmt yuv420p -q:v 2 output_%04d.yuv
```

### 4.1.3 YUV图像浏览工具

**使用FFmpeg播放YUV文件：**
```bash
# 播放YUV文件（需要指定格式和分辨率）
ffplay -f rawvideo -pixel_format yuv420p -video_size 1920x1080 input.yuv

# 循环播放
ffplay -f rawvideo -pixel_format yuv420p -video_size 1920x1080 -loop input.yuv

# 指定帧率播放
ffplay -f rawvideo -pixel_format yuv420p -video_size 1920x1080 -framerate 30 input.yuv
```

**YUV格式转换：**
```bash
# YUV转RGB
ffmpeg -f rawvideo -pixel_format yuv420p -video_size 1920x1080 -i input.yuv -pix_fmt rgb24 output.rgb

# YUV转JPEG
ffmpeg -f rawvideo -pixel_format yuv420p -video_size 1920x1080 -i input.yuv -q:v 2 output.jpg

# YUV转PNG
ffmpeg -f rawvideo -pixel_format yuv420p -video_size 1920x1080 -i input.yuv output.png
```

**YUV格式分析：**
```bash
# 分析YUV文件的头信息
hexdump -C input.yuv | head -5

# 使用FFprobe分析
ffprobe -v quiet -print_format json -show_format -show_streams -f rawvideo -pixel_format yuv420p -video_size 1920x1080 input.yuv
```

## 4.2 JPEG图像

### 4.2.1 为什么要用JPEG格式

JPEG是一种广泛使用的有损压缩图像格式，具有以下特点：

**1. 高压缩比**
- 通常能实现10:1到20:1的压缩比
- 在保持较好图像质量的同时大幅减少文件大小

**2. 广泛支持**
- 几乎所有设备和软件都支持JPEG格式
- 网页传输和存储的标准格式

**3. 可调质量**
- 可以根据需要调整压缩质量
- 平衡文件大小和图像质量

**FFmpeg处理JPEG的基本命令：**
```bash
# 设置JPEG质量（1-31，数值越小质量越高）
ffmpeg -i input.jpg -q:v 2 output.jpg

# 设置优化选项
ffmpeg -i input.jpg -q:v 2 -optimize 2 output.jpg

# 渐进式JPEG
ffmpeg -i input.jpg -q:v 2 -progressive output.jpg
```

### 4.2.2 把视频帧保存为JPEG图片

**单帧保存：**
```bash
# 保存第一帧为JPEG
ffmpeg -i input.mp4 -vframes 1 -q:v 2 frame1.jpg

# 保存指定时间点的帧
ffmpeg -i input.mp4 -ss 00:00:05 -vframes 1 -q:v 2 frame5s.jpg

# 保存指定帧号的帧
ffmpeg -i input.mp4 -vf "select=eq(n\,100)" -vframes 1 -q:v 2 frame100.jpg
```

**批量保存：**
```bash
# 每秒保存一帧
ffmpeg -i input.mp4 -vf "fps=1" -q:v 2 frame_%03d.jpg

# 每隔30帧保存一帧
ffmpeg -i input.mp4 -vf "select=not(mod(n\,30))" -vsync vfr -q:v 2 frame_%04d.jpg

# 保存所有帧
ffmpeg -i input.mp4 -q:v 2 frame_%06d.jpg
```

**高质量保存：**
```bash
# 设置最高质量
ffmpeg -i input.mp4 -q:v 1 -qscale:v 1 frame_%04d.jpg

# 设置特定分辨率
ffmpeg -i input.mp4 -s 1920x1080 -q:v 2 frame_%04d.jpg

- 保持原始分辨率
ffmpeg -i input.mp4 -vsync 0 -q:v 2 frame_%06d.jpg
```

### 4.2.3 图像转换器

**格式转换：**
```bash
# PNG转JPEG
ffmpeg -i input.png -q:v 2 output.jpg

# JPEG转PNG
ffmpeg -i input.jpg output.png

# 批量转换
for file in *.png; do ffmpeg -i "$file" -q:v 2 "${file%.png}.jpg"; done
```

**图像处理：**
```bash
# 调整图像大小
ffmpeg -i input.jpg -s 800x600 output.jpg

# 裁剪图像
ffmpeg -i input.jpg -crop:w=800:h=600:x=100:y=50 output.jpg

# 旋转图像
ffmpeg -i input.jpg -vf "transpose=1" output.jpg

# 添加水印
ffmpeg -i input.jpg -i watermark.png -filter_complex "overlay=10:10" output.jpg
```

**质量优化：**
```bash
# 优化文件大小
ffmpeg -i input.jpg -q:v 5 -optimize 2 output.jpg

- 创建渐进式JPEG
ffmpeg -i input.jpg -q:v 2 -progressive output.jpg

# 调整压缩级别
ffmpeg -i input.jpg -q:v 3 -compression_level 6 output.jpg
```

## 4.3 其他图像格式

### 4.3.1 把视频帧保存为PNG图片

PNG是一种无损压缩格式，适合保存需要高质量的场景：

**基础PNG保存：**
```bash
# 保存为PNG格式（无损压缩）
ffmpeg -i input.mp4 -vframes 1 frame.png

# 设置压缩级别（0-9，0为无压缩，9为最大压缩）
ffmpeg -i input.mp4 -vframes 1 -compression_level 6 frame.png

# 设置预测模式
ffmpeg -i input.mp4 -vframes 1 -pred all frame.png
```

**批量PNG保存：**
```bash
# 每秒保存一帧为PNG
ffmpeg -i input.mp4 -vf "fps=1" frame_%03d.png

# 保存高质量PNG序列
ffmpeg -i input.mp4 -compression_level 0 frame_%04d.png

# 保存带透明通道的PNG
ffmpeg -i input.mp4 -pix_fmt rgba frame_%04d.png
```

**PNG格式优化：**
```bash
# 使用无损压缩
ffmpeg -i input.jpg -compression_level 0 output.png

# 使用最佳压缩
ffmpeg -i input.jpg -compression_level 9 output.png

- 针对网络优化
ffmpeg -i input.jpg -compression_level 6 -pred all output.png
```

### 4.3.2 把视频帧保存为BMP图片

BMP是一种未压缩的位图格式，适合需要原始图像数据的场景：

**基础BMP保存：**
```bash
# 保存为BMP格式
ffmpeg -i input.mp4 -vframes 1 frame.bmp

# 指定BMP格式
ffmpeg -i input.mp4 -vframes 1 -f bmp frame.bmp

# 设置位深度
ffmpeg -i input.mp4 -vframes 1 -pix_fmt bgr24 frame.bmp
```

**批量BMP保存：**
```bash
# 保存BMP序列
ffmpeg -i input.mp4 frame_%06d.bmp

# 指定高质量BMP
ffmpeg -i input.mp4 -pix_fmt rgb48 frame_%04d.bmp

# 保存16位BMP
ffmpeg -i input.mp4 -pix_fmt rgb565 frame_%04d.bmp
```

### 4.3.3 把视频保存为GIF动画

GIF动画在网页和社交媒体中广泛使用：

**基础GIF创建：**
```bash
# 创建简单GIF动画
ffmpeg -i input.mp4 -vf "fps=10,scale=320:-1" output.gif

# 设置调色板优化
ffmpeg -i input.mp4 -vf "fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" output.gif

# 设置循环次数
ffmpeg -i input.mp4 -vf "fps=10,scale=320:-1" -loop 0 output.gif
```

**高质量GIF：**
```bash
# 使用高调色板质量
ffmpeg -i input.mp4 -vf "fps=15,scale=480:-1:flags=lanczos,split[s0][s1];[s0]palettegen=reserve_transparent=on:stats_mode=full[p];[s1][p]paletteuse" output.gif

# 设置特定时间段
ffmpeg -i input.mp4 -ss 00:00:05 -t 10 -vf "fps=10,scale=320:-1" output.gif

- 优化文件大小
ffmpeg -i input.mp4 -vf "fps=8,scale=280:-1:flags=lanczos,split[s0][s1];[s0]palettegen=max_colors=256[p];[s1][p]paletteuse=dither=bayer:bayer_scale=5" output.gif
```

**GIF特效：**
```bash
# 添加文字到GIF
ffmpeg -i input.mp4 -vf "fps=10,scale=320:-1,drawtext=text='Sample':fontfile=arial.ttf:x=10:y=10:fontsize=24:fontcolor=white" output.gif

# 创建循环GIF
ffmpeg -i input.mp4 -vf "fps=10,scale=320:-1" -loop 0 output.gif

# 创建延迟GIF
ffmpeg -i input.mp4 -vf "fps=5,scale=320:-1" output.gif
```

## 4.4 实战项目：图片转视频

这个项目将展示如何将一系列静态图片转换为视频文件。

**项目需求：**
- 支持多种图片格式输入
- 可配置输出视频参数
- 支持添加转场效果
- 支持背景音乐

**实现步骤：**

**1. 准备图片序列：**
```bash
# 确保图片命名规范：img_001.jpg, img_002.jpg, ...
# 或者使用数字序列：frame001.jpg, frame002.jpg, ...

# 统一图片尺寸
for file in *.jpg; do ffmpeg -i "$file" -s 1920x1080 "${file%.jpg}_resized.jpg"; done
```

**2. 基础图片转视频：**
```bash
# 创建25fps的视频
ffmpeg -framerate 25 -i img_%03d.jpg -c:v libx264 -pix_fmt yuv420p output.mp4

# 设置视频时长（每张图片显示3秒）
ffmpeg -framerate 1/3 -i img_%03d.jpg -c:v libx264 -pix_fmt yuv420p output.mp4

# 添加过渡效果
ffmpeg -framerate 25 -i img_%03d.jpg -vf "fade=in:0:30,fade=out:30:30" output.mp4
```

**3. 高级功能实现：**
```bash
# 添加背景音乐
ffmpeg -framerate 25 -i img_%03d.jpg -i background.mp3 -c:v libx264 -c:a aac -shortest output_with_audio.mp4

# 添加水印
ffmpeg -framerate 25 -i img_%03d.jpg -i watermark.png -filter_complex "overlay=W-w-10:H-h-10" output_watermarked.mp4

# 创建幻灯片效果
ffmpeg -framerate 1/3 -i img_%03d.jpg -vf "zoompan=z='if(lte(on,1),1,1.5)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=125" slideshow.mp4
```

**完整的批处理脚本：**
```bash
#!/bin/bash
# 图片转视频脚本

# 设置参数
INPUT_DIR="images"
OUTPUT_FILE="output.mp4"
FRAMERATE=25
DURATION_PER_IMAGE=3

# 统一图片尺寸
echo "正在调整图片尺寸..."
for img in "$INPUT_DIR"/*.jpg; do
    ffmpeg -i "$img" -s 1920x1080 -q:v 2 "${img%.jpg}_fixed.jpg"
done

# 创建视频
echo "正在创建视频..."
ffmpeg -framerate $((1/$DURATION_PER_IMAGE)) -i "${INPUT_DIR}/*_fixed.jpg" \
    -c:v libx264 -preset slow -crf 18 -pix_fmt yuv420p \
    "$OUTPUT_FILE"

echo "视频创建完成: $OUTPUT_FILE"
```

## 4.5 小结

本章详细介绍了FFmpeg处理各种图像格式的方法：

**1. YUV图像处理**
- 理解YUV格式的优势和应用场景
- 掌握视频帧保存为YUV文件的方法
- 学会使用YUV图像浏览和分析工具

**2. JPEG图像处理**
- 了解JPEG格式的特点和应用
- 掌握从视频中提取JPEG帧的方法
- 学会图像格式转换和优化技术

**3. 其他图像格式**
- PNG无损压缩的使用方法
- BMP未压缩格式的应用
- GIF动画的创建和优化

**4. 实战项目**
- 图片序列转视频的完整流程
- 多种转场效果的实现
- 音频和水印的添加方法

通过本章的学习，读者应该能够熟练使用FFmpeg处理各种图像格式，并能将静态图片转换为动态视频。这些技能在视频编辑、内容创作和多媒体应用开发中都非常重要。