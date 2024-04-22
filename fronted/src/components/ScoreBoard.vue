<template>
  <div class="scoreboard">
    <el-row>
      <el-col v-for="item in teamList" :key="item.id" span="2">
        <div>
          <div class="board-item" style="background-color: #3397CE">
            {{item.shortName}}
          </div>
          <div class="board-item" style="background-color: palevioletred">
            {{item.currentScore}}
          </div>
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
     }
   },

   mounted() {
     const data = this.$store.state.teams
     this.teamList = data
     if (this.$store.state.currentRoute === 'model61') {
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

.board-item {
  width: 80px;
  height: 40px;
  border-radius: 8px;
  line-height: 40px;
  text-align: center;
  font-size: larger;
  color: white;
}
</style>