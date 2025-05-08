import { ref } from 'vue';
import { getTimeDateDisplay } from "./utils";
import { dayjs, ElMessage } from "element-plus";
import httpInstance from "@/utils/http";

const ONEMINUTE = 1000 * 60;
const ONEHOUR = 60 * ONEMINUTE;
const ONEDAY = 24 * ONEHOUR;
const TIMEOFFSET = (new Date()).getTimezoneOffset() * ONEMINUTE;

export const venueStateEnum = {
  'open': '开放预约',
  'closed': '未开放预约',
};
export const courtStateEnum = {
  'open': '开放',
  'closed': '关闭',
};

export const availabilityEnum = {
  'reserveable': '可预约',
  'matching': '拼场中',
  'full': '预约已满',
  'closed': '未开放',
};

export const venueSearch = ref(''); // 场馆搜索输入框的内容
export const venuePage = ref(1); // 场馆当前页码
export const venueDate = ref(dayjs(Date.now()).format()); // 查找场馆时间段的输入
export const venueCommentPage = ref(1); // 场馆评论当前页码
export const venueCommentInput = ref({
  content: '',
  score: 5.0,
}); // 场馆评价的输入

export const venueListLoading = ref(false); // 场馆列表页面是否正在加载
export const venueDetailRequestCount = ref(0); // 场馆详情的未加载请求数量
export const venueList = ref([]); // 场馆列表
export const venueInfo = ref({}); // 场馆详情
export const venueTimeslots = ref([]); // 场馆可预约时间段
export const venueComments = ref([]); // 场馆评价
export const venueCount = ref(0); // 场馆的数量
export const commentCount = ref(0); // 评论的数量
export const commentSuccessDialog = ref(false); // 评论发送成功弹窗
export const venueCourtType = ref([]);

export const getVenueList = async (page) => {
  httpInstance.get('/api/venues/list', { params: { page: page, name: venueSearch.value } } ).then((res) => {
    venueList.value = res.content;
    venueCount.value = res.totalElements;
  }).catch((err) => {
    console.log(err);
  });
};

export const getVenueDetail = async (venueId) => {
  ++venueDetailRequestCount.value;
  await httpInstance.get('/api/venues/detail', { params: { venueId: venueId } } ).then((res) => {
    --venueDetailRequestCount.value;
    venueInfo.value = res;
  }).catch((err) => {
    ElMessage.info("查找场馆信息失败：" + err.msg || err);
    venueDetailRequestCount.value = -1;
  });
};

export const getVenueCourts = async (venueId) => {
  ++venueDetailRequestCount.value;
  await httpInstance.get('/api/venues/courts', { params: { venueId: venueId } } ).then((res) => {
    --venueDetailRequestCount.value;
    venueInfo.value.courts = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getVenueCourtType = async (venueId) => {
  await httpInstance.get('/api/venues/court-type', { params: { venueId: venueId } } ).then((res) => {
    venueCourtType.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getVenueTimeslots = async (venueId) => {
  ++venueDetailRequestCount.value;
  const dateStr = dayjs(venueDate.value).format('YYYY-MM-DD');
  await httpInstance.get('/api/venues/timeslots', {
    params: { venueId: venueId, date: dateStr }
  }).then((res) => {
    --venueDetailRequestCount.value;
    venueTimeslots.value = res;
  }).catch((err) => {
    console.log(err);
  });
};

export const getVenueComments = async (venueId, page) => {
  ++venueDetailRequestCount.value;
  await httpInstance.get('/api/venues/comments', {
    params: { venueId: venueId, page: page }
  }).then((res) => {
    --venueDetailRequestCount.value;
    venueComments.value = res.content;
    commentCount.value = res.totalElements;
  }).catch((err) => {
    console.log(err);
  });
};

export const postVenueComments = () => {
  if(!venueCommentInput.value.content){
    ElMessage.info('请输入评论内容');
    return;
  }
  const commentData = {
    ...venueCommentInput.value,
    venueId: venueInfo.value.venueId,
    time: new Date(Date.now() - TIMEOFFSET),
  };
  httpInstance.post('/api/venues/comments', commentData).then((res) => {
    commentSuccessDialog.value = true;
    venueCommentInput.value.content = '';
    venueCommentInput.value.score = 5.0;
    commentPageUpdate(1);
  }).catch((err) => {
    console.log(err);
  });
};

export const venuePageUpdate = (val) => {
  venuePage.value = val;
  getVenueList(venuePage.value - 1);
};

export const commentPageUpdate = (val) => {
  venueCommentPage.value = val;
  getVenueComments(venueInfo.value.venueId, venueCommentPage.value - 1);
};

export const findCourtInfo = (id) => {
  if(!venueInfo.value.courts){
    return null;
  }
  return venueInfo.value.courts.find((court) => court.courtId === id);
};

export const handleVenueDateChange = () => {
  getVenueTimeslots(venueInfo.value.venueId);
};

export const incrementVenueDate = (increment) => {
  venueDate.value = dayjs(venueDate.value).add(increment, 'day').format();
  handleVenueDateChange();
};

export const processCommentTime = (time) => {
  const timeDate = new Date(time);
  // timeDate.setTime(timeDate.getTime() + TIMEOFFSET);
  const timeDif = Date.now() - timeDate.getTime();
  if(timeDif < ONEHOUR){
    return Math.floor(timeDif / ONEMINUTE) + '分钟前评论';
  }
  else if(timeDif < ONEDAY){
    return Math.floor(timeDif / ONEHOUR) + '小时前评论';
  }
  else{
    return '评论于 ' + getTimeDateDisplay(time);
  }
};

export const resetVenueSearch = () => {
  venueSearch.value = '';
  getVenueList(0);
};

export const checkAvailabilitySelection = (startTime) => {
  const availabilityStart = new Date(startTime);
  const availabilityStartNum = availabilityStart.getTime() + availabilityStart.getTimezoneOffset() * 60 * 1000;
  if(availabilityStartNum < Date.now()){
    ElMessage.warning('不能预约以前的场地');
    return false;
  }
  return true;
};