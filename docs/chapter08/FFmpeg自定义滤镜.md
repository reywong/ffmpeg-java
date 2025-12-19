# 第8章 FFmpeg自定义滤镜

本章介绍如何编译FFmpeg源码、优化性能、创建自定义滤镜，并实现侧边模糊滤镜等高级功能。

## 8.1 Windows环境编译FFmpeg

### 8.1.1 给FFmpeg集成x264

在Windows环境下编译FFmpeg并集成x264编码器：

**准备工作：**
```bash
# 1. 安装MSYS2
# 下载地址：https://www.msys2.org/
# 安装后在MSYS2终端中更新包管理器
pacman -Syu
pacman -Su

# 2. 安装必要的编译工具
pacman -S mingw-w64-x86_64-gcc
pacman -S mingw-w64-x86_64-yasm
pacman -S mingw-w64-x86_64-nasm
pacman -S make
pacman -S git
pacman -S pkg-config
```

**编译x264：**
```bash
# 克隆x264源码
git clone https://code.videolan.org/videolan/x264.git
cd x264

# 配置编译选项
./configure --prefix=/usr/local/x264 --enable-shared --disable-cli

# 编译安装
make -j$(nproc)
make install

# 添加到环境变量
export PATH="/usr/local/x264/bin:$PATH"
export PKG_CONFIG_PATH="/usr/local/x264/lib/pkgconfig:$PKG_CONFIG_PATH"
```

**编译FFmpeg：**
```bash
# 克隆FFmpeg源码
git clone https://git.ffmpeg.org/ffmpeg.git
cd ffmpeg

# 配置编译选项（集成x264）
./configure --prefix=/usr/local/ffmpeg \
    --enable-gpl --enable-nonfree \
    --enable-libx264 \
    --enable-shared --disable-static \
    --enable-avresample \
    --enable-avisynth \
    --enable-fontconfig \
    --enable-gnutls \
    --enable-libass \
    --enable-libbluray \
    --enable-libfreetype \
    --enable-libmp3lame \
    --enable-libopus \
    --enable-libvorbis \
    --enable-libvpx \
    --enable-libwebp \
    --enable-libzmq \
    --enable-libzvbi \
    --enable-openssl

# 编译安装
make -j$(nproc)
make install

# 添加到环境变量
export PATH="/usr/local/ffmpeg/bin:$PATH"
export LD_LIBRARY_PATH="/usr/local/ffmpeg/lib:$LD_LIBRARY_PATH"
```

**Java实现示例：**
```java
/**
 * 使用x264编码器进行高质量编码
 */
public static void encodeWithX264(String inputFile, String outputFile, 
                                int crf, String preset) {
    try {
        String command = String.format(
            "ffmpeg -i %s -c:v libx264 -crf %d -preset %s -c:a copy %s",
            inputFile, crf, preset, outputFile);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("x264编码完成: " + outputFile);
        }
    } catch (Exception e) {
        System.err.println("x264编码失败: " + e.getMessage());
    }
}
```

### 8.1.2 给FFmpeg集成avs2

集成AVS2（中国音视频编码标准）编码器：

**编译avs2编码器：**
```bash
# 克隆AVS2源码
git clone https://github.com/pkuvcl/avs2sdk.git
cd avs2sdk

# 创建编译目录
mkdir build
cd build

# 配置CMake
cmake .. -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=ON

# 编译安装
make -j$(nproc)
make install

# 复制库文件到系统路径
cp libavs2.dylib /usr/local/lib/
cp avs2enc.h /usr/local/include/
```

**集成到FFmpeg：**
```bash
# 重新配置FFmpeg
cd ffmpeg
make clean

./configure --prefix=/usr/local/ffmpeg \
    --enable-gpl --enable-nonfree \
    --enable-libx264 --enable-libavs2 \
    --enable-shared --disable-static

# 重新编译
make -j$(nproc)
make install
```

### 8.1.3 给FFmpeg集成mp3lame

集成mp3lame MP3编码器：

**Windows环境编译mp3lame：**
```bash
# 下载mp3lame源码
wget https://downloads.sourceforge.net/lame/lame-3.100.tar.gz
tar -xzf lame-3.100.tar.gz
cd lame-3.100

# 配置编译选项
./configure --prefix=/usr/local/lame --enable-shared

# 编译安装
make -j$(nproc)
make install

# 配置环境变量
export LAME_LIBS="/usr/local/lame/lib"
export LAME_CFLAGS="-I/usr/local/lame/include"
```

