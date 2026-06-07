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
          <div class="fixed-layer-container" :style="isType8QuestionMode ? { height: '65px' } : {}">
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
            <div class="fixed-layer-content" v-show="!buzzStatus && showStatus" :style="isType8QuestionMode ? { height: 'auto' } : {}">
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
                  <template v-else-if="isType8QuestionMode">
                    <div class="board-item" style="background-color: #0070C0; margin-bottom: 5px;">
                      {{ stageNameForTeam(item) }}
                    </div>
                    <div class="board-item" :style="{ backgroundColor: questionStatusBgColor(item.status) }">
                      {{ questionAnswerText(item) }}
                    </div>
                  </template>
                  <div
                      v-else
                      class="board-item"
                      :style="{ backgroundColor: stageSelectBgColor(item.status) }"
                  >
                    <template v-if="isStageMode">
                      {{ stageSelectText(item) }}
                    </template>
                    <template v-else-if="isQuestionMode">
                      {{ questionSelectText(item) }}
                    </template>
                    <template v-else-if="item.status === 2 || item.status === 3">
                      {{ item.teamSelect }}
                    </template>
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
      questionItem: sessionStorage.getItem('questionItem') || {},
      stageNameCache: {}
    };
  },
  beforeRouteUpdate(to, from, next) {
    const data = this.$store.state.teams;
    this.teamList = data;
    next();
  },
  watch: {
    '$store.state.teams': function (newTeams) {
      this.teamList = newTeams;
      this.updateStageNameCache(newTeams);
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
    this.updateStageNameCache(data);
    this.buzzStatus = this.$store.state.buzzStatus;
    this.barStatus = this.$store.state.barStatus;
    const route = this.$store.state.currentRoute;
    if (route.includes('model61')) {
      this.showStatus = true;
      this.showMember = true;
    } else if (route.includes('model60')) {
      this.showStatus = true;
      this.showMember = false;
    } else if (route.includes('stages')) {
      this.showStatus = true;
      this.showMember = false;
    } else if (route.includes('question')) {
      const stage = this.$store.state.stage;
      if (stage && stage.boardAnswer === 1) {
        this.showStatus = true;
        this.showMember = false;
      }
    }
  },
  methods: {
    stageSelectText(item) {
      if (item.status === 2) return item.teamSelect || '已选择';
      if (item.status === 1) return '已选择';
      return '未选择';
    },
    stageSelectBgColor(status) {
      if (!this.isStageMode && !this.isQuestionMode) return this.statusColor[status] || '#FFFFFF00';
      if (status === 2) return '#00B050';
      if (status === 1) return '#0070C0';
      return '#888888';
    },
    questionSelectText(item) {
      if (item.status === 2) return item.teamSelect || '已提交';
      return item.teamSelect || '未提交';
    },
    questionStatusBgColor(status) {
      if (status === 2) return '#00B050';
      if (status === 1) return '#0070C0';
      return '#888888';
    },
    questionAnswerText(item) {
      if (item.status === 2) return item.teamSelect || '';
      if (item.status === 1) return '已提交';
      return '未提交';
    },
    stageNameForTeam(item) {
      return this.stageNameCache[item.id] || item.teamSelect || '未选择';
    },
    updateStageNameCache(teams) {
      if (!teams) return;
      teams.forEach(team => {
        if (team.status !== 2 && team.teamSelect) {
          this.$set(this.stageNameCache, team.id, team.teamSelect);
        } else if (team.status === 0 && !team.teamSelect) {
          this.$delete(this.stageNameCache, team.id);
        }
      });
    },
  },
  computed: {
    isStageMode() {
      const route = this.$store.state.currentRoute;
      return route && route.includes('stages');
    },
    isQuestionMode() {
      const route = this.$store.state.currentRoute;
      const stage = this.$store.state.stage;
      return route && route.includes('question') && stage && stage.boardAnswer === 1;
    },
    isType8QuestionMode() {
      const stage = this.$store.state.stage;
      return this.isQuestionMode && stage && stage.questionType === 8;
    },
  },
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
