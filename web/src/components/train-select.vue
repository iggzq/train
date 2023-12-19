<template>
  <a-select v-model:value="trainCode" show-search allow-clear
            :filter-option="filterTrainCodeOption"
            @change="onChange" placeholder="请选择车次">
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code"
                     :label="item.code + item.start + item.end">
      {{ item.code }} | {{ item.start }} ~ {{ item.end }}
    </a-select-option>
  </a-select>
</template>
<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "train-select-view",
  props: ["modelValue"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const trainCode = ref();
    const trains = ref([]);

    watch(() => props.modelValue, () => {
      console.log("props.modelValue", props.modelValue);
      trainCode.value = props.modelValue;
    }, {immediate: true});

    const queryTrainCode = () => {
      axios.get("/business/train/query-all").then((resp) => {
        let data = resp.data;
        if (data.success) {
          trains.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      })
    };

    const filterTrainCodeOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    const onChange = (value) => {
      emit('update:modelValue', value);
      let train = trains.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      emit('change', train)
    };
    onMounted(() => {
      queryTrainCode();
    });

    return {
      trainCode,
      trains,
      filterTrainCodeOption,
      onChange,

    }

  }

})
</script>