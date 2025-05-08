<script setup>
import { ref } from 'vue';
import { selectedGroup } from '../boundaries/reservationBoundary';
import { getUserLeaderGroups, userLeaderGroupList } from '../boundaries/groupBoundary'
import { Search } from '@element-plus/icons-vue';

const groupSelectVisible = ref(false);
const curGroup = ref(null);
const displayGroup = ref([]);
const searchInput = ref('');

const showGroupSelect = async () => {
  groupSelectVisible.value = true;
  curGroup.value = selectedGroup.value;
  await getUserLeaderGroups();
  displayGroup.value = userLeaderGroupList.value;
};

const checkGroupSelected = (id) => {
  return curGroup.value && curGroup.value.groupId === id;
};

const selectGroup = (group) => {
  curGroup.value = group;
};

const handleSelectConfirm = () => {
  selectedGroup.value = curGroup.value;
  groupSelectVisible.value = false;
};

const searchGroup = () => {
  displayGroup.value = userLeaderGroupList.value.filter(group => group.groupName.includes(searchInput.value));
};

const resetSearch = () => {
  displayGroup.value = userLeaderGroupList.value;
};

defineExpose({ showGroupSelect })

</script>

<template>
  <el-dialog v-model="groupSelectVisible" title="选择团体">
    <div class="selectedGroupTitle">已选择团体</div>
    <div v-if="curGroup" class="selectedGroupItem">
      <div class="groupNameDisplay">{{ curGroup.groupName }}</div>
      <div class="groupIdDisplay">(ID: {{ curGroup.groupId }})</div>
    </div>
    <div v-else class="noGroupDisplay">
      未选择团体
    </div>
    <div class="searchGroupArea">
      <el-input placeholder="搜索团体" :suffix-icon="Search" v-model="searchInput"></el-input>
      <el-button type="primary" style="margin-left: 10px;" @click="searchGroup">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    <div class="candidateGroupList">
      <div class="candidateGroupItem" v-for="group in userLeaderGroupList">
        <div class="groupNameDisplay">{{ group.groupName }}</div>
        <div class="groupIdDisplay">(ID: {{ group.groupId }})</div>
        <el-button class="groupControl" @click="selectGroup(group)"
        :disabled="checkGroupSelected(group.groupId)" size="small" type="primary">选择</el-button>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleSelectConfirm">确定</el-button>
      <el-button @click="groupSelectVisible = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.selectedGroupTitle{
  margin-bottom: 5px;
}

.selectedGroupItem{
  display: flex;
  background-color: var(--lightblue-bg);
  border: 1px solid var(--lightblue-text);
  padding: 10px;
}

.searchGroupArea{
  display: flex;
  flex-wrap: nowrap;
  padding-top: 10px;
  padding-bottom: 10px;
}

.candidateGroupList{
  border: 1px solid var(--lightgray-border);
  height: 330px;
  overflow: auto;
}

.candidateGroupItem{
  display: flex;
  align-items: center;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--gray-border);
  padding: 10px;
}

.groupIdDisplay{
  color: var(--gray-text);
  margin-left: 5px;
}

.groupControl{
  margin-left: auto;
}

.noGroupDisplay{
  display: flex;
  justify-content: center;
  color: var(--gray-text);
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  background-color: var(--lightgray-bg);
}

</style>