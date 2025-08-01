<template>
  <div class="fullscreen-button" @click="toggleFullscreen" :title="isFullscreen ? '退出全屏' : '全屏'">
    <!-- 全屏图标 -->
    <svg v-if="!isFullscreen" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
      <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
    </svg>
    <!-- 退出全屏图标 -->
    <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
      <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
    </svg>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isFullscreen: false,
    };
  },
  methods: {
    toggleFullscreen() {
      if (!this.isFullscreen) {
        document.documentElement.requestFullscreen().then(() => {
          this.isFullscreen = true;
        }).catch((err) => {
          console.error('Error entering fullscreen:', err);
        });
      } else {
        document.exitFullscreen().then(() => {
          this.isFullscreen = false;
        }).catch((err) => {
          console.error('Error exiting fullscreen:', err);
        });
      }
    },
  },
};
</script>

<style scoped>
.fullscreen-button {
  position: fixed;
  bottom: 60px;
  left: 60px;
  width: 44px;
  height: 44px;
  background-color: #00b050;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  transition: all 0.3s ease;
}

.fullscreen-button:hover {
  background-color: #008040;
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

.fullscreen-button:active {
  transform: scale(0.95);
}
</style>