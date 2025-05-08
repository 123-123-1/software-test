import httpInstance from '@/utils/http';
import { ref } from 'vue';
import CryptoJS from 'crypto-js'

// 登录注册组件
export const loginData = ref({
  userName: '',
  password: '',
});
export const registerData = ref({
  userName: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  userType: 'user',
});

export const loginLoading = ref(false);
export const registerLoading = ref(false);

export const registerSuccessInfo = ref({
  visible: false,
  resUserId: '',
  resUserName: '',
});

export const loginMessage = ref({
  userNameEmpty: '',
  passwordEmpty: '',
  resultMessage: '',
});

export const registerMessage = ref({
  userNameEmpty: '',
  passwordEmpty: '',
  confirmPasswordErr: '',
  realNameEmpty: '',
  phoneEmpty: '',
});

export const checkLoginUserNameEmpty = () => {
  loginMessage.value.userNameEmpty = loginData.value.userName ? '' : '用户名不能为空';
};

export const checkLoginPasswordEmpty = () => {
  loginMessage.value.passwordEmpty = loginData.value.password ? '' : '密码不能为空';
};

export const checkRegisterUserNameEmpty = () => {
  registerMessage.value.userNameEmpty = registerData.value.userName ? '' : '用户名不能为空';
};

export const checkRegisterPassword = () => {
  registerMessage.value.passwordEmpty = registerData.value.password ? '' : '密码不能为空';
  checkConfirmPassword();
};

export const checkConfirmPassword = () => {
  if(!registerData.value.confirmPassword){
    registerMessage.value.confirmPasswordErr = '确认密码不能为空';
  }
  else if (registerData.value.confirmPassword !== registerData.value.password) {
    registerMessage.value.confirmPasswordErr = '两次密码不一致';
  }
  else{
    registerMessage.value.confirmPasswordErr = '';
  }
};

export const checkRealNameEmpty = () => {
  registerMessage.value.realNameEmpty = registerData.value.realName ? '' : '真实姓名不能为空';
};

export const checkPhoneEmpty = () => {
  registerMessage.value.phoneEmpty = registerData.value.phone ? '' : '联系电话不能为空';
};

// 登录
export const login = (processLoginSuccess) => {
  const processedLoginData = {
    userName: loginData.value.userName,
    password: CryptoJS.SHA256(loginData.value.password).toString(),
  }
  loginLoading.value = true;
  httpInstance.post('/api/users/login', processedLoginData).then((res) => {
    processLoginSuccess(res);
  }).catch((err) => {
    loginLoading.value = false;
    loginMessage.value.resultMessage = err.response?.data?.msg || err;
    ElMessage.error("登录失败: " + loginMessage.value.resultMessage);
  })
};

// 注册
export const register = () => {
  registerLoading.value = true;
  const data = { ...registerData.value };
  data.password = CryptoJS.SHA256(data.password).toString();
  httpInstance.post('/api/users/registration', data).then((res) => {
    registerLoading.value = false;
    registerData.value = {
      userName: '',
      password: '',
      confirmPassword: '',
      realName: '',
      phone: '',
      userType: 'user',
    };
    registerSuccessInfo.value.visible = true;
    registerSuccessInfo.value.resUserId = res.userId;
    registerSuccessInfo.value.resUserName = res.userName;
  }).catch((err) => {
    registerLoading.value = false;
    ElMessage.error("注册失败: " + (err.response?.data?.msg || err));
  });
};

// 信息组件
export const editInfoData = ref({
  "userName": '',
  "phone": '',
  "realName": '',
  "photo": '',
  "avatar": {
    file: null,
    url: '',
  },
});
export const infoData = ref({
  "userId": '',
  "userName": '',
  "phone": '',
  "realName": '',
  "registrationDate": '',
  "photo": ''
});
export const editPwdData = ref({
  "oldPwd": '',
  "newPwd": '',
  "newPwd2": ''
});
export const showEditInfo = ref(false);
export const showEditPwd = ref(false);
export const showLogout = ref(false);

// 获取信息
export const getInfo = async () => {
  await httpInstance.get('/api/users/info').then((res) => {
    infoData.value = res;
    editInfoData.value.userName = infoData.value.userName;
    editInfoData.value.phone = infoData.value.phone;
    editInfoData.value.realName = infoData.value.realName;
    editInfoData.value.avatar.url = infoData.value.photo;
  }).catch((err) => {
    console.log(err);
  });
};

// 修改信息
export const handleEditInfo = async (successHandler, errHandler) => {
  if(!editInfoData.value.userName){
    errHandler("用户名不能为空");
    return;
  }
  if(!editInfoData.value.realName){
    errHandler("真实姓名不能为空");
    return;
  }
  if(!editInfoData.value.phone ){
    errHandler("联系电话不能为空");
    return;
  }
  httpInstance.patch('/api/users/info', editInfoData.value).then((res) => {
    successHandler(res);
  }).catch((err) => {
    errHandler(err);
  });
};

