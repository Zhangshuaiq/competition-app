const { defineConfig } = require('@vue/cli-service')

const backend = process.env.VUE_APP_BASE_URL || 'http://127.0.0.1:8080'
const proxy = {}
for (const route of ['/rest', '/resource', '/actuator', '/ws']) {
  proxy[route] = {
    target: backend,
    changeOrigin: true,
    ws: route === '/ws'
  }
}

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: '0.0.0.0',
    port: 8081,
    proxy
  }
})
