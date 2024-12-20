<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <div v-if="rule" style="padding: 100px; text-align: center">
        {{rule}}
      </div>
      <div v-else>
        <div id="status-bar">
          <div style="padding: 10px; color: white">
            <el-row>
              <el-col span="6">
                {{ teamInfo.fullName }}
              </el-col>
              <el-col span="6">
                {{ score }}  分
              </el-col>
              <el-col  span="12">
                <div v-if="showResult">
                  <div v-if="questionIndex == questionList.length - 1">
                    完成{{questionIndex<0?0:questionIndex + 1}}题 <font color="red">答对 {{correctCount}}题</font> | 用时{{ formatTime() }}
                  </div>
                  <div v-else>
                    完成{{questionIndex<0?0:questionIndex}}题  <font color="red">答对 {{correctCount}}题</font>  | 用时{{ formatTime() }}
                  </div>
                </div>
                <div v-else>
                  完成{{questionIndex<0?0:questionIndex}}题  <font color="red">答对 {{correctCount}}题</font>  | 用时{{ formatTime() }}
                </div>

              </el-col>
            </el-row>
          </div>

          <div v-if="questionItem" style="padding: 10px; color: white">
<!--            <el-row>-->
<!--              <el-col v-for="i in questionList.length" :key="i" span="1">-->
<!--                <div v-if="i <= questionList.length" style="padding: 10px 0">-->
<!--                  <div v-if="i > userSelects.length" style="height: 20px; width: 20px; border-radius: 10px; background-color: white">-->

<!--                  </div>-->
<!--                  <div v-else-if="userSelects[i-1] === questionList[i-1].answer" style="height: 20px; width: 20px; border-radius: 10px; background-color: green">-->

<!--                  </div>-->
<!--                  <div v-else style="height: 20px; width: 20px; border-radius: 10px; background-color: red">-->

<!--                  </div>-->
<!--                </div>-->

