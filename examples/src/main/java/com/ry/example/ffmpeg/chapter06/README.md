# 第6章 FFmpeg加工视频 - Java代码示例

本章包含FFmpeg视频加工的各种Java实现示例，涵盖滤波加工、添加特效、变换方位和实战项目。

## 📁 文件结构

```
chapter06/
├── VideoFilterProcessor.java    # 视频滤镜处理器
├── VideoTransformer.java        # 视频变换处理器
├── VintageEffectGenerator.java  # 老电影怀旧效果生成器
├── Chapter06Demo.java          # 综合演示类
└── README.md                   # 使用说明
```

## 🎯 主要功能

### 1. 滤波加工 (VideoFilterProcessor)
- **色彩调整**: 亮度、对比度、饱和度调整
- **图像变换**: 水平翻转、垂直翻转、旋转变换
- **模糊锐化**: 高斯模糊、盒式模糊、锐化、边缘检测
- **色彩空间**: RGB、HSV、YUV等色彩空间转换
- **渐变背景**: 添加纯色或渐变背景
- **绿幕抠图**: 色彩替换和透明处理
- **明暗对比**: Gamma校正和亮度调整
- **淡入淡出**: 视频开始和结束的过渡效果

### 2. 变换方位 (VideoTransformer)
- **翻转效果**: 水平、垂直翻转
- **旋转缩放**: 任意角度旋转和尺寸调整
- **裁剪填充**: 视频画面的裁剪和填充
- **位置调整**: 视频在画布中的位置调整

### 3. 怀旧特效 (VintageEffectGenerator)
- **老电影效果**: 暗角、噪点、色彩偏移
- **复古滤镜**: 棕褐色调、褪色效果
- **胶片效果**: 胶片颗粒、划痕、灰尘
- **风格化**: 黑白电影、色彩饱和度调整

## 🚀 快速开始

### 1. 环境准备
```bash
# 确保FFmpeg已安装并可用
ffmpeg -version
ffprobe -version
```

### 2. 准备测试视频
将测试视频文件放在项目根目录：
```bash
# 下载测试视频或使用自己的视频文件
# 支持格式：mp4, avi, mov, mkv等
```

### 3. 运行示例
```bash
# 编译项目
mvn compile

# 运行综合演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter06.Chapter06Demo"

# 运行滤镜处理示例
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter06.VideoFilterProcessor"

# 运行变换处理示例
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter06.VideoTransformer"

# 运行怀旧特效示例
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter06.VintageEffectGenerator"
```

## 📖 使用示例

### 基础滤镜处理
```java
// 色彩调整
VideoFilterProcessor.applyColorAdjustment(
    "input.mp4", 
    "output.mp4", 
    0.2,    // 亮度增加20%
    1.2,    // 对比度增加20%
    1.5     // 饱和度增加50%
);

// 图像变换
VideoFilterProcessor.applyImageTransform(
    "input.mp4", 
    "output.mp4", 
    VideoFilterProcessor.TransformType.ROTATE_90
);

// 模糊效果
VideoFilterProcessor.applyBlurSharpness(
    "input.mp4", 
    "output.mp4", 
    VideoFilterProcessor.FilterType.GAUSSIAN_BLUR, 
    2.0     // 模糊强度
);
```

### 视频变换
```java
// 缩放和旋转
VideoTransformer.applyScaleAndRotate(
    "input.mp4", 
    "output.mp4", 
    0.5,    // 缩放50%
    45.0    // 旋转45度
);

// 裁剪视频
VideoTransformer.applyCrop(
    "input.mp4", 
    "output.mp4", 
    640,    // 裁剪宽度
    480,    // 裁剪高度
    320,    // 起始X坐标
    240     // 起始Y坐标
);
```

### 怀旧特效
```java
// 老电影效果
VintageEffectGenerator.createVintageFilm(
    "input.mp4", 
    "output.mp4", 
    VintageEffectGenerator.VintageStyle.CLASSIC_FILM
);

// 复古滤镜
VintageEffectGenerator.createRetroEffect(
    "input.mp4", 
    "output.mp4", 
    VintageEffectGenerator.RetroStyle.SEPIA_TONE
);
```

## 🎨 滤镜参数说明

### 色彩调整参数
- **brightness**: 亮度调整 (-1.0 到 1.0)
- **contrast**: 对比度调整 (-2.0 到 2.0)
- **saturation**: 饱和度调整 (0.0 到 3.0)
- **gamma**: Gamma校正 (0.1 到 10.0)

### 模糊参数
- **sigma**: 高斯模糊标准差 (1.0 到 10.0)
- **radius**: 模糊半径 (1.0 到 20.0)
- **strength**: 锐化强度 (0.0 到 5.0)

### 淡入淡出参数
- **startFrame**: 开始帧数
- **duration**: 持续帧数

## 🔧 高级配置

### 批量处理
```java
List<String> inputFiles = Arrays.asList("video1.mp4", "video2.mp4", "video3.mp4");
VideoFilterProcessor.FilterConfig config = 
    new VideoFilterProcessor.FilterConfig(0.2, 1.2, 1.5);

VideoFilterProcessor.batchApplyFilters(inputFiles, "output/", config);
```

### 自定义滤镜链
```java
// 组合多个滤镜效果
String filterChain = "eq=brightness=0.2:contrast=1.2,unsharp=5:5:1.5,fade=in:0:30";
VideoFilterProcessor.applyCustomFilter(inputFile, outputFile, filterChain);
```

## 📊 输出结果

运行示例后，会在以下目录生成处理后的视频文件：
```
output/
├── filters/           # 滤镜处理结果
│   ├── brightness_adjusted.mp4
│   ├── horizontal_flip.mp4
│   ├── gaussian_blur.mp4
│   └── ...
├── transforms/        # 变换处理结果
│   ├── scaled.mp4
│   ├── rotated.mp4
│   └── cropped.mp4
└── vintage/          # 怀旧特效结果
    ├── classic_film.mp4
    ├── sepia_tone.mp4
    └── retro_style.mp4
```

## ⚠️ 注意事项

1. **文件权限**: 确保有输入文件的读取权限和输出目录的写入权限
2. **磁盘空间**: 视频处理可能产生大文件，确保有足够的磁盘空间
3. **处理时间**: 高分辨率视频处理时间较长，请耐心等待
4. **FFmpeg版本**: 建议使用FFmpeg 4.0+版本以获得最佳兼容性
5. **内存使用**: 批量处理时注意内存使用，避免同时处理过多文件

## 🛠 故障排除

### 常见问题
1. **命令未找到**: 确保FFmpeg已正确安装并添加到PATH
2. **权限错误**: 检查文件和目录权限
3. **内存不足**: 减少批量处理的文件数量
4. **格式不支持**: 确保输入视频格式受FFmpeg支持

### 调试技巧
```bash
# 查看详细日志
export FFMPEG_LOG_LEVEL=debug

# 检查视频信息
ffprobe -v quiet -print_format json -show_format -show_streams input.mp4
```

## 📚 相关文档

- [第6章文档](../../../docs/chapter06/FFmpeg加工视频.md)
- [FFmpeg滤镜文档](https://ffmpeg.org/ffmpeg-filters.html)
- [视频处理最佳实践](../../../docs/best-practices.md)

## 🎯 学习目标

通过本章学习，你将掌握：
1. FFmpeg视频滤镜的使用方法
2. 视频变换和特效的实现
3. 批量视频处理技术
4. 自定义视频效果的开发
5. 视频处理性能优化

## 📞 技术支持

如有问题，请参考：
1. 项目文档
2. FFmpeg官方文档
3. 示例代码注释
4. 错误日志信息