import axios from 'axios';
import useUserStore from '@/stores/userStore';

const httpInstance = axios.create({
  // baseURL: 'http://47.116.222.84:8080',
  baseURL: '/backend',
  timeout: 5000, // 超时时间
});

httpInstance.interceptors.request.use(config => {
  const userStore = useUserStore();
  if (userStore.userToken) {
    config.headers.Authorization = `Bearer ${userStore.userToken}`;
  }
  return config;
}, e => Promise.reject(e));

httpInstance.interceptors.response.use(res => res.data, e => {
  return Promise.reject(e);
});

export default httpInstance;