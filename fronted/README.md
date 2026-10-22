# fronted

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### 本机与局域网访问

开发页面统一使用 `http://服务器IP:8081/index.html#/allRank`，本机也可使用
`http://localhost:8081/index.html#/allRank`。API、背景资源和 WebSocket 由开发服务器
转发到 `.env.development` 的 `VUE_APP_BASE_URL`，该地址是开发服务器使用的后台地址。

`npm run build` 完成后自动将 `dist` 同步到 `fx-server/src/main/resources/frontend`。
随后重新打包并重启 Java 后端，8080 端口才会提供新版页面。只构建 `dist` 或只刷新
浏览器，无法更新已运行的旧 JAR。生产环境所有设备应访问同一台服务器的 8080 端口。
