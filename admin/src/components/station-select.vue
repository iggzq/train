<template>
  <a-select v-model:value="name" show-search allow-clear
            :filter-option="filterNameOption"
            @change="onChange" placeholder="请选择车站">
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name"
                     :label="item.name + item.namePinyin + item.namePy">
      {{ item.name }} | {{ item.namePinyin }} ~ {{ item.namePy }}
    </a-select-option>
  </a-select>
</template>
<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "station-select-view",
  props: ["modelValue"],
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const name = ref();
    const stations = ref([]);

    watch(() => props.modelValue, () => {
      console.log("props.modelValue", props.modelValue);
      name.value = props.modelValue;
    }, {immediate: true});

    const queryName = () => {
      axios.get("/business/admin/station/query-all").then((resp) => {
        let data = resp.data;
        if (data.success) {
          stations.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      })
    };

    const filterNameOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    const onChange = (value) => {
      emit('update:modelValue', value);
      let station = stations.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(station)) {
        station = {};
      }
      emit('change', station)
    };
    onMounted(() => {
      queryName();
    });

    return {
      name,
      stations,
      filterNameOption,
      onChange,

    }

  }

})
</script>