**重新编译FFmpeg：**
```bash
cd ffmpeg
make clean

./configure --prefix=/usr/local/ffmpeg \
    --enable-libmp3lame \
    --enable-gpl --enable-nonfree \
    --enable-shared

make -j$(nproc)
make install
```

### 8.1.4 给FFmpeg集成FreeType

集成FreeType字体渲染库：

**编译FreeType：**
```bash
# 下载FreeType源码
wget https://download.savannah.gnu.org/releases/freetype/freetype-2.12.1.tar.gz
tar -xzf freetype-2.12.1.tar.gz
cd freetype-2.12.1

# 配置编译
./configure --prefix=/usr/local/freetype --enable-shared

# 编译安装
make -j$(nproc)
make install
```

**集成到FFmpeg：**
```bash
cd ffmpeg
make clean

./configure --prefix=/usr/local/ffmpeg \
    --enable-libfreetype \
    --enable-fontconfig \
    --enable-libass

make -j$(nproc)
make install
```

### 8.1.5 给FFmpeg集成x265

集成x265 H.265/HEVC编码器：

**编译x265：**
```bash
# 克隆x265源码
git clone https://bitbucket.org/multicoreware/x265_git.git
cd x265_git

# 创建构建目录
mkdir 12bit 8bit
cd 8bit

# 配置CMake
cmake ../source -DCMAKE_INSTALL_PREFIX=/usr/local/x265 \
    -DENABLE_SHARED:BOOL=ON \
    -DHIGH_BIT_DEPTH=OFF \
    -DMAIN12=OFF

# 编译安装
make -j$(nproc)
make install
```

**集成到FFmpeg：**
```bash
cd ffmpeg
make clean

./configure --prefix=/usr/local/ffmpeg \
    --enable-libx265 \
    --enable-gpl --enable-nonfree

make -j$(nproc)
make install
```

## 8.2 优化FFmpeg源码

### 8.2.1 读写音视频文件的元数据

优化元数据处理逻辑：

**C源码示例：**
```c
// 优化元数据读取函数
static int optimized_metadata_read(AVFormatContext *s, AVDictionary **metadata) {
    AVDictionaryEntry *tag = NULL;
    
    // 使用缓冲区减少系统调用
    char buffer[4096];
    int buffer_size = 0;
    
    // 批量读取元数据
    while ((tag = av_dict_get(*metadata, "", tag, AV_DICT_IGNORE_SUFFIX))) {
        if (buffer_size + strlen(tag->key) + strlen(tag->value) + 3 < sizeof(buffer)) {
            strcat(buffer, tag->key);
            strcat(buffer, ": ");
            strcat(buffer, tag->value);
            strcat(buffer, "\n");
            buffer_size += strlen(tag->key) + strlen(tag->value) + 3;
        }
    }
    
    // 返回缓冲区内容
    return buffer_size > 0 ? 0 : AVERROR(ENOMEM);
}
```

**Java封装：**
```java
/**
 * 优化的元数据读取
 */
public static Map<String, String> readMetadataOptimized(String inputFile) {
    Map<String, String> metadata = new HashMap<>();
    
    try {
        // 使用FFprobe高效读取元数据
        String command = String.format(
            "ffprobe -v quiet -print_format json -show_format -show_streams %s",
            inputFile);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        // 解析JSON输出
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            
            // 使用JSON解析器提取元数据
            parseMetadataFromJson(jsonBuilder.toString(), metadata);
        }
        
    } catch (Exception e) {
        System.err.println("读取元数据失败: " + e.getMessage());
    }
    
    return metadata;
}

private static void parseMetadataFromJson(String json, Map<String, String> metadata) {
    // 简化的JSON解析逻辑
    // 实际应用中应使用专门的JSON解析库
    if (json.contains("title")) {
        int start = json.indexOf("\"title\":\"") + 9;
        int end = json.indexOf("\"", start);
        if (end > start) {
            metadata.put("title", json.substring(start, end));
        }
    }
    // ... 其他字段解析
}
```

### 8.2.2 元数据的中文乱码问题处理

处理中文元数据的编码问题：

