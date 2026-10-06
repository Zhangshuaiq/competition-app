<template>
  <div class="connection-status" :class="{ 'connection-status-offline': !connected }"
       role="status" aria-live="polite">
    <span class="connection-dot" aria-hidden="true"></span>
    <span>状态：{{ statusText }}</span>
    <strong v-if="!connected" class="connection-warning">请刷新网页</strong>
  </div>
</template>

<script>
export default {
  name: 'ConnectionStatus',
  data() {
    return { status: 'disconnected' };
  },
  computed: {
    connected() {
      return this.status === 'connected';
    },
    statusText() {
      if (this.connected) return '已连接';
      if (this.status === 'connecting') return '连接中';
      if (this.status === 'reconnecting') return '重连中';
      return '未连接';
    },
  },
  mounted() {
    this.unsubscribeStatus = this.$websocket.onConnectionStatusChange(info => {
      this.status = info.status;
    });
  },
  beforeUnmount() {
    if (this.unsubscribeStatus) this.unsubscribeStatus();
  },
};
</script>

<style scoped>
.connection-status {
  position: fixed;
  right: 16px;
  bottom: 16px;
  z-index: 10001;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  max-width: calc(100vw - 64px);
  padding: 10px 14px;
  border: 2px solid #15803d;
  border-radius: 8px;
  background: #fff;
  color: #166534;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  pointer-events: none;
}
.connection-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #15803d;
}
.connection-status-offline {
  border-color: #b91c1c;
  background: #fff1f2;
  color: #b91c1c;
}
.connection-status-offline .connection-dot {
  background: #b91c1c;
}
.connection-warning {
  padding: 4px 8px;
  background: #b91c1c;
  color: #fff;
  font-size: 20px;
}
</style>
