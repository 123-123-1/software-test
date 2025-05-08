<script setup>
import { Search } from '@element-plus/icons-vue';
import { groupTypeSelect, getAllGroups, userGroupList, getUserGroups, groupSearch, searchGroup, resetSearchGroup, allGroupFiltered } from '@/boundaries/groupBoundary';
import { getLocalTimeDateDisplay } from '@/boundaries/utils';
import { ref, onMounted } from 'vue';
import JoinGroupDialog from '@/components/JoinGroupDialog.vue';
import { useRouter } from 'vue-router';

const joinGroupDialogRef = ref(null);
const router = useRouter();

onMounted(() => {
  getAllGroups();
  getUserGroups();
});

const viewGroupDetail = (group) => {
  router.push({
    path: '/groups/detail',
    query: { id: group.groupId }
  });
};

</script>

<template>
  <div class="groupList">
    <el-tabs v-model="groupTypeSelect" :stretch="true">
      <el-tab-pane label="所有团体" name="all">
        <div class="searchArea">
          <el-input :suffix-icon="Search" placeholder="搜索团体" v-model="groupSearch"></el-input>
          <el-button type="primary" style="margin-left: 10px;" @click="searchGroup">搜索</el-button>
          <el-button @click="resetSearchGroup">重置</el-button>
        </div>
        <div class="listArea">
          <el-table :data="allGroupFiltered" class="groupTable">
            <el-table-column prop="groupId" label="团体号" width="100"></el-table-column>
            <el-table-column prop="groupName" label="团体名称"></el-table-column>
            <el-table-column label="创建时间">
              <template #default="scope">
                {{ getLocalTimeDateDisplay(scope.row.creationTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" type="primary" @click="joinGroupDialogRef.showJoinGroup(scope.row)">申请加入</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的团体" name="my">
        <div class="listArea">
          <el-table :data="userGroupList" class="groupTable">
            <el-table-column prop="groupId" label="团体号" width="100"></el-table-column>
            <el-table-column prop="groupName" label="团体名称"></el-table-column>
            <el-table-column>
              <template #default="scope">
                {{ getLocalTimeDateDisplay(scope.row.creationTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" type="primary" @click="viewGroupDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
  <JoinGroupDialog ref="joinGroupDialogRef"></JoinGroupDialog>
</template>

<style scoped>
.groupList{
  display: flex;
  flex-direction: column;
}

.searchArea{
  display: flex;
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.listArea{
  flex: 1;
  margin: 10px;
  overflow: auto;
  border: 1px solid var(--lightgray-border);
}

.groupTable{
  height: 100%;
}

</style>