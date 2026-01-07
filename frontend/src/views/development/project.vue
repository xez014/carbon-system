<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>开发项目管理</span>
          <el-button type="primary" @click="showCreateDialog">新建项目</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="项目列表" name="list">
          <div class="stats-container">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-value">{{ stats.totalCount || 0 }}</div>
                  <div class="stat-label">全部项目</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card planning">
                  <div class="stat-value">{{ stats.plannedCount || 0 }}</div>
                  <div class="stat-label">规划中</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card registered">
                  <div class="stat-value">{{ stats.registeredCount || 0 }}</div>
                  <div class="stat-label">已备案</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="never" class="stat-card verified">
                  <div class="stat-value">{{ stats.verifiedCount || 0 }}</div>
                  <div class="stat-label">已核证</div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="状态筛选">
              <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
                <el-option label="规划中" value="PLANNED" />
                <el-option label="已注册" value="REGISTERED" />
                <el-option label="已核证" value="VERIFIED" />
                <el-option label="已签发" value="ISSUED" />
              </el-select>
            </el-form-item>
            <el-form-item label="方法学">
              <el-input v-model="filterForm.methodology" placeholder="搜索方法学" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFilter">查询</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
          
          <el-table :data="projectList" v-loading="loading" stripe style="width: 100%">
            <el-table-column prop="name" label="项目名称" min-width="180">
              <template #default="{ row }">
                <el-link type="primary" @click="showDetail(row)">{{ row.name }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="methodology" label="方法学" width="150" />
            <el-table-column prop="location" label="项目地点" width="180" />
            <el-table-column prop="estimatedEmissionReduction" label="预估减排量(tCO2e)" width="150" align="right">
              <template #default="{ row }">
                {{ formatNumber(row.estimatedEmissionReduction) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="currentStep" label="当前阶段" width="120">
              <template #default="{ row }">
                {{ getStepName(row.currentStep) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="showDetail(row)">查看</el-button>
                <el-button type="primary" link size="small" @click="showEditDialog(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="loadProjects"
              @current-change="loadProjects"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="进度监控" name="progress">
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in stepsData"
              :key="index"
              :timestamp="`${step.count} 个项目`"
              :type="step.count > 0 ? 'primary' : 'gray'"
              placement="top"
            >
              <el-card shadow="never">
                <h4>{{ step.stepName }}</h4>
                <p class="step-desc">{{ step.description }}</p>
                <div class="step-projects" v-if="step.projects && step.projects.length > 0">
                  <el-tag 
                    v-for="project in step.projects.slice(0, 5)" 
                    :key="project.id" 
                    size="small" 
                    style="margin-right: 5px; margin-bottom: 5px"
                  >
                    {{ project.name }}
                  </el-tag>
                  <el-tag v-if="step.count > 5" size="small" type="info">
                    +{{ step.count - 5 }} 更多
                  </el-tag>
                </div>
                <el-empty v-else description="暂无项目" :image-size="40" />
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新建项目' : dialogType === 'edit' ? '编辑项目' : '项目详情'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef"
        :model="projectForm"
        :rules="formRules"
        label-width="120px"
        :disabled="dialogType === 'view'"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方法学" prop="methodology">
              <el-select v-model="projectForm.methodology" placeholder="请选择方法学" style="width: 100%">
                <el-option
                  v-for="item in methodologies"
                  :key="item.code"
                  :label="`${item.code} - ${item.name}`"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="projectForm.status" style="width: 100%">
                <el-option label="规划中" value="PLANNED" />
                <el-option label="已注册" value="REGISTERED" />
                <el-option label="已核证" value="VERIFIED" />
                <el-option label="已签发" value="ISSUED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="项目地点" prop="location">
              <el-input v-model="projectForm.location" placeholder="请输入项目详细地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预估减排量" prop="estimatedEmissionReduction">
              <el-input-number 
                v-model="projectForm.estimatedEmissionReduction" 
                :min="0" 
                :precision="2" 
                style="width: 100%"
              />
              <span class="unit">tCO2e/年</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前阶段" prop="currentStep">
              <el-select v-model="projectForm.currentStep" style="width: 100%">
                <el-option v-for="(name, idx) in stepNames" :key="idx" :label="name" :value="idx" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="基准线描述">
              <el-input 
                type="textarea" 
                :rows="3" 
                v-model="projectForm.baselineDescription"
                placeholder="请输入项目基准线描述..." 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input 
                type="textarea" 
                :rows="2" 
                v-model="projectForm.remark"
                placeholder="请输入备注信息（可选）" 
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button v-if="dialogType !== 'view'" type="primary" @click="handleSubmit" :loading="submitting">
            {{ dialogType === 'create' ? '创建' : '保存' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProjectList,
  getProject,
  createProject,
  updateProjectInfo,
  deleteProject,
  getProjectStats,
  getProjectsByStep,
  getActiveMethodologies
} from '@/api/development'

const activeTab = ref('list')
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('view')
const formRef = ref(null)

const projectList = ref([])
const methodologies = ref([])
const stepsData = ref([])

const stats = reactive({
  totalCount: 0,
  plannedCount: 0,
  registeredCount: 0,
  verifiedCount: 0,
  issuedCount: 0
})

const filterForm = reactive({
  status: '',
  methodology: ''
})

const projectForm = reactive({
  id: null,
  name: '',
  methodology: '',
  status: 'PLANNED',
  location: '',
  estimatedEmissionReduction: 0,
  currentStep: 0,
  baselineDescription: '',
  remark: ''
})

const formRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  methodology: [{ required: true, message: '请选择方法学', trigger: 'change' }],
  location: [{ required: true, message: '请输入项目地点', trigger: 'blur' }]
}

const stepNames = ['立项', 'PDD设计', '第三方审定', '主管部门备案', '减排量核证', '资产签发']

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadProjects = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (filterForm.status) {
      params.status = filterForm.status
    }
    if (filterForm.methodology) {
      params.methodology = filterForm.methodology
    }
    
    console.log('请求项目列表参数:', params)
    const res = await getProjectList(params)
    console.log('项目列表响应:', res)
    
    if (res && res.records) {
      projectList.value = res.records
      total.value = res.total
    } else if (Array.isArray(res)) {
      projectList.value = res
      total.value = res.length
    } else {
      projectList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
    projectList.value = []
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const res = await getProjectStats()
    if (res) {
      Object.assign(stats, res)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadStepsData = async () => {
  try {
    const res = await getProjectsByStep()
    if (res) {
      stepsData.value = res
    }
  } catch (error) {
    console.error('加载阶段数据失败:', error)
  }
}

const loadMethodologies = async () => {
  try {
    const res = await getActiveMethodologies()
    if (res) {
      methodologies.value = res
    } else if (Array.isArray(res)) {
      methodologies.value = res
    }
  } catch (error) {
    console.error('加载方法学列表失败:', error)
  }
}

const handleFilter = () => {
  currentPage.value = 1
  loadProjects()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.methodology = ''
  currentPage.value = 1
  loadProjects()
}

const showCreateDialog = () => {
  dialogType.value = 'create'
  Object.assign(projectForm, {
    id: null,
    name: '',
    methodology: '',
    status: 'PLANNED',
    location: '',
    estimatedEmissionReduction: 0,
    currentStep: 0,
    baselineDescription: '',
    remark: ''
  })
  dialogVisible.value = true
}

const showEditDialog = async (row) => {
  dialogType.value = 'edit'
  try {
    const res = await getProject(row.id)
    if (res) {
      Object.assign(projectForm, res)
    } else {
      Object.assign(projectForm, row)
    }
  } catch (error) {
    Object.assign(projectForm, row)
  }
  dialogVisible.value = true
}

const showDetail = async (row) => {
  dialogType.value = 'view'
  try {
    const res = await getProject(row.id)
    if (res) {
      Object.assign(projectForm, res)
    } else {
      Object.assign(projectForm, row)
    }
  } catch (error) {
    Object.assign(projectForm, row)
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  submitting.value = true
  try {
    if (dialogType.value === 'create') {
      const data = {
        name: projectForm.name,
        methodology: projectForm.methodology,
        status: projectForm.status,
        location: projectForm.location,
        estimatedEmissionReduction: projectForm.estimatedEmissionReduction,
        currentStep: projectForm.currentStep,
        currentTask: stepNames[projectForm.currentStep],
        baselineDescription: projectForm.baselineDescription,
        ownerId: 1
      }
      const res = await createProject(data)
      if (res && (res.code === 200 || res.success)) {
        ElMessage.success('项目创建成功')
        dialogVisible.value = false
        await loadProjects()
        await loadStats()
      } else {
        ElMessage.error(res?.message || '创建失败')
      }
    } else {
      const data = {
        name: projectForm.name,
        methodology: projectForm.methodology,
        status: projectForm.status,
        location: projectForm.location,
        estimatedEmissionReduction: projectForm.estimatedEmissionReduction,
        currentStep: projectForm.currentStep,
        currentTask: stepNames[projectForm.currentStep],
        baselineDescription: projectForm.baselineDescription
      }
      const res = await updateProjectInfo(projectForm.id, data)
      if (res && (res.code === 200 || res.success)) {
        ElMessage.success('项目更新成功')
        dialogVisible.value = false
        await loadProjects()
        await loadStats()
      } else {
        ElMessage.error(res?.message || '更新失败')
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该项目？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteProject(row.id)
    if (res && (res.code === 200 || res.success)) {
      ElMessage.success('删除成功')
      await loadProjects()
      await loadStats()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const types = {
    'PLANNING': 'info',
    'PLANNED': 'warning',
    'REGISTERED': 'primary',
    'VERIFIED': 'success',
    'ISSUED': 'success',
    'CANCELLED': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'PLANNING': '规划中',
    'PLANNED': '规划完成',
    'REGISTERED': '已注册',
    'VERIFIED': '已核证',
    'ISSUED': '已签发',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const getStepName = (step) => {
  return stepNames[step] || '未知阶段'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const formatNumber = (num) => {
  if (num == null) return '0'
  return new Intl.NumberFormat('zh-CN').format(num)
}

onMounted(() => {
  loadProjects()
  loadStats()
  loadStepsData()
  loadMethodologies()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border-radius: 8px;
}

.stat-card.planning {
  border-left: 4px solid #e6a23c;
}

.stat-card.registered {
  border-left: 4px solid #409eff;
}

.stat-card.verified {
  border-left: 4px solid #67c23a;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.filter-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.step-desc {
  color: #606266;
  font-size: 14px;
  margin: 10px 0;
}

.step-projects {
  margin-top: 10px;
}

.unit {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
