<script setup>
import { ref } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { userList, getAllUsers, getUsersByName } from '@/boundaries/accountBoundary'
import { userFriendList, getUserFriends, sendFriendApplication } from '@/boundaries/socializationBoundary'

import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);

const searchUserDialog = ref(false);
const addUserDialog = ref(false);
const targetUser = ref({});
const searchUserInput = ref('');
const applyInfoInput = ref('');

const showSearchUserDialog = async () => {
  await getAllUsers();
  await getUserFriends();
  searchUserDialog.value = true;
};

const showAddFriend = (user) => {
  targetUser.value = user;
  searchUserDialog.value = false;
  addUserDialog.value = true;
};

const isUserFriend = (user) => {
  return userFriendList.value.map(item => item.userId).includes(user.userId) || user.userId === userId.value;
};

const handleApplicationConfirm = () => {
  sendFriendApplication(targetUser.value.userId, applyInfoInput.value, handleApplicationSuccess);
};

const handleApplicationSuccess = (res) => {
  ElMessage.success('已发送好友申请');
  addUserDialog.value = false;
};

defineExpose({ showSearchUserDialog });

</script>

<template>
  <el-dialog v-model="searchUserDialog" title="添加好友">
    <div class="searchUserArea">
      <el-input placeholder="搜索用户" :suffix-icon="Search" v-model="searchUserInput"></el-input>
      <el-button @click="getUsersByName(searchUserInput)">搜索</el-button>
    </div>
    <div class="candidateUserList">
      <div class="candidateUserItem" v-for="user in userList">
        <img class="userAvatarDisplay" :src="user.photo" />
        <div class="userNameDisplay">{{ user.userName }}</div>
        <div class="userIdDisplay">(ID: {{ user.userId }})</div>
        <el-button class="userControl" type="primary" @click="showAddFriend(user)" :disabled="isUserFriend(user)"
        >添加</el-button>
      </div>
    </div>
    <template #footer>
      <el-button @click="searchUserDialog = false">关闭</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="addUserDialog" title="添加好友">
    <div class="addFriendInfo">申请添加的好友：</div>
    <div class="userItemDisplay">
      <img class="userAvatarDisplay" :src="targetUser.photo" />
      <div class="userNameDisplay">{{ targetUser.userName }}</div>
      <div class="userIdDisplay">(ID: {{ targetUser.userId }})</div>
    </div>
    <div class="addFriendInfo">申请说明：</div>
    <el-input type="textarea" placeholder="输入好友申请" v-model="applyInfoInput"></el-input>
    <template #footer>
      <el-button type="primary" @click="handleApplicationConfirm">确定</el-button>
      <el-button @click="addUserDialog = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.searchUserArea{
  display: flex;
  padding-top: 10px;
  padding-bottom: 10px;
}

.candidateUserList{
  border: 1px solid var(--lightgray-border);
  height: 330px;
  overflow: auto;
}

.candidateUserItem{
  display: flex;
  align-items: center;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--lightgray-border);
  padding: 10px;
}

.userItemDisplay{
  display: flex;
  align-items: center;
  padding: 10px;
}

.userAvatarDisplay{
  width: 40px;
  height: 40px;
}

.userNameDisplay{
  margin-left: 10px;
}

.userIdDisplay{
  color: var(--gray-text);
  margin-left: 10px;
}

.userControl{
  margin-left: auto;
}

.addFriendInfo{
  padding-top: 10px;
  padding-bottom: 10px;
}

</style>