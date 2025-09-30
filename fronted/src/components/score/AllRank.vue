<template>
  <div class="ranking-app">
    <div class="leaderboard">
      <div class="leaderboard-header">{{title}}</div>


      <div class="leaderboard-header-row">
        <span class="header-rank">{{col1}}</span>
        <span class="header-name">{{col2}}</span>
        <span class="header-score">{{col3}}</span>
      </div>

      <ul class="leaderboard-list">
        <li
            v-for="(item, index) in displayedData"
            :key="item.name"
            :class="[
            'leaderboard-item',
            { 'rank-1': item.currentRank === 1 },
            { 'rank-2': item.currentRank === 2 },
            { 'rank-3': item.currentRank === 3 },
            { 'valid-name-length': item.name.length === 9 },
            item.trendClass
          ]"
        >
          <span class="rank" :id="index">{{ item.currentRank }}</span>
          <span class="name">{{ item.name }}</span>
          <span class="score">
            {{ item.score.toLocaleString() }}
            <i
                v-if="item.trend !== 'same'"
                :class="['fas', item.trendIcon, 'trend-icon', item.trend]"
            ></i>
          </span>
        </li>
      </ul>

    </div>
  </div>
</template>

<script>
import {rankList, rankTitle} from "@/api";

export default {
  name: 'RankingBoard',
  data() {
    return {
      title: "排行榜",
      col1: "名次",
      col2: "名称",
      col3: "分数",
      rankingData: [],
      refreshInterval: null,
      currentPage: 1,
      pageSize: 12,
    };
  },
  computed: {
    lastRanking() {
      return JSON.parse(localStorage.getItem('lastRanking')) || [];
    },
    totalRecords() {
      return this.rankingData.length;
    },
    totalPages() {
      return Math.ceil(this.totalRecords / this.pageSize);
    },
    displayedData() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.rankingData.slice(start, end);
    }
  },
  methods: {
    async generateRanking() {
      try {
        // 获取标题配置
        const titleResponse = await rankTitle();
        this.title = titleResponse.data.title;
        this.col1 = titleResponse.data.col1;
        this.col2 = titleResponse.data.col2;
        this.col3 = titleResponse.data.col3;

        // 获取排名数据
        const rankResponse = await rankList();
        this.rankingData = rankResponse.data.map(item => ({
          name: item.name,
          score: item.score,
          currentRank: item.currentRank,
          previousRank: item.previousRank,
          trend: item.trend,
          trendIcon: item.trendIcon,
          trendClass: item.trendClass
        }));

        // 如果当前页超出了总页数，调整到最后一页
        if (this.currentPage > this.totalPages && this.totalPages > 0) {
          this.currentPage = this.totalPages;
        }
      } catch (error) {
        console.error('获取排名数据失败:', error);
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    previousPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    handleKeydown(event) {
      switch (event.key) {
        case 'ArrowUp':
          event.preventDefault();
          this.previousPage();
          break;
        case 'ArrowDown':
          event.preventDefault();
          this.nextPage();
          break;
        case 'Home':
          event.preventDefault();
          this.currentPage = 1;
          break;
        case 'End':
          event.preventDefault();
          this.currentPage = this.totalPages;
          break;
      }
    }
  },

  mounted() {
    sessionStorage.setItem("role", 'score');
    this.generateRanking();
    this.refreshInterval = setInterval(this.generateRanking, 3000);

    // 添加键盘事件监听
    window.addEventListener('keydown', this.handleKeydown);

    // 确保组件可以获得焦点
    this.$el.setAttribute('tabindex', '0');
    this.$el.focus();
  },

  beforeDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
    // 移除键盘事件监听
    window.removeEventListener('keydown', this.handleKeydown);
  }
};
</script>

<style scoped>
.ranking-app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  outline: none; /* 移除焦点outline */
}

.leaderboard {
  background-color: transparent;
  border-radius: 15px;
  width: 100%;
  max-width: 1000px;
  overflow: hidden;
}

.leaderboard-header {
  background: transparent;
  color: #ffffff;
  padding: 25px;
  text-align: center;
  font-size: 36px;
  font-weight: bold;
}

