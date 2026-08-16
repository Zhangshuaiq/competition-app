import { createApp } from 'vue'
import App from './App.vue'
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import WebSocketPlugin from './plugins/websocket';
import TimerPlugin from './plugins/timerPlugin';
import store from './store';
import baseUrl from "@/config";

const app = createApp(App)

const wsUrl = `${baseUrl}/ws`;
const healthUrl = `${baseUrl}/actuator/health`;

app.use(store)
   .use(router)
   .use(ElementPlus)
   .use(WebSocketPlugin, {
     url: wsUrl,
     healthCheckUrl: healthUrl // 使用自定义健康检查
   })
   .use(TimerPlugin, {});

app.mount('#app');
