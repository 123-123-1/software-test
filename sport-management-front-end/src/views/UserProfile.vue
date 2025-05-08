<template>
  <div class="container">
    <div class="infoBlock">
      <div class="infoBlockTitle">个人信息</div>
      <div class="basicInfoArea">
        <div class="basicInfoItem">
          <div class="basicInfoLabel">用户ID</div>
          <div class="basicInfoContent">{{ infoData.userId }}</div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">用户名</div>
          <div class="basicInfoContent">{{ infoData.userName }}</div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">联系电话</div>
          <div class="basicInfoContent">{{ infoData.phone }}</div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">真实姓名</div>
          <div class="basicInfoContent">{{ infoData.realName }}</div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">注册时间</div>
          <div class="basicInfoContent">{{ getTimeDateDisplay(infoData.registrationDate) }}</div>
        </div>
      </div>
    </div>
    <div class="infoBlock" v-if="infoData.userType === 'user'">
      <div class="infoBlockTitle">违约信息</div>
      <div class="basicInfoArea">
        <div class="basicInfoItem">
          <div class="basicInfoLabel">本月违约次数</div>
          <div class="basicInfoContent">{{ violationInfo.violationCount }}</div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">预约权限</div>
          <div class="basicInfoContent reservePermissionDisplay" :data-status="violationInfo.state">
            {{ reservePermissionEnum[violationInfo.state] }}
          </div>
        </div>
        <div class="basicInfoItem">
          <div class="basicInfoLabel">权限更新时间</div>
          <div class="basicInfoContent">
            {{ getLocalTimeDateDisplay(violationInfo.updateTime) }}
          </div>
        </div>
        <div class="basicInfoItem" v-if="violationInfo.state === 'locked'">
          <div class="basicInfoLabel">解禁时间</div>
          <div class="basicInfoContent">
            {{ getLocalTimeDateDisplay(violationInfo.unlockTime) }}
          </div>
        </div>
      </div>
    </div>
    <div class="infoBlock">
      <div class="infoBlockTitle">账户管理</div>
      <div class="accountControlArea">
        <div class="accountControls">
          <div class="controlItem" @click="editInfoRef.showEditUserInfo()">
            <div class="controlText">修改个人信息</div>
            <el-icon><arrow-right></arrow-right></el-icon>
          </div>
          <div class="controlItem" @click="editPwdRef.showEditPwd()">
            <div class="controlText">修改密码</div>
            <el-icon><arrow-right></arrow-right></el-icon>
          </div>
          <div class="controlItem logoutButton" @click="showLogout = true">
            <div class="controlText logoutText">退出登录</div>
            <el-icon><arrow-right></arrow-right></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
  <EditUserInfoDialog ref="editInfoRef"></EditUserInfoDialog>
  <EditPwdDialog ref="editPwdRef"></EditPwdDialog>

  <el-dialog v-model="showLogout" title="退出登录确认">
    确定要退出登录吗
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showLogout = false">取消</el-button>
        <el-button type="primary" @click="logout">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
  
<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import useUserStore from "@/stores/userStore";
import { storeToRefs } from 'pinia';
import { 
  showLogout, getInfo, infoData
} from '../boundaries/accountBoundary';
import { getTimeDateDisplay, getLocalTimeDateDisplay } from '../boundaries/utils'
import {
  violationInfo, reservePermissionEnum,
  getUserViolation
} from '../boundaries/reservationBoundary';
import { ArrowRight } from '@element-plus/icons-vue';
import EditUserInfoDialog from '@/components/EditUserInfoDialog.vue';
import EditPwdDialog from '@/components/EditPwdDialog.vue';

const router = useRouter();

const userStore = useUserStore();
const { userToken, expirationTime, userType, userId } = storeToRefs(userStore);

const editInfoRef = ref(null);
const editPwdRef = ref(null);

onMounted(async () => {
  await getInfo();
  if(infoData.value.userType === 'user'){
    await getUserViolation();
  }
});

const logout = () => {
  showLogout.value = false;
  userId.value = '';
  userType.value = '';
  userToken.value = '';
  expirationTime.value = '';
  router.push('/user/login');
}

</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  margin: 10px;
  background-color: white;
  border-radius: 5px;
}

.userInfoTitle{
  display: flex;
  justify-content: center;
  font-size: 25px;
  font-weight: 700;
  padding: 10px;
  border-bottom: 1px solid black;
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

.basicInfoArea{
  padding: 10px;
}

.basicInfoItem{
  display: flex;
  padding-top: 10px;
  padding-bottom: 10px;
}

.basicInfoLabel{
  margin-left: 10px;
  width: 120px;
}

.basicInfoContent{
  display: flex;
  align-items: center;
  color: var(--gray-text);
}

.accountControls{
  display: flex;
  flex-wrap: wrap;
}

.controlItem{
  display: flex;
  align-items: center;
  width: calc(50% - 32px);
  border: 1px solid var(--lightgray-border);
  padding: 15px;
}

.controlItem:hover{
  cursor: pointer;
  background-color: var(--gray-bg);
}

.controlText{
  margin-right: auto;
}

.logoutText{
  color: var(--red-text);
}

.logoutButton:hover{
  background-color: var(--red-bg);
}

.reservePermissionDisplay[data-status="normal"]{
  color: var(--green-text);
}

.reservePermissionDisplay[data-status="locked"]{
  color: var(--red-text);
}

</style>