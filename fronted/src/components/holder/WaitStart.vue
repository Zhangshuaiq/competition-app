<template>
  <div id="container-box" :style="containerBoxStyle">
    <img :src="iconUrl" style="margin: 10px 0 0 10px">
    <div class="welcome-text" style="margin-top: 15vh" >
      欢迎使用答赛答题竞赛系统
    </div>

    <div class="welcome-text" style="margin-top: 10vh">
      等待开始
    </div>
    
    <div style="margin: 20vh 5% 0 5%; text-align: center; height: 40px; line-height: 40px; width: 90%; background-color: white">
      服务联系方式（微信同号）：陈先生 18008320850
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";
import {answerPrefix} from "@/api";
export default {
  name: "WaitStart",
  data() {
    return {
      imageUrl: `${baseUrl}/resource/等待答题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      iconUrl: `${baseUrl}/resource/图标.jpg?${new Date().getTime()}`
    };
  },
  computed: {
    containerBoxStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize: '100vw 100vh',
        height: '100vh',
      };
    },
  },

  methods:{

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    
    queryPrefix() {
      answerPrefix().then(res => {
        sessionStorage.setItem("prefix", res.data)
      })
    }
  },
  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    sessionStorage.clear()
    this.queryPrefix()
    sessionStorage.setItem("role", "holder")
  },
}
</script>


<style scoped>
.welcome-text {
  text-align: center;
  font-size: 64px;
  color: white; /* 填充色 */
  -webkit-text-stroke-width: 2px; /* 边框宽度 */
  -webkit-text-stroke-color: #ED7D31; /* 边框颜色 */
}
</style>