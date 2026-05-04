<template>
  <div id="app">
    <router-view />
    <dify-chat-dock />
  </div>
</template>

<script>
import DifyChatDock from '@/components/DifyChatDock.vue'

export default {
  name: 'App',
  components: {
    DifyChatDock
  }
}
</script>
