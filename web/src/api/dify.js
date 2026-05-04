import request from '@/utils/request'

/**
 * 通过自家后端代理调用 Dify「发送对话消息」（blocking）。
 * @param {{ query: string, conversationId?: string, inputs?: object }} data
 */
export function sendDifyChatMessage(data) {
  return request({
    url: '/dify/chat-messages',
    method: 'post',
    data
  })
}

/** 从助手回复里解析形如 [/identification] 的路径 */
export function parseDifyNavPath(text) {
  if (!text || typeof text !== 'string') return null
  const m = text.match(/\[(\/[a-zA-Z0-9\/_-]+)\]/)
  return m ? m[1] : null
}

/**
 * 把 Dify 里约定的「路径标签」映射到真实跳转地址。
 */
const DIFY_NAV_BY_PATH = {
  '/knowledge': 'http://csi420-01-vm2.ucd.ie/knowledge/index'
}

export function resolveDifyNavUrl(path) {
  if (!path || typeof path !== 'string') return null
  if (DIFY_NAV_BY_PATH[path]) return DIFY_NAV_BY_PATH[path]
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  return path
}

/**
 * 根据助手完整回复执行跳转。
 * @param {string} answer 接口返回的 answer
 * @param {object} [router] Vue Router 实例
 */
export function navigateFromDifyAnswer(answer, router) {
  const path = parseDifyNavPath(answer)
  if (!path) return false
  const target = resolveDifyNavUrl(path)
  if (target.startsWith('http://') || target.startsWith('https://')) {
    window.location.href = target
    return true
  }
  if (router) {
    router.push(target)
    return true
  }
  window.location.href = target
  return true
}

/**
 * 发一条对话并尝试根据 answer 跳转。
 */
export async function sendDifyChatAndNavigate(payload, router) {
  const res = await sendDifyChatMessage(payload)
  if (res.code === 200 && res.data && typeof res.data.answer === 'string') {
    navigateFromDifyAnswer(res.data.answer, router)
  }
  return res
}
