<script setup>
import { useMenuStore } from '@/stores/menuStore';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const menuStore = useMenuStore();
const { userName, userAvatar, userType } = storeToRefs(userStore);
const { selectedPage } = storeToRefs(menuStore);
const router = useRouter();
const linkUserProfile = () => {
  if(userType.value === 'venueadmin'){
    router.push('/management/profile');
  }
  else{
    router.push('/user/profile');
  }
};

</script>

<template>
  <div class="Topbar">
    <div class="pageTitle">{{ selectedPage.title }}</div>
    <div class="userInfo" @click="linkUserProfile">
      <img class="avatarDisplay" :src="userAvatar">
      <div class="usernameDisplay">{{ userName }}</div>
    </div>
  </div>
</template>

<style scoped>
.Topbar {
  display: flex;
  height: var(--top-size);
  align-items: center;
  border-bottom: 1px solid lightgray;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
}

.userInfo {
  display: flex;
  margin-left: auto;
  margin-right: 20px;
  padding-left: 5px;
  padding-right: 5px;
  align-items: center;
  border-radius: 5px;
}

.userInfo:hover{
  cursor: pointer;
  background-color: var(--gray-bg);
}

.avatarDisplay{
  height: calc(var(--top-size) - 20px);
  width: calc(var(--top-size) - 20px);
  margin-right: 10px;
  border-radius: 5px;
}

.pageTitle {
  margin-left: 20px;
  font-size: 18px;
  font-weight: 700;
}


</style>