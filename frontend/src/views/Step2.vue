<template>
  <div class="content-container step">
    <h1 class="title is-2">Step 2. Capture video with designated emotions</h1>
    <div class="camera-button">
      <button type="button" class="button is-rounded" :class="{ 'is-primary' : !isCameraOpen, 'is-danger' : isCameraOpen}" @click="toggleCamera">
        <span v-if="!isCameraOpen">Open Camera</span>
        <span v-else>Close Camera</span>
      </button>
    </div>

    <div v-show="isCameraOpen && isLoading" class="camera-loading">
      <ul class="loader-circle">
        <li></li>
        <li></li>
        <li></li>
      </ul>
    </div>

    <div v-if="isCameraOpen" v-show="!isLoading" class="camera-box" :class="{ 'flash' : isShotPhoto }">

      <div class="camera-shutter" :class="{'flash' : isShotPhoto}"></div>

      <video v-show="!isPhotoTaken" ref="camera" :width="450" :height="337.5" autoplay></video>

      <canvas v-show="isPhotoTaken" id="photoTaken" ref="canvas" :width="450" :height="337.5"></canvas>
    </div>

    <div v-if="isCameraOpen && !isLoading" class="columns is-8 is-multiline is-mobile" style="margin-top: 15px; width: 100%">
      <div class="column is-half">
        <h4 class="title is-4">Settings</h4>

        <div class="task-box">
          <div class="row">
            <h3>Task: {{ curTask }}</h3>
          </div>

          <select v-model="curTask" :disabled="isCapturing">
            <option disabled value="">Please select one</option>
            <option>neutral</option>
            <option>happiness</option>
            <option>surprise</option>
            <option>sadness</option>
            <option>anger</option>
          </select>
        </div>

        <div class="slider-box" >
          <div class="row">
            <h3>Time interval: {{ interval }} ms</h3>
          </div>
          <vue-slider
              :disabled="isCapturing" style="width: 100%"
              ref="slider"
              v-model="interval"
              v-bind="options"
          ></vue-slider>
        </div>

        <div class="slider-box" >
          <div class="row">
            <h3>Length of video capture: {{ vidLen }} ms</h3>
          </div>
          <vue-slider
              :disabled="isCapturing" style="width: 100%"
              ref="slider"
              v-model="vidLen"
              v-bind="options"
          ></vue-slider>
        </div>

      </div>

      <div class="column is-half">
        <h4 class="title is-4">Timer</h4>
        <timer v-model="paddedCurrentTime"></timer>
      </div>
    </div>

    <small v-if="!intervalsValid">Timespan of capturing is required to be above interval</small>
    <div v-if="isCameraOpen && !isLoading" class="camera-shoot">
      <button type="button" tip="Start capturing" class="button tip"
              :class="{pointed: !isCapturing&&intervalsValid, highlited: !isCapturing&&intervalsValid  }"
              :disabled="isCapturing||!intervalsValid"
              @click="startCapturing">
        <img class="tip" :class="{pointed: !isCapturing&&intervalsValid }" src="https://img.icons8.com/ios/50/000000/anonymous-mask.png"/>
      </button>
    </div>


    <div class="time-is-over-box" v-if="isFinished">
      <h1>Time is over.</h1>
      <h3 v-if="verified==='Yes'">You are verified with you registration photo</h3>
      <h3 v-else>You are NOT verified with you registration photo</h3>

      <div>Similarity with etalon: {{mean_dist}} </div>
      <div>The task was: {{curTask}} </div>
      <div>Server recognised: {{emo}} </div>

      <br>
      <h2 v-if="(verified==='Yes')&&(emo==curTask)">
        YOU ARE LEGIT!
        <img style="margin-left: 10px" alt="svgImg" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHg9IjBweCIgeT0iMHB4Igp3aWR0aD0iNTAiIGhlaWdodD0iNTAiCnZpZXdCb3g9IjAgMCAxNzIgMTcyIgpzdHlsZT0iIGZpbGw6IzAwMDAwMDsiPjxnIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0ibm9uemVybyIgc3Ryb2tlPSJub25lIiBzdHJva2Utd2lkdGg9IjEiIHN0cm9rZS1saW5lY2FwPSJidXR0IiBzdHJva2UtbGluZWpvaW49Im1pdGVyIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IiIgc3Ryb2tlLWRhc2hvZmZzZXQ9IjAiIGZvbnQtZmFtaWx5PSJub25lIiBmb250LXdlaWdodD0ibm9uZSIgZm9udC1zaXplPSJub25lIiB0ZXh0LWFuY2hvcj0ibm9uZSIgc3R5bGU9Im1peC1ibGVuZC1tb2RlOiBub3JtYWwiPjxwYXRoIGQ9Ik0wLDE3MnYtMTcyaDE3MnYxNzJ6IiBmaWxsPSJub25lIj48L3BhdGg+PGcgZmlsbD0iIzAwZDFiMiI+PHBhdGggZD0iTTg2LDYuODhjLTQzLjYyNjA4LDAgLTc5LjEyLDM1LjQ5MzkyIC03OS4xMiw3OS4xMmMwLDQzLjYyOTUyIDM1LjQ5MzkyLDc5LjEyIDc5LjEyLDc5LjEyYzQzLjYyOTUyLDAgNzkuMTIsLTM1LjQ5MDQ4IDc5LjEyLC03OS4xMmMwLC00My42MjYwOCAtMzUuNDkwNDgsLTc5LjEyIC03OS4xMiwtNzkuMTJ6TTEyMy4yNDQ4OCw1Ni45NzMyOGwtMzkuNTk3ODQsNTguMzUyNzJsLTMwLjk0OTY4LC0yOC43MjA1NmMtMS4zOTMyLC0xLjI5IC0xLjQ3NTc2LC0zLjQ2NzUyIC0wLjE4MjMyLC00Ljg2MDcyYzEuMjksLTEuMzk2NjQgMy40NzA5NiwtMS40NzIzMiA0Ljg2MDcyLC0wLjE4MjMybDI1LjA3NzYsMjMuMjY4MTZsMzUuMDk4MzIsLTUxLjcyMzg0YzEuMDY5ODQsLTEuNTcyMDggMy4yMDk1MiwtMS45NzggNC43NzgxNiwtMC45MTUwNGMxLjU3NTUyLDEuMDY2NCAxLjk4NDg4LDMuMjA2MDggMC45MTUwNCw0Ljc4MTZ6Ij48L3BhdGg+PC9nPjwvZz48L3N2Zz4="/>
      </h2>
      <h2 v-else>
        You are NOT totally legit.
        <img style="margin-left: 10px" alt="svgImg" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHg9IjBweCIgeT0iMHB4Igp3aWR0aD0iNTAiIGhlaWdodD0iNTAiCnZpZXdCb3g9IjAgMCAxNzIgMTcyIgpzdHlsZT0iIGZpbGw6IzAwMDAwMDsiPjxnIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0ibm9uemVybyIgc3Ryb2tlPSJub25lIiBzdHJva2Utd2lkdGg9IjEiIHN0cm9rZS1saW5lY2FwPSJidXR0IiBzdHJva2UtbGluZWpvaW49Im1pdGVyIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IiIgc3Ryb2tlLWRhc2hvZmZzZXQ9IjAiIGZvbnQtZmFtaWx5PSJub25lIiBmb250LXdlaWdodD0ibm9uZSIgZm9udC1zaXplPSJub25lIiB0ZXh0LWFuY2hvcj0ibm9uZSIgc3R5bGU9Im1peC1ibGVuZC1tb2RlOiBub3JtYWwiPjxwYXRoIGQ9Ik0wLDE3MnYtMTcyaDE3MnYxNzJ6IiBmaWxsPSJub25lIj48L3BhdGg+PGcgZmlsbD0iI2YxNDY2OCI+PHBhdGggZD0iTTg2LDYuODhjLTQzLjYyMjY0LDAgLTc5LjEyLDM1LjQ5NzM2IC03OS4xMiw3OS4xMmMwLDQzLjYyMjY0IDM1LjQ5NzM2LDc5LjEyIDc5LjEyLDc5LjEyYzQzLjYyMjY0LDAgNzkuMTIsLTM1LjQ5NzM2IDc5LjEyLC03OS4xMmMwLC00My42MjI2NCAtMzUuNDk3MzYsLTc5LjEyIC03OS4xMiwtNzkuMTJ6TTExNS45NjI0LDExMS4wNzc2YzEuMzQxNiwxLjM0MTYgMS4zNDE2LDMuNTQzMiAwLDQuODg0OGMtMC42ODgsMC42NTM2IC0xLjU0OCwwLjk5NzYgLTIuNDQyNCwwLjk5NzZjLTAuODk0NCwwIC0xLjc1NDQsLTAuMzQ0IC0yLjQ0MjQsLTAuOTk3NmwtMjUuMDc3NiwtMjUuMDc3NmwtMjUuMDc3NiwyNS4wNzc2Yy0wLjY4OCwwLjY1MzYgLTEuNTQ4LDAuOTk3NiAtMi40NDI0LDAuOTk3NmMtMC44OTQ0LDAgLTEuNzU0NCwtMC4zNDQgLTIuNDQyNCwtMC45OTc2Yy0xLjM0MTYsLTEuMzQxNiAtMS4zNDE2LC0zLjU0MzIgMCwtNC44ODQ4bDI1LjA3NzYsLTI1LjA3NzZsLTI1LjA3NzYsLTI1LjA3NzZjLTEuMzQxNiwtMS4zNDE2IC0xLjM0MTYsLTMuNTQzMiAwLC00Ljg4NDhjMS4zNDE2LC0xLjM0MTYgMy41NDMyLC0xLjM0MTYgNC44ODQ4LDBsMjUuMDc3NiwyNS4wNzc2bDI1LjA3NzYsLTI1LjA3NzZjMS4zNDE2LC0xLjM0MTYgMy41NDMyLC0xLjM0MTYgNC44ODQ4LDBjMS4zNDE2LDEuMzQxNiAxLjM0MTYsMy41NDMyIDAsNC44ODQ4bC0yNS4wNzc2LDI1LjA3NzZ6Ij48L3BhdGg+PC9nPjwvZz48L3N2Zz4="/>
      </h2>
      <br>

      <div>Number of photos where face was recognised: {{num_emo_faces}} </div>
      <div>Similarity between captured photos: {{mean_sim_dist}} </div>
      <div>Преобразование полученных данных в секундах: {{open_images_time}}</div>
      <div>Распознавание лица в секундах: {{face_recognition_time}}</div>
      <div>Распознавание эмоции в секундах: {{emotion_recognition_time}}</div>
    </div>

  </div>

