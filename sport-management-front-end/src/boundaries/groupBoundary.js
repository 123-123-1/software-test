import { ref } from 'vue';
import httpInstance from '@/utils/http';

export const applicationStateEnum = {
  'waiting': '待审核',
  'accepted': '已同意',
  'rejected': '已拒绝',
  'expired': '已过期',
};

export const memberRoleEnum = {
  'leader': '管理员',
  'member': '成员',
};

export const groupCreateInput = ref({
  groupName: '',
  description: '',
  photo: '',
  members: [],
});

export const groupInfo = ref({});

export const groupTypeSelect = ref('all');
export const groupApplicationList = ref([]);
const allGroupList = ref([]);
export const userGroupList = ref([]);
export const allGroupFiltered = ref([]);
export const userLeaderGroupList = ref([]);
export const groupSearch = ref('');
export const groupCreateResultDialog = ref(false);
export const groupCreateResultTitle = ref('');
export const groupCreateResultContent = ref('');

export const isUserLeader = (userId) => {
  if(!groupInfo.value.members){
    return false;
  }
  return groupInfo.value.members.find(member => member.userId === userId).role === 'leader';
};

export const getAllGroups = () => {
  httpInstance.get('/api/groups').then((res) => {
    allGroupList.value = res;
    allGroupFiltered.value = allGroupList.value;
  }).catch((err) => {
    console.log(err);
  });
};

export const getUserGroups = () => {
  httpInstance.get('/api/groups/byUser').then((res) => {
    userGroupList.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getUserLeaderGroups = async () => {
  await httpInstance.get('/api/groups/leadergroups').then((res) => {
    userLeaderGroupList.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getGroupDetail = async (groupId) => {
  await httpInstance.get(`/api/groups/byId/${groupId}`).then((res) => {
    groupInfo.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getGroupApplications = () => {
  httpInstance.get('/api/groups/application').then((res) => {
    groupApplicationList.value = res;
  }).catch((err) => {
    console.log(err);
  })
};

export const sendJoinGroupApplication = (groupId, applyInfo, successHandler) => {
  const applyData = {
    groupId: groupId,
    applyInfo: applyInfo
  };
  httpInstance.post('/api/groups/application', applyData).then((res) => {
    successHandler(res);
  }).catch((err) => {
    console.log(err);
  });
};

export const auditGroupApplication = (targetId, auditResult) => {
  const auditData = {
    auditObjectId: targetId,
    result: auditResult,
  };
  httpInstance.post('/api/groups/application', auditData).then((res) => {
    ElMessage.success('已审核申请');
    getGroupApplications();
  }).catch((err) => {
    console.log(err);
  });
};

export const createGroup = () => {
  if(!groupCreateInput.value.groupName){
    showGroupCreateErr('团体名称不能为空');
    return;
  }
  const groupData = { ...groupCreateInput.value };
  groupData.members = groupCreateInput.value.members.map(item => item.userId);
  httpInstance.post('/api/groups', groupData).then((res) => {
    groupCreateResultTitle.value = '创建团体成功';
    groupCreateResultContent.value = '已成功创建团体';
    groupCreateResultDialog.value = true;
    groupCreateInput.value = {
      groupName: '',
      description: '',
      photo: '',
      members: [],
    };
  }).catch((err) => {
    showGroupCreateErr(err.response?.data?.msg || '未知错误');
  });
};

export const removeGroupMember = (memberId) => {
  httpInstance.delete('/api/groups/members/by', {
    params: {
      groupId: groupInfo.value.groupId,
      memberId: memberId,
    }
  }).then((res) => {
    ElMessage.success('已成功移出成员');
    getGroupDetail(groupInfo.value.groupId);
  }).catch((err) => {
    console.log(err);
  });
};

export const changeMemberRole = (memberId, role) => {
  const roleData = {
    targetId: memberId,
    groupId: groupInfo.value.groupId,
    role: role,
  };
  httpInstance.patch('/api/groups/members', roleData).then((res) => {
    ElMessage.success('已授予管理员权限');
    getGroupDetail(groupInfo.value.groupId);
  }).catch((err) => {
    console.log(err);
  });
};

export const inviteJoinGroup = (groupId, targetId, successHandler, errHandler) => {
  const inviteData = {
    groupId: groupId,
    inviteeId: targetId,
  };
  httpInstance.post('/api/groups/application/by').then((res) => {
    ElMessage.success('已向该用户发送申请');
  }).catch((err) => {
    console.log(err);
  });
};

export const removeCreateMember = (index) => {
  groupCreateInput.value.members.splice(index, 1);
};

export const createGroupInvite = (users) => {
  groupCreateInput.value.members = users;
};

export const searchGroup = () => {
  allGroupFiltered.value = allGroupList.value.filter(group => group.groupName.includes(groupSearch.value));
};

export const resetSearchGroup = () => {
  allGroupFiltered.value = allGroupList.value;
};

const showGroupCreateErr = (msg) => {
  groupCreateResultTitle.value = '创建团体失败';
  groupCreateResultContent.value = msg;
  groupCreateResultDialog.value = true;
};