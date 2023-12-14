<template>
    <a-space class="top_button">
            <a-button type="primary" @click="handleQuery()">刷新</a-button>
            
    </a-space>
    <a-table :data-source="dailyTrainStationSeats"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading">
        <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'operation'">
            </template>
                    <template v-else-if="column.dataIndex === 'col'">
        <span v-for="item in SEAT_COL_ARRAY" :key="item.key">
          <span v-if="item.key === record.col">
            {{item.value}}
          </span>
        </span>
                    </template>
                    <template v-else-if="column.dataIndex === 'seatType'">
        <span v-for="item in SEAT_TYPE_ARRAY" :key="item.key">
          <span v-if="item.key === record.seatType">
            {{item.value}}
          </span>
        </span>
                    </template>
        </template>
    </a-table>
</template>

<script>
import {defineComponent, ref, onMounted} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

    export default defineComponent({
        name: "daily-train-seat-view",
        setup() {
            const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
            const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
            const visible = ref(false);
            let dailyTrainStationSeat = ref({
                id: undefined,
                date: undefined,
                trainCode: undefined,
                carriageIndex: undefined,
                row: undefined,
                col: undefined,
                seatType: undefined,
                carriageSeatIndex: undefined,
                sell: undefined,
                createTime: undefined,
                updateTime: undefined,
            });
            const dailyTrainStationSeats = ref([]);
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
                    key: 'date',
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
                    dataIndex: 'row',
                    key: 'row',
                },
                {
                    title: '列号',
                    dataIndex: 'col',
                    key: 'col',
                },
                {
                    title: '座位类型',
                    dataIndex: 'seatType',
                    key: 'seatType',
                },
                {
                    title: '同车箱座序',
                    dataIndex: 'carriageSeatIndex',
                    key: 'carriageSeatIndex',
                },
                {
                    title: '售卖情况',
                    dataIndex: 'sell',
                    key: 'sell',
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
                axios.get("/business/admin/daily-train-seat/query-list", {
                    params: {
                        page: param.page,
                        size: param.size
                    }
                }).then((response) => {
                    loading.value = false;
                    let data = response.data;
                    if (data.success) {
                        dailyTrainStationSeats.value = data.content.data;
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

            return {
                SEAT_COL_ARRAY,
                SEAT_TYPE_ARRAY,
                dailyTrainStationSeat,
                visible,
                dailyTrainStationSeats,
                pagination,
                columns,
                handleTableChange,
                handleQuery,
                loading,
            };
        },
    });
</script>
<style scoped>
.top_button{
  position: relative;
  display: flex;
}
</style>