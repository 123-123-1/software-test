<script setup>
import { ref } from 'vue';

const reservationConfirmDialog = ref(false);
const showReservationConfirm = () => {
  reservationConfirmDialog.value = true;
};

defineExpose({ showReservationConfirm });

</script>

<template>
  <el-dialog v-model="reservationConfirmDialog" title="预约信息确认" align-center>
    <div class="reservationConfirm">
      <div class="reservationInfoBlock">
        <div class="reservationInfoTitle">个人信息</div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">真实姓名</div>
          <div class="reservationInfoContent">用户</div>
        </div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">联系电话</div>
          <div class="reservationInfoContent">11122223333</div>
        </div>
      </div>
      <div class="reservationInfoBlock">
        <div class="reservationInfoTitle">邀请成员信息</div>
        <div class="reservationInfoLine" v-for="user in [1, 1]">
          <div class="reserveUserName">user1</div>
          <div class="reserveUserRealname">用*户</div>
          <div class="reserveUserPhone">123****4321</div>
        </div>
      </div>
      <div class="reservationInfoBlock">
        <div class="reservationInfoTitle">场地信息</div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">场馆名称</div>
          <div class="reservationInfoContent">
            <div class="reserveName">同济大学篮球馆</div>
            <div class="reserveId">(ID: 12345)</div>
          </div>
        </div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">场馆地址</div>
          <div class="reservationInfoContent">上海市嘉定区曹安公路4800号同济大学</div>
        </div>
        <!-- 拼场预约没有场地信息，缺少一个v-if -->
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">场地名称</div>
          <div class="reservationInfoContent">
            <div class="reserveName">1号场</div>
            <div class="reserveId">(ID: 1)</div>
          </div>
        </div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">场地类型</div>
          <div class="reservationInfoContent">篮球场</div>
        </div>
      </div>
      <div class="reservationInfoBlock">
        <div class="reservationInfoTitle">预约信息</div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">预约时间段</div>
          <div class="reservationInfoContent">2024-12-30 09:00-10:00</div>
        </div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">预约类型</div>
          <div class="reservationInfoContent">团体</div>
        </div>
        <!-- 仅团体预约有这一项 -->
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">团体名称</div>
          <div class="reservationInfoContent">
            <div class="reserveName">篮球小队</div>
            <div class="reserveId">(ID: 1)</div>
          </div>
        </div>
        <div class="reservationInfoLine">
          <div class="reservationInfoLabel">预约价格</div>
          <div class="reservationInfoContent">10.00元</div>
        </div>
      </div>
    </div>
    <el-checkbox>我已确认预约信息准确无误</el-checkbox>
    <template #footer>
      <el-button type="primary">确定</el-button>
      <el-button>取消</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.reservationConfirm{
  --label-width: 100px;
}

.reservationInfoBlock{
  background-color: var(--lightgray-bg);
  margin-bottom: 10px;
  border-radius: 5px;
  border: 1px solid var(--lightgray-border);
  box-shadow: 0px 2px 4px  rgba(0, 0, 0, 0.25);
}

.reservationInfoTitle{
  font-size: 1.2em;
  padding: 10px;
  font-weight: 700;
  color: var(--theme-darkblue);
  background-color: var(--lightblue-bg);
  border-bottom: 1px solid var(--theme-darkblue);
}

.reservationInfoLine{
  display: flex;
  padding: 10px;
  font-size: 1em;
  border-bottom: 1px solid var(--lightgray-border);;
}

.reservationInfoLabel{
  width: var(--label-width);
  color: var(--gray-text);
}

.reservationInfoContent{
  display: flex;
  overflow: hidden;
  text-wrap: nowrap;
  text-overflow: ellipsis;
}

.reserveUserName{
  color: var(--gray-text);
}

.reserveUserRealname{
  margin-left: auto;
}

.reserveUserPhone{
  margin-left: auto;
}

.reserveId{
  margin-left: 10px;
  color: var(--gray-text);
}

</style>