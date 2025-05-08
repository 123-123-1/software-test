<script setup>
import { onMounted } from 'vue';
import {
  getReservationDetail, reservationInfo, reservationTypeEnum, reservationUserStateEnum,
  getUserNameById, reservationOperationEnum, reservationStateEnum
} from '@/boundaries/reservationBoundary'
import { useRoute, useRouter } from 'vue-router';
import { getTimeDateDisplay, getLocalTimeDateDisplay, getTimeslotDisplay } from '@/boundaries/utils';
import { Clock, Loading, CircleCheckFilled, WarningFilled, CircleCloseFilled } from '@element-plus/icons-vue';
import { changeReservationState, changeReservationStateDialog, changeUserReservationState, changeUserStateDialog, showChangeReservationState, showChangeUserState } from '@/boundaries/managementBoundary';

const route = useRoute();
const router = useRouter();

onMounted(() => {
  if(!route.query.id){
    return;
  }
  getReservationDetail(route.query.id);
});

const linkReservationList = () => {
  router.push('/management/reservations/list');
}

</script>

<template>
  <div class="reservationDetail">
    <div class="reservationDetailMain">
      <div class="infoBlock" v-if="reservationInfo.basicInfo">
        <div class="infoBlockTitle">基本信息</div>
        <div class="infoBlockContent">
          <div class="infoLine">
            <div class="infoLabel">预约号</div>
            <div class="infoContent">{{ reservationInfo.basicInfo.reservationId }}</div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">场馆</div>
            <div class="infoContent">
              <div>{{ reservationInfo.basicInfo.venueName }}</div>
              <div class="infoIdDisplay">(ID: {{ reservationInfo.basicInfo.venueId }})</div>
            </div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">场地</div>
            <div class="infoContent">
              <div>{{ reservationInfo.basicInfo.courtName }}</div>
              <div class="infoIdDisplay">(ID: {{ reservationInfo.basicInfo.courtId }})</div>
            </div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">预约类型</div>
            <div class="infoContent">
              {{ reservationTypeEnum[reservationInfo.basicInfo.type] }}
            </div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">预约时间段</div>
            <div class="infoContent">
              {{ getTimeslotDisplay(reservationInfo.basicInfo.startTime, reservationInfo.basicInfo.endTime, true) }}
            </div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">预约状态</div>
            <div class="infoContent reservationState" :data-state="reservationInfo.basicInfo.state">
              {{ reservationStateEnum[reservationInfo.basicInfo.state] }}
            </div>
          </div>
          <div class="infoLine" v-if="reservationInfo.basicInfo.type === 'group'">
            <div class="infoLabel">预约团体</div>
            <div class="infoContent">
              <div>{{ reservationInfo.basicInfo.groupName }}</div>
              <div class="infoIdDisplay">(ID: {{ reservationInfo.basicInfo.groupId }})</div>
            </div>
          </div>
          <div class="infoLine" v-if="reservationInfo.basicInfo.type === 'match'">
            <div class="infoLabel">拼场过期时间</div>
            <div class="infoContent">
              {{ getTimeDateDisplay(reservationInfo.basicInfo.expirationTime) }}
            </div>
          </div>
        </div>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">预约成员</div>
        <div class="userListArea">
          <el-popover v-for="reservationUser in reservationInfo.users" title="更改用户预约状态" content="内容" placement="top" :width="250">
            <template #reference>
              <div class="userItem">
                <img class="userAvatar" :src="reservationUser.photo" />
                <div class="realNameDisplay">{{ reservationUser.realName }}</div>
                <div class="userNameDisplay">({{ reservationUser.userName }})</div>
                <div class="userIdDisplay">(ID: {{ reservationUser.userId }})</div>
                <div class="userStateDisplay" :data-state="reservationUser.userState">
                  {{ reservationUserStateEnum[reservationUser.userState] }}
                </div>
              </div>
            </template>
            <template #default>
              <div v-if="reservationInfo.basicInfo.state === 'normal' && !(['signed', 'violated'].includes(reservationUser.userState))">
                <el-button size="small" type="danger" v-if="['reserved', 'matching'].includes(reservationUser.userState)"
                @click="showChangeUserState(reservationUser, 'cancelled')">取消预约</el-button>
                <el-button size="small" type="primary" v-if="reservationUser.userState === 'reserved'"
                @click="showChangeUserState(reservationUser, 'signed')">签到</el-button>
                <el-button size="small" type="danger"
                @click="showChangeUserState(reservationUser, 'violated')">违约</el-button>
              </div>
              <div v-else>
                无法改变该用户的预约状态
              </div>
            </template>
          </el-popover>
        </div>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">操作记录</div>
        <div class="infoBlockContent">
          <div class="operationListArea">
            <div class="operationItem" v-for="record in reservationInfo.records">
              <el-icon :size="20" color="var(--gray-text)"><Clock></Clock></el-icon>
              <div class="operationTimeDisplay">{{ getLocalTimeDateDisplay(record.time) }}</div>
              <div class="operationUserName" v-if="record.userId">{{ getUserNameById(record.userId) }}</div>
              <div class="operationUserId" v-if="record.userId">(ID: {{ record.userId }})</div>
              <div class="operationContent">{{ reservationOperationEnum[record.operation] }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="reservationControls">
      <el-button size="large" @click="linkReservationList" style="margin-right: auto;">返回预约列表</el-button>
      <el-button size="large" v-if="reservationInfo?.basicInfo?.state === 'pending'" type="primary"
      @click="showChangeReservationState('normal')">
        通过预约申请
      </el-button>
      <el-button size="large" v-if="reservationInfo?.basicInfo?.state !== 'cancelled'" type="danger"
      @click="showChangeReservationState('cancelled')">
        <div v-if="reservationInfo?.basicInfo?.state === 'matching'">终止拼场预约</div>
        <div v-else>取消预约</div>
      </el-button>
    </div>
  </div>
  <el-dialog v-model="changeUserStateDialog.visible" title="用户预约状态更改">
    <div class="stateConfirmDisplay">
      <div>请确认要更改的用户和预约状态：</div>
      <div class="stateConfirmLine">
        <div class="stateConfirmLabel">预约用户：</div>
        <div class="stateConfirmContent">
          <div class="realNameDisplay">{{ changeUserStateDialog.userInfo.realName }}</div>
          <div class="userNameDisplay">(用户名：{{ changeUserStateDialog.userInfo.userName }})</div>
          <div class="userIdDisplay">(ID: {{ changeUserStateDialog.userInfo.userId }})</div>
        </div>
      </div>
      <div class="stateConfirmLine">
        <div class="stateConfirmLabel">更改后状态：</div>
        <div class="stateConfirmContent userStateDisplay" :data-state="changeUserStateDialog.state">{{ reservationUserStateEnum[changeUserStateDialog.state] }}</div>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="changeUserReservationState">确定</el-button>
      <el-button @click="changeUserStateDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="changeReservationStateDialog.visible" title="预约状态更改">
    <div class="stateConfirmDisplay">
      <div>请确认要更改的预约状态：</div>
      <div class="stateConfirmLine">
        <div class="stateConfirmLabel">更改后状态：</div>
        <div class="stateConfirmContent reservationState" :data-state="changeReservationStateDialog.state">
          {{ reservationStateEnum[changeReservationStateDialog.state] }}
        </div>
      </div>
      <div v-if="changeReservationStateDialog.state === 'normal'">
        <div v-if="changeReservationStateDialog.stateCountLoading" class="stateCountLoadingDisplay">
          <el-icon class="is-loading"><Loading></Loading></el-icon>
          <div class="stateCountText">正在查询预约冲突情况...</div>
        </div>
        <div v-else-if="changeReservationStateDialog.normalCount === 0 && changeReservationStateDialog.pendingCount === 0" class="stateCountResultDisplay" data-conflict="none">
          <el-icon><CircleCheckFilled></CircleCheckFilled></el-icon>
          <div class="stateCountText">该时间段没有预约冲突</div>
        </div>
        <div v-else-if="changeReservationStateDialog.normalCount === 0" class="stateCountResultDisplay" data-conflict="warning">
          <el-icon><WarningFilled></WarningFilled></el-icon>
          <div class="stateCountText">该时间段有{{ changeReservationStateDialog.pendingCount }}个待审核的预约，可能出现预约冲突</div>
        </div>
        <div v-else class="stateCountResultDisplay" data-conflict="error">
          <el-icon><CircleCloseFilled></CircleCloseFilled></el-icon>
          <div class="stateCountText">
            <span>
              该时间段有{{ changeReservationStateDialog.normalCount }}个已经确定的预约
            </span>
            <span v-if="changeReservationStateDialog.pendingCount > 0">
              和{{ changeReservationStateDialog.pendingCount }}个待审核的预约
            </span>
            <span>
              ，如果通过该预约审核，会出现预约冲突！
            </span>
          </div>
        </div>
      </div>
      <div class="stateConfirmLine" v-if="changeReservationStateDialog.state === 'cancelled'">
        <el-checkbox v-model="changeReservationStateDialog.changeAvailability">更改相应时间段状态为 可预约</el-checkbox>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="changeReservationState">确定</el-button>
      <el-button @click="changeReservationStateDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scope>
.reservationDetail{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.reservationDetailMain{
  flex: 1;
  padding: 10px;
  overflow: auto;
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

.reservationState[data-state="pending"]{
  color: var(--yellow-text);
}

.reservationState[data-state="normal"]{
  color: var(--green-text);
}

.reservationState[data-state="cancelled"]{
  color: var(--gray-text);
}

.userListArea{
  display: flex;
  flex-wrap: wrap;
}

.userItem{
  display: flex;
  width: calc(50% - 21px);
  align-items: center;
  padding: 10px;
  background-color: var(--lightgray-bg);
  border-right: 1px solid var(--lightgray-border);
  border-bottom: 1px solid var(--lightgray-border);
}

.userAvatar{
  width: 40px;
  height: 40px;
  border-radius: 5px;
}

.userItem>.realNameDisplay{
  margin-left: 10px;
}

.userNameDisplay{
  margin-left: 10px;
  color: var(--gray-text);
}

.userIdDisplay{
  margin-left: 5px;
  color: var(--gray-text);
}

.userItem>.userStateDisplay{
  margin-left: auto;
}

.userStateDisplay[data-state="reserved"]{
  color: var(--lightblue-text);
}

.userStateDisplay[data-state="matching"]{
  color: var(--yellow-text);
}

.userStateDisplay[data-state="signed"]{
  color: var(--green-text);
}

.userStateDisplay[data-state="cancelled"]{
  color: var(--gray-text);
}

.userStateDisplay[data-state="violated"]{
  color: var(--red-text);
}

.operationItem{
  display: flex;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
}

.operationTimeDisplay{
  color: var(--gray-text);
  margin-left: 10px;
  margin-right: 20px;
}

.operationUserId{
  color: var(--gray-text);
  margin-left: 5px;
  margin-right: 5px;
}

.reservationControls{
  display: flex;
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.stateConfirmDisplay{
  font-size: 1.2em;
}

.stateConfirmLabel{
  width: 150px;
}

.stateConfirmLine{
  display: flex;
  line-height: 2.4em;
}

.stateConfirmContent{
  display: flex;
}

.stateCountLoadingDisplay{
  display: flex;
  color: var(--gray-text);
  align-items: center;
}

.stateCountResultDisplay{
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.stateCountResultDisplay[data-conflict="none"]{
  color: var(--green-text);
}

.stateCountResultDisplay[data-conflict="warning"]{
  color: var(--yellow-text);
}

.stateCountResultDisplay[data-conflict="error"]{
  color: var(--red-text);
}

.stateCountText{
  margin-left: 10px;
}

</style>