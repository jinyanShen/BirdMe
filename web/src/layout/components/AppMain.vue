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
  width: 100%;
  max-width: calc(100vw - 280px);
  margin-left: 30px;
  margin-top: 20px;
  background-color: #fff;
  box-sizing: border-box;
  overflow-x: hidden;
}
</style>