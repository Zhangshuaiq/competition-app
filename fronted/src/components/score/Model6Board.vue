<!-- 答题模式网页版 -->
<template>
  <div class="page-wrapper" :style="pageWrapperStyle">
  <div id="container-box" :style="containerBoxStyle">
    <div
        v-for="(team, index) in displayList"
        :key="team.id || index"
        class="score-item"
        :style="scoreItemStyle(team, index)"
    >
      <!-- 队伍名称区：只受"团队设置"控制，永远不随作答状态变色（透明，显示页面背景） -->
      <div class="team-name" :style="teamNameStyle">
        {{ team.fullName }}
      </div>

      <!-- 答案/内容区：唯一随状态变色的区域，字体/颜色统一用"答案设置" -->
      <div class="answer-content" :style="answerContentStyle(team)">
        <!-- 非答题模式(phase1)：展示队伍总分 -->
        <template v-if="team.phase === 1">{{ team.teamScore }}</template>
        <!-- 进入答题后未提交、等待、公布未作答：答案区不显示内容 -->
        <template v-else-if="team.buzzStatus === 0"></template>
        <!-- 已提交 -->
        <template v-else-if="team.buzzStatus === 1">已提交</template>
        <!-- 答案公布：展示队伍提交的答案文字 -->
        <template v-else>{{ team.answer }}</template>
      </div>

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

      imageUrl:        `${baseUrl}/resource/答题面板背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,

      pollTimer: null,
    };
  },

  computed: {
    displayList() {
      return this.teamList.slice(0, this.cfg.row * this.cfg.col);
    },

    pageWrapperStyle() {
      return {
        width:           "100vw",
        height:          "100vh",
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize:  "100vw 100vh",
        position:        "relative",
        overflow:        "hidden",
      };
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
      // 队名区：占格子上部(28%)，用普通格底色(backColor)，只受"团队设置"控制，不随作答状态变色
      return {
        position:      "absolute",
        top:           0,
        left:          0,
        width:         "100%",
        height:        "28%",
        display:       "flex",
        alignItems:    "center",
        justifyContent:"center",
        textAlign:     "center",
        fontSize:      this.cfg.teamFontsize + "px",
        fontWeight:    this.cfg.teamFontWidth,
        color:         this.cfg.teamColor,
        backgroundColor: this.cfg.backColor,
        borderBottom:  "1px solid rgba(0, 0, 0, 0.25)",
        zIndex:        10,
        whiteSpace:    "nowrap",
        overflow:      "hidden",
        textOverflow:  "ellipsis",
        padding:       "0 4px",
        boxSizing:     "border-box",
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
      // 整格：仅保留装饰性状态背景图（铺满整格不动），不再在整格上染状态纯色，
      // 状态纯色只染下方"答案区"，队名区保持透明。
      return {
        position:        "absolute",
        left:            this.cellLeft(index) + "px",
        top:             this.cellTop(index)  + "px",
        width:           this.cfg.borderW + "px",   // 格子宽
        height:          this.cfg.borderL + "px",   // 格子高
        overflow:        "hidden",
        backgroundImage: `url(${this.getMediaUrl(team.buzzStatus)})`,
        backgroundSize:  "100% 100%",
        boxSizing:       "border-box",
        borderRadius:    "6px",
        boxShadow:       "0 2px 6px rgba(0, 0, 0, 0.25)",
      };
    },

    answerContentStyle(team) {
      // 答案区：占格子下半部，是唯一随状态变色的区域。
      // 字体/颜色统一用"答案设置"（answerColor/answerFontsize），与团队设置解耦。
      // phase1 展示总分时同样使用答案设置，仅背景保持普通格底色。
      return {
        position:        "absolute",
        top:             "28%",
        left:            0,
        width:           "100%",
        height:          "72%",
        display:         "flex",
        alignItems:      "center",
        justifyContent:  "center",
        zIndex:          10,
        fontSize:        this.cfg.answerFontsize + "px",
        fontWeight:      this.cfg.answerFontWidth,
        color:           this.cfg.answerColor,
        backgroundColor: this.getStatusBgColor(team.buzzStatus),
        transition:      "background-color 0.3s ease",
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

    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload  = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
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
html, body { overflow: hidden; height: 100%; }

.score-item { position: absolute; }

.team-name, .answer-content {
  position: absolute;
  z-index: 10;
}
</style>