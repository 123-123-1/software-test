<script setup>
import { Location } from '@element-plus/icons-vue';
import {
  venueStateEnum, courtStateEnum, venueInfo, venueComments, venueDate, venueCommentInput,
  venueDetailRequestCount, commentCount, venueCommentPage, commentSuccessDialog, 
  getVenueDetail, getVenueCourts, getVenueTimeslots, getVenueComments, postVenueComments,
  processCommentTime, commentPageUpdate, 
} from '@/boundaries/venueBoundary'
import { selectedTimeslot, selectedAvailability } from '@/boundaries/reservationBoundary';

import TimeslotView from '@/components/TimeslotView.vue';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { dayjs } from 'element-plus';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const { selectedVenueId } = storeToRefs(userStore);

onMounted(async () => {
  const { venueId } = route.query;
  if(!venueId){
    venueDetailRequestCount.value = -1;
    return;
  }
  selectedVenueId.value = venueId;
  venueDate.value = dayjs(Date.now()).format();
  await getVenueDetail(venueId);
  await getVenueCourts(venueId);
  getVenueTimeslots(venueId);
  getVenueComments(venueId, 0);
});

const linkReservation = (availability, timeslot) => {
  selectedTimeslot.value = timeslot;
  selectedAvailability.value = availability;
  router.push('/reservations/reserve');
};

</script>

<template>
  <div class="venueDetail">
    <div v-if="venueDetailRequestCount === -1" class="detailErrPage">
      查找场馆信息失败，请检查查询参数或刷新重试
    </div>
    <div v-else>
      <div class="infoBlock">
        <div class="infoBlockTitle">基本信息</div>
        <div class="basicInfoArea">
          <img class="venueImage" :src="venueInfo.image" />
          <div class="basicInfoText">
            <div class="venueTitle">{{ venueInfo.venueName }}</div>
            <div class="basicInfoItem">
              <div class="basicInfoLabel">简介</div>
              <div class="basicInfoContent">{{ venueInfo.description }}</div>
            </div>
            <div class="basicInfoItem">
              <div class="basicInfoLabel">编号</div>
              <div class="basicInfoContent">{{ venueInfo.venueId }}</div>
            </div>
            <div class="basicInfoItem">
              <div class="basicInfoLabel">地址</div>
              <div class="basicInfoContent">
                <el-icon :size="20"><Location></Location></el-icon>
                <div style="margin-left: 10px;">{{ venueInfo.location }}</div>
              </div>
            </div>
            <div class="basicInfoItem">
              <div class="basicInfoLabel">联系电话</div>
              <div class="basicInfoContent">{{ venueInfo.contactNumber }}</div>
            </div>
            <div class="basicInfoItem">
              <div class="basicInfoLabel">开放状态</div>
              <div class="basicInfoContent venueStateDisplay" :data-state="venueInfo.state">
                {{ venueStateEnum[venueInfo.state] }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">场地信息</div>
        <el-table :data="venueInfo.courts" :show-overflow-tooltip="{ effect: 'light'}">
          <el-table-column label="编号" prop="courtId" width="100"></el-table-column>
          <el-table-column label="名称" prop="courtName"></el-table-column>
          <el-table-column label="容量" prop="capacity" width="100"></el-table-column>
          <el-table-column label="类型" prop="type" width="150"></el-table-column>
          <el-table-column label="状态" width="state">
            <template #default="scope">
              <div class="venueState" :data-state="scope.row.state">{{ courtStateEnum[scope.row.state] }}</div>
            </template>
          </el-table-column>
          <el-table-column label="位置" prop="location"></el-table-column>
        </el-table>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">预约时间段</div>
        <TimeslotView @timeslot-click="linkReservation"></TimeslotView>
      </div>
      <div class="infoBlock">
        <div class="infoBlockTitle">场馆评价</div>
        <div class="commentInputArea">
          <el-input type="textarea" placeholder="评价该场地..." v-model="venueCommentInput.content"></el-input>
          <div class="commentControls">
            <el-rate allow-half class="rateControl" v-model="venueCommentInput.score"></el-rate>
            <el-button type="primary" @click="postVenueComments">发布</el-button>
          </div>
        </div>
        <div class="commentItem" v-for="comment in venueComments">
          <div class="commentHeader">
            <img class="commentUserAvatar" :src="comment.userPhoto" />
            <div class="commentUserProfile">
              <div class="commentUserName">{{ comment.userName }}</div>
              <div class="commentTime">{{ processCommentTime(comment.time) }}</div>
            </div>
            <el-rate disabled v-model="comment.score" class="commentRate"></el-rate>
          </div>
          <div class="commentContent">{{ comment.content }}</div>
        </div>
        <div class="pageSelectArea">
          <el-pagination layout="prev, pager, next" :total="commentCount"
          :current-page="venueCommentPage" @current-change="commentPageUpdate"></el-pagination>
        </div>
      </div>
    </div>
  </div>
  <el-dialog v-model="commentSuccessDialog" title="发布评价成功">
    已成功发布场馆评价
    <template #footer>
      <el-button type="primary" @click="commentSuccessDialog = false">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.venueDetail{
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

.basicInfoArea{
  display: flex;
  padding: 10px;
}

.venueImage{
  width: min(50%, 480px);
  height: 270px;
}

.basicInfoText{
  margin-left: 10px;
  padding-left: 10px;
  padding-right: 10px;
}

.venueTitle{
  font-size: 18px;
  padding-top: 10px;
  padding-bottom: 10px;
  font-weight: 700;
}

.basicInfoItem{
  display: flex;
  padding-top: 10px;
  padding-bottom: 10px;
}

.basicInfoLabel{
  width: 100px;
}

.basicInfoContent{
  display: flex;
  align-items: center;
  color: var(--gray-text);
}

.venueStateDisplay[data-state="open"]{
  color: var(--green-text);
}

.venueStateDisplay[data-state="closed"]{
  color: var(--red-text);
}

.venueState[data-state="open"]{
  color: var(--green-text);
}

.venueState[data-state="closed"]{
  color: var(--red-text);
}

.commentInputArea{
  padding: 10px;
  background-color: var(--lightgray-bg);
  border-bottom: 1px solid var(--lightgray-border);
}

.commentControls{
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.rateControl{
  margin-right: 20px;
}

.commentItem{
  padding: 10px;
  background-color: white;
  border-bottom: 1px solid var(--lightgray-border);
}

.commentHeader{
  display: flex;
  align-items: center;
}

.commentUserAvatar{
  width: 50px;
  height: 50px;
  border-radius: 5px;
}

.commentUserProfile{
  margin-left: 10px;
}

.commentUserName{
  padding-top: 5px;
  padding-bottom: 5px;
  font-size: 1em;
}

.commentTime{
  font-size: 0.8em;
  color: var(--gray-text);
}

.commentRate{
  margin-left: auto;
}

.commentContent{
  margin-top: 5px;
  color: var(--gray-text);
}

.pageSelectArea{
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: white;
  padding-top: 5px;
  padding-bottom: 5px;
}

.detailErrPage{
  display: flex;
  justify-content: center;
  color: gray;
  font-size: 18px;
}

</style>