**编码转换函数：**
```c
// 处理中文编码转换
static char* convert_chinese_encoding(const char* input, const char* from_encoding, 
                                    const char* to_encoding) {
    iconv_t cd = iconv_open(to_encoding, from_encoding);
    if (cd == (iconv_t)-1) {
        return NULL;
    }
    
    size_t inbytesleft = strlen(input);
    size_t outbytesleft = inbytesleft * 4; // 足够的输出空间
    
    char* output = malloc(outbytesleft + 1);
    char* outptr = output;
    const char* inptr = input;
    
    iconv(cd, (char**)&inptr, &inbytesleft, &outptr, &outbytesleft);
    *outptr = '\0';
    
    iconv_close(cd);
    return output;
}
```

**Java实现：**
```java
/**
 * 处理中文元数据编码问题
 */
public static String fixChineseEncoding(String input) {
    try {
        // 尝试不同的编码方式
        String[] encodings = {"UTF-8", "GBK", "GB2312", "BIG5", "ISO-8859-1"};
        
        for (String encoding : encodings) {
            try {
                byte[] bytes = input.getBytes("ISO-8859-1");
                String decoded = new String(bytes, encoding);
                
                // 验证解码质量（简单的启发式检查）
                if (isValidChinese(decoded)) {
                    return decoded;
                }
            } catch (Exception e) {
                // 继续尝试下一种编码
                continue;
            }
        }
        
        return input;
        
    } catch (Exception e) {
        System.err.println("编码转换失败: " + e.getMessage());
        return input;
    }
}

private static boolean isValidChinese(String text) {
    int chineseCharCount = 0;
    int totalLength = text.length();
    
    for (int i = 0; i < totalLength; i++) {
        char c = text.charAt(i);
        if (c >= 0x4E00 && c <= 0x9FFF) { // 中文字符范围
            chineseCharCount++;
        }
    }
    
    // 如果中文字符占比较高，认为解码成功
    return (double) chineseCharCount / totalLength > 0.3;
}
```

### 8.2.3 修改FFmpeg源码解决乱码

在FFmpeg源码中添加中文支持：

**修改avformat.h：**
```c
// 在AVFormatContext结构体中添加编码支持
typedef struct AVFormatContext {
    // ... 现有字段 ...
    
    /**
     * 优先编码列表
     */
    const char *preferred_encoding;
    
    /**
     * 自动编码检测
     */
    int auto_detect_encoding;
    
} AVFormatContext;
```

**修改metadata.c：**
```c
// 添加自动编码检测函数
static const char* detect_encoding(const char* text) {
    // 简单的编码检测启发式
    if (is_valid_utf8(text)) {
        return "UTF-8";
    }
    
    if (is_likely_gbk(text)) {
        return "GBK";
    }
    
    if (is_likely_big5(text)) {
        return "BIG5";
    }
    
    return "ISO-8859-1";
}

// 修改元数据处理函数
static int process_metadata_with_encoding(AVDictionary **metadata, const char* encoding) {
    AVDictionaryEntry *tag = NULL;
    
    while ((tag = av_dict_get(*metadata, "", tag, AV_DICT_IGNORE_SUFFIX))) {
        if (needs_encoding_conversion(tag->value)) {
            const char* detected_encoding = encoding ? encoding : detect_encoding(tag->value);
            char* converted = convert_encoding(tag->value, detected_encoding, "UTF-8");
            
            if (converted) {
                av_dict_set(metadata, tag->key, converted, AV_DICT_DONT_STRDUP_VAL);
            }
        }
    }
    
    return 0;
}
```

## 8.3 自定义视频滤镜

### 8.3.1 添加模糊和锐化特效

创建自定义的模糊和锐化滤镜：

