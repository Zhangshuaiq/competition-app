

<template>
  <div id="container">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div id="result-box">
      <div id="correct-answer">
        正确答案: {{resultData.correctAnswer}}
      </div>
      <div class="member-answer" style="margin-top: 20px">
        <el-row>
          <el-col span="12" v-for="(item, index) in resultData.dataItemList" :key="index">
            <div style="margin: 10px 0">
              <el-row>
                <el-col span="20">
                  <div class="answer-info">团队：{{item.teamName}}  队员：{{item.member}}  回答：{{item.answer}}</div>
                </el-col>
                <el-col span="4">
                  <div class="score-info">{{item.score}}</div>
                </el-col>
              </el-row>
            </div>

          </el-col>

        </el-row>
      </div>
    </div>
  </div>
</template>

<script>

  import ScoreBoard from "@/components/ScoreBoard.vue";

  export default {
    name: "Model61R",
    components: {ScoreBoard},

    data(){
      return {
        resultData: {}
      }
    },


    beforeRouteUpdate(to, from, next) {
      const data = this.$store.state.data
      this.resultData = data
      next();
    },

    mounted() {
      const data = this.$store.state.data
      this.resultData = data
      console.log(this.resultData)
    }
  }

</script>

<style scoped>

#container {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
}

.member-answer {
  height: 50px;
  line-height: 50px;
}


#result-box {
  padding: 30px 10%;
}

#correct-answer {
  background-color: chartreuse;
  color: white;
  text-align: center;
  height: 100px;
  line-height: 100px;
  font-size: xxx-large;
}


.answer-info{
  background-color: #3397CE;
  color: white;
  height: 60px;
  line-height: 60px;
}

.score-info {
  background-color: red;
  color: white;
  height: 60px;
  width: 60px;
  line-height: 60px;
  text-align: center;
}

</style>