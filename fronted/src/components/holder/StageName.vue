<template>
  <div class="container"  :style="containerBoxStyle">
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
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";

export default {
  name: "StageName",
  components: {ScoreBoard},
  data(){
    return {
      imageUrl: `${baseUrl}/resource/环节名称背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stage: {}
    }
  },

  watch: {
    additionalData: {
      handler(newData, oldData) {
        console.log('Stage data changed:', newData);
        if (newData) {
          this.stage = newData;
          this.onStageChange(newData, oldData);
        }
      },
      deep: true, // 深度监听对象属性变化
      immediate: true // 立即执行一次（组件创建时就执行）
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
    const data = this.$store.state.data
    console.log(data)
    this.stage = data
  },
}
</script>

<style scoped>

.stageName {
  display: flex;           /* 启用Flexbox布局 */
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  height: 50vh;
}

.stage-item {
  margin: 10px auto;       /* 上下边距为0，左右边距自动居中 */
  width: 500px;
  height: max-content;
  line-height: 100px;
  background-color: #C40000;
  border-radius: 10px;
  padding: 0 20px;
  font-size: 50px;
  color: white;
  overflow-wrap: break-word;
  word-break: break-all;
}
</style>