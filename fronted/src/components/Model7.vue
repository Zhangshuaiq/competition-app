<template>
  <div id="container-box">
    <div id="container">
      <div v-if="rule" style="padding: 100px; text-align: center">
        {{rule}}
      </div>
      <div v-else>
        <div id="status-bar">
          <div style="padding: 10px; color: white">
            <el-row>
              <el-col span="12">
                {{ teamInfo.fullName }}
              </el-col>
              <el-col span="12">
                完成{{questionIndex<0?0:questionIndex}}题 | 用时{{ formatTime() }}
              </el-col>
            </el-row>
          </div>

          <div v-if="questionItem" style="padding: 10px; color: white">
            <el-row>
              <el-col v-for="i in 24" :key="i" span="1">
                <div v-if="i <= questionList.length">
                  <div v-if="i > userSelects.length" style="height: 20px; width: 20px; border-radius: 10px; background-color: white">

                  </div>
                  <div v-else-if="userSelects[i-1] === questionList[i-1].answer" style="height: 20px; width: 20px; border-radius: 10px; background-color: green">

                  </div>
                  <div v-else style="height: 20px; width: 20px; border-radius: 10px; background-color: red">

                  </div>
                </div>

              </el-col>
            </el-row>
          </div>
        </div>
        <div id="question-info"  v-if="questionItem && !showResult">
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
            <el-button :disabled="!btnClicked" style="width: 200px; height: 50px; font-size: x-large" type="primary" @click="submitAnswer"> 确 认 </el-button>
          </div>
        </div>
        <div v-if="showResult" style="text-align: center; padding: 0 100px">
          <div class="result-info">
            答题结束
          </div>
          <div class="result-info">
            {{ score }} 分
          </div>

          <div class="result-info">
            {{ formatTime() }}
          </div>
        </div>

        <div v-if="showWait" style="text-align: center; padding: 0 100px">
          <div class="result-info">

          </div>
          <div class="result-info">
            等待答题
          </div>

          <div class="result-info">

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Model7M",
  data(){
    return {

      retryTimer: null, // 用于重试逻辑的计时器
      retryInterval: 500, // 重试间隔时间，例如5秒
      
      btnClicked : false,
      subscription: null,
      questionList: [],
      userSelects: [],
      questionIndex: -1,
      questionItem: null,
      activeDiv: '',
      second: -1,
      teamInfo: '',
      score: 0,
      // 定时器是否一开始
      hasStart: false,
      // 是否进入显示结果页面
      showResult: false,
      showWait: true,
      rule: '',
    }
  }, 
  methods: {
    submitAnswer(){
      // 提交答案到后台
      console.log(this.activeDiv)
      
      const submited = JSON.parse(JSON.stringify(this.questionItem))
      const selected = this.activeDiv
      if (selected === this.questionItem.answer) {
        this.score = this.score + this.questionItem.score
      }
      this.userSelects.push(selected)
      this.saveData()
      // 队伍名称, 题目分值
      submited.teamName =  this.teamInfo.fullName
      submited.userSelect = this.activeDiv
      // 题目列表
      console.log(submited)
      if (this.questionIndex >= 0 ) {
        this.$websocket.send('/app/model7/submit', {
          data: JSON.stringify(submited),
          time: this.formatTime(this.second),
          second: this.second
        })
         if ( this.questionIndex < this.questionList.length - 1) {
           this.questionIndex ++;
           this.questionItem = this.questionList[this.questionIndex]
           this.activeDiv = ''
           this.questionItem.teamName =  this.teamInfo.fullName
         } else {
           this.hasStart = false
           // 显示结果 div
           this.showResult = true
           this.btnClicked = false
           this.saveData()
         }
      }
    },

    formatTime() {
      const seconds = this.second;
      if (seconds <= 0) {
        return `0 分 00 秒`;
      }
      const minutes = Math.floor(seconds / 60); // 计算总分钟数
      const remainingSeconds = seconds % 60; // 计算剩余秒数
      // 将剩余秒数格式化为两位数
      const formattedSeconds = remainingSeconds.toString().padStart(2, '0');
      return `${minutes} 分 ${formattedSeconds} 秒`;
    },
    
    saveData(){
      sessionStorage.setItem("time", this.second + "")
      sessionStorage.setItem("score", this.score + "")
      sessionStorage.setItem("showWait", this.showWait + "")
      sessionStorage.setItem("showResult", this.showResult + "")
      sessionStorage.setItem("questionItem", JSON.stringify(this.questionItem))
      sessionStorage.setItem("userSelects", JSON.stringify(this.userSelects))
      sessionStorage.setItem("questionList", JSON.stringify(this.questionList))
      sessionStorage.setItem("questionIndex", JSON.stringify(this.questionIndex))
    },

    setRetryTimer() {
      // 先清除之前的计时器（如果有）
      clearTimeout(this.retryTimer);
      this.retryTimer = setTimeout(() => {
        console.log('订阅超时，尝试重新订阅...');
        // 这里重新调用订阅逻辑
        this.subscribeQuestionGroup();
      }, this.retryInterval);
    },
    
    loadData(){

      const score = sessionStorage.getItem("score")
      if (score && score !== 'null') {
        this.score = parseInt(score)
      }

      const wait = sessionStorage.getItem("showWait")
      if (wait && wait !== 'null') {
        this.showWait = wait === 'true'
      }
      const result = sessionStorage.getItem("showResult")
      if (result && result !== 'null') {
        this.showResult = result === 'true'
      }
      
      const item = sessionStorage.getItem("questionItem");
      if (item && item !== 'null') {
        this.questionItem = JSON.parse(item)
      }
      const list = sessionStorage.getItem("questionList");
      if (list && list !== 'null') {
        this.questionList = JSON.parse(list)
      }
      const selects = sessionStorage.getItem("userSelects");
      if (selects && selects !== 'null') {
        this.userSelects = JSON.parse(selects)
      }
      const index = sessionStorage.getItem("questionIndex");
      if (index && index !== 'null') {
        this.questionIndex = JSON.parse(index)
      }
      console.log(this.questionItem)
    },


    setActiveDiv(divNumber) {
      this.activeDiv = divNumber;
    },

    shuffleArray(array) {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]]; // ES6 语法，用于交换两个元素
      }
      return array;
    },
    
    subscribeQuestionGroup(){
      this.subscription = this.$websocket.subscribe('/topic/question/group', (message) => {
        clearTimeout(this.retryTimer);
        this.rule = ''
        if (message.type === 'time') {
          this.showWait = false
          this.btnClicked = true
          console.log(message.data)
          if (!this.showResult) {
            this.second = parseInt(message.data)
          }
          this.saveData()
        } else if (message.type === 'finish') {
          this.showResult = true
          this.saveData()
        } else {
          const random = message.random
          // 随机乱序题序
          if (random) {
            this.questionList = this.shuffleArray(message.data)
          } else {
            this.questionList = message.data
          }
          this.questionIndex = 0;
          this.questionItem = this.questionList[this.questionIndex]
          this.questionItem.teamName = this.teamInfo.fullName
          this.userSelects = []
          this.showWait = false
          this.showResult = false
          this.score = 0
          this.saveData()
        }
        
      })
      
    },

    
  },
  
  mounted() {
    this.rule = sessionStorage.getItem('rule')
    sessionStorage.setItem('rule', '')
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.teamInfo = JSON.parse(item)
    console.log(this.teamInfo)
    this.loadData();
    const time = sessionStorage.getItem("time")
    if (time && time !== "null"){
      this.second = parseInt(time)
    }
    this.setRetryTimer()
  },

  beforeRouteLeave(to, from, next) {
    console.log(from, to, next)
    // 在这里编写离开页面前需要执行的逻辑
    // 例如，你可以在这里提示用户保存未保存的更改
    if (from.name !== to.name) {
      sessionStorage.removeItem("time")
      sessionStorage.removeItem("score")
      sessionStorage.removeItem("showResult")
    }
    next()
  },
  
}                                                                             
</script>

<style scoped>

.column-center {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

#status-bar {
  background-color: #3397CE;
  margin: 0 0 20px 0;
  border-radius: 10px;
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