<script setup>
import { ref } from 'vue';
import {
  groupInfo, getGroupDetail, removeGroupMember, changeMemberRole, inviteJoinGroup
} from '@/boundaries/groupBoundary';
import { userFriendList, getUserFriends } from '@/boundaries/socializationBoundary';
import { Search } from '@element-plus/icons-vue';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const manageMemberVisible = ref(false);
const confirmDialogVisible = ref(false);
const searchInput = ref('');
const operationInfo = ref({
  target: null,
  type: '',
});

const displayUsers = ref([]);

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);

const showManageMember = async () => {
  await getUserFriends();
  displayUsers.value = userFriendList.value;
  manageMemberVisible.value = true;
};

const searchMember = () => {
  displayUsers.value = userFriendList.value.filter(user => user.userName.includes(searchInput.value));
};

const resetSearch = () => {
  displayUsers.value = userFriendList.value;
};

const isGroupMember = (id) => {
  return groupInfo.value.members.map(member => member.userId).includes(id);
};

const showConfirmDialog = (user, type) => {
  operationInfo.value = {
    target: user,
    type: type,
  };
  confirmDialogVisible.value = true;
};

const handleConfirm = () => {
  if(operationInfo.value.type === 'invite'){
    inviteJoinGroup(operationInfo.value.target.userId);
  }
  else if(operationInfo.value.type === 'remove'){
    removeGroupMember(operationInfo.value.target.userId);
  }
  else if(operationInfo.value.type === 'change'){
    changeMemberRole(operationInfo.value.target.userId, 'leader');
  }
  confirmDialogVisible.value = false;
};

defineExpose({ showManageMember });

</script>

<template>
  <el-dialog v-model="manageMemberVisible" title="管理团体成员" align-center>
    <div class="manageMemberDisplay">
      <div class="groupInfoDisplay">
        <div class="groupInfoLabel">团体：</div>
        <div class="groupNameDisplay">{{ groupInfo.groupName }}</div>
        <div class="groupIdDisplay">(ID: {{ groupInfo.groupId }})</div>
      </div>
      <div class="groupMemberTitle">团体成员</div>
      <div class="groupMemberList">
        <div class="groupMemberItem" v-for="user in groupInfo.members"
        :data-role="user.userId === userId ? 'self' : user.role">
          <img class="userAvatarDisplay" :src="user.photo" />
          <div class="userNameDisplay">{{ user.userName }}</div>
          <div class="userIdDisplay">(ID: {{ user.userId }})</div>
          <div class="selfDisplay" v-if="userId === user.userId">(我)</div>
          <div v-else-if="user.role === 'leader'" class="leaderDisplay">管理员</div>
          <div v-else class="memberControls">
            <el-button type="primary" @click="showConfirmDialog(user, 'change')">授予管理员</el-button>
            <el-button type="danger" @click="showConfirmDialog(user, 'remove')">移出</el-button>
          </div>
        </div>
      </div>
      <div class="searchUserArea">
        <el-input placeholder="搜索用户" :suffix-icon="Search" v-model="searchInput"></el-input>
        <el-button type="primary" @click="searchMember" style="margin-left: 10px;">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div class="candidateUserList">
        <div class="candidateUserItem" v-for="user in displayUsers">
          <img class="userAvatarDisplay" :src="user.photo" />
          <div class="userNameDisplay">{{ user.userName }}</div>
          <div class="userIdDisplay">(ID: {{ user.userId }})</div>
          <el-button class="userControl" type="primary" @click="showConfirmDialog(user, 'invite')"
          :disabled="isGroupMember(user.userId)">邀请</el-button>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="manageMemberVisible = false">关闭</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="confirmDialogVisible" title="操作确认">
    <div v-if="operationInfo.type === 'invite'">
      是否确定邀请 {{ operationInfo.target.userName }} (ID: {{ operationInfo.target.userId }}) 加入团体？
    </div>
    <div v-else-if="operationInfo.type === 'remove'">
      是否确定移出成员 {{ operationInfo.target.userName }} (ID: {{ operationInfo.target.userId }}) ？
    </div>
    <div v-else-if="operationInfo.type === 'change'">
      是否确定将 {{ operationInfo.target.userName }} (ID: {{ operationInfo.target.userId }}) 授权为团体管理员？
    </div>
    <template #footer>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
      <el-button @click="confirmDialogVisible = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.manageMemberDisplay{
  max-height: 75vh;
  overflow: auto;
}

.groupInfoDisplay{
  display: flex;
  font-size: 1.2em;
}

.groupIdDisplay{
  color: var(--gray-text);
  margin-left: 5px;
}

.groupMemberTitle{
  margin-top: 10px;
  margin-bottom: 5px;
}

.groupMemberList{
  border: 1px solid var(--gray-border);
}

.groupMemberItem{
  display: flex;
  align-items: center;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--lightgray-border);
  padding: 10px;
}

.groupMemberItem[data-role="self"]{
  background-color: var(--lightblue-bg);
}

.groupMemberItem[data-role="leader"]{
  background-color: var(--yellow-bg);
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

.selfDisplay{
  color: var(--lightblue-text);
  margin-left: auto;
}

.leaderDisplay{
  color: var(--yellow-text);
  margin-left: auto;
}

.memberControls{
  display: flex;
  margin-left: auto;
}

</style>