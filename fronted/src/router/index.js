import Vue from 'vue';
import Router from 'vue-router';

import SContestantIndex from '../components/score/ContestantIndex.vue';

import BContestantIndex from '../components/buzz/ContestantIndex.vue';
import BuzzPage from '../components/buzz/BuzzPage.vue';

import ContestantIndex from '../components/member/ContestantIndex.vue';
import WaitStart from "@/components/member/WaitStart.vue";
import Model7 from "@/components/member/Model7.vue";
import Model60 from "@/components/member/Model60.vue";
import Model61 from "@/components/member/Model61.vue";
import StageList from "@/components/member/StageList.vue";
import StageName from "@/components/member/StageName.vue";
import StageQuestion from "@/components/member/StageQuestion.vue";
import Question from "@/components/member/Question.vue";
import Model3 from "@/components/member/Model3.vue";
import Model61R from "@/components/member/Model61R.vue";
import Roll from "@/components/member/Roll.vue";
import ClueQuestion from "@/components/member/ClueQuestion.vue";
import Rule from "@/components/member/Rule.vue";
import StageRank from "@/components/member/StageRank.vue";
import TeamRank from "@/components/member/TeamRank.vue";
import BuzzRank from "@/components/member/BuzzRank.vue";
import MemberRank from "@/components/member/MemberRank.vue";
import StageRule from "@/components/member/StageRule.vue";

import HStageRule from "@/components/holder/StageRule.vue";
import HWaitStart from "@/components/holder/WaitStart";
import HModel7 from "@/components/holder/Model7";
import HModel60 from "@/components/holder/Model60";
import HModel61 from "@/components/holder/Model61";
import HStageList from "@/components/holder/StageList";
import HStageName from "@/components/holder/StageName";
import HStageQuestion from "@/components/holder/StageQuestion";
import HQuestion from "@/components/holder/Question";
import HModel3 from "@/components/holder/Model3";
import HModel61R from "@/components/holder/Model61R.vue";
import HRoll from "@/components/holder/Roll";
import HClueQuestion from "@/components/holder/ClueQuestion";
import HRule from "@/components/holder/Rule";
import HStageRank from "@/components/holder/StageRank";
import HTeamRank from "@/components/holder/TeamRank";
import HBuzzRank from "@/components/holder/BuzzRank.vue";
import HMemberRank from "@/components/holder/MemberRank";
import TeamScore from "@/components/score/TeamScore";
import AllScore from "@/components/score/AllScore";
Vue.use(Router);

export default new Router({
    mode: 'hash', // 使用HTML5 History模式
    routes: [
         // 选手路由
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
            path: '/stagerule',
            name: 'stagerule',
            component: StageRule
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
        },

        {
            path: '/stageRank',
            name: 'stageRank',
            component: StageRank
        },
        {
            path: '/teamRank',
            name: 'teamRank',
            component: TeamRank
        },

        {
            path: '/buzzRank',
            name: 'buzzRank',
            component: BuzzRank
        },

        {
            path: '/memberRank',
            name: 'memberRank',
            component: MemberRank
        },
        
        // 主持人，评委路由

        {
            path: '/hwait',
            name: 'hwait',
            component: HWaitStart,
        },

        {
            path: '/hmodel7',
            name: 'hmodel7',
            component: HModel7,
        },

        {
            path: '/hmodel60',
            name: 'hmodel60',
            component: HModel60,
        },

        {
            path: '/hmodel61',
            name: 'hmodel61',
            component: HModel61,
        },

        {
            path: '/hmodel61R',
            name: 'hmodel61R',
            component: HModel61R,
        },

        {
            path: '/hstages',
            name: 'hstages',
            component: HStageList,
        },

        {
            path: '/hstageName',
            name: 'hstageName',
            component: HStageName,
        },

        {
            path: '/hstageQuestion',
            name: 'hstageQuestion',
            component: HStageQuestion,
        },


        {
            path: '/hquestion',
            name: 'hquestion',
            component: HQuestion,
        },

        {
            path: '/hmodel3',
            name: 'hmodel3',
            component: HModel3
        },

        {
            path: '/hrule',
            name: 'hrule',
            component: HRule
        },
        {
            path: '/hstagerule',
            name: 'hstagerule',
            component: HStageRule
        },
        {
            path: '/hroll',
            name: 'hroll',
            component: HRoll
        },

        {
            path: '/hclue',
            name: 'hclue',
            component: HClueQuestion
        },

        {
            path: '/hstageRank',
            name: 'hstageRank',
            component: HStageRank
        },
        {
            path: '/hteamRank',
            name: 'hteamRank',
            component: HTeamRank
        },

        {
            path: '/hbuzzRank',
            name: 'hbuzzRank',
            component: HBuzzRank
        },

        {
            path: '/hmemberRank',
            name: 'hmemberRank',
            component: HMemberRank
        },
        
        // 个人分数端
        {
            path: '/scoreLogin',
            name: 'scoreLogin',
            component: SContestantIndex
        },

        {
            path: '/teamScore',
            name: 'teamScore',
            component: TeamScore
        },

        {
            path: '/allScore',
            name: 'allScore',
            component: AllScore
        },

        // 抢答界面
        {
            path: '/buzzLogin',
            name: 'buzzLogin',
            component: BContestantIndex
        },
        {
            path: '/buzz',
            name: 'buzz',
            component: BuzzPage
        },

    ],
});
