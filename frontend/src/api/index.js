import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

const ERROR_MESSAGES = {
  400: '参数错误',
  401: '请先登录',
  403: '权限不足',
  404: '资源不存在',
  409: '操作冲突',
  500: '服务器内部错误',
}

api.interceptors.request.use((config) => {
  const user = useUserStore()
  if (user.token) config.headers.Authorization = `Bearer ${user.token}`
  return config
})

api.interceptors.response.use(
  (res) => {
    const code = res.data?.code
    if (code !== undefined && code !== 200) {
      const message = res.data?.message || ERROR_MESSAGES[code] || '请求失败'
      ElMessage.error(message)
      if (code === 401) {
        handleUnauthorized()
      }
      return Promise.reject(new Error(message))
    }
    return res
  },
  (err) => {
    const status = err.response?.status
    const message = err.response?.data?.message || ERROR_MESSAGES[status] || err.message || '网络错误'

    ElMessage.error(message)

    if (status === 401) {
      handleUnauthorized()
    }

    return Promise.reject(err)
  }
)

function handleUnauthorized() {
  const user = useUserStore()
  user.logout()
  const currentPath = router.currentRoute.value.fullPath
  router.push({ name: 'Login', query: { redirect: currentPath } })
}

export default api
