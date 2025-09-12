<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <!-- 视频背景 -->
    <video
        v-if="shouldShowVideo()"
        :key="`team-video-${teamItem.buzzStatus}`"
        :src="getMediaUrl()"
        autoplay
        loop
        muted
        playsinline
        class="background-video"
        @loadstart="onVideoLoadStart"
        @canplay="onVideoCanPlay"
        @error="handleVideoError"
    ></video>

    <div id="container" v-if="this.teamItem.buzzStatus === 0">
      <div class="codeOutStyle" :style="codeOutStyle">
        <div :style="codeStyle">
          {{ teamItem.code }}
        </div>
      </div>

      <div :style="teamStyle">
        {{ teamItem.fullName }}
      </div>

      <div :style="scoreStyle">
        {{ teamItem.teamScore }}
      </div>
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";
import {scoreStyle, queryScore} from "@/api";

export default {
  name: "TeamScore",

  data() {
    return {
      timer: null,
      dialogFormVisible: false,
      teamItem: '',
      imageUrl: `${baseUrl}/resource/团队分数背景.jpg?${new Date().getTime()}`,
      buzzSuccessUrl: `${baseUrl}/resource/抢答成功背景.jpg?${new Date().getTime()}`,
      buzzFailUrl: `${baseUrl}/resource/抢答失败背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      // 添加视频URL支持
      videoUrl: `${baseUrl}/resource/团队分数背景.mp4?${new Date().getTime()}`,
      buzzSuccessVideoUrl: `${baseUrl}/resource/抢答成功背景.mp4?${new Date().getTime()}`,
      buzzFailVideoUrl: `${baseUrl}/resource/抢答失败背景.mp4?${new Date().getTime()}`,
      defaultVideoUrl: `${baseUrl}/resource/默认背景.mp4?${new Date().getTime()}`,
      codeCfg: {
        color: 'black'
      },
      teamCfg: {
        color: 'black'
      },
      scoreCfg: {
        color: 'black'
      },
      // 媒体类型缓存
      mediaTypes: {}
    }
  },

  computed: {
    containerBoxStyle() {
      const mediaUrl = this.getMediaUrl();
      const isVideo = this.shouldShowVideo();

      let style = {
        backgroundSize: '100vw 100vh',
        height: '100vh',
        position: 'relative',
        overflow: 'hidden'
      };

      // 如果不是视频或者是未抢答状态，设置背景图片
      if (!isVideo || this.teamItem.buzzStatus === 0) {
        style.backgroundImage = `url(${mediaUrl})`;
      }

      return style;
    },

    codeOutStyle(){
      return {
        width: '100%',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: `${this.codeCfg.size}px`,
        textAlign: 'center',
        position: 'absolute',
        top: `${this.codeCfg.y}vh`,
        zIndex: 10 // 确保在视频上方
      }
    },

    codeStyle(){
      return {
        position: 'absolute',
        width: `${this.codeCfg.size}px`,
        height: `${this.codeCfg.size}px`,
        lineHeight: `${this.codeCfg.size}px`,
        borderRadius: '50%',
        border: `1px solid ${this.codeCfg.color.replace('0x', '#')}`,
        textAlign: 'center',
        color: this.codeCfg.color.replace('0x', '#'),
        fontSize: `${this.codeCfg.size}px`,
        alignItems: 'center',
        justifyContent: 'center',
        display: this.codeCfg.show === 1? 'block': 'none',
        backgroundColor: 'rgba(255, 255, 255, 0.8)' // 添加半透明背景提高可读性
      }
    },

    teamStyle(){
      return {
        position: 'absolute',
        width: '100%',
        textAlign: 'center',
        top: `${this.teamCfg.y}vh`,
        color: this.teamCfg.color.replace('0x', '#'),
        fontSize: `${this.teamCfg.size}px`,
        display: this.teamCfg.show === 1? 'block': 'none',
        zIndex: 10,
        textShadow: '2px 2px 4px rgba(0,0,0,0.5)' // 添加文字阴影提高可读性
      }
    },

    scoreStyle(){
      return {
        position: 'absolute',
        width: '100%',
        textAlign: 'center',
        top: `${this.scoreCfg.y}vh`,
        color: this.scoreCfg.color.replace('0x', '#'),
        fontSize: `${this.scoreCfg.size}px`,
        zIndex: 10,
        textShadow: '2px 2px 4px rgba(0,0,0,0.5)' // 添加文字阴影提高可读性
      }
    }
  },

  methods: {
    // 判断是否应该显示视频
    shouldShowVideo() {
      // 只有抢答状态不为0时（即抢答成功或失败时）才显示视频
      if (!this.teamItem || this.teamItem.buzzStatus === 0) return false;

      const mediaType = this.getMediaType();
      return mediaType === 'video';
    },

    // 检测媒体类型（图片还是视频）
    getMediaType() {
      const url = this.getMediaUrl();
      if (this.mediaTypes[url]) {
        return this.mediaTypes[url];
      }

      // 根据文件扩展名判断
      const videoExtensions = ['.mp4', '.webm', '.ogg', '.avi', '.mov'];
      const isVideo = videoExtensions.some(ext => url.toLowerCase().includes(ext));

      this.mediaTypes[url] = isVideo ? 'video' : 'image';
      return this.mediaTypes[url];
    },

    // 获取媒体URL
    getMediaUrl() {
      if (!this.teamItem) return this.imageUrl;

      let url;
      if (this.teamItem.buzzStatus === 0) {
        // 未抢答的时候只展示图片
        url = this.imageUrl;
      } else if (this.teamItem.buzzStatus === 1) {
        // 抢答成功，优先使用视频
        url = this.buzzSuccessVideoUrl;
      } else if (this.teamItem.buzzStatus === 2) {
        // 抢答失败，优先使用视频
        url = this.buzzFailVideoUrl;
      }
      return url;
    },

    // 检查媒体文件是否存在
    checkMediaExists(url) {
      console.log('Checking media:', url);
      // 这里可以实现异步检查，暂时返回true
      // 实际项目中可以通过HEAD请求检查文件是否存在
      return true;
    },

    // 视频开始加载
    onVideoLoadStart(event) {
      console.log('Team video load started:', event.target.src);
    },

    // 视频可以播放
    onVideoCanPlay(event) {
      console.log('Team video can play:', event.target.src);
      // 确保视频开始播放
      event.target.play().catch(err => {
        console.log('Team video play failed:', err);
      });
    },

    // 视频加载错误处理
    handleVideoError(event) {
      console.log('Team video load error, falling back to image');
      const video = event.target;
      const container = video.parentElement;

      // 隐藏视频，显示默认背景图片
      video.style.display = 'none';
      container.style.backgroundImage = `url(${this.defaultImageUrl})`;
      container.style.backgroundSize = "100vw 100vh";
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },

    updateScore() {
      queryScore(this.teamItem.id).then(res => {
        sessionStorage.setItem('team', JSON.stringify(res.data));
        // 使用 $nextTick 确保响应式更新
        this.$nextTick(() => {
          this.teamItem = res.data;
        });
      })
    },

    queryStyle(){
      scoreStyle().then(res => {
        const result = res.data.item
        console.log(result)
        this.codeCfg = result[0]
        this.teamCfg = result[1]
        this.scoreCfg = result[2]
      })
    },

    // 预加载视频
    preloadVideos() {
      const videos = [
        this.buzzSuccessVideoUrl,
        this.buzzFailVideoUrl
      ];

      videos.forEach(videoUrl => {
        const video = document.createElement('video');
        video.src = videoUrl;
        video.preload = 'metadata';
        video.muted = true;
        video.load();
      });
    }
  },

  mounted() {
    const login = sessionStorage.getItem('team')
    if (!login || login === '') {
      this.$router.push({name: "scoreLogin"})
      return
    }
    this.teamItem = JSON.parse(login)
    this.queryStyle()

    // 预加载视频
    this.preloadVideos();

    // 检查所有媒体文件
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("Team image invalid, using default")
        this.imageUrl = this.defaultImageUrl;
      }
    });

    this.checkImageUrl(this.buzzSuccessUrl, (isValid) => {
      if (!isValid) {
        console.log("Buzz success image invalid, using default")
        this.buzzSuccessUrl = this.defaultImageUrl;
      }
    });

    this.checkImageUrl(this.buzzFailUrl, (isValid) => {
      if (!isValid) {
        console.log("Buzz fail image invalid, using default")
        this.buzzFailUrl = this.defaultImageUrl;
      }
    });

    sessionStorage.setItem('role', 'score')
    this.timer = setInterval(() => {
      this.updateScore()
    }, 1000)
  },

  beforeDestroy(){
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style scoped>
#container {
  position: relative;
  z-index: 10;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.codeOutStyle,
.teamStyle,
.scoreStyle {
  position: relative;
  z-index: 10;
}

.member {
  border-color: black;
  margin: 20px 10px;
  width: 50px;
  padding: 0 10px;
  color: white;
  line-height: 60px;
  background-color: #ff9900;
  text-align: center;
  height: 60px;
}

.member:hover {
  background-color: #000000; /* 鼠标悬停时的背景颜色 */
  color: #ffffff;
}
</style>