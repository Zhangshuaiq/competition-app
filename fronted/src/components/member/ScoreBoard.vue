<template>
  <div class="scoreboard" v-if="barStatus">
    <el-row>
      <el-col v-for="(item, index) in teamList" :key="item.id" :value="index" :span="2">
        <div style="margin-bottom: 5px">
          <!-- 第一层：编号 + 分数 -->
          <el-row>
            <el-col :span="10">
              <div class="round-board-item">
                {{ item.code }}
              </div>
            </el-col>
            <el-col :span="12">
              <div class="board-item">
                {{ item.teamScore }}
              </div>
            </el-col>
          </el-row>

          <!-- 第二层：固定高度，内容切换显示 -->
          <div class="fixed-layer-container" :style="isType8AnswerMode ? { height: '65px' } : {}">
            <!-- 抢答时间 -->
            <div
                class="fixed-layer-content"
                v-show="buzzStatus"
                style="background-color: transparent"
            >
              <el-row>
                <el-col :span="22">
                  <div class="board-item" :style="{ backgroundColor: buzzStatusColor[item.buzzStatus] }">
                    {{ item.buzzTime }}
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 状态展示 -->
            <div class="fixed-layer-content" v-show="!buzzStatus && showStatus" :style="isType8AnswerMode ? { height: 'auto' } : {}">
              <el-row>
                <el-col :span="22">
                  <!-- 自由答题：无论指定几号队员，状态条始终按团队显示 -->
                  <div v-if="isFreeAnswerMode"
                       class="board-item"
                       :style="{ backgroundColor: freeAnswerStatusColor(item) }">
                    {{ freeAnswerStatusText(item) }}
                  </div>

                  <!-- model61：成员状态格 -->
                  <div v-else-if="showMember" class="member-status-row">
                    <div
                        v-for="(st, idx) in item.statusList"
                        :key="idx"
                        class="status-item"
                        :style="{ backgroundColor: statusColor[st] }"
                    >
                      {{ idx + 1 }}
                    </div>
                  </div>

                  <!-- type8 答题界面：子环节名 + 答案状态（含"未提交"）
                       对应后端：boardAnswer=1 && previewPane instanceof QuestionPreviewPreview && questionType=8 -->
                  <template v-else-if="isType8AnswerMode">
                    <div class="board-item" :style="{ backgroundColor: type8StageBgColor(item), marginBottom: '5px' }">
                      {{ stageNameForTeam(item) }}
                    </div>
                    <div class="board-item" :style="{ backgroundColor: questionStatusBgColor(item.status) }">
                      {{ questionAnswerText(item) }} 
                    </div>
                  </template>

                  <!-- type8 选题界面（stages 路由）：只展示子环节选择状态，无"未提交"
                       对应后端：questionType=8 时始终展示的子环节名行 -->
                  <div v-else-if="isType8StageMode"
                       class="board-item"
                       :style="{ backgroundColor: type8StageBgColor(item) }">
                    {{ stageNameForTeam(item) }}
                  </div>

                  <!-- model60 / 非type8 question 路由：答题提交状态 -->
                  <div v-else-if="isQuestionMode || isModel60Mode"
                       class="board-item"
                       :style="{ backgroundColor: stageSelectBgColor(item.status) }">
                    {{ questionSelectText(item) }}
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
import {isFreeAnswerMode} from "@/utils/stageMode";
export default {
  name: "ScoreBoard",
  data() {
    return {
      barStatus: false,
      buzzStatus: false,
      statusColor: ['#FFFFFF00', '#0070C0', '#00B050', '#FF0000'],
      buzzStatusColor: ['#FFFFFF00', '#00B050', '#FF0000'],
      questionItem: sessionStorage.getItem('questionItem') || {}
    };
  },
  mounted() {
    this.syncFromStore(this.$store.state);
    // 直接订阅 Vuex mutation，任何 setTeamsData/setBarStatus/setBuzzStatus 都会
    // 立刻同步本地状态并强制重渲染，绕开 watcher/computed 未触发的问题
    this._unsub = this.$store.subscribe((mutation, state) => {
      this.syncFromStore(state);
      this.$forceUpdate();
    });
  },
  beforeUnmount() {
    if (this._unsub) {
      this._unsub();
      this._unsub = null;
    }
  },
  methods: {
    // 从 store 同步本地展示状态（teams 由 computed teamList 直接读取，这里只处理副作用）
    syncFromStore(state) {
      this.barStatus = state.barStatus;
      this.buzzStatus = state.buzzStatus;
      if (this.buzzStatus !== undefined) {
        sessionStorage.setItem('buzzStatus', this.buzzStatus);
      }
      const qi = sessionStorage.getItem('questionItem');
      this.questionItem = qi ? JSON.parse(qi) : {};
    },
    stageSelectText(item) {
      if (item.status === 2) return item.teamSelect || '已选择';
      if (item.status === 1) return '已选择';
      return '未选择';
    },
    stageSelectBgColor(status) {
      console.log('stageSelectBgColor', status);
      if (!this.isStageMode && !this.isQuestionMode) return this.statusColor[status] || '#0070C0';
      if (status === 2) return '#00B050';
      if (status === 1) return '#0070C0';
      return '#0070C0';
    },
    // 对齐后端 addModel60Panel 逻辑
    questionSelectText(item) {
      if (item.status === 1) return '已提交';                        // 已提交，等待揭晓
      if (item.status === 3) return item.teamSelect || '未提交';     // 强制结束且未提交
      if (item.status === 2) return item.teamSelect || '';           // 揭晓后显示答案
      return '';                                                     // status 0：答题中，空
    },
    questionStatusBgColor(status) {
      if (status === 2) return '#00B050';
      if (status === 3) return '#FF0000';
      if (status === 1) return '#0070C0';
      return '#888888';
    },
    questionAnswerText(item) {
      if (item.status === 2) return item.teamSelect || '';
      if (item.status === 3) return item.teamSelect || '已提交';
      if (item.status === 1) return '已提交';
      return '未提交';
    },
    type8StageBgColor(item) {
      if (item.stageSelectStatus === 2) return '#00B050';  // 已揭晓：绿
      if (item.stageSelectStatus === 1) return '#0070C0';  // 已选择：蓝
      return '#888888';                                     // 未选择：灰
    },
    stageNameForTeam(item) {
      // 纯按后端状态渲染：已揭晓(2)显示子环节名，已选择(1)显示"已选择"，否则"未选择"
      if (item.stageSelectStatus === 2) return item.stageSelect || '已选择';
      if (item.stageSelectStatus === 1) return '已选择';
      return '未选择';
    },
    freeAnswerStatus(item) {
      const selectedTeams = (this._stage && this._stage.freeAnswerTeams) || [];
      if (selectedTeams.indexOf(item.fullName) < 0) return -1;
      const index = (this._stage && this._stage.freeAnswerMemberIndex) || 0;
      return item.statusList && item.statusList[index] !== undefined ? item.statusList[index] : 0;
    },
    freeAnswerStatusColor(item) {
      const status = this.freeAnswerStatus(item);
      return status < 0 ? '#888888' : this.statusColor[status];
    },
    freeAnswerStatusText(item) {
      const status = this.freeAnswerStatus(item);
      if (status < 0) return '未参与';
      return ['', '已提交', '正确', '错误'][status] || '未提交';
    },
  },
  computed: {
    // 直接读 store，teams 更新后界面实时刷新（避免本地拷贝导致的不同步）
    teamList() { return this.$store.state.teams || []; },
    // 使用 this.$route.name 替代 store 里的 currentRoute，避免持久化脏数据
    _route() { return this.$route.name || ''; },
    _stage() { return this.$store.state.stage; },

    // model61 路由：个人答题模式，展示成员状态格
    showMember() { return this._route.includes('model61'); },

    isFreeAnswerMode() {
      return this.showMember && !!(this._stage
          && isFreeAnswerMode(this._stage));
    },

    // model60 路由：团队答题模式
    isModel60Mode() { return this._route.includes('model60'); },

    // stages 路由：环节列表（选题界面）
    isStageMode() { return this._route.includes('stages'); },

    // stages / stageName 路由 + questionType=8：type8 自选环节，展示子环节选择状态
    // stageName 是主屏显示环节名称时的路由，stages 是平板选题列表路由
    isType8StageMode() {
      const inStageRoute = this._route.includes('stages') || this._route.includes('stageName') || this._route.includes('stageQuestion');
      if (!inStageRoute) return false;
      if (this._stage && this._stage.questionType === 8) return true;
      // 有队伍有 stageSelectStatus 说明当前在 type8 上下文
      return !!(this.teamList && this.teamList.some(t => t.stageSelectStatus > 0));
    },

    // question 路由 + boardAnswer=1：进入了答题界面
    // 对应后端 previewPane instanceof QuestionPreviewPreview
    // 注意排除 stageQuestion（选题列表路由），避免误触发答题模式
    isQuestionMode() {
      return !!(this._route.includes('question') && !this._route.includes('stageQuestion') && this._stage && this._stage.boardAnswer === 1);
    },

    // question 路由 + boardAnswer=1 + questionType=8：type8 答题界面
    // 对应后端：boardAnswer=1 && QuestionPreviewPreview && questionType=8
    // 此状态下才会出现"未提交"
    isType8AnswerMode() {
      return this.isQuestionMode && !!(this._stage && this._stage.questionType === 8);
    },

    // 状态行整体是否显示，对齐后端 ScoreBoardHPreview 的条件：
    // 仅在 model60/61、type8选题、question答题界面时展示
    showStatus() {
      return this.showMember
          || this.isModel60Mode
          || this.isType8StageMode
          || this.isQuestionMode;
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
