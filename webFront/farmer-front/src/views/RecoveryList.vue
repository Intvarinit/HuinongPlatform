<template>
  <div class="recovery-list-bg">
    <div class="recovery-list">
      <van-nav-bar title="回收进度">
        <template #right>
          <van-button type="primary" size="small" plain @click="goStart" style="margin-right:8px;">开始回收</van-button>
          <van-button type="danger" size="small" plain @click="logout" style="margin-right:8px;">注销</van-button>
        </template>
      </van-nav-bar>
      <van-tabs v-model:active="active" @change="onTabChange">
        <van-tab title="全部" name="all" />
        <van-tab title="进行中" name="doing" />
        <van-tab title="已完成" name="done" />
      </van-tabs>
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadData">
        <van-cell v-for="item in list" :key="item.id" :title="item.cropType" :label="item.location + (item.createTime ? '  ' + (item.createTime.replace('T', ' ')) : '')" @click="goDetail(item.id)">
          <template #right-icon>
            <van-tag :type="item.status === 2 ? 'success' : 'primary'">
              {{ item.status === 0 ? '待审核' : item.status === 1 ? '已通过' : item.status === 2 ? '已完成' : item.status === 3 ? '已拒绝' : '未知' }}
            </van-tag>
          </template>
        </van-cell>
      </van-list>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { recoveryApi } from '../api/recovery'

const active = ref('all')
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10
const router = useRouter()

const statusMap = {
  all: undefined,
  doing: 1,
  done: 2
}

const onTabChange = () => {
  page.value = 1
  finished.value = false
  list.value = []
  loadData()
}

const loadData = async () => {
  if (loading.value) return
  loading.value = true
  const res = await recoveryApi.list({
    pageNum: page.value,
    pageSize,
    status: statusMap[active.value]
  })
  if (res.code === 200) {
    if (page.value === 1) list.value = []
    list.value = list.value.concat(res.data.records || [])
    finished.value = res.data.records.length < pageSize
    page.value++
  }
  loading.value = false
}

const goDetail = (id) => {
  router.push(`/recovery/${id}`)
}

const logout = () => {
  localStorage.removeItem('token')
  router.replace('/login')
}

const goStart = () => {
  router.push('/recovery/start')
}

onMounted(() => {
  page.value = 1
  finished.value = false
  list.value = []
  loadData()
})

onActivated(() => {
  page.value = 1
  finished.value = false
  list.value = []
  loadData()
})
</script>

<style scoped>
.recovery-list-bg {
  min-height: 100vh;
  background: #f7f8fa;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 0;
}

.recovery-list {
  width: 95vw;
  max-width: 420px;
  min-height: 100vh;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.08);
  margin: 0 auto;
  padding-bottom: 32px;
}

@media (max-width: 480px) {
  .recovery-list {
    width: 100vw;
    max-width: 100vw;
    min-height: 100vh;
    border-radius: 0;
    box-shadow: none;
    margin: 0;
    padding-bottom: 16px;
  }
  .recovery-list-bg {
    padding: 0;
  }
}
</style> 