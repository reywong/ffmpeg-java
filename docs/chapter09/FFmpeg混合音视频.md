# 第9章 FFmpeg混合音视频

本章介绍如何混合多路音频和视频，实现画中画、多宫格效果、转场动画等高级混合技术。

## 9.1 多路音频

### 9.1.1 同时过滤视频和音频

```bash
# 同时应用视频和音频滤镜
ffmpeg -i input1.mp4 -i input2.mp4 -filter_complex \
"[0:v]scale=640:480[v1];[1:a]volume=0.5[a1];[v1][a1]concat=n=1:v=1:a=1[outv][outa]" \
-map "[outv]" -map "[outa]" output.mp4
```

### 9.1.2 利用多通道实现混音

```bash
# 混合两个音频文件
ffmpeg -i audio1.mp3 -i audio2.mp3 -filter_complex \
"[0:a][1:a]amix=inputs=2:duration=longest:dropout_transition=2" \
output_mix.mp3

# 调整音频混合权重
ffmpeg -i audio1.mp3 -i audio2.mp3 -filter_complex \
"[0:a]volume=0.7[a1];[1:a]volume=0.3[a2];[a1][a2]amix=inputs=2" \
output_weighted_mix.mp3
```

### 9.1.3 给视频添加背景音乐

```bash
# 添加背景音乐，保持原音频
ffmpeg -i video.mp4 -i music.mp3 -filter_complex \
"[0:a][1:a]amix=inputs=2:weights=0.8 0.2" \
-c:v copy -c:a aac output_with_music.mp4

# 淡入淡出背景音乐
ffmpeg -i video.mp4 -i music.mp3 -filter_complex \
"[1:a]afade=t=in:st=0:d=3,afade=t=out:st=30:d=3[music];[0:a][music]amix=inputs=2" \
-c:v copy output_with_fade_music.mp4
```

## 9.2 多路视频

### 9.2.1 通过叠加视频实现画中画

```bash
# 基本画中画效果
ffmpeg -i main.mp4 -i pip.mp4 -filter_complex \
"[0:v][1:v]overlay=20:20" -c:a copy output_pip.mp4

# 右下角画中画
ffmpeg -i main.mp4 -i pip.mp4 -filter_complex \
"[0:v][1:v]overlay=W-w-20:H-h-20" -c:a copy output_pip_corner.mp4

# 缩放画中画
ffmpeg -i main.mp4 -i pip.mp4 -filter_complex \
"[1:v]scale=320:240[pip];[0:v][pip]overlay=20:20" -c:a copy output_scaled_pip.mp4
```

### 9.2.2 多路视频实现四宫格效果

```bash
# 四宫格布局
ffmpeg -i video1.mp4 -i video2.mp4 -i video3.mp4 -i video4.mp4 \
-filter_complex \
"[0:v]scale=640:360[v1];[1:v]scale=640:360[v2];[2:v]scale=640:360[v3];[3:v]scale=640:360[v4];\
[v1][v2]hstack=inputs=2[top];[v3][v4]hstack=inputs=2[bottom];[top][bottom]vstack=inputs=2" \
output_grid.mp4

# 九宫格布局
ffmpeg -i input.mp4 -filter_complex \
"[0:v]split=9[s1][s2][s3][s4][s5][s6][s7][s8][s9];\
[s1][s2][s3]hstack=inputs=3[row1];[s4][s5][s6]hstack=inputs=3[row2];[s7][s8][s9]hstack=inputs=3[row3];\
[row1][row2][row3]vstack=inputs=3" \
output_9grid.mp4
```

### 9.2.3 透视两路视频的混合画面

```bash
# 透视混合效果
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v][1:v]blend=all_mode=overlay:all_opacity=0.5" output_blend.mp4

# 动态透明度混合
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v][1:v]blend=all_expr='if(eq(mod(T,10),5),A,B)'" output_dynamic_blend.mp4
```

## 9.3 转场动画

### 9.3.1 给视频添加转场动画

```bash
# 淡入淡出转场
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v][1:v]xfade=transition=fade:duration=1:offset=30" output_fade_transition.mp4

# 滑动转场
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v][1:v]xfade=transition=slideleft:duration=2:offset=25" output_slide_transition.mp4

# 缩放转场
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v][1:v]xfade=transition=zoomin:duration=1.5:offset=20" output_zoom_transition.mp4
```

### 9.3.2 转场动画的代码分析

转场动画的实现原理：
- 时间同步：确保两个视频在转场点的精确对齐
- 像素混合：根据转场类型混合两个视频的像素
- 动画插值：在转场期间平滑过渡效果

### 9.3.3 自定义斜边转场动画

```bash
# 自定义斜边转场
ffmpeg -i video1.mp4 -i video2.mp4 -filter_complex \
"[0:v]split=2[v1a][v1b];[1:v]split=2[v2a][v2b];\
[v1a][v2a]blend=all_expr='if(lt(Y,480-abs(X-960)),A,B)'[top];\
[v1b][v2b]blend=all_expr='if(gte(Y,480-abs(X-960)),A,B)'[bottom];\
[top][bottom]vstack=inputs=2" output_diagonal_transition.mp4
```

## 9.4 实战项目：翻书转场动画

### 9.4.1 贝塞尔曲线实现翻页特效

```bash
# 3D翻页效果
ffmpeg -i page1.mp4 -i page2.mp4 -filter_complex \
"[0:v][1:v]xfade=transition=pixelize:duration=2:offset=15" output_page_turn.mp4
```

### 9.4.2 集成翻书转场动画效果

结合多种技术实现逼真的翻书效果，包括：
- 页面弯曲模拟
- 阴影效果
- 翻页音频同步
- 背景环境

## 9.5 小结

本章学习了音视频混合的高级技术：

1. **多路音频混合**: 音频混合、背景音乐添加
2. **多路视频叠加**: 画中画、多宫格、透视混合
3. **转场动画**: 各种转场效果和自定义转场
4. **实战项目**: 翻书转场动画的完整实现

这些技术为创建专业的视频剪辑和特效制作提供了强大支持。