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
    const member = sessionStorage.getItem("member");
    
    sessionStorage.clear()
    sessionStorage.setItem("member", member)
    sessionStorage.setItem("user", item)
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