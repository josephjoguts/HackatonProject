<template>
  <div id="app">
    <router-view/>
  </div>


</template>

<script>

//import axios from 'axios'
export default {


}
</script>

<style lang="scss">
@import "https://cdn.jsdelivr.net/npm/bulma@0.9.3/css/bulma.min.css";
#app {
  font-family: Inter, Roboto, Oxygen, Fira Sans, Helvetica Neue, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
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
}

.step{
  padding: 20px;
}
.tip:hover::after {
  content: attr(tip);
  position: absolute;
  left: 120%; top: 100%;
  z-index: 1;
  background: rgba(120, 210, 182, 0.48);
  font-family: Arial, sans-serif;
  font-size: 16px;
  padding: 5px 10px; /* Поля */

  border-radius: 4px;
}
small {
  color: #e53935;
  font-size: 16px;
}
.row {
  display: flex;
  flex-direction: row;
  width: 100%;
  justify-content: center;
  align-items: center;
}
body {
  display: flex;
  justify-content: center;
  align-items: center;
}


  .highlited:hover {
    box-shadow:
        0 0 7px #308677,
        0 0 9px #308677,
        0 0 11px #6cccaa,
        0 0 13px #0fa;
    animation-duration: 1s;
  }
  .pointed:hover {
    cursor: pointer;
    transform: scale(1.01);
    animation-duration: 1s;
  }

.pointed-no-scale:hover {
  cursor: pointer;
  animation-duration: 1s;
}

</style>