**滤镜注册代码：**
```c
#include "libavfilter/avfilter.h"
#include "libavfilter/internal.h"
#include "libavutil/eval.h"
#include "libavutil/opt.h"

typedef struct BlurSharpContext {
    float blur_strength;
    float sharp_strength;
    int radius;
    int planes;
} BlurSharpContext;

static const AVOption blursharp_options[] = {
    { "blur",     "Blur strength",     OFFSET(blur_strength), AV_OPT_TYPE_FLOAT, {.dbl=1.0}, 0.0, 10.0, FLAGS },
    { "sharp",    "Sharp strength",    OFFSET(sharp_strength),AV_OPT_TYPE_FLOAT, {.dbl=1.0}, 0.0, 10.0, FLAGS },
    { "radius",   "Filter radius",      OFFSET(radius),        AV_OPT_TYPE_INT,   {.i64=3},   1,   20,   FLAGS },
    { "planes",   "Filter planes",      OFFSET(planes),        AV_OPT_TYPE_INT,   {.i64=15},  0,   15,   FLAGS },
    { NULL }
};

AVFILTER_DEFINE_CLASS(blursharp);

static int query_formats(AVFilterContext *ctx) {
    static const enum AVPixelFormat pix_fmts[] = {
        AV_PIX_FMT_YUV420P, AV_PIX_FMT_YUV422P, AV_PIX_FMT_YUV444P,
        AV_PIX_FMT_GBRP, AV_PIX_FMT_NONE
    };
    
    return ff_set_common_formats(ctx, ff_make_format_list(pix_fmts));
}

static int config_props(AVFilterLink *inlink) {
    AVFilterContext *ctx = inlink->dst;
    BlurSharpContext *s = ctx->priv;
    
    // 验证参数
    if (s->radius < 1 || s->radius > 20) {
        av_log(ctx, AV_LOG_ERROR, "Invalid radius: %d\n", s->radius);
        return AVERROR(EINVAL);
    }
    
    return 0;
}

static int filter_frame(AVFilterLink *inlink, AVFrame *in) {
    AVFilterContext *ctx = inlink->dst;
    AVFilterLink *outlink = ctx->outputs[0];
    BlurSharpContext *s = ctx->priv;
    AVFrame *out;
    
    out = ff_get_video_buffer(outlink, outlink->w, outlink->h);
    if (!out) {
        av_frame_free(&in);
        return AVERROR(ENOMEM);
    }
    
    av_frame_copy_props(out, in);
    
    // 应用模糊和锐化效果
    for (int plane = 0; plane < 4; plane++) {
        if (!(s->planes & (1 << plane)))
            continue;
            
        int width = (plane == 0 || plane == 3) ? inlink->w : inlink->w >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_w;
        int height = (plane == 0 || plane == 3) ? inlink->h : inlink->h >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_h;
        
        uint8_t *src = in->data[plane];
        uint8_t *dst = out->data[plane];
        int src_linesize = in->linesize[plane];
        int dst_linesize = out->linesize[plane];
        
        // 应用模糊算法
        if (s->blur_strength > 0) {
            apply_gaussian_blur(src, dst, width, height, src_linesize, dst_linesize, s->radius, s->blur_strength);
        }
        
        // 应用锐化算法
        if (s->sharp_strength > 0) {
            apply_unsharp_mask(dst, dst, width, height, dst_linesize, dst_linesize, s->radius, s->sharp_strength);
        }
    }
    
    av_frame_free(&in);
    return ff_filter_frame(outlink, out);
}
```

### 8.3.2 视频滤镜的代码分析

分析滤镜的工作原理和性能优化：

**滤镜链处理流程：**
```c
// 滤镜链的构建和执行
static int build_filter_chain(AVFilterGraph *graph, AVFilterContext **src_ctx, 
                             AVFilterContext **sink_ctx, const char *filter_desc) {
    AVFilterContext *last_ctx = *src_ctx;
    AVFilterInOut *outputs = avfilter_inout_alloc();
    AVFilterInOut *inputs = avfilter_inout_alloc();
    int ret = 0;
    
    // 解析滤镜描述
    AVFilterInOut *filter_graph = NULL;
    ret = avfilter_graph_parse2(graph, filter_desc, &inputs, &outputs);
    
    if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "Failed to parse filter graph: %s\n", av_err2str(ret));
        return ret;
    }
    
    // 配置滤镜链
    ret = avfilter_graph_config(graph, NULL);
    if (ret < 0) {
        av_log(NULL, AV_LOG_ERROR, "Failed to configure filter graph: %s\n", av_err2str(ret));
        return ret;
    }
    
    *sink_ctx = inputs->filter_ctx;
    avfilter_inout_free(&inputs);
    avfilter_inout_free(&outputs);
    avfilter_inout_free(&filter_graph);
    
    return ret;
}
```

### 8.3.3 自定义视频翻转滤镜

实现自定义的视频翻转滤镜：

