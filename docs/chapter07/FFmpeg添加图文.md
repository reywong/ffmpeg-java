# 第7章 FFmpeg添加图文

本章介绍如何在视频中添加图标、文本和字幕，实现图文并茂的视频效果。

## 7.1 添加图标

### 7.1.1 添加图片标志

在视频的指定位置添加图片水印或Logo：

```bash
# 基本语法
ffmpeg -i video.mp4 -i logo.png -filter_complex "[0:v][1:v]overlay=x=10:y=10" -codec:a copy output.mp4

# 在右下角添加Logo
ffmpeg -i video.mp4 -i logo.png -filter_complex "[0:v][1:v]overlay=W-w-10:H-h-10" -codec:a copy output.mp4

# 在居中位置添加Logo
ffmpeg -i video.mp4 -i logo.png -filter_complex "[0:v][1:v]overlay=(W-w)/2:(H-h)/2" -codec:a copy output.mp4

# 设置透明度和时间
ffmpeg -i video.mp4 -i logo.png -filter_complex "[0:v][1:v]overlay=x=10:y=10:enable='between(t,5,15)':format=auto,alpha=0.8" -codec:a copy output.mp4
```

**Java实现示例：**

```java
/**
 * 在视频中添加图片水印
 */
public static void addImageWatermark(String inputVideo, String watermarkImage, 
                                    String outputVideo, int x, int y) {
    try {
        String command = String.format(
            "ffmpeg -i %s -i %s -filter_complex \"[0:v][1:v]overlay=x=%d:y=%d:format=auto\" -codec:a copy %s",
            inputVideo, watermarkImage, x, y, outputVideo);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("图片水印添加成功: " + outputVideo);
        }
    } catch (Exception e) {
        System.err.println("添加图片水印失败: " + e.getMessage());
    }
}
```

### 7.1.2 清除图标区域

使用模糊或马赛克效果清除视频中的特定区域：

```bash
# 模糊处理指定区域
ffmpeg -i video.mp4 -vf "boxblur=luma_radius=10:luma_power=2:chroma_radius=10:chroma_power=2:enable='between(x,100,200)*between(y,100,200)'" output.mp4

# 马赛克效果
ffmpeg -i video.mp4 -vf "boxblur=20:1:cr=0:ar=0" -crf 23 output.mp4

# 隐藏Logo区域
ffmpeg -i video.mp4 -vf "delogo=x=10:y=10:w=100:h=50:show=0" output.mp4
```

**Java实现示例：**

```java
/**
 * 清除视频中的指定区域
 */
public static void clearRegion(String inputVideo, String outputVideo, 
                              int x, int y, int width, int height) {
    try {
        String command = String.format(
            "ffmpeg -i %s -vf \"delogo=x=%d:y=%d:w=%d:h=%d:show=0\" -c:a copy %s",
            inputVideo, x, y, width, height, outputVideo);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("区域清除成功: " + outputVideo);
        }
    } catch (Exception e) {
        System.err.println("区域清除失败: " + e.getMessage());
    }
}
```

### 7.1.3 利用调色板生成GIF动画

从视频生成高质量的GIF动画：

```bash
# 生成调色板
ffmpeg -i video.mp4 -vf "fps=10,scale=320:-1:flags=lanczos,palettegen" palette.png

# 使用调色板生成GIF
ffmpeg -i video.mp4 -i palette.png -filter_complex "fps=10,scale=320:-1:flags=lanczos[x];[x][1:v]paletteuse" output.gif

# 一步生成GIF
ffmpeg -i video.mp4 -vf "fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" output.gif
```

**Java实现示例：**

```java
/**
 * 生成高质量GIF动画
 */
public static void createHighQualityGIF(String inputVideo, String outputGIF, 
                                        int fps, int width) {
    try {
        // 生成调色板
        String paletteFile = "palette.png";
        String paletteCommand = String.format(
            "ffmpeg -i %s -vf \"fps=%d,scale=%d:-1:flags=lanczos,palettegen\" %s",
            inputVideo, fps, width, paletteFile);
        
        Process paletteProcess = Runtime.getRuntime().exec(paletteCommand);
        paletteProcess.waitFor();
        
        if (paletteProcess.exitValue() == 0) {
            // 使用调色板生成GIF
            String gifCommand = String.format(
                "ffmpeg -i %s -i %s -filter_complex \"fps=%d,scale=%d:-1:flags=lanczos[x];[x][1:v]paletteuse\" %s",
                inputVideo, paletteFile, fps, width, outputGIF);
            
            Process gifProcess = Runtime.getRuntime().exec(gifCommand);
            gifProcess.waitFor();
            
            if (gifProcess.exitValue() == 0) {
                System.out.println("GIF生成成功: " + outputGIF);
            }
            
            // 清理临时文件
            new File(paletteFile).delete();
        }
    } catch (Exception e) {
        System.err.println("GIF生成失败: " + e.getMessage());
    }
}
```

## 7.2 添加文本

### 7.2.1 Linux环境安装FreeType

在Linux系统中安装FreeType库以支持文本渲染：

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install libfreetype6-dev

