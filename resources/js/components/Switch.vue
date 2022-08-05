<template>

  <div class="position-absolute top-0 end-0 mt-2 me-2">  
  	<input type="checkbox" id="toggle" class="toggle--checkbox">
  	<label for="toggle" class="toggle--label">
  	  	<span class="toggle--label-background"></span>
  	</label>
  </div>

</template>

<style scoped>

.toggle--checkbox {
  display: none;
}
.toggle--checkbox:checked {
  /** This will all flip from sun to moon **/
  /** Change the label color **/
}
.toggle--checkbox:checked ~ .background {
  background: #808fc7;
}
.toggle--checkbox:checked + .toggle--label {
  background: #6b7abb;
  border-color: #5d6baa;
  /** Change the cloud to stars **/
  /** Change the sun into the moon **/
  /** Show the dimples on the moon **/
}
.toggle--checkbox:checked + .toggle--label .toggle--label-background {
  left: 30px;
  width: 2.5px;
}
.toggle--checkbox:checked + .toggle--label .toggle--label-background:before {
  width: 2.5px;
  height: 2.5px;
  top: -17px;
}
.toggle--checkbox:checked + .toggle--label .toggle--label-background:after {
  width: 2.5px;
  height: 2.5px;
  left: -11px;
  top: 10px;
}
.toggle--checkbox:checked + .toggle--label:before {
  background: #fff;
  border-color: #e8e8ea;
  animation-name: switch;
  animation-duration: 350ms;
  animation-fill-mode: forwards;
}
.toggle--checkbox:checked + .toggle--label:after {
  transition-delay: 350ms;
  opacity: 1;
}
.toggle--label {
  /** Placeholder element, starting at blue **/
  width: 100px;
  height: 50px;
  background: #96dcee;
  border-radius: 100px;
  border: 5px solid #72cce3;
  display: flex;
  position: relative;
  transition: all 350ms ease-in;
  /** The sun cloud and moon stars **/
  /** Sun/Moon element **/
  /** Gray dots on the moon **/
}
.toggle--label-background {
  width: 5px;
  height: 2.5px;
  border-radius: 5px;
  position: relative;
  background: #fff;
  left: 67.5px;
  top: 22.5px;
  transition: all 150ms ease-in;
}
.toggle--label-background:before {
  content: "";
  position: absolute;
  top: -2.5px;
  width: 20px;
  height: 2.5px;
  border-radius: 5px;
  background: #fff;
  left: -10px;
  transition: all 150ms ease-in;
}
.toggle--label-background:after {
  content: "";
  position: absolute;
  top: 2.5px;
  width: 20px;
  height: 2.5px;
  border-radius: 5px;
  background: #fff;
  left: -5px;
  transition: all 150ms ease-in;
}
.toggle--label:before {
  animation-name: reverse;
  animation-duration: 350ms;
  animation-fill-mode: forwards;
  transition: all 350ms ease-in;
  content: "";
  width: 41px;
  height: 41px;
  border: 5px solid #f5eb71;
  top: 0px;
  left: 2px;
  position: absolute;
  border-radius: 82px;
  background: #fffaa8;
}
.toggle--label:after {
  transition-delay: 0ms;
  transition: all 250ms ease-in;
  position: absolute;
  content: "";
  box-shadow: #e8e8ea -13px 0 0 2px, #e8e8ea -24px 14px 0 -2px;
  left: 71.5px;
  top: 11.5px;
  width: 5px;
  height: 5px;
  background: transparent;
  border-radius: 50%;
  opacity: 0;
}

@keyframes switch {
  0% {
    left: 2px;
  }
  60% {
    left: 2px;
    width: 56px;
  }
  100% {
    left: 52px;
    width: 41px;
  }
}
@keyframes reverse {
  0% {
    left: 52px;
    width: 41px;
  }
  60% {
    left: 36px;
    width: 56px;
  }
  100% {
    left: 2px;
  }
}

</style>