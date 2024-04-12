<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>

export default {
  name: 'App',
  
  mounted() {

    this.$websocket.connect(() => {
          console.log("success")
          this.$websocket.subscribe('/topic/wait', (message) => {
            console.log(message)
            this.$router.push({name: "wait"});
          })
        },
        () => console.log("error")
    )


    // const intervalID = setInterval(() => {
    //   axios.get('http://localhost:8080/rest/heartbeat').then(() => {
    //
    //   }).catch(err => {
    //     console.log(err)
    //     sessionStorage.clear()
    //     location.reload()
    //     clearInterval(intervalID)
    //   })
    // }, 1000);
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
