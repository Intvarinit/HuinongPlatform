import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import Layout from '../components/Layout.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue')
      },
      {
        path: 'recovery',
        name: 'Recovery',
        component: () => import('../views/recovery/RecoveryList.vue')
      },
      {
        path: 'recovery/apply',
        name: 'RecoveryApply',
        component: () => import('../views/recovery/RecoveryApply.vue')
      },
      {
        path: 'recovery/detail/:id',
        name: 'RecoveryDetail',
        component: () => import('../views/recovery/RecoveryDetail.vue')
      },
      {
        path: 'compost',
        name: 'Compost',
        component: () => import('../views/compost/CompostList.vue')
      },
      {
        path: 'compost/create',
        name: 'CompostCreate',
        component: () => import('../views/compost/CompostCreate.vue')
      },
      {
        path: 'compost/detail/:id',
        name: 'CompostDetail',
        component: () => import('../views/compost/CompostDetail.vue')
      },
      {
        path: 'inspection',
        name: 'Inspection',
        component: () => import('../views/inspection/InspectionList.vue')
      },
      {
        path: 'inspection/create',
        name: 'InspectionCreate',
        component: () => import('../views/inspection/InspectionCreate.vue')
      },
      {
        path: 'inspection/detail/:id',
        name: 'InspectionDetail',
        component: () => import('../views/inspection/InspectionDetail.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue')
      },
      {
        path: 'admin/operation-log',
        name: 'OperationLog',
        component: () => import('../views/admin/OperationLog.vue'),
        meta: { requiresAuth: true, adminOnly: true }
      },
      {
        path: 'admin/user-management',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { requiresAuth: true, adminOnly: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router 