<!-- 竞技模式网页版 -->
<template>
  <div class="board-wrapper" :style="wrapperStyle">
    <div :style="gridStyle">
      <div
        v-for="(team, index) in displayList"
        :key="team.id || index"
        class="score-card"
        :class="{
          'card-anim-plus':  getAnim(team) && getAnim(team).type === 'plus',
          'card-anim-minus': getAnim(team) && getAnim(team).type === 'minus',
        }"
        :style="cardStyle"
      >
        <!-- 队伍名称 -->
        <div class="card-team-name" :style="teamNameStyle">{{ team.fullName }}</div>

        <!-- 分数区域 -->
        <div class="card-score-area">
          <!-- 有动画时用带动画 class 的 div（key 变化触发重建，动画重播） -->
          <div
            v-if="getAnim(team)"
            :key="`s-${getTeamKey(team)}-${getAnim(team).key}`"
            class="card-score"
            :class="getAnim(team).type === 'plus' ? 'score-pulse' : 'score-pulse-minus'"
            :style="scoreStyle(team)"
          >{{ team.teamScore }}</div>
          <!-- 无动画时普通展示 -->
          <div v-else class="card-score" :style="scoreStyle(team)">{{ team.teamScore }}</div>

          <!-- 浮动加减分数字 -->
          <div
            v-if="getAnim(team)"
            :key="`d-${getTeamKey(team)}-${getAnim(team).key}`"
            :class="getAnim(team).type === 'plus' ? 'plus-number' : 'minus-number'"
          >{{ getAnim(team).type === 'plus' ? `+${getAnim(team).diff}` : getAnim(team).diff }}</div>
        </div>

        <!-- 底部统计 -->
        <div class="card-stats">
          <div v-if="cfg.correctRatioShow" class="stat-item" :style="accuracyStyle(team)">
            正确率: {{ team.correctRatio }}%
          </div>
          <div v-if="cfg.answeredShow" class="stat-item" :style="answeredStyle">
            已答: {{ team.answeredCount }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { model7BoardStyle, model7BoardData } from "@/api";
import baseUrl from "@/config";

export default {
  name: "CompeteScore",

  data() {
    return {
      teamList: [],

      cfg: {
        row: 3, col: 4,
        x: 100, y: 100,
        cspace: 20, rspace: 20,
        borderW: 200, borderL: 160,
        teamFontsize: 14, teamFontWidth: 400,
        scoreFontsize: 48, scoreFontWidth: 700,
        answeredFontsize: 12,
        correctRatioFontsize: 12, correctRatioFontWidth: 400,
        backColor:         "#FFFFFF",
        teamColor:         "#333333",
        scoreColor:        "#4A90D9",
        scoreLargeColor:   "#27AE60",
        answeredColor:     "#E74C3C",
        correctRatioColor: "#E74C3C",
        answeredShow:      true,
        correctRatioShow:  true,
        webSite:           "",
      },

      imageUrl:        `${baseUrl}/resource/竞技面板背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,

      // 动画状态：{ [teamKey]: { type: 'plus'|'minus', diff: Number, key: Number } | null }
      animState: {},
      animKeyCounter: 0,
      // 上一次各队分数，用于检测变化
      prevScores: {},

      pollTimer: null,
    };
  },

  watch: {
    teamList: {
      handler(newList) {
        newList.forEach(team => {
          const k = this.getTeamKey(team);
          const prev = this.prevScores[k];
          if (prev !== undefined && prev !== team.teamScore) {
            this.triggerAnim(team, team.teamScore - prev);
          }
          this.prevScores[k] = team.teamScore;
        });
      },
      deep: true,
    },
  },

  computed: {
    displayList() {
      return this.teamList.slice(0, this.cfg.row * this.cfg.col);
    },

    wrapperStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize:  "100vw 100vh",
      };
    },

    gridStyle() {
      return {
        position:            "absolute",
        left:                this.cfg.x + "px",
        top:                 this.cfg.y + "px",
        display:             "grid",
        gridTemplateColumns: `repeat(${this.cfg.col}, ${this.cfg.borderW}px)`,
        gridTemplateRows:    `repeat(${this.cfg.row}, ${this.cfg.borderL}px)`,
        gap:                 `${this.cfg.rspace}px ${this.cfg.cspace}px`,
      };
    },

    cardStyle() {
      return {
        width:          this.cfg.borderW + "px",
        height:         this.cfg.borderL + "px",
        background:     this.cfg.backColor,
        backdropFilter: "blur(5px)",
        border:         "1px solid rgba(255,255,255,0.3)",
      };
    },

    teamNameStyle() {
      return {
        fontSize:     this.cfg.teamFontsize + "px",
        fontWeight:   this.cfg.teamFontWidth,
        color:        this.cfg.teamColor,
      };
    },

    answeredStyle() {
      return {
        fontSize: this.cfg.answeredFontsize + "px",
        color:    this.cfg.answeredColor,
      };
    },
  },

  methods: {
    getTeamKey(team) {
      return team.id != null ? String(team.id) : team.fullName;
    },

    getAnim(team) {
      return this.animState[this.getTeamKey(team)] || null;
    },

    triggerAnim(team, diff) {
      const k = this.getTeamKey(team);
      // 先清空，nextTick 后重设，确保 CSS 动画从头播放
      this.animState[k] = null;
      this.$nextTick(() => {
        this.animKeyCounter++;
        const animKey = this.animKeyCounter;
        this.animState[k] = {
          type: diff > 0 ? "plus" : "minus",
          diff,
          key:  animKey,
        };
        setTimeout(() => {
          const cur = this.animState[k];
          if (cur && cur.key === animKey) {
            this.animState[k] = null;
          }
        }, 1600);
      });
    },

    scoreStyle(team) {
      const isActive = team.answeredCount && team.answeredCount > 0;
      return {
        fontSize:   this.cfg.scoreFontsize + "px",
        fontWeight: this.cfg.scoreFontWidth,
        color:      isActive ? this.cfg.scoreLargeColor : this.cfg.scoreColor,
      };
    },

    accuracyStyle(team) {
      const r = team.correctRatio || 0;
      let color;
      if (r >= 80)      color = "#4CAF50";
      else if (r >= 60) color = "#FF9800";
      else              color = "#F44336";
      return {
        fontSize:   this.cfg.correctRatioFontsize + "px",
        fontWeight: this.cfg.correctRatioFontWidth,
        color,
      };
    },

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload  = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },

    // ===== 配置加载 =====
    loadConfig() {
      model7BoardStyle()
        .then(res => this.applyConfig(res.data))
        .catch(err => console.error("[Config] model7 加载失败", err));
    },

    applyConfig(data) {
      if (!data) return;
      const num  = (v, d) => (v !== undefined && v !== null && v !== "" ? parseInt(v)  : d);
      const str  = (v, d) => (v !== undefined && v !== null && v !== "" ? v            : d);
      const bool = (v, d) => (v !== undefined && v !== null             ? !!v          : d);

      this.cfg.row                   = num(data.row,                   this.cfg.row);
      this.cfg.col                   = num(data.col,                   this.cfg.col);
      this.cfg.x                     = num(data.x,                     this.cfg.x);
      this.cfg.y                     = num(data.y,                     this.cfg.y);
      this.cfg.cspace                = num(data.cspace,                this.cfg.cspace);
      this.cfg.rspace                = num(data.rspace,                this.cfg.rspace);
      this.cfg.borderW               = num(data.borderW,               this.cfg.borderW);
      this.cfg.borderL               = num(data.borderL,               this.cfg.borderL);
      this.cfg.teamFontsize          = num(data.teamFontsize,           this.cfg.teamFontsize);
      this.cfg.teamFontWidth         = num(data.teamFontWidth,          this.cfg.teamFontWidth);
      this.cfg.scoreFontsize         = num(data.scoreFontsize,          this.cfg.scoreFontsize);
      this.cfg.scoreFontWidth        = num(data.scoreFontWidth,         this.cfg.scoreFontWidth);
      this.cfg.answeredFontsize      = num(data.answeredFontsize,       this.cfg.answeredFontsize);
      this.cfg.correctRatioFontsize  = num(data.correctRatioFontsize,   this.cfg.correctRatioFontsize);
      this.cfg.correctRatioFontWidth = num(data.correctRatioFontWidth,  this.cfg.correctRatioFontWidth);
      this.cfg.backColor             = str(data.backColor,              this.cfg.backColor);
      this.cfg.teamColor             = str(data.teamColor,              this.cfg.teamColor);
      this.cfg.scoreColor            = str(data.scoreColor,             this.cfg.scoreColor);
      this.cfg.scoreLargeColor       = str(data.scoreLargeColor,        this.cfg.scoreLargeColor);
      this.cfg.answeredColor         = str(data.answeredColor,          this.cfg.answeredColor);
      this.cfg.correctRatioColor     = str(data.correctRatioColor,      this.cfg.correctRatioColor);
      this.cfg.answeredShow          = bool(data.answeredShow,          this.cfg.answeredShow);
      this.cfg.correctRatioShow      = bool(data.correctRatioShow,      this.cfg.correctRatioShow);
      this.cfg.webSite               = str(data.webSite,                this.cfg.webSite);
    },

    // ===== 队伍数据 =====
    fetchTeams() {
      model7BoardData()
        .then(res => { this.teamList = res.data || []; })
        .catch(err => console.error("[Teams] 拉取失败", err));
    },

    startPoll() {
      if (this.pollTimer) return;
      this.pollTimer = setInterval(() => {
        this.loadConfig();
        this.fetchTeams();
      }, 1000);
    },

    stopPoll() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null; }
    },
  },

  mounted() {
    sessionStorage.setItem("role", "score");
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        this.imageUrl = this.defaultImageUrl;
      }
    });
    this.loadConfig();
    this.fetchTeams();
    this.startPoll();
  },

  beforeUnmount() {
    this.stopPoll();
  },
};
</script>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; }

.board-wrapper {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

/* ===== 卡片基础样式 ===== */
.score-card {
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: visible;
  transition: box-shadow 0.3s ease;
}

.card-team-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-score-area {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.card-score {
  text-align: center;
  line-height: 1;
}

.card-stats {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
}

.stat-item {
  white-space: nowrap;
}

/* ===== 加分：卡片发光动画 ===== */
@keyframes cardAnimPlus {
  0%   { box-shadow: 0 6px 20px rgba(0,0,0,0.25); border-color: rgba(255,255,255,0.3); background-color: transparent; }
  20%  { box-shadow: 0 0 60px rgba(76,175,80,0.9); border-color: #4CAF50; background-color: rgba(76,175,80,0.1); }
  60%  { box-shadow: 0 0 80px rgba(76,175,80,1);   border-color: #4CAF50; background-color: rgba(76,175,80,0.05); }
  100% { box-shadow: 0 6px 20px rgba(0,0,0,0.25); border-color: rgba(255,255,255,0.3); background-color: transparent; }
}

.card-anim-plus {
  animation: cardAnimPlus 1.5s ease-in-out;
}

/* ===== 减分：卡片缩放 + 红色发光动画 ===== */
@keyframes cardAnimMinus {
  0%   { transform: scale(1);    box-shadow: 0 6px 20px rgba(0,0,0,0.25); border-color: rgba(255,255,255,0.3); }
  20%  { transform: scale(1.06); box-shadow: 0 0 80px rgba(244,67,54,0.8); border-color: #F44336; }
  60%  { transform: scale(1.03); box-shadow: 0 0 100px rgba(244,67,54,1);  border-color: #F44336; }
  100% { transform: scale(1);    box-shadow: 0 6px 20px rgba(0,0,0,0.25); border-color: rgba(255,255,255,0.3); }
}

.card-anim-minus {
  animation: cardAnimMinus 1.5s ease-in-out;
}

/* ===== 分数加分脉冲动画 ===== */
@keyframes scorePulse {
  0%   { transform: scale(1);   color: inherit; text-shadow: none; }
  25%  { transform: scale(1.8); color: #4CAF50; text-shadow: 0 0 30px rgba(76,175,80,0.9); }
  50%  { transform: scale(2);   color: #FFD700; text-shadow: 0 0 50px rgba(255,215,0,0.8); }
  75%  { transform: scale(1.8); color: #4CAF50; text-shadow: 0 0 30px rgba(76,175,80,0.9); }
  100% { transform: scale(1);   color: inherit; text-shadow: none; }
}

.score-pulse {
  animation: scorePulse 1.5s ease-in-out;
  display: inline-block;
}

/* ===== 分数减分脉冲动画 ===== */
@keyframes scorePulseMinus {
  0%   { transform: scale(1);   color: inherit; text-shadow: none; }
  25%  { transform: scale(1.6); color: #F44336; text-shadow: 0 0 30px rgba(244,67,54,0.9); }
  50%  { transform: scale(1.8); color: #FF5722; text-shadow: 0 0 50px rgba(255,87,34,0.8); }
  75%  { transform: scale(1.6); color: #F44336; text-shadow: 0 0 30px rgba(244,67,54,0.9); }
  100% { transform: scale(1);   color: inherit; text-shadow: none; }
}

.score-pulse-minus {
  animation: scorePulseMinus 1.5s ease-in-out;
  display: inline-block;
}

/* ===== 加分浮动数字 ===== */
@keyframes plusNumber {
  0%   { opacity: 0; transform: translateX(-50%) translateY(0)     scale(0.5); }
  50%  { opacity: 1; transform: translateX(-50%) translateY(-50px) scale(1.2); }
  100% { opacity: 0; transform: translateX(-50%) translateY(-100px) scale(1.5); }
}

.plus-number {
  position: absolute;
  left: 50%;
  top: 50%;
  font-size: 2rem;
  font-weight: bold;
  color: #4CAF50;
  text-shadow: 0 0 20px rgba(76,175,80,0.8);
  animation: plusNumber 1.5s ease-out forwards;
  pointer-events: none;
  z-index: 100;
  white-space: nowrap;
}

/* ===== 减分浮动数字 ===== */
@keyframes minusNumber {
  0%   { opacity: 0; transform: translateX(-50%) translateY(0)     scale(0.5); }
  50%  { opacity: 1; transform: translateX(-50%) translateY(-50px) scale(1.2); }
  100% { opacity: 0; transform: translateX(-50%) translateY(-100px) scale(1.5); }
}

.minus-number {
  position: absolute;
  left: 50%;
  top: 50%;
  font-size: 2rem;
  font-weight: bold;
  color: #F44336;
  text-shadow: 0 0 20px rgba(244,67,54,0.8);
  animation: minusNumber 1.5s ease-out forwards;
  pointer-events: none;
  z-index: 100;
  white-space: nowrap;
}
</style>
