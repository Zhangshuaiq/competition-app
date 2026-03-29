<template>
  <div class="container" :style="containerBoxStyle">
    <div>
      <ScoreBoard></ScoreBoard>
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
            :class="{ 'stage-item--selected': isOptional && selectedStageId === item.id }"
            @click="handleStageClick(item)"
          >
            {{ item.name }}
          </div>
        </el-col>

        <!-- 自选环节：提交按钮 -->
        <el-col v-if="isOptional" :span="6" style="text-align: center">
          <div
            class="stage-item stage-item--submit"
            :class="{ 'stage-item--submit-disabled': !selectedStageId }"
            @click="handleSubmit"
          >
            提交
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

    handleStageClick(item) {
      if (!this.isOptional) return;
      this.selectedStageId = item.id;
    },

    handleSubmit() {
      if (!this.selectedStageId) return;
      const selected = this.stages.find((s) => s.id === this.selectedStageId);
      
      const userStr = sessionStorage.getItem("user");
      const user = JSON.parse(userStr);
      const member = sessionStorage.getItem("member");

      selectStage({
        stage: selected.name,
        team: user.fullName,      // 根据你 user 对象的实际字段名调整
        member: member,
      }).then((res) => {
        console.log("提交成功", res);
        // 后续逻辑在这里处理
      }).catch((err) => {
        console.error("提交失败", err);
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

    sessionStorage.clear();
    this.queryPrefix();
    sessionStorage.setItem("member", member);
    sessionStorage.setItem("user", item);

    const data = this.$store.state.data;
    console.log(data);
    this.stages = data.stages;
    this.stageType = data.stageType;
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