</template>

<script>

import { useTimer } from '@/hooks/useTimer'
import axios from 'axios'
export default {
  setup () {
    const {   currentTime,
      paddedCurrentTime,
      initTimer,
      stopTimer,
      isCapturing,
      isFinished
    } = useTimer()

    return { currentTime,
      paddedCurrentTime,
      initTimer,
      stopTimer,
      isCapturing,
      isFinished }
  },
  data() {
    return {
      // response with final result ===
      verified: 'NaN',
      mean_dist: 0,
      emo: "NaN",
      num_emo_faces: 0,
      mean_sim_dist: 0,
      open_images_time:0,
      face_recognition_time:0,
      emotion_recognition_time:0,
      // response with final result end ===
      isCameraOpen: false,
      isPhotoTaken: false,
      isShotPhoto: false,
      isLoading: false,
      link: '#',
      timeout: 1000,
      // that is what we alter on the front for ml testing:
      interval: 500,
      vidLen: 2000,
      curTask: "neutral",

      options: {
        min: 500,
        max: 4000,
        height: 4,
      },
    }
  },
  computed: {
    intervalsValid: function () {
      // `this` points to the vm instance
      return this.interval<=this.vidLen
    }
  },
  methods: {

    toggleCamera() {
      if(this.isCameraOpen) {
        this.isCameraOpen = false;
        this.isPhotoTaken = false;
        this.isShotPhoto = false;
        this.stopCameraStream();
      } else {
        this.isCameraOpen = true;
        this.createCameraElement();
      }
    },

    createCameraElement() {
      this.isLoading = true;

      const constraints = (window.constraints = {
        audio: false,
        video: true
      });


      navigator.mediaDevices
          .getUserMedia(constraints)
          .then(stream => {
            this.isLoading = false;
            this.$refs.camera.srcObject = stream;
          })
          .catch(e => {
            this.isLoading = false;
            alert("May the browser didn't support or there is some errors: " + e.message);
          });
    },

    stopCameraStream() {
      let tracks = this.$refs.camera.srcObject.getTracks();

      tracks.forEach(track => {
        track.stop();
      });
    },

    takePhoto() {
      if(!this.isPhotoTaken) {
        this.isShotPhoto = true;

        const FLASH_TIMEOUT = 50;

        setTimeout(() => {
          this.isShotPhoto = false;
        }, FLASH_TIMEOUT);
      }

      this.isPhotoTaken = !this.isPhotoTaken;

      const context = this.$refs.canvas.getContext('2d');
      context.drawImage(this.$refs.camera, 0, 0, 450, 337.5);
    },

    downloadImage() {
      return document.getElementById("photoTaken").toDataURL("image/jpeg")
    },

    async postNewImg(cnt){
      this.takePhoto()
      const newImageString = this.downloadImage()
      try {
        const response = await axios.post('http://localhost:8080/receivePhoto',
            {
              imageString: newImageString,
              task: this.curTask,
              photoCount: cnt
            })
        console.log(response)
        return response
      } catch (e) {
        console.log(e)
        return e
      }
    },

    async startCapturing() {
      //this.isCapturing = true
      const context = this
      /*const _init = this.initTimer
      const _stop = this.stopTimer
      const _callback = this.postNewImg
      const y = this.vidLen
      const x = this.interval*/

      setTimeout(async function() {
        //_init()
        context.initTimer()
        const photoCount = Math.floor(context.vidLen / context.interval)
        console.log('the number of photos we\'ll take: ', photoCount)
        let timesRun = 0

        // 1 start ==============
        let intervalFn = setInterval( /*async */function(){
          console.log(timesRun)
          timesRun += 1
          if(timesRun >= photoCount){
            clearInterval(intervalFn)
          }
          try {
            //_callback(photoCount)
            /*await */context.postNewImg(photoCount)
          } catch (e) {
            console.log(e)
          }
        }, context.interval)
        // 1 end ==============

        // 2 start ==============
        /*const intervalPosts = async () => {
          console.log(timesRun)
          timesRun += 1
          await context.postNewImg(photoCount)
          if(timesRun >= photoCount){
            return
          }
          setTimeout(intervalPosts,context.interval)
        }
        intervalPosts()*/
        // 2 end ================

        // 3 start ==============
        /*async function delay(ms) {
          // return await for better async stack trace support in case of errors.
          return await new Promise(resolve => setTimeout(resolve, ms));
        }
        let run = async () => {
          for (let i = 0; i < photoCount; i++) {
            console.log(timesRun)
            timesRun += 1
            await context.postNewImg(photoCount)
            await delay(context.interval)
          }
        }
        run()*/
        // 3 end ================

        console.log('hm...timesRun = ', timesRun)
        //while(timesRun<photoCount);
        let response = null
        try {
          console.log('waiting status...')
          //while(timesRun < photoCount - 1);
          response = await axios.get('http://localhost:8080/status')
          //_stop()
          context.stopTimer()
          console.log(response)
          /*context.verified = response.data.verified
          context.mean_dist = response.data.mean_dist
          context.emo = response.data.emo
          context.num_emo_faces = response.data.num_emo_faces
          context.mean_sim_dist = response.data.mean_sim_dist
          context.open_images_time = response.data.open_images_time
          context.face_recognition_time = response.data.face_recognition_time
          context.emotion_recognition_time = response.data.emotion_recognition_time*/
          for(const key of Object.keys(response.data)){
            context[key] = response.data[key]
          }

        } catch (e) {
          console.log(e)
        }
      }, this.timeout);
    },

  }

}
</script>

