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
</template>
<script>
import {defineComponent} from "vue";

export default defineComponent({
  name: "order-view",
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    const SEAT_TYPE = window.SEAT_TYPE;
    const seatTypes = [];
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
    return {
      dailyTrainTicket,
      seatTypes
    }
  }
});
</script>
<style scoped>
.order-train .order-train-main {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
</style>