<script setup>
import { ref } from 'vue';
import { editPwdData, handleEditPwd } from '../boundaries/accountBoundary'

const editPwdVisible = ref(false);

const showEditPwd = () => {
  editPwdVisible.value = true;
  editPwdData.value.oldPwd = '';
  editPwdData.value.newPwd = '';
  editPwdData.value.newPwd2 = '';
};

// 修改密码成功
const processUpdatePwdSuccess = (res) => {
  editPwdVisible.value = false;
  ElMessage.success("修改密码成功");
};

// 修改密码失败
const processUpdatePwdError = (err) => {
  ElMessage.error("修改密码失败: " + (err.response?.data?.msg || err));
};

defineExpose({ showEditPwd });

</script>

<template>
  <el-dialog v-model="editPwdVisible" title="修改密码">
    <div class="editDialogMain">
      <div class="editItem">
        <div class="editLabel">原密码</div>
        <el-input v-model="editPwdData.oldPwd" type="password"></el-input>
      </div>
      <div class="editItem">
        <div class="editLabel">新密码</div>
        <el-input v-model="editPwdData.newPwd" type="password"></el-input>
      </div>
      <div class="editItem">
        <div class="editLabel">确认密码</div>
        <el-input v-model="editPwdData.newPwd2" type="password"></el-input>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleEditPwd(processUpdatePwdSuccess, processUpdatePwdError)">确定</el-button>
      <el-button @click="editPwdVisible = false">取消</el-button>
    </template>
  </el-dialog>
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
</style>