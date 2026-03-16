<template>
  <div class="tags-view-container">
    <span style="margin-left: 20px;font-size: 18px;"><strong v-if="!first">{{ routeName }}</strong></span>
  </div>
</template>

<script>
export default {
  computed: {  
    routeName() {  
      return this.$route.meta.title;  
    },
    first(){
      return this.$route.path == '/dashboard';
    }  
  }
}
</script>

<style lang="scss" scoped>
  .tags-view-container {
    height: 40px;
    width: 100%;
    background: #fff;
    margin-top: 1px;
    span {  
      line-height: 45px;
    }  
  }
</style>
