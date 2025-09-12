<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <el-row v-for="x in rows" :key="x">
        <el-col :span="24 / cols" v-for="y in cols" :key="y">
          <div v-if="teamList[(x - 1) * cols + y - 1]" class="score-item" :style="scoreItemStyle(teamList[(x - 1) * cols + y - 1].buzzStatus)">
            <!-- 视频背景 -->
            <video
                v-if="getMediaType(teamList[(x - 1) * cols + y - 1].buzzStatus) !== 'image'"
                :src="getMediaUrl(teamList[(x - 1) * cols + y - 1].buzzStatus)"
                autoplay
                loop
                muted
                playsinline
                class="background-video"
                @error="handleVideoError"
            ></video>

            <div v-if="teamList[(x - 1) * cols + y - 1].buzzStatus === 0">
              <div class="codeOutStyle" :style="codeOutStyle">
                <div :style="codeStyle">
                  {{teamList[(x - 1) * cols + y - 1].code}}
                </div>
              </div>

              <div :style="teamStyle">
                {{teamList[(x - 1) * cols + y - 1].fullName}}
              </div>

              <div :style="scoreStyle">
                {{teamList[(x - 1) * cols + y - 1].teamScore}}
              </div>
            </div>
          </div>
        </el-Col>
      </el-row>
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";
import {scoreStyle, queryTeams} from "@/api";

