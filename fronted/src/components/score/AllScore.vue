<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <el-row v-for="x in rows" :key="x">
        <el-col :span="24 / cols" v-for="y in cols" :key="y">
          <div v-if="teamList[(x - 1) * cols + y - 1]" class="score-item" :style="scoreItemStyle(teamList[(x - 1) * cols + y - 1].buzzStatus)">
            <div >
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
        </el-col>
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
      }
    }
  },
  
  methods: {

    scoreItemStyle(buzzStatus) {
      let imageUrl;
      if (buzzStatus === 0) {
        imageUrl = this.imageUrl;
      } else if (buzzStatus === 1) {
        imageUrl = this.buzzSuccessUrl;
      } else {
        imageUrl = this.buzzFailUrl;
      }

      this.checkImageUrl(imageUrl, (isValid) => {
        if (!isValid) {
          imageUrl = this.defaultImageUrl;
        }
      });

      return {
        textAlign: 'center',
        backgroundImage: `url(${imageUrl})`,
        backgroundSize: "100% 100%",
        width: `100%`,
        height: `${100 / this.rows}vh`,
        position: 'relative',
        backgroundColor: buzzStatus === 1 ? 'red' : buzzStatus === 2 ? 'green' : 'transparent'
      };
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
        console.log(result)
        this.codeCfg = result[0]
        this.teamCfg = result[1]
        this.scoreCfg = result[2]
        this.rows =  this.codeCfg.rows
        this.cols =  this.codeCfg.cols
      })
    },
  },
  
  mounted() {
    this.queryStyle()
    this.updateScore()
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
  overflow: hidden; /* 禁止滚动条 */
}

</style>