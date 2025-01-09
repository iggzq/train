<template>
  <a-space class="top_button">
    <a-button type="primary" @click="handleQuery()">刷新</a-button>

  </a-space>
  <a-table :data-source="tickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'seatCol'">
        <span v-for="item in SEAT_COL_ARRAY" :key="item.key">
          <span v-if="item.key === record.seatCol && item.type === record.seatType">
            {{ item.value }}
          </span>
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'seatType'">
        <span v-for="item in SEAT_TYPE_ARRAY" :key="item.key">
          <span v-if="item.key === record.seatType">
            {{ item.value }}
          </span>
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'status'">
        <span v-for="item in ORDER_STATUS" :key="item.code">
          <span v-if="item.code === record.status">
            {{ item.desc }}
          </span>
          <!--          <a-button type="primary" shape="round"  v-if="item.code === 'P'" @click="ensureGoPay">去支付</a-button>-->
        </span>
      </template>
    </template>
  </a-table>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";


const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
const ORDER_STATUS = window.CONFIRM_ORDER_STATUS_ARRAY;
const tickets = ref([]);
// 分页的三个属性名是固定的
const pagination = ref({
  total: 0,
  current: 1,
  pageSize: 10,
});
let loading = ref(false);
const columns = [
  {
    title: '乘客姓名',
    dataIndex: 'passengerName',
    key: 'passengerName',
  },
  {
    title: '日期',
    dataIndex: 'trainDate',
    key: 'trainDate',
  },
  {
    title: '车次编号',
    dataIndex: 'trainCode',
    key: 'trainCode',
  },
  {
    title: '箱序',
    dataIndex: 'carriageIndex',
    key: 'carriageIndex',
  },
  {
    title: '排号',
    dataIndex: 'seatRow',
    key: 'seatRow',
  },
  {
    title: '列号',
    dataIndex: 'seatCol',
    key: 'seatCol',
  },
  {
    title: '出发站',
    dataIndex: 'startStation',
    key: 'startStation',
  },
  {
    title: '出发时间',
    dataIndex: 'startTime',
    key: 'startTime',
  },
  {
    title: '到达站',
    dataIndex: 'endStation',
    key: 'endStation',
  },
  {
    title: '到站时间',
    dataIndex: 'endTime',
    key: 'endTime',
  },
  {
    title: '座位类型',
    dataIndex: 'seatType',
    key: 'seatType',
  }, {
    title: '订单状态',
    dataIndex: 'status',
    key: 'status',
  },

];


const handleQuery = (param) => {
  if (!param) {
    param = {
      page: 1,
      size: pagination.value.pageSize
    };
  }
  loading.value = true;
  axios.get("/member/ticket/query-list", {
    params: {
      page: param.page,
      size: param.size
    }
  }).then((response) => {
    loading.value = false;
    let data = response.data;
    if (data.success) {
      tickets.value = data.content.data;
      // 设置分页控件的值
      pagination.value.current = param.page;
      pagination.value.total = data.content.total;
    } else {
      notification.error({description: data.message});
    }
  });
};

const handleTableChange = (page) => {
  // console.log("看看自带的分页参数都有啥：" + JSON.stringify(page));
  pagination.value.pageSize = page.pageSize;
  handleQuery({
    page: page.current,
    size: page.pageSize
  });
};

onMounted(() => {
  handleQuery({
    page: 1,
    size: pagination.value.pageSize
  });
});

// const ensureGoPay = () => {
//   axios.post("/business/ticket-pay/pay",{
//     tradeNum: orderInfo.content.tradeNum,
//     tradeName: orderInfo.content.tradeName,
//     subject: orderInfo.content.subject,
//   }).then((resp) => {
//     const htmlCode = resp.data;
//     const newWindow = window.open('', '_blank');
//     newWindow.document.write(htmlCode);
//   })
// }
</script>
<style scoped>
.top_button {
  position: relative;
  display: flex;
}
</style>