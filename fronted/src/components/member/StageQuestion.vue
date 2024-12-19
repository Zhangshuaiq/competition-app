<template>
  <div class="container" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div id="stageName">
      <div>
        {{stage.name}}
      </div>
    </div>

    <div id="questionList">
      <el-row :gutter="20">
        <el-col span="2.4" v-for="(question, index) in questions" :key="question.questionNumber">
          <div class="question-item" :style="{ visibility: question.hasAnswer === true  ? 'hidden' : 'visible' }">
            {{ index + 1 }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "StageQuestion",
  components: {ScoreBoard},

  data() {
    return {
      imageUrl: `${baseUrl}/resource/选题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stage: {},
      questions: []
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
  
  methods:{
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    console.log(data)
    this.stage = data.stage
    this.questions = data.questions
    next();
  },

  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const data = this.$store.state.data
    console.log(data)
    this.stage = data.stage
    this.questions = data.questions
  }


}
</script>

<style scoped>
/*.container {*/
/*  background-image: url("~@/assets/background.png");*/
/*  background-size: 100vw 100vh;*/
/*  height: 100vh;*/
/*  !*display: flex;           !* 启用Flexbox布局 *!*!*/
/*  !*justify-content: center; !* 水平居中 *!*!*/
/*  !*align-items: center;     !* 垂直居中 *!*!*/
/*}*/

#stageName {
  display: flex; /* 启用Flexbox布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

#stageName div {
  width: 30%;
  height: 40px;
  line-height: 40px;
  text-align: center;
  padding: 10px 0;
  border-radius: 8px;
  color: white;
  background-color: #C40000;
  margin-top: 20px;
  font-size: xx-large;
}

#questionList {
  margin-top: 50px;
  padding: 10px 10%;
}

.question-item {
  width: 60px;
  height: 60px;
  background-color: #C40000;
  line-height: 60px;
  color: white;
  font-size: xx-large;
  border-radius: 8px;
  text-align: center;
  margin: 5px 1px;
}

</style>