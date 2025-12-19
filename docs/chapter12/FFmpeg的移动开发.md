# 第12章 FFmpeg的移动开发

## 本章简介

本章将详细介绍如何在移动平台上使用 FFmpeg，主要涵盖 Android 开发环境的搭建、交叉编译、音视频播放等核心内容。通过学习本章，你将掌握在移动设备上集成和使用 FFmpeg 的完整流程。

## 目录

- [12.1 搭建Android开发环境](#121-搭建android开发环境)
  - [12.1.1 搭建Android的NDK开发环境](#1211-搭建android的ndk开发环境)
  - [12.1.2 交叉编译Android需要的SO库](#1212-交叉编译android需要的so库)
  - [12.1.3 App工程调用FFmpeg的SO库](#1213-app工程调用ffmpeg的so库)
- [12.2 App通过FFmpeg播放音频](#122-app通过ffmpeg播放音频)
  - [12.2.1 交叉编译时集成mp3lame](#1221-交叉编译时集成mp3lame)
  - [12.2.2 通过AudioTrack播放音频](#1222-throughaudiotrack播放音频)
  - [12.2.3 使用OpenSL ES播放音频](#1223-使用opensl-es播放音频)
- [12.3 App通过FFmpeg播放视频](#123-app通过ffmpeg播放视频)
  - [12.3.1 交叉编译时集成x264和FreeType](#1231-交叉编译时集成x264和freetype)
  - [12.3.2 通过ANativeWindow播放视频](#1232-throughanativewindow播放视频)
  - [12.3.3 使用OpenGL ES播放视频](#1233-使用opengl-es播放视频)
- [12.4 实战项目：仿剪映的视频剪辑](#124-实战项目仿剪映的视频剪辑)
- [12.5 小结](#125-小结)

---

## 12.1 搭建Android开发环境

### 12.1.1 搭建Android的NDK开发环境

#### 1. 安装Android Studio

首先需要安装 Android Studio，这是Android开发的官方IDE。下载地址：https://developer.android.com/studio

#### 2. 配置NDK

1. 打开 Android Studio
2. 点击 File → Settings → Appearance & Behavior → System Settings → Android SDK
3. 选择 SDK Tools 标签页
4. 勾选 NDK (Side by side) 和 CMake
5. 点击 Apply 进行安装

#### 3. 环境变量配置

在系统环境变量中添加：

```bash
# Windows
ANDROID_HOME=C:\Users\用户名\AppData\Local\Android\Sdk
NDK_HOME=%ANDROID_HOME%\ndk\25.1.8937393

# Linux/Mac
export ANDROID_HOME=$HOME/Android/Sdk
export NDK_HOME=$ANDROID_HOME/ndk/25.1.8937393
```

#### 4. 验证安装

```bash
# 验证NDK是否正确安装
$NDK_HOME/ndk-build --version
```

### 12.1.2 交叉编译Android需要的SO库

#### 1. 下载FFmpeg源码

```bash
wget https://ffmpeg.org/releases/ffmpeg-4.4.tar.gz
tar -zxvf ffmpeg-4.4.tar.gz
cd ffmpeg-4.4
```

#### 2. 配置编译脚本

创建编译脚本 `build_android.sh`：

```bash
#!/bin/bash

# 设置NDK路径
NDK_ROOT=/path/to/ndk
TOOLCHAIN=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64

# 设置API级别
API=21

# 设置目标架构
ARCH=arm64-v8a
CPU=armv8-a

# 输出目录
PREFIX=./android/$ARCH

# 配置FFmpeg
./configure \
    --prefix=$PREFIX \
    --disable-static \
    --enable-shared \
    --disable-doc \
    --disable-programs \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-symver \
    --enable-libx264 \
    --enable-libmp3lame \
    --enable-gpl \
    --enable-nonfree \
    --cross-prefix=$TOOLCHAIN/bin/aarch64-linux-android21- \
    --target-os=android \
    --arch=$ARCH \
    --cpu=$CPU \
    --cc=$TOOLCHAIN/bin/aarch64-linux-android21-clang \
    --cxx=$TOOLCHAIN/bin/aarch64-linux-android21-clang++

# 编译
make clean
make -j8
make install
```

### 12.1.3 App工程调用FFmpeg的SO库

#### 1. 集成FFmpeg库

将编译好的SO库复制到项目：

```
app/src/main/jniLibs/
├── arm64-v8a/
│   ├── libavcodec.so
│   ├── libavformat.so
│   ├── libavutil.so
│   ├── libswresample.so
│   └── libswscale.so
├── armeabi-v7a/
├── x86/
└── x86_64/
```

#### 2. 配置CMake

在 `CMakeLists.txt` 中添加：

```cmake
# 添加FFmpeg库
add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libavcodec.so)

# 链接到native库
target_link_libraries(native-lib
        avcodec avformat avutil swresample swscale)
```

---

## 12.2 App通过FFmpeg播放音频

### 12.2.1 交叉编译时集成mp3lame

#### 1. 下载mp3lame源码

```bash
wget https://downloads.sourceforge.net/project/lame/lame/3.100/lame-3.100.tar.gz
tar -zxvf lame-3.100.tar.gz
cd lame-3.100
```

#### 2. 编译mp3lame

创建编译脚本：

```bash
#!/bin/bash

NDK_ROOT=/path/to/ndk
TOOLCHAIN=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64
PREFIX=./android/mp3lame

./configure \
    --prefix=$PREFIX \
    --host=arm-linux \
    --disable-static \
    --enable-shared \
    --disable-frontend \
    CC=$TOOLCHAIN/bin/aarch64-linux-android21-clang \
    CXX=$TOOLCHAIN/bin/aarch64-linux-android21-clang++

make clean
make -j8
make install
```

### 12.2.2 通过AudioTrack播放音频

#### 1. AudioTrack基础

AudioTrack 是 Android 提供的音频播放类：

```java
public class AudioTrackPlayer {
    private AudioTrack audioTrack;
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    
    public void init() {
        int bufferSize = AudioTrack.getMinBufferSize(
            sampleRate, channelConfig, audioFormat);
        
        audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM
        );
    }
    
    public void play() {
        if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.play();
        }
    }
    
    public void writeData(byte[] audioData) {
        if (audioTrack != null) {
            audioTrack.write(audioData, 0, audioData.length);
        }
    }
}
```

### 12.2.3 使用OpenSL ES播放音频

#### 1. OpenSL ES简介

OpenSL ES 是一套针对嵌入式系统的跨平台音频API，提供低延迟的音频处理能力。

#### 2. 初始化OpenSL ES

```cpp
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

class OpenSLESPlayer {
private:
    SLObjectItf engineObj;
    SLEngineItf engine;
    SLObjectItf outputMixObj;
    SLObjectItf playerObj;
    SLPlayItf player;
    SLAndroidSimpleBufferQueueItf bufferQueue;
    
public:
    int init() {
        // 创建引擎
        slCreateEngine(&engineObj, 0, nullptr, 0, nullptr, nullptr);
        (*engineObj)->Realize(engineObj, SL_BOOLEAN_FALSE);
        (*engineObj)->GetInterface(engineObj, SL_IID_ENGINE, &engine);
        
        // 创建输出混合
        (*engine)->CreateOutputMix(engine, &outputMixObj, 0, nullptr, nullptr);
        (*outputMixObj)->Realize(outputMixObj, SL_BOOLEAN_FALSE);
        
        return 0;
    }
};
```

---

## 12.3 App通过FFmpeg播放视频

### 12.3.1 交叉编译时集成x264和FreeType

#### 1. 编译x264

```bash
#!/bin/bash

NDK_ROOT=/path/to/ndk
TOOLCHAIN=$NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64

export CC=$TOOLCHAIN/bin/aarch64-linux-android21-clang
export CXX=$TOOLCHAIN/bin/aarch64-linux-android21-clang++

./configure \
    --prefix=./android/x264 \
    --host=arm-linux \
    --enable-shared \
    --disable-static \
    --enable-pic \
    --disable-cli \
    --cross-prefix=aarch64-linux-android21-

make clean
make -j8
make install
```

### 12.3.2 通过ANativeWindow播放视频

#### 1. ANativeWindow基础

ANativeWindow 是 Android NDK 提供的原生窗口接口：

```cpp
#include <android/native_window.h>
#include <android/native_window_jni.h>

class VideoRenderer {
private:
    ANativeWindow *nativeWindow;
    int windowWidth;
    int windowHeight;
    
public:
    void init(JNIEnv* env, jobject surface) {
        nativeWindow = ANativeWindow_fromSurface(env, surface);
        ANativeWindow_acquire(nativeWindow);
        
        // 设置窗口参数
        ANativeWindow_setBuffersGeometry(nativeWindow, 
            windowWidth, windowHeight, WINDOW_FORMAT_RGBA_8888);
    }
    
    void renderFrame(AVFrame* frame) {
        if (!nativeWindow) return;
        
        ANativeWindow_Buffer buffer;
        if (ANativeWindow_lock(nativeWindow, &buffer, nullptr) == 0) {
            // 复制视频帧到窗口
            copyFrameToBuffer(frame, &buffer);
            ANativeWindow_unlockAndPost(nativeWindow);
        }
    }
};
```

### 12.3.3 使用OpenGL ES播放视频

#### 1. OpenGL ES渲染器

```cpp
#include <GLES2/gl2.h>
#include <EGL/egl.h>

class OpenGLRenderer {
private:
    EGLDisplay display;
    EGLContext context;
    EGLSurface surface;
    
    GLuint program;
    GLuint texture;
    
public:
    int init() {
        // 初始化EGL
        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(display, nullptr, nullptr);
        
        // 创建着色器程序
        program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        
        // 创建纹理
        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        
        return 0;
    }
    
    void renderFrame(AVFrame* frame) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        // 更新纹理数据
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, frame->width, frame->height, 
                     0, GL_RGBA, GL_UNSIGNED_BYTE, frame->data[0]);
        
        // 绘制
        glUseProgram(program);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        
        // 交换缓冲区
        eglSwapBuffers(display, surface);
    }
};
```

---

## 12.4 实战项目：仿剪映的视频剪辑

### 项目概述

本节将实现一个仿剪映的视频剪辑应用，包含视频剪辑、滤镜效果、音频处理等核心功能。

### 核心功能

#### 1. 视频剪辑管理器

```java
public class VideoEditor {
    private List<VideoClip> clips;
    private List<AudioClip> audioClips;
    private int currentFilter = FILTER_NONE;
    private VideoRenderer renderer;
    
    public static final int FILTER_NONE = 0;
    public static final int FILTER_GRAYSCALE = 1;
    public static final int FILTER_SEPIA = 2;
    public static final int FILTER_BRIGHTNESS = 3;
    
    public void addVideoClip(VideoClip clip) {
        clips.add(clip);
        sortClips();
    }
    
    public void applyFilter(int filterType) {
        this.currentFilter = filterType;
    }
}
```

#### 2. 滤镜效果实现

```cpp
class VideoFilter {
public:
    enum FilterType {
        FILTER_NONE,
        FILTER_GRAYSCALE,
        FILTER_SEPIA,
        FILTER_BRIGHTNESS,
        FILTER_CONTRAST,
        FILTER_SATURATION
    };
    
    static void applyFilter(AVFrame* frame, FilterType type, float progress = 1.0f) {
        switch (type) {
            case FILTER_GRAYSCALE:
                applyGrayscaleFilter(frame);
                break;
            case FILTER_SEPIA:
                applySepiaFilter(frame);
                break;
            case FILTER_BRIGHTNESS:
                applyBrightnessFilter(frame, progress);
                break;
        }
    }
};
```

---

## 12.5 小结

本章详细介绍了 FFmpeg 在移动平台上的应用，主要包括：

### 核心知识点

1. **Android开发环境搭建**
   - NDK环境配置
   - 交叉编译技术
   - SO库集成方法

2. **音频处理技术**
   - AudioTrack播放音频
   - OpenSL ES低延迟播放
   - mp3lame编码集成

3. **视频处理技术**
   - ANativeWindow视频渲染
   - OpenGL ES高性能渲染
   - x264和FreeType集成

4. **实战项目经验**
   - 视频剪辑架构设计
   - 滤镜和转场效果实现
   - 音视频同步处理

### 技术要点

- **性能优化**：使用多线程处理、硬件加速、内存池等技术
- **内存管理**：及时释放FFmpeg资源，避免内存泄漏
- **错误处理**：完善的异常处理机制
- **用户体验**：流畅的UI响应、实时的预览效果

### 学习建议

1. 深入理解FFmpeg的API使用
2. 掌握移动平台的特性限制
3. 注重代码的可维护性和扩展性
4. 多实践，积累项目经验

通过本章的学习，读者应该能够独立开发移动端的音视频处理应用，并为后续的深入学习打下坚实基础。