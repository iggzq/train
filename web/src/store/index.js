import {createStore} from 'vuex'

const MEMBER = "MEMBER";
export default createStore({
    state: {
        member: JSON.parse(window.sessionStorage.getItem(MEMBER)) || {}
    },
    getters: {},
    mutations: {
        setMember(state, member) {
            state.member = member;
            window.sessionStorage.setItem(MEMBER,JSON.parse(member));
        }
    },
    actions: {},
    modules: {}
})
