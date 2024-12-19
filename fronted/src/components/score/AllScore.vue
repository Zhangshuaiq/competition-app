<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <div :style="codeStyle">
        {{ teamItem.code }}
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
        width: `${this.codeCfg.size/ 40 }em`,
        height: `${this.codeCfg.size/ 40 }em`,
        borderRadius: '50%',
        border: `0.05em solid ${this.codeCfg.color.replace('0x', '#')}`,
        textAlign: 'center',
        top: `${this.codeCfg.y}vh`,
        left: `${this.codeCfg.x}vw`,
        color: this.codeCfg.color.replace('0x', '#'),
        fontSize: `${this.codeCfg.size/ 15 }em`,
        //fontSize: `${this.codeCfg.size }px`,
        alignItems: 'center',
        justifyContent: 'center',
        display: this.codeCfg.show === 1? 'block': 'none'
      }
    },

    teamStyle(){
      return {
        position: 'absolute',
        width: 'max-content',
        top: `${this.teamCfg.y}vh`,
        left: `${this.teamCfg.x}vw`,
        color: this.teamCfg.color.replace('0x', '#'),
        fontSize: `${this.teamCfg.size / 15 }em`,
        display: this.teamCfg.show === 1? 'block': 'none'
      }
    },

    scoreStyle(){
      return {
        position: 'absolute',
        width: 'max-content',
        top: `${this.scoreCfg.y}vh`,
        left: `${this.scoreCfg.x}vw`,
        color: this.scoreCfg.color.replace('0x', '#'),
        fontSize: `${this.scoreCfg.size/ 15 }em`,
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
    
    this.timer = setInterval(() => {
      this.updateScore()
    }, 1000)
  },
  
  beforeDestroy(){
    
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