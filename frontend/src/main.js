import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueCompositionAPI from '@vue/composition-api'
import VueSlider from 'vue-slider-component'
import components from '@/components'
import 'vue-slider-component/theme/default.css'
import ImageUploader from "vue-image-upload-resize";

Vue.use(ImageUploader)
Vue.use(VueCompositionAPI)
Vue.component('VueSlider', VueSlider)

Vue.config.productionTip = false

components.forEach(component => {
  Vue.component(component.name, component)
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
