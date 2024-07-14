<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',

  mounted() {
    
    
    this.$websocket.subscribe('/topic/page', (message) => {
      console.log(message.route);
      //const currentRouteName = this.$router.currentRoute.name;
      let targetRouteName = message.route;
      const role = sessionStorage.getItem('role');
      if (targetRouteName !== '') {
        // 竞技模式的情况下评委需要看到的数据
        if (targetRouteName === 'hmodel7' && role && role === 'holder') {
          this.$store.commit('setStage', message.stage);
          this.$store.commit('setCurrentRoute', targetRouteName);
          this.$store.commit('setAdditionalData', message.data); // 假设 message.data 包含需要的额外数据
          this.$router.push(
              {
                name: targetRouteName,
                query: {t: Date.now()}
              }
          );
        } else {
          
          if (targetRouteName === 'hclue' && role && role !== 'holder') {
            return
          }
          if (targetRouteName === 'clue' && role && role === 'holder') {
            return
          }
          
          if (role && role === 'holder' && !targetRouteName.startsWith('h')) {
            targetRouteName = 'h' + targetRouteName
          }
          
          if (targetRouteName !== 'hmodel7') {
            // 如果消息包含数据和路由，更新 Vuex 状态
            this.$store.commit('setStage', message.stage);
            this.$store.commit('setCurrentRoute', targetRouteName);
            this.$store.commit('setAdditionalData', message.data); // 假设 message.data 包含需要的额外数据
            // 检查当前路由名称与目标路由名称是否相同
            //if (targetRouteName !== currentRouteName) {
            this.$router.push(
                {
                  name: targetRouteName,
                  query: {t: Date.now()}
                }
            );
          }
        }
      }

      this.$store.commit('setTeamsData', message.teams)
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
</style>
