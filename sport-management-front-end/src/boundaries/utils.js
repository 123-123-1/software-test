// 项目中用到的各种工具函数

// 时间处理函数
// 日期时间显示：YYYY-MM-DD hh:mm
export const getLocalTimeDateDisplay = (time) => {
  return getTimeDateDisplay(getUTCTime(time));
};


export const getTimeDateDisplay = (time) => {
  const timeDate = new Date(time);
  return timeDate.getUTCFullYear() + '-' + (timeDate.getUTCMonth() + 1).toString().padStart(2, '0') + '-' 
  + timeDate.getUTCDate().toString().padStart(2, '0') + ' ' + timeDate.getUTCHours().toString().padStart(2, '0') + ':' 
  + timeDate.getUTCMinutes().toString().padStart(2, '0');
};

// 时间段显示：hh:mm-hh:mm，displayDate设置为true表示展示日期
export const getTimeslotDisplay = (startTime, endTime, displayDate) => {
  const dateStart = new Date(startTime);
  const dateEnd = new Date(endTime);
  let res = '';
  if(displayDate){
    res = dateStart.getUTCFullYear() + '-' + (dateStart.getUTCMonth() + 1).toString().padStart(2, '0') + '-' 
    + dateStart.getUTCDate().toString().padStart(2, '0') + ' ';
  }
  return res + dateStart.getUTCHours().toString().padStart(2, '0') + ':' + dateStart.getUTCMinutes().toString().padStart(2, '0') + '-'
  + dateEnd.getUTCHours().toString().padStart(2, '0') + ':' + dateEnd.getUTCMinutes().toString().padStart(2, '0');
};

export const getUTCTime = (time) => {
  const date = new Date(time);
  date.setTime(date.getTime() - date.getTimezoneOffset() * 60 * 1000);
  return date;
};

export const parseTimeStr = (timestr) => {
  const [ hour, minute, second ] = timestr.split(':');
  const date = new Date();
  date.setHours(hour);
  date.setMinutes(minute);
  return date;
};