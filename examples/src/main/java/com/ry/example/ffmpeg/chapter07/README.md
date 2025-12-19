# 第7章 FFmpeg添加图文 - Java代码示例

本章包含FFmpeg添加图标、文本和字幕的各种Java实现示例，涵盖水印处理、文本渲染、字幕管理和卡拉OK项目。

## 📁 文件结构

```
chapter07/
├── ImageTextProcessor.java    # 图文处理器
├── SubtitleProcessor.java     # 字幕处理器
├── KaraokeProject.java       # 卡拉OK项目
├── Chapter07Demo.java         # 综合演示类
└── README.md                  # 使用说明
```

## 🎯 主要功能

### 1. 图文处理 (ImageTextProcessor)
- **图片水印**: 在视频任意位置添加图片水印，支持透明度设置
- **区域清除**: 使用模糊或delogo效果清除视频特定区域
- **GIF生成**: 从视频生成高质量GIF动画，支持调色板优化
- **文本添加**: 英文和中文文本渲染，支持位置、颜色、阴影设置
- **滚动文本**: 创建横向滚动的文本效果
- **时间戳**: 在视频上显示实时时间戳
- **批量处理**: 批量为多个视频添加水印

### 2. 字幕处理 (SubtitleProcessor)
- **软字幕**: 添加可切换的SRT字幕流
- **硬字幕**: 将字幕烧录到视频中，永久显示
- **ASS字幕**: 支持复杂样式的ASS字幕格式
- **样式自定义**: 完整的字幕样式配置系统
- **格式转换**: 在SRT、ASS等字幕格式间转换
- **时间调整**: 延迟或提前字幕时间
- **字幕提取**: 从视频中提取字幕文件

### 3. 卡拉OK项目 (KaraokeProject)
- **卡拉OK字幕**: 创建带进度填充效果的歌词显示
- **多语言支持**: 中英文字幕同步显示
- **音乐视频**: 从音频和背景图创建音乐视频
- **音频分析**: 自动分析音频节奏同步歌词
- **位置控制**: 支持歌词在画面不同位置显示

## 🚀 快速开始

### 1. 环境准备
```bash
# 确保FFmpeg已安装并可用
ffmpeg -version
ffprobe -version

# 准备测试文件
# - input.mp4 (输入视频)
# - logo.png (水印图片)
# - background_music.mp3 (背景音乐，可选)
# - background.jpg (背景图片，可选)
# - 中文字体文件 (用于中文文本)
```

### 2. 运行综合演示
```bash
# 编译项目
mvn compile

# 运行完整演示
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter07.Chapter07Demo"

# 运行图文处理示例
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter07.ImageTextProcessor"

# 运行字幕处理示例
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter07.SubtitleProcessor"

# 运行卡拉OK项目
mvn exec:java -Dexec.mainClass="com.ry.example.ffmpeg.chapter07.KaraokeProject"
```

## 📖 使用示例

### 基本图片水印
```java
// 添加基本水印
ImageTextProcessor.addImageWatermark(
    "input.mp4",           // 输入视频
    "logo.png",            // 水印图片
    "output.mp4",          // 输出视频
    10, 10,               // 水印位置 (x, y)
    0.8f                  // 透明度
);

// 右下角水印
ImageTextProcessor.addWatermarkBottomRight(
    "input.mp4", 
    "logo.png", 
    "output.mp4", 
    10,    // 边距
    0.7f   // 透明度
);
```

### 文本添加
```java
// 英文文本
ImageTextProcessor.addEnglishText(
    "input.mp4",
    "output.mp4", 
    "Hello FFmpeg!",      // 文本内容
    50, 50,               // 位置
    32,                   // 字号
    "white",              // 颜色
    ImageTextProcessor.TextPosition.TOP_LEFT  // 位置类型
);

// 中文文本
ImageTextProcessor.addChineseText(
    "input.mp4",
    "output.mp4",
    "你好，FFmpeg！",     // 中文文本
    "C:/Windows/Fonts/simhei.ttf",  // 字体文件
    100, 100,             // 位置
    32,                   // 字号
    "white",              // 颜色
    true                  // 添加阴影
);

// 滚动文本
ImageTextProcessor.addScrollingText(
    "input.mp4",
    "output.mp4",
    "滚动文本内容",
    "font.ttf",           // 字体文件
    28,                   // 字号
    "yellow",             // 颜色
    900                   // Y坐标
);
```

