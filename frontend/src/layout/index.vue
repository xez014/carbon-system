<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Promotion /></el-icon>
        <span v-show="!isCollapse">碳资产管理平台</span>
      </div>
      <el-menu
        :default-active="activePath"
        class="el-menu-vertical"
        router
        :collapse="isCollapse"
        background-color="var(--layout-sidebar-bg)"
        text-color="#bfcbd9"
        active-text-color="var(--el-color-primary)"
      >
        <template v-for="route in accessRoutes" :key="route.path">
           <!-- Single menu item (no children) -->
           <el-menu-item 
              v-if="!route.children || (route.children.length === 1 && !route.children[0].children)" 
              :index="resolvePath(route)"
            >
              <el-icon v-if="route.meta && route.meta.icon || (route.children && route.children[0].meta && route.children[0].meta.icon)">
                <component :is="route.meta?.icon || route.children[0].meta.icon" />
              </el-icon>
              <template #title>
                <span>{{ route.children ? route.children[0].meta.title : route.meta.title }}</span>
              </template>
           </el-menu-item>

           <!-- Submenu -->
           <el-sub-menu v-else :index="route.path">
             <template #title>
                <el-icon v-if="route.meta && route.meta.icon"><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta.title }}</span>
             </template>
             <el-menu-item 
                v-for="child in route.children" 
                :key="child.path" 
                :index="route.path + '/' + child.path"
              >
                {{ child.meta.title }}
             </el-menu-item>
           </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="使用文档" placement="bottom">
            <el-icon class="header-icon"><QuestionFilled /></el-icon>
          </el-tooltip>
          <el-tooltip content="全屏" placement="bottom">
            <el-icon class="header-icon"><FullScreen /></el-icon>
          </el-tooltip>
          <el-dropdown class="avatar-dropdown">
            <span class="el-dropdown-link">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="User" @click="router.push('/account/info')">个人中心</el-dropdown-item>
                <el-dropdown-item icon="Setting" @click="router.push('/system/config')">系统设置</el-dropdown-item>
                <el-dropdown-item divided icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const username = ref(localStorage.getItem('username') || 'Admin')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// Filter routes that should be displayed in menu (exclude hidden ones)
const accessRoutes = computed(() => {
  return router.options.routes.filter(r => !r.hidden)
})

const activePath = computed(() => route.path)
const currentRouteTitle = computed(() => route.meta.title || '当前页面')

const resolvePath = (route) => {
  if (route.redirect) return route.redirect
  if (route.children && route.children.length === 1) {
    return route.path === '/' ? route.children[0].path : route.path + '/' + route.children[0].path
  }
  return route.path
}

const logout = () => {
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: var(--layout-sidebar-bg);
  color: #fff;
  transition: width 0.3s;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  background-color: var(--layout-sidebar-active-bg);
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.logo-icon {
  margin-right: 10px;
  font-size: 24px;
  color: var(--el-color-primary);
}
.el-menu-vertical {
  border-right: none;
}
.main-container {
  flex-direction: column;
}
.header {
  background-color: var(--layout-header-bg);
  border-bottom: 1px solid var(--border-primary);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
.header-left {
  display: flex;
  align-items: center;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}
.header-right {
  display: flex;
  align-items: center;
}
.header-icon {
  font-size: 20px;
  margin-right: 20px;
  cursor: pointer;
  color: var(--text-secondary);
}
.header-icon:hover { color: var(--el-color-primary); }

.avatar-dropdown { cursor: pointer; }
.el-dropdown-link { display: flex; align-items: center; }
.username { margin: 0 8px; font-weight: 500; }

.main {
  background-color: var(--layout-main-bg);
  padding: 20px;
  overflow-y: auto;
}

/* Transitions */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>

