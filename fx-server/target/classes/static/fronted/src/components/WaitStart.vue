<template>
  <div id="container-box">

  </div>
</template>

<script>
export default {
  name: "WaitStart",

  mounted() {
    const item = sessionStorage.getItem("user");
    if (!item) {
      this.$router.push({name: "contestant"})
      return
    }

    const subscription = this.$websocket.subscribe('/topic/enter', (message) => {
      if (message.rule) {
        sessionStorage.setItem('rule', message.rule)
      }
      if (message.type === '7') {
        this.$router.push({name: 'model7'})
      } else if (message.type === '61') {
        this.$router.push({name: 'model61'})
      } else if (message.type === '60') {
        this.$router.push({name: 'model60'})
      }
      this.$websocket.unsubscribe(subscription)
    })
    
    const teamInfo = JSON.parse(item)
    this.$websocket.send('/app/enter', {team: teamInfo.fullName + "-" + sessionStorage.getItem("member")})
  },
}
</script>


<style scoped>
#container-box {
  background-image: url("~@/assets/waitstart.png");
  background-size: 100vw 100vh;
  height: 100vh;
}
</style>