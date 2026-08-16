<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <div id="login-panel">
        <el-row>
          <div style="height: 50px; display: flex; align-items: center; width: 100%">
            <el-col :span="16">
              <el-input  class="custom-input" style="height: 50px" v-model="form.password" placeholder="请输入密码" autocomplete="off"
                         @keyup.enter="validPassword"></el-input>
            </el-col>
            <el-col :span="8" align="right">
              <el-button style="color: #00b050; font-size: 20px; height: 50px" @click="validPassword"> 提交 </el-button>
            </el-col>
          </div>
        </el-row>
      </div>
    </div>
  </div>
  
</template>

<script>
import {queryTeams} from "@/api";
import baseUrl from "@/config";

export default {
  name: "ContestantIndex",
  
  data() {
    return {
      form: {
        member: '',
        password: ''
      },
      teamList: [],
      dialogFormVisible: false,
      teamItem: '',
      member: '',
      imageUrl: `${baseUrl}/resource/登录背景.jpg?${new Date().getTime()}`,
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
  
  methods: {
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },
    queryTeamList(){
      queryTeams().then(res => {
        console.log(res)
        if (res.code === 200) {
          this.teamList = res.data
        }
      })
    },


    validPassword() {
      if (!this.form.password) {
        this.$message.error("请输入密码")
        return
      }
      this.teamItem = ''
      for (let i = 0; i < this.teamList.length; i++) {
        const item = this.teamList[i]
        if (item.password === this.form.password) {
          this.teamItem = item
          break
        }
      }

      if (this.teamItem === '') {
        this.$message.error("密码错误")
        return;
      }
      this.enterWaitPage(this.teamItem);
    },


    selectMember(teamItem){
      this.teamItem = teamItem;
      this.dialogFormVisible = true
      this.form = {
        member: '',
        password: ''
      }
    },

    enterWaitPage(teamItem){
      sessionStorage.setItem("team", JSON.stringify(teamItem))
      this.$router.push({name: "teamScore"});
    }
  },
  
  mounted() {
    sessionStorage.setItem('role', 'score')
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
    sessionStorage.clear()
    sessionStorage.setItem('role', 'score')
    this.queryTeamList()
  }


}
</script>

<style scoped>

#container {
  height: 100%;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

#login-panel {
  width: 40%;
}

.haotai {
  margin-top: 40px;
  margin: 0 30px;
  text-align: center;
  color: red;
  height: 60px;
  line-height: 60px;
  font-size: x-large;
  border-width: 1px;
  border-color: black;
  background-color: white;
  border-radius: 20px;
}

.haotai1 {
  margin-top: 40px;
  margin: 0 30px;
  text-align: center;
  color: red;
  height: 60px;
  line-height: 60px;
  font-size: x-large;
  border-width: 1px;
  border-color: black;
  background-color: white;
  border-radius: 20px;
}

.haotai:hover {
  background-color: #000000; /* 鼠标悬停时的背景颜色 */
  color: #ffffff;
}

.member {
  border-color: black;
  margin: 20px 10px;
  width: 50px;
  padding: 0 10px;
  color: white;
  line-height: 60px;
  background-color: #ff9900;
  text-align: center;
  height: 60px;
}

.custom-input :deep(.el-input__wrapper) {
  height: 50px; /* Element Plus 输入框外壳控制高度 */
}
.custom-input :deep(.el-input__inner) {
  height: 50px; /* 自定义高度 */
  line-height: 50px; /* 调整文字垂直居中 */
  font-size: 20px;
}

.member:hover {
  background-color: #000000; /* 鼠标悬停时的背景颜色 */
  color: #ffffff;
}
</style>