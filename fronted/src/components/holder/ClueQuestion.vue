<template>
  <div id="container-box" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 20px; font-weight: bold;" >
      <el-row>
<!--        <el-col span="4"> 组号: {{ teamInfo.shortName }}</el-col>-->
<!--        <el-col span="4"> 组名: {{ teamInfo.fullName }}</el-col>-->
<!--        <el-col span="4"> 选手: {{ member }}</el-col>-->
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
          <div id="question-name">
            <div v-if="questionItem.txbz === '4'">
              {{ questionItem.order }} &nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.txbz === '3'">
              {{ questionItem.order }} &nbsp;&nbsp; {{questionItem.questionType}} &nbsp;&nbsp; {{ questionItem.question }}
            </div>
            <div v-if="questionItem.txbz === '2'">
              {{ questionItem.order }} &nbsp; ({{questionItem.questionType}}) &nbsp; {{ questionItem.question }}
            </div>

            <div v-if="questionItem.txbz === '1'">
              {{ questionItem.order }} &nbsp; [{{questionItem.questionType}}] &nbsp; {{ questionItem.question }}
            </div>
          </div>
          <div id="answer-selection">
            <div class="selection-item"  v-if="questionItem.choiceA">
               {{questionItem.choiceA}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceB">
               {{questionItem.choiceB}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceC">
               {{questionItem.choiceC}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceD">
               {{questionItem.choiceD}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceE">
               {{questionItem.choiceE}}
            </div>

            <div class="selection-item"  v-if="questionItem.choiceF">
               {{questionItem.choiceF}}
            </div>
          </div>
        </div>
        
        <div  id="answer-info" style="font-size: xx-large; margin-top: 20px; text-align: center; background-color: #C40000; border-radius: 8px;">
           <font color="white">{{ prefix }} {{questionItem.answer}}</font>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "QuestionL",
  components: {ScoreBoard},

  data(){
    return {
      
      retryTimer: null, // 用于重试逻辑的计时器
      retryInterval: 500, // 重试间隔时间，例如5秒
      imageUrl: `${baseUrl}/resource/答题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      subscription: null,
      questionItem: null,
      second: -1,
      showResult: true,
      showWait: true,
      rule: '',
      stage: {},
      prefix: ''
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
  },
  
  methods: {
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
        } 

      })

    },
  },

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    this.questionItem = data
    next();
  },

  mounted() {

    const login =  sessionStorage.getItem('login')
    if (!login || login === 'false') {
      this.$router.push({name: "hwait"})
      return
    }
    
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    this.prefix = sessionStorage.getItem('prefix')
    const data = this.$store.state.data
    this.stage = this.$store.state.stage
    this.questionItem = data
    
    this.setRetryTimer()
  },
}
</script>

<style scoped>

#container {
  padding:0 100px;
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