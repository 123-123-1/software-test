import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import httpInstance from '@/utils/http';

export const applicationStateEnum = {
  'accepted': '已同意',
  'rejected': '已拒绝',
  'expired': '已过期',
};

export const chatCreateInput = ref({
  name: '',
  photo: '',
  members: []
});

export const createChatResultDialog = ref(false);
export const createChatResultTitle = ref('');
export const createChatResultContent = ref('');
export const messageInput = ref('');
export const userChatList = ref([]);
export const userFriendList = ref([]);
export const friendApplicationList = ref([]);
export const chatMessages = ref([]);
export const selectedChat = ref(null);

export const removeCreateMember = (index) => {
  chatCreateInput.value.members.splice(index, 1);
};

export const selectChat = (chat) => {
  selectedChat.value = chat;
  if(chat){
    getChatMessage();
  }
};

export const createChat = (userId) => {
  if(!validateChatCreate()){
    return;
  }
  const chatMembers = chatCreateInput.value.members.map(item => item.userId);
  chatMembers.push(userId);
  const chatData = {
    chatName: chatCreateInput.value.name,
    photo: chatCreateInput.value.photo,
    members: chatMembers,
    type: 'friendGroup',
  };
  httpInstance.post('/api/socialize/chats', chatData).then((res) => {
    createChatResultTitle.value = '创建群聊成功';
    createChatResultContent.value = '已成功创建群聊，ID：' + res.chatId;
    createChatResultDialog.value = true;
    chatCreateInput.value = {
      name: '',
      photo: '',
      members: []
    };
  }).catch((err) => {
    showChatCreateErr(err.response?.data?.msg);
  });
};

export const getUserChats = async () => {
  await httpInstance.get('/api/socialize/chats').then((res) => {
    userChatList.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const postChatMessage = () => {
  const messageData = {
    chatId: selectedChat.value.chatId,
    content: messageInput.value,
  };
  httpInstance.post('/api/socialize/messages', messageData).then((res) => {
    ElMessage.success("已发送消息");
    getChatMessage();
    messageInput.value = null;
  }).catch((err) => {
    console.log(err);
  });
};

export const getUserFriends = async () => {
  await httpInstance.get('/api/socialize/friends').then((res) => {
    userFriendList.value = res;
    userChatList.value = res.map(item => {
      return {
        chatId: item.chatId,
        chatName: item.userName,
        type: 'friendChat',
        photo: item.photo,
      }
    });
  }).catch((err) => {
    console.log(err);
  });
};

export const sendFriendApplication = async (reviewerId, applyInfo, successHandler) => {
  const applyData = {
    reviewerId: reviewerId,
    applyInfo: applyInfo,
  };
  httpInstance.post('/api/socialize/application', applyData)
    .then(successHandler)
    .catch((err) => {
      ElMessage.error(err.response?.data?.msg || '未知错误');
    });
};

export const getFriendApplications = () => {
  httpInstance.get('/api/socialize/application').then((res) => {
    friendApplicationList.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const auditFriendApplication = (targetId, auditResult) => {
  const auditData = {
    auditObjectId: targetId,
    result: auditResult,
  }
  httpInstance.patch('/api/socialize/application', auditData).then((res) => {
    ElMessage.success('成功进行审核操作');
    getFriendApplications();
  }).catch((err) => {
    console.log(err);
  });
};

export const createGroupInvite = (users) => {
  chatCreateInput.value.members = users;
};

const getChatMessage = () => {
  httpInstance.get('/api/socialize/messages', {
    params: { chatId: selectedChat.value.chatId }
  }).then((res) => {
    chatMessages.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

const validateChatCreate = () => {
  if(!chatCreateInput.value.name){
    showChatCreateErr('群聊名称不能为空');
    return false;
  }
  return true;
};

const showChatCreateErr = (msg) => {
  createChatResultTitle.value = '创建群聊失败';
  createChatResultContent.value = msg || '未知错误';
  createChatResultDialog.value = true;
};
