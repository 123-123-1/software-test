import { createRouter, createWebHistory } from 'vue-router'
import PageLayout from '@/PageLayout.vue'
import useUserStore from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useMenuStore } from '@/stores/menuStore';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // {
    //   path: '/',
    //   name: 'home',
    //   component: HomeView,
    // },
    {
      path: '/user/login',
      name: 'user-login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/user/register',
      name: 'user-register',
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/',
      name: 'main',
      component: PageLayout,
      children: [
        {
          path: '/venues/list',
          name: 'venues-list',
          component: () => import('../views/VenueList.vue'),
        },
        {
          path: '/venues/detail',
          name: 'venue-detail',
          component: () => import('../views/VenueDetail.vue'),
        },
        {
          path: '/reservations/reserve',
          name: 'reservations-reserve',
          component: () => import('../views/VenueReservation.vue'),
        },
        {
          path: '/reservations/list',
          name: 'reservations-list',
          component: () => import('../views/ReservationList.vue'),
        },
        {
          path: '/reservations/detail',
          name: 'reservations-detail',
          component: () => import('../views/ReservationDetail.vue'),
        },
        {
          path: '/groups/list',
          name: 'groups-list',
          component: () => import('../views/GroupList.vue'),
        },
        {
          path: '/groups/detail',
          name: 'groups-detail',
          component: () => import('../views/GroupDetail.vue'),
        },
        {
          path: '/groups/create',
          name: 'groups-create',
          component: () => import('../views/GroupCreate.vue'),
        },
        {
          path: '/groups/applications',
          name: 'groups-applications',
          component: () => import('../views/GroupApplication.vue'),
        },

        {
          path: '/user/profile',
          name: 'user-profile',
          component: () => import('../views/UserProfile.vue'),
        },
        {
          path: '/user/notifications',
          name: 'notifications',
          component: () => import('../views/UserNotification.vue'),
        },
        {
          path: '/chats/list',
          name: 'chats-list',
          component: () => import('../views/ChatList.vue'),
        },
        {
          path: '/chats/redirect',
          name: 'chat-redirect',
          component: () => import('../views/ChatRedirect.vue')
        },
        {
          path: '/chats/applications',
          name: 'chats-applications',
          component: () => import('../views/FriendApplication.vue'),
        },
        {
          name: 'infomanage-venue',
          path: '/management/info/venue',
          component: () => import('../views/VenueEdit.vue'),
        },
        {
          name: 'infomanage-timeslot',
          path: '/management/info/timeslots',
          component: () => import('../views/AvailabilityEdit.vue'),
        },
        {
          name: 'apimanage-reservations',
          path: '/management/apiconfig/reservations',
          component: () => import('../views/ReservationApiConfig.vue'),
        },
        {
          name: 'apimanage-userinfo',
          path: '/management/apiconfig/userinfo',
          component: () => import('../views/UserInfoConfig.vue'),
        },
        {
          name: 'apimanage-occupy',
          path: '/management/apiconfig/occupy',
          component: () => import('../views/OccupyApiConfig.vue'),
        },
        {
          name: 'reservationmanage-list',
          path: '/management/reservations/list',
          component: () => import('../views/ReservationManagementList.vue')
        },
        {
          name: 'reservationmanage-detail',
          path: '/management/reservations/detail',
          component: () => import('../views/ReservationManagementDetail.vue')
        },
        {
          name: 'manager-profile',
          path: '/management/profile',
          component: () => import('../views/UserProfile.vue')
        }
      ]
    }
  ],
})

router.beforeEach((to) => {
  if(to.path === '/user/login' || to.path === '/user/register'){
    return true;
  }
  const userStore = useUserStore();
  const { userType, userToken, expirationTime } = storeToRefs(userStore);
  if(!userToken.value || Date.now() >= expirationTime.value){
    return '/user/login';
  }
  if(userType.value === 'venueadmin' && !to.path.startsWith('/management')){
    return '/management/info/venue'
  }
  if(to.path === '/'){
    return '/venues/list';
  }
  return true;
});

router.afterEach((to) => {
  const menuStore = useMenuStore();
  menuStore.updateMenu(to.path);
});

export default router
