<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <div style="float: right; color: white">
      您好：{{member.mobile}}
      <router-link to="/login">
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
          <coffee-outlined /> &nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined /> &nbsp; 乘车人管理
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import router from "@/router";


export default defineComponent({
  setup() {
    let member = store.state.member;
    const selectedKeys = ref([]);
    watch(() => router.currentRoute.value.path, (newValue) => {
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    },{immediate: true});
    return{
      member,
      selectedKeys,
    }
  }
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
