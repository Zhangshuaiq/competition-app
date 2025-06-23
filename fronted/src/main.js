import Vue from 'vue'
import App from './App.vue'
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import WebSocketPlugin from './plugins/websocket';
import TimerPlugin from './plugins/timerPlugin';
import store from './store';
import baseUrl from "@/config";

Vue.config.productionTip = false

Vue.use(ElementUI);

const wsUrl = `${baseUrl}/ws`;
const healthUrl = `${baseUrl}/actuator/health`;
Vue.use(WebSocketPlugin, {
  url: wsUrl,
  healthCheckUrl: healthUrl // 使用自定义健康检查
})
Vue.use(TimerPlugin, {});


new Vue({
  el: '#app',
  store,
  router, // 使用路由配置
  render: h => h(App),

  mounted() {
    console.log('应用已挂载');
    this.checkWebSocketConfig();
  },

  methods: {
    /**
     * 检查WebSocket配置是否正确
     */
    checkWebSocketConfig() {
      if (!this.$websocket) {
        console.error('❌ WebSocket插件未正确安装');
        return;
      }

      console.log('✅ WebSocket插件已安装');
      console.log('连接状态:', this.$websocket.isConnected());

      // 可选：显示连接状态
      const status = this.$websocket.getConnectionStatus();
      if (status) {
        console.log('详细状态:', status);
      }
    }
  }
});


