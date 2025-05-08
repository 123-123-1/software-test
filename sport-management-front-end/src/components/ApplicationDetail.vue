<script setup>
import { getTimeDateDisplay } from '@/boundaries/utils';
import { ref } from 'vue';
import { applicationStateEnum, auditGroupApplication } from '@/boundaries/groupBoundary';

const applicationDetailVisible = ref(false);
const applicationInfo = ref({});

const handleAudit = (state) => {
  auditGroupApplication(applicationInfo.value.groupApplicationId, state);
  applicationDetailVisible.value = false;
};

const showApplicationDetail = (application) => {
  applicationInfo.value = application;
  applicationDetailVisible.value = true;
};

defineExpose({ showApplicationDetail })

</script>

<template>
  <el-dialog v-model="applicationDetailVisible" title="申请详情" align-center>
    <div class="infoBlock">
      <div class="infoBlockTitle">申请信息</div>
      <div class="infoBlockContent">
        <div class="infoLine">
          <div class="infoLabel">申请号</div>
          <div class="infoContent">{{ applicationInfo.groupApplicationId }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">申请用户</div>
          <div class="infoContent">
            <div class="infoNameDisplay">{{ applicationInfo.applicantName }}</div>
            <div class="infoIdDisplay">(ID: {{ applicationInfo.applicantId }})</div>
          </div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">申请团体</div>
          <div class="infoContent">
            <div class="infoNameDisplay">{{ applicationInfo.groupName }}</div>
            <div class="infoIdDisplay">(ID: {{ applicationInfo.groupId }})</div>
          </div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">申请时间</div>
          <div class="infoContent">{{ getTimeDateDisplay(applicationInfo.operationTime) }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">过期时间</div>
          <div class="infoContent">{{ getTimeDateDisplay(applicationInfo.expirationTime) }}</div>
        </div>
        <div class="infoLine" v-if="applicationInfo.reviewerId">
          <div class="infoLabel">审核人</div>
          <div class="infoContent">
            <div class="infoNameDisplay">{{ applicationInfo.reviewerName }}</div>
            <div class="infoIdDisplay">(ID: {{ applicationInfo.reviewerId }})</div>
          </div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">申请状态</div>
          <div class="infoContent applicationStateDisplay" :data-state="applicationInfo.state">
            {{ applicationStateEnum[applicationInfo.state] }}
          </div>
        </div>
      </div>
    </div>
    <div class="infoBlock">
      <div class="infoBlockTitle">申请说明</div>
      <div class="infoBlockContent">
        <div class="applicationMessageDisplay">
          {{ applicationInfo.applyInfo }}
        </div>
      </div>
    </div>
    <template #footer>
      <el-button v-if="applicationInfo.state === 'waiting'" type="primary" @click="handleAudit(true)">同意</el-button>
      <el-button v-if="applicationInfo.state === 'waiting'" type="danger" @click="handleAudit(false)">拒绝</el-button>
      <el-button @click="applicationDetailVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.infoBlock{
  background-color: var(--lightgray-bg);
  margin-bottom: 10px;
  border-radius: 5px;
  border: 1px solid var(--lightgray-border);
  box-shadow: 0px 2px 4px  rgba(0, 0, 0, 0.25);
}

.infoBlockTitle{
  font-size: var(--title-size);
  padding: 10px;
  font-weight: 700;
  color: var(--theme-darkblue);
  background-color: var(--lightblue-bg);
  border-bottom: 1px solid var(--theme-darkblue);
}

.infoLine{
  display: flex;
  padding: 10px;
  border-bottom: 1px solid var(--lightgray-border);
}

.infoLabel{
  color: var(--gray-text);
  width: 100px;
}

.infoContent{
  display: flex;
}

.infoIdDisplay{
  margin-left: 5px;
  color: var(--gray-text);
}

.applicationMessageDisplay{
  color: var(--gray-text);
  padding: 10px;
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