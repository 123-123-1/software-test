<template>
  <div class="login">
    <div class="logoArea">
      <img class="logoTitle" src="../assets/logo-title.png" />
      <img class="logoPlayer" src="../assets/logo-player.png" />
    </div>
    <div class="loginMain">
      <div class="loginInputDisplay">
        <div class="loginItem">
          <div class="loginLabel">用户名</div>
          <el-input
            v-model="loginData.userName"
            placeholder="用户名"
            size="large"
            @blur="checkLoginUserNameEmpty"
          ></el-input>
          <div class="error-message">{{ loginMessage.userNameEmpty }}</div>
        </div>
        <div class="loginItem">
          <div class="loginLabel">密码</div>
          <el-input
            v-model="loginData.password"
            placeholder="密码"
            size="large"
            type="password"
            @blur="checkLoginPasswordEmpty"
          ></el-input>
          <div class="error-message">{{ loginMessage.passwordEmpty }}</div>
        </div>
        <router-link class="registerLink" to="/user/register">注册账号</router-link>
      </div>
      <div class="error-message">{{ loginMessage.resultMessage }}</div>
      <el-button
        type="primary"
        size="large"
        class="loginButton"
        :loading="loginLoading"
        @click="handleLogin"
      >
        登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter, RouterLink} from 'vue-router';
import useUserStore from "@/stores/userStore";
import { storeToRefs } from "pinia";
import {
  login, loginData, loginLoading, checkLoginUserNameEmpty, checkLoginPasswordEmpty, loginMessage
} from '../boundaries/accountBoundary';
import { ElMessage } from 'element-plus';
import { useMenuStore } from '@/stores/menuStore';
import { managerMenuData, userMenuData } from '@/boundaries/mainBoundary';

const userStore = useUserStore();
const menuStore = useMenuStore();
const { userToken, userType, userName, expirationTime, userId, userAvatar } = storeToRefs(userStore);
const { menuData } = storeToRefs(menuStore);
const router = useRouter();


const handleLogin = () => {
  checkLoginUserNameEmpty();
  checkLoginPasswordEmpty();
  if(loginMessage.value.userNameEmpty || loginMessage.value.passwordEmpty){
    ElMessage.error('登录信息有误，请修改后重试');
    return;
  }
  login(processLoginSuccess);
};

// 登录成功
const processLoginSuccess = (res) => {
  loginLoading.value = false;
  userId.value = res.userId;
  userName.value = res.userName;
  userType.value = res.userType;
  userToken.value = res.token;
  userAvatar.value = res.userAvatar;
  expirationTime.value = new Date(res.expiration_time).getTime();
  loginData.value.userName = '';
  loginData.value.password = '';
  loginMessage.value.resultMessage = '';
  ElMessage.success("登录成功");
  if(userType.value === 'user'){
    menuData.value = userMenuData;
    router.push("/venues/list");
  }
  else if(userType.value === 'venueadmin'){
    menuData.value = managerMenuData;
    router.push("/management/info/venue");
  }
};
</script>

<style scoped>
.login{
  display: flex;
  background: linear-gradient(90deg, var(--theme-lightblue), rgba(255, 255, 255, 1) 100%);
  height: calc(100vh - 20px);
}

.logoArea{
  flex: 1;
  display: flex;
}

.loginMain{
  display: flex;
  flex-direction: column;
  background-color: rgba(238, 238, 238, 0.5);
  width: max(30%, 400px);
  padding: 20px;
}

.logoTitle{
  margin: auto;
  width: 50%;
}

.logoPlayer{
  width: 25%;
  position: absolute;
  left: 0;
  bottom: 0;
}

.loginInputDisplay{
  margin-top: auto;
  padding-top: 50px;
  padding-bottom: 50px;
}

.loginItem{
  margin-top: 20px;
  margin-bottom: 20px;
}

.loginLabel{
  margin-bottom: 10px;
}

.loginButton{
  width: 100%;
  margin-top: 20px;
  margin-bottom: auto;
}

.registerLink{
  display: flex;
  justify-content: flex-end;
  margin-left: auto;
  color: var(--lightblue-text);
  text-decoration: underline;
}

.error-message {
  color: red;
  font-size: 12px;
  height: 1em;
  margin-top: 5px;
}

</style>