<template>
  <div class="order-train">
    <span class="order-train-main">{{ dailyTrainTicket.data }}</span>&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.trainCode }}</span>次&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.start }}</span>站
    <span class="order-train-main">({{ dailyTrainTicket.startTime }})</span>&nbsp;
    <span class="order-train-main">——</span>&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.end }}</span>站
    <span class="order-train-main">({{ dailyTrainTicket.endTime }})</span>&nbsp;
  </div>
  <div class="order-train-ticket">
    <span v-for="item in seatTypes" :key="item.key">
      <span>{{ item.value }}：</span>
      <span class="order-train-ticket-main">{{ item.price }}￥</span>&nbsp;
      <span class="order-train-ticket-main">{{ item.count }}</span>&nbsp;张票&nbsp;&nbsp;
    </span>
  </div>
  <a-divider></a-divider>

  <div class="passengerChose">
    <b>勾选要购票的乘客:</b>
  </div>
  <br/>


  <div class="passengers">
    <a-checkbox-group v-model:value="passengerChecked" :options="passengerOptions"></a-checkbox-group>
  </div>


  <a-table class="order-tickets" :columns="columns" bordered :data-source="tickets" :pagination="pagination">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'passengerType'">
        <a-select v-model:value="record.passengerType" style="width: 100%">
          <a-select-option v-for="item in PASSENGER_TYPE_ARRAY" :key="item.key">
            {{ item.value }}
          </a-select-option>
        </a-select>
      </template>

      <template v-else-if="column.dataIndex ==='seatTypeCode'">
        <a-select v-model:value="record.seatTypeCode" style="width: 100%">
          <a-select-option v-for="item in seatTypes" :key="item.key" :value="item.key">
            {{ item.value }}
          </a-select-option>
        </a-select>
      </template>

      <template v-else-if="column.dataIndex === 'seatPosition' && record.seatTypeCode === '1'">
        <a-select v-model:value="record.seatPosition" style="width: 100%" :placeholder="seatTypes[0].count <= 20 ? '剩余座位数小于20,不可选座' : '可选择座位'"
                  :disabled="seatTypes[0].count <= 20">
          <a-select-option v-for="item in seatColsYD" :key="item.key" :value="item.value">
            {{ item.value }}
          </a-select-option>
        </a-select>
      </template>

      <template v-else-if="column.dataIndex === 'seatPosition' && record.seatTypeCode === '2'">
        <a-select v-model:value="record.seatPosition" style="width: 100%" :placeholder="seatTypes[1].count <= 20 ? '剩余座位数小于20,不可选座' : '可选择座位'"
                  :disabled="seatTypes[1].count <= 20">
          <a-select-option v-for="item in seatColsED" :key="item.key" :value="item.value">
            {{ item.value }}
          </a-select-option>
        </a-select>
      </template>
    </template>
  </a-table>

  <a-divider></a-divider>

  <div class="GoPay">
    <a-button type="primary" @click="goPay" shape="round">去支付</a-button>
  </div>
</template>
<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";
import router from "@/router";

