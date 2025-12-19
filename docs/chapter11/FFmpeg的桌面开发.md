# 第11章 FFmpeg的桌面开发

## 11.1 搭建Qt开发环境

### 11.1.1 安装桌面开发工具Qt

Qt是一个跨平台的C++应用程序开发框架，特别适合开发桌面多媒体应用。

**Windows环境安装Qt：**
```bash
# 下载Qt安装包
# 官网：https://www.qt.io/download

# 选择开源版本安装
# 包含Qt Creator IDE和必要的库文件

# 配置环境变量
set QT_PATH=C:\Qt\5.15.2\msvc2019_64
set PATH=%QT_PATH%\bin;%PATH%
```

**Linux环境安装Qt：**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install qt5-default qtcreator qt5-qmake

# CentOS/RHEL
sudo yum groupinstall "Development Tools"
sudo yum install qt5-qtbase-devel qt5-qttools-devel

# 验证安装
qmake --version
```

**macOS环境安装Qt：**
```bash
# 使用Homebrew安装
brew install qt@5

# 或下载官方安装包
# https://www.qt.io/download

# 配置环境变量
export PATH=/usr/local/opt/qt@5/bin:$PATH
```

### 11.1.2 创建一个基于C++的Qt项目

**使用Qt Creator创建项目：**
```cpp
// 项目结构
FFmpegPlayer/
├── FFmpegPlayer.pro
├── main.cpp
├── mainwindow.cpp
├── mainwindow.h
├── mainwindow.ui
└── ffmpeg/
    ├── player.h
    ├── player.cpp
    └── CMakeLists.txt
```

**FFmpegPlayer.pro文件配置：**
```pro
QT += core gui multimedia widgets

TARGET = FFmpegPlayer
TEMPLATE = app

# FFmpeg库配置
FFMPEG_LIBS = -L$$PWD/ffmpeg/lib -lavformat -lavcodec -lavutil -lswscale -lswresample
FFMPEG_INCLUDE = $$PWD/ffmpeg/include

LIBS += $$FFMPEG_LIBS
INCLUDEPATH += $$FFMPEG_INCLUDE

# Windows平台特定配置
win32 {
    LIBS += -lole32 -lstrmiids -luuid
}

# Linux平台特定配置
unix {
    LIBS += -lpthread -lm -ldl
}
```

**main.cpp入口文件：**
```cpp
#include <QApplication>
#include "mainwindow.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    
    // 设置应用程序信息
    app.setApplicationName("FFmpeg Player");
    app.setApplicationVersion("1.0");
    app.setOrganizationName("FFmpeg Examples");
    
    MainWindow window;
    window.show();
    
    return app.exec();
}
```

### 11.1.3 把Qt项目打包成可执行文件

**Windows平台打包：**
```bash
# 使用windeployqt工具
windeployqt.exe FFmpegPlayer.exe

# 手动复制FFmpeg DLL文件
copy ffmpeg\bin\*.dll release\

