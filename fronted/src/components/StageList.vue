<template>
  <div class="container" :style="containerBoxStyle">
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
import baseUrl from "@/config";
import {answerPrefix} from "@/api";

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
    },

  },
  
  methods:{

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    
    queryPrefix() {
      answerPrefix().then(res => {
        sessionStorage.setItem("prefix", res.data)
      })
    }
  },
  
  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }
    const member = sessionStorage.getItem("member");

    sessionStorage.clear()
    this.queryPrefix()
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

 .stageList {
   margin-top: 20vh;
   margin-left: 15vw;
   margin-right: 15vw;
   display: flex;           /* 启用Flexbox布局 */
   justify-content: center; /* 水平居中 */
   align-items: center;     /* 垂直居中 */
 }
 
 .stage-item {
   margin: 10px auto;       /* 上下边距为0，左右边距自动居中 */
   width: 250px;
   height: 60px;
   line-height: 60px;
   background-color: #C40000;
   border-radius: 10px;
   font-family: "微软雅黑";
   font-size: 24px;
   color: white;
 }
</style>