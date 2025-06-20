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
        },
        children: [{
            path: '/welcome',
            component: () => import('../views/main/welcome.vue'),
        },{
            path: '/passenger',
            component: () => import('../views/main/passenger.vue')
        },{
            path: '/ticket',
            component: () => import('../views/main/ticket.vue')
        },{
            path: '/order',
            component: () => import('../views/main/order.vue')
        },{
            path: '/orderConfirm',
            component: () => import('../views/main/orderConfirm.vue')
        },{
            path: '/my-ticket',
            component: () => import('../views/main/my-ticket.vue')
        },{
            path: '/all-ticket',
            component: () => import('../views/main/all-ticket.vue')
        }]
    },
    {
        path: '',
        redirect: '/welcome'
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登陆校验：", item.meta.loginRequire || false);

        return item.meta.loginRequire;
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
