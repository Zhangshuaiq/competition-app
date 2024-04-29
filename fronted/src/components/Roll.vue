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

    <div id="question">
      {{ questionNumber }}
    </div>

  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";

export default {
  name: "RollPage",

  components: {ScoreBoard},

  data(){
    return{
      stage: {},
      questionNumber: ''
    }
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    console.log(data)
    this.stage = this.$store.state.stage
    this.questionNumber = data
    next();
  },

  mounted() {
    this.stage = this.$store.state.stage
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

#question {
  text-align: center;
  height: 70vh;
  line-height: 70vh;
  color: red;
  font-size: 200px
}
</style>