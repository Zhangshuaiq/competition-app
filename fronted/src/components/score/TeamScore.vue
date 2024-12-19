<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <div :style="codeStyle">
        
      </div>

      <div :style="teamStyle">

      </div>

      <div :style="scoreStyle">

      </div>
    </div>
  </div>
  
</template>

<script>
import baseUrl from "@/config";

export default {
  name: "TeamScore",
  
  data() {
    return {
      dialogFormVisible: false,
      teamItem: '',
      imageUrl: `${baseUrl}/resource/团队分数背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      codeCfg: {},
      teamCfg: {},
      scoreCfg: {},
    }
  },

  computed: {
    containerBoxStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize: '100vw 100vh',
        height: '100vh',
        position: 'relative'
      };
    },

    codeStyle(){
      return {
        position: 'absolute',
        width: 'max-content',
        top: `${this.codeCfg.y}`,
        left: `${this.codeCfg.x}`,
        color: `${this.codeCfg.color}`,
        fontsize: `${this.codeCfg.size}`,
      }
    },

    teamStyle(){
      return {
        position: 'absolute',
        width: 'max-content',
        top: `${this.teamCfg.y}`,
        left: `${this.teamCfg.x}`,
        color: `${this.teamCfg.color}`,
        fontsize: `${this.teamCfg.size}`,
      }
    },

    scoreStyle(){
      return {
        position: 'absolute',
        width: 'max-content',
        top: `${this.scoreCfg.y}`,
        left: `${this.scoreCfg.x}`,
        color: `${this.scoreCfg.color}`,
        fontsize: `${this.scoreCfg.size}`,
      }
    }
  },
  
  methods: {
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    }
  },
  
  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
  }


}
</script>

<style scoped>

#container {
  padding: 100px;
}

.haotai {
  margin-top: 40px;
  margin: 0 30px;
  text-align: center;
  color: red;
  height: 60px;
  line-height: 60px;
  font-size: xx-large;
  border-width: 1px;
  border-color: black;
  background-color: white;
  border-radius: 20px;
}


.haotai:hover {
  background-color: #000000; /* 鼠标悬停时的背景颜色 */
  color: #ffffff;
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