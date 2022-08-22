<template>
	<div class="clock">
    <div class="outer-clock-face">
    	<div class="marking marking-one"></div>
    	<div class="marking marking-two"></div>
    	<div class="marking marking-three"></div>
	  	<div class="marking marking-four"></div>
      	<div class="inner-clock-face">
          <div class="hand hour-hand"></div>
          <div class="hand min-hand"></div>
        	<div class="hand second-hand"></div>
      	</div>
    </div>
    <div>
      <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-skip-backward float-start hover-hand" viewBox="0 0 16 16" @click="this.changeTime(false)">
        <path d="M.5 3.5A.5.5 0 0 1 1 4v3.248l6.267-3.636c.52-.302 1.233.043 1.233.696v2.94l6.267-3.636c.52-.302 1.233.043 1.233.696v7.384c0 .653-.713.998-1.233.696L8.5 8.752v2.94c0 .653-.713.998-1.233.696L1 8.752V12a.5.5 0 0 1-1 0V4a.5.5 0 0 1 .5-.5zm7 1.133L1.696 8 7.5 11.367V4.633zm7.5 0L9.196 8 15 11.367V4.633z"/>
      </svg>


      <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-skip-forward float-end hover-hand" viewBox="0 0 16 16" @click="this.changeTime(true)">
        <path d="M15.5 3.5a.5.5 0 0 1 .5.5v8a.5.5 0 0 1-1 0V8.752l-6.267 3.636c-.52.302-1.233-.043-1.233-.696v-2.94l-6.267 3.636C.713 12.69 0 12.345 0 11.692V4.308c0-.653.713-.998 1.233-.696L7.5 7.248v-2.94c0-.653.713-.998 1.233-.696L15 7.248V4a.5.5 0 0 1 .5-.5zM1 4.633v6.734L6.804 8 1 4.633zm7.5 0v6.734L14.304 8 8.5 4.633z"/>
      </svg>
    </div>
	</div> 
</template>

<script >

	export default {

        data() {
            return {
                hour: 12
            }
        },

        mounted() {
          this.setDate();
        },

        methods: {
          changeTime(direction) {
              (direction)? this.hour++ : this.hour--;
              this.hour = (this.hour + 24) % 24; 
              this.setDate();
          },

          setDate() {

              let now = new Date(2022, 8, 16, this.hour);

              const secondHand = document.querySelector('.second-hand');
              const minsHand = document.querySelector('.min-hand');
              const hourHand = document.querySelector('.hour-hand');

              const seconds = now.getSeconds();
              const secondsDegrees = ((seconds / 60) * 360) + 90;
              secondHand.style.transform = `rotate(${secondsDegrees}deg)`;

              const mins = now.getMinutes();
              const minsDegrees = ((mins / 60) * 360) + ((seconds/60)*6) + 90;
              minsHand.style.transform = `rotate(${minsDegrees}deg)`;

              const hour = now.getHours();
              const hourDegrees = ((hour / 12) * 360) + ((mins/60)*30) + 90;
              hourHand.style.transform = `rotate(${hourDegrees}deg)`;
          }
        }
    }
</script>
	
<style scoped>
	
.clock {
  width: 30rem;
  height: 30rem;
  border: 7px solid #282828;
  box-shadow: -4px -4px 10px rgba(67,67,67,0.5),
                inset 4px 4px 10px rgba(0,0,0,0.5),
                inset -4px -4px 10px rgba(67,67,67,0.5),
                4px 4px 10px rgba(0,0,0,0.3);
  border-radius: 50%;
  margin: 50px auto;
  position: relative;
  padding: 2rem;
 
}

.outer-clock-face {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 100%;
  background: #282828;
  
 
  overflow: hidden;
}

.outer-clock-face::after {
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  transform: rotate(90deg)
}

.outer-clock-face::before,
.outer-clock-face::after,
.outer-clock-face .marking{
  content: '';
  position: absolute;
  width: 5px;
  height: 100%;
  background: #1df52f;
  z-index: 0;
  left: 49%;
}

.outer-clock-face .marking {
  background: #bdbdcb;
  width: 3px;
}

.outer-clock-face .marking.marking-one {
  -webkit-transform: rotate(30deg);
  -moz-transform: rotate(30deg);
  transform: rotate(30deg)
}

.outer-clock-face .marking.marking-two {
  -webkit-transform: rotate(60deg);
  -moz-transform: rotate(60deg);
  transform: rotate(60deg)
}

.outer-clock-face .marking.marking-three {
  -webkit-transform: rotate(120deg);
  -moz-transform: rotate(120deg);
  transform: rotate(120deg)
}

.outer-clock-face .marking.marking-four {
  -webkit-transform: rotate(150deg);
  -moz-transform: rotate(150deg);
  transform: rotate(150deg)
}

.inner-clock-face {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  background: #282828;
  -webkit-border-radius: 100%;
  -moz-border-radius: 100%;
  border-radius: 100%;
  z-index: 1;
}

.inner-clock-face::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 16px;
  height: 16px;
  border-radius: 18px;
  margin-left: -9px;
  margin-top: -6px;
  background: #4d4b63;
  z-index: 11;
}

.hand {
  width: 50%;
  right: 50%;
  height: 6px;
  background: #61afff;
  position: absolute;
  top: 50%;
  border-radius: 6px;
  transform-origin: 100%;
  transform: rotate(90deg);
  transition-timing-function: cubic-bezier(0.1, 2.7, 0.58, 1);
}

.hand.hour-hand {
  width: 30%;
  z-index: 3;
  transition: transform 0.5s;
}

.hand.min-hand {
  height: 3px;
  z-index: 10;
  width: 40%;
  transition: transform 0.5s;
}

.hand.second-hand {
  background: #ee791a;
  width: 45%;
  height: 2px;
  transition: transform 0.5s;
}

.hover-hand:hover {
  cursor: pointer;
}
</style>