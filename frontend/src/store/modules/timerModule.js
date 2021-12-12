import {toTime, toTimestamp, pad} from '@/helpers/timeFns'

export const timerModule = {
    state: () => ({
        isRunning: true,
        startTime: null,
        currentTime: {
            min:'00',
            sec:'00',
            mill:'000'
        },
        intervalFn: null
    }),
    getters: {
        getCurrentTime: (state) => state.currentTime,
        getIsRunning: (state) => state.isRunning,
    },
    actions: {
        initClock({ commit }) {
            commit("setStartTime", new Date())
            commit("toggleIsRunning", true)
            commit("updateClock")
            console.log('HOSDAOFGKASPDOFKLASPDOFKLAPSDF')
            window.setInterval("commit(\"updateClock\")", 1);
        },
    },
    mutations: {
        toggleIsRunning(state, newVal) {
            state.isRunning = newVal
        },
        setStartTime (state, newTime){
            state.startTime = newTime
        },
        updateClock(state) {
            /*state.intervalFn = () => {
                var nowDate = new Date()
                var now = toTimestamp(nowDate)
                var mill = nowDate.getMilliseconds(),
                    sec = toTime(now - state.startTime).getSeconds(),
                    min = toTime(now - state.startTime).getMinutes();

                /!*state.currentTime.min = this.pad(min, 2)
                state.currentTime.sec = this.pad(sec, 2)
                state.currentTime.mill = this.pad(mill, 2)*!/
                state.currentTime = {
                    min: pad(min, 2),
                    sec: pad(sec, 2),
                    mill: mill
                }
            }*/

            state.intervalFn = setInterval(() => {
                var nowDate = new Date()
                var now = toTimestamp(nowDate)
                var mill = nowDate.getMilliseconds(),
                    sec = toTime(now - state.startTime).getSeconds(),
                    min = toTime(now - state.startTime).getMinutes();

                /*state.currentTime.min = this.pad(min, 2)
                state.currentTime.sec = this.pad(sec, 2)
                state.currentTime.mill = this.pad(mill, 2)*/
                state.currentTime = {
                    min: pad(min, 2),
                    sec: pad(sec, 2),
                    mill: mill
                }
            }, 1);
        },
        stopClock(state) {
            clearInterval(state.intervalFn)
            state.isRunning = false
        }
    },
    namespaced: true
}
