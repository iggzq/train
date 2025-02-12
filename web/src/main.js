import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import './assets/js/enums';


const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')

const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

axios.interceptors.request.use(function (config) {
    console.log('请求参数： ', config)
    const token = store.state.member.token;
    if (token) {
        config.headers.token = token;
        console.log("为请求增加token header:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
})

axios.interceptors.response.use(function (response) {
    console.log('返回结果： ', response)
    return response;
}, error => {
    const resp = error.response;
    const status = resp.status;
    if (status === 401) {
        console.log("未登录或登陆超时，跳到登陆页");
        store.commit("setMember", {});
        notification.error({description: "未登录或登陆超时"});
        router.push('/login');
    } else {
        notification.error({description: '系统出错，请联系管理员！'})
    }
    return Promise.reject(error);
})
axios.defaults.baseURL = process.env.VUE_APP_SERVER
console.log('环境：', process.env.NODE_ENV)
console.log('后端地址：', process.env.VUE_APP_SERVER)