<script setup>
import {
  chatCreateInput, removeCreateMember, createChat,
  createGroupInvite
} from '@/boundaries/socializationBoundary'
import InviteUserDialog from '@/components/InviteUserDialog.vue';
import { ref } from 'vue';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const chatCreateVisible = ref(false);
const inviteUserDialogRef = ref(null);
const showChatCreate = () => {
  chatCreateVisible.value = true;
};

defineExpose({ showChatCreate });

</script>

<template>
  <el-dialog v-model="chatCreateVisible" title="创建群聊">
    <div class="infoBlock">
      <div class="infoBlockTitle">群聊信息</div>
      <div class="infoBlockContent">
        <div class="infoCreateLine">
          <div class="infoCreateLabel">群聊名称</div>
          <el-input placeholder="输入群聊名称" v-model="chatCreateInput.name"></el-input>
        </div>
      </div>
    </div>
    <div class="infoBlock">
      <div class="infoBlockTitle">群聊成员</div>
      <div class="userListArea">
        <div class="userItem" v-for="(chatMember, index) in chatCreateInput.members">
          <img class="userAvatar" :src="chatMember.photo" />
          <div class="userNameDisplay">{{ chatMember.userName }}</div>
          <div class="userIdDisplay">(ID: {{ chatMember.userId }})</div>
          <el-button class="removeMemberButton" @click="removeCreateMember(index)">移除</el-button>
        </div>
        <div class="addUserDisplay" @click="inviteUserDialogRef.showInviteUser(chatCreateInput.members)">
          + 邀请好友
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="createChat(userId)" type="primary">创建</el-button>
      <el-button @click="chatCreateVisible = false">取消</el-button>
    </template>
  </el-dialog>
  <InviteUserDialog ref="inviteUserDialogRef" @invite-confirm="createGroupInvite"></InviteUserDialog>
</template>

<style scoped>
.chatCreate{
  flex: 1;
  overflow: auto;
  padding: 10px;
}

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

.infoBlockContent{
  background-color: var(--lightgray-bg);
}

.infoCreateLine{
  display: flex;
  align-items: center;
  padding: 10px;
}

.infoCreateLabel{
  width: 100px;
}

.userListArea{
  display: flex;
  flex-wrap: wrap;
}

.userItem{
  display: flex;
  align-items: center;
  width: calc(50% - 22px);
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.userAvatar{
  width: 40px;
  height: 40px;
  border-radius: 5px;
}

.userNameDisplay{
  margin-left: 10px;
}

.userIdDisplay{
  margin-left: 5px;
  color: var(--gray-text);
}

.removeMemberButton{
  margin-left: auto;
}

.addUserDisplay{
  display: flex;
  align-items: center;
  justify-content: center;
  width: calc(50% - 2px);
  height: 60px;
  background-color: var(--gray-bg);
  border: 1px dashed var(--gray-border);
  color: var(--gray-text);
}

.addUserDisplay:hover{
  cursor: pointer;
  background-color: var(--lightgray-bg);
}

</style>