<script setup>
import { ref } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { getUserFriends, userFriendList } from '@/boundaries/socializationBoundary'
import { groupInfo, getGroupDetail } from '@/boundaries/groupBoundary'
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const inviteUserDialog = ref(false);
const invitedUsers = ref([]);
const candidateUserList = ref([]);
const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const isGroupInvite = ref(false);
const searchInput = ref('');
const emit = defineEmits(['inviteConfirm']);

const showInviteUser = async (curUsers, restrictGroup) => {
  invitedUsers.value = [...curUsers];
  if(restrictGroup){
    await getGroupDetail(restrictGroup);
    candidateUserList.value = groupInfo.value.members;
    isGroupInvite.value = true;
  }
  else{
    await getUserFriends();
    candidateUserList.value = userFriendList.value;
  }
  inviteUserDialog.value = true;
};

const addInviteUser = (item) => {
  invitedUsers.value.push(item);
};

const removeInviteUser = (index) => {
  invitedUsers.value.splice(index, 1);
};

const checkUserInvited = (id) => {
  return id === userId.value || invitedUsers.value.map(item => item.userId).includes(id);
};

const searchUser = () => {
  if(isGroupInvite.value){
    candidateUserList.value = groupInfo.value.members.filter(user => user.userName.includes(searchInput.value));
  }
  else{
    candidateUserList.value = userFriendList.value.filter(user => user.userName.includes(searchInput.value));
  }
};

const resetSearch = () => {
  if(isGroupInvite.value){
    candidateUserList.value = groupInfo.value.members;
  }
  else{
    candidateUserList.value = userFriendList.value;
  }
};

defineExpose({ showInviteUser });

const handleInviteConfirm = () => {
  emit('inviteConfirm', invitedUsers.value);
  inviteUserDialog.value = false;
}

</script>

<template>
  <el-dialog v-model="inviteUserDialog" title="邀请用户" align-center>
    <div class="inviteUserDisplay">
      <div class="invitedUserTitle">已邀请用户</div>
      <div class="noInviteDisplay" v-if="invitedUsers.length === 0">
        未邀请用户
      </div>
      <div class="invitedUserList" v-else>
        <div class="invitedUserItem" v-for="(user, index) in invitedUsers">
          <img class="userAvatarDisplay" :src="user.photo" />
          <div class="userNameDisplay">{{ user.userName }}</div>
          <div class="userIdDisplay">(ID: {{ user.userId }})</div>
          <el-button class="userControl" @click="removeInviteUser(index)">移除</el-button>
        </div>
      </div>
      <div class="searchUserArea">
        <el-input placeholder="搜索用户" :suffix-icon="Search" v-model="searchInput"></el-input>
        <el-button type="primary" style="margin-left: 10px;" @click="searchUser">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="candidateUserList">
        <div class="candidateUserItem" v-for="user in candidateUserList">
          <img class="userAvatarDisplay" :src="user.photo" />
          <div class="userNameDisplay">{{ user.userName }}</div>
          <div class="userIdDisplay">(ID: {{ user.userId }})</div>
          <el-button class="userControl" type="primary" @click="addInviteUser(user)"
          :disabled="checkUserInvited(user.userId)">添加</el-button>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleInviteConfirm">确定</el-button>
      <el-button @click="inviteUserDialog = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.inviteUserDisplay{
  max-height: 75vh;
  overflow: auto;
}

.invitedUserTitle{
  color: var(--lightblue-text);
  margin-bottom: 5px;
}

.noInviteDisplay{
  display: flex;
  justify-content: center;
  color: var(--gray-text);
  padding: 20px;
  border: 1px solid var(--lightgray-border);
  background-color: var(--lightgray-bg);
}

.invitedUserList{
  border: 1px solid var(--lightblue-text);
}

.invitedUserItem{
  display: flex;
  align-items: center;
  background-color: var(--lightblue-bg);
  border-bottom: 1px solid var(--lightgray-border);
  padding: 10px;
}

.userAvatarDisplay{
  width: 40px;
  height: 40px;
  border-radius: 4px;
}
.userNameDisplay{
  margin-left: 10px;
  color: black;
}

.userIdDisplay{
  color: var(--gray-text);
  margin-left: 5px;
}

.userControl{
  margin-left: auto;
}

.searchUserArea{
  display: flex;
  flex-wrap: nowrap;
  padding-top: 10px;
  padding-bottom: 10px;
}

.userSearchBox{
  flex: 1;
  margin-right: 10px;
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

</style>