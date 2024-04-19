import Vue from 'vue'
import App from './App.vue'
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import WebSocketPlugin from './plugins/websocket';
import TimerPlugin from './plugins/timerPlugin';
import store from './store';


Vue.config.productionTip = false

Vue.use(ElementUI);
const host = window.location.hostname;
const port = window.location.port ? `:${window.location.port}` : ':8080';
// 构建WebSocket的URL
const wsUrl = `http://${host}${port}/ws`;
//const wsUrl = `http://localhost:8080/ws`;
Vue.use(WebSocketPlugin, { url: wsUrl });
Vue.use(TimerPlugin, {});


new Vue({
  el: '#app',
  store,
  router, // 使用路由配置
  render: h => h(App)
});
