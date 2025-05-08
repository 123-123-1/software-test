<script setup>
import { sendJoinGroupApplication, userGroupList } from '@/boundaries/groupBoundary';
import { ref } from 'vue';

const targetGroup = ref(null);
const joinGroupDialog = ref(false);
const joinApplyInfoInput = ref('');

const showJoinGroup = (group) => {
  if(userGroupList.value.map(g => g.groupId).includes(group.groupId)){
    ElMessage.info('您已在该团体中');
    return;
  }
  targetGroup.value = group;
  joinGroupDialog.value = true;
};

const handleJoinConfirm = () => {
  sendJoinGroupApplication(targetGroup.value.groupId, joinApplyInfoInput.value, handleJoinSuccess);
};

const handleJoinSuccess = (res) => {
  ElMessage.success('已发送加入申请');
  joinGroupDialog.value = false;
};

defineExpose({ showJoinGroup });

</script>

<template>
  <el-dialog v-model="joinGroupDialog" title="加入团体">
    <div class="joinGroupInfo">申请加入的团体：</div>
    <div class="groupItemDisplay">
      <div class="groupNameDisplay">{{ targetGroup.groupName }}</div>
      <div class="groupIdDisplay">(ID: {{ targetGroup.groupId }})</div>
    </div>
    <div class="joinGroupInfo">申请说明：</div>
    <el-input type="textarea" placeholder="输入加入团体申请" v-model="joinApplyInfoInput"></el-input>
    <template #footer>
      <el-button type="primary" @click="handleJoinConfirm">确定</el-button>
      <el-button @click="joinGroupDialog = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.groupItemDisplay{
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--gray-border);
}

.groupIdDisplay{
  color: var(--gray-text);
  margin-left: 10px;
}

.joinGroupInfo{
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>