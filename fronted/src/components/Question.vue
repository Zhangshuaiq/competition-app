<template>
  <div id="container-box">
    <div id="container">
      <div v-if="rule" style="padding: 100px; text-align: center">
        {{rule}}
      </div>
      <div v-else>
        <div id="question-info"  v-if="questionItem">
          <div v-if="second >= 0" style="font-size: xx-large; color: red">
            剩余时间: {{second}} 秒
          </div>
          <div id="question-name">
            {{ questionItem.questionNumber }} &nbsp;&nbsp; {{ questionItem.question }}
          </div>
          <div id="answer-selection">
            <div class="selection-item"  v-if="questionItem.choiceA">
              A . {{questionItem.choiceA}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceB">
              B . {{questionItem.choiceB}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceC">
              C . {{questionItem.choiceC}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceD">
              D . {{questionItem.choiceD}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceE">
              E . {{questionItem.choiceE}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceF">
              F . {{questionItem.choiceF}}
            </div>
          </div>
        </div>
        
        <div v-if="showResult" id="answer-info" style="font-size: xx-large; margin-top: 20px; text-align: center;">
          正确答案: <font color="#cd5c5c">{{questionItem.answer}}</font>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "QuestionL",

  data(){
    return {
      retryTimer: null, // 用于重试逻辑的计时器
      retryInterval: 500, // 重试间隔时间，例如5秒

      subscription: null,
      questionItem: null,
      teamInfo: '',
      second: -1,
      showResult: false,
      showWait: true,
      rule: '',
    }
  },
  methods: {
    saveData(){
      sessionStorage.setItem("showResult", this.showResult + "")
      sessionStorage.setItem("showWait", this.showWait + "")
      sessionStorage.setItem("questionItem", JSON.stringify(this.questionItem))
    },

    setRetryTimer() {
      // 先清除之前的计时器（如果有）
      clearTimeout(this.retryTimer);
      this.retryTimer = setTimeout(() => {
        // 这里重新调用订阅逻辑
        this.subscribeQuestion();
      }, this.retryInterval);
    },

    loadData(){
      // const wait = sessionStorage.getItem("showWait")
      // if (wait && wait !== 'null') {
      //   this.showWait = wait === 'true'
      // }
      //
      // const result = sessionStorage.getItem("showResult")
      // if (result && result !== 'null') {
      //   this.showResult = result === 'true'
      // }
      //
      // const item = sessionStorage.getItem("questionItem");
      // if (item && item !== 'null') {
      //   this.questionItem = JSON.parse(item)
      // }
      // console.log(this.questionItem)
    },

    subscribeQuestion(){
      this.subscription = this.$websocket.subscribe('/topic/question', (message) => {
        if (message.type === 'rule') {
          console.log(message.data)
          this.rule = message.data
          return
        }
        if (message.type === 'time') {
          console.log(message.data)
          this.second = parseInt(message.data)
        } else {
          this.showResult = true
        }

      })

    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.questionItem = data
    this.showResult = false
    next();
  },

  mounted() {
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.teamInfo = JSON.parse(item)
    this.loadData();
    const data = this.$store.state.data
    this.questionItem = data
    this.setRetryTimer()
  },
}
</script>

<style scoped>

#container-box {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
}

#container {
  padding: 50px 100px;
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