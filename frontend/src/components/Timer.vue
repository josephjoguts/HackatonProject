<template>
<div class="row">
  <div class="time-box">
    {{ value.min }}:{{ value.sec }}:{{ value.mill }}
  </div>

</div>


</template>

<script>
export default {
  name: "Timer",
  props: {
    value: {
      type:Object,
      required: true
    }
  }
}
</script>

<style scoped lang="scss">
.row{
  position: relative;
}
.time-box {
  font: small-caps lighter 43px/150% "Segoe UI", Frutiger, "Frutiger Linotype", "Dejavu Sans", "Helvetica Neue", Arial, sans-serif;
  text-align: left;
  //width: 50%;
  position: absolute;
  left: 30%;
  /*margin-left: auto;
  margin-right: auto;*/
  border-left: 5px solid #00d1b2;
  padding: 20px;
}

</style>
