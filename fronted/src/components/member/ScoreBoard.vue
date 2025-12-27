<template>
  <div class="scoreboard" v-if="barStatus">
    <el-row>
      <el-col v-for="(item, index) in teamList" :key="item.id" :value="index" span="2">
        <div style="margin-bottom: 5px">
          <!-- 第一层：编号 + 分数 -->
          <el-row>
            <el-col span="10">
              <div class="round-board-item">
                {{ item.code }}
              </div>
            </el-col>
            <el-col span="12">
              <div class="board-item">
                {{ item.teamScore }}
              </div>
            </el-col>
          </el-row>

          <!-- 第二层：固定高度，内容切换显示 -->
          <div class="fixed-layer-container">
            <!-- 抢答时间 -->
            <div
                class="fixed-layer-content"
                v-show="buzzStatus"
                style="background-color: transparent"
            >
              <el-row>
                <el-col span="22">
                  <div class="board-item" :style="{ backgroundColor: buzzStatusColor[item.buzzStatus] }">
                    {{ item.buzzTime }}
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 状态展示 -->
            <div class="fixed-layer-content" v-show="!buzzStatus && showStatus">
              <el-row>
                <el-col span="22">
                  <div v-if="showMember" class="member-status-row">
                    <div
                        v-for="(st, idx) in item.statusList"
                        :key="idx"
                        class="status-item"
                        :style="{ backgroundColor: statusColor[st] }"
                    >
                      {{ idx + 1 }}
                    </div>
                  </div>
                  <div
                      v-else
                      class="board-item"
                      :style="{ backgroundColor: statusColor[item.status] }"
                  >
                    <div v-if="item.status === 2 || item.status === 3">
                      {{ item.teamSelect }}
                    </div>
                  </div>
                </el-col>
              </el-row>
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
    return {
      barStatus: true,
      buzzStatus: false,
      teamList: [],
      showStatus: false,
      showMember: false,
      statusColor: ['#FFFFFF00', '#0070C0', '#00B050', '#FF0000'],
      buzzStatusColor: ['#FFFFFF00', '#00B050', '#FF0000'],
      questionItem: sessionStorage.getItem('questionItem') || {}
    };
  },
  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.teams;
    this.teamList = data;
    next();
  },
  watch: {
    '$store.state.teams': function (newStatus) {
      this.teamList = newStatus;
      this.questionItem = JSON.parse(sessionStorage.getItem('questionItem'));
    },
    '$store.state.buzzStatus': function (newStatus) {
      sessionStorage.setItem('buzzStatus', newStatus);
      this.buzzStatus = newStatus;
    },
    '$store.state.barStatus': function (newStatus) {
      sessionStorage.setItem('barStatus', newStatus);
      this.barStatus = newStatus;
    },
    questionItem(newVal, oldVal) {
      console.log(oldVal)
      console.log('Session questionItem changed:', newVal);
    }
  },
  mounted() {
    const data = this.$store.state.teams;
    this.teamList = data;
     this.buzzStatus = this.$store.state.buzzStatus;
    this.barStatus = this.$store.state.barStatus;
    const route = this.$store.state.currentRoute;
    if (route.includes('model61')) {
      this.showStatus = true;
      this.showMember = true;
    } else if (route.includes('model60')) {
      this.showStatus = true;
      this.showMember = false;
    }
  }
};
</script>

<style scoped>
/* 每一项的高度控制在两层 */
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

/* 固定层容器：高度固定 */
.fixed-layer-container {
  position: relative;
  height: 30px;
  overflow: hidden;
  margin-top: 5px;
}

/* 固定内容层：绝对定位，层叠显示 */
.fixed-layer-content {
  position: absolute;
  top: 0;
  left: 0;
  height: 30px;
  width: 100%;
  line-height: 30px;
  font-size: smaller;
  color: white;
  text-align: center;
}

/* 成员状态样式 */
.member-status-row {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 30px;
}

.status-item {
  width: 20px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: larger;
  color: white;
  margin-right: 2px;
}
</style>