export const uploadAvatar = async(avatar, successHandler, errHandler) => {
  httpInstance.post('api/users/avatar', avatar).then((res) => {
    successHandler(res);
  }).catch((err) => {
    errHandler(err);
  });
};

// 用户通知
export const notificationData = ref([]);
export const notificationState = ref({
  'notificationId': '',
  'operation': ''
});
export const showNotificationDetail = ref(false);
export const notificationDetail = ref({
  'notificationId': '',
  'title': '',
  'content': '',
  'timestamp': ''
});

export const handleMarkNotification = (notification) => {
  if(notification.state !== 'mark'){
    markNotification(notification);
  }
  else{
    unmarkNotification(notification);
  }
};

export const handleReadNotification = (notification) => {
  notificationState.value.notificationId = notification.notificationId;
  notificationState.value.operation = "read";
  httpInstance.patch('/api/users/newNotifications', notificationState.value).then((res) => {
    getNotificationList();
    notification.state = 'read';
    ElMessage.success("已将通知设置为已读");
  }).catch((err) => {
    ElMessage.error("修改通知状态失败：" + (err.response?.data?.msg || err));
  })
};

const markNotification = async (notification) => {
  notificationState.value.notificationId = notification.notificationId;
  notificationState.value.operation = "mark";
  httpInstance.patch('/api/users/newNotifications', notificationState.value).then((res) => {
    getNotificationList();
    notification.state = 'mark';
    ElMessage.success("通知标记成功");
  }).catch((err) => {
    ElMessage.error("修改通知状态失败：" + (err.response?.data?.msg || err));
  });
}

const unmarkNotification = async (notification) => {
  notificationState.value.notificationId = notification.notificationId;
  notificationState.value.operation = "read";
  httpInstance.patch('/api/users/newNotifications', notificationState.value).then((res) => {
    getNotificationList();
    notification.state = 'read';
    ElMessage.success("取消标记成功");
  }).catch((err) => {
    ElMessage.error("修改通知状态失败：" + (err.response?.data?.msg || err));
  });
};

export const deleteNotification = async (notification) => {
  if (!Array.isArray(notificationData.value)) {
    return;
  }
  const index = notificationData.value.findIndex(item => item.notificationId === notification.notificationId);
  if(index === -1){
    ElMessage.error("未找到要删除的通知");
    return;
  }
  notificationData.value.splice(index, 1);
  notificationState.value.notificationId = notification.notificationId;
  notificationState.value.operation = "deleted";
  httpInstance.patch('/api/users/newNotifications', notificationState.value).then((res) => {
    getNotificationList();
    ElMessage.info("删除通知成功");
    showNotificationDetail.value = false;
  }).catch((err) => {
    ElMessage.error("修改通知状态失败：" + (err.response?.data?.msg || err));
  })
};


export const getNotificationList = async () => {
  httpInstance.get('/api/users/notifications').then((res) => {
    notificationData.value = res;
  }).catch((err) => {
    ElMessage.error("获取通知信息失败: " + (err.response?.data?.msg || err));
  });
};

export const notificationStateEnum = {
  'read': '已读',
  'unread': '未读',
  'mark': '标记',
  'deleted': '已删除'
};

export const handleNotificationDetail = (notification) => {
  showNotificationDetail.value = true;
  notificationDetail.value.notificationId = notification.notificationId;
  notificationDetail.value.title = notification.title;
  notificationDetail.value.content = notification.content;
  notificationDetail.value.timestamp = notification.timestamp;
  notificationDetail.value.state = notification.state;
  handleReadNotification(notification);
};
 
export const handleEditPwd = async (successHandler, errHandler) => {
  if(!editPwdData.value.oldPwd){
    errHandler("原密码不能为空");
    return;
  }
  if(!editPwdData.value.newPwd){
    errHandler("新密码不能为空");
    return;
  }
  if(!editPwdData.value.newPwd2 ){
    errHandler("确认密码不能为空");
    return;
  }
  if(editPwdData.value.newPwd !== editPwdData.value.newPwd2){
    errHandler("两次密码输入不一致");
    return;
  }
  const pwdData = {
    oldPwd: CryptoJS.SHA256(editPwdData.value.oldPwd).toString(),
    newPwd: CryptoJS.SHA256(editPwdData.value.newPwd).toString(),
  }
  httpInstance.patch('/api/users/password', pwdData).then((res) => {
    successHandler(res);
  }).catch((err) => {
    errHandler(err);
  });
};

// 获取用户列表组件
export const userList = ref([]);

export const getUsersByName = async(userName) => {
  httpInstance.get('api/users/names', { params: { "userName": userName }}).then((res) => {
    userList.value = res;
  }).catch((err) => {
    ElMessage.error("获取用户列表失败: " + (err.response?.data?.msg || err));
  });  
};

export const getAllUsers = async() =>{
  httpInstance.get('api/users/list').then((res) => {
    userList.value = res;
  }).catch((err) => {
    ElMessage.error("获取用户列表失败: " + (err.response?.data?.msg || err));
  });
}