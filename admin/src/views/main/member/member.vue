<template>
  <a-space class="top_button">
    <a-button type="primary" @click="handleQuery()">刷新</a-button>
    <a-button type="primary" @click="onAdd">新增</a-button>
  </a-space>
  <a-table :data-source="members"
           :columns="columns"
           @change="handleTableChange"
           :loading="loading"
           :scroll="{ y: 480 }">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'id'">
        <span>{{ record.id }}</span>
      </template>
      <template v-else-if="column.dataIndex === 'isSchoolAdmin'">
        <a-switch v-model:checked="record.isSchoolAdmin" @change="changeMemberPermission(record)"/>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="用户" @ok="handleOk" ok-text="保存" cancel-text="取消">
    <a-form :model="member" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="手机号" :rules="[{ required: true, message: '请输入手机号!'}]">
        <a-input v-model:value="member.mobile"/>
      </a-form-item>
      <a-form-item label="权限" :rules="[{ required: true, message: '请选择权限!'}]">
        <a-switch v-model:checked="member.isSchoolAdmin"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

const visible = ref(false);
const member = ref({
  mobile: undefined,
  isSchoolAdmin: undefined
});
const members = ref([]);
// 分页的三个属性名是固定的
let loading = ref(false);
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '手机号',
    dataIndex: 'mobile',
    key: 'mobile',
  },
  {
    title: '是否是学校管理员',
    dataIndex: 'isSchoolAdmin',
    key: 'isSchoolAdmin'
  }
];

const onAdd = () => {
  member.value = {};
  visible.value = true;
};


const handleOk = () => {
  axios.post("/member/admin/member/save", member.value).then((response) => {
    let data = response.data;
    if (data.success) {
      notification.success({description: "保存成功！"});
      visible.value = false;
      handleQuery();
    } else {
      notification.error({description: data.message});
    }
  });
};

const handleQuery = () => {
  loading.value = true;
  axios.get("/member/admin/member/query-list").then((response) => {
    loading.value = false;
    console.log(response.data);
    let data = response.data;
    if (data.success) {
      members.value = data.content;
    } else {
      notification.error({description: data.message});
    }
  });
};

const handleTableChange = () => {
  handleQuery();
};

const changeMemberPermission = (record) => {
  axios.post("/member/admin/member/changePermission", {
    id: record.id,
    isSchoolAdmin: record.isSchoolAdmin
  }).then((response) => {
    let data = response.data;
    if (data.success) {
      notification.success({description: data.content});
    } else {
      notification.error({description: data.content});
    }
  });
}

onMounted(() => {
  handleQuery();
});

</script>
<style scoped>
.top_button {
  position: relative;
  display: flex;
}
</style>