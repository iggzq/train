<template>
  <a-button type="primary" @click="showModal">新增</a-button>
  <a-modal v-model:visible="visible" title="乘车人" @ok="handleOk" ok-text="确认" cancel-text="取消">
    <a-form :model="passenger" :label-col="{span: 4}" :wrapper-col="{span: 20}">
      <a-form-item label="姓名">
        <a-input v-model:value="passenger.name"/>
      </a-form-item>
      <a-form-item label="身份证">
        <a-input v-model:value="passenger.idCard"/>
      </a-form-item>
      <a-form-item label="类型">
        <a-select v-model="passenger.type">
          <a-select-option value="1">成人</a-select-option>
          <a-select-option value="2">儿童</a-select-option>
          <a-select-option value="3">学生</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>

  </a-modal>
  <h1>乘车人管理</h1>
</template>

<script>
import {defineComponent, reactive, ref} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  setup() {
    const visible = ref(false);
    const passenger = reactive({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const showModal = () => {
      visible.value = true;
    }
    const handleOk = (e) => {
      axios.post("member/passenger/save", passenger).then((resp) => {
        let data = resp.data;
        if (data.success) {
          notification.success({description: "保存成功"});
          visible.value = false;
        } else {
          notification.error({description: "保存失败"})
        }
      })
      console.log(e);
      visible.value = false;
    }
    return {
      visible,
      showModal,
      handleOk,
      passenger,
    };
  },

})
</script>
<style scoped>
</style>