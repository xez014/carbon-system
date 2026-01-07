<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="never" class="project-list-card">
          <template #header>
            <div class="clearfix">
              <span>进行中的项目</span>
              <el-button 
                style="float: right; padding: 3px 0" 
                link 
                type="primary"
                @click="showCreateDialog"
              >
                新建
              </el-button>
            </div>
          </template>
          <div class="project-list">
            <div 
              v-for="(item, index) in projects" 
              :key="item.id" 
              class="project-item"
              :class="{ active: currentProject && currentProject.id === item.id }"
              @click="selectProject(item)"
            >
              <div class="p-title">{{ item.name }}</div>
              <div class="p-info">
                <el-tag size="small">{{ item.methodology || '未指定方法学' }}</el-tag>
                <span class="p-date">{{ formatDate(item.updateTime) }}</span>
              </div>
            </div>
            <el-empty v-if="!projects || projects.length === 0" description="暂无项目" :image-size="60" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card v-if="currentProject" shadow="hover">
          <template #header>
            <div class="detail-header">
              <div>
                <h3>{{ currentProject.name }}</h3>
                <el-tag :type="getStatusType(currentProject.status)" size="small" style="margin-top: 5px">
                  {{ getStatusText(currentProject.status) }}
                </el-tag>
              </div>
              <div>
                <el-button type="primary" size="small" @click="saveProgress">保存进度</el-button>
              </div>
            </div>
          </template>

          <div class="step-container">
            <el-steps :active="currentProject.currentStep" finish-status="success" align-center>
              <el-step title="立项" description="提交项目建议书"></el-step>
              <el-step title="PDD设计" description="编写项目设计文件"></el-step>
              <el-step title="第三方审定" description="DOE 现场审定"></el-step>
              <el-step title="主管部门备案" description="发改委/生态部备案"></el-step>
              <el-step title="减排量核证" description="监测与核证"></el-step>
              <el-step title="资产签发" description="CCER 上市"></el-step>
            </el-steps>
          </div>

          <el-divider />

          <div class="task-content">
            <h4>当前阶段任务：{{ currentProject.currentTask || getStepName(currentProject.currentStep) }}</h4>
            <el-alert 
              :title="getStepAlert(currentProject.currentStep)" 
              type="info" 
              show-icon 
              style="margin-bottom: 20px" 
            />
            
            <el-form label-position="top" :model="projectForm">
              <el-form-item label="项目基准线描述">
                <el-input 
                  type="textarea" 
                  :rows="4" 
                  v-model="projectForm.baselineDescription" 
                  placeholder="请输入项目基准线描述..."
                />
              </el-form-item>
              <el-form-item label="预计年减排量 (tCO2e)">
                <el-input-number 
                  v-model="projectForm.estimatedEmissionReduction" 
                  :min="0" 
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="实际减排量 (tCO2e)">
                <el-input-number 
                  v-model="projectForm.actualEmissionReduction" 
                  :min="0" 
                  :precision="2"
                />
              </el-form-item>
              <el-form-item label="附件上传">
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :on-remove="handleFileRemove"
                  :file-list="fileList"
                  multiple
                  :limit="5"
                >
                  <el-button type="primary">点击上传文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">支持上传项目相关文件，单个文件不超过50MB</div>
                  </template>
                </el-upload>
              </el-form-item>
            </el-form>
            
            <div style="margin-top: 20px; text-align: right;">
              <el-button type="success" @click="completeCurrentTask">
                完成当前阶段
              </el-button>
            </div>
          </div>

          <el-divider />

          <div class="attachments-section">
            <h4>项目附件</h4>
            <el-table :data="attachments" style="width: 100%" v-if="attachments && attachments.length > 0">
              <el-table-column prop="fileName" label="文件名" />
              <el-table-column prop="fileType" label="类型" width="120" />
              <el-table-column prop="uploadTime" label="上传时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.uploadTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="downloadAttachment(row)">
                    下载
                  </el-button>
                  <el-button type="danger" link size="small" @click="removeAttachment(row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无附件" :image-size="60" />
          </div>
        </el-card>
        
        <el-card v-else shadow="hover">
          <el-empty description="请选择一个项目查看详情">
            <el-button type="primary" @click="showCreateDialog">新建项目</el-button>
          </el-empty>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="createDialogVisible"
      title="新建开发项目"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="120px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="方法学" prop="methodology">
          <el-select v-model="createForm.methodology" placeholder="请选择方法学" style="width: 100%">
            <el-option
              v-for="item in methodologies"
              :key="item.code"
              :label="`${item.code} - ${item.name}`"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目地点" prop="location">
          <el-input v-model="createForm.location" placeholder="请输入项目地点" />
        </el-form-item>
        <el-form-item label="预计减排量" prop="estimatedEmissionReduction">
          <el-input-number 
            v-model="createForm.estimatedEmissionReduction" 
            :min="0" 
            :precision="2" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            type="textarea" 
            :rows="3" 
            v-model="createForm.remark"
            placeholder="请输入备注信息（可选）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createProject" :loading="creating">
            创建
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
  getActiveProjects, 
  getProjectDetail, 
  getProjectTasks,
  getProjectAttachments,
  updateProject,
  updateProjectStep,
  updateTaskStatus,
  uploadAttachment,
  deleteAttachment,
  initializeProjectTasks,
  getActiveMethodologies
} from '@/api/development'

