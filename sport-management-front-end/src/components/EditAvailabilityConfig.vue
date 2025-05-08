<script setup>
import { createAvailabilityConfig, getAvailabilityConfig, patchAvailabilityConfig, repetitionDayEnum } from '@/boundaries/managementBoundary';
import { parseTimeStr } from '@/boundaries/utils';
import { ref } from 'vue';

const editAvailabilityConfigVisible = ref(false);
const editingCourt = ref({});
const editingConfig = ref(null);
const editMode = ref('');
const editErrorDialog = ref({
  visible: false,
  errmsg: '',
});

const showEditAvailabilityConfig = (courtAvconfig, configIndex) => {
  if(configIndex >= 0){
    editingConfig.value = JSON.parse(JSON.stringify(courtAvconfig.config[configIndex]));
    editingConfig.value.courtId = courtAvconfig.courtId;
    for(const tsconfigItem of editingConfig.value.tsconfig){
      tsconfigItem.timeRange = [ parseTimeStr(tsconfigItem.startTime), parseTimeStr(tsconfigItem.endTime) ]
    }
    editMode.value = 'edit';
  }
  else{
    editingConfig.value = {
      avconfigName: '',
      courtId: courtAvconfig.courtId,
      tsconfig: [],
      repetition: [],
      dayAhead: 7,
      createHour: 4,
    };
    editMode.value = 'create';
  }
  editingCourt.value = {
    courtId: courtAvconfig.courtId,
    courtName: courtAvconfig.courtName,
  };
  editAvailabilityConfigVisible.value = true;
};

const addTsconfig = () => {
  editingConfig.value.tsconfig.push({
    timeRange: [],
    price: 0,
  });
};

const deleteTsconfig = (index) => {
  editingConfig.value.tsconfig.splice(index, 1);
};

const validateEdit = () => {
  const MINTIMESLOTLEN = 10;
  if(!editingConfig.value.avconfigName){
    showErrDialog('配置名称不能为空');
    return false;
  }
  if(editingConfig.value.tsconfig.length === 0){
    showErrDialog('配置的时间段不能为空');
    return false;
  }
  if(editingConfig.value.repetition.length === 0){
    showErrDialog('配置的重复不能为空');
    return false;
  }
  for(const { timeRange } of editingConfig.value.tsconfig){
    if(timeRange.length < 2){
      showErrDialog('配置时间段的开始时间和结束时间不能为空');
      return false;
    }
    if(timeRange[1].getTime() - timeRange[0].getTime() < MINTIMESLOTLEN * 60 * 1000){
      showErrDialog(`单个时间段的长度不能小于${MINTIMESLOTLEN}分钟`);
      return false;
    }
  }
  const sortedTimeRange = editingConfig.value.tsconfig.map(ts => ts.timeRange).sort((tr1, tr2) => tr1[0].getTime() - tr2[0].getTime());
  for(let i = 1; i < sortedTimeRange.length; i++){
    const prevEnd = sortedTimeRange[i - 1][1].getHours() * 60 + sortedTimeRange[i - 1][1].getMinutes();
    const curStart = sortedTimeRange[i][0].getHours() * 60 + sortedTimeRange[i][0].getMinutes();
    if(curStart < prevEnd){
      const errMsg = `时间段发生重合：${formatTimeslot(sortedTimeRange[i - 1][0])}-${formatTimeslot(sortedTimeRange[i - 1][1])} \
        与 ${formatTimeslot(sortedTimeRange[i][0])}-${formatTimeslot(sortedTimeRange[i][1])}`;
      showErrDialog(errMsg);
      return false;
    }
  }
  return true;
};

const handleConfirmEdit = () => {
  if(!validateEdit()){
    return;
  }
  for(const timeslotItem of editingConfig.value.tsconfig){
    timeslotItem.startTime = formatTimeslot(timeslotItem.timeRange[0]);
    timeslotItem.endTime = formatTimeslot(timeslotItem.timeRange[1]);
  }
  if(editMode.value === 'create'){
    createAvailabilityConfig(editingConfig.value, handleEditSuccess, handleEditErr);
  }
  else if(editMode.value === 'edit'){
    patchAvailabilityConfig(editingConfig.value, handleEditSuccess, handleEditErr);
  }
};

