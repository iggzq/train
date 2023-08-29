import {createRouter, createWebHistory} from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
    {
        path: '/login',
        component: () => import('../views/login.vue')
    },
    {
        path: '/',
        component: () => import('../views/main.vue'),
        meta:{
            loginRequire: true
        }
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登陆校验：", item.meta.loginRequire || false);

        return item.meta.loginRequire
    })) {
        const member = store.state.member;
        console.log("登录页面校验开始:", member);
        if (!member.token) {
            console.log("用户未登录或登陆超时");
            notification.error({description: "未登录或登陆超时"});
            next("/login");
        }else {
            next();
        }
    }else {
        next();
    }
});

export default router
