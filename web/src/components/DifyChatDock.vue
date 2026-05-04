<template>
  <div class="dify-chat-dock">
    <transition name="fade">
      <div v-show="panelOpen" class="dock-panel">
        <div class="dock-header">
          <span class="dock-title">birdme</span>
          <button type="button" class="dock-icon-btn" title="Close" @click="panelOpen = false">×</button>
        </div>
        <div ref="scrollRef" class="dock-messages">
          <div v-if="!messages.length" class="dock-empty">Ask anything. Replies may include navigation tags like [/knowledge].</div>
          <div
            v-for="(m, idx) in messages"
            :key="idx"
            class="dock-row"
            :class="m.role === 'user' ? 'is-user' : 'is-assistant'"
          >
            <div class="dock-bubble">{{ m.text }}</div>
          </div>
        </div>
        <div class="dock-input-row">
          <el-input
            v-model="draft"
            type="textarea"
            :rows="2"
            placeholder="Talk to birdme"
            resize="none"
            @keyup.enter.native.exact.prevent="send"
          />
          <el-button type="primary" class="dock-send" :loading="loading" @click="send">Send</el-button>
        </div>
      </div>
    </transition>
    <button
      type="button"
      class="dock-fab"
      aria-label="Open chat"
      @click="panelOpen = !panelOpen"
    >
      <span v-if="!panelOpen">💬</span>
      <span v-else>▼</span>
    </button>
  </div>
</template>

<script>
import { sendDifyChatAndNavigate } from '@/api/dify'

export default {
  name: 'DifyChatDock',
  data() {
    return {
      panelOpen: false,
      draft: '',
      loading: false,
      conversationId: '',
      messages: []
    }
  },
  methods: {
    async send() {
      const query = (this.draft || '').trim()
      if (!query || this.loading) return

      this.messages.push({ role: 'user', text: query })
      this.draft = ''
      this.loading = true
      this.$nextTick(this.scrollToBottom)

      try {
        const res = await sendDifyChatAndNavigate(
          { query, conversationId: this.conversationId || undefined },
          this.$router
        )

        if (res.code === 200 && res.data) {
          if (res.data.conversationId) {
            this.conversationId = res.data.conversationId
          }
          const answer = typeof res.data.answer === 'string' ? res.data.answer : ''
          this.messages.push({ role: 'assistant', text: answer || '(empty reply)' })
        } else if (res && res.msg) {
          this.$message.error(res.msg)
        }
      } catch (e) {
        if (!e || e.message !== 'Unauthorized') {
          this.$message.error(e && e.message ? e.message : 'Request failed')
        }
      } finally {
        this.loading = false
        this.$nextTick(this.scrollToBottom)
      }
    },
    scrollToBottom() {
      const el = this.$refs.scrollRef
      if (el) {
        el.scrollTop = el.scrollHeight
      }
    }
  }
}
</script>

<style scoped lang="scss">
.dify-chat-dock {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 9998;
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
}

.dock-fab {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: #1c64f2;
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(28, 100, 242, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 10px 28px rgba(28, 100, 242, 0.55);
  }
}

.dock-panel {
  position: absolute;
  right: 0;
  bottom: 72px;
  width: 24rem;
  max-width: calc(100vw - 40px);
  height: 40rem;
  max-height: calc(100vh - 120px);
  background: #f3f6fb;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dock-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: linear-gradient(90deg, #1c64f2, #3b82f6);
  color: #fff;
  font-weight: 600;
}

.dock-icon-btn {
  border: none;
  background: transparent;
  color: #fff;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  padding: 0 4px;
  opacity: 0.9;

  &:hover {
    opacity: 1;
  }
}

.dock-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.dock-empty {
  font-size: 13px;
  color: #64748b;
  padding: 8px;
  line-height: 1.5;
}

.dock-row {
  display: flex;
  margin-bottom: 10px;

  &.is-user {
    justify-content: flex-end;
  }

  &.is-assistant {
    justify-content: flex-start;
  }
}

.dock-bubble {
  max-width: 85%;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.45;
  white-space: pre-wrap;
  word-break: break-word;
}

.is-user .dock-bubble {
  background: #1c64f2;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.is-assistant .dock-bubble {
  background: #fff;
  color: #0f172a;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
}

.dock-input-row {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding: 10px;
  background: #fff;
  border-top: 1px solid #e2e8f0;
}

.dock-input-row ::v-deep .el-textarea__inner {
  font-size: 14px;
}

.dock-send {
  flex-shrink: 0;
  align-self: stretch;
  height: auto;
  min-height: 54px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 480px) {
  .dock-panel {
    width: calc(100vw - 32px);
    right: -8px;
    height: 70vh;
    max-height: 70vh;
  }
}
</style>
