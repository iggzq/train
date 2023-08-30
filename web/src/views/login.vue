<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center">
        <rocket-two-tone/>
        12306售票系统
      </h1>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: '请输入手机号' }]"
        >
          <a-input v-model:value="loginForm.mobile"/>
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: '请输入验证码'}]"

        >
          <a-input v-model:value="loginForm.code">
            <template #addonAfter>
              <a @click="sendCode">获取验证码</a>
            </template>
          </a-input>
        </a-form-item>


        <a-form-item>
          <a-button type="primary" block @click="login">提交</a-button>
        </a-form-item>
      </a-form>
    </a-col>
  </a-row>


</template>

<script>
import {defineComponent, reactive} from 'vue';
import {left} from "core-js/internals/array-reduce";
import axios from 'axios';
import {notification} from "ant-design-vue";
import {useRouter} from "vue-router";
import store from "@/store";

export default defineComponent({
  name: "login-view",
  methods: {left},
  setup() {
    const router = useRouter();
    const loginForm = reactive({
      mobile: '18212341234',
      code: '',
    });
    const sendCode = () => {
      axios.post("member/member/send-code", {
        mobile: loginForm.mobile
      }).then(resp => {
        let data = resp.data;
        if (data.success) {
          notification.success({description: '发送验证码成功'});
          loginForm.code = "8888";
        } else {
          notification.error({description: data.message})
        }
      })
    };

    const login = () => {
      axios.post("member/member/login", loginForm)
          .then(resp => {
            let data = resp.data;
            if (data.success) {
              notification.success({description: '登陆成功'});
              //登陆成功跳到主页面
              router.push("/welcome");
              store.commit("setMember",data.content);
              loginForm.code = "8888";
            } else {
              notification.error({description: data.message})
            }
          })
    };
    return {
      loginForm,
      sendCode,
      login,
    };
  },
});
</script>

<style scoped>
.login-main h1 {
  font-size: 25px;
  font-weight: bold;
}

.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}

</style>
