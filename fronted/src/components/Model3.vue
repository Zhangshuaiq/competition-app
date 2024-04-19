<template>
  <div class="container">
    
    <div id="questionList">
      <el-row v-for="i in stages.length" :key="i">
        <el-col span="4">

          <div class="question-item" style="background-color: black; border-radius: 8px">
            {{stages[i - 1].name}}
          </div>
        </el-col>
        <el-col  span="18">
          <el-row>
            <el-col span="3" v-for="question in questions[i - 1]" :key="question.questionNumber">
              <div class="question-item" :style="{ visibility: question.hasAnswer === true  ? 'hidden' : 'visible' }">
                {{ question.questionNumber }}
              </div>
            </el-col>
          </el-row>
        </el-col>
        
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "Model3M",

  data() {
    return {
      stages: [],
      questions: []
    }
  },


  mounted() {
    const data = this.$store.state.data
    console.log(data)
    this.stages = data.stages
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

.stageName div {
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border-radius: 8px;
  color: red;
  background-color: #3397CE;
  margin-top: 100px;
  font-size: xx-large;
}

#questionList {
  padding: 10px 10%;
}

.question-item {
  width: 80px;
  height: 80px;
  background-color: dodgerblue;
  line-height: 80px;
  color: white;
  font-size: xx-large;
  text-align: center;
  margin: 10px;
}

</style>