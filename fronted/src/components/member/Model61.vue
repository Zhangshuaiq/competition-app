<template>
  <div id="container-box" :style="containerBoxStyle">
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

          <!--       图片不区分题目类型。因为图文制作在一起   -->
          <div v-if="questionItem.resourceType === '图片'">
            <img :src="resourceUrl" alt="Overlay Image" class="overlay-img">
          </div>
          <!--    音频无需展示      -->
          <!--    视频需要播放      -->
          <!--    文字则正常展示      -->

          <div v-else-if="questionItem.questionType === '单选题' || questionItem.questionType === '多选题' || questionItem.questionType === '不定项选择题'">
            <div class="question-name">


              <div v-if="questionItem.txbz === '4'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  {{ questionItem.question }}
                </div>
              </div>
              <div v-if="questionItem.txbz === '3'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp;&nbsp; {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
                </div>
              </div>
              <div v-if="questionItem.txbz === '2'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
                </div>

              </div>

              <div v-if="questionItem.txbz === '1'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
                </div>

              </div>
            </div>
            <div v-if="questionItem.resourceType === '视频'">
              <video width="320" height="240" controls muted>
                <source :src="resourceUrl" type="video/mp4">
                Your browser does not support the video tag.
              </video>
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

          <div v-else-if="questionItem.questionType === '填空题' || questionItem.questionType === '简答题' || questionItem.questionType === '判断题'">
            <div class="question-name">

              <div v-if="questionItem.txbz === '4'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  {{ questionItem.question }}
                </div>
              </div>
              <div v-if="questionItem.txbz === '3'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp;&nbsp; {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
                </div>
              </div>
              <div v-if="questionItem.txbz === '2'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
                </div>

              </div>

              <div v-if="questionItem.txbz === '1'">
                <div v-if="questionItem.showNumber === 1">
                  {{ questionItem.order }}、&nbsp; [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
                </div>
                <div v-else>
                  [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
                </div>

              </div>
            </div>
            <div v-if="questionItem.resourceType === '视频'">
              <video width="320" height="240" controls muted>
                <source :src="resourceUrl" type="video/mp4">
                Your browser does not support the video tag.
              </video>
            </div>
            <!--      填空 简单 判断无需展示选项       -->
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
                  <div class="answer-item"  :class="{ 'blue-background': activeDiv === '正确' }" @click="setActiveDiv('正确')">
                    正确
                  </div>
                </el-col>
                <el-col span="4" class="column-center">
                  <div class="answer-item"  :class="{ 'blue-background': activeDiv === '错误' }" @click="setActiveDiv('错误')" >
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
              <el-row type="flex" justify="center" style="text-align: center">
                <el-input :disabled="(second <= 0 || showResult)" placeholder="请输入答案" type="textarea" rows="3" v-model="activeDiv"></el-input>
              </el-row>
            </div>
          </div>

          <div v-if="questionItem" style="text-align: center; margin-top: 20px">

            <div v-if="submit">
              <el-button disabled style="width: 200px; height: 50px; font-size: x-large" type="primary" @click="submitAnswer"> {{btnText}} </el-button>
            </div>
            <div v-else>
              <el-button :disabled="second <= 0 || showResult || activeDiv === '' || isBuzz" style="width: 200px; height: 50px; font-size: x-large; z-index: 2" type="primary" @click="submitAnswer"> {{btnText}} </el-button>
            </div>
          </div>
        </div>
        <div class="qd-area" :style="qdBoxStyle" @click="startBuzz">
          抢
        </div>
        <div v-if="showResult" id="answer-info" style="font-size: xx-large; margin-top: 20px; background-color: #C40000; border-radius: 8px;">
           <font color="white">{{ prefix }} {{questionItem.answer}}</font>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";
import {buzzQuestion} from "@/api";
export default {
  name: "Model61M",
  components: {ScoreBoard},
  data(){
    return {
      isBuzz: false,
      retryTimer: null, // 用于重试逻辑的计时器
      retryInterval: 500, // 重试间隔时间，例如5秒
      prefix: '',
      subscription: null,
      questionItem: null,
      activeDiv: '',
      teamInfo: '',
      second: -1,
      showResult: false,
      rule: '',
      btnText: "提 交",
      member: '',
      stage: '',
      submit : false,
      resourceUrl: '',
      imageUrl: `${baseUrl}/resource/答题背景.jpg?${new Date().getTime()}`,
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

    qdBoxStyle(){
      return {
        visibility: (this.stage.openQd && this.second >= 0 && this.isBuzz) ? 'visible' : 'hidden',
      }
    }
  },
  
  methods: {
    startBuzz(){
      const data = {
        'team': this.teamInfo.shortName,
        'username': this.member
      }
      buzzQuestion(data).then(() => {
        console.log("抢答成功")
      })
    },

    sortString(input) {
      // 将字符串转换为字符数组
      let charArray = input.split('');
      // 对字符数组进行排序
      charArray.sort();
      // 将排序后的字符数组转换回字符串
      return charArray.join('');
    },
    
    submitAnswer(){
      // 提交答案到后台
      console.log(this.activeDiv)
      if (this.questionItem.questionType === '多选题' || this.questionItem.questionType === '不定项选择题') {
        // 排序答案
        this.activeDiv = this.sortString(this.activeDiv)
      }
      // this.showResult = true;
      const submited = JSON.parse(JSON.stringify(this.questionItem))


      // 队伍名称, 题目分值
      submited.teamName =  this.teamInfo.fullName
      submited.userSelect = this.activeDiv
      submited.userName = sessionStorage.getItem("member")
      submited.second = this.second
      this.questionItem = submited
      this.submit = true
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
        this.activeDiv = this.questionItem.userSelect || ''
      }
      console.log(this.questionItem)
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
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

    subscribeQuestion(){
      this.subscription = this.$websocket.subscribe('/topic/question', (message) => {
        if (message.type === 'rule') {
          console.log(message.data)
          this.rule = message.data
          return
        }
        if (message.type === 'time') {
          this.second = parseInt(message.data)
          this.isBuzz = message.isBuzz == 'true'
          // if (this.second <= 0) {
          //   // if (this.showResult === false) {
          //   //   this.activeDiv = ''
          //   // }
          // }
        } else {
          this.showResult = message.data
          if (this.questionItem.resourceType === '图片') {
            if (this.showResult) {
              this.resourceUrl = this.resourceUrl = `${baseUrl}/resource/${this.questionItem.answer}`
            } else {
              this.resourceUrl = this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
            }
          }
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
    if (this.questionItem.resourceType === '图片') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
    } else if (this.questionItem.resourceType === '视频') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.resource}`
    }
    this.showResult = false
    this.btnText = "提 交"
    this.activeDiv = ""
    this.submit = false
    this.saveData()
    next();
  },
  
  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.prefix = sessionStorage.getItem('prefix')
    this.teamInfo = JSON.parse(item)
    this.member = sessionStorage.getItem('member')
    this.loadData();
    
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    
    if (this.questionItem == null) {
      this.questionItem = data
    } else {
      if (this.questionItem.order !== data.questionNumber) {
        this.questionItem = data
        this.activeDiv = ""
        this.showResult = false
        this.submit = false;
        this.btnText = "提 交"
      }
    }

    if (this.questionItem.resourceType === '图片') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
    } else if (this.questionItem.resourceType === '视频') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.resource}`
    }

    this.setRetryTimer()
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
      sessionStorage.removeItem("submit")
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

/*#container-box {*/
/*  background-image: url("~@/assets/background.png");*/
/*  background-size: 100vw 100vh;*/
/*  height: 100vh;*/
/*}*/

#container {
  padding: 50px 100px;
}

.qd-area {
  width: 100px;            /* 圆的直径 */
  height: 100px;           /* 圆的直径 */
  line-height: 94px;
  background-color: #00b050; /* 绿色背景 */
  color: white;            /* 字体颜色为白色 */
  border-radius: 50%;      /* 实现圆形效果 */
  text-align: center;
  font-size: 50px;         /* 字体大小 */
  font-family: 'Microsoft YaHei', sans-serif; /* 字体 */
  border: 10px solid #e5e0e0;  /* 边框为灰色 */
  margin: 0 auto;
  pointer-events: auto;
  z-index: 10;
}

.question-name {
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
  border-style: outset;
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

.overlay-img {
  all: unset; /* 重置所有样式 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>