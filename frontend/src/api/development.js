import request from '@/utils/request'

/**
 * 分页查询方法学列表
 */
export function getMethodologyList(params) {
  return request({
    url: '/api/development/methodology/list',
    method: 'get',
    params
  })
}

/**
 * 查询所有有效方法学（用于下拉选择）
 */
export function getActiveMethodologies() {
  return request({
    url: '/api/development/methodology/active',
    method: 'get'
  })
}

/**
 * 根据ID查询方法学详情
 */
export function getMethodologyById(id) {
  return request({
    url: `/api/development/methodology/${id}`,
    method: 'get'
  })
}

/**
 * 新增方法学
 */
export function createMethodology(data) {
  return request({
    url: '/api/development/methodology',
    method: 'post',
    data
  })
}

/**
 * 更新方法学
 */
export function updateMethodology(id, data) {
  return request({
    url: `/api/development/methodology/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除方法学
 */
export function deleteMethodology(id) {
  return request({
    url: `/api/development/methodology/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除方法学
 */
export function batchDeleteMethodology(ids) {
  return request({
    url: '/api/development/methodology/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 查询开发项目列表
 */
export function getProjectList(params) {
  return request({
    url: '/api/development/projects',
    method: 'get',
    params
  })
}

/**
 * 获取项目详情
 */
export function getProject(id) {
  return request({
    url: `/api/development/projects/${id}`,
    method: 'get'
  })
}

/**
 * 创建开发项目
 */
export function createProject(data) {
  return request({
    url: '/api/development/projects',
    method: 'post',
    data
  })
}

/**
 * 更新项目信息
 */
export function updateProjectInfo(id, data) {
  return request({
    url: `/api/development/projects/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除项目
 */
export function deleteProject(id) {
  return request({
    url: `/api/development/projects/${id}`,
    method: 'delete'
  })
}

/**
 * 获取项目统计数据
 */
export function getProjectStats() {
  return request({
    url: '/api/development/projects/stats',
    method: 'get'
  })
}

/**
 * 获取按阶段分组的项目列表
 */
export function getProjectsByStep() {
  return request({
    url: '/api/development/projects/by-step',
    method: 'get'
  })
}

/**
 * 更新项目状态
 */
export function updateProjectStatus(id, status) {
  return request({
    url: `/api/development/projects/${id}/status?status=${status}`,
    method: 'put'
  })
}

/**
 * 更新项目阶段
 */
export function updateProjectStepInfo(id, step) {
  return request({
    url: `/api/development/projects/${id}/step?step=${step}`,
    method: 'put'
  })
}

/**
 * 获取进行中的项目列表（工作台用）
 */
export function getActiveProjects(params) {
  return request({
    url: '/api/development/workbench/projects/active',
    method: 'get',
    params
  })
}

/**
 * 获取项目详情（工作台用）
 */
export function getProjectDetail(id) {
  return request({
    url: `/api/development/workbench/projects/${id}`,
    method: 'get'
  })
}

/**
 * 获取项目任务列表（工作台用）
 */
export function getProjectTasks(projectId) {
  return request({
    url: `/api/development/workbench/projects/${projectId}/tasks`,
    method: 'get'
  })
}

/**
 * 获取项目附件列表（工作台用）
 */
export function getProjectAttachments(projectId) {
  return request({
    url: `/api/development/workbench/projects/${projectId}/attachments`,
    method: 'get'
  })
}

/**
 * 更新项目信息（工作台用）
 */
export function updateProject(id, data) {
  return request({
    url: `/api/development/workbench/projects/${id}`,
    method: 'put',
    data
  })
}

/**
 * 更新项目当前阶段（工作台用）
 */
export function updateProjectStep(id, step) {
  return request({
    url: `/api/development/workbench/projects/${id}/step?step=${step}`,
    method: 'put'
  })
}

/**
 * 更新任务状态（工作台用）
 */
export function updateTaskStatus(taskId, status) {
  return request({
    url: `/api/development/workbench/tasks/${taskId}/status?status=${status}`,
    method: 'put'
  })
}

/**
 * 上传附件（工作台用）
 */
export function uploadAttachment(data) {
  return request({
    url: '/api/development/workbench/attachments',
    method: 'post',
    data
  })
}

/**
 * 删除附件（工作台用）
 */
export function deleteAttachment(id) {
  return request({
    url: `/api/development/workbench/attachments/${id}`,
    method: 'delete'
  })
}

/**
 * 获取项目汇总信息（工作台用）
 */
export function getProjectSummary(projectId) {
  return request({
    url: `/api/development/workbench/summary/${projectId}`,
    method: 'get'
  })
}

/**
 * 初始化项目任务（工作台用）
 */
export function initializeProjectTasks(id) {
  return request({
    url: `/api/development/workbench/projects/${id}/initialize`,
    method: 'post'
  })
}
