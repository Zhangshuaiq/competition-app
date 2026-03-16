import {get, post} from "@/request";


export const queryTeams = () => get('/rest/teams')

export const queryQuestion = (id) => get(`/rest/question/${id}`)

export const heartbeat = () => get('/rest/heartbeat')

export const answerPrefix = () => get('/rest/answerPrefix')

export const holderPass = () => get('/rest/holderPass')

export const scoreStyle = () => get('/rest/scoreConfig')

export const queryScore = (id) => get(`/rest/score/${id}`)

export const saveStatus = (data) => post('/rest/saveStatus', data)

export const queryStatus = (teamId) => get(`/rest/status/${teamId}`)

export const queryContact = () => get('/rest/contact')

export const buzzQuestion = (data) => post(`/rest/quzz`, data)

export const queryModel7Questions = () => get('/rest/currentStageQuestions')

export const rankList = () => get('/rest/ranklist')

export const rankTitle = () => get('/rest/rankTitle')

export const memberRankList = () => get('/rest/memberRanklist')

export const memberTitle = () => get('/rest/memberRankTitle')

export const model6BoardStyle = () => get('/rest/model6BoardStyle')

export const model6BoardData = () => get('/rest/model6BoardData')