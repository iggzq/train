<template>
  <a-space>
    <a-button type="primary" @click="handleQuery()">刷新</a-button>
    <a-button type="primary" @click="showModal">新增</a-button>
  </a-space>

  <a-table :data-source="passengers"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading"/>
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


</template>

<script>
import {defineComponent, reactive, ref, onMounted} from "vue";
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

    const passengers = ref([]);

    const columns = [{
      title: "姓名",
      dataIndex: "name",
      key: "name",
    }, {
      title: "身份证",
      dataIndex: "idCard",
      key: "idCard",
    }, {
      title: "类型",
      dataIndex: "type",
      key: "type",
    }];

    const pagination = reactive({
      total: 0,
      current: 1,
      pageSize: 2,
    })

    const showModal = () => {
      visible.value = true;
    }

    let loading = ref(false);

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
    };

    const handleQuery = (param) => {
      console.log("1 " + param);
      if (!param) {
        param = {
          page: 1,
          size: pagination.pageSize,
        }
      }
      loading.value = true;
      console.log("2 " + param)
      axios.get("/member/passenger/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((resp) => {
        loading.value = false;
        let data = resp.data;
        if (data.success) {
          passengers.value = data.content.data;
          pagination.current = param.page;
        } else {
          notification.error({description: data.message});
        }
      })
    };

    const handleTableChange = (pagination) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize,
      })
    }

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.pageSize,
      })
    });

    return {
      visible,
      showModal,
      handleOk,
      passenger,
      passengers,
      handleQuery,
      columns,
      pagination,
      handleTableChange
    };
  },

})
</script>
<style scoped>
</style>