<template>
  <div class="title">订单确认界面</div>
  <div class="order-train">
    <div class="trainInfo">
      <span class="order-train-main">{{ dailyTrainTicket.data }}</span>&nbsp;
      <span class="order-train-main">{{ dailyTrainTicket.trainCode }}</span>次&nbsp;
      <span class="order-train-main">{{ dailyTrainTicket.start }}</span>站
      <span class="order-train-main">({{ dailyTrainTicket.startTime }})</span>&nbsp;
      <span class="order-train-main">——</span>&nbsp;
      <span class="order-train-main">{{ dailyTrainTicket.end }}</span>站
      <span class="order-train-main">({{ dailyTrainTicket.endTime }})</span>&nbsp;
    </div>
    <div class="deadlineTime">
      <span> 支付倒计时：{{ deadlineTimeText }}</span>
    </div>
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
    <span class="totalMoney">总价：{{ totalMoney }}￥</span>
    <a-button type="primary" @click="goBack" size="large" class="goBackButton">上一步</a-button>
    <a-button type="primary" @click="ensureGoPay" size="large">去支付</a-button>
  </div>


</template>
<script>
import {computed, defineComponent, onMounted, ref} from "vue";
import router from "@/router";
import axios from "axios";

export default defineComponent({
  name: "order-confirm",
  setup() {

    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    const seatTypes = SessionStorage.get(SESSION_CONFIRM_SEAT_TYPES) || [];
    const columns = SessionStorage.get(SESSION_CONFIRM_COLUMNS) || [];
    const sessionTickets = SessionStorage.get(SESSION_CONFIRM_TICKETS) || [];
    const totalMoney = SessionStorage.get(SESSION_TOTAL_MONEY) || 0;
    const PASSENGER_TYPE_ARRAY = window.PASSENGER_TYPE_ARRAY;
    const deadlineTime = ref(0);
    let countdown = null;
    const tickets = [];
    for (let i = 0; i < sessionTickets._rawValue.length; i++) {
      tickets.push(sessionTickets._rawValue[i]);
    }
    const initCountdown = () => {
      countdown = setInterval(() => {
        if (deadlineTime.value <= 0) {
          clearInterval(countdown);
          deadlineTime.value = 0;
        } else {
          deadlineTime.value -= 1;
        }
      }, 1000);
    };
    // 开始倒计时
    const startCountdown = () => {
      // deadlineTime.value = 15 * 60; // 设置为15分钟
      initCountdown();
    };
    // 组件挂载后开始倒计时
    onMounted(() => {
      getExpireTime();
      startCountdown();
    });
    // 渲染倒计时文本
    const deadlineTimeText = computed(() => {
      let minutes = Math.floor(deadlineTime.value / 60);
      let seconds = deadlineTime.value % 60;
      return `${minutes}分${seconds}秒`;
    });


    const goBack = () => {
      router.push("/order");
    }

    const ensureGoPay = () => {
    }

    const getExpireTime = () => {
      axios.post("/business/confirm-order/get-expire-time",{
        dailyTrainTicketId: dailyTrainTicket.id,
        date: dailyTrainTicket.date,
        trainCode: dailyTrainTicket.trainCode,
        start: dailyTrainTicket.start,
        end: dailyTrainTicket.end,
        tickets: tickets,
      }).then((resp) => {
        deadlineTime.value = resp.data.content;
        console.log(3);
        console.log(deadlineTime.value);
      })
    }


    return {
      goBack,
      ensureGoPay,
      dailyTrainTicket,
      seatTypes,
      tickets,
      columns,
      PASSENGER_TYPE_ARRAY,
      totalMoney,
      deadlineTime,
      deadlineTimeText
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
  justify-content: space-between;
}


.order-train .trainInfo .order-train-main {
  font-size: 30px;
  font-weight: bold;
  color: #333;
}
.order-train .deadlineTime{
  font-size: 30px;
  color: red;
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