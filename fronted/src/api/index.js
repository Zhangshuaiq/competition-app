import {get} from "@/request";


export const queryTeams = () => get('/rest/teams')

export const queryQuestion = (id) => get(`/rest/question/${id}`)

export const heartbeat = () => get('/rest/heartbeat')

export const answerPrefix = () => get('/rest/answerPrefix')
