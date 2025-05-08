<script setup>
import { Search, Location } from '@element-plus/icons-vue';
import {
  venueStateEnum, venueSearch, venueList, venueCount, venuePage,
  getVenueList, venuePageUpdate, resetVenueSearch
} from '@/boundaries/venueBoundary';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

onMounted(() => {
  getVenueList(0);
});

const router = useRouter();

const showVenueDetail = (id) => {
  router.push({
    path: '/venues/detail',
    query: { venueId: id}
  });
};

</script>

<template>
  <div class="venueList">
    <div class="searchArea">
      <el-input :prefix-icon="Search" placeholder="搜索场地"  v-model="venueSearch"></el-input>
      <el-button type="primary" @click="getVenueList(0)" style="margin-left: 10px;">搜索</el-button>
      <el-button @click="resetVenueSearch">重置</el-button>
    </div>
    <div class="listArea">
      <div class="venueItem" v-for="venue in venueList" :data-state="venue.state"
      @click="showVenueDetail(venue.venueId)">
        <img class="venueImage" :src="venue.image" />
        <div class="venueInfoLine">
          <div class="venueTitle">{{ venue.venueName }}</div>
          <div class="venueState" :data-state="venue.state">{{ venueStateEnum[venue.state] }}</div>
        </div>
        <div class="venueInfoLine">
          <div class="infoContent">ID: {{ venue.venueId }}</div>
        </div>
        <div class="venueInfoLine">
          <div class="infoContent">
            <el-icon :size="15"><Location></Location></el-icon>
            {{ venue.location }}
          </div>
        </div>
      </div>
    </div>
    <div class="pageArea">
      <el-pagination background layout="prev, pager, next" :total="venueCount" 
      :current-page="venuePage" @current-change="venuePageUpdate"></el-pagination>
    </div>
  </div>
</template>

<style scoped>
.venueList{
  display: flex;
  flex-direction: column;
  height: 100%;
}

.searchArea{
  display: flex;
  padding-left: 10px;
  padding-right: 10px;
  padding-top: 5px;
  padding-bottom: 5px;
  background-color: var(--gray-bg);
}

.listArea{
  flex: 1;
  overflow: auto;
  display: flex;
  flex-wrap: wrap;
  background-color: var(--lightgray-bg);
}

.pageArea{
  display: flex;
  justify-content: center;
  border-top: 1px solid var(--lightgray-border);
  border-bottom: 1px solid var(--lightgray-border);
  padding-top: 5px;
  padding-bottom: 5px;
}

.venueItem{
  padding: 15px;
  margin: 10px;
  width: 360px;
  height: 300px;
  border-radius: 10px;
  border: 1px solid var(--lightgray-border);
  box-shadow: 0px 2px 4px  rgba(0, 0, 0, 0.25);
}

.venueItem[data-state="open"]{
  background-color: var(--green-bg);
}

.venueItem[data-state="closed"]{
  background-color: var(--gray-bg);
}

.venueItem:hover{
  cursor: pointer;
  background-color:var(--lightblue-bg);
}

.venueImage{
  width: 100%;
  height: 200px;
  margin-bottom: 10px;
}

.venueInfoLine{
  display: flex;
  padding-top: 5px;
  padding-bottom: 5px;
}

.venueTitle{
  font-weight: 700;
  font-size: var(--title-size);
}

.venueState{
  margin-left: auto;
}

.venueState[data-state="open"]{
  color: var(--green-text);
}

.venueState[data-state="closed"]{
  color: var(--gray-text);
}

.infoContent{
  display: flex;
  align-items: center;
  color: var(--gray-text);
}

</style>