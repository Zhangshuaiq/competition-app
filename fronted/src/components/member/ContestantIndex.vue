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
              <el-button style="color: #ff0000; font-size: 20px; height: 50px" @click="validPassword"> 提交 </el-button>
            </el-col>
          </div>

        </el-row>
        <br>
        <div id="member-pane" :style="{ visibility: isVisible ? 'visible' : 'hidden' }">
          <div v-if="isVisible">
            <el-row :gutter="20">
              <el-col v-for="(member, index) in memberList" :key="index" :span="8">
                <div class="name-tag" @click="doLogin(member)">{{ member }}</div>
              </el-col>
            </el-row>
          </div>
        </div>

      </div>
    </div>
  </div>

</template>

<script>
import {answerPrefix, queryTeams} from "@/api";
import baseUrl from "@/config";

export default {
  name: "ContestantIndex",

  data() {
    return {
      form: {
        password: ''
      },
      isVisible: false,
      teamList: [],
      dialogFormVisible: false,
      teamItem: '',
      member: '',
      imageUrl: `${baseUrl}/resource/登录背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
    }
  },

  computed: {
    memberList() {
      const value = this.teamItem && this.teamItem.members
      if (!value) {
        return []
      }
      const trimmed = value.trim()
      if (trimmed.startsWith('[') && trimmed.endsWith(']')) {
        try {
          const members = JSON.parse(trimmed)
          if (Array.isArray(members)) {
            return members
          }
        } catch (e) {
          // Keep compatibility with legacy text that only looks like JSON.
        }
      }
      return value.split(',')
    },
    containerBoxStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize: '100vw 100vh',
        height: '100vh',
      };
    },
  },

  methods: {
    queryTeamList() {
      queryTeams().then(res => {
        console.log(res)
        if (res.code === 200) {
          this.teamList = res.data
        }
      })
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
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
        this.isVisible = false
        return;
      }
      this.isVisible = true
    },

    doLogin(member) {
      this.member = member
      // 查询是否应该进入竞技模式
      this.enterWaitPage(this.teamItem, this.member);
      // queryStatus(this.teamItem.code).then(res => {
      //   if (res.data) {
      //     sessionStorage.setItem("lastStatus", 'true')
      //     sessionStorage.setItem("user", JSON.stringify(this.teamItem))
      //     sessionStorage.setItem("member", this.member)
      //     this.$websocket.send('/app/enter', {team: this.teamItem.fullName + "-" + this.member})
      //     this.$router.push({name: "model7"});
      //   } else {
      //     this.enterWaitPage(this.teamItem, this.member);
      //   }
      // })
    },

    selectMember(teamItem) {
      this.teamItem = teamItem;
      this.dialogFormVisible = true
      this.form = {
        member: '',
        password: ''
      }
    },

    enterWaitPage(teamItem, member) {
      sessionStorage.setItem("user", JSON.stringify(teamItem))
      sessionStorage.setItem("member", member)
      console.log(teamItem)
      this.$router.push({name: "wait"});
    },

    queryPrefix() {
      answerPrefix().then(res => {
        sessionStorage.setItem("prefix", res.data)
      })
    }
  },

  mounted() {
    sessionStorage.clear()
    this.queryTeamList()
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        console.log("invalid....................")
        this.imageUrl = this.defaultImageUrl;
      }
    });
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

.name-tag {
  border: black solid 1px;
  text-align: center;
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  margin: 5px 0;
}

#login-panel {
  width: 40%;
}

#member-pane{
  height: 200px;
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