<style lang="scss">
.settings-box{
  margin-top: 20px;
}
.slider-box{
  margin-top: 20px;
}
.content-container {
  margin-top: 2rem;
  margin-bottom: 2rem;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 4px;
  min-width: 500px;

  .slider-box{
    width: 100%;
  }

  .camera-button {
    margin-bottom: 2rem;
  }

  .camera-box {
    .camera-shutter {
      opacity: 0;
      width: 450px;
      height: 337.5px;
      background-color: #fff;
      position: absolute;

      &.flash {
        opacity: 1;
      }
    }
  }

  .camera-shoot {
    margin: 1rem 0;

    button {
      height: 60px;
      width: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 100%;

      img {
        height: 35px;
        object-fit: cover;
      }
    }
  }

  .camera-loading {
    overflow: hidden;
    height: 100%;
    position: absolute;
    width: 100%;
    min-height: 150px;
    margin: 3rem 0 0 -1.2rem;

    ul {
      height: 100%;
      position: absolute;
      width: 100%;
      z-index: 999999;
      margin: 0;
    }

    .loader-circle {
      display: block;
      height: 14px;
      margin: 0 auto;
      top: 70%;
      left: 100%;
      transform: translateY(-50%);
      transform: translateX(-50%);
      position: absolute;
      width: 100%;
      padding: 0;

      li {
        display: block;
        float: left;
        width: 10px;
        height: 10px;
        line-height: 10px;
        padding: 0;
        position: relative;
        margin: 0 0 0 4px;
        background: #999;
        animation: preload 1s infinite;
        top: -50%;
        border-radius: 100%;

        &:nth-child(2) {
          animation-delay: .2s;
        }

        &:nth-child(3) {
          animation-delay: .4s;
        }
      }
    }
  }

  @keyframes preload {
    0% {
      opacity: 1
    }
    50% {
      opacity: .4
    }
    100% {
      opacity: 1
    }
  }
}
</style>
