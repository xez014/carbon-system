<template>
  <div class="app-container" v-loading="loading">
    <div class="welcome-section">
      <h2>欢迎回到工作台，{{ username }}</h2>
      <p class="date-info">{{ currentDate }}</p>
    </div>

    <!-- 数据概览 -->
    <el-row :gutter="20" class="data-row">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-blue clickable" @click="goToPage('/assets/quota')">
          <div class="card-content">
            <div class="icon-wrapper">
              <el-icon><Cloudy /></el-icon>
            </div>
            <div class="info">
              <div class="label">碳配额总量</div>
              <div class="value">{{ formatNumber(summaryData.totalQuota) }} <span class="unit">tCO2e</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-green clickable" @click="goToPage('/assets/credit')">
          <div class="card-content">
            <div class="icon-wrapper">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="info">
              <div class="label">CCER持有量</div>
              <div class="value">{{ formatNumber(summaryData.totalCredit) }} <span class="unit">tCO2e</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-orange clickable" @click="goToPage('/market/account')">
          <div class="card-content">
            <div class="icon-wrapper">
              <el-icon><Money /></el-icon>
            </div>
            <div class="info">
              <div class="label">今日交易额</div>
              <div class="value">¥ {{ formatNumber(summaryData.todayTradeAmount) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-red clickable" @click="goToPage('/system/approval')">
          <div class="card-content">
            <div class="icon-wrapper">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="info">
              <div class="label">待办事项</div>
              <div class="value">{{ summaryData.pendingTasks }} <span class="unit">个</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 左侧：趋势图表 -->
      <el-col :span="16">
        <el-card class="chart-card" header="碳排放与资产趋势">
          <div ref="chartRef" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      
      <!-- 右侧：快捷入口和通知 -->
      <el-col :span="8">
        <el-card header="快捷入口" class="shortcut-card">
          <div class="shortcut-grid">
             <div class="shortcut-item" @click="goToPage('/development/project')">
               <el-button type="primary" circle size="large" icon="Plus" />
               <span>项目立项</span>
             </div>
             <div class="shortcut-item" @click="goToPage('/market/exchange')">
               <el-button type="success" circle size="large" icon="ShoppingCart" />
               <span>发布交易</span>
             </div>
             <div class="shortcut-item" @click="goToPage('/system/operation')">
               <el-button type="warning" circle size="large" icon="Document" />
               <span>报表下载</span>
             </div>
             <div class="shortcut-item" @click="goToPage('/system/config')">
               <el-button type="info" circle size="large" icon="Setting" />
               <span>系统设置</span>
             </div>
          </div>
        </el-card>

        <el-card header="最新动态" style="margin-top: 20px;">
          <el-timeline>
             <el-timeline-item timestamp="10:00" type="primary">
               系统完成每日数据盘点
             </el-timeline-item>
             <el-timeline-item timestamp="09:30" type="success">
               CCER 项目 #202401 审核通过
             </el-timeline-item>
             <el-timeline-item timestamp="Yesterday" type="warning">
               新增碳交易订单 5 笔
             </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboardSummary, getUserStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const router = useRouter()
const username = ref('Admin')
const loading = ref(false)
const chartRef = ref(null)
let chartInstance = null

// 仪表盘汇总数据
const summaryData = ref({
  totalUsers: 0,
  certifiedCompanies: 0,
  totalQuota: 0,
  totalCredit: 0,
  totalAssets: 0,
  todayTradeAmount: 0,
  totalTradeAmount: 0,
  pendingTasks: 0,
  systemStatus: '正常运行'
})

// 用户个人统计数据
const userStatsData = ref({
  userQuota: 0,
  userCredit: 0,
  tradeCount: 0
})

onMounted(() => {
  const storedUser = localStorage.getItem('username')
  if (storedUser) {
    username.value = storedUser
  }
  loadDashboardData()
  initChart()
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    // 获取汇总数据
    const summaryRes = await getDashboardSummary(1)
    if (summaryRes) {
      summaryData.value = summaryRes
    }

    // 获取用户统计数据
    const userStatsRes = await getUserStats(1)
    if (userStatsRes) {
      userStatsData.value = userStatsRes
    }
  } catch (error) {
    console.error('加载仪表盘数据失败', error)
    ElMessage.error('加载仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

// 格式化数字（添加千分位）
const formatNumber = (num) => {
  if (!num && num !== 0) return 0
  return num.toLocaleString('zh-CN')
}

// 页面跳转
const goToPage = (path) => {
  router.push(path)
}

// 初始化ECharts图表
const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['碳配额', 'CCER', '交易量']
    },
    xAxis: [
      {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '资产量 (吨)',
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '交易量 (笔)',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '碳配额',
        type: 'bar',
        data: [820000, 932000, 901000, 934000, 1290000, 1330000, 1320000],
        itemStyle: {
          color: '#00A8E8'
        }
      },
      {
        name: 'CCER',
        type: 'bar',
        data: [45000, 52000, 48000, 50000, 60000, 55000, 58000],
        itemStyle: {
          color: '#2ECC71'
        }
      },
      {
        name: '交易量',
        type: 'line',
        yAxisIndex: 1,
        data: [150, 230, 224, 218, 350, 380, 420],
        itemStyle: {
          color: '#FFA500'
        },
        lineStyle: {
          width: 3
        },
        smooth: true
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  // 窗口大小改变时自动调整图表
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}
</script>

<style scoped>
.app-container { padding: 20px; }
.welcome-section {
  margin-bottom: 20px;
}
.welcome-section h2 { margin: 0; color: #303133; }
.date-info { margin: 5px 0 0; color: #909399; font-size: 14px; }

.data-card {
  border: none;
  border-radius: 8px;
  color: #fff;
  transition: transform 0.3s;
}
.data-card:hover { transform: translateY(-5px); }
.data-card.clickable { cursor: pointer; }
.data-card.clickable:active { transform: translateY(-3px); }
.card-blue { background: linear-gradient(135deg, #00A8E8 0%, #2B6CB0 100%); }
.card-green { background: linear-gradient(135deg, #2ECC71 0%, #27AE60 100%); }
.card-orange { background: linear-gradient(135deg, #FFA500 0%, #F39C12 100%); }
.card-red { background: linear-gradient(135deg, #FF6B6B 0%, #EE5A24 100%); }

.card-content { display: flex; align-items: center; }
.icon-wrapper { 
  font-size: 40px; 
  padding: 10px; 
  background: rgba(255,255,255,0.2); 
  border-radius: 50%; 
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px; 
}
.info .label { font-size: 14px; opacity: 0.9; }
.info .value { font-size: 24px; font-weight: bold; margin-top: 5px; }
.info .unit { font-size: 12px; font-weight: normal; opacity: 0.8; }

.chart-card {
  min-height: 450px;
}

.shortcut-grid { display: flex; justify-content: space-around; padding: 10px 0; }
.shortcut-item { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  cursor: pointer;
  transition: transform 0.2s;
}
.shortcut-item:hover { transform: scale(1.05); }
.shortcut-item:active { transform: scale(0.95); }
.shortcut-item span { margin-top: 8px; font-size: 14px; color: #606266; }
</style>

