<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <div style="float: right; color: white">
      您好：{{ member.mobile }}
      <router-link to="/login" @click.prevent="clearLoginInfo()">
        退出登陆
      </router-link>
    </div>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
        v-model:selected-keys="selectedKeys"
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
    </a-menu>
  </a-layout-header>
</template>

<script setup>
import {ref, watch} from 'vue';
import store from "@/store";
import router from "@/router";


const member = store.state.member;
const clearLoginInfo = (() => {
  store.commit("clearLoginInfo",{})
})
const selectedKeys = ref([]);
watch(() => router.currentRoute.value.path, (newValue) => {
  selectedKeys.value = [];
  selectedKeys.value.push(newValue);
}, {immediate: true});
</script>

<style scoped>

</style>
