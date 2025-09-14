<template>
  <div class="ranking-app">
    <div class="ranking-container">
      <div class="header-box">
        <h1 class="main-title">排行榜</h1>
      </div>
      
      <table>
        <thead>
          <tr>
            <th style="width: 25%">名次</th>
            <th style="width: 45%">名称</th>
            <th style="width: 30%">分数</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="(item, index) in rankingData" 
            :key="item.name"
            :class="['rank-' + (index + 1), item.trendClass]"
          >
            <td>
              <div class="rank-content">
                <i 
                  v-if="index < 3" 
                  :class="['fas', 'fa-trophy', 'medal-icon', medalColors[index]]"
                ></i>
                <div v-else class="medal-icon"></div>
                <span class="rank-number">{{ index + 1 }}</span>
              </div>
            </td>
            <td class="player-name">{{ item.name }}</td>
            <td>
              <span class="score-value">{{ item.score.toLocaleString() }}</span>
              <i 
                v-if="item.trend !== 'same'" 
                :class="['fas', item.trendIcon, 'trend-icon', item.trend]"
              ></i>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RankingBoard',
  data() {
    return {
      names: [
        '重庆代表队',
        '四川代表队', 
        '上海代表队',
        '浙江代表队',
        '新疆代表队',
        '江苏代表队',
        '天津代表队',
        '云南代表队',
        '贵州代表队',
        '西藏代表队'
      ],
      medalColors: ['gold', 'silver', 'bronze'],
      rankingData: [],
      refreshInterval: null
    };
  },
  computed: {
    lastRanking() {
      return JSON.parse(localStorage.getItem('lastRanking')) || [];
    }
  },
  methods: {
    generateRanking() {
      // 生成当前数据
      const currentData = this.names.map((name, index) => {
        console.log(index)
        const previousItem = this.lastRanking.find(item => item.name === name);
        return {
          name,
          score: Math.floor(Math.random() * 9000 + 1000),
          previousRank: previousItem ? previousItem.currentRank : 0
        };
      });

      // 排序
      currentData.sort((a, b) => b.score - a.score);
      
      // 添加当前排名和趋势信息
      this.rankingData = currentData.map((item, index) => {
        const currentRank = index + 1;
        let trend = 'same';
        let trendIcon = '';
        let trendClass = '';
        
        if (item.previousRank === 0) {
          trend = 'same';
        } else if (currentRank < item.previousRank) {
          trend = 'up';
          trendIcon = 'fa-arrow-up';
          trendClass = 'row-up';
        } else if (currentRank > item.previousRank) {
          trend = 'down';
          trendIcon = 'fa-arrow-down';
          trendClass = 'row-down';
        }
        
        return {
          ...item,
          currentRank,
          trend,
          trendIcon,
          trendClass
        };
      });

      // 更新本地存储
      localStorage.setItem('lastRanking', JSON.stringify(this.rankingData));
    }
  },
  mounted() {
    this.generateRanking();
    this.refreshInterval = setInterval(this.generateRanking, 3000);
  },
  beforeDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }
};
</script>

<style>
:root {
  --gold: #ffd700;
  --silver: #e0e0e0;
  --bronze: #cd7f32;
  --text-primary: #333333;
  --text-secondary: #555555;
  --text-tertiary: #777777;
  --up-green: #4CAF50;
  --down-red: #F44336;
  --highlight: #FFC107;
  --bg-primary: #f01b1b;
  --bg-secondary: #f01b1b;
}

.ranking-app {
  font-family: 'HarmonyOS Sans SC', 'Microsoft YaHei', sans-serif;
  background: url("~@/assets/红色主题.jpg");
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  margin: 0;
  padding: 20px;
}

.ranking-container {
  background: rgba(255,255,240,0.98);
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(139,0,0,0.3);
  width: 92%;
  max-width: 880px;
  overflow: hidden;
}

.header-box {
  padding: 32px 40px;
  background: linear-gradient(95deg, var(--bg-primary), var(--bg-secondary));
  border-bottom: 2px solid rgba(255,255,255,0.3);
  text-align: center;
}

.main-title {
  font-size: 3.2em;
  color: #ffffff;
  margin: 0;
  padding: 0 20px;
  letter-spacing: 5px;
  font-weight: 800;
  position: relative;
  display: inline-block;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.main-title:before {
  content: "";
  position: absolute;
  top: -5px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(to right, transparent, #ffd700, transparent);
}

.main-title:after {
  content: "";
  position: absolute;
  bottom: -5px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(to right, transparent, #ffd700, transparent);
}

@keyframes titleGlow {
  0% { 
    text-shadow: 0 4px 8px rgba(0,0,0,0.4),
                0 0 10px rgba(255,215,0,0.3);
  }
  50% { 
    text-shadow: 0 6px 12px rgba(0,0,0,0.5),
                0 0 20px rgba(255,215,0,0.6);
  }
  100% { 
    text-shadow: 0 4px 8px rgba(0,0,0,0.4),
                0 0 10px rgba(255,215,0,0.3);
  }
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  padding: 18px;
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 3px solid rgba(255, 140, 0, 0.3);
  background-color: rgba(255, 215, 0, 0.1);
}

td {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 140, 0, 0.2);
  vertical-align: middle;
  text-align: center;
  transition: all 0.3s ease;
}
td:hover {
  background-color: rgba(255, 215, 0, 0.1);
}

/* 名次列特殊样式 */
td:first-child {
  text-align: left;
  padding-left: 40px;
}

/* 奖牌颜色系统 */
.gold { color: var(--gold); }
.silver { color: var(--silver); }
.bronze { color: var(--bronze); }

/* 字体颜色系统 */
.player-name {
  color: var(--text-primary);
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(255, 140, 0, 0.3);
}

.score-value {
  color: var(--text-primary);
  font-weight: 800;
  font-size: 1.3em;
  text-shadow: 0 1px 2px rgba(255,255,255,0.5);
}

.rank-number {
  font-weight: 900;
  font-size: 1.5em;
  letter-spacing: 1px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  margin-right: 8px;
}
.rank-1 .rank-number { color: var(--gold); }
.rank-2 .rank-number { color: var(--silver); }
.rank-3 .rank-number { color: var(--bronze); }
.rank-4 .rank-number,
.rank-5 .rank-number,
.rank-6 .rank-number,
.rank-7 .rank-number,
.rank-8 .rank-number,
.rank-9 .rank-number,
.rank-10 .rank-number { color: #1565C0; }

.player-name {
  color: var(--text-primary);
  font-weight: 800;
  font-size: 1.3em;
  letter-spacing: 1px;
  text-shadow: 0 1px 2px rgba(255,255,255,0.5);
}

/* 趋势图标 */
.trend-icon {
  margin-left: 8px;
  font-size: 0.9em;
}
.up { color: var(--up-green); }
.down { color: var(--down-red); }

/* 对齐系统 */
.rank-content {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.medal-icon {
  font-size: 1.4em;
  min-width: 32px;
  text-align: center;
}

/* 动画效果 */
@keyframes medalGlow {
  0% { opacity: 0.8; }
  50% { opacity: 1; }
  100% { opacity: 0.8; }
}
@keyframes rowUp {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}
@keyframes rowDown {
  0% { transform: scale(1); }
  50% { transform: scale(0.9); }
  100% { transform: scale(1); }
}
.medal-icon {
  animation: medalGlow 2s ease-in-out infinite;
}
.rank-number {
  font-size: 1.2em;
  font-weight: 700;
}
.row-up {
  animation: rowUp 0.8s ease-out;
}
.row-down {
  animation: rowDown 0.8s ease-out;
}
</style>