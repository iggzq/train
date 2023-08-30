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
      <a-menu-item key="3">nav 3</a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref} from 'vue';
import store from "@/store";


export default defineComponent({
  name: "the-header",
  setup() {
    let member = store.state.member;
    return{
      member,
    }
  }
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
