<template>
  <section v-if="first" style="background-color: white;height: 800px;">
    <transition name="fade" mode="out-in">
      <keep-alive >
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
  <section v-else class="app-main">
    <transition name="fade" mode="out-in">
      <keep-alive >
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    key() {
      return this.$route.path
    },
    first(){
      return this.$route.path == '/dashboard';
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  width: 100%; /* 设置宽度为屏幕宽度的80% */  
  max-width: 1250px; /* 设置最大宽度为1200px，以防止在大屏幕上变得过大 */  
  margin-left: 30px;
  margin-top: 20px;
  background-color: #fff; /* 设置背景颜色为白色 */
}
</style>