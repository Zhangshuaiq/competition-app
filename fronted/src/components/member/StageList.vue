<template>
  <div class="container" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
    </div>
    <div style="margin-top: 10px; padding-left: 100px; font-size: 28px; font-weight: bold; color: #4d8af5;
text-shadow: -1px -1px 0 white, 1px -1px 0 white, -1px 1px 0 white, 1px 1px 0 white, -2px -2px 0 white, 2px -2px 0 white, -2px 2px 0 white, 2px 2px 0 white;">
      <el-row>
        <el-col :span="8">
          <div style="color: red;">
            <span v-if="second >= 0">时间: {{ second }} 秒</span>
            <span v-else style="opacity: 0;">占位文本</span>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="stageList">
      <el-row>
        <el-col
          :span="isOptional ? 6 : 12"
          style="text-align: center"
          v-for="item in stages"
          :key="item.id"
        >
          <div
            class="stage-item"
            :class="{
              'stage-item--selected': isOptional && selectedStageId === item.id,
              'stage-item--disabled': isOptional && (second <= 0 || submitted)
            }"
            @click="handleStageClick(item)"
          >
            {{ item.name }}
          </div>
        </el-col>

        <!-- 自选环节：提交按钮 -->
        <el-col v-if="isOptional" :span="6" style="text-align: center">
          <div
            class="stage-item stage-item--submit"
            :class="{ 'stage-item--submit-disabled': !selectedStageId || second <= 0 || submitted }"
            @click="handleSubmit"
          >
            {{ submitted ? '已提交' : '提交' }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import ScoreBoard from "@/components/member/ScoreBoard.vue";
import baseUrl from "@/config";
import { answerPrefix, selectStage } from "@/api";

export default {
  name: "StageList",
  components: { ScoreBoard },
  data() {
    return {
      imageUrl: `${baseUrl}/resource/环节背景.jpg?${new Date().getTime()}`,
      defaultImageUrl: `${baseUrl}/resource/默认背景.jpg?${new Date().getTime()}`,
      stages: [],
      stageType: null,
      selectedStageId: null,
      second: -1,
      submitted: false,
      subscription: null,
    };
  },

  computed: {
    containerBoxStyle() {
      return {
        backgroundImage: `url(${this.imageUrl})`,
        backgroundSize: "100vw 100vh",
        height: "100vh",
      };
    },
    currentRoute() {
      return this.$store.state.currentRoute;
    },
    additionalData() {
      return this.$store.state.data;
    },
    isOptional() {
      return this.stageType === 8;
    },
  },

  methods: {
    checkImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => callback(true);
      img.onerror = () => callback(false);
      img.src = url;
    },

    queryPrefix() {
      answerPrefix().then((res) => {
        sessionStorage.setItem("prefix", res.data);
      });
    },

    subscribeStageSelect() {
      this.subscription = this.$websocket.subscribe('/topic/stageSelect', (message) => {
        if (message.type === 'time') {
          const t = parseInt(message.data);
          this.second = t > 0 ? t : -1;
        } else if (message.type === 'stop') {
          this.second = -1;
        } else if (message.type === 'reset') {
          this.submitted = false;
          this.selectedStageId = null;
          sessionStorage.removeItem('selectedStage');
        }
      });
    },

    handleStageClick(item) {
      if (!this.isOptional) return;
      if (this.second <= 0 || this.submitted) return;
      this.selectedStageId = item.id;
    },

    handleSubmit() {
      if (!this.selectedStageId || this.second <= 0 || this.submitted) return;
      this.submitted = true;

      const selected = this.stages.find((s) => s.id === this.selectedStageId);
      const userStr = sessionStorage.getItem("user");
      const user = JSON.parse(userStr);
      const member = sessionStorage.getItem("member");

      selectStage({
        stage: selected.name,
        team: user.fullName,
        member: member,
      }).then((res) => {
        console.log("提交成功", res);
        sessionStorage.setItem('selectedStage', selected.name);
      }).catch((err) => {
        console.error("提交失败", err);
        this.submitted = false;
      });
    },
  },

  mounted() {
    this.checkImageUrl(this.imageUrl, (isValid) => {
      if (!isValid) {
        this.imageUrl = this.defaultImageUrl;
      }
    });

    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({ name: "contestant" });
      return;
    }
    const member = sessionStorage.getItem("member");
    const selectedStage = sessionStorage.getItem("selectedStage");

    sessionStorage.clear();
    this.queryPrefix();
    sessionStorage.setItem("member", member);
    sessionStorage.setItem("user", item);

    if (selectedStage) {
      sessionStorage.setItem("selectedStage", selectedStage);
    }

    const data = this.$store.state.data;
    console.log(data);
    this.stages = data.stages;
    this.stageType = data.stageType;

    if (selectedStage && this.stages) {
      const found = this.stages.find((s) => s.name === selectedStage);
      if (found) {
        this.submitted = true;
        this.selectedStageId = found.id;
      }
    }

    this.subscribeStageSelect();
  },
};
</script>

<style scoped>
.stageList {
  margin-top: 3vh;
  margin-left: 15vw;
  margin-right: 15vw;
  justify-content: center;
  align-items: center;
}

.stage-item {
  margin: 25px 15%;
  width: 70%;
  height: 60px;
  line-height: 60px;
  background-color: #c40000;
  border-radius: 10px;
  font-family: "微软雅黑";
  font-size: 24px;
  color: white;
  cursor: default;
  transition: background-color 0.2s, transform 0.1s;
  user-select: none;
}

/* 自选模式下环节可点击 */
.stage-item--selected {
  background-color: #ffffff;
  color: #c40000;
  border: 3px solid #c40000;
  cursor: pointer;
}

/* 自选模式下未选中时也要有 hover 效果 */
.isOptional .stage-item:hover {
  opacity: 0.85;
  cursor: pointer;
}

/* 时间结束时环节不可点击 */
.stage-item--disabled {
  opacity: 0.5;
  cursor: not-allowed !important;
}

/* 提交按钮 */
.stage-item--submit {
  background-color: #5b9bd5;
  cursor: pointer;
}

.stage-item--submit:hover {
  background-color: #4a8ac4;
}

.stage-item--submit-disabled {
  background-color: #a0b4c8;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>