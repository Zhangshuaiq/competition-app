import Vue from 'vue';
import Router from 'vue-router';
import ContestantIndex from '../components/ContestantIndex.vue';
import WaitStart from "@/components/WaitStart";
import Model7 from "@/components/Model7";
import Model60 from "@/components/Model60";
import Model61 from "@/components/Model61";
import StageList from "@/components/StageList";
import StageName from "@/components/StageName";
import StageQuestion from "@/components/StageQuestion";
import Question from "@/components/Question";
import Model3 from "@/components/Model3";
import Model61R from "@/components/Model61R.vue";
import Roll from "@/components/Roll";
import ClueQuestion from "@/components/ClueQuestion";
import Rule from "@/components/Rule";
Vue.use(Router);

export default new Router({
    mode: 'hash', // 使用HTML5 History模式
    routes: [
        {
            path: '/',
            name: 'contestant',
            component: ContestantIndex,
        },

        {
            path: '/wait',
            name: 'wait',
            component: WaitStart,
        },

        {
            path: '/model7',
            name: 'model7',
            component: Model7,
        },

        {
            path: '/model60',
            name: 'model60',
            component: Model60,
        },

        {
            path: '/model61',
            name: 'model61',
            component: Model61,
        },

        {
            path: '/model61R',
            name: 'model61R',
            component: Model61R,
        },

        {
            path: '/stages',
            name: 'stages',
            component: StageList,
        },

        {
            path: '/stageName',
            name: 'stageName',
            component: StageName,
        },

        {
            path: '/stageQuestion',
            name: 'stageQuestion',
            component: StageQuestion,
        },


        {
            path: '/question',
            name: 'question',
            component: Question,
        },

        {
            path: '/model3',
            name: 'model3',
            component: Model3
        },

        {
            path: '/rule',
            name: 'rule',
            component: Rule
        },

        {
            path: '/roll',
            name: 'roll',
            component: Roll
        },

        {
            path: '/clue',
            name: 'clue',
            component: ClueQuestion
        }
    ],
});
