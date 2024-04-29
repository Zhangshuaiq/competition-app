// store.js
import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

export default new Vuex.Store({
    plugins: [createPersistedState()],
    state: {
        stage: {},
        currentRoute: '',
        data: {},  // 存储更多的数据,
        teams: {},
    },
    mutations: {
        setStage(state, stage) {
            state.stage = stage;
        },
        setCurrentRoute(state, route) {
            state.currentRoute = route;
        },
        setAdditionalData(state, data) {
            state.data = data;
        },
        setTeamsData(state, data) {
            state.teams = data
        }
    },
});
