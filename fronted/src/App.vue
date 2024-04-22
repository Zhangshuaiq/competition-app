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
      const targetRouteName = message.route;

      if (targetRouteName !== '') {
        // 如果消息包含数据和路由，更新 Vuex 状态
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
        //}
        //else {
        //  console.log('Attempted to navigate to the same route:', targetRouteName);
        //}
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
