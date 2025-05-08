<script setup>
import { onMounted } from 'vue';
import {
  getReservationDetail, reservationInfo, reservationTypeEnum, reservationUserStateEnum,
  getUserNameById, reservationOperationEnum, reservationStateEnum,
  cancelReservationDialog,
  cancelUserReservation,
} from '@/boundaries/reservationBoundary'
import { useRoute, useRouter } from 'vue-router';
import { getLocalTimeDateDisplay, getTimeDateDisplay, getTimeslotDisplay } from '@/boundaries/utils';
import { Clock } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

onMounted(() => {
  if(!route.query.id){
    return;
  }
  getReservationDetail(route.query.id);
});

const linkReservationList = () => {
  router.push('/reservations/list');
};

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
            <div class="infoLabel">预约状态</div>
            <div class="infoContent reservationStateDisplay" v-if="reservationInfo.basicInfo.state !== 'normal'
            && reservationInfo.basicInfo.userState !== 'violated'" :data-state="reservationInfo.basicInfo.state">
              {{ reservationStateEnum[reservationInfo.basicInfo.state] }}
            </div>
            <div class="infoContent reservationStateDisplay" v-else :data-state="reservationInfo.basicInfo.userState">
              {{ reservationUserStateEnum[reservationInfo.basicInfo.userState] }}
            </div>
          </div>
          <div class="infoLine">
            <div class="infoLabel">预约时间段</div>
            <div class="infoContent">
              {{ getTimeslotDisplay(reservationInfo.basicInfo.startTime, reservationInfo.basicInfo.endTime, true) }}
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
              {{ getLocalTimeDateDisplay(reservationInfo.basicInfo.expirationTime) }}
            </div>
          </div>
        </div>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">预约成员</div>
        <div class="userListArea">
          <div class="userItem" v-for="reservationUser in reservationInfo.users">
            <img class="userAvatar" :src="reservationUser.photo" />
            <div class="userNameDisplay">{{ reservationUser.userName }}</div>
            <div class="userIdDisplay">(ID: {{ reservationUser.userId }})</div>
          </div>
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
      <el-button size="large" style="margin-right: auto;" @click="linkReservationList">返回预约列表</el-button>
      <el-button type="danger" size="large" @click="cancelReservationDialog = true"
      v-if="reservationInfo?.basicInfo?.state !== 'cancelled' && !['cancelled', 'signed', 'violated'].includes(reservationInfo?.basicInfo?.userState)">取消预约</el-button>
    </div>
  </div>
  <el-dialog title="取消预约" v-model="cancelReservationDialog">
    选择"取消个人预约"会取消您的预约，不会改变其它人的预约状态；选择"取消预约项目"会取消整个预约项目
    <template #footer>
      <el-button @click="cancelUserReservation('individual')">取消个人预约</el-button>
      <el-button type="danger" @click="cancelUserReservation('all')">取消预约项目</el-button>
      <el-button @click="cancelReservationDialog = false">关闭</el-button>
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

.reservationStateDisplay[data-state="reserved"]{
  color: var(--lightblue-text);
}

.reservationStateDisplay[data-state="matching"]{
  color: var(--yellow-text);
}

.reservationStateDisplay[data-state="signed"]{
  color: var(--green-text);
}

.reservationStateDisplay[data-state="pending"]{
  color: var(--gray-text);
}

.reservationStateDisplay[data-state="cancelled"]{
  color: var(--gray-text);
}

.reservationStateDisplay[data-state="violated"]{
  color: var(--red-text);
}

.userListArea{
  display: flex;
  flex-wrap: wrap;
}

.userItem{
  display: flex;
  align-items: center;
  width: calc(50% - 20px);
  padding: 10px;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--gray-border);
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
  border: 1px solid var(--gray-border);
  background-color: var(--lightgray-bg);
  padding: 10px;
}

</style>