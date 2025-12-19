# 第10章 FFmpeg播放音视频

本章介绍如何使用SDL播放音视频，实现网络推流拉流，以及线程间同步处理。

## 10.1 通过SDL播放音视频

### 10.1.1 FFmpeg集成SDL

SDL（Simple DirectMedia Layer）是一个跨平台的多媒体开发库，常与FFmpeg配合使用：

```bash
# 编译SDL支持
./configure --enable-sdl2
make && make install

# SDL播放基本流程
1. 初始化SDL子系统
2. 创建窗口和渲染器
3. 解码音视频帧
4. 渲染视频帧
5. 播放音频数据
6. 事件处理和循环
```

### 10.1.2 利用SDL播放视频

```c
// SDL视频播放示例
#include <SDL2/SDL.h>
#include <libavformat/avformat.h>

int play_video(const char* filename) {
    SDL_Window* window = NULL;
    SDL_Renderer* renderer = NULL;
    SDL_Texture* texture = NULL;
    
    // 初始化SDL
    SDL_Init(SDL_INIT_VIDEO);
    
    // 创建窗口
    window = SDL_CreateWindow("Video Player",
        SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
        800, 600, SDL_WINDOW_SHOWN);
    
    renderer = SDL_CreateRenderer(window, -1, 0);
    
    // 打开视频文件
    AVFormatContext* format_ctx = NULL;
    avformat_open_input(&format_ctx, filename, NULL, NULL);
    avformat_find_stream_info(format_ctx, NULL);
    
    // 查找视频流
    int video_stream = av_find_best_stream(format_ctx, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    
    // 主循环
    while (1) {
        AVPacket packet;
        if (av_read_frame(format_ctx, &packet) >= 0) {
            if (packet.stream_index == video_stream) {
                // 处理视频帧
                SDL_UpdateYUVTexture(texture, NULL, 
                    y_plane, pitch, u_plane, pitch/2, v_plane, pitch/2);
                SDL_RenderClear(renderer);
                SDL_RenderCopy(renderer, texture, NULL, NULL);
                SDL_RenderPresent(renderer);
            }
            av_packet_unref(&packet);
        }
        
        SDL_Event event;
        if (SDL_PollEvent(&event) && event.type == SDL_QUIT) {
            break;
        }
    }
    
    // 清理资源
    SDL_DestroyTexture(texture);
    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
    
    return 0;
}
```

### 10.1.3 利用SDL播放音频

```c
// SDL音频播放示例
#include <SDL2/SDL.h>

void audio_callback(void* userdata, Uint8* stream, int len) {
    AudioData* audio = (AudioData*)userdata;
    
    if (audio->len == 0) {
        return;
    }
    
    len = (len > audio->len ? audio->len : len);
    SDL_memcpy(stream, audio->pos, len);
    
    audio->pos += len;
    audio->len -= len;
}

int play_audio(const char* filename) {
    SDL_AudioSpec wanted_spec, spec;
    
    // 设置音频参数
    wanted_spec.freq = 44100;
    wanted_spec.format = AUDIO_S16SYS;
    wanted_spec.channels = 2;
    wanted_spec.samples = 4096;
    wanted_spec.callback = audio_callback;
    
    // 打开音频设备
    if (SDL_OpenAudio(&wanted_spec, &spec) < 0) {
        printf("无法打开音频设备: %s\n", SDL_GetError());
        return -1;
    }
    
    // 解码音频文件并播放
    // ... 音频解码逻辑 ...
    
    SDL_PauseAudio(0);
    
    // 等待播放完成
    while (audio_len > 0) {
        SDL_Delay(100);
    }
    
    SDL_CloseAudio();
    return 0;
}
```

## 10.2 FFmpeg推流和拉流

### 10.2.1 什么是推拉流

**推流（Push）**: 将本地音视频流推送到流媒体服务器
**拉流（Pull）**: 从流媒体服务器拉取音视频流进行播放

### 10.2.2 FFmpeg向网络推流

```bash
# 推流到RTMP服务器
ffmpeg -re -i input.mp4 -c copy -f flv rtmp://server/live/stream_key

# 推流摄像头
ffmpeg -f dshow -i video="Integrated Camera" -f flv rtmp://server/live/camera

# 推流桌面
ffmpeg -f gdigrab -i desktop -f flv rtmp://server/live/desktop

# 带编码的推流
ffmpeg -re -i input.mp4 -c:v libx264 -preset fast -maxrate 1000k -bufsize 2000k \
-c:a aac -b:a 128k -f flv rtmp://server/live/hd_stream
```

