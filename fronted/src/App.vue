<template>
  <div id="app">
    <FullscreenButton v-if="showGlobalHeader"/>
    <router-view :key="routerKey"></router-view>
    <div v-if="boardImageUrl" class="board-image-overlay">
      <img :src="boardImageUrl" class="board-image-full"/>
    </div>
  </div>
</template>

<script>
import FullscreenButton from "@/components/FullscreenButton.vue";
import baseUrl from "@/config";

export default {
  name: 'App',
  components: {
    FullscreenButton,
  },

  data() {
    return {
      routerKey: 0,
      boardImageUrl: null
    }
  },

  computed: {
    showGlobalHeader() {
      // 排行榜页面隐藏全局组件
      const hidden = ['allRank', 'allScore', 'teamScore', 'scoreLogin', 'allMemberRank']
      return hidden.indexOf(this.$route.name) <= -1;
    }
  },

  methods: {
    forceRefreshComponent() {
      // 通过改变 key 强制重新渲染组件
      this.routerKey += 1;
    }
  },
  mounted() {
    this.$websocket.connect(() => {
      this.$websocket.subscribe('/topic/boardImage', (message) => {
        if (message.url) {
          this.boardImageUrl = `${baseUrl}/rest/screenImage/${encodeURIComponent(message.url)}`;
        } else {
          this.boardImageUrl = null;
        }
      });

      this.$websocket.subscribe('/topic/page', (message) => {
        console.log(message.route);
        // 同步全屏图片状态
        if (message.boardImageUrl !== undefined) {
          if (message.boardImageUrl) {
            this.boardImageUrl = `${baseUrl}/rest/screenImage/${encodeURIComponent(message.boardImageUrl)}`;
          } else {
            this.boardImageUrl = null;
          }
        }
        let targetRouteName = message.route;
        const role = sessionStorage.getItem('role');
        if (role === 'score') {
          return;
        }
        if (targetRouteName !== '') {
          if (targetRouteName === 'hmodel7' && role && role === 'holder') {
            this.$store.commit('setStage', message.stage);
            this.$store.commit('setCurrentRoute', targetRouteName);
            this.$store.commit('setAdditionalData', message.data);
            this.$router.push({ name: targetRouteName, query: {t: Date.now()} });
          } else {
            if (role && role === 'holder' && !targetRouteName.startsWith('h')) {
              targetRouteName = 'h' + targetRouteName;
            }
            if (targetRouteName !== 'hmodel7') {
              this.$store.commit('setStage', message.stage);
              this.$store.commit('setCurrentRoute', targetRouteName);
              this.$store.commit('setAdditionalData', message.data);
              this.$router.push({ name: targetRouteName, query: {t: Date.now()} });
            }
          }
        }
        this.$store.commit('setTeamsData', message.teams);
        this.$store.commit('setBuzzStatus', message.buzzStatus);
        this.$store.commit('setBarStatus', message.barStatus);
      });
    });
  }


}
</script>

<style>
body, html, #app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0;
  padding: 0;
}

.board-image-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  background: #000;
}

.board-image-full {
  width: 100%;
  height: 100%;
  object-fit: fill;
}
</style>
