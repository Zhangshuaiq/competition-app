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
Vue.use(WebSocketPlugin, { url: wsUrl });
Vue.use(TimerPlugin, {});


new Vue({
  el: '#app',
  store,
  router, // 使用路由配置
  render: h => h(App)
});
