<template>
  <div class="notificationList">
    <div class="listArea">
      <el-table :data="notificationData" :show-overflow-tooltip="{'effect': 'light'}" class="notificationTable">
        <el-table-column prop="notificationId" label="通知ID" width="100"></el-table-column>
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column label="时间" width="200">
          <template #default="scope">
            {{ getLocalTimeDateDisplay(scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <div class="notificationStateDisplay" :data-state="scope.row.state">
              {{ notificationStateEnum[scope.row.state] }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="handleNotificationDetail(scope.row)">
              详情
            </el-button>
            <el-button size="small"  @click="handleMarkNotification(scope.row)">
              <div v-if="scope.row.state !== 'mark'">添加标记</div>
              <div v-else>取消标记</div>
            </el-button>
            <el-button size="small" type="danger" @click="deleteNotification(scope.row)">
              删除
            </el-button>
            <el-button size="small" type="primary" v-if="scope.row.state === 'unread'" @click="handleReadNotification(scope.row)">
              已读
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
  <el-dialog v-model="showNotificationDetail" :title="notificationDetail.title" width="500">
    <div class="notificationContent">{{ notificationDetail.content }}</div>
    <div class="notificationInfo">
      <div class="notificationId">ID: {{ notificationDetail.notificationId }}</div>
      <div class="notificationTime">{{ getLocalTimeDateDisplay(notificationDetail.timestamp) }}</div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="danger" @click="deleteNotification(notificationDetail)">
          删除
        </el-button>
        <el-button @click="showNotificationDetail = false">取消</el-button>
        <el-button @click="handleMarkNotification(notificationDetail)" v-if="notificationDetail.state !== 'mark'">
          添加标记
        </el-button>
        <el-button @click="handleMarkNotification(notificationDetail)" v-else>
          取消标记
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>


<script setup>
import {
  getNotificationList, notificationData, notificationStateEnum, deleteNotification, handleNotificationDetail,
  showNotificationDetail, handleMarkNotification, notificationDetail,
  handleReadNotification
} from '../boundaries/accountBoundary';
import { onMounted } from 'vue';
import { getLocalTimeDateDisplay } from '../boundaries/utils';

onMounted(() => {
  getNotificationList();
});

</script>

<style scoped>
.notificationList{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.listArea{
  margin: 10px;
  flex: 1;
  overflow: auto;
  border: 1px solid var(--lightgray-border);
}

.notificationTable{
  height: 100%;
}

.searchArea{
  display: flex;
  background-color: var(--gray-bg);
  padding: 10px;
}

.notificationStateDisplay[data-state="read"]{
  color: var(--green-text);
}

.notificationStateDisplay[data-state="unread"]{
  color: var(--gray-text);
}

.notificationStateDisplay[data-state="mark"]{
  color: var(--red-text);
}

.notificationContent{
  margin-bottom: 10px;
}

.notificationInfo{
  display: flex;
}

.notificationId{
  color: var(--gray-text);
}

.notificationTime{
  color: var(--gray-text);
  margin-left: auto;
}

</style>