# 使用NSIS创建安装包
# 编写NSIS脚本文件
```

**Linux平台打包：**
```bash
# 创建AppImage
mkdir FFmpegPlayer.AppDir
cp FFmpegPlayer FFmpegPlayer.AppDir/
cp -r /usr/lib/x86_64-linux-gnu/qt5 FFmpegPlayer.AppDir/
cp ffmpeg/lib/*.so FFmpegPlayer.AppDir/

# 使用linuxdeploy
linuxdeploy --appdir FFmpegPlayer.AppDir --executable FFmpegPlayer --output appimage
```

**macOS平台打包：**
```bash
# 创建应用程序包
mkdir -p FFmpegPlayer.app/Contents/MacOS
mkdir -p FFmpegPlayer.app/Contents/Frameworks

cp FFmpegPlayer FFmpegPlayer.app/Contents/MacOS/
cp -r Qt*.framework FFmpegPlayer.app/Contents/Frameworks/
cp ffmpeg/lib/*.dylib FFmpegPlayer.app/Contents/Frameworks/

# 使用macdeployqt
macdeployqt FFmpegPlayer.app
```

## 11.2 桌面程序播放音频

### 11.2.1 给Qt工程集成FFmpeg

**FFmpeg播放器头文件：**
```cpp
#ifndef AUDIOPLAYER_H
#define AUDIOPLAYER_H

#include <QObject>
#include <QTimer>
#include <QByteArray>
#include <memory>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/opt.h>
#include <libswresample/swresample.h>
}

class AudioPlayer : public QObject
{
    Q_OBJECT
    
public:
    explicit AudioPlayer(QObject *parent = nullptr);
    ~AudioPlayer();
    
    bool loadFile(const QString &filePath);
    void play();
    void pause();
    void stop();
    void setVolume(float volume);
    
    qint64 duration() const;
    qint64 position() const;
    void setPosition(qint64 position);
    
signals:
    void positionChanged(qint64 position);
    void durationChanged(qint64 duration);
    void stateChanged(int state);
    
private slots:
    void onTimer();
    
private:
    void initFFmpeg();
    void cleanupFFmpeg();
    bool decodeAudioFrame();
    
    AVFormatContext *m_formatContext;
    AVCodecContext *m_codecContext;
    SwrContext *m_swrContext;
    
    int m_audioStreamIndex;
    QTimer *m_timer;
    
    QByteArray m_audioBuffer;
    qint64 m_currentPosition;
    qint64 m_totalDuration;
    
    bool m_isPlaying;
    bool m_isPaused;
    float m_volume;
};

#endif // AUDIOPLAYER_H
```

**FFmpeg播放器实现文件：**
```cpp
#include "audioplayer.h"
#include <QDebug>
#include <QFile>

AudioPlayer::AudioPlayer(QObject *parent)
    : QObject(parent)
    , m_formatContext(nullptr)
    , m_codecContext(nullptr)
    , m_swrContext(nullptr)
    , m_audioStreamIndex(-1)
    , m_timer(new QTimer(this))
    , m_currentPosition(0)
    , m_totalDuration(0)
    , m_isPlaying(false)
    , m_isPaused(false)
    , m_volume(1.0f)
{
    initFFmpeg();
    connect(m_timer, &QTimer::timeout, this, &AudioPlayer::onTimer);
}

AudioPlayer::~AudioPlayer()
{
    stop();
    cleanupFFmpeg();
}

void AudioPlayer::initFFmpeg()
{
    // 注册所有编解码器和格式
    av_register_all();
    avformat_network_init();
}

void AudioPlayer::cleanupFFmpeg()
{
    if (m_swrContext) {
        swr_free(&m_swrContext);
    }
    if (m_codecContext) {
        avcodec_free_context(&m_codecContext);
    }
    if (m_formatContext) {
        avformat_close_input(&m_formatContext);
    }
}

bool AudioPlayer::loadFile(const QString &filePath)
{
    // 清理之前的资源
    cleanupFFmpeg();
    
    // 打开输入文件
    if (avformat_open_input(&m_formatContext, filePath.toUtf8().constData(), nullptr, nullptr) != 0) {
        qWarning() << "无法打开文件:" << filePath;
        return false;
    }
    
    // 获取流信息
    if (avformat_find_stream_info(m_formatContext, nullptr) < 0) {
        qWarning() << "无法获取流信息";
        return false;
    }
    
    // 查找音频流
    m_audioStreamIndex = av_find_best_stream(m_formatContext, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);
    if (m_audioStreamIndex < 0) {
        qWarning() << "未找到音频流";
        return false;
    }
    
    // 获取编解码器参数
    AVCodecParameters *codecParams = m_formatContext->streams[m_audioStreamIndex]->codecpar;
    
    // 查找解码器
    const AVCodec *codec = avcodec_find_decoder(codecParams->codec_id);
    if (!codec) {
        qWarning() << "未找到解码器";
        return false;
    }
    
    // 创建编解码器上下文
    m_codecContext = avcodec_alloc_context3(codec);
    if (!m_codecContext) {
        qWarning() << "无法创建编解码器上下文";
        return false;
    }
    
    // 复制编解码器参数
    if (avcodec_parameters_to_context(m_codecContext, codecParams) < 0) {
        qWarning() << "无法复制编解码器参数";
        return false;
    }
    
    // 打开解码器
    if (avcodec_open2(m_codecContext, codec, nullptr) < 0) {
        qWarning() << "无法打开解码器";
        return false;
    }
    
    // 计算总时长
    m_totalDuration = m_formatContext->duration / 1000; // 转换为毫秒
    emit durationChanged(m_totalDuration);
    
    return true;
}

void AudioPlayer::play()
{
    if (!m_formatContext) return;
    
    m_isPlaying = true;
    m_isPaused = false;
    m_timer->start(100); // 100ms更新一次
    emit stateChanged(1); // Playing state
}

void AudioPlayer::pause()
{
    m_isPaused = !m_isPaused;
    emit stateChanged(m_isPaused ? 2 : 1); // Paused or Playing
}

void AudioPlayer::stop()
{
    m_isPlaying = false;
    m_isPaused = false;
    m_currentPosition = 0;
    m_timer->stop();
    emit stateChanged(0); // Stopped state
    emit positionChanged(0);
}

void AudioPlayer::onTimer()
{
    if (!m_isPlaying || m_isPaused) return;
    
    // 更新播放位置
    AVPacket *packet = av_packet_alloc();
    
    if (av_read_frame(m_formatContext, packet) >= 0) {
        if (packet->stream_index == m_audioStreamIndex) {
            // 解码音频帧
            AVFrame *frame = av_frame_alloc();
            
            int ret = avcodec_send_packet(m_codecContext, packet);
            if (ret == 0) {
                ret = avcodec_receive_frame(m_codecContext, frame);
                if (ret == 0) {
                    // 这里应该将音频数据发送到音频输出设备
                    // 简化起见，这里只更新位置
                    m_currentPosition = (int64_t)frame->pts * 1000 * 
                                        m_formatContext->streams[m_audioStreamIndex]->time_base.num / 
                                        m_formatContext->streams[m_audioStreamIndex]->time_base.den;
                    emit positionChanged(m_currentPosition);
                }
            }
            
            av_frame_free(&frame);
        }
        av_packet_unref(packet);
    } else {
        // 播放结束
        stop();
    }
    
    av_packet_free(&packet);
}
```

### 11.2.2 Qt工程使用SDL播放音频

**SDL音频播放器类：**
```cpp
#ifndef SDLAUDIOPLAYER_H
#define SDLAUDIOPLAYER_H

#include <QObject>
#include <QTimer>
#include <QByteArray>

extern "C" {
#include <SDL2/SDL.h>
}

class SDLAudioPlayer : public QObject
{
    Q_OBJECT
    
public:
    explicit SDLAudioPlayer(QObject *parent = nullptr);
    ~SDLAudioPlayer();
    
    bool initSDL();
    void cleanupSDL();
    
    bool openAudio(int sampleRate, int channels, Uint16 format);
    void closeAudio();
    
    void playAudio(const QByteArray &audioData);
    void pauseAudio();
    void stopAudio();
    
    void setVolume(int volume);
    int getVolume() const;
    
signals:
    void audioFinished();
    
private:
    void audioCallback(Uint8 *stream, int len);
    
    static void s_audioCallback(void *userdata, Uint8 *stream, int len);
    
    SDL_AudioSpec m_audioSpec;
    SDL_AudioDeviceID m_audioDevice;
    
    QByteArray m_audioBuffer;
    int m_bufferPos;
    int m_volume;
    
    bool m_isInitialized;
    bool m_isPlaying;
    bool m_isPaused;
};

#endif // SDLAUDIOPLAYER_H
```

### 11.2.3 通过QAudioSink播放音频

**Qt音频输出播放器：**
```cpp
#ifndef QAUDIPLAYER_H
#define QAUDIPLAYER_H

#include <QObject>
#include <QAudioOutput>
#include <QAudioDeviceInfo>
#include <QBuffer>
#include <QByteArray>

class QAudioPlayer : public QObject
{
    Q_OBJECT
    
public:
    explicit QAudioPlayer(QObject *parent = nullptr);
    ~QAudioPlayer();
    
    bool initAudio(int sampleRate, int channels, int sampleSize);
    
    void play(const QByteArray &audioData);
    void pause();
    void stop();
    
    void setVolume(qreal volume);
    qreal getVolume() const;
    
    bool isPlaying() const;
    
signals:
    void stateChanged(QAudio::State state);
    void positionChanged(qint64 position);
    
private slots:
    void handleStateChanged(QAudio::State state);
    
private:
    QAudioOutput *m_audioOutput;
    QIODevice *m_audioDevice;
    QBuffer *m_audioBuffer;
    
    int m_sampleRate;
    int m_channels;
    int m_sampleSize;
    
    bool m_isInitialized;
};

#endif // QAUDIPLAYER_H
```

## 11.3 桌面程序播放视频

### 11.3.1 通过QImage播放视频

**QImage视频播放器：**
```cpp
#ifndef QIMAGEVIDEOPLAYER_H
#define QIMAGEVIDEOPLAYER_H

#include <QObject>
#include <QTimer>
#include <QImage>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
}

class QImageVideoPlayer : public QObject
{
    Q_OBJECT
    
public:
    explicit QImageVideoPlayer(QObject *parent = nullptr);
    ~QImageVideoPlayer();
    
    bool loadVideo(const QString &filePath);
    void play();
    void pause();
    void stop();
    
    QImage getCurrentFrame() const;
    
    qint64 duration() const;
    qint64 position() const;
    void setPosition(qint64 position);
    
signals:
    void frameReady(const QImage &frame);
    void positionChanged(qint64 position);
    void durationChanged(qint64 duration);
    
private slots:
    void onVideoTimer();
    
private:
    bool decodeVideoFrame();
    void cleanup();
    
    AVFormatContext *m_formatContext;
    AVCodecContext *m_codecContext;
    SwsContext *m_swsContext;
    
    int m_videoStreamIndex;
    QTimer *m_videoTimer;
    
    QImage m_currentFrame;
    qint64 m_currentPosition;
    qint64 m_totalDuration;
    
    bool m_isPlaying;
    bool m_isPaused;
};

#endif // QIMAGEVIDEOPLAYER_H
```

### 11.3.2 OpenGL的着色器小程序

**OpenGL着色器：**
```glsl
// 顶点着色器
#version 330 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aTexCoord;

out vec2 TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
}
```

```glsl
// 片段着色器
#version 330 core

in vec2 TexCoord;
out vec4 FragColor;

uniform sampler2D videoTexture;

uniform float brightness = 1.0;
uniform float contrast = 1.0;
uniform float saturation = 1.0;

void main()
{
    vec3 color = texture(videoTexture, TexCoord).rgb;
    
    // 亮度调整
    color *= brightness;
    
    // 对比度调整
    color = (color - 0.5) * contrast + 0.5;
    
    // 饱和度调整
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, saturation);
    
    FragColor = vec4(color, 1.0);
}
```

### 11.3.3 使用OpenGL播放视频

**OpenGL视频播放器：**
```cpp
#ifndef OPENGLVIDEOPLAYER_H
#define OPENGLVIDEOPLAYER_H

#include <QObject>
#include <QOpenGLWidget>
#include <QOpenGLFunctions_3_3_Core>
#include <QTimer>
#include <QImage>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
}

class OpenGLVideoPlayer : public QOpenGLWidget, protected QOpenGLFunctions_3_3_Core
{
    Q_OBJECT
    
public:
    explicit OpenGLVideoPlayer(QWidget *parent = nullptr);
    ~OpenGLVideoPlayer();
    
    bool loadVideo(const QString &filePath);
    void play();
    void pause();
    void stop();
    
    void setBrightness(float brightness);
    void setContrast(float contrast);
    void setSaturation(float saturation);
    
signals:
    void videoFinished();
    
protected:
    void initializeGL() override;
    void paintGL() override;
    void resizeGL(int w, int h) override;
    
private slots:
    void updateVideoFrame();
    
private:
    void setupShaders();
    void setupTextures();
    bool decodeVideoFrame();
    void updateUniforms();
    
    GLuint m_shaderProgram;
    GLuint m_vertexArrayObject;
    GLuint m_vertexBufferObject;
    GLuint m_texture;
    
    GLint m_brightnessLoc;
    GLint m_contrastLoc;
    GLint m_saturationLoc;
    
    AVFormatContext *m_formatContext;
    AVCodecContext *m_codecContext;
    SwsContext *m_swsContext;
    
    int m_videoStreamIndex;
    QTimer *m_updateTimer;
    
    float m_brightness;
    float m_contrast;
    float m_saturation;
    
    bool m_isPlaying;
    bool m_videoLoaded;
    
    const char *m_vertexShaderSource;
    const char *m_fragmentShaderSource;
};

#endif // OPENGLVIDEOPLAYER_H
```

## 11.4 实战项目：桌面影音播放器

### 播放器主界面设计

**主窗口类：**
```cpp
#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QMediaPlayer>
#include <QVideoWidget>
#include <QSlider>
#include <QPushButton>
#include <QLabel>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QFileDialog>
#include <QMenuBar>
#include <QStatusBar>

#include "videoplayer.h"
#include "audioplayer.h"

QT_BEGIN_NAMESPACE
class QMediaPlayer;
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT
    
public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    
private slots:
    void openFile();
    void playPause();
    void stop();
    void seek(int position);
    void updatePosition(qint64 position);
    void updateDuration(qint64 duration);
    void updateState(QMediaPlayer::State state);
    
    void setVolume(int volume);
    void muteToggled(bool muted);
    
    void about();
    
private:
    void setupUI();
    void setupMenuBar();
    void setupStatusBar();
    void setupConnections();
    
    // UI组件
    QWidget *m_centralWidget;
    QVideoWidget *m_videoWidget;
    QSlider *m_positionSlider;
    QSlider *m_volumeSlider;
    QPushButton *m_playPauseButton;
    QPushButton *m_stopButton;
    QPushButton *m_muteButton;
    QLabel *m_timeLabel;
    QLabel *m_statusLabel;
    
    // 布局
    QVBoxLayout *m_mainLayout;
    QHBoxLayout *m_controlLayout;
    QHBoxLayout *m_volumeLayout;
    
    // 媒体播放器
    QMediaPlayer *m_mediaPlayer;
    
    // 自定义播放器
    VideoPlayer *m_videoPlayer;
    AudioPlayer *m_audioPlayer;
    
    // 状态变量
    QString m_currentFile;
    bool m_isPlaying;
    bool m_isMuted;
};

#endif // MAINWINDOW_H
```

### 播放器功能实现

**主窗口实现：**
```cpp
#include "mainwindow.h"
#include <QApplication>
#include <QMessageBox>
#include <QStyle>
#include <QFileDialog>
#include <QStandardPaths>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , m_centralWidget(nullptr)
    , m_videoWidget(nullptr)
    , m_positionSlider(nullptr)
    , m_volumeSlider(nullptr)
    , m_playPauseButton(nullptr)
    , m_stopButton(nullptr)
    , m_muteButton(nullptr)
    , m_timeLabel(nullptr)
    , m_statusLabel(nullptr)
    , m_mediaPlayer(new QMediaPlayer(this))
    , m_videoPlayer(new VideoPlayer(this))
    , m_audioPlayer(new AudioPlayer(this))
    , m_isPlaying(false)
    , m_isMuted(false)
{
    setupUI();
    setupMenuBar();
    setupStatusBar();
    setupConnections();
    
    setWindowTitle("FFmpeg Desktop Player");
    setMinimumSize(800, 600);
    resize(1200, 800);
}

void MainWindow::setupUI()
{
    m_centralWidget = new QWidget(this);
    setCentralWidget(m_centralWidget);
    
    m_mainLayout = new QVBoxLayout(m_centralWidget);
    
    // 视频显示区域
    m_videoWidget = new QVideoWidget(this);
    m_mediaPlayer->setVideoOutput(m_videoWidget);
    m_mainLayout->addWidget(m_videoWidget, 3);
    
    // 控制区域
    m_controlLayout = new QHBoxLayout();
    
    m_playPauseButton = new QPushButton(this);
    m_playPauseButton->setIcon(style()->standardIcon(QStyle::SP_MediaPlay));
    m_controlLayout->addWidget(m_playPauseButton);
    
    m_stopButton = new QPushButton(this);
    m_stopButton->setIcon(style()->standardIcon(QStyle::SP_MediaStop));
    m_controlLayout->addWidget(m_stopButton);
    
    m_positionSlider = new QSlider(Qt::Horizontal, this);
    m_positionSlider->setRange(0, 0);
    m_controlLayout->addWidget(m_positionSlider);
    
    m_mainLayout->addLayout(m_controlLayout);
    
    // 音量控制区域
    m_volumeLayout = new QHBoxLayout();
    
    m_muteButton = new QPushButton(this);
    m_muteButton->setIcon(style()->standardIcon(QStyle::SP_MediaVolumeMuted));
    m_volumeLayout->addWidget(m_muteButton);
    
    m_volumeSlider = new QSlider(Qt::Horizontal, this);
    m_volumeSlider->setRange(0, 100);
    m_volumeSlider->setValue(70);
    m_volumeLayout->addWidget(m_volumeSlider);
    
    m_timeLabel = new QLabel("00:00:00 / 00:00:00", this);
    m_volumeLayout->addWidget(m_timeLabel);
    
    m_mainLayout->addLayout(m_volumeLayout);
}

void MainWindow::setupConnections()
{
    // 媒体播放器信号连接
    connect(m_mediaPlayer, &QMediaPlayer::positionChanged, this, &MainWindow::updatePosition);
    connect(m_mediaPlayer, &QMediaPlayer::durationChanged, this, &MainWindow::updateDuration);
    connect(m_mediaPlayer, &QMediaPlayer::stateChanged, this, &MainWindow::updateState);
    
    // 控制按钮信号连接
    connect(m_playPauseButton, &QPushButton::clicked, this, &MainWindow::playPause);
    connect(m_stopButton, &QPushButton::clicked, this, &MainWindow::stop);
    connect(m_positionSlider, &QSlider::sliderMoved, this, &MainWindow::seek);
    connect(m_volumeSlider, &QSlider::valueChanged, this, &MainWindow::setVolume);
    connect(m_muteButton, &QPushButton::clicked, this, &MainWindow::muteToggled);
}

void MainWindow::openFile()
{
    QString fileName = QFileDialog::getOpenFileName(
        this,
        tr("打开媒体文件"),
        QStandardPaths::writableLocation(QStandardPaths::MoviesLocation),
        tr("视频文件 (*.mp4 *.avi *.mkv *.mov);;音频文件 (*.mp3 *.wav *.flac);;所有文件 (*.*)")
    );
    
    if (!fileName.isEmpty()) {
        m_currentFile = fileName;
        m_mediaPlayer->setMedia(QUrl::fromLocalFile(fileName));
        m_statusLabel->setText(tr("已加载: %1").arg(fileName));
    }
}

void MainWindow::playPause()
{
    if (m_isPlaying) {
        m_mediaPlayer->pause();
    } else {
        m_mediaPlayer->play();
    }
}

void MainWindow::stop()
{
    m_mediaPlayer->stop();
    m_positionSlider->setValue(0);
}

void MainWindow::seek(int position)
{
    m_mediaPlayer->setPosition(position);
}

void MainWindow::updatePosition(qint64 position)
{
    m_positionSlider->setValue(position);
    
    qint64 duration = m_mediaPlayer->duration();
    QString timeString = QString("%1:%2:%3 / %4:%5:%6")
        .arg(position / 60000, 2, 10, QChar('0'))
        .arg((position % 60000) / 1000, 2, 10, QChar('0'))
        .arg((position % 1000) / 10, 2, 10, QChar('0'))
        .arg(duration / 60000, 2, 10, QChar('0'))
        .arg((duration % 60000) / 1000, 2, 10, QChar('0'))
        .arg((duration % 1000) / 10, 2, 10, QChar('0'));
    
    m_timeLabel->setText(timeString);
}

void MainWindow::updateDuration(qint64 duration)
{
    m_positionSlider->setRange(0, duration);
}

void MainWindow::updateState(QMediaPlayer::State state)
{
    m_isPlaying = (state == QMediaPlayer::PlayingState);
    
    if (m_isPlaying) {
        m_playPauseButton->setIcon(style()->standardIcon(QStyle::SP_MediaPause));
        m_statusLabel->setText(tr("播放中"));
    } else {
        m_playPauseButton->setIcon(style()->standardIcon(QStyle::SP_MediaPlay));
        m_statusLabel->setText(tr("已暂停"));
    }
}

void MainWindow::setVolume(int volume)
{
    m_mediaPlayer->setVolume(volume);
}

void MainWindow::muteToggled(bool muted)
{
    m_mediaPlayer->setMuted(muted);
    m_isMuted = muted;
    
    if (muted) {
        m_muteButton->setIcon(style()->standardIcon(QStyle::SP_MediaVolumeMuted));
    } else {
        m_muteButton->setIcon(style()->standardIcon(QStyle::SP_MediaVolume));
    }
}

MainWindow::~MainWindow()
{
}
```

## 11.5 小结

本章详细介绍了如何使用Qt和FFmpeg开发桌面音视频应用程序，包括：

1. **Qt环境搭建**: 跨平台开发环境配置
2. **音频播放**: SDL、QAudioSink多种播放方式
3. **视频播放**: QImage、OpenGL硬件加速渲染
4. **完整播放器**: 功能丰富的桌面播放器实现

通过本章学习，读者可以掌握桌面音视频应用开发的核心技术，构建专业的多媒体播放软件。

关键技术点：
- Qt框架集成FFmpeg
- 跨平台音视频处理
- OpenGL硬件加速
- 完整的播放器架构设计
- 用户界面和交互设计