<!--  选手页面 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div id="container">
      <el-row>
        <el-col v-for="(item, index) in teamList"
                :key="index"
                span=6
        >
          <div style="height: 80px">
            <el-row>
              <el-col span="6">
                <div class="haotai" style="width: 100%;" >
                  {{item.code}}
                </div>
              </el-col>
              <el-col span="18">
                <div class="haotai" @click="selectMember(item)">
                  {{item.shortName}}
                </div>
              </el-col>
            </el-row>
          </div>

        </el-col>
      </el-row>
    </div>


    <el-dialog title="登陆验证" :visible.sync="dialogFormVisible">
      <el-form :model="form" @submit.native.prevent>
          <el-form-item label="请输入选手姓名" label-width="120">
            <el-input v-model="form.member" autocomplete="off" @keyup.enter.native="validPassword"></el-input>
          </el-form-item>
          <el-form-item label="请输入密码" label-width="120">
            <el-input v-model="form.password" autocomplete="off" @keyup.enter.native="validPassword"></el-input>
           </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="validPassword" >确 定</el-button>
      </div>
    </el-dialog>
  </div>
  
</template>

<script>
import {answerPrefix, queryTeams, queryStatus} from "@/api";
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
    queryTeamList(){
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

    validPassword(){
      if (!this.form.member || !this.form.password) {
        this.$message.error("请完整填写登录信息")
        return
      }
      if (this.teamItem.members.indexOf(this.form.member) < 0) {
        this.$message.error("当前队伍没有对应的选手姓名")
        return
      }
      if (this.teamItem.password === this.form.password) {
        this.member = this.form.member
        // 查询是否应该进入竞技模式
        queryStatus(this.teamItem.code).then(res => {
          if (res.data) {
            sessionStorage.setItem("lastStatus", 'true')
            sessionStorage.setItem("user", JSON.stringify(this.teamItem))
            sessionStorage.setItem("member", this.member)
            this.$router.push({name: "model7"});
          } else {
            this.enterWaitPage(this.teamItem, this.member);
          }
        })
      } else {
        this.$message.error("登陆密码错误")
      }
    },

    selectMember(teamItem){
      this.teamItem = teamItem;
      this.dialogFormVisible = true
      this.form = {
        member: '',
        password: ''
      }
    },

    enterWaitPage(teamItem, member){
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
  padding: 100px;
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

.member:hover {
  background-color: #000000; /* 鼠标悬停时的背景颜色 */
  color: #ffffff;
}
</style>