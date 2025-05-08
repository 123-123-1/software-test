<script setup>
import { useRouter } from 'vue-router';
import { ArrowRight } from '@element-plus/icons-vue';
import { storeToRefs } from 'pinia';
import { useMenuStore } from '@/stores/menuStore.js';

const menuStore = useMenuStore();
const { menuData, selectedPage, selectedSubsystem } = storeToRefs(menuStore);
const router = useRouter();

// 涉及router，在此处定义跳转功能
const handleMenuSelect = (menuItem) => {
  router.push(menuItem.link);
};

</script>

<template>
  <div class="sidebar">
    <img class="sidebarLogo" src="../assets/logo.png" />
    <el-collapse v-model="selectedSubsystem" accordion>
      <el-collapse-item v-for="menuItem in menuData" :title="menuItem.title" :name="menuItem.index">
        <template #title>
          <div class="menuTitleDisplay">{{ menuItem.title }}</div>
        </template>
        <div class="submenuDisplay">
          <div class="submenuItem" v-for="submenuItem in menuItem.submenu" @click="handleMenuSelect(submenuItem)"
          :data-selected="submenuItem.index === selectedPage.index" >
            <div class="submenuTitle">{{ submenuItem.title }}</div>
            <el-icon><arrow-right></arrow-right></el-icon>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<style scoped>
.sidebar {
  border-right: 1px solid lightgray;
  height: calc(100vh - 20px);
  overflow: auto;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
}

.sidebarLogo {
  width: 100%;
}

:deep(.el-menu-item.is-active){
  color: var(--lightblue-text);
  background-color: var(--lightblue-bg-dark);
}

.menuTitleDisplay{
  padding-left: 20px;
  font-size: 1.2em;
}

.submenuItem{
  display: flex;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 20px;
  padding-right: 20px;
  font-size: 1.1em;
}

.submenuDisplay{
  border: 1px solid var(--menuitem-border);
}

.submenuTitle{
  margin-left: 10px;
  margin-right: auto;
}

.submenuItem:hover{
  cursor: pointer;
  background-color: var(--lightgray-bg);
}

.submenuItem[data-selected=true]{
  background-color: var(--lightblue-bg);
  color: var(--lightblue-text);
}

:deep(.el-collapse-item__content){
  padding-bottom: 0;
}

:deep(.el-collapse-item .is-active){
  filter: contrast(90%);
}

</style>