/* 分页信息样式 */
.pagination-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  margin: 8px 10px;
  font-size: 14px;
  color: #333;
}

.pagination-hint {
  font-style: italic;
  color: #666;
  font-size: 12px;
}

.leaderboard-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 80px;
  background-color: white;
  border-radius: 8px;
  margin: 8px 10px;
  font-weight: bold;
  color: #000000;
  min-height: 15px;
}

.leaderboard-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.leaderboard-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 80px;
  background-color: white;
  border-radius: 8px;
  margin: 8px 10px;
  transition: all 0.2s ease;
  min-height: 15px;
}

.leaderboard-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.rank {
  font-weight: bold;
  color: #000000;
  min-width: 80px;
  margin: 0 20px;
  text-align: center;
  font-size: 22px;
}

.name {
  flex: 2;
  margin: 0 40px;
  font-weight: 700;
  text-align: center;
  color: #000000;
  font-size: 22px;
}

.score {
  color: #000000;
  font-weight: bold;
  min-width: 120px;
  margin: 0 20px;
  text-align: right;
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.rank-1 {
  background-color: #ff0000 !important; /* 正红色底色 */
  border: 2px solid #cc0000;
}

.rank-2 {
  background-color: #0000ff !important; /* 正蓝色底色 */
  border: 2px solid #0000cc;
}

.rank-3 {
  background-color: #ffff00 !important; /* 正黄色底色 */
  border: 2px solid #cccc00;
}

.rank-1 .rank {
  color: #ffffff;
  font-size: 24px;
  font-weight: 800;
}

.rank-2 .rank {
  color: #ffffff;
  font-size: 24px;
  font-weight: 800;
}

.rank-3 .rank {
  color: #000000;
  font-size: 24px;
  font-weight: 800;
}

.rank-1 .name,
.rank-1 .score {
  color: #ffffff !important;
}

.rank-2 .name,
.rank-2 .score {
  color: #ffffff !important;
}

.rank-3 .name,
.rank-3 .score {
  color: #000000 !important;
}

.header-rank {
  min-width: 80px;
  margin: 0 20px;
  text-align: center;
  font-size: 22px;
}

.header-name {
  flex: 2;
  margin: 0 40px;
  text-align: center;
  font-size: 22px;
}

.header-score {
  min-width: 120px;
  margin: 0 20px;
  text-align: right;
  font-size: 22px;
}

/* 分页控制按钮样式 */
.pagination-controls {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px;
  margin-top: 10px;
}

.pagination-btn {
  padding: 10px 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border: 2px solid #667eea;
  border-radius: 8px;
  color: #333;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #667eea;
  color: white;
  transform: translateY(-2px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: rgba(255, 255, 255, 0.5);
}

/* 9字符名称的特殊样式 */
.valid-name-length {
  position: relative;
}

.valid-name-length::after {
  content: '✓';
  position: absolute;
  top: 50%;
  right: -25px;
  transform: translateY(-50%);
  color: #28a745;
  font-weight: bold;
  font-size: 16px;
  background-color: rgba(40, 167, 69, 0.1);
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: translateY(-50%) scale(1);
    box-shadow: 0 0 0 0 rgba(40, 167, 69, 0.4);
  }
  70% {
    transform: translateY(-50%) scale(1.05);
    box-shadow: 0 0 0 10px rgba(40, 167, 69, 0);
  }
  100% {
    transform: translateY(-50%) scale(1);
    box-shadow: 0 0 0 0 rgba(40, 167, 69, 0);
  }
}

/* 趋势图标样式 */
.trend-icon {
  font-size: 0.9em;
}

.up {
  color: #28a745;
}

.down {
  color: #dc3545;
}

/* 动画效果 */
.row-up {
  animation: rowUp 0.8s ease-out;
}

.row-down {
  animation: rowDown 0.8s ease-out;
}

@keyframes rowUp {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

@keyframes rowDown {
  0% { transform: scale(1); }
  50% { transform: scale(0.95); }
  100% { transform: scale(1); }
}
</style>