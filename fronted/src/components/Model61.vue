<template>
  <div id="container-box">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
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
  
            <div class="selection-item" >
              B . {{questionItem.choiceB}}
            </div>
  
            <div class="selection-item" >
              C . {{questionItem.choiceC}}
            </div>
  
            <div class="selection-item" >
              D . {{questionItem.choiceD}}
            </div>
  
            <div class="selection-item" >
              E . {{questionItem.choiceE}}
            </div>
  
            <div class="selection-item" >
              F . {{questionItem.choiceF}}
            </div>
          </div>
          
          <br>
          <div>
            <el-row>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'A' }" @click="setActiveDiv('A')" v-if="questionItem.choiceA">
                  A 
                </div>
              </el-col>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'B' }" @click="setActiveDiv('B')" v-if="questionItem.choiceB">
                  B 
                </div>
              </el-col>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'C' }" @click="setActiveDiv('C')" v-if="questionItem.choiceC">
                  C 
                </div>
  
              </el-col>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'D' }" @click="setActiveDiv('D')" v-if="questionItem.choiceD">
                  D 
                </div>
              </el-col>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'E' }" @click="setActiveDiv('E')" v-if="questionItem.choiceE">
                  E 
                </div>
              </el-col>
              <el-col span="4">
                <div class="answer-item" :class="{ 'blue-background': activeDiv === 'F' }" @click="setActiveDiv('F')" v-if="questionItem.choiceF">
                  F 
                </div>
              </el-col>
            </el-row>
          </div>
          
          <div v-if="questionItem" style="text-align: center; margin-top: 30px">
            <el-button :disabled="second <= 0 || showResult" style="width: 200px; height: 50px; font-size: x-large" type="primary" @click="submitAnswer"> {{btnText}} </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";

export default {
  name: "Model61M",
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
      rule: '',
      btnText: "提 交"
    }
  },
  methods: {
    submitAnswer(){
      // 提交答案到后台
      console.log(this.activeDiv)
      this.showResult = true;
      const submited = JSON.parse(JSON.stringify(this.questionItem))


      // 队伍名称, 题目分值
      submited.teamName =  this.teamInfo.fullName
      submited.userSelect = this.activeDiv
      submited.userName = sessionStorage.getItem("member")
      this.questionItem = submited
      this.saveData()
      // 题目列表
      console.log(submited)
      //  提交答案
      this.$websocket.send('/app/model61/submit', {
        data: JSON.stringify(submited),
      })
      this.btnText = "已提交"
    },
    

    saveData(){
      sessionStorage.setItem("showResult", this.showResult + "")
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
      const result = sessionStorage.getItem("showResult")
      if (result && result !== 'null') {
        this.showResult = result === 'true'
        if (this.showResult) {
          this.btnText = "已提交"
        } else {
          this.btnText = "提 交"
        }
      }

      const item = sessionStorage.getItem("questionItem");
      if (item && item !== 'null') {
        this.questionItem = JSON.parse(item)
        this.activeDiv = this.questionItem.userSelect
      }
      console.log(this.questionItem)
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
          console.log(message.data)
          this.second = parseInt(message.data)
          if (this.second <= 0) {
            if (this.showResult === false) {
              this.activeDiv = ''
              this.submitAnswer()
            }
          }
        } else {
          this.questionItem = message.data
          this.questionItem.teamName = this.teamInfo.fullName
          this.activeDiv = ''
          this.showResult = false
          this.saveData()
        }
        
      })

      this.$websocket.subscribe('/topic/model61/start', (message) => {
        console.log(message)
        this.showResult = false
        this.saveData()
      })
    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.questionItem = data
    this.showResult = false
    this.btnText = "提 交"
    this.activeDiv = ""
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
    this.loadData();
    const data = this.$store.state.data
    if (this.questionItem.questionNumber !== data.questionNumber) {
      this.questionItem = data
      this.activeDiv = ""
      this.showResult = false
    }
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