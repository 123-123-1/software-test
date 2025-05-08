<script setup>
import {
  groupApplicationList, applicationStateEnum, auditGroupApplication, getGroupApplications,
} from '@/boundaries/groupBoundary';
import { getLocalTimeDateDisplay } from '@/boundaries/utils';
import { ref, onMounted } from 'vue';
import ApplicationDetail from '@/components/ApplicationDetail.vue';

const applicationDetailRef = ref(null);

onMounted(() => {
  getGroupApplications();
});

</script>

<template>
  <div class="groupApplication">
    <el-table :data="groupApplicationList" class="applicationTable">
      <el-table-column prop="groupApplicationId" label="申请号" width="100"></el-table-column>
      <el-table-column label="团体">
        <template #default="scope">
          <div class="userInfoDisplay">
            <div>{{ scope.row.groupName }}</div>
            <div class="userIdDisplay">(ID: {{ scope.row.groupId }})</div>
          </div>
        </template>
      </el-table-column>
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
      <el-table-column label="审核人">
        <template #default="scope">
          <div class="userInfoDisplay" v-if="scope.row.reviewerId">
            <div>{{ scope.row.reviewerName }}</div>
            <div class="userIdDisplay">(ID: {{ scope.row.reviewerId }})</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <div v-if="scope.row.state === 'waiting'">
            <el-button size="small" @click="applicationDetailRef.showApplicationDetail(scope.row)">详情</el-button>
            <el-button type="primary" size="small" @click="auditGroupApplication(scope.row.groupApplicationId, true)">
              同意
            </el-button>
            <el-button type="danger" size="small" @click="auditGroupApplication(scope.row.groupApplicationId, false)">
              拒绝
            </el-button>
          </div>
          <div v-else class="applicationStateDisplay" :data-state="scope.row.state">
            {{ applicationStateEnum[scope.row.state] }}
            <el-button size="small" @click="applicationDetailRef.showApplicationDetail(scope.row)" style="margin-left: 10px;">详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <ApplicationDetail ref="applicationDetailRef"></ApplicationDetail>
</template>

<style scoped>
.groupApplication{
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