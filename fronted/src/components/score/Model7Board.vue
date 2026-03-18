<!-- 竞技模式网页版 -->
<template>
  <div id="compete-box" :style="containerBoxStyle">
    <div
        v-for="(team, index) in displayList"
        :key="team.id || index"
        class="compete-item"
        :style="scoreItemStyle(index)"
    >
      <!-- 左上：队伍名称 -->
      <div class="team-name" :style="teamNameStyle">
        {{ team.fullName }}
      </div>


      <!-- 中间：分数 -->
      <div class="score-value" :style="scoreValueStyle(team)">
        {{ team.teamScore }}
      </div>

      <!-- 底部左：正确率（correctRatioShow 控制） -->
      <div v-if="cfg.correctRatioShow" class="correct-ratio" :style="correctRatioStyle">
        正确率：{{ team.correctRatio }}%
      </div>

      <!-- 底部右：已答题数 -->
      <div v-if="cfg.answeredShow" class="answered-count" :style="answeredCountStyle">
        已答：{{ team.answeredCount }}
      </div>
    </div>
  </div>
</template>

<script>
import { model7BoardStyle, model7BoardData } from "@/api";

export default {
  name: "CompeteScore",

  data() {
    return {
      teamList: [],

      // model7 配置默认值，字段与后端 JSON key 一致
      cfg: {
        row: 3, col: 4,
        x: 100, y: 100,
        cspace: 20, rspace: 20,
        borderW: 200, borderL: 160,       // 格子宽、格子高（px）
        teamFontsize: 14, teamFontWidth: 400,
        scoreFontsize: 48, scoreFontWidth: 700,
        answeredFontsize: 12,
        correctRatioFontsize: 12, correctRatioFontWidth: 400,
        backColor:          "#FFFFFF",
        teamColor:          "#333333",
        scoreColor:         "#4A90D9",     // 普通分数色
        scoreLargeColor:    "#27AE60",     // 高分/抢答成功色
        answeredColor:      "#E74C3C",     // 已答角标色
        correctRatioColor:  "#E74C3C",     // 正确率颜色
        answeredShow:       true,
        correctRatioShow:   true,
        webSite:            "",
      },

      pollTimer: null,
    };
  },

  computed: {
    displayList() {
      return this.teamList.slice(0, this.cfg.row * this.cfg.col);
    },

    containerBoxStyle() {
      const w = this.cfg.x + this.cfg.col * this.cfg.borderW + (this.cfg.col - 1) * this.cfg.cspace;
      const h = this.cfg.y + this.cfg.row * this.cfg.borderL + (this.cfg.row - 1) * this.cfg.rspace;
      return {
        width:    w + "px",
        height:   h + "px",
        position: "relative",
        overflow: "visible",
      };
    },

    teamNameStyle() {
      return {
        position:     "absolute",
        top:          "8px",
        left:         "10px",
        right:        "28px",           // 给右上角标留位
        fontSize:     this.cfg.teamFontsize + "px",
        fontWeight:   this.cfg.teamFontWidth,
        color:        this.cfg.teamColor,
        whiteSpace:   "nowrap",
        overflow:     "hidden",
        textOverflow: "ellipsis",
        zIndex:       10,
      };
    },

    correctRatioStyle() {
      return {
        position:   "absolute",
        bottom:     "8px",
        left:       "10px",
        fontSize:   this.cfg.correctRatioFontsize + "px",
        fontWeight: this.cfg.correctRatioFontWidth,
        color:      this.cfg.correctRatioColor,
        zIndex:     10,
      };
    },

    answeredCountStyle() {
      return {
        position:  "absolute",
        bottom:    "8px",
        right:     "10px",
        fontSize:  this.cfg.correctRatioFontsize + "px",
        color:     this.cfg.answeredColor,
        zIndex:    10,
      };
    },
  },

  methods: {
    cellLeft(index) {
      const col = index % this.cfg.col;
      return this.cfg.x + col * (this.cfg.borderW + this.cfg.cspace);
    },

    cellTop(index) {
      const row = Math.floor(index / this.cfg.col);
      return this.cfg.y + row * (this.cfg.borderL + this.cfg.rspace);
    },

    scoreItemStyle(index) {
      return {
        position:        "absolute",
        left:            this.cellLeft(index) + "px",
        top:             this.cellTop(index)  + "px",
        width:           this.cfg.borderW + "px",
        height:          this.cfg.borderL + "px",
        backgroundColor: this.cfg.backColor,
        borderRadius:    "8px",
        boxShadow:       "0 2px 8px rgba(0,0,0,0.15)",
        boxSizing:       "border-box",
        overflow:        "hidden",
      };
    },

    // 分数颜色：有抢答记录（answeredCount > 0）用 scoreLargeColor，否则用 scoreColor
    scoreValueStyle(team) {
      const isLarge = team.answeredCount && team.answeredCount > 0;
      return {
        position:   "absolute",
        top:        "50%",
        left:       0,
        transform:  "translateY(-60%)",   // 略偏上，给底部信息让位
        width:      "100%",
        textAlign:  "center",
        fontSize:   this.cfg.scoreFontsize + "px",
        fontWeight: this.cfg.scoreFontWidth,
        color:      isLarge ? this.cfg.scoreLargeColor : this.cfg.scoreColor,
        zIndex:     10,
        lineHeight: 1,
      };
    },

    // ===== 配置加载 =====
    loadConfig() {
      model7BoardStyle()
          .then(res => this.applyConfig(res.data))
          .catch(err => console.error("[Config] model7 加载失败", err));
    },

    applyConfig(data) {
      if (!data) return;
      const num  = (v, def) => (v !== undefined && v !== null && v !== "" ? parseInt(v)  : def);
      const str  = (v, def) => (v !== undefined && v !== null && v !== "" ? v            : def);
      const bool = (v, def) => (v !== undefined && v !== null             ? !!v          : def);

      this.cfg.row                    = num(data.row,                    this.cfg.row);
      this.cfg.col                    = num(data.col,                    this.cfg.col);
      this.cfg.x                      = num(data.x,                      this.cfg.x);
      this.cfg.y                      = num(data.y,                      this.cfg.y);
      this.cfg.cspace                 = num(data.cspace,                  this.cfg.cspace);
      this.cfg.rspace                 = num(data.rspace,                  this.cfg.rspace);
      this.cfg.borderW                = num(data.borderW,                 this.cfg.borderW);
      this.cfg.borderL                = num(data.borderL,                 this.cfg.borderL);
      this.cfg.teamFontsize           = num(data.teamFontsize,             this.cfg.teamFontsize);
      this.cfg.teamFontWidth          = num(data.teamFontWidth,            this.cfg.teamFontWidth);
      this.cfg.scoreFontsize          = num(data.scoreFontsize,            this.cfg.scoreFontsize);
      this.cfg.scoreFontWidth         = num(data.scoreFontWidth,           this.cfg.scoreFontWidth);
      this.cfg.answeredFontsize       = num(data.answeredFontsize,         this.cfg.answeredFontsize);
      this.cfg.correctRatioFontsize   = num(data.correctRatioFontsize,     this.cfg.correctRatioFontsize);
      this.cfg.correctRatioFontWidth  = num(data.correctRatioFontWidth,    this.cfg.correctRatioFontWidth);
      this.cfg.backColor              = str(data.backColor,                this.cfg.backColor);
      this.cfg.teamColor              = str(data.teamColor,                this.cfg.teamColor);
      this.cfg.scoreColor             = str(data.scoreColor,               this.cfg.scoreColor);
      this.cfg.scoreLargeColor        = str(data.scoreLargeColor,          this.cfg.scoreLargeColor);
      this.cfg.answeredColor          = str(data.answeredColor,            this.cfg.answeredColor);
      this.cfg.correctRatioColor      = str(data.correctRatioColor,        this.cfg.correctRatioColor);
      this.cfg.answeredShow           = bool(data.answeredShow,            this.cfg.answeredShow);
      this.cfg.correctRatioShow       = bool(data.correctRatioShow,        this.cfg.correctRatioShow);
      this.cfg.webSite                = str(data.webSite,                  this.cfg.webSite);
    },

    // ===== 队伍数据 =====
    fetchTeams() {
      model7BoardData()
          .then(res => { this.teamList = res.data || []; })
          .catch(err => console.error("[Teams] 拉取失败", err));
    },

    startPoll() {
      if (this.pollTimer) return;
      this.pollTimer = setInterval(() => 
      {
        this.loadConfig();
        this.fetchTeams()
      }, 
      1000);
    },

    stopPoll() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null; }
    },
  },

  mounted() {
    sessionStorage.setItem('role', 'score')
    this.loadConfig();
    this.fetchTeams();
    this.startPoll();
  },

  beforeDestroy() {
    this.stopPoll();
  },
};
</script>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body { overflow: hidden; height: 100%; }

.compete-item { position: absolute; }

.team-name,
.answered-badge,
.score-value,
.correct-ratio,
.answered-count {
  position: absolute;
  z-index: 10;
}
</style>