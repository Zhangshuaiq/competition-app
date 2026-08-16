// timerPlugin.js
export default {
    install(app, options) {
        // 定时器ID
        let timerId = null;

        // 定时器执行的任务
        let onTick = options.onTick || (() => {});

        // 启动定时器
        const startTimer = (interval) => {
            if (timerId === null) {
                timerId = setInterval(() => {
                    onTick();
                }, interval);
            }
        };

        const hasStoped = () => {
            return timerId === null
        };

        // 停止定时器
        const stopTimer = () => {
            if (timerId !== null) {
                clearInterval(timerId);
                timerId = null;
            }
        };

        // 设置定时器执行的任务
        const setOnTick = (callback) => {
            onTick = callback;
        };

        // 将方法暴露给所有Vue实例
        app.config.globalProperties.$timer = {
            hasStoped: hasStoped,
            start: startTimer,
            stop: stopTimer,
            setOnTick,
        };
    }
};