<!--              </el-col>-->
<!--            </el-row>-->
          </div>
        </div>
        <div id="question-info"  v-if="questionItem && !showResult">
          <div id="question-name">
            <div v-if="questionItem.txbz === '4'">
              {{ questionItem.questionNumber }} &nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.txbz === '3'">
              {{ questionItem.questionNumber }} &nbsp;&nbsp; {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.txbz === '2'">
              {{ questionItem.questionNumber }} &nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
            </div>

            <div v-if="questionItem.txbz === '1'">
              {{ questionItem.questionNumber }} &nbsp; [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
            </div>
<!--            {{ questionItem.questionNumber }} &nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}-->
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
            <!--    单选题答案只能选择一个       -->
            <div v-if="questionItem.questionType === '单选题'">
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
            <div v-else-if="questionItem.questionType === '判断题'">
              <el-row type="flex" justify="center" style="text-align: center">
                <el-col span="4" class="column-center" >
                  <div class="answer-item"   :class="{ 'blue-background': activeDiv === '正确' }" @click="setActiveDiv('正确')">
                    正确
                  </div>
                </el-col>
                <el-col span="4" class="column-center">
                  <div class="answer-item"   :class="{ 'blue-background': activeDiv === '错误' }" @click="setActiveDiv('错误')" >
                    错误
                  </div>
                </el-col>
              </el-row>
            </div>
            <div v-else-if="questionItem.questionType === '多选题' || questionItem.questionType === '不定项选择题'">
              <el-row type="flex" justify="center" style="text-align: center">
                <el-col span="4" class="column-center"  v-if="questionItem.choiceA">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('A') >= 0 }" @click="appendActiveDiv('A')">
                    A
                  </div>
                </el-col>
                <el-col span="4" class="column-center" v-if="questionItem.choiceB">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('B') >= 0  }" @click="appendActiveDiv('B')" >
                    B
                  </div>
                </el-col>
                <el-col span="4" class="column-center" v-if="questionItem.choiceC">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('C') >= 0  }" @click="appendActiveDiv('C')" >
                    C
                  </div>

                </el-col>
                <el-col span="4" class="column-center" v-if="questionItem.choiceD">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('D') >= 0  }" @click="appendActiveDiv('D')" >
                    D
                  </div>
                </el-col>
                <el-col span="4" class="column-center" v-if="questionItem.choiceE">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('E') >= 0  }" @click="appendActiveDiv('E')" >
                    E
                  </div>
                </el-col>
                <el-col span="4" class="column-center" v-if="questionItem.choiceF">
                  <div class="multi-answer-item" :class="{ 'blue-background': activeDiv.indexOf('F') >= 0  }" @click="appendActiveDiv('F')" >
                    F
                  </div>
                </el-col>
              </el-row>
            </div>
            <div v-else>
              {{questionItem.questionType}}
              <el-row type="flex" justify="center" style="text-align: center">
                <el-input :disabled="(second <= 0 || showResult)" placeholder="请输入答案" type="textarea" rows="3" v-model="activeDiv"></el-input>
              </el-row>
            </div>
          </div>
          <div v-if="questionItem" style="text-align: center; margin-top: 20px">
            <el-button :disabled="!btnClicked || this.activeDiv === ''" style="width: 200px; height: 50px; font-size: x-large; z-index: 2" type="primary" @click="submitAnswer"> 确 认 </el-button>
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
import baseUrl from "@/config";
import {queryStatus, saveStatus} from "@/api";
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
      imageUrl: `${baseUrl}/resource/竞技模式背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
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
    
    correctCount(){
      let count = 0
      for (let i = 0; i < this.questionList.length; i++) {
        if (this.userSelects[i] === this.questionList[i].answer) {
          count = count + 1
        }
      }
      return count
    },
  },
  
  methods: {

    handleBeforeClose(event){
      console.log(event)
      this.saveCurrentStatus()
    },

    saveCurrentStatus(){
      const data = {
        teamId: this.teamInfo.code,
        questionText: JSON.stringify(this.questionList),
        currentNo: this.questionIndex,
        userSelects: JSON.stringify(this.userSelects),
        score: this.score
      }

      saveStatus(data)
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    
    submitAnswer(){
      // 提交答案到后台
      console.log(this.activeDiv)
      
      const submited = JSON.parse(JSON.stringify(this.questionItem))
      const selected = this.activeDiv
      if (selected === this.questionItem.answer) {
        this.score = this.score + this.questionItem.score
      } else {
        this.score = this.score - this.questionItem.desValue
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
         this.saveCurrentStatus()
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

    appendActiveDiv(divNumber) {
      if (this.activeDiv.indexOf(divNumber) >= 0) {
        this.activeDiv = this.activeDiv.replace(divNumber, "")
      } else {
        this.activeDiv = this.activeDiv + divNumber;
      }
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
          if (!this.showResult) {
            if (this.questionIndex + 1 === this.questionList.length) {
              this.score = this.score -  this.questionItem.desValue
            } else {
              this.score = this.score - ((this.questionList.length - this.questionIndex) * this.questionItem.desValue)
            }
            this.showResult = true
          }
          this.saveData()
        } else {
          queryStatus(this.teamInfo.code).then(res => {
            if (res.data) {
              console.log("已经获取过数据了......")
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
        }
        
      })
      
    },

    
  },
  
  
  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
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

    queryStatus(this.teamInfo.code).then(res => {
      if (res.data) {
        this.questionList = JSON.parse(res.data.questionText)
        this.questionIndex = (res.data.currentNo)
        this.questionItem = this.questionList[this.questionIndex]
        this.questionItem.teamName = this.teamInfo.fullName
        this.userSelects = JSON.parse(res.data.userSelects)
        this.showWait = false
        this.showResult = false
        this.score = res.data.score
        this.saveData()
      }
    })


    this.setRetryTimer()
    window.addEventListener("beforeunload", this.handleBeforeClose)
  },

  beforeDestroy() {
    window.removeEventListener("beforeunload", this.handleBeforeClose)
  },

  beforeRouteLeave(to, from, next) {
    // 在这里编写离开页面前需要执行的逻辑
    // 例如，你可以在这里提示用户保存未保存的更改
    if (from.name !== to.name && to.name !== 'stageRank') {
      sessionStorage.removeItem("time")
      sessionStorage.removeItem("score")
      sessionStorage.removeItem("showResult")
      sessionStorage.removeItem("userSelects")
      sessionStorage.removeItem("questionIndex")
      sessionStorage.removeItem("questionItem")
      sessionStorage.removeItem("questionList")
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



#container {
  padding: 50px 100px;
}

#question-name {
  font-family: "Microsoft YaHei UI";
  font-size: x-large;
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
  font-size: large;
  background-color: white;
  border-width: 1px;
  border-color: black;
  border-style: solid;
  border-radius: 8px;
}

.multi-answer-item {
  height: 60px;
  width: 60px;
  line-height: 60px;
  text-align: center;
  font-size: large;
  background-color: white;
  border-width: 1px;
  border-color: black;
  border-style: solid;
  border-radius: 8px;
}

.blue-background {
  background-color: #4472C4;
}

.result-info {
  margin: 50px;
  font-size: xx-large;
}
</style>