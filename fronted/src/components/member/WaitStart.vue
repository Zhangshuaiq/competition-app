<template>
  <div id="container-box" :style="containerBoxStyle">
    <div style="height: 110px; line-height: 110px">
      <el-row>
        <el-col :span="12">
          <img :src="iconUrl" style="margin: 10px 0 0 10px">
        </el-col>
        <el-col :span="12">
          <div style="text-align: right; line-height: 100px; height: 100px; margin-right: 20px; font-size: x-large ">
            {{team.shortName}} | {{member}}
          </div>

        </el-col>
      </el-row>
    </div>
    
    
    <div>
    </div>
    <div class="welcome-text" style="margin-top: 15vh" >
      欢迎使用答赛答题竞赛系统
    </div>

    <div class="welcome-text" style="margin-top: 10vh">
      等待开始
    </div>
    
    <div style="margin: 20vh 5% 0 5%; text-align: center; height: 40px; line-height: 40px; width: 90%; background-color: white">
      {{contactText}}
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";
import {answerPrefix, queryContact} from "@/api";
export default {
  name: "WaitStart",
  data() {
    return {
      contactText: '',
      imageUrl: `${baseUrl}/resource/等待答题背景.jpg?${new Date().getTime()}`,
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
  },
  
  methods:{

    validPassword(){
      if (this.teamItem.password === this.form.password) {
        this.enterWaitPage(this.teamItem, this.member);
      } else {
        this.$message.error("登陆密码错误")
      }
    },
    
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
    },

    contactInfo(){
      queryContact().then(res => {
        this.contactText = res.data
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
    this.member = member
    sessionStorage.clear()
    sessionStorage.setItem("member", member)
    sessionStorage.setItem("user", item)
    this.queryPrefix()
    this.contactInfo()
    const teamInfo = JSON.parse(item)
    this.team = teamInfo
    this.$websocket.send('/app/enter', {team: teamInfo.shortName + "-" + sessionStorage.getItem("member")})
  },
}
</script>


<style scoped>
.welcome-text {
  text-align: center;
  font-size: 64px;
  color: white; /* 填充色 */
  -webkit-text-stroke-width: 2px; /* 边框宽度 */
  -webkit-text-stroke-color: #ED7D31; /* 边框颜色 */
}
</style>