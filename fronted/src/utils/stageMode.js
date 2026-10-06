export const FREE_ANSWER_TYPE = 9

export function isFreeAnswerMode(stage) {
  return !!stage && stage.questionType === FREE_ANSWER_TYPE
}
