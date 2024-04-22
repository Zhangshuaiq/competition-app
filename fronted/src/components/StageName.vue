<template>
  <div class="container">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div class="stageName">
      <el-row>
        <el-col span="24" style="text-align: center">
          <div class="stage-item">
            {{ stage.name }}
          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>
import ScoreBoard from "@/components/ScoreBoard.vue";

export default {
  name: "StageName",
  components: {ScoreBoard},
  data(){
    return {
      stage: {}
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


    const data = this.$store.state.data
    console.log(data)
    this.stage = data
  },
}
</script>

<style scoped>
.container {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
}

.stageName {
  display: flex;           /* 启用Flexbox布局 */
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  height: 70vh;
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