<template>
  <div class="ranking-app" :style="backgroundImage ? { backgroundImage: `url(${backgroundImage})` } : {}">
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
import baseUrl from "@/config";

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
      backgroundImage:`${baseUrl}/resource/团队排名大屏背景.jpg?${new Date().getTime()}`,
      fallbackBackgrounds: []
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
        const titleResponse = await rankTitle();
        this.title = titleResponse.data.title;
        this.col1 = titleResponse.data.col1;
        this.col2 = titleResponse.data.col2;
        this.col3 = titleResponse.data.col3;

        if (titleResponse.data.backgroundImage) {
          this.backgroundImage = titleResponse.data.backgroundImage;
        }

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

        if (this.currentPage > this.totalPages && this.totalPages > 0) {
          this.currentPage = this.totalPages;
        }
      } catch (error) {
        console.error('获取排名数据失败:', error);
      }
    },

    preloadBackgroundImage() {
      if (!this.backgroundImage) {
        return;
      }

      const img = new Image();
      let currentIndex = 0;

      const tryLoadImage = (url) => {
        img.src = url;
        img.onload = () => {
          console.log('背景图片加载成功:', url);
        };
        img.onerror = () => {
          console.warn('背景图片加载失败:', url);
          currentIndex++;
          if (currentIndex < this.fallbackBackgrounds.length) {
            this.backgroundImage = this.fallbackBackgrounds[currentIndex];
            tryLoadImage(this.fallbackBackgrounds[currentIndex]);
          } else {
            console.error('所有背景图片加载失败,使用默认渐变背景');
            this.backgroundImage = '';
          }
        };
      };

      tryLoadImage(this.backgroundImage);
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
        case 'ArrowLeft':
          event.preventDefault();
          this.previousPage();
          break;
        case 'ArrowDown':
        case 'ArrowRight':
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

    this.preloadBackgroundImage();

    window.addEventListener('keydown', this.handleKeydown);

    this.$el.setAttribute('tabindex', '0');
    this.$el.focus();
  },

  beforeDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
    window.removeEventListener('keydown', this.handleKeydown);
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.ranking-app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  margin: 0;
  padding: 0;
  width: 1920px;
  height: 1080px;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  outline: none;
}

/* 当有背景图片时添加半透明遮罩层提高内容可读性 */
.ranking-app[style*="backgroundImage"]::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 0;
}

.leaderboard {
  background-color: transparent;
  border-radius: 15px;
  width: 1200px;
  height: 950px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  overflow: hidden;
  z-index: 1;
}

.leaderboard-header {
  background: transparent;
  color: #ffffff;
  padding: 20px;
  text-align: center;
  font-size: 42px;
  font-weight: bold;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.leaderboard-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 60px;
  background-color: white;
  border-radius: 8px;
  margin: 10px 15px;
  font-weight: bold;
  color: #000000;
  min-height: 20px;
  font-size: 24px;
}

.ranking-app[style*="backgroundImage"] .leaderboard-header-row {
  background-color: rgba(255, 255, 255, 0.95);
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
  padding: 10px 60px;
  background-color: white;
  border-radius: 8px;
  margin: 8px 15px;
  transition: all 0.2s ease;
  min-height: 20px;
  font-size: 20px;
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
  flex: 1;
  margin: 0 20px;
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
}

.rank-1 {
  background-color: #ff0000 !important;
  border: 2px solid #cc0000;
}

.rank-2 {
  background-color: #0000ff !important;
  border: 2px solid #0000cc;
}

.rank-3 {
  background-color: #ffff00 !important;
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
  flex: 1;
  margin: 0 20px;
  text-align: center;
  font-size: 22px;
}

.header-score {
  min-width: 120px;
  margin: 0 20px;
  text-align: right;
  font-size: 22px;
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
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes rowDown {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.95);
  }
  100% {
    transform: scale(1);
  }
}
</style>