<!-- 答题模式网页版 -->
<template>
  <div id="container-box" :style="containerBoxStyle">
    <div
        v-for="(team, index) in displayList"
        :key="team.id || index"
        class="score-item"
        :style="scoreItemStyle(team, index)"
    >
      <!-- 队伍名称 -->
      <div class="team-name" :style="teamNameStyle">
        {{ team.fullName }}
        <div style="width: 100%; height: 1px; background-color: black"></div>
      </div>

      <!-- 答题内容 -->
      <div class="answer-content" :style="answerContentStyle(team.buzzStatus)">
        <template v-if="team.buzzStatus === 0"></template>
        <template v-else-if="team.buzzStatus === 1">已提交</template>
        <template v-else>{{ team.answer }}</template>
      </div>

      <!-- 分数（scoreShow 控制） -->
      <div v-if="cfg.scoreShow" class="team-score" :style="teamScoreStyle">
        {{ team.teamScore }}
      </div>
    </div>
  </div>
</template>

<script>
import baseUrl from "@/config";
import { model6BoardStyle, model6BoardData } from "@/api";

export default {
  name: "AnswerScore",

  data() {
    return {
      teamList: [],

      cfg: {
        row: 5, col: 6,
        x: 0, y: 0,
        cspace: 10, rspace: 10,
        borderW: 150, borderL: 100,   // 格子宽、格子高（px）
        teamFontsize: 16, teamFontWidth: 400,
        answerFontsize: 36, answerFontWidth: 700,
        backColor:     "#FFFFFF",
        waitColor:     "#333333",
        teamColor:     "#333333",
        answerColor:   "#FFFFFF",
        answeredColor: "#4A90D9",
        rightColor:    "#27AE60",
        errorColor:    "#E74C3C",
        scoreShow: false,
        webSite: "",
      },

      pollTimer: null,
    };
  },

  computed: {
    displayList() {
      return this.teamList.slice(0, this.cfg.row * this.cfg.col);
    },

    containerBoxStyle() {
      // 容器大小 = 起始偏移 + 所有格子 + 所有间距
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
        left:         0,
        width:        "100%",
        textAlign:    "center",
        fontSize:     this.cfg.teamFontsize + "px",
        fontWeight:   this.cfg.teamFontWidth,
        color:        this.cfg.teamColor,
        zIndex:       10,
        whiteSpace:   "nowrap",
        overflow:     "hidden",
        textOverflow: "ellipsis",
        padding:      "0 4px",
        boxSizing:    "border-box",
      };
    },

    teamScoreStyle() {
      return {
        position:  "absolute",
        bottom:    "6px",
        left:      0,
        width:     "100%",
        textAlign: "center",
        fontSize:  this.cfg.teamFontsize + "px",
        color:     this.cfg.teamColor,
        zIndex:    10,
      };
    },
  },

  methods: {
    // 第 index 个格子的 left（px）
    cellLeft(index) {
      const col = index % this.cfg.col;
      return this.cfg.x + col * (this.cfg.borderW + this.cfg.cspace);
    },

    // 第 index 个格子的 top（px）
    cellTop(index) {
      const row = Math.floor(index / this.cfg.col);
      return this.cfg.y + row * (this.cfg.borderL + this.cfg.rspace);
    },

    scoreItemStyle(team, index) {
      return {
        position:        "absolute",
        left:            this.cellLeft(index) + "px",
        top:             this.cellTop(index)  + "px",
        width:           this.cfg.borderW + "px",   // 格子宽
        height:          this.cfg.borderL + "px",   // 格子高
        overflow:        "hidden",
        backgroundColor: this.getStatusBgColor(team.buzzStatus),
        backgroundImage: `url(${this.getMediaUrl(team.buzzStatus)})`,
        backgroundSize:  "100% 100%",
        boxSizing:       "border-box",
        transition:      "background-color 0.3s ease",
      };
    },

    answerContentStyle(buzzStatus) {
      const isWait = buzzStatus === 0;
      return {
        position:   "absolute",
        top:        "50%",
        left:       0,
        transform:  "translateY(-50%)",
        width:      "100%",
        textAlign:  "center",
        zIndex:     10,
        fontSize:   (isWait ? this.cfg.teamFontsize : this.cfg.answerFontsize) + "px",
        fontWeight: this.cfg.answerFontWidth,
        color:      isWait ? this.cfg.waitColor : this.cfg.answerColor,
      };
    },

    getStatusBgColor(buzzStatus) {
      switch (buzzStatus) {
        case 1:  return this.cfg.answeredColor;
        case 2:  return this.cfg.rightColor;
        case 3:  return this.cfg.errorColor;
        default: return this.cfg.backColor;
      }
    },

    getMediaUrl(buzzStatus) {
      switch (buzzStatus) {
        case 1:  return `${baseUrl}/resource/已提交背景.jpg`;
        case 2:  return `${baseUrl}/resource/正确背景.jpg`;
        case 3:  return `${baseUrl}/resource/错误背景.jpg`;
        default: return `${baseUrl}/resource/等待背景.jpg`;
      }
    },

    // ===== 配置加载 =====
    loadConfig() {
      model6BoardStyle()
          .then(res => this.applyConfig(res.data))
          .catch(err => console.error("[Config] 加载失败", err));
    },

    applyConfig(data) {
      if (!data) return;
      const num = (v, def) => (v !== undefined && v !== null && v !== "" ? parseInt(v) : def);
      const str = (v, def) => (v !== undefined && v !== null && v !== "" ? v : def);

      this.cfg.row             = num(data.row,             this.cfg.row);
      this.cfg.col             = num(data.col,             this.cfg.col);
      this.cfg.x               = num(data.x,               this.cfg.x);
      this.cfg.y               = num(data.y,               this.cfg.y);
      this.cfg.cspace          = num(data.cspace,           this.cfg.cspace);
      this.cfg.rspace          = num(data.rspace,           this.cfg.rspace);
      this.cfg.borderW         = num(data.borderW,          this.cfg.borderW);  // 格子宽
      this.cfg.borderL         = num(data.borderL,          this.cfg.borderL);  // 格子高
      this.cfg.teamFontsize    = num(data.teamFontsize,      this.cfg.teamFontsize);
      this.cfg.teamFontWidth   = num(data.teamFontWidth,     this.cfg.teamFontWidth);
      this.cfg.answerFontsize  = num(data.answerFontsize,    this.cfg.answerFontsize);
      this.cfg.answerFontWidth = num(data.answerFontWidth,   this.cfg.answerFontWidth);
      this.cfg.backColor       = str(data.backColor,         this.cfg.backColor);
      this.cfg.waitColor       = str(data.waitColor,         this.cfg.waitColor);
      this.cfg.teamColor       = str(data.teamColor,         this.cfg.teamColor);
      this.cfg.answerColor     = str(data.answerColor,       this.cfg.answerColor);
      this.cfg.answeredColor   = str(data.answeredColor,     this.cfg.answeredColor);
      this.cfg.rightColor      = str(data.rightColor,        this.cfg.rightColor);
      this.cfg.errorColor      = str(data.errorColor,        this.cfg.errorColor);
      this.cfg.scoreShow       = !!data.scoreShow;
      this.cfg.webSite         = str(data.webSite,           this.cfg.webSite);
    },

    // ===== 队伍数据 =====
    fetchTeams() {

      this.loadConfig()

      model6BoardData()
          .then(res => { this.teamList = res.data || []; })
          .catch(err => console.error("[Teams] 拉取失败", err));
    },

    startPoll() {
      if (this.pollTimer) return;
      this.pollTimer = setInterval(() => this.fetchTeams(), 1000);
    },

    stopPoll() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null; }
    },
  },

  mounted() {
    sessionStorage.setItem("role", "score");
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

.score-item { position: absolute; }

.team-name, .answer-content, .team-score {
  position: absolute;
  z-index: 10;
}
</style>