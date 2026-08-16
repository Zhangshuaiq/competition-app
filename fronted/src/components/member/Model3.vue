<template>
  <div class="container" :style="containerBoxStyle">


    <div id="stageName">
      <div>
        {{pstage.name}}
      </div>
      
    </div>

    <div id="questionList">
      <el-row v-for="i in stages.length" :key="i">
        <el-col :span="4">
          <div class="title-item" >
            {{ stages[i - 1].name }}
          </div>
        </el-col>
        <el-col :span="20">
          <el-row>
            <el-col :span="2" v-for="question in questions[i - 1]" :key="question.questionNumber">
              <div class="question-item" :style="{ visibility: question.hasAnswer === true  ? 'hidden' : 'visible' }">
                {{ question.questionNumber }}
              </div>
            </el-col>
          </el-row>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";

export default {
  name: "Model3M",

  data() {
    return {
      imageUrl: `${baseUrl}/resource/风险题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stages: [],
      questions: [],
      pstage: {}
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


  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const data = this.$store.state.data
    console.log(data)
    this.stages = data.stages
    this.questions = data.questions
    this.pstage = data.pstage
  }


}
</script>

<style scoped>

#stageName {
  padding-top: 20px;
}

#stageName div {
  background-color: #C40000;
  text-align: center;
  height: 40px;
  line-height: 40px;
  width: 30%;
  padding: 10px 0;
  margin: 0 35%;
  font-size: xx-large;
  color: white;
  border-radius: 10px;
}

#questionList {
  margin-top: 50px;
  padding: 10px 5%;
}

.title-item {
  width: 120px;
  height: 120px;
  background-color: #C40000;
  line-height: 120px;
  color: white;
  font-size: large;
  text-align: center;
  margin: 10px;
  border-radius: 10px;
}


.question-item {
  width: 50px;
  height: 50px;
  background-color: #C40000;
  line-height: 50px;
  color: white;
  font-size: xx-large;
  text-align: center;
  margin: 10px 10px ;
  border-radius: 10px;
}

</style>