<!--  选手页面 -->
<template>
  <div id="container-box">
    <div id="container">
      <el-row>
        <el-col v-for="(item, index) in teamList"
                :key="index"
                span=6
        >
          <div style="height: 160px">
            <div class="haotai" >
              {{item.shortName}}
            </div>
            <div style="display: flex; justify-content: center; height: 60px; line-height: 60px">
              ` <div v-if="!item.members"></div>
                <div v-else v-for="(member, mIndex) in item.members.split(',')" @click="enterWaitPage(item, member)"
                     :key="mIndex" class="member">
                  {{member}}
                </div>
            </div>
          </div>

        </el-col>
      </el-row>
    </div>
  </div>
  
</template>

<script>
import {queryTeams} from "@/api";

export default {
  name: "ContestantIndex",
  
  data() {
    return {
      teamList: []
    }
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

    enterWaitPage(teamItem, member){
      sessionStorage.setItem("user", JSON.stringify(teamItem))
      sessionStorage.setItem("member", member)
      console.log(teamItem)
      this.$router.push({name: "wait"});
    }
  },
  
  mounted() {
    sessionStorage.clear()
    this.queryTeamList()
  }


}
</script>

<style scoped>

#container-box {
  background-image: url("~@/assets/background.png");
  background-size: 100vw 100vh;
  height: 100vh;
}

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
  font-size: xx-large;
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