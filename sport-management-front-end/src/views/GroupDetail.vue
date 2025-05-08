<script setup>
import { getGroupDetail, groupInfo, memberRoleEnum, isUserLeader } from '@/boundaries/groupBoundary';
import { getLocalTimeDateDisplay } from '@/boundaries/utils';
import { useRouter, useRoute } from 'vue-router';
import { ref, onMounted } from 'vue';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import ManageMemberDialog from '@/components/ManageMemberDialog.vue';

const manageMemberRef = ref(null);

const route = useRoute();
const userStore = useUserStore();
const { userId } = storeToRefs(userStore);

onMounted(() => {
  if(!route.query.id){
    return;
  }
  getGroupDetail(route.query.id);
});

const router = useRouter();
const linkGroupChat = (chatId) => {
  router.push({
    path: '/chats/list',
    query: { type: 'group', id: chatId }
  })
};

</script>

<template>
  <div class="groupDetail">
    <div class="infoBlock">
      <div class="infoBlockTitle">基本信息</div>
      <div class="infoBlockContent">
        <div class="infoLine">
          <div class="infoLabel">团体名称</div>
          <div class="infoContent">{{ groupInfo.groupName }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">团体号</div>
          <div class="infoContent">{{ groupInfo.groupId }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">创建时间</div>
          <div class="infoContent">{{ getLocalTimeDateDisplay(groupInfo.creationTime) }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">团体描述</div>
          <div class="infoContent">{{ groupInfo.description }}</div>
        </div>
        <div class="infoLine">
          <div class="infoLabel">团体群聊</div>
          <div class="infoContent chatLinkDisplay" @click="linkGroupChat(groupInfo.chatId)">点击前往团体群聊</div>
        </div>
      </div>
    </div>
    <div class="infoBlock">
      <div class="infoBlockTitle">
        <div>团体成员</div>
        <div class="groupMemberControls" v-if="isUserLeader(userId)">
          <el-button size="small" @click="manageMemberRef.showManageMember()" type="primary">管理成员</el-button>
        </div>
      </div>
      <div class="userListArea">
      <div class="userItem" v-for="groupMember in groupInfo.members"
      :data-role="groupMember.userId === userId ? 'self' : groupMember.role">
        <img class="userAvatar" :src="groupMember.photo" />
        <div class="userNameDisplay">{{ groupMember.userName }}</div>
        <div class="selfIdDisplay" v-if="groupMember.userId === userId">(我)</div>
        <div class="userIdDisplay" v-else>(ID: {{ groupMember.userId }})</div>
        <div class="userRoleDisplay" :data-role="groupMember.role">{{ memberRoleEnum[groupMember.role] }}</div>
      </div>
    </div>
    </div>
  </div>
  <ManageMemberDialog ref="manageMemberRef"></ManageMemberDialog>
</template>

<style scoped>
.groupDetail{
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
  display: flex;
  align-items: center;
  font-size: var(--title-size);
  padding: 10px;
  font-weight: 700;
  color: var(--theme-darkblue);
  background-color: var(--lightblue-bg);
  border-bottom: 1px solid var(--theme-darkblue);
}

.infoBlockContent{
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 20px;
  padding-right: 20px;
}

.infoLine{
  display: flex;
  padding-top: 10px;
  padding-bottom: 10px;
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

.chatLinkDisplay{
  color: var(--lightblue-text);
  text-decoration: underline;
}

.chatLinkDisplay:hover{
  cursor: pointer;
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

.userRoleDisplay{
  margin-left: auto;
  margin-right: 10px;
}

.userRoleDisplay[data-role="member"]{
  color: var(--lightblue-text);
}

.userRoleDisplay[data-role="leader"]{
  color: var(--yellow-text);
}

.userItem[data-role="self"]{
  background-color: var(--lightblue-bg);
}

.userItem[data-role="leader"]{
  background-color: var(--yellow-bg);
}

.selfIdDisplay{
  margin-left: 5px;
  color: var(--lightblue-text);
}

.groupMemberControls{
  margin-left: auto;
}

</style>