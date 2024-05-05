<template>
  <div id="container-box">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 20px; font-weight: bold; " >
      <el-row>
        <el-col span="4"> 组号: {{ teamInfo.shortName }}</el-col>
        <el-col span="4"> 组名: {{ teamInfo.fullName }}</el-col>
        <el-col span="4"> 选手: {{ member }}</el-col>
        <el-col span="4"> 环节: {{ stage.name}}</el-col>
        <el-col span="4">
          <div v-if="second >= 0" style="color: red">
            时间: {{second}} 秒
          </div>
        </el-col>
      </el-row>
    </div>
    <div id="container">
      <div v-if="rule" style="padding: 100px; text-align: center">
        {{rule}}
      </div>
      <div v-else>
        <div id="question-info"  v-if="questionItem">
         
          <div id="question-name">
            {{ questionItem.questionNumber }} &nbsp;({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
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
          <br>
          <div style="margin: 0 25%">
            <el-row type="flex" justify="center" style="text-align: center">
              <el-col span="4" class="column-center"  v-if="questionItem.choiceA">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'A' }" @click="setActiveDiv('A')">
                  A
                </div>
              </el-col>
              <el-col span="4" class="column-center" v-if="questionItem.choiceB">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'B' }" @click="setActiveDiv('B')" >
                  B
                </div>
              </el-col>
              <el-col span="4" class="column-center" v-if="questionItem.choiceC">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'C' }" @click="setActiveDiv('C')" >
                  C
                </div>

              </el-col>
              <el-col span="4" class="column-center" v-if="questionItem.choiceD">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'D' }" @click="setActiveDiv('D')" >
                  D
                </div>
              </el-col>
              <el-col span="4" class="column-center" v-if="questionItem.choiceE">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'E' }" @click="setActiveDiv('E')" >
                  E
                </div>
              </el-col>
              <el-col span="4" class="column-center" v-if="questionItem.choiceF">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'F' }" @click="setActiveDiv('F')" >
                  F
                </div>
              </el-col>
            </el-row>
          </div>

          <div v-if="questionItem" style="text-align: center; margin-top: 20px">

            <div v-if="submit">
              <el-button disabled style="width: 200px; height: 50px; font-size: x-large" type="primary" @click="submitAnswer"> {{btnText}} </el-button>
            </div>
            <div v-else>
              <el-button :disabled="second <= 0 || showResult || this.activeDiv === ''" style="width: 200px; height: 50px; font-size: x-large" type="primary" @click="submitAnswer"> {{btnText}} </el-button>
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
import ScoreBoard from "@/components/ScoreBoard";

export default {
  name: "Model60M",
  components: {ScoreBoard},
  data(){
    return {
      retryTimer: null, // 用于重试逻辑的计时器
      retryInterval: 500, // 重试间隔时间，例如5秒

      subscription: null,
      questionItem: null,
      activeDiv: '',
      teamInfo: '',
      second: -1,
      showResult: false,
      showWait: true,
      rule: '',
      btnText: "提 交",
      member: '',
      stage: {},
      submit : false,
    }
  },
  methods: {
    submitAnswer(){
      // 提交答案到后台
      console.log(this.activeDiv)
      const submited = JSON.parse(JSON.stringify(this.questionItem))
      // 队伍名称, 题目分值
      submited.teamName =  this.teamInfo.fullName
      submited.userSelect = this.activeDiv
      submited.second = this.second
      this.questionItem = submited
      this.submit = true
      this.saveData()
      // 题目列表
      //  提交答案
      this.$websocket.send('/app/model60/submit', {
        data: JSON.stringify(submited),
      })
      this.btnText = "已提交"
    },


    saveData(){
      sessionStorage.setItem("showResult", this.showResult + "")
      sessionStorage.setItem("showWait", this.showWait + "")
      sessionStorage.setItem("submit", this.submit + "")
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
      const wait = sessionStorage.getItem("showWait")
      if (wait && wait !== 'null') {
        this.showWait = wait === 'true'
      }

      const submit = sessionStorage.getItem("submit")
      if (submit && submit !== 'null') {
        this.submit = submit === 'true'
        if (this.submit) {
          this.btnText = "已提交"
        } else {
          this.btnText = "提 交"
        }
      }

      const result = sessionStorage.getItem("showResult")
      if (result && result !== 'null') {
        this.showResult = result === 'true'
      }

      const item = sessionStorage.getItem("questionItem");
      if (item && item !== 'null') {
        this.questionItem = JSON.parse(item)
        console.log(this.questionItem)
        this.activeDiv = this.questionItem.userSelect
      }
    },


    setActiveDiv(divNumber) {
      this.activeDiv = divNumber;
    },

    subscribeQuestion(){
      this.subscription = this.$websocket.subscribe('/topic/question', (message) => {
        if (message.type === 'rule') {
          console.log(message.data)
          this.rule = message.data
          return
        }
        if (message.type === 'time') {
          console.log(this.showResult)
          this.second = parseInt(message.data)
          if (this.second <= 0) {
            if (this.showResult === false) {
              this.activeDiv = ''
              //this.submitAnswer()
            }
          }
        } else {
          this.showResult = ! this.showResult
        }

      })

    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.questionItem = data
    this.showResult = false
    this.activeDiv = ''
    this.btnText = "提 交"
    this.submit = false
    this.activeDiv = ''
    this.saveData()
    next();
  },
  
  
  mounted() {
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.teamInfo = JSON.parse(item)
    console.log(this.teamInfo)
    this.member = sessionStorage.getItem('member')

    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    this.questionItem = data
    this.loadData();
    this.setRetryTimer()
  },
  
}
</script>

<style scoped>

.column-center {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

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