<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        mode="inline"
        :style="{ height: '100%' , borderRight: 0 }"
        :open-keys="['batch','base','business']"
        v-model:selected-keys="selectedKey"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined/> &nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-sub-menu key="batch">
        <template #title>
          <coffee-outlined/> &nbsp; 跑批处理
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined/> &nbsp; 任务管理
          </router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="base">
        <template #title>
          <coffee-outlined/> &nbsp; 基础数据
        </template>
        <a-menu-item key="/base/station">
          <router-link to="/base/station">
            <user-outlined/> &nbsp; 车站管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train">
          <router-link to="/base/train">
            <user-outlined/> &nbsp; 火车管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-station">
          <router-link to="/base/train-station">
            <user-outlined/> &nbsp; 车站管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-carriage">
          <router-link to="/base/train-carriage">
            <user-outlined/> &nbsp; 车箱管理
          </router-link>
        </a-menu-item>
        <a-menu-item key="/base/train-seat">
          <router-link to="/base/train-seat">
            <user-outlined/> &nbsp; 火车座位
          </router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="business">
        <template #title>
          <coffee-outlined/> &nbsp; 业务管理
        </template>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <user-outlined/> &nbsp; 每日车次
          </router-link>
        </a-menu-item>

        <a-menu-item key="/business/daily-train-station">
          <router-link to="/business/daily-train-station">
            <user-outlined/> &nbsp; 每日车站
          </router-link>
        </a-menu-item>

      </a-sub-menu>
<!--      <a-menu-item key="/about">-->
<!--        <router-link to="/about">-->
<!--          <user-outlined/> &nbsp; 关于-->
<!--        </router-link>-->
<!--      </a-menu-item>-->


    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";


export default defineComponent({
  name: "the-sider",
  setup() {
    const selectedKey = ref([]);
    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch ', newValue);
      selectedKey.value = [];
      selectedKey.value.push(newValue);
    }, {immediate: true})
    return {
      selectedKey
    }
  }
});
</script>

<style scoped>

</style>
