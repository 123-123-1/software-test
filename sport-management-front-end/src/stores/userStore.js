import { defineStore } from 'pinia';
import { ref } from 'vue';

const useUserStore = defineStore("userStore", () => {
  const userId = ref('');
  const userName = ref('');
  const userType = ref('');
  const userToken = ref('');
  const userAvatar = ref('');
  const selectedVenueId = ref(-1);
  const expirationTime = ref('');
  return { userId, userName, userType, userToken, userAvatar, selectedVenueId, expirationTime };
},{ persist: true });

export default useUserStore;