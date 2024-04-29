<template>
  <div class="container">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div class="stageList">
      <el-row>
        <el-col span="12" style="text-align: center" v-for="item in stages" :key="item.id">
          <div class="stage-item">
            {{ item.name }}
          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";

export default {
  name: "StageList",
  components: {ScoreBoard},
  data(){
    return {
      stages: []  
    }
  },

  computed: {
    currentRoute() {
      return this.$store.state.currentRoute;
    },
    additionalData() {
      return this.$store.state.data;
    }
  },
  
  mounted() {
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    const member = sessionStorage.getItem("member");

    sessionStorage.clear()
    sessionStorage.setItem("member", member)
    sessionStorage.setItem("user", item)

    // 查找列表
    
    const data = this.$store.state.data
    console.log(data)
    this.stages = data
  },
}
</script>

<style scoped>
 .container {
   background-image: url("~@/assets/background.png");
   background-size: 100vw 100vh;
   height: 100vh;
 }

 .stageList {
   margin-top: 20vh;
   display: flex;           /* 启用Flexbox布局 */
   justify-content: center; /* 水平居中 */
   align-items: center;     /* 垂直居中 */
 }
 
 .stage-item {
   margin: 10px auto;       /* 上下边距为0，左右边距自动居中 */
   width: 200px;
   height: 60px;
   line-height: 60px;
   background-color: #3397CE;
   border-radius: 10px;
   font-size: 20px;
   color: white;
 }
</style>