# CentOS/RHEL
sudo yum install freetype-devel
sudo yum install libtool libpng-devel

# 编译安装最新版本
wget https://download.savannah.gnu.org/releases/freetype/freetype-2.12.1.tar.gz
tar -xzf freetype-2.12.1.tar.gz
cd freetype-2.12.1
./configure --prefix=/usr/local/freetype
make && sudo make install
```

### 7.2.2 添加英文文本

使用drawtext滤镜添加英文文本：

```bash
# 基本文本添加
ffmpeg -i video.mp4 -vf "drawtext=text='Hello World':x=10:y=10:fontsize=24:fontcolor=white" -codec:a copy output.mp4

# 带时间戳的文本
ffmpeg -i video.mp4 -vf "drawtext=text='Time\: %{pts\:hms}':x=10:y=10:fontsize=24:fontcolor=yellow" -codec:a copy output.mp4

# 滚动文本
ffmpeg -i video.mp4 -vf "drawtext=text='Scrolling Text...':x=w-10*t:y=10:fontsize=24:fontcolor=red" -codec:a copy output.mp4

# 多行文本
ffmpeg -i video.mp4 -vf "drawtext=text='Line 1\\nLine 2':x=10:y=10:fontsize=24:fontcolor=white" -codec:a copy output.mp4
```

**Java实现示例：**

```java
/**
 * 在视频中添加英文文本
 */
public static void addEnglishText(String inputVideo, String outputVideo, 
                                 String text, int x, int y, int fontSize, String color) {
    try {
        String command = String.format(
            "ffmpeg -i %s -vf \"drawtext=text='%s':x=%d:y=%d:fontsize=%d:fontcolor=%s\" -codec:a copy %s",
            inputVideo, text.replace("'", "\\'"), x, y, fontSize, color, outputVideo);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("英文文本添加成功: " + outputVideo);
        }
    } catch (Exception e) {
        System.err.println("添加英文文本失败: " + e.getMessage());
    }
}
```

### 7.2.3 添加中文文本

添加中文文本需要支持中文字体：

```bash
# 使用系统字体
ffmpeg -i video.mp4 -vf "drawtext=text='你好世界':fontfile=/usr/share/fonts/truetype/droid/DroidSansFallbackFull.ttf:x=10:y=10:fontsize=24:fontcolor=white" -codec:a copy output.mp4

# 使用自定义字体
ffmpeg -i video.mp4 -vf "drawtext=text='中文测试':fontfile=/path/to/chinese_font.ttf:x=10:y=10:fontsize=24:fontcolor=yellow" -codec:a copy output.mp4

# 带阴影的中文文本
ffmpeg -i video.mp4 -vf "drawtext=text='带阴影的文本':fontfile=/path/to/font.ttf:x=50:y=50:fontsize=32:fontcolor=white:shadowx=2:shadowy=2:shadowcolor=black" -codec:a copy output.mp4
```

**Java实现示例：**

```java
/**
 * 在视频中添加中文文本
 */
public static void addChineseText(String inputVideo, String outputVideo, 
                                  String text, String fontFile, int x, int y, int fontSize) {
    try {
        String command = String.format(
            "ffmpeg -i %s -vf \"drawtext=text='%s':fontfile=%s:x=%d:y=%d:fontsize=%d:fontcolor=white:shadowx=2:shadowy=2:shadowcolor=black\" -codec:a copy %s",
            inputVideo, text, fontFile, x, y, fontSize, outputVideo);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("中文文本添加成功: " + outputVideo);
        }
    } catch (Exception e) {
        System.err.println("添加中文文本失败: " + e.getMessage());
    }
}
```

## 7.3 添加字幕

### 7.3.1 Linux环境安装libass

安装libass库以支持字幕渲染：

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install libass-dev

# CentOS/RHEL
sudo yum install libass-devel

# 编译安装依赖
sudo yum install fribidi-devel harfbuzz-devel fontconfig-devel

# 编译安装libass
wget https://github.com/libass/libass/releases/download/0.16.0/libass-0.16.0.tar.gz
tar -xzf libass-0.16.0.tar.gz
cd libass-0.16.0
./configure --prefix=/usr/local/libass
make && sudo make install
```

### 7.3.2 Linux安装中文字体

安装中文字体以正确显示中文内容：

```bash
# 安装常见中文字体
sudo apt install fonts-wqy-zenhei fonts-wqy-microhei fonts-arphic-ukai fonts-arphic-uming

# 手动安装字体文件
sudo mkdir -p /usr/share/fonts/chinese
sudo cp chinese_font.ttf /usr/share/fonts/chinese/
sudo chmod 644 /usr/share/fonts/chinese/chinese_font.ttf
sudo fc-cache -fv

# 验证字体安装
fc-list | grep -i chinese
```

### 7.3.3 添加中文字幕

使用字幕文件添加中文字幕：