const handleEditSuccess = (res) => {  
  editAvailabilityConfigVisible.value = false;
  getAvailabilityConfig();
};

const handleEditErr = (err) => {
  showErrDialog(err.response?.data?.msg || err);
}

const showErrDialog = (msg) => {
  editErrorDialog.value.errmsg = msg;
  editErrorDialog.value.visible = true;
};

const formatTimeslot = (time) => {
  return time.getHours().toString().padStart(2, '0') + ':' +  time.getMinutes().toString().padStart(2, '0')
};

defineExpose({ showEditAvailabilityConfig });

</script>

<template>
  <el-dialog align-center title="编辑配置项" v-model="editAvailabilityConfigVisible">
    <div class="sectionHeader">
      <div class="sectionLabel">配置场地项：</div>
      <div class="courtNameDisplay">{{ editingCourt.courtName }}</div>
      <div class="courtIdDisplay">(ID: {{ editingCourt.courtId }})</div>
    </div>
    <div class="sectionHeader">
      <div class="sectionLabel">配置名称：</div>
      <el-input v-model="editingConfig.avconfigName" placeholder="输入配置名称" class="avconfigNameInput"></el-input>
    </div>
    <div class="sectionHeader">
      <div class="sectionLabel">时间段：</div>
      <el-button class="addtsconfigButton" type="primary" @click="addTsconfig">添加时间段</el-button>
    </div>
    <div class="tsconfigDisplay">
      <div class="tsconfigItemDisplay" v-for="(tsconfigItem, tsconfigIndex) in editingConfig.tsconfig">
        <div class="timeslotSelect">
          <el-time-picker v-model="tsconfigItem.timeRange" is-range format="HH:mm"></el-time-picker>
        </div>
        <div class="priceInputArea">
          <div class="priceInputText">价格：</div>
          <el-input-number :min="0" :precision="2" :max="9999.99" :step="0.01" v-model="tsconfigItem.price" class="priceInputItem"></el-input-number>
        </div>
        <el-button type="danger" @click="deleteTsconfig(tsconfigIndex)">删除</el-button>
      </div>
    </div>
    <div class="sectionHeader">
      <div class="sectionLabel">提前预约天数：</div>
      <el-input-number :min="1" :max="365" v-model="editingConfig.dayAhead"></el-input-number>
    </div>
    <div class="sectionHeader">
      <div class="sectionLabel">开放预约时间：</div>
      <el-input-number :min="0" :max="23" v-model="editingConfig.createHour"></el-input-number>
      <div>&nbsp;时</div>
    </div>
    <div class="sectionHeader">
      <div class="sectionLabel">重复：</div>
      <el-select multiple v-model="editingConfig.repetition" class="repetitionSelect">
        <el-option v-for="weekday in Object.keys(repetitionDayEnum)" :value="weekday" :label="repetitionDayEnum[weekday]"></el-option>
      </el-select>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleConfirmEdit">保存</el-button>
      <el-button @click="editAvailabilityConfigVisible = false">取消</el-button>
    </template>
  </el-dialog>
  <el-dialog v-model="editErrorDialog.visible" title="编辑失败" >
    <div class="errorMsgDisplay">{{ editErrorDialog.errmsg }}</div>
    <template #footer>
      <el-button type="primary" @click="editErrorDialog.visible = false">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
:deep(.el-input__wrapper){
  width: 100%;
}

.sectionHeader{
  display: flex;
  align-items: center;
  font-size: 1.2em;
  line-height: 1.5em;
  padding: 10px;
}

.avconfigNameInput{
  flex: 1;
}

.courtIdDisplay{
  margin-left: 10px;
  color: var(--gray-text);
}

.addtsconfigButton{
  margin-left: auto;
}

.tsconfigDisplay{
  margin: 10px;
  border: 1px solid var(--lightgray-border);
  overflow: auto;
  height: clamp(200px, 35vh, 400px);
}

.tsconfigItemDisplay{
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
}

.timeslotSelect{
  width: clamp(150px, 35%, 250px);
}

.priceInputArea{
  display: flex;
  align-items: center;
}

.priceInputItem{
  flex: 1;
}

.repetitionSelect{
  flex: 1;
}

.errorMsgDisplay{
  font-size: 1.2em;
}

</style>