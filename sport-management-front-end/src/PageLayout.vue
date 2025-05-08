<script setup>
import { RouterView } from 'vue-router';
import SideBar from './components/SideBar.vue';
import TopBar from './components/TopBar.vue';

</script>

<template>
  <div class="pageLayout">
    <div class="sidebarArea">
      <SideBar></SideBar>
    </div>
    <div class="mainArea">
      <div class="topbarArea">
        <TopBar></TopBar>
      </div>
      <div class="pageMain">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<style scoped>
.pageLayout {
  display: flex;
  width: 100%;
}

.sidebarArea {
  width: var(--side-size);
}

.mainArea {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 20px);
  flex: 1;
}

.topbarArea {
  width: 100%;
}

.pageMain{
  overflow: auto;
  flex: 1;
}
</style>