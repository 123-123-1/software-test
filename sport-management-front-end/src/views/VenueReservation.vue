<script setup>
import { ref, onMounted } from 'vue';
import InviteUserDialog from '@/components/InviteUserDialog.vue';
import ReservationConfirm from '@/components/ReservationConfirm.vue';
import TimeslotSelection from '@/components/TimeslotSelection.vue';
import GroupSelectDialog from '@/components/GroupSelectDialog.vue';
import {
  reservationType, selectedAvailability, selectedTimeslot, selectedGroup, handleReservation,
  reservationResultDialog, reservationResultTitle, reservationResultContent, matchReserveCount,
  matchCourtType, reservationUserInput, handleInviteUserConfirm, reservationLoading,
} from '@/boundaries/reservationBoundary';
import { venueInfo, findCourtInfo, getVenueCourtType, venueCourtType } from '@/boundaries/venueBoundary';
import { getTimeslotDisplay } from '@/boundaries/utils';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const { userId } = storeToRefs(userStore);
const inviteUserDialogRef = ref(null);
const reservationConfirmRef = ref(null);
const timeslotSelectionRef = ref(null);
const groupSelectRef = ref(null);
const router = useRouter();

onMounted(() => {
  if(!venueInfo.value.venueId){
    ElMessage.info('请在场馆列表中选择一个场馆后进入该页面');
    router.push('/venues/list');
    return;
  }
  getVenueCourtType(venueInfo.value.venueId);
})

</script>

<template>
  <div class="venueReservation" v-loading="reservationLoading">
    <div class="reservationHeader">
      <el-tabs v-model="reservationType" type="border-card" :stretch="true">
        <el-tab-pane name="match" label="拼场预约">
          <div class="reservationTypeInfo">
            <div class="reservationTypeTitle">拼场预约</div>
            <div class="reservationTypeText">拼场预约的说明</div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="individual" label="个人预约">
          <div class="reservationTypeInfo">
            <div class="reservationTypeTitle">个人预约</div>
            <div class="reservationTypeText">个人预约的说明</div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="group" label="团体预约">
          <div class="reservationTypeInfo">
            <div class="reservationTypeTitle">团体预约</div>
            <div class="reservationTypeText">团体预约的说明</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="reservationMain">
      <!-- <div class="reservationItem">
        <div class="reservationItemLabel">预约类型</div>
        <div class="reservationTypeSelection">
          <el-radio-group v-model="reservationType" size="large">
            <el-radio-button value="match" label="拼场"></el-radio-button>
            <el-radio-button value="individual" label="个人"></el-radio-button>
            <el-radio-button value="group" label="团体"></el-radio-button>
          </el-radio-group>
          <div class="typeDescription">拼场预约的说明</div>
        </div>
      </div> -->
      <div class="reservationItem">
        <div class="reservationItemLabel">预约场次</div>
        <div class="reservationItemBox">
          <div v-if="reservationType !== 'match' && !selectedAvailability || 
          reservationType === 'match' && !selectedTimeslot" class="noSelectDisplay">未选择场次</div>
          <div v-else class="reservationVenueDisplay">
            <div class="reservationVenueName">{{ venueInfo.venueName }}</div>
            <div class="reservationVenueId">(ID: {{ venueInfo.venueId }})</div>
            <div class="reservationTimeslot">
              {{ getTimeslotDisplay(selectedTimeslot.startTime, selectedTimeslot.endTime, true) }}
            </div>
            <div class="reservationCourtName" v-if="reservationType !== 'match'">
              {{ findCourtInfo(selectedAvailability.courtId).courtName }}
            </div>
          </div>
        </div>
        <el-button class="reservationItemControl"
        @click="timeslotSelectionRef.showTimeslotSelection(reservationType === 'match')">更改</el-button>
      </div>
      <div class="reservationItem" v-if="reservationType === 'match'">
        <div class="reservationItemLabel">预约场地类型</div>
        <el-select class="courtTypeSelect" size="large" v-model="matchCourtType">
          <el-option v-for="type in venueCourtType" :value="type" :label="type"></el-option>
        </el-select>
      </div>
      <div class="reservationItem" v-if="reservationType === 'group'">
        <div class="reservationItemLabel">预约团体</div>
        <div class="reservationItemBox" v-if="selectedGroup">
          <div class="reservationGroupName">{{ selectedGroup.groupName }}</div>
          <div class="reservationGroupId">(ID: {{ selectedGroup.groupId }})</div>
        </div>
        <div class="reservationItemBox" v-else>
          <div class="noSelectDisplay">未选择团体</div>
        </div>
        <el-button class="reservationItemControl" @click="groupSelectRef.showGroupSelect()">更改</el-button>
      </div>
      <div class="reservationItem">
        <div class="reservationItemLabel">预约成员</div>
        <div class="reservationItemBox">
          <div v-if="reservationUserInput.length === 0" class="noSelectDisplay">
            未邀请预约成员
          </div>
          <div class="inviteUserList" v-else>
            <div class="inviteUserItem" v-for="invitedUser in reservationUserInput">
              <img class="inviteUserAvatar" :src="invitedUser.photo">
              <div class="inviteUserName">{{ invitedUser.userName }}</div>
              <div class="inviteUserId">(ID: {{ invitedUser.userId }})</div>
            </div>
          </div>
        </div>
        <el-button class="reservationItemControl" :disabled="reservationType === 'group' && !selectedGroup"
        @click="inviteUserDialogRef.showInviteUser(reservationUserInput, reservationType === 'group' ? selectedGroup.groupId : null)">
          邀请
        </el-button>
      </div>
      <div class="reservationItem" v-if="reservationType === 'match'">
        <div class="reservationItemLabel">预约人数</div>
        <el-input-number :min="1" v-model="matchReserveCount"></el-input-number>
      </div>
    </div>
    <div class="reservationControl">
      <el-button size="large" type="primary" @click="handleReservation(userId)">预约</el-button>
    </div>
  </div>
  <el-dialog v-model="reservationResultDialog" :title="reservationResultTitle">
    {{ reservationResultContent }}
    <template #footer>
      <el-button type="primary" @click="reservationResultDialog = false">确定</el-button>
    </template>
  </el-dialog>
  <TimeslotSelection ref="timeslotSelectionRef"></TimeslotSelection>
  <GroupSelectDialog ref="groupSelectRef"></GroupSelectDialog>
  <InviteUserDialog ref="inviteUserDialogRef" @invite-confirm="handleInviteUserConfirm"></InviteUserDialog>
  <ReservationConfirm ref="reservationConfirmRef"></ReservationConfirm>
