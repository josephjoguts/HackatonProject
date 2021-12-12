import { ref, computed } from '@vue/composition-api'
import {toTime, toTimestamp, pad} from '@/helpers/timeFns'

export function useTimer() {
    const currentTime = ref({
        min:0,
        sec:0,
        mill:0
    })
    //const paddedCurrentTime = ref(null)
    const isCapturing = ref(false)
    const isFinished = ref(false)

    //timestamp
    const startTime = ref(toTimestamp(new Date()))
    const intervalFn = ref(null)

    const updateTimer = () => {
        let nowDate = new Date()
        let now = toTimestamp(nowDate)
        let R_date = toTime(now - startTime.value)

        let mill = nowDate.getMilliseconds()
        let sec = R_date.getSeconds()
        let min = R_date.getMinutes()
        currentTime.value = {
            min: min,
            sec: sec,
            mill: mill
        }

    }
    const paddedCurrentTime = computed(() => {
        return {
            min: pad(currentTime.value.min, 2),
            sec: pad(currentTime.value.sec, 2),
            mill: pad(currentTime.value.mill,3),
        }
    })

    const initTimer = () => {
        currentTime.value = {
            min: 0,
            sec: 0,
            mill: 0
        }
        startTime.value = toTimestamp(new Date())

        //console.log(paddedCurrentTime)

        updateTimer()
        isFinished.value = false
        isCapturing.value = true
        intervalFn.value = window.setInterval(updateTimer, 1)
    }

    const stopTimer = () => {
        clearInterval(intervalFn.value)
        isCapturing.value = false
        isFinished.value = true
    }

    return { currentTime, paddedCurrentTime, initTimer, stopTimer, isCapturing, isFinished }
}
