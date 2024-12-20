<template>
  <div class="container" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
<!--    <div id="stageName">-->
<!--      <div>-->
<!--        {{stage.name}}-->
<!--      </div>-->
<!--    </div>-->

    <div id="question">
        {{ questionNumber }}
    </div>

  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "RollPage",

  components: {ScoreBoard},

  data(){
    return{
      imageUrl: `${baseUrl}/resource/滚动抽题背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stage: {},
      questionNumber: ''
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

  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.data
    console.log(data)
    this.stage = this.$store.state.stage
    this.questionNumber = data
    next();
  },
  
  methods:{
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
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
    this.stage = this.$store.state.stage
  }

}
</script>

<style scoped>


#stageName {
  display: flex; /* 启用Flexbox布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

#stageName div {
  width: 500px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border-radius: 8px;
  color: red;
  background-color: #3397CE;
  margin-top: 20px;
  font-size: xx-large;
}

#question {
  text-align: center;
  height: 150px;
  width: 150px;
  margin-left: calc(50vw - 75px);
  margin-top: 35vh;
  line-height: 150px;
  color: white;
  background-color: #C40000;
  border-radius: 75px;
  font-size: 100px
}
</style>