<script setup>
import EditAvailabilityConfig from '@/components/EditAvailabilityConfig.vue';
import {
  availabilityConfigInfo, deleteAvailabilityConfig, deleteAvailabilityConfigDialog,
  getAvailabilityConfig, showAvailabilityConfigDelete
} from '@/boundaries/managementBoundary';
import { ref, onMounted } from 'vue';

const editAvailabilityConfigRef = ref(null);

onMounted(() => {
  getAvailabilityConfig();
});

</script>

<template>
  <div class="timeslotEdit">
    <div class="configAreaTitle">默认时间段配置</div>
    <div class="instructionArea">
      说明：系统会在指定的时间根据可提前预约的天数自动创建预约项
    </div>
    <div class="configArea">
      <el-collapse accordion>
        <el-collapse-item v-for="(courtAvconfig, courtIndex) in availabilityConfigInfo" class="courtItem">
          <template #title>
            <div class="courtTitle">
              <div class="courtNameDisplay">{{ courtAvconfig.courtName }}</div>
              <div class="courtIdDisplay">(ID: {{ courtAvconfig.courtId }})</div>
            </div>
          </template>
          <div class="configDisplay">
            <div class="configItemDisplay" v-for="(avconfigItem, avconfigIndex) in courtAvconfig.config">
              <div class="configNameDisplay">{{ avconfigItem.avconfigName }}</div>
              <el-button type="primary" class="configEditButton" @click="editAvailabilityConfigRef.showEditAvailabilityConfig(courtAvconfig, avconfigIndex)">
                编辑
              </el-button>
              <el-button type="danger" @click="showAvailabilityConfigDelete(courtIndex, avconfigIndex)">删除</el-button>
            </div>
            <div class="addConfigButton">
              <el-button type="primary" @click="editAvailabilityConfigRef.showEditAvailabilityConfig(courtAvconfig, -1)">添加配置</el-button>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
  <el-dialog v-model="deleteAvailabilityConfigDialog.visible" title="删除配置项确认">
    <div class="timeslotConfigDeleteText">
      <div>确认删除 {{ availabilityConfigInfo[deleteAvailabilityConfigDialog.courtIndex].courtName }} (ID: {{ availabilityConfigInfo[deleteAvailabilityConfigDialog.courtIndex].courtId }})
        的配置项 {{ availabilityConfigInfo[deleteAvailabilityConfigDialog.courtIndex].config[deleteAvailabilityConfigDialog.avconfigIndex].avconfigName }} 吗？
      </div>
      <div>已创建的开放时间段会被保留</div>
    </div>
    <template #footer>
      <el-button @click="deleteAvailabilityConfig" type="primary">确定</el-button>
      <el-button @click="deleteAvailabilityConfigDialog.visible = false">取消</el-button>
    </template>
  </el-dialog>
  <EditAvailabilityConfig ref="editAvailabilityConfigRef"></EditAvailabilityConfig>
</template>

<style scoped>
.configAreaTitle{
  padding: 10px;
  font-size: 1.2em;
  font-weight: 700;
  color: var(--theme-darkblue);
  margin: 10px;
  border-bottom: 1px solid var(--theme-darkblue);
}

:deep(.el-collapse-item__header){
  font-size: 1em;
  padding-left: 20px;
  padding-right: 20px;
}

:deep(.el-collapse-item__header.is-active){
  background-color: var(--lightblue-bg);
  color: var(--lightblue-text);  
}

:deep(.el-collapse-item__content){
  font-size: 1em;
}

.instructionArea{
  display: flex;
  color: var(--gray-text);
  margin: 10px;
}

.configArea{
  margin: 10px;
  border: 1px solid var(--lightgray-border);
}

.addConfigButton{
  display: flex;
  justify-content: flex-end;
  padding: 10px;
}

.courtTitle{
  display: flex;
}

.courtIdDisplay{
  margin-left: 10px;
  color: var(--gray-text);
}

.configDisplay{
  padding: 10px;
}

.configItemDisplay{
  display: flex;
  align-items: center;
  background-color: var(--lightgray-bg);
  border: 1px solid var(--lightgray-border);
  padding: 10px;
}

.configEditButton{
  margin-left: auto;
}

.timeslotConfigDeleteText{
  font-size: 1.2em;
  line-height: 2em;
}

</style>