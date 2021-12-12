<template>
    <div class="time-box">
      <a id="min">{{ value.min }}</a>:
      <a id="sec">{{ value.sec }}</a>:
      <a id="mill">{{ value.mill }}</a>
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
.time-box {
  font: small-caps lighter 43px/150% "Segoe UI", Frutiger, "Frutiger Linotype", "Dejavu Sans", "Helvetica Neue", Arial, sans-serif;
  text-align: left;
  width: 50%;
  margin: 40px auto;
  border-left: 5px solid #2fab95;
  padding: 20px;
}
</style>
