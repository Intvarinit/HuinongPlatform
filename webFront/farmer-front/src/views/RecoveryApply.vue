<template>
  <div class="recovery-apply">
    <van-nav-bar title="回收申请" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.item" label="回收物品" placeholder="请输入回收物品" required />
        <van-field v-model="form.amount" label="数量" placeholder="请输入数量" type="number" required />
        <van-field v-model="form.remark" label="备注" placeholder="选填" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">提交申请</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { recoveryApi } from '../api/recovery'
import { useRouter } from 'vue-router'

const form = ref({ item: '', amount: '', remark: '' })
const router = useRouter()

const onSubmit = async () => {
  if (!form.value.item || !form.value.amount) {
    showToast('请填写完整信息')
    return
  }
  const res = await recoveryApi.apply(form.value)
  if (res.code === 200) {
    showToast('申请成功')
    router.replace('/recovery')
  } else {
    showToast(res.message || '申请失败')
  }
}
</script>

<style scoped>
.recovery-apply {
  min-height: 100vh;
  background: #f7f8fa;
}
</style> 