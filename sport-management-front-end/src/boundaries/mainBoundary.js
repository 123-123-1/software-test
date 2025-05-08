
// 用户端左侧菜单中显示的项目
export const userMenuData = [
  {
    title: '场地信息',
    index: 'venues',
    submenu: [
      {
        title: '场馆列表',
        index: 'venues-list',
        link: '/venues/list',
      }
    ],
  },
  {
    title: '预约管理',
    index: 'reservations',
    submenu: [
      {
        title: '场馆预约',
        index: 'reservations-reserve',
        link: '/reservations/reserve',
      },
      {
        title: '预约记录',
        index: 'reservations-list',
        link: '/reservations/list',
      }
    ],
  },
  {
    title: '团体管理',
    index: 'groups',
    submenu: [
      {
        title: '团体列表',
        index: 'groups-list',
        link: '/groups/list',
      },
      {
        title: '团体申请',
        index: 'groups-applications',
        link: '/groups/applications',
      },
      {
        title: '创建团体',
        index: 'groups-create',
        link: '/groups/create',
      }
    ],
  },
  {
    title: '社交管理',
    index: 'chats',
    submenu: [
      {
        title: '群聊列表',
        index: 'chats-group',
        link: {
          path: '/chats/redirect',
          query: { type: 'group' }
        },
      },
      {
        title: '好友列表',
        index: 'chats-friends',
        link: {
          path: '/chats/redirect',
          query: { type: 'friend' }
        },
      },
      {
        title: '好友申请',
        index: 'chats-applications',
        link: '/chats/applications'
      },
    ],
  },
  {
    title: '用户管理',
    index: 'user',
    submenu: [
      {
        title: '个人信息',
        index: 'user-profile',
        link: '/user/profile',
      },
      {
        title: '通知',
        index: 'user-notifications',
        link: '/user/notifications',
      }
    ]
  }
];

// 管理端左侧菜单项中显示的项目
export const managerMenuData = [
  {
    title: '信息管理',
    index: 'infomanage',
    submenu: [
      {
        title: '场地信息管理',
        index: 'infomanage-venue',
        link: '/management/info/venue'
      },
      {
        title: '开放时间段管理',
        index: 'infomanage-timeslots',
        link: '/management/info/timeslots'
      }
    ],
  },
  {
    title: '接口配置',
    index: 'apimanage',
    submenu: [
      {
        title: '预约接口',
        index: 'apimanage-reservations',
        link: '/management/apiconfig/reservations',
      },
      {
        title: '用户信息数据',
        index: 'apimanage-userinfo',
        link: '/management/apiconfig/userinfo',
      },
      {
        title: '场地占用',
        index: 'apimanage-occupy',
        link: '/management/apiconfig/occupy',
      }
    ]
  },
  {
    title: '预约管理',
    index: 'reservationmanage',
    submenu: [
      {
        title: '预约列表',
        index: 'reservationmanage-list',
        link: '/management/reservations/list',
      }
    ]
  },
  {
    title: '用户管理',
    index: 'manager',
    submenu: [
      {
        title: '个人信息',
        index: 'manager-profile',
        link: '/management/profile',
      }
    ]
  }
];

// 根据菜单选中情况查找选中的菜单项
export const findSelectedItem = (index, menuData) => {
  for(const menuItem of menuData){
    for(const submenuItem of menuItem.submenu){
      if(submenuItem.index === index){
        return submenuItem;
      }
    }
  }
  return {};
};