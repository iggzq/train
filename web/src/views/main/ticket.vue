<template>
  <a-space class="top_button">
    日期：
    <a-date-picker :disabled-date="disabledDate" v-model:value="params.date" valueFormat="YYYY-MM-DD"
                   placeholder="请选择出发日期"></a-date-picker>
    始：
    <station-select-view v-model="params.start" style="width: 150px" placeholder="请选择上车站"></station-select-view>
    终：
    <station-select-view v-model="params.end" style="width: 150px" placeholder="请选择下车站"></station-select-view>
    <a-button type="primary" @click="handleQuery()">刷新</a-button>

  </a-space>
  <a-table :data-source="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading"
           :row-class-name="(_record, index) => (index % 2 === 1 ? 'table-striped' : null)">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-button type="primary" @click="toOrder(record)">预定</a-button>
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

<script>
import {defineComponent, ref, onMounted} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";
import StationSelectView from "@/components/station-select.vue";
import dayjs from 'dayjs';
import router from "@/router";

export default defineComponent({
  name: "ticket-view",
  components: {StationSelectView},
  setup() {
    const visible = ref(false);
    let dailyTrainTicket = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      start: undefined,
      startPinyin: undefined,
      startTime: undefined,
      startIndex: undefined,
      end: undefined,
      endPinyin: undefined,
      endTime: undefined,
      endIndex: undefined,
      ydz: undefined,
      ydzPrice: undefined,
      edz: undefined,
      edzPrice: undefined,
      rw: undefined,
      rwPrice: undefined,
      yw: undefined,
      ywPrice: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
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
      {
        title: '操作',
        dataIndex: 'operation',
      }
    ];


    const handleQuery = (param) => {
      if (Tool.isEmpty(params.value.start)) {
        notification.error({description: '请选择上车站'});
        return;
      }
      if (Tool.isEmpty(params.value.end)) {
        notification.error({description: '请选择下车站'});
        return;
      }
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize,
          date: dayjs().startOf('day'),
        };
      }
      SessionStorage.set(SESSION_TICKET_PARAMS, params.value)
      loading.value = true;
      axios.get("/business/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode,
          date: params.value.date === null ? dayjs().startOf('day').format('YYYY-MM-DD') : params.value.date,
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

    const disabledDate = current => {
      //不能够选择今天之前的，还有从当天15天后的
      return current < dayjs().startOf('day') || current > dayjs().startOf('day').add(15, 'days');
    };
    const toOrder = (param) => {

      dailyTrainTicket.value = Tool.copy(param);
      SessionStorage.set(SESSION_ORDER, dailyTrainTicket.value);
      router.push("/order");
    }

    onMounted(() => {
      params.value = SessionStorage.get(SESSION_TICKET_PARAMS) || {};
      if (Tool.isNotEmpty(params.value)) {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize,
          date: dayjs().startOf('day'),
        });
      }
    });


    return {
      dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      params,
      calDuration,
      disabledDate,
      toOrder
    };
  },
});
</script>
<style scoped>
.top_button {
  position: relative;
  display: flex;
}
</style>