```bash
# 添加SRT字幕
ffmpeg -i video.mp4 -i subtitles.srt -c:v copy -c:a copy -c:s mov_text -map 0:v:0 -map 0:a:0 -map 1:s:0 output_with_subtitles.mp4

# 烧录字幕到视频（硬字幕）
ffmpeg -i video.mp4 -vf "subtitles=subtitles.srt:force_style='Fontsize=24,PrimaryColour=&Hffffff,BackColour=&H80000000'" output.mp4

# 添加ASS字幕
ffmpeg -i video.mp4 -vf "ass=subtitles.ass" output.mp4

# 字幕样式配置
ffmpeg -i video.mp4 -vf "subtitles=subtitles.srt:force_style='Fontname=Microsoft YaHei,Fontsize=20,PrimaryColour=&H00ffffff,OutlineColour=&H000000,Bold=0'" output.mp4
```

**Java实现示例：**

```java
/**
 * 添加字幕到视频
 */
public static void addSubtitles(String inputVideo, String subtitleFile, 
                               String outputVideo, boolean hardSub) {
    try {
        String command;
        if (hardSub) {
            // 硬字幕（烧录到视频）
            command = String.format(
                "ffmpeg -i %s -vf \"subtitles=%s:force_style='Fontsize=24,PrimaryColour=&Hffffff,BackColour=&H80000000'\" %s",
                inputVideo, subtitleFile, outputVideo);
        } else {
            // 软字幕（可切换）
            command = String.format(
                "ffmpeg -i %s -i %s -c:v copy -c:a copy -c:s mov_text -map 0:v:0 -map 0:a:0 -map 1:s:0 %s",
                inputVideo, subtitleFile, outputVideo);
        }
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("字幕添加成功: " + outputVideo);
            System.out.println("字幕类型: " + (hardSub ? "硬字幕" : "软字幕"));
        }
    } catch (Exception e) {
        System.err.println("添加字幕失败: " + e.getMessage());
    }
}
```

## 7.4 实战项目：卡拉OK音乐短片

### 7.4.1 视频字幕制作工具

创建卡拉OK字幕的专用工具：

```bash
# 创建Karaoke字幕样式
ffmpeg -i video.mp4 -vf "ass=karaoke.ass" karaoke_output.mp4

# 带动画效果的字幕
ffmpeg -i video.mp4 -vf "drawtext=textfile=lyrics.txt:fontfile=font.ttf:x=w-tw-10:y=h-th-10:fontsize=24:fontcolor=white:shadowx=2:shadowy=2:shadowcolor=black" output.mp4
```

### 7.4.2 制作卡拉OK字幕

实现卡拉OK字幕的同步效果：

**Java实现示例：**

```java
/**
 * 创建卡拉OK字幕效果
 */
public static void createKaraokeEffect(String inputVideo, String outputVideo, 
                                      List<KaraokeLyric> lyrics) {
    try {
        // 生成ASS字幕文件
        String assFile = generateKaraokeASS(lyrics);
        
        // 应用字幕
        String command = String.format(
            "ffmpeg -i %s -vf \"ass=%s\" -c:a copy %s",
            inputVideo, assFile, outputVideo);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("卡拉OK字幕创建成功: " + outputVideo);
        }
        
        // 清理临时文件
        new File(assFile).delete();
        
    } catch (Exception e) {
        System.err.println("创建卡拉OK字幕失败: " + e.getMessage());
    }
}

/**
 * 生成卡拉OK格式的ASS字幕
 */
private static String generateKaraokeASS(List<KaraokeLyric> lyrics) {
    StringBuilder assContent = new StringBuilder();
    assContent.append("[Script Info]\n");
    assContent.append("Title: Karaoke Subtitles\n");
    assContent.append("ScriptType: v4.00+\n\n");
    
    assContent.append("[V4+ Styles]\n");
    assContent.append("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
    assContent.append("Style: Default,Microsoft YaHei,32,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,0,0,0,0,100,100,0,0,1,2,0,2,0,0,0,1\n\n");
    
    assContent.append("[Events]\n");
    assContent.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");
    
    for (KaraokeLyric lyric : lyrics) {
        String startTime = formatTime(lyric.getStartTime());
        String endTime = formatTime(lyric.getEndTime());
        
        // 生成卡拉OK效果（渐变色）
        String karaokeText = String.format("{\\k%d}%s", 
            lyric.getDuration() / 10, lyric.getText());
        
        assContent.append(String.format("Dialogue: 0,%s,%s,Default,,0,0,0,,%s\n", 
            startTime, endTime, karaokeText));
    }
    
    String assFile = "karaoke.ass";
    try (FileWriter writer = new FileWriter(assFile)) {
        writer.write(assContent.toString());
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    return assFile;
}
```

## 7.5 小结

本章学习了在视频中添加图文的各种技术：

1. **图片处理**: 添加水印、清除区域、生成GIF
2. **文本添加**: 英文和中文文本的渲染
3. **字幕支持**: 软硬字幕的实现和样式配置
4. **实战应用**: 卡拉OK字幕制作

这些技术可以实现丰富的视频图文效果，让视频内容更加生动有趣。在实际应用中，可以根据需求选择合适的图文添加方式，并注意字体版权和显示效果。