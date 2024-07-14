

<template>
  <div id="container" :style="containerBoxStyle">
    <div id="result-box">
      <div id="correct-answer">排行榜</div>
      <div class="member-answer" style="margin-top: 20px">
        <el-row>
          <el-row>
            <el-col span="6">
              <div class="answer-info">排名</div>
            </el-col>
            <el-col span="6">
              <div class="answer-info">团队</div>
            </el-col>
            <el-col span="6">
              <div class="answer-info">分数</div>
            </el-col>
            <el-col span="6">
              <div class="answer-info">时间</div>
            </el-col>
          </el-row>
          <el-col span="24" v-for="(item, index) in resultData.rank" :key="index">
            <div style="border-color: black; border-style: solid; border-width: 1px">
              <el-row>
                <el-col span="6">
                  <div class="score-info">{{item.rank}}</div>
                </el-col>
                <el-col span="6">
                  <div class="score-info"
                       style="width: 100%; /* 根据需要调整宽度 */
                            height: 60px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;">{{item.team}}</div>
                </el-col>
                <el-col span="6">
                  <div class="score-info">{{item.score}}</div>
                </el-col>
                <el-col span="6">
                  <div class="score-info">{{item.time}}</div>
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

import baseUrl from "@/config";

export default {
  name: "StageRank",

  data(){
    return {
      resultData: {},
      imageUrl: `${baseUrl}/resource/环节排名背景.jpg?${new Date().getTime()}`,
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
  },
  
  methods:{
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
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
    const data = this.$store.state.data
    this.resultData = data
    console.log(this.resultData)
  }
}

</script>

<style scoped>


.member-answer {
  height: 50px;
  line-height: 50px;
}


#result-box {
  padding: 30px 20%;
}

#correct-answer {
  color: black;
  text-align: center;
  height: 100px;
  line-height: 100px;
  font-size: xx-large;
  font-weight: bolder;
}


.answer-info{
  font-size: x-large;
  text-align: center;
  background-color: black;
  color: white;
  height: 50px;
  font-weight: bold;
  line-height: 50px;
}

.score-info {
  font-size: x-large;
  text-align: center;
  color: white;
  height: 50px;
  color: black;
  line-height: 50px;
}

</style>