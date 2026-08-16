<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
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
      codeCfg: {
        color: 'black'
      },
      teamCfg: {
        color: 'black'
      },
      scoreCfg: {
        color: 'black'
      },
    }
  },

  computed: {
    containerBoxStyle() {
      let imageUrl = this.imageUrl
      if (this.teamItem.buzzStatus === 1) {
        imageUrl = this.buzzSuccessUrl
      } else if (this.teamItem.buzzStatus === 2) {
        imageUrl = this.buzzFailUrl
      }
      return {
        backgroundImage: `url(${imageUrl})`,
        backgroundSize: '100vw 100vh',
        height: '100vh',
        position: 'relative'
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
        top: `${this.codeCfg.y}vh`,
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
        //fontSize: `${this.codeCfg.size }px`,
        alignItems: 'center',
        justifyContent: 'center',
        display: this.codeCfg.show === 1? 'block': 'none'
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
        display: this.teamCfg.show === 1? 'block': 'none'
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
      }
    }
  },
  
  methods: {
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    
    updateScore() {
      queryScore(this.teamItem.id).then(res => {
        sessionStorage.setItem('team', JSON.stringify(res.data))
        this.teamItem = res.data
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
  },
  
  mounted() {
    sessionStorage.setItem('role', 'score')
    const login =  sessionStorage.getItem('team')
    if (!login || login === '') {
      this.$router.push({name: "scoreLogin"})
      return
    }
    this.teamItem = JSON.parse(login)
    this.queryStyle()
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    this.checkImageUrl(this.buzzSuccessUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    this.checkImageUrl(this.buzzFailUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    sessionStorage.setItem('role', 'score')
    this.timer = setInterval(() => {
      this.updateScore()
    }, 1000)
  },
  
  beforeUnmount(){
    if (this.timer) {
      clearInterval(this.timer)
    }
  }


}
</script>

<style scoped>

#container {
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