<template>
  <div class="scoreboard">
    <el-row>
      <el-col v-for="(item, index) in teamList" :key="item.id" span="2">
        <div>
          <el-row>
            <el-col span="6">
              <div class="round-board-item" style="background-color: #3397CE">
                {{index + 1}}
              </div>
            </el-col>
            <el-col span="16">
              <div class="board-item" style="background-color: palevioletred">
                {{item.currentScore}}
              </div>
            </el-col>
          </el-row>
          <div v-if="showStatus">
            <div v-if="showMember" >
              <el-row>
                <el-col span="6" v-for="(st, index) in item.statusList" :key="index">
                  <div class="status-item" :style="{backgroundColor: statusColor[st]}">
                    {{index + 1}}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div v-else class="board-item" :style="{backgroundColor: statusColor[item.status]}">
              <div v-if="item.status === 2 || item.status === 3">
                {{questionItem.userSelect}}
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
 export default {
   name: "ScoreBoard",
   data() {
     return  {
       teamList: [],
       showStatus: false,
       showMember: false,
       statusColor: ['black', 'grey', 'green', 'red'],
       questionItem: sessionStorage.getItem('questionItem') || {}
     }
   },

   beforeRouteUpdate(to, from, next) {
     const data = this.$store.state.teams
     this.teamList = data
     next();
   },

   watch: {
     '$store.state.teams': function (newStatus) {
       this.teamList = newStatus
       this.questionItem = JSON.parse(sessionStorage.getItem('questionItem'))
       console.log("1572910872933333")
       console.log(this.questionItem)
     },

     questionItem(newVal, oldVal) {
       // 当 mySessionData 变化时执行操作
       console.log(oldVal)
       console.log('sessionStorage myKey changed:', newVal);
     }
   },

   mounted() {
     const data = this.$store.state.teams
     console.log(data)
     this.teamList = data
     if (this.$store.state.currentRoute === 'model61' || this.$store.state.currentRoute === 'model61R') {
       this.showStatus = true
       this.showMember = true
     } else if (this.$store.state.currentRoute === 'model60') {
       this.showStatus = true
       this.showMember = false
     }
   }
 }

</script>

<style scoped>

.status-item {
  width: 20px;
  height: 40px;
  border-radius: 8px;
  line-height: 40px;
  text-align: center;
  font-size: larger;
  color: white;
}

.round-board-item {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  line-height: 40px;
  text-align: center;
  font-size: larger;
  color: white;
}

.board-item {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  line-height: 40px;
  text-align: center;
  font-size: larger;
  color: white;
}
</style>