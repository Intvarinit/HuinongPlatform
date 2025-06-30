import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/recovery/apply', name: 'RecoveryApply', component: () => import('../views/RecoveryApply.vue') },
  { path: '/recovery', name: 'RecoveryList', component: () => import('../views/RecoveryList.vue') },
  { path: '/recovery/finished', name: 'RecoveryFinished', component: () => import('../views/RecoveryFinished.vue') },
  { path: '/recovery/:id', name: 'RecoveryDetail', component: () => import('../views/RecoveryDetail.vue') },
  { path: '/recovery/start', name: 'RecoveryStart', component: () => import('../views/RecoveryStart.vue') },
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录自动跳转到登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
  } else {
    next()
  }
})

export default router 