### 字幕处理
```java
// 软字幕（可切换）
SubtitleProcessor.addSRTSubtitle(
    "input.mp4",
    "subtitles.srt",
    "output.mp4"
);

// 硬字幕（烧录到视频）
SubtitleProcessor.burnSubtitle(
    "input.mp4",
    "subtitles.srt", 
    "output.mp4",
    "Microsoft YaHei",    // 字体
    24                    // 字号
);

// 自定义样式字幕
SubtitleProcessor.SubtitleStyle style = new SubtitleProcessor.SubtitleStyle();
style.fontSize = 28;
style.primaryColor = "&H00FFFF00";  // 黄色
style.outlineColor = "&H00000000";  // 黑色边框

SubtitleProcessor.addStyledSubtitle(
    "input.mp4",
    "subtitles.srt",
    "output.mp4", 
    style
);
```

### 卡拉OK效果
```java
// 创建卡拉OK歌词
List<KaraokeProject.KaraokeLyric> lyrics = new ArrayList<>();
lyrics.add(new KaraokeProject.KaraokeLyric(
    "第一句歌词",           // 歌词文本
    0.0, 3.0,             // 开始/结束时间
    KaraokeProject.LyricPosition.CENTER  // 显示位置
));

// 创建卡拉OK视频
KaraokeProject.createKaraokeVideo(
    "input.mp4",
    "karaoke_output.mp4",
    lyrics
);

// 多语言卡拉OK
List<KaraokeProject.MultilingualLyric> multilingualLyrics = new ArrayList<>();
multilingualLyrics.add(new KaraokeProject.MultilingualLyric(
    "你好世界",            // 中文
    "Hello World",        // 英文
    0.0, 3.0             // 时间
));

KaraokeProject.createMultiLanguageKaraoke(
    "input.mp4",
    "multilingual_karaoke.mp4", 
    multilingualLyrics
);
```

## 🎨 样式配置

### 字幕样式参数
```java
SubtitleProcessor.SubtitleStyle style = new SubtitleProcessor.SubtitleStyle();

// 基本样式
style.fontName = "Microsoft YaHei";    // 字体名称
style.fontSize = 24;                   // 字体大小
style.primaryColor = "&H00FFFFFF";     // 主要颜色
style.outlineColor = "&H00000000";     // 边框颜色
style.backgroundColor = "&H80000000";  // 背景颜色

// 文本样式
style.bold = true;                      // 加粗
style.italic = false;                   // 斜体
style.underline = false;                // 下划线
style.strikeOut = false;                // 删除线

// 变换
style.scaleX = 1.0;                     // X轴缩放
style.scaleY = 1.0;                     // Y轴缩放
style.angle = 0.0;                      // 旋转角度

// 边框和阴影
style.borderStyle = 1;                  // 边框样式
style.outline = 2;                      // 边框宽度
style.shadow = 2;                       // 阴影距离

// 对齐和边距
style.alignment = 2;                    // 对齐方式
style.marginLeft = 0;                   // 左边距
style.marginRight = 0;                  // 右边距
style.marginVertical = 0;               // 垂直边距
```

### 颜色编码
```
&H00FFFFFF  - 白色
&H000000FF  - 红色  
&H0000FF00  - 绿色
&H00FF0000  - 蓝色
&H00FFFF00  - 黄色
&H80808080  - 50%透明灰色
&H80000000  - 50%透明黑色
```

## 🔧 高级功能

