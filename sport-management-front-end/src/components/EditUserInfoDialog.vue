<script setup>
import { ref } from 'vue';
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { editInfoData, infoData, handleEditInfo, uploadAvatar } from '@/boundaries/accountBoundary';

const editUserInfoVisible = ref(false);
const avatarInputRef = ref(null);
const userStore = useUserStore();
const { userId, userName, userAvatar } = storeToRefs(userStore);

const showEditUserInfo = () => {
  editInfoData.value.userName = infoData.value.userName;
  editInfoData.value.phone = infoData.value.phone;
  editInfoData.value.realName = infoData.value.realName;
  editInfoData.value.avatar.url = userAvatar.value;
  editUserInfoVisible.value = true;
};

const handleEdit = () => {
  const handleUploadAvatar = (res) => {
    userName.value = editInfoData.value.userName;
    infoData.value = res;
    if(!editInfoData.value.avatar.file){
      processUpdateInfoSuccess({ msg: userAvatar.value });
      return;
    }
    const avatarData = new FormData();
    avatarData.append('avatar', editInfoData.value.avatar.file);
    uploadAvatar(avatarData, processUpdateInfoSuccess, processUpdateInfoError);
  };
  handleEditInfo(handleUploadAvatar, processUpdateInfoError);
};

const handleAvatarChange = () => {
  if(avatarInputRef.value.files.length === 0){
    return;
  }
  if(editInfoData.value.avatar.file){
    URL.revokeObjectURL(editInfoData.value.avatar.url);
  }
  editInfoData.value.avatar.file = avatarInputRef.value.files[0];
  editInfoData.value.avatar.url = URL.createObjectURL(editInfoData.value.avatar.file);
};

// 修改信息成功
const processUpdateInfoSuccess = (res) => {
  editUserInfoVisible.value = false;
  userAvatar.value = res.msg;
  ElMessage.success("修改信息成功")
};

// 修改信息失败
const processUpdateInfoError = (err) => {
  ElMessage.error("修改信息失败: " + (err.response?.data?.msg || err));
};

defineExpose({ showEditUserInfo });

</script>

<template>
  <el-dialog v-model="editUserInfoVisible" title="修改个人信息">
    <div class="editDialogMain">
      <div class="editItem">
        <div class="editLabel">用户ID</div>
        <div class="editContent">{{ userId }}</div>
      </div>
      <div class="editItem">
        <div class="editLabel">头像</div>
        <div class="editContent avatarUpload">
          <img class="avatarPreview" :src="editInfoData.avatar.url" />
          <el-button @click="avatarInputRef.click()">上传图片</el-button>
        </div>
      </div>
      <div class="editItem">
        <div class="editLabel">用户名</div>
        <el-input v-model="editInfoData.userName"></el-input>
      </div>
      <div class="editItem">
        <div class="editLabel">真实姓名</div>
        <el-input v-model="editInfoData.realName"></el-input>
      </div>
      <div class="editItem">
        <div class="editLabel">联系电话</div>
        <el-input v-model="editInfoData.phone"></el-input>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleEdit">确定</el-button>
      <el-button @click="editUserInfoVisible = false">取消</el-button>
    </template>
  </el-dialog>
  <input type="file" v-show="false" ref="avatarInputRef" @change="handleAvatarChange" />
</template>

<style scoped>
.editDialogMain{
  padding: 10px;
}

.editItem{
  display: flex;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
}

.editLabel{
  width: 100px;
}

.avatarUpload{
  display: flex;
  align-items: center;
}

.avatarPreview{
  width: 150px;
  height: 150px;
  margin-right: 20px;
  border-radius: 5px;
}

</style>

