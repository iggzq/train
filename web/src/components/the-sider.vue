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
    },{immediate: true})
    return {
      selectedKey
    }
  }
});
</script>

<style scoped>

</style>
