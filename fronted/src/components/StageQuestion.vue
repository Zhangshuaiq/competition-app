<template>
  <div class="container">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div id="stageName">
      <div>
        {{stage.name}}
      </div>
    </div>

    <div id="questionList">
      <el-row>
        <el-col span="4" v-for="question in questions" :key="question.questionNumber">
          <div class="question-item" :style="{ visibility: question.hasAnswer === true  ? 'hidden' : 'visible' }">
            {{ question.questionNumber }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";

export default {
  name: "StageQuestion",
  components: {ScoreBoard},

  data() {
    return {
      stage: {},
      questions: []
    }
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    console.log(data)
    this.stage = data.stage
    this.questions = data.questions
    next();
  },

  mounted() {
    const data = this.$store.state.data
    console.log(data)
    this.stage = data.stage
    this.questions = data.questions
  }


}
</script>

<style scoped>
.container {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
  /*display: flex;           !* 启用Flexbox布局 *!*/
  /*justify-content: center; !* 水平居中 *!*/
  /*align-items: center;     !* 垂直居中 *!*/
}

#stageName {
  display: flex; /* 启用Flexbox布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

#stageName div {
  width: 500px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border-radius: 8px;
  color: red;
  background-color: #3397CE;
  margin-top: 20px;
  font-size: xx-large;
}

#questionList {
  padding: 10px 25%;
}

.question-item {
  width: 80px;
  height: 80px;
  background-color: dodgerblue;
  line-height: 80px;
  color: white;
  font-size: xx-large;
  text-align: center;
  margin: 5px;
}

</style>