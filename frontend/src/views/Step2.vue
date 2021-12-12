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

    <div v-if="isCameraOpen && !isLoading" class="settings-box">
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
          <option>disgust</option>
          <option>fear</option>
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
    <br>
    <small v-if="!intervalsValid">Timespan of capturing is required to be above interval</small>
    <div v-if="isCameraOpen && !isLoading" class="camera-shoot">
      <button type="button" tip="Start capturing" class="button tip"
              :class="{pointed: !isCapturing&&intervalsValid, highlited: !isCapturing&&intervalsValid  }"
              :disabled="isCapturing||!intervalsValid"
              @click="startCapturing">
        <img class="tip" :class="{pointed: !isCapturing&&intervalsValid }" src="https://img.icons8.com/ios/50/000000/anonymous-mask.png"/>
      </button>
    </div>
    <timer v-if="isCameraOpen && !isLoading" v-model="paddedCurrentTime"></timer>

    <div class="time-is-over-box" v-if="isFinished"><h1>Time is over.</h1></div>

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

    startCapturing() {
      this.isCapturing = true
      const _init = this.initTimer
      const _stop = this.stopTimer

      const _callback = this.postNewImg
      const y = this.vidLen
      const x = this.interval


      setTimeout(function() {
        _init()
        const photoCount = parseInt(y / x)
        var timesRun = 0
        var intervalFn = setInterval(function(){
          console.log(timesRun)
          _callback(photoCount)
          timesRun += 1
          if(timesRun === photoCount){
            _stop()
            clearInterval(intervalFn)
          }
        }, 500);

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
