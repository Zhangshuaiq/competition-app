<template>
  <div class="team-ranking-app" :style="boardStyle">
    <div class="team-leaderboard" :style="containerStyle">
      <div class="team-leaderboard-header">{{title}}</div>

      <div class="team-leaderboard-header-row" :style="headerStyle">
        <span class="team-header-rank">{{col1}}</span>
        <span class="team-header-name">{{col2}}</span>
        <span class="team-header-score">{{col3}}</span>
      </div>

      <ul class="team-leaderboard-list">
        <li
            v-for="(item, index) in displayedData"
            :key="item.name"
            :style="rowStyle(item)"
            :class="[
            'team-leaderboard-item',
            { 'team-rank-1': item.currentRank === 1 },
            { 'team-rank-2': item.currentRank === 2 },
            { 'team-rank-3': item.currentRank === 3 },
            { 'team-valid-name-length': item.name.length === 9 },
            item.trendClass
          ]"
        >
          <span class="team-rank" :id="index">{{ item.currentRank }}</span>
          <span class="team-name">{{ item.name }}</span>
          <span class="team-score">
            {{ item.score.toLocaleString() }}
            <i
                v-if="item.trend !== 'same'"
                :class="['fas', item.trendIcon, 'team-trend-icon', item.trend]"
            ></i>
          </span>
        </li>
      </ul>

    </div>
  </div>
</template>

<script>
import {rankList, rankTitle, teamRankStyle} from "@/api";
import baseUrl from "@/config";

export default {
  name: 'TeamRankingBoard',
  data() {
    return {
      title: "排行榜",
      col1: "名次",
      col2: "名称",
      col3: "分数",
      viewportWidth: window.innerWidth,
      viewportHeight: window.innerHeight,
      styleConfig: {},
      rankingData: [],
      refreshInterval: null,
      currentPage: 1,
      pageSize: 12,
      backgroundImage:`${baseUrl}/resource/团队排名大屏背景.jpg?${new Date().getTime()}`,
      fallbackBackgrounds: []
    };
  },
  computed: {
    boardStyle() {
      return {
        backgroundImage: this.backgroundImage ? `url(${this.backgroundImage})` : undefined,
        '--team-board-scale': Math.min(this.viewportWidth / 1920, this.viewportHeight / 1080),
        '--team-title-font-size': `${this.styleConfig.titleFontSize || 42}px`,
        '--team-header-font-size': `${this.styleConfig.headerFontSize || 22}px`,
        '--team-row-font-size': `${this.styleConfig.rowFontSize || 22}px`
      };
    },
    containerStyle() {
      const config = this.styleConfig;
      const scale = Math.min(this.viewportWidth / 1920, this.viewportHeight / 1080);
      const autoCenter = config.autoCenter !== false;
      return {
        backgroundColor: config.showContainerBackground ? config.containerBackground || '#ffffff' : 'transparent',
        left: `${autoCenter ? (this.viewportWidth - 1200 * scale) / 2 : Number(config.contentX) || 0}px`,
        top: `${autoCenter ? (this.viewportHeight - 950 * scale) / 2 : Number(config.contentY) || 0}px`
      };
    },
    headerStyle() {
      return {
        backgroundColor: this.styleConfig.showRowBackground === false ? 'transparent' : this.styleConfig.headerBackground || '#ffffff',
        color: this.styleConfig.headerColor || '#000000'
      };
    },
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
    updateViewport() {
      this.viewportWidth = window.innerWidth;
      this.viewportHeight = window.innerHeight;
    },
    rowStyle(item) {
      const config = this.styleConfig;
      const colors = config.rowColors || ['#ff0000', '#0000ff', '#ffff00'];
      const background = (item.currentRank >= 1 && item.currentRank <= 3 ? colors[item.currentRank - 1] : null) || config.rowBackground || '#ffffff';
      const visible = config.showRowBackground !== false;
      const hex = background.replace('#', '');
      const rgb = [0, 2, 4].map(offset => parseInt(hex.slice(offset, offset + 2), 16));
      const brightness = (rgb[0] * 299 + rgb[1] * 587 + rgb[2] * 114) / 1000;
      return {
        backgroundColor: visible ? background : 'transparent',
        color: visible && brightness < 128 ? '#ffffff' : '#000000'
      };
    },
    async loadStyle() {
      try {
        const response = await teamRankStyle();
        this.styleConfig = response.data || {};
      } catch (error) {
        console.error('获取排行榜样式失败:', error);
      }
    },
    async generateRanking() {
      await this.loadStyle();
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
    window.addEventListener('resize', this.updateViewport);

    this.$el.setAttribute('tabindex', '0');
    this.$el.focus();
  },

  beforeUnmount() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
    window.removeEventListener('keydown', this.handleKeydown);
    window.removeEventListener('resize', this.updateViewport);
  }
};
</script>

<style scoped>
.team-ranking-app, .team-ranking-app * {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}


.team-ranking-app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  height: 100dvh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  outline: none;
}

.team-leaderboard {
  background-color: transparent;
  border-radius: 15px;
  width: 1200px;
  height: 950px;
  position: absolute;
  top: 0;
  left: 0;
  transform-origin: top left;
  transform: scale(var(--team-board-scale, 1));
  overflow: hidden;
  z-index: 1;
}

.team-leaderboard-header {
  background: transparent;
  color: #ffffff;
  padding: 20px;
  text-align: center;
  font-size: var(--team-title-font-size, 42px);
  font-weight: bold;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.team-leaderboard-header-row {
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

.team-ranking-app[style*="background-image"] .team-leaderboard-header-row {
  background-color: rgba(255, 255, 255, 0.95);
}

.team-leaderboard-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.team-leaderboard-item {
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

.team-leaderboard-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.team-rank {
  font-weight: bold;
  color: inherit;
  min-width: 80px;
  margin: 0 20px;
  text-align: center;
  font-size: var(--team-row-font-size, 22px);
}

.team-name {
  flex: 1;
  margin: 0 20px;
  font-weight: 700;
  text-align: center;
  color: inherit;
  font-size: var(--team-row-font-size, 22px);
}

.team-score {
  color: inherit;
  font-weight: bold;
  min-width: 120px;
  margin: 0 20px;
  text-align: right;
  font-size: var(--team-row-font-size, 22px);
}

.team-header-rank {
  min-width: 80px;
  margin: 0 20px;
  text-align: center;
  font-size: var(--team-row-font-size, 22px);
}

.team-header-name {
  flex: 1;
  margin: 0 20px;
  text-align: center;
  font-size: var(--team-row-font-size, 22px);
}

.team-header-score {
  min-width: 120px;
  margin: 0 20px;
  text-align: right;
  font-size: var(--team-row-font-size, 22px);
}

/* 9字符名称的特殊样式 */
.team-valid-name-length {
  position: relative;
}

.team-valid-name-length::after {
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
.team-trend-icon {
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

.team-header-rank, .team-header-name, .team-header-score {
  font-size: var(--team-header-font-size, 22px);
}
</style>