export default defineComponent({
  name: "order-view",
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    const SEAT_TYPE = window.SEAT_TYPE;
    const SEAT_COL = window.SEAT_COL;
    const seatColsYD = [];
    const seatColsED = [];
    const seatTypes = [];
    const passengers = ref([]);
    const passengerOptions = ref([]);
    const passengerChecked = ref([]);
    const tickets = ref([]);
    const PASSENGER_TYPE_ARRAY = window.PASSENGER_TYPE_ARRAY;
    const pagination = ref({
      position: ['bottomLeft'],
    })
    const columns = [{
      title: '乘客',
      dataIndex: 'passengerName',
    }, {
      title: '身份证',
      dataIndex: 'passengerIdCard',
    }, {
      title: '票种',
      dataIndex: 'passengerType',
    }, {
      title: '座位类型',
      dataIndex: 'seatTypeCode'
    }, {
      title: '座位编号',
      dataIndex: 'seatPosition'
    }];
    for (let KEY in SEAT_TYPE) {
      let key = KEY.toLowerCase();
      if (dailyTrainTicket[key] >= 0) {
        seatTypes.push({
          type: KEY,
          key: SEAT_TYPE[KEY]["key"],
          value: SEAT_TYPE[KEY]["value"],
          count: dailyTrainTicket[key],
          price: dailyTrainTicket[key + 'Price'],
        })
      }
    }
    console.log(seatTypes);

    for (let KEY in SEAT_COL) {
      if (SEAT_COL[KEY] ["type"] === "1") {
        seatColsYD.push({
          num: SEAT_COL[KEY]["num"],
          type: KEY,
          key: SEAT_COL[KEY]["key"],
          value: SEAT_COL[KEY]["value"],
          ticketType: SEAT_COL[KEY]["type"]
        })
      }

      if (SEAT_COL[KEY] ["type"] === "2") {
        seatColsED.push({
          num: SEAT_COL[KEY]["num"],
          type: KEY,
          key: SEAT_COL[KEY]["key"],
          value: SEAT_COL[KEY]["value"],
          ticketType: SEAT_COL[KEY]["type"]
        })
      }

    }


    const handleQueryPassengers = () => {
      axios.get("/member/passenger/query-my-passenger").then((response) => {
        let data = response.data;
        if (data.success) {
          passengers.value = data.content;
          passengers.value.forEach((item) => passengerOptions.value.push({
            label: item.name,
            value: item
          }))
        } else {
          notification.error({description: data.message});
        }
      })
    }

    const goPay = () => {

      axios.post("/business/confirm-order/save-order", {
        dailyTrainTicketId: dailyTrainTicket.id,
        date: dailyTrainTicket.date,
        trainCode: dailyTrainTicket.trainCode,
        start: dailyTrainTicket.start,
        end: dailyTrainTicket.end,
        tickets: tickets.value,
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "下单成功！"});
        } else {
          notification.error({description: data.message});
        }
      });
      let seatTypeTmp = Tool.copy(seatTypes);
      if (tickets.value.length === 0) {
        notification.error({description: '请选择乘客！'});
        return;
      }
      for (let i = 0; i < tickets.value.length; i++) {
        let ticket = tickets.value[i];
        for (let j = 0; j < seatTypeTmp.length; j++) {
          if (ticket.seatTypeCode === seatTypeTmp[j].key) {
            seatTypeTmp[j].count--;
            if (seatTypeTmp[j].count < 0) {
              notification.error({description: seatTypeTmp[j].value + '仅剩' + seatTypes[j].count + '张票' + '，票数不足！'});
              return;
            }
          }
        }
      }

      SessionStorage.set(SESSION_CONFIRM_SEAT_TYPES, seatTypes);
      SessionStorage.set(SESSION_CONFIRM_COLUMNS, columns);
      SessionStorage.set(SESSION_CONFIRM_TICKETS, tickets);
      router.push("/orderConfirm");
    }

    onMounted(() => {
      handleQueryPassengers();
    })
    watch(() => passengerChecked.value, () => {
      tickets.value = [];
      passengerChecked.value.forEach((item) => {
        tickets.value.push({
          passengerId: item.id,
          passengerType: item.type,
          seatTypeCode: seatTypes[0].key,
          passengerName: item.name,
          passengerIdCard: item.idCard,
          seatPosition: null,
        })
      })

      console.log(tickets);


    }, {immediate: true});

    return {
      dailyTrainTicket,
      seatTypes,
      handleQueryPassengers,
      passengers,
      passengerOptions,
      passengerChecked,
      tickets,
      PASSENGER_TYPE_ARRAY,
      columns,
      goPay,
      pagination,
      SEAT_COL,
      seatColsYD,
      seatColsED
    }
  }
});
</script>
<style scoped>
.main {
  display: flex;
}

.order-train {
  display: flex;
}

.order-train-ticket {
  display: flex;
}

.order-train-ticket .order-train-ticket-main {
  font-size: 25px;
  font-weight: bold;
  color: chartreuse;
}

.order-train .order-train-main {
  font-size: 40px;
  font-weight: bold;
  color: #333;
}

.passengerChose {
  display: flex;
  font-weight: 500;
  font-size: 30px;
}

.passengers {
  display: flex;
  justify-content: space-between;
}

.GoPay {
  display: flex;
  justify-content: flex-end;
}


</style>