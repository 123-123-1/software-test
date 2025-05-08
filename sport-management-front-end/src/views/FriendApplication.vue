<script setup>
import {
  applicationStateEnum,
  auditFriendApplication,
  friendApplicationList, getFriendApplications
} from '@/boundaries/socializationBoundary';
import { getLocalTimeDateDisplay } from '@/boundaries/utils';
import { onMounted } from 'vue';

onMounted(() => {
  getFriendApplications();
});

</script>

<template>
  <div class="friendApplication">
    <el-table :data="friendApplicationList" class="applicationTable">
      <el-table-column prop="friendApplicationId" label="申请号" width="100"></el-table-column>
      <el-table-column label="申请用户">
        <template #default="scope">
          <div class="userInfoDisplay">
            <div>{{ scope.row.applicantName }}</div>
            <div class="userIdDisplay">(ID: {{ scope.row.applicantId }})</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="200">
        <template #default="scope">
          {{ getLocalTimeDateDisplay(scope.row.operationTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <div v-if="scope.row.state === 'waiting'">
            <el-button type="primary" size="small" v-if="scope.row.state === 'waiting'"
            @click="auditFriendApplication(scope.row.friendApplicationId, true)">同意</el-button>
            <el-button type="danger" size="small" v-if="scope.row.state === 'waiting'"
            @click="auditFriendApplication(scope.row.friendApplicationId, true)">拒绝</el-button>
          </div>
          <div v-else class="applicationStateDisplay" :data-state="scope.row.state">
            {{ applicationStateEnum[scope.row.state] }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.friendApplication{
  margin: 10px;
  border: 1px solid var(--lightgray-border); 
}

.applicationTable{
  height: 100%;
}

.userInfoDisplay{
  display: flex;
}

.userIdDisplay{
  margin-left: 5px;
  color: var(--gray-text);
}

.applicationStateDisplay[data-state="accepted"]{
  color: var(--lightblue-text);
}

.applicationStateDisplay[data-state="rejected"]{
  color: var(--red-text);
}

.applicationStateDisplay[data-state="expired"]{
  color: var(--gray-text);
}

</style>