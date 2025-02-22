<template>
  <div class="scoreboard">
    <el-row>
      <el-col v-for="(item, index) in teamList" :key="item.id" span="2">
        <div style="margin-bottom: 5px">
          <el-row>
            <el-col span="10">
              <div class="round-board-item">
                {{index + 1}}
              </div>
            </el-col>
            <el-col span="12">
              <div class="board-item" >
                {{item.teamScore}}
              </div>
            </el-col>
          </el-row>
          <div :style="{ visibility: buzzStatus ? 'visible' : 'hidden' }">
            <el-row>
              <el-col span="22">
                <div class="board-item" :style="{backgroundColor: buzzStatusColor[item.buzzStatus]}">
                  <div>
                    {{item.buzzTime}}
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div :style="{ visibility: showStatus ? 'visible' : 'hidden' }">
            <div :style="{ visibility: showMember ? 'visible' : 'hidden' }">
              <el-row>
                <el-col span="6" v-for="(st, index) in item.statusList" :key="index">
                  <div class="status-item" :style="{backgroundColor: statusColor[st]}">
                    {{index + 1}}
                  </div>
                </el-col>
              </el-row>
            </div>
<!--            <div :style="{ visibility: !showMember ? 'visible' : 'hidden' }">-->
<!--              <el-row>-->
<!--                <el-col span="22">-->
<!--                  <div class="board-item" :style="{backgroundColor: statusColor[item.status]}">-->
<!--                    <div v-if="item.status === 2 || item.status === 3">-->
<!--                      {{item.teamSelect}}-->
<!--                    </div>-->
<!--                  </div>-->
<!--                </el-col>-->
<!--              </el-row>-->
<!--            </div>-->
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
       buzzStatus: false,
       teamList: [],
       showStatus: false,
       showMember: false,
       statusColor: ['#FFFFFF', '#0070C0', '#00B050', '#FF0000'],
       buzzStatusColor: ['#FFFFFF',  '#00B050', '#FF0000'],
       questionItem: sessionStorage.getItem('questionItem') || {}
     }
   },

   beforeRouteUpdate(to, from, next) {
     const data = this.$store.state.teams
     this.teamList = data
     console.log(this.teamList)
     next();
   },

   watch: {
     '$store.state.teams': function (newStatus) {
       this.teamList = newStatus
       this.questionItem = JSON.parse(sessionStorage.getItem('questionItem'))
       console.log(this.questionItem)
     },

     '$store.state.buzzStatus': function (newStatus) {
       sessionStorage.setItem('buzzStatus', newStatus)
       this.buzzStatus = newStatus
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
     this.buzzStatus = sessionStorage.getItem('buzzStatus') === 'true'
     if (this.$store.state.currentRoute.indexOf('model61') >= 0) {
       this.showStatus = true
       this.showMember = true
     } else if (this.$store.state.currentRoute.indexOf('model60') >= 0) {
       this.showStatus = true
       this.showMember = false
     }
   }
 }

</script>

<style scoped>

.status-item {
  width: 20px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: larger;
  color: white;
}

.round-board-item {
  width: 100%;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: smaller;
  color: white;
  background-color: #FFC000;
}

.board-item {
  width: 100%;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: smaller;
  color: white;
  background-color: #C40000;
}
</style>