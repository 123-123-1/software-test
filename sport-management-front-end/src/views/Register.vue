<template>
  <div class="register">
    <div class="logoArea">
      <img class="logoTitle" src="../assets/logo-title.png" />
      <img class="logoPlayer" src="../assets/logo-player.png" />
    </div>
    <div class="registerMain">
      <div class="registerInputDisplay">
        <div class="registerItem">
          <div class="registerLabel">注册类型</div>
          <el-select v-model="registerData.userType">
            <el-option value="user" label="普通用户"></el-option>
            <el-option value="venueadmin" label="场地管理员"></el-option>
          </el-select>
        </div>
        <div class="registerItem">
          <div class="registerLabel">用户名</div>
          <el-input v-model="registerData.userName" placeholder="用户名" size="large" @blur="checkRegisterUserNameEmpty"></el-input>
          <div class="error-message">{{ registerMessage.userNameEmpty }}</div>
        </div>
        <div class="registerItem">
          <div class="registerLabel">密码</div>
          <el-input v-model="registerData.password" placeholder="密码" type="password" size="large" @blur="checkRegisterPassword"></el-input>
          <div class="error-message">{{ registerMessage.passwordEmpty }}</div>
        </div>
        <div class="registerItem">
          <div class="registerLabel">确认密码</div>
          <el-input v-model="registerData.confirmPassword" placeholder="确认密码" type="password" size="large" @blur="checkConfirmPassword"></el-input>
          <div class="error-message">{{ registerMessage.confirmPasswordErr }}</div>
        </div>
        <div class="registerItem">
          <div class="registerLabel">真实姓名</div>
          <el-input v-model="registerData.realName" placeholder="姓名" size="large" @blur="checkRealNameEmpty"></el-input>
          <div class="error-message">{{ registerMessage.realNameEmpty }}</div>
        </div>
        <div class="registerItem">
          <div class="registerLabel">联系电话</div>
          <el-input v-model="registerData.phone" placeholder="联系电话" size="large" @blur="checkPhoneEmpty"></el-input>
          <div class="error-message">{{ registerMessage.phoneEmpty }}</div>
        </div>
        <router-link class="loginLink" to="/user/login">返回登录</router-link>
      </div>
      <el-button class="registerButton" type="primary" size="large" :loading="registerLoading" @click="handleRegister">注册</el-button>
    </div>
  </div>
  <el-dialog v-model="registerSuccessInfo.visible" title="注册成功">
    <div>注册成功！</div>
    <div>用户ID：{{ registerSuccessInfo.resUserId }}</div>
    <div>用户名：{{ registerSuccessInfo.resUserName }}</div>
    <template #footer>
      <el-button type="primary" @click="linkLogin">前往登录</el-button>
      <el-button @click="registerSuccessInfo.visible = false">关闭</el-button>
    </template>
  </el-dialog>
  <!-- <el-dialog v-model="showLoginSuccess" title="登录成功">
    <template v-slot:footer>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
    </template>
  </el-dialog> -->
</template>

<script setup>
import { ElMessage } from 'element-plus';
import { useRouter, RouterLink } from 'vue-router';
import { 
  register, registerData, registerLoading, registerMessage, checkRegisterUserNameEmpty,
  checkRegisterPassword, checkConfirmPassword, checkRealNameEmpty, checkPhoneEmpty, registerSuccessInfo,
} from '../boundaries/accountBoundary';

const router = useRouter();

const handleRegister = async () => {
  // 检查输入是否有效
  checkRegisterUserNameEmpty();
  checkRegisterPassword();
  checkRealNameEmpty();
  checkPhoneEmpty();
  for(const key of Object.keys(registerMessage.value)){
    if(registerMessage.value[key]){
      ElMessage.error('注册信息有误，请修改后重试');
      return;
    }
  }
  register();
};

const linkLogin = () => {
  router.push("/user/login")
};

</script>

<style scoped>

.register{
  display: flex;
  background: linear-gradient(90deg, var(--theme-lightblue), rgba(255, 255, 255, 1) 100%);
  height: calc(100vh - 20px);
}

.logoArea{
  flex: 1;
  display: flex;
}

.registerMain{
  display: flex;
  flex-direction: column;
  overflow: auto;
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

.registerInputDisplay{
  padding-top: 10px;
  padding-bottom: 10px;
}

.registerItem{
  margin-top: 20px;
  margin-bottom: 20px;
}

.registerLabel{
  margin-bottom: 10px;
}

.registerButton{
  width: 100%;
  margin-top: auto;
}

.loginLink{
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