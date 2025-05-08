<script setup>
import {
  incrementVenueDate, venueDate, handleVenueDateChange, venueTimeslots,
  findCourtInfo, availabilityEnum,
  checkAvailabilitySelection
} from '@/boundaries/venueBoundary';
import { getTimeslotDisplay } from '@/boundaries/utils';
import { ref } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';

const emit = defineEmits(['timeslotClick']);
const selectedTimeslot = ref(0);
const handleTimeslotClick = (availability) => {
  if(availability.state !== 'reserveable'){
    return;
  }
  if(!checkAvailabilitySelection(venueTimeslots.value[selectedTimeslot.value].startTime)){
    return;
  }
  emit('timeslotClick', availability, venueTimeslots.value[selectedTimeslot.value]);
};

</script>

<template>
  <div class="dateSelectArea">
    <el-button size="small" @click="incrementVenueDate(-1)">&lt;</el-button>
    <el-date-picker size="small" placeholder="选择时间段" v-model="venueDate"
      @change="handleVenueDateChange"></el-date-picker>
      <el-button size="small" @click="incrementVenueDate(1)">&gt;</el-button>
  </div>
  <!-- <el-collapse>
    <el-collapse-item v-for="timeslotItem in venueBoundary.venueTimeslots.value"
    :title="getTimeslotDisplay(timeslotItem.timeslot.startTime, timeslotItem.timeslot.endTime, false)">
      <div class="courtTimeslotItem" v-for="courtTimeslot in timeslotItem.courtAvailabilities" :data-state="courtTimeslot.state"
      @click="emit('timeslotClick', courtTimeslot, timeslotItem)">
        <div class="courtTimeslotName">{{ venueBoundary.findCourtInfo(courtTimeslot.courtId).courtName }}</div>
        <div class="courtTimeslotType">{{ venueBoundary.findCourtInfo(courtTimeslot.courtId).type }}</div>
        <div class="courtTimeslotPrice">￥{{ courtTimeslot.price }}</div>
        <div class="courtTimeslotState">{{ venueBoundary.availabilityEnum[courtTimeslot.state] }}</div>
      </div>
    </el-collapse-item>
  </el-collapse> -->
  <div class="noTimeslotDisplay" v-if="venueTimeslots.length === 0">
    当前日期没有可用的开放时间段
  </div>
  <div class="timeslotMain" v-else>
    <div class="timeslotSelect">
      <el-radio-group v-model="selectedTimeslot">
        <el-radio-button v-for="(timeslotItem, index) in venueTimeslots" :value="index" class="timeslotButton"
        >{{ getTimeslotDisplay(timeslotItem.startTime, timeslotItem.endTime, false) }}</el-radio-button>
      </el-radio-group>
    </div>
    <div class="availabilitySelect">
      <div class="courtTimeslotItem" v-for="courtTimeslot in venueTimeslots[selectedTimeslot]?.courtAvailabilities"
      :data-state="courtTimeslot.state" @click="handleTimeslotClick(courtTimeslot)">
        <div class="courtTimeslotName">{{ findCourtInfo(courtTimeslot.courtId)?.courtName }}</div>
        <div class="courtTimeslotId">(ID: {{ courtTimeslot.courtId }})</div>
        <div class="courtTimeslotType">{{ findCourtInfo(courtTimeslot.courtId)?.type }}</div>
        <div class="courtTimeslotPrice">￥{{ courtTimeslot.price }}</div>
        <div class="courtTimeslotState">{{ availabilityEnum[courtTimeslot.state] }}</div>
        <el-icon v-if="courtTimeslot.state === 'reserveable'" size="20" color="var(--gray-text)"><arrow-right></arrow-right></el-icon>
        <div class="reservationLinkBlank" v-else></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dateSelectArea{
  display: flex;
  justify-content: center;
  padding-top: 5px;
  padding-bottom: 5px;
}

.timeslotMain{
  display: flex;
}

