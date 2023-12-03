<template>
  <a-layout>
    <the-header/>
    <a-layout-content style="padding: 0 50px">
      <a-layout style="padding: 24px 0; background: #fff">
        <the-sider></the-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
          <router-view></router-view>
        </a-layout-content>
      </a-layout>
    </a-layout-content>
    <a-layout-footer style="text-align: center">
      Ant Design ©2018 Created by Ant UED
    </a-layout-footer>
  </a-layout>
</template>
<script>
import {defineComponent} from 'vue';
import TheHeader from "@/components/the-header.vue";
import TheSider from "@/components/the-sider.vue";

export default defineComponent({
  components: {
    TheSider,
    TheHeader,
  },
  setup() {
    return {};
  },
});
</script>
<style>
</style>