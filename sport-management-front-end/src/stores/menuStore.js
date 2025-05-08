import { defineStore } from "pinia";
import { ref } from 'vue';

export const useMenuStore = defineStore('menuStore', () => {
  const selectedPage = ref({});
  const selectedSubsystem = ref('');
  const menuData = ref([]);
  const updateMenu = (path) => {
    for(const menuItem of menuData.value){
      for(const subMenuItem of menuItem.submenu){
        if(subMenuItem.link === path){
          selectedPage.value = subMenuItem;
          selectedSubsystem.value = menuItem.index;
          return;
        }
      }
    }
  };
  const updateMenuByIndex = (index) => {
    for(const menuItem of menuData.value){
      for(const subMenuItem of menuItem.submenu){
        if(subMenuItem.index === index){
          selectedPage.value = subMenuItem;
          selectedSubsystem.value = menuItem.index;
          return;
        }
      }
    }
  };
  return { selectedPage, menuData, selectedSubsystem, updateMenu, updateMenuByIndex };
}, { persist: true });