export default {
  name: "AllScore",

  data() {
    return {
      rows: 1,
      cols: 1,
      timer: null,
      dialogFormVisible: false,
      teamList: [],
      imageUrl: `${baseUrl}/resource/全局分数背景.jpg?${new Date().getTime()}`,
      buzzSuccessUrl: `${baseUrl}/resource/抢答成功背景.jpg?${new Date().getTime()}`,
      buzzFailUrl: `${baseUrl}/resource/抢答失败背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      // 添加视频URL支持
      videoUrl: `${baseUrl}/resource/全局分数背景.mp4?${new Date().getTime()}`,
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
      showCfg1: {
        color: 'black'
      },
      showCfg2: {
        color: 'black'
      },
      // 媒体类型缓存
      mediaTypes: {}
    }
  },

  computed: {
    containerBoxStyle() {
      return {
        backgroundSize: '100vw 100vh',
        height: '100vh',
      };
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
        top: `${this.codeCfg.y}%`,
        zIndex: 10 // 确保在视频上方
      }
    },

    codeStyle(){
      return {
        width: `${this.codeCfg.size + 10 }px`,
        height: `${this.codeCfg.size  + 10 }px`,
        lineHeight: `${this.codeCfg.size  + 10 }px`,
        borderRadius: '50%',
        border: `1px solid ${this.codeCfg.color.replace('0x', '#')}`,
        textAlign: 'center',
        color: this.codeCfg.color.replace('0x', '#'),
        fontSize: `${this.codeCfg.size }px`,
        alignItems: 'center',
        justifyContent: 'center',
        display: this.showCfg1.show === 1? 'block': 'none',
        backgroundColor: 'rgba(255, 255, 255, 0.8)' // 添加半透明背景提高可读性
      }
    },

    teamStyle(){
      return {
        top: `${this.teamCfg.y}%`,
        position: 'absolute',
        textAlign: 'center',
        width: '100%',
        height: `${this.teamCfg.size }px`,
        color: this.teamCfg.color.replace('0x', '#'),
        fontSize: `${this.teamCfg.size }px`,
        display: this.showCfg2.show === 1? 'block': 'none',
        zIndex: 10,
        textShadow: '2px 2px 4px rgba(0,0,0,0.5)' // 添加文字阴影提高可读性
      }
    },

    scoreStyle(){
      return {
        top: `${this.scoreCfg.y}%`,
        position: 'absolute',
        width: '100%',
        textAlign: 'center',
        color: this.scoreCfg.color.replace('0x', '#'),
        fontSize: `${this.scoreCfg.size }px`,
        height: `${this.scoreCfg.size }px`,
        zIndex: 10,
        textShadow: '2px 2px 4px rgba(0,0,0,0.5)' // 添加文字阴影提高可读性
      }
    }
  },

  methods: {
    // 检测媒体类型（图片还是视频）
    getMediaType(buzzStatus) {
      const url = this.getMediaUrl(buzzStatus);
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
    getMediaUrl(buzzStatus) {
      let url;
      if (buzzStatus === 0) {
        // 未抢答的时候只展示图片
        url = this.imageUrl;
      } else if (buzzStatus === 1) {
        url = this.checkMediaExists(this.buzzSuccessVideoUrl) ? this.buzzSuccessVideoUrl : this.buzzSuccessUrl;
      } else {
        url = this.checkMediaExists(this.buzzFailVideoUrl) ? this.buzzFailVideoUrl : this.buzzFailUrl;
      }
      return url;
    },

    // 检查媒体文件是否存在
    checkMediaExists(url) {
      console.log(url)
      // 这里可以实现异步检查，暂时返回true
      // 实际项目中可以通过HEAD请求检查文件是否存在
      return true;
    },

    scoreItemStyle(buzzStatus) {
      const mediaUrl = this.getMediaUrl(buzzStatus);

      let style = {
        textAlign: 'center',
        width: `100%`,
        height: `${100 / this.rows}vh`,
        position: 'relative',
        overflow: 'hidden'
      };

      // 如果是图片，设置背景图片

        this.checkImageUrl(mediaUrl, (isValid) => {
          if (!isValid) {
            style.backgroundImage = `url(${this.defaultImageUrl})`;
          }
        });

        style.backgroundImage = `url(${mediaUrl})`;
        style.backgroundSize = "100% 100%";


      // 根据状态设置背景色
      if (buzzStatus === 1) {
        style.backgroundColor = 'red'; // 红色
      } else if (buzzStatus === 2) {
        style.backgroundColor = 'green'; // 绿色
      } else {
        style.backgroundColor = 'transparent'
      }

      return style;
    },

    // 视频加载错误处理
    handleVideoError(event) {
      console.log('Video load error, falling back to image');
      const video = event.target;
      const container = video.parentElement;

      // 隐藏视频，显示默认背景图片
      video.style.display = 'none';
      container.style.backgroundImage = `url(${this.defaultImageUrl})`;
      container.style.backgroundSize = "100% 100%";
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },

    updateScore() {
      queryTeams().then(res => {
        this.teamList = res.data
      })
    },

    queryStyle(){
      scoreStyle().then(res => {
        const result = res.data.total
        this.codeCfg = result[0]
        this.teamCfg = result[1]
        this.scoreCfg = result[2]
        this.rows =  this.codeCfg.rows
        this.cols =  this.codeCfg.cols

        const showResult = res.data.item
        this.showCfg1 = showResult[0]
        this.showCfg2 = showResult[1]
      })
    },

    // 加载媒体配置（可选方法，用于从外部获取配置）
    loadMediaConfig() {
      // 示例：从localStorage获取配置
      const savedConfig = localStorage.getItem('mediaConfig');
      if (savedConfig) {
        this.mediaConfig = { ...this.mediaConfig, ...JSON.parse(savedConfig) };
      }

      // 或者从API获取配置
      // mediaConfigAPI().then(res => {
      //   this.mediaConfig = { ...this.mediaConfig, ...res.data };
      // });
    },

    // 更新媒体配置的方法
    updateMediaConfig(newConfig) {
      this.mediaConfig = { ...this.mediaConfig, ...newConfig };
      // 清除媒体类型缓存，强制重新检测
      this.mediaTypes = {};
      // 可选：保存到localStorage
      localStorage.setItem('mediaConfig', JSON.stringify(this.mediaConfig));
    },
  },

  mounted() {
    this.queryStyle()
    this.updateScore()

    // 检查所有媒体文件
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("Image invalid, using default")
        this.imageUrl = this.defaultImageUrl;
      }
    });

    this.checkImageUrl(this.buzzSuccessUrl, (isValid) => {
      if (!isValid) {
        this.buzzSuccessUrl = this.defaultImageUrl;
      }
    });

    this.checkImageUrl(this.buzzFailUrl, (isValid) => {
      if (!isValid) {
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
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

.score-item {
  position: relative;
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
</style>