<script setup>
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { dayjs } from 'element-plus';
import { ref } from 'vue';
import {
  getVenueDetail, getVenueCourts, getVenueTimeslots, venueDate, incrementVenueDate, handleVenueDateChange,
  venueTimeslots,
  checkAvailabilitySelection,
} from '@/boundaries/venueBoundary';
import { selectedAvailability, selectedTimeslot } from '@/boundaries/reservationBoundary';
import TimeslotView from './TimeslotView.vue';
import { getTimeslotDisplay } from '@/boundaries/utils';

const userStore = useUserStore();
const { selectedVenueId } = storeToRefs(userStore); 
const timeslotSelectionDialog = ref(false);
const isGroupSelect = ref(false);

const showTimeslotSelection = async (isMatch) => {
  if(selectedVenueId.value !== -1){
    await getVenueDetail(selectedVenueId.value);
    await getVenueCourts(selectedVenueId.value);
    await getVenueTimeslots(selectedVenueId.value);
  }
  venueDate.value = dayjs(Date.now()).format();
  handleVenueDateChange();
  if(isMatch){
    isGroupSelect.value = true;
  }
  else{
    isGroupSelect.value = false;
  }
  timeslotSelectionDialog.value = true;
};

const handleSelect = (availability, timeslot) => {
  if(!checkAvailabilitySelection(timeslot.startTime)){
    return;
  }
  selectedTimeslot.value = timeslot;
  selectedAvailability.value = availability;
  timeslotSelectionDialog.value = false;
};

defineExpose({ showTimeslotSelection });

</script>

<template>
  <el-dialog title="选择预约时间段" v-model="timeslotSelectionDialog">
    <div v-if="selectedVenueId === -1">
      请从场地信息列表中选择某一场馆后再进入该页面选择时间段
    </div>
    <div>如果要更换场地，请从场地信息页面选择进入预约页面</div>
    <div v-if="isGroupSelect">
      <div class="dateSelectArea">
      <el-button size="small" @click="incrementVenueDate(-1)">&lt;</el-button>
      <el-date-picker size="small" placeholder="选择时间段" v-model="venueDate"
        @change="handleVenueDateChange"></el-date-picker>
        <el-button size="small" @click="incrementVenueDate(1)">&gt;</el-button>
      </div>
      <div class="timeslotList">
        <div class="timeslotItem" v-for="timeslot in venueTimeslots"
        @click="handleSelect(null, timeslot)">
          {{ getTimeslotDisplay(timeslot.startTime, timeslot.endTime) }}
        </div>
      </div>
    </div>
    <div class="timeslotDisplay" v-else>
      <TimeslotView @timeslot-click="handleSelect"></TimeslotView>
    </div>
    
    <template #footer>
      <el-button @click="timeslotSelectionDialog = false">取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.timeslotDisplay{
  margin-top: 10px;
  margin-bottom: 10px;
}

.dateSelectArea{
  display: flex;
  justify-content: center;
  padding-top: 5px;
  padding-bottom: 5px;
}

.timeslotList{
  display: flex;
  flex-wrap: wrap;
}

.timeslotItem{
  width: calc(50% - 22px);
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.timeslotItem:hover{
  cursor: pointer;
  background-color: var(--gray-bg);
}

</style>