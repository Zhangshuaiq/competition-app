<template>
  <div id="container-box" :style="containerBoxStyle">
    <div style="height: 110px; line-height: 110px">
      <el-row>
        <el-col span="12">
          <img :src="iconUrl" style="margin: 10px 0 0 10px">
        </el-col>
        <el-col span="12">
          <div style="text-align: right; line-height: 100px; height: 100px; margin-right: 20px; font-size: x-large ">
            {{team.shortName}}
          </div>

        </el-col>
      </el-row>
    </div>


    <div>
    </div>
    <div class="qd-area" :style="qdBoxStyle" @click="startBuzz">
      抢
    </div>

  </div>
</template>

<script>
import baseUrl from "@/config";
import {buzzQuestion} from "@/api";
export default {
  name: "WaitStart",
  data() {
    return {
      contactText: '',
      imageUrl: `${baseUrl}/resource/抢答背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      iconUrl: `${baseUrl}/resource/图标.jpg?${new Date().getTime()}`,
      team: '',
      member: ''
    };
  },
  computed: {
    containerBoxStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize: '100vw 100vh',
        height: '100vh',
      };
    },

    qdBoxStyle(){
      return {
        visibility: 'visible'
      }
    }
  },

  methods:{
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },

    startBuzz(){
      const data = {
        'team': this.team.shortName,
        'username': ''
      }
      buzzQuestion(data).then((res) => {
        if (res.data === 'true') {
          this.hasBuzzed = true;
          console.log("抢答成功")
        } else {
          this.$message.error("抢答尚未开始")
          this.hasBuzzed = false;
        }
        sessionStorage.setItem('hasBuzzed', this.hasBuzzed)

      })
    },
  },

  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });

    const login =  sessionStorage.getItem('team')
    if (!login || login === '') {
      this.$router.push({name: "scoreLogin"})
      return
    }
    this.team = JSON.parse(login)
  },
}
</script>

<style scoped>
.qd-area {
  width: 200px;            /* Circle diameter */
  height: 200px;           /* Circle diameter */
  display: flex;           /* Use Flexbox for centering */
  justify-content: center; /* Center horizontally */
  align-items: center;     /* Center vertically */
  background-color: #00b050; /* Green background */
  color: white;            /* White text color */
  border-radius: 50%;      /* Circular shape */
  text-align: center;      /* Center text */
  font-size: 80px;         /* Font size */
  font-family: 'Microsoft YaHei', sans-serif; /* Font */
  border: 10px solid #e5e0e0;  /* Border color */
  margin: 0 auto;          /* Center horizontally */
  position: absolute;      /* Positioning */
  top: 50%;                /* Center vertically */
  left: 50%;               /* Center horizontally */
  transform: translate(-50%, -50%); /* Adjust for true center */
  pointer-events: auto;
  z-index: 10;
}
</style>