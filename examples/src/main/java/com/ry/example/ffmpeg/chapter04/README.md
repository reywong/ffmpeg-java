# 第4章：FFmpeg处理图像

本章演示了如何使用FFmpeg处理各种图像格式，包括YUV、JPEG、PNG、BMP和GIF等。

## 功能模块

### 1. YUVImageProcessor - YUV图像处理器
- **功能**：处理YUV格式的图像转换和保存
- **主要方法**：
  - `saveFrameAsYUV()` - 将视频帧保存为YUV格式
  - `batchSaveYUV()` - 批量保存YUV文件
  - `convertYUVToRGB()` - YUV格式转换为其他格式
  - `analyzeYUVFile()` - YUV格式信息分析

### 2. JPEGImageProcessor - JPEG图像处理器
- **功能**：处理JPEG格式的图像转换和优化
- **主要方法**：
  - `extractFrameAsJPEG()` - 从视频中提取JPEG帧
  - `batchExtractJPEG()` - 批量提取JPEG文件
  - `optimizeJPEG()` - JPEG质量优化
  - `createProgressiveJPEG()` - 创建渐进式JPEG
  - `processImage()` - 图像处理（缩放、裁剪、旋转）
  - `addWatermark()` - 添加水印

### 3. ImageFormatConverter - 图像格式转换器
- **功能**：支持PNG、BMP、GIF等格式的转换和处理
- **主要方法**：
  - `extractFrameAsPNG()` - 提取PNG图片
  - `extractFrameAsBMP()` - 提取BMP图片
  - `createGIFAnimation()` - 创建GIF动画
  - `createHighQualityGIF()` - 创建高质量GIF
  - `convertImageFormat()` - 格式转换
  - `createLoopedGIF()` - 创建循环GIF

### 4. ImageToVideoConverter - 图片转视频转换器
- **功能**：实战项目，将静态图片序列转换为动态视频
- **主要方法**：
  - `convertImagesToVideo()` - 基础图片转视频
  - `createVideoWithTransitions()` - 创建带转场效果的视频
  - `createVideoWithMusic()` - 创建带背景音乐的视频
  - `createVideoWithWatermark()` - 创建带水印的视频
  - `createSlideshowVideo()` - 创建幻灯片效果的视频
  - `normalizeImageSizes()` - 批量处理图片尺寸统一

## 使用示例

### 基础用法

```java
// 1. YUV图像处理
YUVImageProcessor.saveFrameAsYUV("input.mp4", "output/frame_0.yuv", 0);
YUVImageProcessor.batchSaveYUV("input.mp4", "output/frame_%03d.yuv", 30);

// 2. JPEG图像处理
JPEGImageProcessor.extractFrameAsJPEG("input.mp4", "output/frame_0.jpg", 0, 0.8f);
JPEGImageProcessor.batchExtractJPEG("input.mp4", "output/frame_%03d.jpg", 1);
JPEGImageProcessor.optimizeJPEG("input.jpg", "output_optimized.jpg", 0.6f);

// 3. 图像格式转换
ImageFormatConverter.createGIFAnimation("input.mp4", "output.gif", 10, 320, 240);
ImageFormatConverter.createHighQualityGIF("input.mp4", "output_hq.gif", 15, 480, 360);

// 4. 图片转视频
List<String> images = Arrays.asList("img1.jpg", "img2.jpg", "img3.jpg");
ImageToVideoConverter.convertImagesToVideo(images, "output.mp4", 25, 1920, 1080);
```

### 运行演示

```bash
# 运行完整演示
java com.ry.example.ffmpeg.chapter04.Chapter04Demo

# 交互式演示
java com.ry.example.ffmpeg.chapter04.Chapter04Demo interactive
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
examples/src/main/java/com/ry/example/ffmpeg/chapter04/
├── YUVImageProcessor.java          # YUV图像处理器
├── JPEGImageProcessor.java         # JPEG图像处理器
├── ImageFormatConverter.java       # 图像格式转换器
├── ImageToVideoConverter.java     # 图片转视频转换器
├── Chapter04Demo.java              # 演示类
└── README.md                       # 说明文档
```

## 输入文件要求

### 视频文件
- 支持格式：MP4, AVI, MOV, MKV等
- 建议分辨率：720p或1080p
- 编码格式：H.264推荐

### 图片文件
- 支持格式：JPEG, PNG, BMP等
- 建议分辨率：统一尺寸便于处理
- 文件命名：建议使用数字序列

## 输出文件

### YUV文件
- 格式：YUV420P
- 扩展名：.yuv
- 用途：视频分析、格式转换

### JPEG文件
- 质量：0.1-1.0可调
- 支持渐进式和优化
- 压缩比可控制

### GIF动画
- 支持调色板优化
- 可设置帧率和尺寸
- 支持循环播放

### 视频文件
- 格式：MP4
- 编码：H.264
- 分辨率：可自定义

## 性能优化建议

### 1. 内存管理
- 及时释放Frame和BufferedImage资源
- 批量处理时控制内存使用

### 2. 处理优化
- 使用多线程处理大批量文件
- 预先统一图片尺寸提高效率

### 3. 存储优化
- 定期清理临时文件
- 使用合适的压缩参数

## 常见问题

### Q1: YUV文件播放问题
A: 播放YUV文件需要指定正确的像素格式和分辨率：
```bash
ffplay -f rawvideo -pixel_format yuv420p -video_size 1920x1080 input.yuv
```

### Q2: GIF文件过大
A: 调整帧率和尺寸，使用调色板优化：
```bash
ffmpeg -i input.mp4 -vf fps=5,scale=320:-1 palettegen palette.png
ffmpeg -i input.mp4 -i palette.png -lavfi fps=5,scale=320:-1[x];[x][1:v]paletteuse output.gif
```

### Q3: 图片转视频质量差
A: 调整编码参数：
```java
recorder.setBitrate(2000000);  // 提高比特率
recorder.setPreset("slow");    // 提高编码质量
```

## 扩展功能

### 1. 高级图像处理
- 图像滤镜效果
- 色彩校正
- 边缘检测

### 2. 视频增强
- 超分辨率
- 去噪处理
- 稳定化

### 3. 格式支持扩展
- WebP格式支持
- HEIF格式支持
- AVIF格式支持

## 注意事项

1. **版权问题**：处理媒体文件时注意版权
2. **性能考虑**：大文件处理时注意内存和CPU使用
3. **错误处理**：正确处理IO异常和FFmpeg错误
4. **路径问题**：确保文件路径正确且有读写权限

## 联系信息

如有问题或建议，请通过以下方式联系：
- 项目仓库：[GitHub链接]
- 文档：[文档链接]
- 邮箱：[邮箱地址]