**翻转滤镜实现：**
```c
typedef struct FlipContext {
    int flip_horizontal;
    int flip_vertical;
    int flip_diagonal;
} FlipContext;

static int flip_filter_frame(AVFilterLink *inlink, AVFrame *in) {
    AVFilterContext *ctx = inlink->dst;
    AVFilterLink *outlink = ctx->outputs[0];
    FlipContext *s = ctx->priv;
    AVFrame *out;
    
    out = ff_get_video_buffer(outlink, outlink->w, outlink->h);
    if (!out) {
        av_frame_free(&in);
        return AVERROR(ENOMEM);
    }
    
    av_frame_copy_props(out, in);
    
    // 执行翻转操作
    for (int plane = 0; plane < 4; plane++) {
        if (!in->data[plane])
            continue;
            
        int width = (plane == 0 || plane == 3) ? inlink->w : inlink->w >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_w;
        int height = (plane == 0 || plane == 3) ? inlink->h : inlink->h >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_h;
        
        uint8_t *src = in->data[plane];
        uint8_t *dst = out->data[plane];
        int src_linesize = in->linesize[plane];
        int dst_linesize = out->linesize[plane];
        
        if (s->flip_horizontal) {
            flip_horizontal_plane(src, dst, width, height, src_linesize, dst_linesize);
        }
        
        if (s->flip_vertical) {
            flip_vertical_plane(src, dst, width, height, src_linesize, dst_linesize);
        }
        
        if (s->flip_diagonal) {
            flip_diagonal_plane(src, dst, width, height, src_linesize, dst_linesize);
        }
    }
    
    av_frame_free(&in);
    return ff_filter_frame(outlink, out);
}

static void flip_horizontal_plane(const uint8_t *src, uint8_t *dst, 
                                int width, int height, int src_linesize, int dst_linesize) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            dst[y * dst_linesize + x] = src[y * src_linesize + (width - 1 - x)];
        }
    }
}

static void flip_vertical_plane(const uint8_t *src, uint8_t *dst, 
                               int width, int height, int src_linesize, int dst_linesize) {
    for (int y = 0; y < height; y++) {
        memcpy(dst + y * dst_linesize, src + (height - 1 - y) * src_linesize, width);
    }
}

static void flip_diagonal_plane(const uint8_t *src, uint8_t *dst, 
                              int width, int height, int src_linesize, int dst_linesize) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            dst[y * dst_linesize + x] = src[(height - 1 - y) * src_linesize + (width - 1 - x)];
        }
    }
}
```

## 8.4 实战项目：侧边模糊滤镜

### 8.4.1 实现两侧模糊逻辑

实现画面两侧渐变模糊效果：

**侧边模糊滤镜实现：**
```c
typedef struct SideBlurContext {
    int left_width;
    int right_width;
    int transition_width;
    float max_blur;
    int blur_type; // 0: gaussian, 1: box, 2: motion
} SideBlurContext;

static int side_blur_filter_frame(AVFilterLink *inlink, AVFrame *in) {
    AVFilterContext *ctx = inlink->dst;
    AVFilterLink *outlink = ctx->outputs[0];
    SideBlurContext *s = ctx->priv;
    AVFrame *out;
    
    out = ff_get_video_buffer(outlink, outlink->w, outlink->h);
    if (!out) {
        av_frame_free(&in);
        return AVERROR(ENOMEM);
    }
    
    av_frame_copy_props(out, in);
    
    // 处理每个平面
    for (int plane = 0; plane < 4; plane++) {
        if (!in->data[plane])
            continue;
            
        int width = (plane == 0 || plane == 3) ? inlink->w : inlink->w >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_w;
        int height = (plane == 0 || plane == 3) ? inlink->h : inlink->h >> av_pix_fmt_desc_get(inlink->format)->log2_chroma_h;
        
        apply_side_blur(in->data[plane], out->data[plane], 
                       width, height, in->linesize[plane], out->linesize[plane], s);
    }
    
    av_frame_free(&in);
    return ff_filter_frame(outlink, out);
}

static void apply_side_blur(const uint8_t *src, uint8_t *dst, 
                           int width, int height, int src_linesize, int dst_linesize, 
                           SideBlurContext *s) {
    
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float blur_factor = calculate_blur_factor(x, width, s);
            
            if (blur_factor == 0.0f) {
                // 无模糊，直接复制
                dst[y * dst_linesize + x] = src[y * src_linesize + x];
            } else if (blur_factor == 1.0f) {
                // 完全模糊
                dst[y * dst_linesize + x] = apply_full_blur(src, x, y, width, height, src_linesize, s);
            } else {
                // 渐变模糊
                uint8_t original = src[y * src_linesize + x];
                uint8_t blurred = apply_partial_blur(src, x, y, width, height, src_linesize, s, blur_factor);
                
                // 线性插值
                dst[y * dst_linesize + x] = (uint8_t)(original * (1.0f - blur_factor) + blurred * blur_factor);
            }
        }
    }
}

static float calculate_blur_factor(int x, int width, SideBlurContext *s) {
    // 左侧模糊区域
    if (x < s->left_width) {
        return s->max_blur;
    }
    // 左侧过渡区域
    else if (x < s->left_width + s->transition_width) {
        float progress = (float)(x - s->left_width) / s->transition_width;
        return s->max_blur * (1.0f - progress);
    }
    // 右侧过渡区域
    else if (x > width - s->right_width - s->transition_width) {
        float progress = (float)(width - s->right_width - x) / s->transition_width;
        return s->max_blur * (1.0f - progress);
    }
    // 右侧模糊区域
    else if (x > width - s->right_width) {
        return s->max_blur;
    }
    // 中间清晰区域
    else {
        return 0.0f;
    }
}

static uint8_t apply_gaussian_blur(const uint8_t *src, int x, int y, 
                                 int width, int height, int src_linesize, 
                                 int radius, float strength) {
    float sum = 0.0f;
    float weight_sum = 0.0f;
    
    // 高斯核
    float sigma = radius / 3.0f;
    float two_sigma_squared = 2.0f * sigma * sigma;
    
    for (int dy = -radius; dy <= radius; dy++) {
        for (int dx = -radius; dx <= radius; dx++) {
            int nx = x + dx;
            int ny = y + dy;
            
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                float distance = sqrtf(dx * dx + dy * dy);
                if (distance <= radius) {
                    float weight = expf(-(distance * distance) / two_sigma_squared);
                    sum += src[ny * src_linesize + nx] * weight;
                    weight_sum += weight;
                }
            }
        }
    }
    
    return (uint8_t)(sum / weight_sum);
}
```

