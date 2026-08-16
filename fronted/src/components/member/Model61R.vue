

<template>
  <div id="container"  :style="containerBoxStyle">
    <div>
<!--      <ScoreBoard></ScoreBoard>-->
    </div>
    <div id="result-box">
      <div id="correct-answer">
        {{ prefix }} {{resultData.correctAnswer}}
      </div>
      <div class="member-answer" style="margin-top: 20px">
        <el-row >
          <el-col :span="12" v-for="(item, index) in resultData.dataItemList" :key="index">
            <div style="margin: 10px 0">
              <el-row justify="right">
                <el-col :span="20" :offset="index % 2 ==0?0:2">
                  <div v-if="item.member" class="answer-info">
                    <el-row>
                      <el-col :span="6">
                        <div style="width: 100%; /* 根据需要调整宽度 */
                            height: 60px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                             /* 确保padding包含在宽度内 */">
                          {{item.teamName}}
                        </div>
                      </el-col>
                      <el-col :span="6">{{item.member}}</el-col>
                      <el-col :span="12"> 回答 ：&nbsp;{{item.answer}}</el-col>
                    </el-row>
                      
                  </div>
                  <div v-else class="answer-info">
                    <el-col :span="6">
                      <div style="width: 100%; /* 根据需要调整宽度 */
                            height: 60px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                             /* 确保padding包含在宽度内 */">
                        {{item.teamName}}
                      </div>
                    </el-col>
                    <el-col :span="18"> 回答 ：&nbsp;{{item.answer}}</el-col>
                  </div>
                </el-col>
                <el-col :span="2">
                  <div class="score-info" v-if="item.answer ===resultData.correctAnswer" style="background-color: green; width: 100%">+{{item.score}}</div>
                  <div class="score-info" style="width: 100%" v-else>{{item.score}}</div>
                </el-col>
              </el-row>
            </div>

          </el-col>

        </el-row>
      </div>
    </div>
  </div>
</template>

<script>

  // import ScoreBoard from "@/components/ScoreBoard.vue";
  import baseUrl from "@/config";

  export default {
    name: "Model61R",
    components: {},

    data(){
      return {
        prefix: '',
        resultData: {},
        imageUrl: `${baseUrl}/resource/答题背景.jpg?${new Date().getTime()}`,
        defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      }
    },

    methods:{
      checkImageUrl(url, callback) {
        const img = new Image();
        img.onload = () => callback(true);
        img.onerror = () => callback(false);
        img.src = url;
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
    },

    beforeRouteUpdate(to, from, next) {
      const data = this.$store.state.data
      this.resultData = data
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
      const data = this.$store.state.data
      this.resultData = data
      console.log(this.resultData)
    }
  }

</script>

<style scoped>

#container {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
}

.member-answer {
  height: 50px;
  line-height: 50px;
}


#result-box {
  padding: 30px 10%;
}

#correct-answer {
  background-color: #C40000;
  color: white;
  text-align: center;
  height: 80px;
  line-height: 80px;
  font-size: x-large;
  border-radius: 8px;
}


.answer-info{
  background-color: #3397CE;
  color: white;
  height: 60px;
  line-height: 60px;
  padding-left: 10px;
  font-weight: bold;
  border-radius: 8px;
}

.score-info {
  background-color: red;
  color: white;
  height: 60px;
  width: 60px;
  line-height: 60px;
  text-align: center;
  border-radius: 8px;
  font-weight: bold;
}

</style>