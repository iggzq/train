<template>
  <a-space class="top_button">
    <train-select-view v-model="params.code" @change="onChangeCode" style="width: 150px"></train-select-view>
    <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD"
                   placeholder="请选择日期"/>
    <a-button type="primary" @click="handleQuery()">查找</a-button>
    <a-button type="primary" @click="onAdd">新增</a-button>
    <a-button type="danger" shape="round" @click="onClickGenDaily">手动生成车次信息</a-button>
  </a-space>
  <a-table :data-source="dailyTrains"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
          <a-popconfirm
              title="删除后不可恢复，确认删除?"
              @confirm="onDelete(record)"
              ok-text="确认" cancel-text="取消">
            <a style="color: red">删除</a>
          </a-popconfirm>
          <a @click="onEdit(record)">编辑</a>
        </a-space>
      </template>
      <template v-else-if="column.dataIndex === 'type'">
        <span v-for="item in TRAIN_TYPE_ARRAY" :key="item.key">
          <span v-if="item.key === record.type">
            {{ item.value }}
          </span>
        </span>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="每日车次" @ok="handleOk" ok-text="确认" cancel-text="取消">
    <a-form :model="dailyTrain" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="日期">
        <a-date-picker v-model:value="dailyTrain.date" valueFormat="YYYY-MM-DD"
                       placeholder="请选择日期"/>
      </a-form-item>
      <a-form-item label="车次编号">
        <train-select-view v-model="dailyTrain.code" @change="onChangeCode"></train-select-view>
      </a-form-item>
      <a-form-item label="车次类型">
        <a-select v-model:value="dailyTrain.type">
          <a-select-option v-for="item in TRAIN_TYPE_ARRAY" :key="item.key" :value="item.key">{{ item.value }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="始发站">
        <a-input v-model:value="dailyTrain.start" disabled/>
      </a-form-item>
      <a-form-item label="始发站拼音">
        <a-input v-model:value="dailyTrain.startPinyin" disabled/>
      </a-form-item>
      <a-form-item label="出发时间">
        <a-time-picker v-model:value="dailyTrain.startTime" valueFormat="HH:mm:ss"
                       placeholder="请选择时间" disabled/>
      </a-form-item>
      <a-form-item label="终点站">
        <a-input v-model:value="dailyTrain.end" disabled/>
      </a-form-item>
      <a-form-item label="终点站拼音">
        <a-input v-model:value="dailyTrain.endPinyin" disabled/>
      </a-form-item>
      <a-form-item label="到站时间">
        <a-time-picker v-model:value="dailyTrain.endTime" valueFormat="HH:mm:ss"
                       placeholder="请选择时间" disabled/>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal v-model:visible="genDailyVisible" title="生成车次" @ok="handleGenDailyOk"
           :confirm-loading="genDailyLoading" ok-text="确认" cancel-text="取消">
    <a-form :model="genDaily" :label-col="{span: 4}" :wrapper-col="{span: 20}">
      <a-form-item label="日期">
        <a-date-picker v-model:value="genDaily.date" placeholder="请选择日期"></a-date-picker>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import dayjs from "dayjs";

export default defineComponent({
  name: "daily-train-view",
  components: {TrainSelectView},
  setup() {
    const TRAIN_TYPE_ARRAY = window.TRAIN_TYPE_ARRAY;
    const visible = ref(false);
    const genDailyVisible = ref(false);
    const genDaily = ref({
      date: null,
    });
    const genDailyLoading = ref(false);
    let dailyTrain = ref({
      id: undefined,
      date: undefined,
      code: undefined,
      type: undefined,
      start: undefined,
      startPinyin: undefined,
      startTime: undefined,
      end: undefined,
      endPinyin: undefined,
      endTime: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrains = ref([]);
    // 分页的三个属性名是固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      code: null,
      date: null,
    });
    const columns = [
      {
        title: '日期',
        dataIndex: 'date',
        key: 'date',
      },
      {
        title: '车次编号',
        dataIndex: 'code',
        key: 'code',
      },
      {
        title: '车次类型',
        dataIndex: 'type',
        key: 'type',
      },
      {
        title: '始发站',
        dataIndex: 'start',
        key: 'start',
      },
      {
        title: '始发站拼音',
        dataIndex: 'startPinyin',
        key: 'startPinyin',
      },
      {
        title: '出发时间',
        dataIndex: 'startTime',
        key: 'startTime',
      },
      {
        title: '终点站',
        dataIndex: 'end',
        key: 'end',
      },
      {
        title: '终点站拼音',
        dataIndex: 'endPinyin',
        key: 'endPinyin',
      },
      {
        title: '到站时间',
        dataIndex: 'endTime',
        key: 'endTime',
      },
      {
        title: '操作',
        dataIndex: 'operation'
      }
    ];

    const onAdd = () => {
      dailyTrain.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrain.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "删除成功！"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/daily-train/save", dailyTrain.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "保存成功！"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train/query-list", {
        params: {
          page: param.page,
          size: param.size,
          code: params.value.code,
          date: params.value.date,
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrains.value = data.content.data;
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

    const onChangeCode = (train) => {
      console.log("train:" + JSON.stringify(train));
      if (train) {
        dailyTrain.value.code = train.code;
        dailyTrain.value.type = train.type;
        dailyTrain.value.start = train.start;
        dailyTrain.value.startPinyin = train.startPinyin;
        dailyTrain.value.end = train.end;
        dailyTrain.value.endPinyin = train.endPinyin;
        dailyTrain.value.startTime = train.startTime;
        dailyTrain.value.endTime = train.endTime;
      }
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
    };
    const onClickGenDaily = () => {
      genDailyVisible.value = true;
    }

    const handleGenDailyOk = () => {
      loading.value = true;
      genDailyLoading.value = true;
      let date = dayjs(genDaily.value.date).format("YYYY-MM-DD");
      axios.get("/business/admin/daily-train/gen-daily/" + date).then((resp) => {
        let data = resp.data;
        if (data.success) {
          loading.value = false;
          visible.value = false;
          notification.success({description: "生成成功!"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
          genDailyVisible.value = false;
        } else {
          loading.value = false;
          notification.error({description: data.message});
          genDailyVisible.value = false;

        }
      })
      genDailyLoading.value = false;

    }

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      TRAIN_TYPE_ARRAY,
      dailyTrain,
      visible,
      dailyTrains,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      onChangeCode,
      params,
      genDailyVisible,
      onClickGenDaily,
      genDaily,
      handleGenDailyOk,
      genDailyLoading
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