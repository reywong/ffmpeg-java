# FFmpeg Java 视频剪辑教程项目

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 📹 **从零开始学习FFmpeg视频剪辑开发，手把手教你实现自己的"剪映"**

## 📖 项目简介

本项目是一个完整的FFmpeg视频剪辑教程项目，通过文档和代码示例的方式，系统性地介绍如何使用Java和FFmpeg开发视频剪辑功能。项目结合Spring Boot框架，提供现代化的视频处理解决方案。

### ✨ 主要特性

- 🎬 **完整的视频剪辑流程**：从环境搭建到实际项目开发
- 💻 **Java + Spring Boot**：现代化的技术栈
- 📚 **丰富的示例代码**：涵盖各种视频处理场景
- 🔧 **实战项目导向**：从理论到实践的完整学习路径
- 🌐 **跨平台支持**：Windows、Linux、Android等多平台开发

## 🚀 快速开始

### 环境要求

- **Java** 17+
- **Maven** 3.6+
- **FFmpeg** 4.0+
- **Spring Boot** 3.2.0

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/ffmpeg-java.git
   cd ffmpeg-java
   ```

2. **安装FFmpeg**
    - **Windows**: 下载预编译版本并配置环境变量
    - **Linux**: `sudo apt-get install ffmpeg` 或编译安装
    - **macOS**: `brew install ffmpeg`

3. **运行示例项目**
   ```bash
   cd examples
   mvn clean install
   mvn spring-boot:run
   ```

## 📁 项目结构

```
ffmpeg-java/
├── docs/                    # 简易文档
│   ├── chapter01/          # 第1章：FFmpeg环境搭建
│   ├── chapter02/          # 第2章：FFmpeg开发基础
│   ├── ...                 # 其他章节文档
│   └── README.md           # 文档说明
├── books/                   # 详细文档和教程
├── examples/                # 代码示例
│   ├── src/main/java/      # Java源码
│   ├── pom.xml             # Maven配置
│   └── README.md           # 示例说明
├── tools/                   # 第三方工具
├── README.md               # 项目说明
└── LICENSE                 # 开源协议
```

## 📚 学习路径

### 🎯 基础篇（第1-3章）
- [x] **第1章** - FFmpeg环境搭建
- [x] **第2章** - FFmpeg开发基础
- [x] **第3章** - FFmpeg的编解码

### 🎨 进阶篇（第4-7章）
- [x] **第4章** - FFmpeg处理图像
- [x] **第5章** - FFmpeg处理音频
- [x] **第6章** - FFmpeg加工视频
- [x] **第7章** - FFmpeg添加图文

### 🔧 高级篇（第8-12章）
- [x] **第8章** - FFmpeg自定义滤镜
- [x] **第9章** - FFmpeg混合音视频
- [x] **第10章** - FFmpeg播放音视频
- [x] **第11章** - FFmpeg的桌面开发
- [x] **第12章** - FFmpeg的移动开发

## 💡 核心功能

### 视频处理
- 🔄 **格式转换**: MP4、AVI、MKV等格式互转
- ✂️ **视频剪辑**: 精确的片段切割和合并
- 🎭 **滤镜效果**: 模糊、锐化、色彩调整等特效
- 📐 **变换操作**: 旋转、缩放、裁剪、翻转

### 音频处理
- 🎵 **格式转换**: MP3、WAV、AAC、FLAC等
- 🔊 **音效处理**: 淡入淡出、音量调节
- 🎤 **混音功能**: 多轨道音频合并

### 图文处理
- 📝 **文字水印**: 支持中英文字符
- 🖼️ **图片水印**: Logo、图标添加
- 📱 **字幕功能**: SRT字幕文件支持

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 主要开发语言 |
| Spring Boot | 3.2.0 | Web框架 |
| FFmpeg | 4.0+ | 音视频处理核心 |
| Maven | 3.6+ | 项目构建工具 |
| Lombok | - | 代码简化工具 |

## 📖 使用示例

### 基础视频转换
```java
// 示例：将MP4转换为AVI
FFmpegConverter converter = new FFmpegConverter();
converter.convert("input.mp4", "output.avi", "avi");
```

### 视频剪辑
```java
// 示例：裁剪视频片段
VideoCutter cutter = new VideoCutter();
cutter.cutVideo("input.mp4", "output.mp4", 10, 30); // 从10秒到30秒
```

### 添加水印
```java
// 示例：添加文字水印
Watermark watermark = new Watermark();
watermark.addTextWatermark("input.mp4", "output.mp4", "版权所有", "center");
```

更多示例请查看 [examples](./examples) 目录。

## 🌟 实战项目

### 🎬 基础项目
- **图片转视频**: 将静态图片序列转换为视频
- **音频拼接**: 多段音频无缝合并
- **视频格式分析工具**: 查看视频详细信息

### 🚀 高级项目
- **桌面影音播放器**: 基于Qt的播放器应用
- **仿剪映视频剪辑**: 移动端视频剪辑App
- **实时直播推流**: 网络直播解决方案

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔗 相关资源

### 官方文档
- [FFmpeg官方文档](https://ffmpeg.org/documentation.html)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Java官方文档](https://docs.oracle.com/en/java/)

### 学习资源
- [FFmpeg开发指南](https://ffmpeg.org/ffmpeg-faq.html)
- [音视频开发教程](docs/)
- [示例代码库](examples/)

## 📞 联系方式

- 📧 **邮箱**: your-email@example.com
- 🐛 **问题反馈**: [GitHub Issues](https://github.com/your-repo/ffmpeg-java/issues)
- 💬 **讨论**: [GitHub Discussions](https://github.com/your-repo/ffmpeg-java/discussions)

## 🏆 致谢

感谢所有为这个项目做出贡献的开发者，以及FFmpeg社区提供的优秀工具和文档。

---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！