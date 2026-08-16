<template>
  <div class="container" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div class="stageList">
      <el-row>
        <el-col :span="12" style="text-align: center" v-for="item in stages" :key="item.id">
          <div class="stage-item">
            {{ item.name }}
          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "StageList",
  components: {ScoreBoard},
  data(){
    return {
      imageUrl: `${baseUrl}/resource/环节背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stages: []  
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
    
    currentRoute() {
      return this.$store.state.currentRoute;
    },
    additionalData() {
      return this.$store.state.data;
    }
  },
  
  mounted() {
    const login =  sessionStorage.getItem('login')
    if (!login || login === 'false') {
      this.$router.push({name: "hwait"})
      return
    }
    // 查找列表
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const data = this.$store.state.data
    console.log(data)
    this.stages = data
  },
}
</script>

<style scoped>
.stageList {
  margin-top: 3vh;
  margin-left: 15vw;
  margin-right: 15vw;
  /*display: flex;           !* 启用Flexbox布局 *!*/
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
}

.stage-item {
  margin: 25px 15%;
  width: 70%;
  height: 60px;
  line-height: 60px;
  background-color: #C40000;
  border-radius: 10px;
  font-family: "微软雅黑";
  font-size: 24px;
  color: white;
}
</style>