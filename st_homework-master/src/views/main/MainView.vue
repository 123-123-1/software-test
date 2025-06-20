<template>
    <div class="main-bg">
        <el-container class="main-container">
            <el-container class="content-container">
                <el-header class="header-container">
                    <span class="header-title">软件测试</span>
                </el-header>
                <el-main class="main-content">
                    <div class="card main-card">
                        <RouterView />
                    </div>
                </el-main>
            </el-container>
            <el-aside width="220px" class="aside-container">
                <AsideBar class="right-aside" />
            </el-aside>
        </el-container>
    </div>
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
.main-bg {
    background: linear-gradient(120deg, #e6f2fb 0%, #f5f7fa 100%);
    width: 100%;
    height: 100%;
    overflow-x: hidden;
}
.main-container {
    height: 100%;
    width: 100%;
    background: transparent;
    display: flex;
    flex-direction: row;
    overflow: hidden;
    box-sizing: border-box;
}
.content-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: transparent;
    min-width: 0;
}
.header-container {
    width: 100%;
    height: 5%;
    display: flex;
    align-items: center;
    background: linear-gradient(90deg, #3a8ee6 0%, #6fb3fa 100%);
    box-shadow: 0 2px 12px 0 rgba(58,142,230,0.08);
    border-radius: 0 0 18px 18px;
    padding: 0 32px;
}
.header-title {
    font-size: 22px;
    font-weight: bold;
    color: #fff;
    letter-spacing: 2px;
}
.main-content {
    height: 70%;
    width: 100%;
    flex: 1;
    overflow-y: auto;
    background: transparent;
}
.main-card {
    height: 110%;
    margin: 0 auto;
    max-width: 1300px;
}
.aside-container {
    width: 220px !important;
    min-width: 220px;
    max-width: 220px;
    height: 100vh;
    background: linear-gradient(120deg, #84bdf9 0%, #6fb3fa 100%);
    box-shadow: -2px 0 16px 0 rgba(58,142,230,0.06);
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 32px;
    overflow-y: auto;
}
.right-aside {
    width: 100%;
}
</style>