.timeslotSelect{
  width: 10em;
  border: 1px solid var(--lightgray-border);
}

:deep(.el-radio-group) {
  border: none;
  width: 100%;
  border-radius: 0;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 0;
  border-left: none;
}

:deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0;
}

:deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  border: none;
  width: 100%;
  line-height: 2em;
}

.timeslotButton{
  width: 100%;
}

.availabilitySelect{
  flex: 1;
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  background-color: white;
}

.courtTimeslotItem{
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  box-shadow: 0px 2px 4px  rgba(0, 0, 0, 0.25);
  line-height: 2em;
  width: calc(100% - 20px);
  margin-bottom: 10px;
  border-radius: 5%;
}

.courtTimeslotItem[data-state="reserveable"]:hover{
  cursor: pointer;
  background-color: var(--green-bg);
}

.courtTimeslotItem[data-state="matching"]:hover{
  background-color: var(--yellow-bg);
}

.courtTimeslotItem[data-state="full"]:hover, .courtTimeslotItem[data-state="closed"]:hover{
  background-color: var(--gray-bg);
}

.courtTimeslotItem[data-state="full"], .courtTimeslotItem[data-state="closed"]{
  color: var(--lightgray-text);
}

.courtTimeslotId{
  color: var(--gray-text);
  margin-left: 10px;
}

.courtTimeslotPrice{
  margin-left: 10px;
  width: calc(4em + 20px);
}

.courtTimeslotItem[data-state="reserveable"]>.courtTimeslotPrice,
.courtTimeslotItem[data-state="matching"]>.courtTimeslotPrice{
  font-weight: 700;
  color: var(--red-text);
}

.courtTimeslotType{
  margin-left: auto;
}

.courtTimeslotState{
  width: calc(4em + 20px);
}

.courtTimeslotItem[data-state="reserveable"]>.courtTimeslotState{
  color: var(--green-text);
}

.courtTimeslotItem[data-state="matching"]>.courtTimeslotState{
  color: var(--yellow-text);
}

.noTimeslotDisplay{
  display: flex;
  justify-content: center;
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  background-color: white;
  color: var(--gray-text);
}

/* :deep(.el-collapse-item__header){
  padding-left: 10px;
  padding-right: 10px;
  font-size: 1em;
}

:deep(.el-collapse-item__wrap){
  width: 100%;
}

:deep(.el-collapse-item__content){
  display: flex;
  width: 100%;
  font-size: 1em;
  flex-wrap: wrap;
}

.courtTimeslotItem{
  display: flex;
  width: calc(50% - 22px);
  border: 1px solid var(--lightgray-border);
  padding: 10px;
}

.courtTimeslotItem[data-state="reserveable"]{
  background-color: var(--green-bg);
}

.courtTimeslotItem[data-state="full"]{
  background-color: var(--gray-bg);
  color: var(--lightgray-text);
}

.courtTimeslotItem[data-state="closed"]{
  background-color: var(--gray-bg);
  color: var(--lightgray-text);
}

.courtTimeslotItem[data-state="matching"]{
  background-color: var(--yellow-bg);
}

.courtTimeslotType{
  color: var(--gray-text);
  margin-left: 5px;
}

.courtTimeslotItem[data-state="full"]>.courtTimeslotType{
  color: var(--lightgray-text);
}

.courtTimeslotPrice{
  margin-left: auto;
  font-weight: 700;
  color: var(--red-text);
}

.courtTimeslotState{
  margin-left: 20px;
}

.courtTimeslotItem[data-state="reserveable"]>.courtTimeslotState{
  color: var(--green-text);
}

.courtTimeslotItem[data-state="matching"]>.courtTimeslotState{
  color: var(--yellow-text);
}

.courtTimeslotItem[data-state="reserveable"]:hover{
  cursor: pointer;
  background-color: var(--lightblue-bg);
  border: 1px solid var(--lightblue-text);
} */

</style>