### 8.4.2 集成侧边模糊滤镜

将自定义滤镜集成到FFmpeg中：

**滤镜注册和编译：**
```c
// 滤镜定义
static const AVFilterPad sideblur_inputs[] = {
    {
        .name = "default",
        .type = AVMEDIA_TYPE_VIDEO,
        .config_props = config_props,
        .filter_frame = filter_frame,
    },
    { NULL }
};

static const AVFilterPad sideblur_outputs[] = {
    {
        .name = "default",
        .type = AVMEDIA_TYPE_VIDEO,
    },
    { NULL }
};

AVFilter ff_vf_sideblur = {
    .name          = "sideblur",
    .description   = NULL_IF_CONFIG_SMALL("Apply blur to sides of video"),
    .priv_size     = sizeof(SideBlurContext),
    .priv_class    = &sideblur_class,
    .query_formats = query_formats,
    .inputs        = sideblur_inputs,
    .outputs       = sideblur_outputs,
    .flags         = AVFILTER_FLAG_SUPPORT_TIMELINE_GENERIC,
};
```

**Makefile添加：**
```makefile
# 在libavfilter/Makefile中添加
OBJS-$(CONFIG_SIDEBLUR_FILTER) += vf_sideblur.o

# 在libavfilter/allfilters.c中注册
extern AVFilter ff_vf_sideblur;
```

**Java封装使用：**
```java
/**
 * 使用侧边模糊滤镜
 */
public static void applySideBlur(String inputFile, String outputFile, 
                                int leftWidth, int rightWidth, int transitionWidth, 
                                float maxBlur) {
    try {
        String filter = String.format(
            "sideblur=left_width=%d:right_width=%d:transition_width=%d:max_blur=%.2f",
            leftWidth, rightWidth, transitionWidth, maxBlur);
        
        String command = String.format(
            "ffmpeg -i %s -vf \"%s\" -c:a copy %s",
            inputFile, filter, outputFile);
        
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        
        if (process.exitValue() == 0) {
            System.out.println("侧边模糊效果应用成功: " + outputFile);
        } else {
            System.err.println("侧边模糊效果应用失败");
        }
        
    } catch (Exception e) {
        System.err.println("侧边模糊效果应用异常: " + e.getMessage());
    }
}
```

## 8.5 小结

本章深入学习了FFmpeg的高级定制技术：

1. **源码编译**: 在Windows环境编译FFmpeg并集成各种编码器
2. **性能优化**: 优化元数据处理和中文编码支持
3. **自定义滤镜**: 开发自定义的模糊、锐化、翻转滤镜
4. **实战项目**: 完整的侧边模糊滤镜实现和集成

这些技术让开发者能够深度定制FFmpeg，满足特殊应用需求。自定义滤镜的开发需要熟悉FFmpeg架构和C语言编程，但能够实现标准版本中没有的独特效果。