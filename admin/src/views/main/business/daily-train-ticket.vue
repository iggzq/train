<template>
  <a-space class="top_button">
    <train-select-view v-model:value="params.trainCode" style="width: 150px"></train-select-view>
    <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD"></a-date-picker>
    <station-select-view v-model="params.start" style="width: 150px"></station-select-view>
    <station-select-view v-model="params.end" style="width: 150px"></station-select-view>
    <a-button type="primary" @click="handleQuery()">查找/刷新</a-button>

  </a-space>
  <a-table :data-source="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'station'">
        {{ record.start }}<br/>
        {{ record.end }}
      </template>
      <template v-else-if="column.dataIndex === 'time'">
        到站时间：{{ record.startTime }}<br/>
        出站时间：{{ record.endTime }}
      </template>
      <template v-else-if="column.dataIndex === 'duration'">
        {{ calDuration(record.startTime, record.endTime) }}<br/>
        <div v-if="record.startTime.replaceAll(':','') >= record.endTime.replaceAll(':','')">
          次日到达
        </div>
        <div v-else>
          当日到达
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'ydz'">
        <div v-if="record.ydz >= 0">
          余票：{{ record.ydz }}<br/>
          单价：{{ record.ydzPrice }}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'edz'">
        <div v-if="record.edz >= 0">
          余票：{{ record.edz }}<br/>
          单价：{{ record.edzPrice }}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'rw'">
        <div v-if="record.rw >= 0">
          余票：{{ record.rw }}<br/>
          单价：{{ record.rwPrice }}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'yw'">
        <div v-if="record.yw >= 0">
          余票：{{ record.yw }}<br/>
          单价：{{ record.ywPrice }}￥
        </div>
        <div v-else>
          --
        </div>

      </template>
    </template>

  </a-table>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";

const params = ref({
  trainCode: null,
  date: null,
  start: null,
  end: null
});
const dailyTrainTickets = ref([]);
// 分页的三个属性名是固定的
const pagination = ref({
  total: 0,
  current: 1,
  pageSize: 10,
});
let loading = ref(false);
const columns = [
  {
    title: '日期',
    dataIndex: 'date',
  },
  {
    title: '车次编号',
    dataIndex: 'trainCode',
  },
  {
    title: '车站',
    dataIndex: 'station'
  },
  {
    title: '时间',
    dataIndex: 'time'
  },
  {
    title: '历时',
    dataIndex: 'duration'
  },
  {
    title: '一等座',
    dataIndex: 'ydz',
  },
  {
    title: '二等座',
    dataIndex: 'edz',
  },
  {
    title: '软卧',
    dataIndex: 'rw',
  },
  {
    title: '硬卧',
    dataIndex: 'yw',
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
  axios.get("/business/admin/daily-train-ticket/query-list", {
    params: {
      page: param.page,
      size: param.size,
      trainCode: params.value.trainCode,
      date: params.value.date,
      start: params.value.start,
      end: params.value.end,
    }
  }).then((response) => {
    loading.value = false;
    let data = response.data;
    if (data.success) {
      dailyTrainTickets.value = data.content.data;
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

const calDuration = (startTime, endTime) => {
  let diff = dayjs(endTime, 'HH:mm:ss').diff(dayjs(startTime, 'HH:mm:ss'), 'second', true);
  return dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
}

onMounted(() => {
  handleQuery({
    page: 1,
    size: pagination.value.pageSize
  });
});

</script>
<style scoped>
.top_button {
  position: relative;
  display: flex;
}
</style>