</template>

<style scoped>
.venueReservation{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.reservationMain{
  flex: 1;
  --label-width: 120px;
  padding: 10px;
}

.reservationControl{
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.reservationControl>button{
  width: 100%;
}

.reservationTypeInfo{
  border: 1px solid var(--lightgray-border);
  border-radius: 5px;
}

.reservationTypeTitle{
  font-weight: 700;
  font-size: 1.2em;
  color: var(--theme-darkblue);
  background-color: var(--lightblue-bg);
  padding: 10px;
  border-bottom: 1px solid var(--lightgray-border);
}

.reservationTypeText{
  color: var(--gray-text);
  padding: 10px;
  line-height: 2em;
}

.reservationItem{
  display: flex;
  align-items: center;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
  padding: 10px;
}

.reservationItemLabel{
  width: var(--label-width);
}

.typeDescription{
  margin-top: 5px;
  font-size: 0.8em;
  color: var(--lightblue-text);
}

.reservationItemBox{
  display: flex;
  flex: 1;
  border: 1px solid var(--lightgray-border);
  background-color: white;
  border-radius: 5px;
  padding: 10px;
}

.reservationItemControl{
  margin-left: 10px;
}

.reservationVenueDisplay{
  display: flex;
}

.reservationVenueId{
  margin-left: 5px;
  color: var(--gray-text);
}

.reservationTimeslot{
  margin-left: 20px;
}

.reservationCourtName{
  margin-left: 20px;
}

.noSelectDisplay{
  color: var(--lightgray-text);
}

:deep(.el-select--large){
  width: calc(100% - var(--label-width));
}

.reservationGroupId{
  color: var(--gray-text);
  margin-left: 5px;
}

.inviteUserList{
  display: flex;
  flex-wrap: wrap;
}

.inviteUserItem{
  display: flex;
  align-items: center;
  padding-top: 5px;
  padding-bottom: 5px;
  margin-right: 20px;
}

.inviteUserAvatar{
  width: 40px;
  height: 40px;
  border-radius: 4px;
}

.inviteUserName{
  margin-left: 10px;
}

.inviteUserId{
  margin-left: 10px;
  color: var(--gray-text);
}

</style>