### 10.2.3 FFmpeg从网络拉流

```bash
# 从RTMP服务器拉流
ffmpeg -i rtmp://server/live/stream_key -c copy output.mp4

# 录制直播流
ffmpeg -i rtmp://server/live/stream -c copy -t 3600 live_record.mp4

# 实时转码
ffmpeg -i rtmp://source/live/stream -c:v libx264 -c:a aac -f flv rtmp://target/live/stream
```

## 10.3 SDL处理线程间同步

### 10.3.1 SDL的线程

```c
// 创建线程
SDL_Thread* thread = SDL_CreateThread(thread_function, "MyThread", data);

// 等待线程结束
int thread_return;
SDL_WaitThread(thread, &thread_return);

// 线程函数示例
int thread_function(void* data) {
    while (running) {
        // 处理任务
        SDL_Delay(16); // 约60fps
    }
    return 0;
}
```

### 10.3.2 SDL的互斥锁

```c
// 创建互斥锁
SDL_mutex* mutex = SDL_CreateMutex();

// 锁定互斥锁
SDL_LockMutex(mutex);
// 访问共享资源
SDL_UnlockMutex(mutex);

// 销毁互斥锁
SDL_DestroyMutex(mutex);
```

### 10.3.3 SDL的信号量

```c
// 创建信号量
SDL_sem* semaphore = SDL_CreateSemaphore(0);

// 等待信号量
SDL_SemWait(semaphore);

// 发出信号量
SDL_SemPost(semaphore);

// 销毁信号量
SDL_DestroySemaphore(semaphore);
```

## 10.4 实战项目：同步播放音视频

### 10.4.1 同步音视频的播放时钟

```c
typedef struct Clock {
    double pts;       // 当前播放时间
    double pts_drift;  // 时间漂移
    double last_updated; // 最后更新时间
    double speed;      // 播放速度
} Clock;

void clock_init(Clock* clock) {
    clock->speed = 1.0;
    clock->pts = 0.0;
    clock->pts_drift = 0.0;
    clock->last_updated = av_gettime() / 1000000.0;
}

double clock_get(Clock* clock) {
    double time = av_gettime() / 1000000.0;
    return clock->pts_drift + time;
}

void clock_set(Clock* clock, double pts) {
    double time = av_gettime() / 1000000.0;
    clock->pts_drift = pts - time;
}
```

### 10.4.2 优化音视频的同步播放

```c
// 音视频同步播放器
typedef struct VideoPlayer {
    AVFormatContext* format_ctx;
    AVCodecContext* video_ctx;
    AVCodecContext* audio_ctx;
    
    SDL_Window* window;
    SDL_Renderer* renderer;
    SDL_Texture* texture;
    
    Clock video_clock;
    Clock audio_clock;
    
    int video_stream_index;
    int audio_stream_index;
    
    SDL_Thread* decode_thread;
    SDL_mutex* frame_queue_mutex;
    SDL_cond* frame_queue_cond;
} VideoPlayer;

// 主播放循环
void player_loop(VideoPlayer* player) {
    while (!quit) {
        double current_time = clock_get(&player->video_clock);
        
        // 处理事件
        SDL_Event event;
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT) {
                quit = 1;
            }
        }
        
        // 渲染视频帧
        if (should_display_frame(player, current_time)) {
            display_video_frame(player);
        }
        
        // 控制帧率
        SDL_Delay(16);
    }
}

// 音视频同步逻辑
int should_display_frame(VideoPlayer* player, double current_time) {
    AVFrame* frame = get_next_frame(player);
    if (!frame) return 0;
    
    double frame_pts = frame->pts * av_q2d(time_base);
    double delay = frame_pts - current_time;
    
    if (delay > 0.040) { // 超过40ms，等待
        SDL_Delay((int)(delay * 1000));
        return 0;
    } else if (delay < -0.040) { // 超过40ms延迟，跳帧
        return 0;
    }
    
    return 1;
}
```

## 10.5 小结

本章学习了音视频播放的核心技术：

1. **SDL集成**: 音视频播放的基础架构
2. **网络流媒体**: 推流和拉流技术
3. **线程同步**: 多线程音视频处理
4. **时钟同步**: 音视频精确同步播放

这些技术为开发高性能的音视频播放器奠定了基础。