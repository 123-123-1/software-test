<script setup>
import {
  groupCreateInput, removeCreateMember, createGroupInvite, createGroup,
  groupCreateResultDialog,
  groupCreateResultTitle,
  groupCreateResultContent
} from '@/boundaries/groupBoundary'
import InviteUserDialog from '@/components/InviteUserDialog.vue';
import { ref } from 'vue';

const inviteUserDialogRef = ref(null);

</script>

<template>
  <div class="groupCreate">
    <div class="groupCreateMain">
      <div class="infoBlock">
        <div class="infoBlockTitle">基本信息</div>
        <div class="infoBlockContent">
          <div class="infoCreateLine">
            <div class="infoCreateLabel">团体名称</div>
            <el-input placeholder="输入团体名称" v-model="groupCreateInput.groupName"></el-input>
          </div>
          <div class="infoCreateLine">
            <div class="infoCreateLabel">团体描述</div>
            <el-input placeholder="输入团体描述" v-model="groupCreateInput.description" type="textarea"></el-input>
          </div>
        </div>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">团体成员</div>
        <div class="userListArea">
          <div class="userItem" v-for="(groupMember, index) in groupCreateInput.members">
            <img class="userAvatar" :src="groupMember.photo" />
            <div class="userNameDisplay">{{ groupMember.userName }}</div>
            <div class="userIdDisplay">(ID: {{ groupMember.userId }})</div>
            <el-button class="removeMemberButton" @click="removeCreateMember(index)">移除</el-button>
          </div>
          <div class="addUserDisplay" @click="inviteUserDialogRef.showInviteUser(groupCreateInput.members)">
            + 邀请好友
          </div>
        </div>
      </div>
    </div>
    <div class="createControls">
      <el-button @click="createGroup" type="primary" size="large">创建团体</el-button>
    </div>
  </div>
  <el-dialog v-model="groupCreateResultDialog" :title="groupCreateResultTitle">
    {{ groupCreateResultContent }}
    <template #footer>
      <el-button type="primary" @click="groupCreateResultDialog = false">确定</el-button>
    </template>
  </el-dialog>
  <InviteUserDialog ref="inviteUserDialogRef" @invite-confirm="createGroupInvite"></InviteUserDialog>
</template>

<style scoped>
.groupCreate{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.groupCreateMain{
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

.createControls{
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  background-color: var(--lightgray-bg);
}

.createControls>button{
  width: 100%;
}

</style>