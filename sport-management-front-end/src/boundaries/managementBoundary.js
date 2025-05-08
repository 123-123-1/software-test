import httpInstance from "@/utils/http";
import { ref } from 'vue';
import { getReservationDetail, reservationInfo } from "./reservationBoundary";

export const repetitionDayEnum = {
  'Mon': '周一',
  'Tue': '周二',
  'Wed': '周三',
  'Thu': '周四',
  'Fri': '周五',
  'Sat': '周六',
  'Sun': '周日',
};

export const configEnum = ref({
  'reservation': {
    name: '预约接口',
    api: '使用预约接口：所有用户的预约信息会被转发到配置的接口',
    auto: '自动处理预约：怡运动将根据配置的场地剩余容量等信息自动判断用户预约成功或预约失败',
    manual: '手动处理预约：所有预约申请会在管理端显示，由管理员同意或拒绝预约操作',
    requestFields: [],
    responseFields: [],
  },
  'userinfo': {
    name: '用户数据配置',
    api: '使用用户数据配置：请求中的用户数据将会按照配置的信息格式填写',
    auto: '不使用用户数据配置：如果使用自定义接口且请求中涉及用户数据，将会以默认格式填写',
    requestFields: [],
  },
  'occupy': {
    name: '场地占用接口',
    api: '使用场地占用接口：用户进行拼场预约时，系统会将拼场需要占用的场地信息转发至配置的接口',
    auto: '自动处理场地占用：怡运动将自动处理拼场时占用场地的请求，所有可预约的场地都会通过占用请求，管理员可取消拼场预约',
    manual: '手动处理取消预约：怡运动将自动处理拼场时占用场地的请求，所有可预约的场地都会通过占用请求，管理员可取消拼场预约',
    requestFields: [],
    responseFields: [],
  }
});

const originalVenueInfo = ref({});
export const editingVenue = ref({});
export const availabilityConfigInfo = ref([]);
export const venueReservations = ref([]);

export const apiConfigInfo = ref({
  isCreated: false,
  operationType: 'auto',
  apiconfigId: null,
  apiUrl: '',
  type: '',
  requestContent: '',
  responseContent: '',
});

export const editResultDialog= ref({
  visible: false,
  title: '',
  message: '',
});

export const uploadImageDialog = ref({
  visible: false,
  previewUrl: '',
  targetFile: null,
});

export const deleteCourtDialog = ref({
  visible: false,
  targetIndex: -1,
});

export const deleteAvailabilityConfigDialog = ref({
  visible: false,
  courtIndex: -1,
  avconfigIndex: -1,
});

export const changeUserStateDialog = ref({
  visible: false,
  userInfo: null,
  state: null,
});

export const changeReservationStateDialog = ref({
  visible: false,
  state: null,
  changeAvailability: true,
  stateCountLoading: false,
  pendingCount: 0,
  normalCount: 0,
});

export const managerReservationPage = ref({
  total: 0,
  curPage: 1,
});

// ---------- 后端API ------------
// 1. 场馆信息编辑

export const initVenueEdit = async () => {
  await getManagerVenue();
  await getVenueCourts(originalVenueInfo.value.venueId);
  editingVenue.value = JSON.parse(JSON.stringify(originalVenueInfo.value));
};