### 批量处理
```java
// 批量添加水印
List<String> inputVideos = Arrays.asList(
    "video1.mp4", "video2.mp4", "video3.mp4"
);

ImageTextProcessor.WatermarkConfig config = 
    new ImageTextProcessor.WatermarkConfig(15, 0.6f);

ImageTextProcessor.batchAddWatermarks(
    inputVideos, 
    "logo.png",
    "output/batch/",
    config
);
```

### 字幕时间调整
```java
// 延迟字幕2秒
SubtitleProcessor.shiftSubtitleTiming(
    "input.srt", 
    "delayed.srt", 
    2  // 延迟秒数（正数延迟，负数提前）
);
```

### 高质量GIF生成
```java
ImageTextProcessor.createHighQualityGIF(
    "input.mp4",        // 输入视频
    "output.gif",       // 输出GIF
    10,                 // 帧率
    320,                // 宽度
    5,                  // 开始时间（秒）
    3                   // 持续时间（秒）
);
```

## 📊 输出结果

运行示例后，会在以下目录生成处理后的文件：
```
output/chapter07_demo/
├── 01_basic_watermark.mp4        # 基本水印
├── 02_bottom_right_watermark.mp4  # 右下角水印
├── 03_blurred_region.mp4         # 区域模糊
├── 04_logo_removed.mp4           # Logo移除
├── 05_output.gif                 # GIF动画
├── 06_english_text.mp4           # 英文文本
├── 07_timestamp.mp4              # 时间戳
├── 08_chinese_text.mp4           # 中文文本
├── 09_scrolling_text.mp4         # 滚动文本
├── 10_soft_subtitle.mp4          # 软字幕
├── 11_hard_subtitle.mp4          # 硬字幕
├── 12_styled_subtitle.mp4        # 样式字幕
├── 13_ass_subtitle.mp4           # ASS字幕
├── 14_karaoke_video.mp4          # 卡拉OK视频
├── 15_multilingual_karaoke.mp4  # 多语言卡拉OK
├── 16_comprehensive_demo.mp4     # 综合演示
└── batch_watermarks/             # 批量处理结果
    ├── watermarked_1.mp4
    └── watermarked_2.mp4
```

## ⚠️ 注意事项

1. **字体文件**: 中文文本需要提供中文字体文件路径
2. **文件权限**: 确保对输入文件有读权限，对输出目录有写权限
3. **磁盘空间**: 视频处理可能产生大文件，确保有足够存储空间
4. **处理时间**: 高分辨率视频处理时间较长，请耐心等待
5. **字符编码**: 中文字幕文件应使用UTF-8编码
6. **字幕格式**: 不同播放器对字幕格式的支持程度不同

## 🛠 故障排除

### 常见问题
1. **字体未找到**: 检查字体文件路径是否正确
2. **字幕乱码**: 确保字幕文件使用UTF-8编码
3. **水印不显示**: 检查图片文件格式是否支持
4. **GIF质量差**: 尝试调整调色板生成参数
5. **字幕时间不准**: 使用时间调整功能重新同步

### 调试技巧
```bash
# 查看视频信息
ffprobe -v quiet -print_format json -show_format -show_streams input.mp4

# 检查字幕文件
file -bi subtitles.srt

# 测试字体
ffmpeg -f lavfi -i "color=red:size=100x100:duration=5" -vf "drawtext=text='Test':fontfile=font.ttf" test.mp4
```

## 📚 相关文档

- [第7章文档](../../../docs/chapter07/FFmpeg添加图文.md)
- [FFmpeg滤镜文档](https://ffmpeg.org/ffmpeg-filters.html)
- [ASS字幕规范](http://www.aegisub.org/docs/3.2/ASS_Tags_0.html)
- [SRT字幕格式](https://en.wikipedia.org/wiki/SubRip)

## 🎯 学习目标

通过本章学习，你将掌握：
1. FFmpeg图片水印技术
2. 中英文文本渲染方法
3. 字幕系统的完整实现
4. 卡拉OK效果的制作
5. 批量图文处理技术

## 📞 技术支持

如有问题，请参考：
1. 项目文档和示例
2. FFmpeg官方文档
3. 字幕格式规范
4. 错误日志和调试信息