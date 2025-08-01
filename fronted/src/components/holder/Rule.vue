<template>
  <div id="container-box" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 100px; font-weight: bold; font-size: x-large; " >
      <el-row>
<!--        <el-col span="4" v-if="stage"> 环节: {{ stage.name}}</el-col>-->
      </el-row>
    </div>
    <div id="container">
      <div  style="padding: 100px; text-align: left; font-size: xx-large; white-space: pre-line"  v-html="rule">
      </div>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "QuestionL",
  components: {ScoreBoard},

  data(){
    return {
      imageUrl: `${baseUrl}/resource/总规则背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      rule: '',
      stage: {},
    }
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
  methods: {
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.rule = data
    this.stage = this.$store.state.stage
    next();
  },

  mounted() {
    const login =  sessionStorage.getItem('login')
    if (!login || login === 'false') {
      this.$router.push({name: "hwait"})
      return
    }
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    this.rule = data
    console.log(this.rule)
  },
}
</script>

<style scoped>


#container {
  padding:0 100px;
}

#question-name {
  font-family: "Microsoft YaHei UI";
  font-size: xx-large;
}

#answer-selection {
  margin-top: 20px;
  font-family: "Microsoft YaHei UI";
  font-size: x-large;
}

.selection-item {
  padding: 5px ;
}

.answer-item {
  height: 60px;
  width: 60px;
  line-height: 60px;
  text-align: center;
  font-size: xxx-large;
  background-color: white;
}

.blue-background {
  background-color: skyblue;
}

.result-info {
  margin: 50px;
  font-size: xx-large;
}
</style>