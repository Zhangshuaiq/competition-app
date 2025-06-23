<template>
  <div id="container-box" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 20px; font-weight: bold;" >
      <el-row>
        <el-col span="4"> 组号: {{ teamInfo.shortName }}</el-col>
        <el-col span="6"> 组名: {{ teamInfo.fullName }}</el-col>
        <el-col span="6"> 选手: {{ member }}</el-col>
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
            <div v-if="isResourceAvailable && showResult">
              <img :src="answerUrl" alt="Overlay Image" class="overlay-img">
            </div>
            <div v-else>
              <img :src="resourceUrl" alt="Overlay Image" class="overlay-img">
            </div>
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
          <div v-else>
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

        </div>
        <div class="qd-area" :style="qdBoxStyle" @click="startBuzz">
          抢
        </div>

        <div v-if="showResult && !isResourceAvailable" id="answer-info"
             style="font-size: xx-large; margin-top: 20px;">
          <div v-if="questionItem.questionType === '简答题'" style="text-align: left; background-color: #C40000; border-radius: 8px;">
            <font color="white">{{ prefix }} {{questionItem.answer}}</font>
          </div>
          <div v-else style="text-align: center; background-color: #C40000; border-radius: 8px;">
            <font color="white">{{ prefix }} {{questionItem.answer}}</font>
          </div>
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
  name: "QuestionL",
  components: {ScoreBoard},

  data(){
    return {
      hasBuzzed: false,
      buzzStatus: false,
      isResourceAvailable: false,
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
      answerUrl: '',
      imageUrl: `${baseUrl}/resource/答题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      prefix: ''
    }
  },

  watch: {
    answerUrl(newUrl) {
      if (!newUrl) {
        this.isResourceAvailable = false;
        return
      }
      this.checkImageUrl(newUrl, (isValid) => {
        if (isValid) {
          this.isResourceAvailable = true;
        } else {
          this.isResourceAvailable = false;
        }
      });
    },

    '$store.state.buzzStatus': function (newStatus) {
      this.buzzStatus = newStatus
      if (newStatus === true) {
        sessionStorage.setItem('hasBuzzed', this.hasBuzzed)
      } else {
        this.hasBuzzed = false;
        sessionStorage.setItem('hasBuzzed', this.hasBuzzed)
      }
    },
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
        visibility: (this.stage.openQd === 1 && !this.hasBuzzed) ? 'visible' : 'hidden',
      }
    }
  },
  
  methods: {

    startBuzz(){
      const data = {
        'team': this.teamInfo.shortName,
        'username': this.member
      }
      buzzQuestion(data).then((res) => {
        if (res.data === 'true') {
          this.hasBuzzed = true;
          console.log("抢答成功")
        } else {
          this.$message.error("抢答尚未开始")
          this.hasBuzzed = false;
        }
        sessionStorage.setItem('hasBuzzed', this.hasBuzzed)

      })
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    
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
          this.showResult = message.data
          if (this.questionItem.resourceType === '图片') {
            if (this.showResult) {
              this.answerUrl  = `${baseUrl}/resource/${this.questionItem.answer}`
            } else {
              this.resourceUrl  = `${baseUrl}/resource/${this.questionItem.question}`
            }
          }
          this.second = -1
        }

      })

    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    this.answerUrl  = ''
    this.isResourceAvailable = false;
    console.log("yyyyyyyyyyyyyyy")
    console.log(data)
    this.questionItem = data
    if (this.questionItem.resourceType === '图片') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.question}`
    } else if (this.questionItem.resourceType === '视频') {
      this.resourceUrl = `${baseUrl}/resource/${this.questionItem.resource}`
    }
    this.showResult = false
    sessionStorage.removeItem('hasBuzzed')
    this.hasBuzzed = false;
    next();
  },

  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    this.prefix = sessionStorage.getItem('prefix')
    const item = sessionStorage.getItem("user");
    this.member = sessionStorage.getItem('member')
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    this.hasBuzzed = sessionStorage.getItem('hasBuzzed') === 'true'
    this.buzzStatus = sessionStorage.getItem('buzzStatus') === 'true'
    this.teamInfo = JSON.parse(item)
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    console.log("xxxxxxxxxxxxxxxxxx")
    console.log(data)
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
#container {
  padding:0 100px;
}

.qd-area {
  width: 100px;            /* 圆的直径 */
  height: 100px;           /* 圆的直径 */
  line-height: 94px;
  background-color: #00b050; /* 蓝色背景 */
  color: white;            /* 字体颜色为白色 */
  border-radius: 50%;      /* 实现圆形效果 */
  text-align: center;
  font-size: 50px;         /* 字体大小 */
  font-family: 'Microsoft YaHei', sans-serif; /* 字体 */
  border: 10px solid #e5e0e0;  /* 边框为黑色 */
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

.overlay-img {
  all: unset; /* 重置所有样式 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 100;
  pointer-events: none;
}
</style>