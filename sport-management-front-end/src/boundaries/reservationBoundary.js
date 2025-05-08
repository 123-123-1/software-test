import { ref } from 'vue';
import httpInstance from '@/utils/http';

export const reservationTypeEnum = {
  'individual': '个人',
  'group': '团体',
  'match': '拼场',
};

export const reservationUserStateEnum = {
  'reserved': '已预约',
  'signed': '已签到',
  'matching': '拼场中',
  'cancelled': '已取消',
  'violated': '已违约',
}

export const reservationStateEnum = {
  'pending': '待审核',
  'normal': '已预约',
  'cancelled': '已取消',
  'matching': '拼场中'
};

export const reservationOperationEnum = {
  'reserve': '申请预约',
  'sign': '签到',
  'join': '加入拼场',
  'validate': '管理员通过预约申请',
  'sysvalidate': '系统自动通过预约申请',
  'usercancel': '用户取消预约',
  'managercancel': '管理员取消用户预约',
  'cancelall': '取消整个预约',
  'violate': '管理员判断为违约'
};

export const reservePermissionEnum = {
  'normal': '正常',
  'locked': '封禁',
};

export const selectedTimeslot = ref(null);        // 选择的时间段
export const selectedAvailability = ref(null);    // 选择的预约项
export const selectedGroup = ref(null);           // 选择的团体
export const reservationType = ref('match');      // 预约类型
export const reservationUserInput = ref([]);      // 邀请用户列表
export const matchCourtType = ref('');            // 拼场预约类型
export const matchReserveCount = ref(1);          // 拼场预约人数
export const reservationSearch = ref('');         // 预约搜索的输入
export const userReservationList = ref([]);       // 预约记录数组
export const reservationInfo = ref({});           // 预约详细信息
export const reservationResultDialog = ref(false);
export const reservationResultTitle = ref('');
export const reservationResultContent = ref('');
export const cancelReservationDialog = ref(false);
export const reservationLoading = ref(false);
export const reservationPageInfo = ref({
  total: 0,
  curPage: 1,
});
export const violationInfo = ref({
  state: 'normal',
  violationCount: 0,
  updateTime: new Date(),
  unlockTime: null,
}); // 用户违约信息

export const handleReservation = (reserveUserId) => {
  if(reservationType.value === 'individual'){
    individualReservation(reserveUserId);
  }
  else if(reservationType.value === 'group'){
    groupReservation(reserveUserId);
  }
  else if(reservationType.value === 'match'){
    matchReservation(reserveUserId);
  }
};

export const getReservationList = (page) => {
  httpInstance.get('/api/reservations/list', { params: { page: page} }).then((res) => {
    userReservationList.value = res.content;
    reservationPageInfo.value.total = res.totalElements;
  }).catch((err) => {
    console.log(err);
  });
};

export const getReservationDetail = (id) => {
  httpInstance.get('/api/reservations/detail', {
    params: { reservationId: id }
  }).then((res) => {
    reservationInfo.value = res;
    reservationInfo.value.records = reservationInfo.value.records.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime());
  }).catch((err) => {
    console.log(err);
  });
};

export const getUserViolation = async () => {
  await httpInstance.get('/api/reservations/violations').then((res) => {
    violationInfo.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getUserNameById = (id) => {
  return reservationInfo.value.users.find(user => user.userId === id)?.userName || '加载用户名失败';
};

export const handleInviteUserConfirm = (users) => {
  reservationUserInput.value = users;
};

export const cancelUserReservation = async (type) => {
  const cancelData = {
    reservationId: reservationInfo.value.basicInfo.reservationId,
    type: type,
  }
  await httpInstance.patch('/api/reservations/cancel', cancelData).then((res) => {
    ElMessage.success('已取消预约');
    cancelReservationDialog.value = false;
    getReservationDetail(reservationInfo.value.basicInfo.reservationId)
  }).catch((err) => {
    ElMessage.error('取消预约失败：' + err.response?.data?.msg || '未知错误');
  });
};

export const reservationPageUpdate = (val) => {
  reservationPageInfo.value.curPage = val;
  getReservationList(val - 1);
};

const individualReservation = (userId) => {
  if(!selectedAvailability.value){
    ElMessage.error('请选择预约场次');
    return;
  }
  const reservationMembers = [];
  for(const member of reservationUserInput.value){
    if(member.userId){
      reservationMembers.push(member.userId);
    }
  }
  reservationMembers.push(userId);
  const reservationData = {
    availabilityId: selectedAvailability.value.availabilityId,
    users: reservationMembers,
  };
  httpInstance.post('/api/reservations/individual', reservationData)
    .then(handleReservationSuccess)
    .catch(handleReservationErr);
};

const groupReservation = (userId) => {
  if(!selectedAvailability.value){
    ElMessage.error('请选择预约场次');
    return;
  }
  if(!selectedGroup.value){
    ElMessage.error('请选择预约团体');
    return;
  }
  const reservationMembers = [];
  for(const member of reservationUserInput.value){
    if(member.userId){
      reservationMembers.push(member.userId);
    }
  }
  reservationMembers.push(userId);
  const reservationData = {
    availabilityId: selectedAvailability.value.availabilityId,
    users: reservationMembers,
    groupId: selectedGroup.value.groupId,
  };
  httpInstance.post('/api/reservations/group', reservationData)
    .then(handleReservationSuccess)
    .catch(handleReservationErr);
};

const matchReservation = (userId) => {
  if(!selectedTimeslot.value){
    ElMessage.error('请选择预约场次');
    return;
  }
  if(!matchCourtType.value){
    ElMessage.error('请选择预约场地类型');
    return;
  }
  const reservationMembers = [];
  for(const member of reservationUserInput.value){
    if(member.userId){
      reservationMembers.push(member.userId);
    }
  }
  reservationMembers.push(userId);
  const reservationData = {
    timeslotId: selectedTimeslot.value.timeslotId,
    users: reservationMembers.filter(item => item !== null),
    reservationCount: matchReserveCount.value,
    courtType: matchCourtType.value
  };
  httpInstance.post('/api/reservations/match', reservationData)
    .then(handleReservationSuccess)
    .catch(handleReservationErr);
};

const handleReservationSuccess = (res) => {
  reservationResultTitle.value = '预约成功';
  reservationResultContent.value = '预约ID为：' + res.reservationInfo.reservationId;
  if(res.msg){
    reservationResultContent.value += '，' + res.msg;
  }
  reservationResultDialog.value = true;
  reservationUserInput.value = [];
};

const handleReservationErr = (err) => {
  console.log(err);
  reservationResultTitle.value = '预约失败';
  reservationResultContent.value = err.response?.data?.msg || '未知错误';
  reservationResultDialog.value = true;
};