const getManagerVenue = async () => {
  await httpInstance.get('/api/management/venueinfo').then((res) => {
    originalVenueInfo.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

const getVenueCourts = async (venueId) => {
  await httpInstance.get('/api/venues/courts', {
    params: { venueId: venueId },
  }).then((res) => {
    originalVenueInfo.value.courts = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const editVenueBasicInfo = () => {
  const venueData = {
    venueName: editingVenue.value.venueName,
    description: editingVenue.value.description,
    location: editingVenue.value.location,
    state: editingVenue.value.state,
    contactNumber: editingVenue.value.contactNumber,
    image: null,
  }
  httpInstance.patch('/api/management/venueinfo', venueData).then((res) => {
    editResultDialog.value.visible = true;
    editResultDialog.value.title = '编辑成功';
    editResultDialog.value.message = '编辑场馆信息成功';
  }).catch((err) => {
    console.log(err);
  })
};

export const uploadVenueImage = () => {
  const venueImageData = new FormData();
  venueImageData.append('image', uploadImageDialog.value.targetFile);
  httpInstance.post('/api/management/venueimage', venueImageData).then((res) => {
    URL.revokeObjectURL(uploadImageDialog.value.previewUrl);
    uploadImageDialog.value.visible = false;
    uploadImageDialog.value.previewUrl = '';
    editingVenue.value.image = res.msg;
    ElMessage.success('已成功上传图片');
  }).catch((err) => {
    console.log(err);
  })
};

// 2. 场地信息编辑

export const editCourtInfo = (index) => {
  httpInstance.patch('/api/management/courts', editingVenue.value.courts[index]).then((res) => {
    editResultDialog.value.visible = true;
    editingVenue.value.courts[index].isEdit = false;
    editResultDialog.value.title = '编辑成功';
    editResultDialog.value.message = '编辑场馆信息成功';
  }).catch((err) => {
    console.log(err);
  });
};

export const createCourt = () => {
  const newCourtInfo = {
    courtId: null,
    courtName: '新场地',
    location: '',
    type: '运动场地',
    capacity: 10,
    state: 'closed',
  };
  httpInstance.post('/api/management/courts', newCourtInfo).then((res) => {
    newCourtInfo.courtId = res.courtId;
    editingVenue.value.courts.push(newCourtInfo);
    originalVenueInfo.value.courts.push(newCourtInfo);
    editingVenue.value.courts[editingVenue.value.courts.length - 1].isEdit = true;
  }).catch((err) => {
    console.log(err);
  })
};

export const deleteCourt = () => {
  httpInstance.delete('/api/management/courts', {
    params: { courtId: editingVenue.value.courts[deleteCourtDialog.value.targetIndex].courtId }
  }).then((res) => {
    deleteCourtDialog.value.visible = false;
    editResultDialog.value.title = '删除成功';
    editResultDialog.value.message = '已成功删除场地';
    editResultDialog.value.visible = true;
    editingVenue.value.courts.splice(deleteCourtDialog.value.targetIndex, 1);
  }).catch((err) => {
    deleteCourtDialog.value.visible = false;
    editResultDialog.value.title = '删除失败';
    editResultDialog.value.message = err.response?.data?.msg || '未知错误';
    editResultDialog.value.visible = true;
  });
};

export const resetBasicInfo = () => {
  editingVenue.value.venueName = originalVenueInfo.value.venueName;
  editingVenue.value.description = originalVenueInfo.value.description;
  editingVenue.value.location = originalVenueInfo.value.location;
  editingVenue.value.state = originalVenueInfo.value.state;
  editingVenue.value.contactNumber = originalVenueInfo.value.contactNumber;
};

// 3. 时间段编辑

export const getAvailabilityConfig = () => {
  httpInstance.get('/api/management/availability-config').then((res) => {
    availabilityConfigInfo.value = res;
  }).catch((err) => {
    console.log(err);
  })
};

export const createAvailabilityConfig = (configData, successHandler, errHandler) => {
  httpInstance.post('/api/management/availability-config', configData)
    .then((res) => {
      successHandler(res);
      ElMessage.success('已创建配置项');
    })
    .catch(errHandler);
};

export const patchAvailabilityConfig = (configData, successHandler, errHandler) => {
  httpInstance.patch('/api/management/availability-config', configData)
  .then((res) => {
    successHandler(res);
    ElMessage.success('已编辑配置项');
  })
  .catch(errHandler);
}

export const deleteAvailabilityConfig = () => {
  httpInstance.delete('/api/management/availability-config', {
    params: {
      configId: availabilityConfigInfo.value[deleteAvailabilityConfigDialog.value.courtIndex].config[deleteAvailabilityConfigDialog.value.avconfigIndex].avconfigId
    }
  }).then((res) => {
    ElMessage.info("已删除配置项");
    availabilityConfigInfo.value[deleteAvailabilityConfigDialog.value.courtIndex].config.splice(deleteAvailabilityConfigDialog.value.avconfigIndex, 1);
    deleteAvailabilityConfigDialog.value.visible = false;
  }).catch((err) => {
    console.log(err);
  })
};

// 4. 场地接口配置

export const getConfigFields = (configName) => {
  httpInstance.get('/api/management/config-fields', {
    params: { type: configName }
  }).then((res) => {
    configEnum.value[configName].requestFields = res.requestFields;
    configEnum.value[configName].responseFields = res.responseFields;
  }).catch((err) => {
    console.log(err);
  });
};

export const getApiConfig = async (type) => {
  await httpInstance.get('/api/management/api-config', {
    params: { type: type }
  }).then((res) => {
    if(res.exist === 1){
      setApiConfigInfo(res.configInfo);
    }
    else{
      emptyApiConfigInfo();
    }
  }).catch((err) => {
    console.log(err);
  })
};

export const createApiConfig = async (type) => {
  const createInfo = {
    type: type,
  };
  await httpInstance.post('/api/management/api-config', createInfo).then((res) => {
    setApiConfigInfo(res);
    ElMessage.success("已创建配置项");
  }).catch((err) => {
    console.log(err);
  })
};

export const deleteApiConfig = async (apiConfigId) => {
  await httpInstance.delete('/api/management/api-config', {
    params: { apiConfigId: apiConfigId }
  }).then((res) => {
    emptyApiConfigInfo();
    ElMessage.info("已删除配置项");
  }).catch((err) => {
    console.log(err);
  })
};

export const editApiConfig = (configData) => {
  httpInstance.patch('/api/management/api-config', configData).then((res) => {
    setApiConfigInfo(res);
    ElMessage.success("已保存编辑内容");
  }).catch((err) => {
    console.log(err);
  });
};

// 5. 场地预约管理

export const getVenueReservations = (params) => {
  httpInstance.get('/api/management/reservations/list', { params: params }).then((res) => {
    venueReservations.value = res.content;
    managerReservationPage.value.total = res.totalElements;
  }).catch((err) => {
    console.log(err);
  });
};

export const changeUserReservationState = () => {
  const stateData = {
    reservationId: reservationInfo.value.basicInfo.reservationId,
    userId: changeUserStateDialog.value.userInfo.userId,
    userState: changeUserStateDialog.value.state,
  };
  httpInstance.patch('/api/management/reservations/user-state', stateData).then((res) => {
    ElMessage.success('已更改用户预约状态');
    changeUserStateDialog.value.visible = false;
    getReservationDetail(reservationInfo.value.basicInfo.reservationId);
  }).catch((err) => {
    console.log(err);
  })
};

export const getReservationStateCount = () => {
  httpInstance.get('/api/management/reservations/state-count', {
    params: { reservationId: reservationInfo.value.basicInfo.reservationId }
  }).then((res) => {
    changeReservationStateDialog.value.normalCount = res.normalCount;
    changeReservationStateDialog.value.pendingCount = res.pendingCount;
    changeReservationStateDialog.value.stateCountLoading = false;
  }).catch((err) => {
    changeReservationStateDialog.value.stateCountLoading = false;
    console.log(err);
  })
};

export const changeReservationState = () => {
  const stateData = {
    reservationId: reservationInfo.value.basicInfo.reservationId,
    state: changeReservationStateDialog.value.state,
    changeAvailability: changeReservationStateDialog.value.changeAvailability
  }
  httpInstance.patch('/api/management/reservations/state', stateData).then(() => {
    ElMessage.success('已更改预约状态');
    changeReservationStateDialog.value.visible = false;
    getReservationDetail(reservationInfo.value.basicInfo.reservationId);
  }).catch((err) => {
    console.log(err);
  });
};

// ---------- 前端数据处理函数 ----------

export const resetCourtInfo = (index) => {
  editingVenue.value.courts[index] = { ...originalVenueInfo.value.courts[index] };
  editingVenue.value.courts[index].isEdit = false;
};

export const showCourtDelete = (index) => {
  deleteCourtDialog.value.targetIndex = index;
  deleteCourtDialog.value.visible = true;
};

export const showAvailabilityConfigDelete = (courtIndex, avconfigIndex) => {
  deleteAvailabilityConfigDialog.value.courtIndex = courtIndex;
  deleteAvailabilityConfigDialog.value.avconfigIndex = avconfigIndex;
  deleteAvailabilityConfigDialog.value.visible = true;
};

export const loadPreview = (tplstr, configName, dataKey) => {
  let res = tplstr;  
  for(const item of configEnum.value[configName][dataKey]){
    if(typeof item.sample === 'number'){
      res = res.replace(`\${${item.name}}`, item.sample.toString());
    }
    else if(typeof item.sample === 'string'){
      res = res.replace(`\${${item.name}}`, `"${item.sample}"`);
    }
    else if(item.sample instanceof Array){
      res = res.replace(`\${${item.name}}`, '[]');
    }
  }
  return res;
};

export const showChangeUserState = (user, state) => {
  changeUserStateDialog.value.userInfo = user;
  changeUserStateDialog.value.state = state;
  changeUserStateDialog.value.visible = true;
};

export const showChangeReservationState = (state) => {
  changeReservationStateDialog.value.state = state;
  changeReservationStateDialog.value.changeAvailability = true;
  changeReservationStateDialog.value.visible = true;
  changeReservationStateDialog.value.stateCountLoading = false;
  if(state === 'normal'){
    changeReservationStateDialog.value.stateCountLoading = true;
    getReservationStateCount();
  }
};

export const managerReservationPageUpdate = (val) => {
  managerReservationPage.value.curPage = val;
  getVenueReservations({ page: val - 1});
};

const setApiConfigInfo = (res) => {
  apiConfigInfo.value = res;
  apiConfigInfo.value.isCreated = true;
};

const emptyApiConfigInfo = () => {
  apiConfigInfo.value = {
    isCreated: false,
    operationType: 'auto',
    apiconfigId: null,
    apiUrl: '',
    type: '',
    requestContent: '',
    responseContent: '',
  }
};