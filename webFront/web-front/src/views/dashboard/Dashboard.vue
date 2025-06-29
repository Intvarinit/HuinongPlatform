<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="0" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon recovery">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.recoveryCount || 0 }}</div>
              <div class="stats-label">回收申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon compost">
              <el-icon><Leaf /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.compostCount || 0 }}</div>
              <div class="stats-label">堆肥批次</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon inspection">
              <el-icon><Search /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.inspectionCount || 0 }}</div>
              <div class="stats-label">抽检记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.userCount || 0 }}</div>
              <div class="stats-label">注册用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="0" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>回收量统计</span>
              <el-date-picker
                v-model="recoveryDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                @change="loadRecoveryStats"
              />
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="recoveryChartOption" style="height: 300px" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>堆肥合格率</span>
              <el-date-picker
                v-model="compostDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                @change="loadCompostStats"
              />
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="compostChartOption" style="height: 300px" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近活动（自适应填满剩余空间，底部留40px） -->
    <div class="activity-flex-row">
      <el-row :gutter="0" class="activity-row" style="height: 100%;">
        <el-col :span="12" style="height: 100%;">
          <el-card class="activity-card no-padding" style="height: 100%; display: flex; flex-direction: column;">
            <template #header>
              <span>最近回收申请</span>
            </template>
            <div class="activity-list full-height" style="flex: 1;">
              <div
                v-for="item in recentRecoveries"
                :key="item.id"
                class="activity-item"
              >
                <div class="activity-icon">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ item.cropType }} - {{ item.weight }}kg</div>
                  <div class="activity-time">{{ formatTime(item.createTime) }}</div>
                </div>
                <div class="activity-status">
                  <el-tag :type="getStatusType(item.status)">
                    {{ getStatusText(item.status) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12" style="height: 100%;">
          <el-card class="activity-card no-padding" style="height: 100%; display: flex; flex-direction: column;">
            <template #header>
              <span>最近堆肥批次</span>
            </template>
            <div class="activity-list full-height" style="flex: 1;">
              <div
                v-for="item in recentComposts"
                :key="item.id"
                class="activity-item"
              >
                <div class="activity-icon">
                  <el-icon><Leaf /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-title">批次号: {{ item.batchNo }}</div>
                  <div class="activity-time">{{ formatTime(item.createTime) }}</div>
                </div>
                <div class="activity-status">
                  <el-tag :type="getCompostStatusType(item.status)">
                    {{ getCompostStatusText(item.status) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'
import { recoveryApi } from '../../api/recovery'
import { compostApi } from '../../api/compost'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 统计数据
const stats = reactive({
  recoveryCount: 0,
  compostCount: 0,
  inspectionCount: 0,
  userCount: 0
})

// 日期范围
const recoveryDateRange = ref([
  dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])
const compostDateRange = ref([
  dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])

// 最近活动
const recentRecoveries = ref([])
const recentComposts = ref([])

// 回收量统计图表
const recoveryChartOption = ref({
  title: {
    text: '回收量趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '回收量(kg)',
      type: 'line',
      data: [],
      smooth: true
    }
  ]
})

// 堆肥合格率图表
const compostChartOption = ref({
  title: {
    text: '堆肥合格率',
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: function(params) {
      return params.seriesName + '<br/>' +
        params.marker + params.name + ' <b>' + params.value + '%</b>';
    }
  },
  series: [
    {
      name: '合格率',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 0, name: '合格' },
        { value: 0, name: '不合格' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 加载回收统计数据
const loadRecoveryStats = async () => {
  try {
    const response = await recoveryApi.statistics({
      startDate: recoveryDateRange.value[0],
      endDate: recoveryDateRange.value[1],
      groupBy: 'day'
    })
    
    if (response.code === 200) {
      recoveryChartOption.value.xAxis.data = response.data.xaxis
      recoveryChartOption.value.series[0].data = response.data.data
    }
  } catch (error) {
    console.error('加载回收统计数据失败:', error)
  }
}

// 加载堆肥统计数据
const loadCompostStats = async () => {
  try {
    const passRateResponse = await compostApi.passRate({
      startDate: compostDateRange.value[0],
      endDate: compostDateRange.value[1]
    })
    
    const exceptionRateResponse = await compostApi.exceptionRate({
      startDate: compostDateRange.value[0],
      endDate: compostDateRange.value[1]
    })
    
    if (passRateResponse.code === 200 && exceptionRateResponse.code === 200) {
      const passRate = passRateResponse.data.rate || 0
      const exceptionRate = exceptionRateResponse.data.rate || 0
      const normalRate = 1 - passRate - exceptionRate
      
      compostChartOption.value.series[0].data = [
        { value: (passRate * 100).toFixed(1), name: '合格' },
        { value: (normalRate * 100).toFixed(1), name: '进行中' },
        { value: (exceptionRate * 100).toFixed(1), name: '异常' }
      ]
    }
  } catch (error) {
    console.error('加载堆肥统计数据失败:', error)
  }
}

// 加载最近活动
const loadRecentActivities = async () => {
  try {
    // 加载最近回收申请
    const recoveryResponse = await recoveryApi.allRecords({ page: 1, size: 5 })
    if (recoveryResponse.code === 200) {
      recentRecoveries.value = recoveryResponse.data.records || []
    }
    
    // 加载最近堆肥批次
    const compostResponse = await compostApi.page({ page: 1, size: 5 })
    if (compostResponse.code === 200) {
      recentComposts.value = compostResponse.data.records || []
    }
  } catch (error) {
    console.error('加载最近活动失败:', error)
  }
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取堆肥状态类型
const getCompostStatusType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取堆肥状态文本
const getCompostStatusText = (status) => {
  const statusMap = {
    0: '进行中',
    1: '已完成',
    2: '异常',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  loadRecoveryStats()
  loadCompostStats()
  loadRecentActivities()
})
</script>

<style scoped>
.dashboard {
  width: 100%;
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 5px 5px 16px 5px;
  box-sizing: border-box;
}

.el-card {
  width: 100%;
  box-sizing: border-box;
}

.stats-row,
.charts-row {
  margin-bottom: 10px;
}

.stats-card {
  height: 100px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stats-icon.recovery {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.compost {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.inspection {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.user {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stats-label {
  font-size: 14px;
  color: #666;
}

.charts-row {
  height: 350px;
}

.chart-card {
  height: 350px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.activity-flex-row {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.activity-row {
  height: 100%;
  min-height: 0;
}

.activity-card.no-padding {
  padding: 0 !important;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.activity-list.full-height {
  flex: 1;
  height: 100%;
  min-height: 0;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: #666;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.activity-status {
  margin-left: 15px;
}
</style> 