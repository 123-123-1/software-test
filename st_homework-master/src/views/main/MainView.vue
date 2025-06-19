<template>
    <el-container class="main-container">
        <el-aside :width="isCollapse ? '64px' : '200px'" class="aside-container">
            <AsideBar :isCollapse="isCollapse" />
        </el-aside>
        <el-container class="content-container">
            <el-header class="header-container">
                <el-button @click="collapse" :icon="icon()" color="#71777c80" :round=false
                    class="el-button-collapse"></el-button>
                <span style="padding-left:20px; font-size:20px">软件测试</span>
            </el-header>
            <el-main class="main-content">
                <RouterView />
            </el-main>
        </el-container>
    </el-container>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { Minus } from '@element-plus/icons-vue'
import AsideBar from './AsideBar.vue';

function collapse() {
    isCollapse.value = !isCollapse.value
    console.log(isCollapse.value)
}
function icon() {
    return isCollapse.value ? "Plus" : "Minus"
}
const isCollapse = ref(false)
</script>

<style scoped>
.main-container {
    height: 100vh;
    width: 100vw;
    position: fixed;
    top: 0;
    left: 0;
    overflow: hidden;
}

.aside-container {
    height: 100vh;
    background-color: #545c64;
    transition: width 0.3s;
    overflow: hidden;
}

.content-container {
    height: 100vh;
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #f0f2f5;
}

.header-container {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0;
    background-color: #ffffff;
    border-bottom: 1px solid #e6e6e6;
}

.main-content {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background-color: #ffffff;
    margin: 10px;
    border-radius: 4px;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 100%;
}

.el-button-collapse {
    width: 60px;
    height: 100%;
    border: none;
    border-radius: 0;
    margin: 0;
}

:deep(.el-aside) {
    overflow: hidden;
}

:deep(.el-main) {
    padding: 10px;
}
</style>