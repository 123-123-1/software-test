import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { createPinia } from 'pinia'
import piniaPluginPersistedState from 'pinia-plugin-persistedstate';

const pinia = createPinia()
pinia.use(piniaPluginPersistedState)

const app = createApp(App)

app.use(router)
app.use(pinia)
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')
