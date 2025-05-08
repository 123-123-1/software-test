<script setup>
import { Search } from '@element-plus/icons-vue';
import {
  reservationSearch, userReservationList, reservationTypeEnum, reservationUserStateEnum,
  getReservationList, reservationStateEnum,
  reservationPageInfo,
  reservationPageUpdate
} from '@/boundaries/reservationBoundary'
import { getTimeslotDisplay } from '@/boundaries/utils';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const viewReservationDetail = (item) => {
  router.push({
    path: '/reservations/detail',
    query: { id: item.reservationId },
  });
};

onMounted(() => {
  getReservationList(0);
});

</script>

<template>
  <div class="reservationList">
    <div class="searchArea">
      <el-input :suffix-icon="Search" placeholder="预约记录号" v-model="reservationSearch"></el-input>
      <el-button type="primary">搜索</el-button>
    </div>
    <div class="listArea">
      <el-table :data="userReservationList" :show-overflow-tooltip="{'effect': 'light'}" class="reservationTable" 
      row-class-name="reservationItem" @current-change="viewReservationDetail">
        <el-table-column prop="reservationId" label="预约号" width="100"></el-table-column>
        <el-table-column prop="venueName" label="场馆"></el-table-column>
        <el-table-column prop="courtName" label="场地" width="200"></el-table-column>
        <el-table-column label="预约时间段" width="200">
          <template #default="scope">
            {{ getTimeslotDisplay(scope.row.startTime, scope.row.endTime, true) }}
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="scope">
            {{ reservationTypeEnum[scope.row.type] }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <div class="reservationStateDisplay" v-if="scope.row.state !== 'normal' && scope.row.userState !== 'violated'" :data-state="scope.row.state">
              {{ reservationStateEnum[scope.row.state] }}
            </div>
            <div class="reservationStateDisplay" v-else :data-state="scope.row.userState">
              {{ reservationUserStateEnum[scope.row.userState] }}
            </div>
          </template>
        </el-table-column>
      </el-table> 
    </div>
    <div class="pageArea">
      <el-pagination background layout="prev, pager, next" :total="reservationPageInfo.total" :page-size="20" 
      :current-page="reservationPageInfo.curPage" @current-change="reservationPageUpdate"></el-pagination>
    </div>
  </div>

</template>

<style scoped>
.reservationList{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.listArea{
  flex: 1;
  margin: 10px;
  overflow: auto;
  border: 1px solid var(--lightgray-border); 
}

.reservationTable{
  height: 100%;
}

.searchArea{
  display: flex;
  background-color: var(--gray-bg);
  padding: 10px;
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

.reservationStateDisplay[data-state="cancelled"]{
  color: var(--gray-text);
}

.reservationStateDisplay[data-state="violated"]{
  color: var(--red-text);
}

.reservationItem:hover{
  background-color: var(--lightblue-bg);
  text-decoration: underline;
}

.pageArea{
  display: flex;
  justify-content: center;
  padding: 10px;
  border: 1px solid var(--lightgray-border);
  background-color: var(--lightgray-bg);
}

</style>