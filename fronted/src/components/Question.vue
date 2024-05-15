<template>
  <div id="container-box">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 20px; font-weight: bold;  " >
      <el-row>
        <el-col span="4"> 组号: {{ teamInfo.shortName }}</el-col>
        <el-col span="4"> 组名: {{ teamInfo.fullName }}</el-col>
        <el-col span="4"> 选手: {{ member }}</el-col>
        <el-col span="4"> 环节: {{ stage.name}}</el-col>
        <el-col span="4">
          <div v-if="second >= 0" style="color: red; ">
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
            <el-image style="width: 100%" :src="resourceUrl" ></el-image>
          </div>
          <!--    音频无需展示      -->
          <!--    视频需要播放      -->
          <!--    文字则正常展示      -->
          <div v-else-if="questionItem.questionType === '单选题' || questionItem.questionType === '多选题' || questionItem.questionType === '不定项选择题'">
            <div class="question-name">
              {{ questionItem.questionNumber }} &nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.resourceType === '视频'">
              <video width="320" height="240" controls muted>
                <source :src="resourceUrl" type="video/mp4">
                Your browser does not support the video tag.
              </video>
            </div>
            <!--     选择性质的题目有选项       -->
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
              {{ questionItem.questionNumber }} &nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.resourceType === '视频'">
              <video width="320" height="240" controls muted>
                <source :src="resourceUrl" type="video/mp4">
                Your browser does not support the video tag.
              </video>
            </div>
            <!--      填空 简单 判断无需展示选项       -->
          </div>

        </div>
        
        <div v-if="showResult && this.questionItem.resourceType !== '图片'" id="answer-info" style="font-size: xx-large; margin-top: 20px; text-align: center;">
          正确答案: <font color="#cd5c5c">{{questionItem.answer}}</font>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";
import baseUrl from "@/config";
export default {
  name: "QuestionL",
  components: {ScoreBoard},

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
      member: '',
      stage: {},
      resourceUrl: '',
    }
  },
  methods: {
    setRetryTimer() {
      // 先清除之前的计时器（如果有）
      clearTimeout(this.retryTimer);
      this.retryTimer = setTimeout(() => {
        // 这里重新调用订阅逻辑
        this.subscribeQuestion();
      }, this.retryInterval);
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
          this.showResult = ! this.showResult
          if (this.questionItem.resourceType === '图片') {
            if (this.showResult) {
              this.resourceUrl = this.resourceUrl = `${baseUrl}/resource/${this.questionItem.answer}`
            } else {
              this.resourceUrl = this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
            }
          }
          this.second = -1
        }

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
    console.log(this.resourceUrl)
    this.showResult = false
    next();
  },

  mounted() {
    const item = sessionStorage.getItem("user");
    this.member = sessionStorage.getItem('member')
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.teamInfo = JSON.parse(item)
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    this.questionItem = data
    if (this.questionItem.resourceType === '图片') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
    } else if (this.questionItem.resourceType === '视频') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.resource}`
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
  padding:0 100px;
}

.question-name {
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