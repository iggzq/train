<template>
  <div class="title">订单确认界面</div>
  <div class="order-train">
    <span class="order-train-main">{{ dailyTrainTicket.data }}</span>&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.trainCode }}</span>次&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.start }}</span>站
    <span class="order-train-main">({{ dailyTrainTicket.startTime }})</span>&nbsp;
    <span class="order-train-main">——</span>&nbsp;
    <span class="order-train-main">{{ dailyTrainTicket.end }}</span>站
    <span class="order-train-main">({{ dailyTrainTicket.endTime }})</span>&nbsp;
  </div>
  <a-divider></a-divider>
  <div class="secondTitle">
    乘车人信息
  </div>
  <a-table class="order-tickets" :columns="columns" bordered :data-source="tickets">

    <template #bodyCell="{column,record}">
      <template v-if="column.dataIndex === 'passengerType'">
        {{ PASSENGER_TYPE_ARRAY[record.passengerType - 1].value }}
      </template>
      <template v-else-if="column.dataIndex === 'seatTypeCode'">
        {{ seatTypes[record.seatTypeCode - 1].value }}
      </template>

    </template>
  </a-table>
  <div class="button-line">
    <span class="totalMoney">总价：{{totalMoney}}￥</span>
    <a-button type="primary" @click="goBack" size="large" class="goBackButton">上一步</a-button>
    <a-button type="primary" @click="ensureGoPay" size="large">去支付</a-button>
  </div>


</template>
<script>
import {defineComponent} from "vue";
import router from "@/router";

export default defineComponent({
  name: "order-confirm",
  setup() {

    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    const seatTypes = SessionStorage.get(SESSION_CONFIRM_SEAT_TYPES) || [];
    const columns = SessionStorage.get(SESSION_CONFIRM_COLUMNS) || [];
    const sessionTickets = SessionStorage.get(SESSION_CONFIRM_TICKETS) || [];
    const totalMoney = SessionStorage.get(SESSION_TOTAL_MONEY) || 0;
    const PASSENGER_TYPE_ARRAY = window.PASSENGER_TYPE_ARRAY;
    const tickets = [];
    console.log(1);
    console.log(totalMoney);
    //将sessionTickets数组的内容复制到tickets数组中
    for (let i = 0; i < sessionTickets._rawValue.length; i++) {
      tickets.push(sessionTickets._rawValue[i]);
    }
    console.log(tickets);
    console.log(PASSENGER_TYPE_ARRAY);

    const goBack = () => {
      router.push("/order");
    }

    const ensureGoPay = () => {
    }

    return {
      goBack,
      ensureGoPay,
      dailyTrainTicket,
      seatTypes,
      tickets,
      columns,
      PASSENGER_TYPE_ARRAY,
      totalMoney
    }

  }
});

</script>
<style scoped>
.title {
  display: flex;
  font-size: 50px;
  font-weight: bolder;
  color: red;
}

.order-train {
  display: flex;
}


.order-train .order-train-main {
  font-size: 30px;
  font-weight: bold;
  color: #333;
}

.secondTitle {
  display: flex;
  font-size: 20px;
  font-weight: bolder;
}


.button-line {
  display: flex;
  justify-content: end;
  align-items: center;
}

.button-line .goBackButton {
  margin-right: 20px;
}

.button-line .totalMoney {
  font-size: 20px;
  color: red;
}


</style>