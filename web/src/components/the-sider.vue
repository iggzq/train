<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        mode="inline"
        :style="{ height: '100%' , borderRight: 0 }"
        v-model:selected-keys="selectedKey"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined/> &nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined/> &nbsp; 乘车人管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <user-outlined/> &nbsp; 购票
        </router-link>
      </a-menu-item>
      <a-menu-item key="/my-ticket">
        <router-link to="/my-ticket">
          <user-outlined/> &nbsp; 我的车票
        </router-link>
      </a-menu-item>
      <a-menu-item key="/all-ticket" v-if="schoolPermission">
        <router-link to="/all-ticket">
          <user-outlined/> &nbsp; 学生所有车票
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import {ref, watch} from 'vue';
import router from "@/router";
import axios from "axios";
import store from "@/store";


const member = store.state.member;
const schoolPermission = ref(false);
axios.get('/member/member/getPermission', {
  params: {
    mobile: member.mobile
  }
}).then(resp => {
  if (resp.data.success) {
    console.log(resp.data);
    schoolPermission.value = resp.data.content;
  }
})

const selectedKey = ref([]);
watch(() => router.currentRoute.value.path, (newValue) => {
  selectedKey.value = [];
  selectedKey.value.push(newValue);
}, {immediate: true})


</script>

<style scoped>

</style>
