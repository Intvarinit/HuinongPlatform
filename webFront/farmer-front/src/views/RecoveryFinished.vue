<template>
  <div class="recovery-finished">
    <van-nav-bar title="已完成回收" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadData">
      <van-cell v-for="item in list" :key="item.id" :title="item.item" :label="item.createTime">
        <template #right-icon>
          <van-tag type="success">已完成</van-tag>
        </template>
      </van-cell>
    </van-list>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { recoveryApi } from '../api/recovery'

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10

const loadData = async () => {
  if (loading.value) return
  loading.value = true
  const res = await recoveryApi.list({
    pageNum: page.value,
    pageSize,
    status: 2
  })
  if (res.code === 200) {
    if (page.value === 1) list.value = []
    list.value = list.value.concat(res.data.records || [])
    finished.value = res.data.records.length < pageSize
    page.value++
  }
  loading.value = false
}
</script>

<style scoped>
.recovery-finished {
  min-height: 100vh;
  background: #f7f8fa;
}
</style> 