<script setup>
import { onMounted } from 'vue';
import { getVenueReservations, managerReservationPage, managerReservationPageUpdate, venueReservations } from '@/boundaries/managementBoundary';
import { getTimeslotDisplay } from '@/boundaries/utils';
import { reservationTypeEnum, reservationStateEnum } from '@/boundaries/reservationBoundary';
import { useRouter } from 'vue-router';

onMounted(() => {
  getVenueReservations({ page: 0 });
});

const router = useRouter();

const viewReservationDetail = (item) => {
  router.push({
    path: '/management/reservations/detail',
    query: { id: item.reservationId },
  });
};

</script>

<template>
  <div class="reservationList">
    <!-- <div class="searchArea">
      <el-input :suffix-icon="Search" placeholder="预约记录号" v-model="reservationSearch"></el-input>
      <el-button type="primary">搜索</el-button>
    </div> -->
    <div class="listArea">
      <el-table :data="venueReservations" :show-overflow-tooltip="{'effect': 'light'}" 
      row-class-name="reservationItem" @current-change="viewReservationDetail" class="reservationTable">
        <el-table-column prop="reservationId" label="预约号" width="100"></el-table-column>
        <el-table-column prop="courtId" label="场地号" width="100"></el-table-column>
        <el-table-column prop="courtName" label="场地"></el-table-column>
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
        <!-- <el-table-column prop="groupId" label="团体号" width="100"></el-table-column>
        <el-table-column prop="groupName" label="团体名称" width="200"></el-table-column> -->
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <div class="reservationState" :data-state="scope.row.state">
              {{ reservationStateEnum[scope.row.state] }}
            </div>
          </template>
        </el-table-column>
      </el-table> 
    </div>
    <div class="pageArea">
      <el-pagination background layout="prev, pager, next" :total="managerReservationPage.total" 
      :current-page="managerReservationPage.curPage" :page-size="20" @current-change="managerReservationPageUpdate"></el-pagination>
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
  overflow: auto;
  margin: 10px;
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

.reservationState[data-state="normal"]{
  color: var(--green-text);
}

.reservationState[data-state="matching"]{
  color: var(--lightblue-text);
}

.reservationState[data-state="pending"]{
  color: var(--yellow-text);
}

.reservationState[data-state="cancelled"]{
  color: var(--gray-text);
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