const currentProject = ref(null)
const projects = ref([])
const tasks = ref([])
const attachments = ref([])
const methodologies = ref([])
const fileList = ref([])
const createDialogVisible = ref(false)
const creating = ref(false)
const createFormRef = ref(null)

const projectForm = reactive({
  baselineDescription: '',
  estimatedEmissionReduction: 0,
  actualEmissionReduction: 0
})

const createForm = reactive({
  name: '',
  methodology: '',
  location: '',
  estimatedEmissionReduction: 0,
  remark: ''
})

const createRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  methodology: [{ required: true, message: '请选择方法学', trigger: 'change' }],
  location: [{ required: true, message: '请输入项目地点', trigger: 'blur' }]
}

const stepNames = ['立项', 'PDD设计', '第三方审定', '主管部门备案', '减排量核证', '资产签发']
const stepAlerts = [
  '请依据相应方法学提交项目建议书，明确基准线情景和项目边界',
  '请依据 CM-001-V01 方法学进行测算，编写完整的 PDD 文件',
  '请联系有资质的 DOE 进行现场审定，准备相关材料',
  '请准备完整材料向发改委/生态部进行项目备案',
  '请建立监测计划，定期核算实际减排量',
  '完成核证后，提交 CCER 上市申请'
]

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = {
    'PLANNED': 'info',
    'REGISTERED': 'warning',
    'VERIFIED': 'success',
    'ISSUED': 'primary',
    'CANCELLED': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'PLANNED': '规划中',
    'REGISTERED': '已备案',
    'VERIFIED': '已审定',
    'ISSUED': '已签发',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const getStepName = (step) => {
  return stepNames[step] || '未知阶段'
}

const getStepAlert = (step) => {
  return stepAlerts[step] || '请按流程推进项目'
}

const loadProjects = async () => {
  try {
    console.log('Workbench: 请求活跃项目列表')
    const res = await getActiveProjects()
    console.log('Workbench: 项目列表响应:', res)
    if (res && (res.code === 200 || res.success) && res.data) {
      projects.value = res.data
    } else if (Array.isArray(res)) {
      projects.value = res
    } else {
      projects.value = []
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
    projects.value = []
  }
}

const loadMethodologies = async () => {
  try {
    const res = await getActiveMethodologies()
    if (res && (res.code === 200 || res.success) && res.data) {
      methodologies.value = res.data
    } else if (Array.isArray(res)) {
      methodologies.value = res
    }
  } catch (error) {
    console.error('加载方法学列表失败:', error)
  }
}

const selectProject = async (project) => {
  try {
    currentProject.value = project
    
    const detailRes = await getProjectDetail(project.id)
    if (detailRes && (detailRes.code === 200 || detailRes.success) && detailRes.data) {
      Object.assign(currentProject.value, detailRes.data)
      Object.assign(projectForm, {
        baselineDescription: detailRes.data.baselineDescription || '',
        estimatedEmissionReduction: detailRes.data.estimatedEmissionReduction || 0,
        actualEmissionReduction: detailRes.data.actualEmissionReduction || 0
      })
    }
    
    const tasksRes = await getProjectTasks(project.id)
    if (tasksRes && (tasksRes.code === 200 || tasksRes.success) && tasksRes.data) {
      tasks.value = tasksRes.data
    } else if (Array.isArray(tasksRes)) {
      tasks.value = tasksRes
    }
    
    const attachmentsRes = await getProjectAttachments(project.id)
    if (attachmentsRes && (attachmentsRes.code === 200 || attachmentsRes.success) && attachmentsRes.data) {
      attachments.value = attachmentsRes.data
    } else if (Array.isArray(attachmentsRes)) {
      attachments.value = attachmentsRes
    }
    
    if (tasks.value.length === 0) {
      try {
        await initializeProjectTasks(project.id)
        const newTasksRes = await getProjectTasks(project.id)
        tasks.value = newTasksRes.data || []
      } catch (initError) {
        console.error('初始化任务失败:', initError)
      }
    }
  } catch (error) {
    console.error('加载项目详情失败:', error)
    ElMessage.error('加载项目详情失败')
  }
}

const showCreateDialog = () => {
  createForm.name = ''
  createForm.methodology = ''
  createForm.location = ''
  createForm.estimatedEmissionReduction = 0
  createForm.remark = ''
  createDialogVisible.value = true
}

const createProject = async () => {
  if (!createFormRef.value) return
  
  try {
    await createFormRef.value.validate()
    creating.value = true
    
    const data = {
      name: createForm.name,
      methodology: createForm.methodology,
      location: createForm.location,
      estimatedEmissionReduction: createForm.estimatedEmissionReduction,
      status: 'PLANNED',
      ownerId: 1,
      currentStep: 0,
      currentTask: '立项'
    }
    
    const res = await updateProject(0, data)
    if (res.code === 200 || res.success) {
      ElMessage.success('项目创建成功')
      createDialogVisible.value = false
      await loadProjects()
    }
    
    const newProject = projects.value.find(p => p.name === data.name)
    if (newProject) {
      await selectProject(newProject)
    }
  } catch (error) {
    console.error('创建项目失败:', error)
    if (error !== false) {
      ElMessage.error('创建项目失败')
    }
  } finally {
    creating.value = false
  }
}

const saveProgress = async () => {
  if (!currentProject.value) return
  
  try {
    const data = {
      ...currentProject.value,
      baselineDescription: projectForm.baselineDescription,
      estimatedEmissionReduction: projectForm.estimatedEmissionReduction,
      actualEmissionReduction: projectForm.actualEmissionReduction
    }
    
    const res = await updateProject(currentProject.value.id, data)
    if (res.code === 200 || res.success) {
      ElMessage.success('保存成功')
      await loadProjects()
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const completeCurrentTask = async () => {
  if (!currentProject.value) return
  
  try {
    await ElMessageBox.confirm('确认完成当前阶段？完成后将进入下一阶段。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const nextStep = currentProject.value.currentStep + 1
    if (nextStep >= stepNames.length) {
      ElMessage.success('恭喜！项目已完成所有阶段')
      return
    }
    
    const res = await updateProjectStep(currentProject.value.id, nextStep)
    if (res.code === 200 || res.success) {
      currentProject.value.currentStep = nextStep
      currentProject.value.currentTask = stepNames[nextStep]
      ElMessage.success('阶段已完成，已进入下一阶段')
      await loadProjects()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新阶段失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handleFileChange = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    formData.append('projectId', currentProject.value.id)
    formData.append('fileName', file.name)
    formData.append('filePath', `/uploads/${file.name}`)
    formData.append('fileType', file.name.split('.').pop())
    
    const res = await uploadAttachment(formData)
    if (res.code === 200 || res.success) {
      ElMessage.success('文件上传成功')
      const attachmentsRes = await getProjectAttachments(currentProject.value.id)
      if (attachmentsRes.code === 200 && attachmentsRes.data) {
        attachments.value = attachmentsRes.data || []
      }
    }
  } catch (error) {
    console.error('上传文件失败:', error)
    ElMessage.error('上传文件失败')
    fileList.value = fileList.value.filter(f => f.uid !== file.uid)
  }
}

const handleFileRemove = (file) => {
  fileList.value = fileList.value.filter(f => f.uid !== file.uid)
}

const downloadAttachment = (attachment) => {
  ElMessage.info(`正在下载: ${attachment.fileName}`)
}

const removeAttachment = async (attachment) => {
  try {
    await ElMessageBox.confirm('确认删除该附件？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteAttachment(attachment.id)
    if (res.code === 200 || res.success) {
      ElMessage.success('删除成功')
      attachments.value = attachments.value.filter(a => a.id !== attachment.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除附件失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(async () => {
  await loadMethodologies()
  await loadProjects()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.project-list-card {
  height: calc(100vh - 140px);
  overflow-y: auto;
}

.project-list {
  padding: 10px 0;
}

.project-item {
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
}

.project-item:hover {
  background: #ecf5ff;
}

.project-item.active {
  background: #409eff;
  color: white;
}

.project-item.active .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border-color: transparent;
  color: white;
}

.p-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.p-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.p-date {
  font-size: 12px;
  color: #909399;
}

.project-item.active .p-date {
  color: rgba(255, 255, 255, 0.8);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
}

.step-container {
  padding: 20px 0;
}

.task-content {
  padding: 10px 0;
}

.task-content h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

.attachments-section {
  padding: 10px 0;
}

.attachments-section h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-divider) {
  margin: 20px 0;
}
</style>
