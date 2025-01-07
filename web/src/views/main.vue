<template>
  <a-layout>
    <the-header/>
    <a-layout style="padding: 24px 0; background: #fff">
      <the-sider/>
      <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
        <router-view/>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>
<script setup>
import {defineComponent} from 'vue';
import TheHeader from "@/components/the-header.vue";
import TheSider from "@/components/the-sider.vue";

defineComponent({
  components: {
    TheHeader,
    TheSider
  }
})
</script>
<style scoped>
</style>