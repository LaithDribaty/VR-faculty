<template>

    <div class="p-3">
        <div class="row">
            <div class="col-md-9">
                <div>
                    bellow is a map of the house and its walls according to the floorplan on the right
                    if you want to change it please change it and re evaluate the result and
                    alter it as you wish 
                </div>
                <house-three-d :key="webgl_key" :house_id="house_id" />

            </div>
            <div class="col-3 d-none d-md-block bg-dark text-white vld-parent">
                
                <loading v-model:active="isLoading"
                                :can-cancel="false"
                                :is-full-page="false"/>

                <div class="p-3">
                    <img class="w-100" :src="image" alt="" id="output">
                    <input class="btn btn-primary w-100 form-control mt-3" type="file" id="image" accept="image/*" name="image" @change="loadFile" placeholder="upload image">
                    <button class="btn btn-primary w-100 mt-3 disabled" @click="uploadImage" id="uploadButton"> 
                        change
                    </button>
                    <hr>
                    <div>
                        <button @click="regeneratePoints" class="btn btn-warning w-100"> re generate </button>
                    </div>
                    <div class="mt-3">
                        <button @click="saveMap" class="btn btn-primary w-100">
                            save map
                        </button>
                    </div>
                    <div class="mt-3">
                        make sure to save your map after you edit it 
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" class="bi bi-info-circle" viewBox="0 0 16 16">
                          <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                          <path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533L8.93 6.588zM9 4.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/>
                        </svg>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="stage-parent" class="shadow border border-dark bg-white vld-parent">

        <loading v-model:active="isLoading"
                        :can-cancel="false"
                        :is-full-page="false"/>

        <nav class="navbar navbar-light bg-dark">
            <div class="nav-item">
                <!-- testing -->
                <span class="mx-1">
                    <button class="btn btn-primary" @click="this.test()">
                        test
                    </button>
                </span>
                <!-- testing -->

                <span class="mx-1">
                    <button class="btn btn-primary" @click="this.drawLine({x: 10, y: 10}, {x: 60, y: 10})">
                        add wall
                    </button>
                </span>
                <span class="mx-1">
                    <button class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#furnatureOffcanvas" aria-controls="furnatureOffcanvas">
                        furnatures
                    </button>
                </span>
                <span class="mx-1">
                    <button class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#floorsOffcanvas" aria-controls="floorsOffcanvas">
                        floors
                    </button>
                </span>

                <div class="offcanvas offcanvas-top shadow border-0 border-bottom border-dark" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="furnatureOffcanvas" aria-labelledby="offcanvasTopLabel">
                    <div class="offcanvas-body">
                        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                        <div class="row p-3">
                            <div class="col-3 col-md-2 col-lg-1 border border-dark shadow expand-hover p-0 mx-1" @click="this.addMesh(20, 20, '', '', '/meshes/images/light.png')">
                                <img src="/meshes/images/light.png"  class="w-100" />
                            </div>
                            
                            <div v-for="mesh in meshes" class="col-3 col-md-2 col-lg-1 border border-dark shadow expand-hover p-0 mx-1" @click="this.addMesh(20, 20, mesh.id, '', '/storage/meshes/images/'+ mesh.name + 'up.png')">
                                <img :src="'/storage/meshes/images/'+mesh.name+'front.png'" class="w-100 pt-4" />
                            </div>

                        </div>

                    </div>
                </div>

                <div class="offcanvas offcanvas-top shadow border-0 border-bottom border-dark" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="floorsOffcanvas" aria-labelledby="offcanvasTopLabel">
                    <div class="offcanvas-body">
                        <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                        <div class="row">
                            <div class="col-3 col-md-2 col-lg-1 border border-dark shadow expand-hover p-0 mx-1" @click="this.addFloor('/images/floortexture/stone.jpeg')">
                                <img src="/images/floortexture/stone.jpeg" class="w-100" />
                            </div>

                            <div class="col-3 col-md-2 col-lg-1 border border-dark shadow expand-hover p-0 mx-1" @click="this.addFloor('/images/floortexture/wooden.jpg')">
                                <img src="/images/floortexture/wooden.jpg" class="w-100" />
                            </div>
                        </div>
                    </div>
                </div>

                <span class="mx-1">
                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-trash cursor-pointer transition-color" viewBox="0 0 16 16" @click="this.deleteState = !this.deleteState" v-bind:class="{'text-danger': this.deleteState == true, 'text-light': this.deleteState == false}">
                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"></path>
                        <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"></path>
                    </svg>
                </span>
            </div>
        </nav>
        
        <div id="paint" class="position-relative"></div>

    </div>
        

</template>

<script>
    import Konva from 'konva';
    import route from '../../../vendor/tightenco/ziggy/src/js';
    import * as THREE from 'three';
    import * as OrControl from 'three/examples/jsm/controls/OrbitControls.js'

    import Loading from 'vue-loading-overlay';
    import 'vue-loading-overlay/dist/vue-loading.css';

    class JoyStick{
        constructor(options){
            const circle = document.createElement("div");
            circle.style.cssText = "position:absolute; top: 10px; width:80px; height:80px; background:rgba(126, 126, 126, 0.5); border:#444 solid medium; border-radius:50%; left:50px; transform:translateX(-50%);";
            const thumb = document.createElement("div");
            thumb.style.cssText = "position: absolute; left: 20px; top: 20px; width: 40px; height: 40px; border-radius: 50%; background: #fff;";
            thumb.id = "thumb"
            circle.appendChild(thumb);
            document.getElementById('paint').appendChild(circle);
            this.domElement = thumb;
            this.maxRadius = options.maxRadius || 40;
            this.maxRadiusSquared = this.maxRadius * this.maxRadius;
            this.onMove = options.onMove;
            this.origin = { left:this.domElement.offsetLeft, top:this.domElement.offsetTop };
            this.forward = 0;
            this.turn    = 0;

            if (this.domElement!=undefined){
                const joystick = this;
                if ('ontouchstart' in window){
                    this.domElement.addEventListener('touchstart', function(evt){ 
                        evt.preventDefault();
                        joystick.tap(evt);
                        evt.stopPropagation();
                    });
                }else{
                    this.domElement.addEventListener('mousedown', function(evt){ 
                        evt.preventDefault();
                        joystick.tap(evt);
                        evt.stopPropagation();
                    });
                }
            }
        }
        
        getMousePosition(evt){
            let clientX = evt.targetTouches ? evt.targetTouches[0].pageX : evt.clientX;
            let clientY = evt.targetTouches ? evt.targetTouches[0].pageY : evt.clientY;
            return { x:clientX, y:clientY };
        }
        
        tap(evt){
            evt = evt || window.event;
            // get the mouse cursor position at startup:
            this.offset = this.getMousePosition(evt);
            const joystick = this;
            if ('ontouchstart' in window){
                document.ontouchmove = function(evt){ evt.preventDefault(); joystick.move(evt); };
                document.ontouchend =  function(evt){ evt.preventDefault(); joystick.up(evt); };
            }else{
                document.onmousemove = function(evt){ evt.preventDefault(); joystick.move(evt); };
                document.onmouseup = function(evt){ evt.preventDefault(); joystick.up(evt); };
            }
        }
        
        move(evt){
            evt = evt || window.event;
            const mouse = this.getMousePosition(evt);
            // calculate the new cursor position:
            let left = mouse.x - this.offset.x;
            let top = mouse.y - this.offset.y;
            //this.offset = mouse;
            
            const sqMag = left*left + top*top;
            if (sqMag>this.maxRadiusSquared){
                //Only use sqrt if essential
                const magnitude = Math.sqrt(sqMag);
                left /= magnitude;
                top /= magnitude;
                left *= this.maxRadius;
                top *= this.maxRadius;
            }
            // set the element's new position:
            this.domElement.style.top = `${top + this.domElement.clientHeight/2}px`;
            this.domElement.style.left = `${left + this.domElement.clientWidth/2}px`;
            
            const forward = -(top - this.origin.top + this.domElement.clientHeight/2)/this.maxRadius;
            const turn = (left - this.origin.left + this.domElement.clientWidth/2)/this.maxRadius;
            
            this.turn = turn;
            this.forward = forward;
        }
        
        up(evt){
            if ('ontouchstart' in window){
                document.ontouchmove = null;
                document.touchend = null;
            }else{
                document.onmousemove = null;
                document.onmouseup = null;
            }
            this.domElement.style.top = `${this.origin.top}px`;
            this.domElement.style.left = `${this.origin.left}px`;

            this.forward = 0;
            this.turn    = 0;

            var event = new CustomEvent("joystickup", { });
            this.domElement.dispatchEvent(event);
        }
    }

    export default {
        data() {
            return {
                csrfToken: document.head.querySelector("[name~=csrf-token][content]").content,
                stage: null,
                wallsGroup: null,
                meshesGroup: null,
                floorsGroup: null,
                deleteState: false,
                isLoading: false,
                webgl_key: 0,
                meshes: [],
                joystick: null
            };
        },
        
        components: {
            Loading
        },

        props: [
            'image',
            'house_id'
        ],

        methods: {
            loadFile(event) {
                let image = document.getElementById('output');
                image.src = URL.createObjectURL(event.target.files[0]);
                document.getElementById('uploadButton').classList.remove('disabled');
            },
            uploadImage(event) {
                let image = document.getElementById('image').files[0];
                const fd = new FormData();
                fd.append('image', image);
                this.isLoading = true;
                fetch(route('house.map.image', this.house_id), { 
                        method: 'post',
                        body: fd,
                        headers: {
                            "X-CSRF-Token": this.csrfToken
                        } 
                    })
                    .then((res)=>{
                        this.$swal(
                            'Saved!',
                            'image was saved',
                            'success'
                        )
                        event.target.classList.add('disabled');
                    })
                    .catch(err => {
                        this.$swal(
                            'Error!',
                            'try again later, we faced some error saving your image',
                            'error'
                        )
                    })
                    .finally(()=>{
                        this.isLoading = false;
                    });
            },
            createWalls(res) {
                const len = this.wallsGroup.children.length;
                for(let i=len-1; i>=0; --i)
                    this.wallsGroup.children[i].destroy();

                for(let i=0; i < res.length; ++i) {
                    let sp = JSON.parse(res[i].start);
                    let ep = JSON.parse(res[i].end);
                    this.drawLine(sp, ep);
                }
            },
            createObjects(res){
                const len = this.meshesGroup.children.length;
                for(let i=len-1; i>=0; --i)
                    this.meshesGroup.children[i].destroy();

                for(let i=0; i < res.length; ++i) {
                    if(!res[i].mesh_id) {
                        this.addMesh(parseInt(JSON.parse(res[i].position).x), parseInt(JSON.parse(res[i].position).y), '', '', '/meshes/images/light.png');
                    } else {
                        this.addMesh(parseInt(JSON.parse(res[i].position).x), parseInt(JSON.parse(res[i].position).y), res[i].mesh_id, '', '/storage/meshes/images/' + res[i].meshName+'up.png', res[i].size, res[i].rotation);
                    }
                }
            },
            createFloors(res){
                const len = this.floorsGroup.children.length;
                for(let i=len-1; i>=0; --i)
                    this.floorsGroup.children[i].destroy();

                for(let i=0; i < res.length; ++i) {
                    this.addFloor(res[i].image_url, JSON.parse(res[i].p1), JSON.parse(res[i].p2), JSON.parse(res[i].p3), JSON.parse(res[i].p4));
                }
            },
            initiateKonvaContainer(){
                this.stage = new Konva.Stage({
                    container: 'paint',
                    width: window.innerWidth,
                    height: window.innerHeight,
                    // draggable: true,
                });

                // add canvas element
                let layer = new Konva.Layer();
                this.wallsGroup = new Konva.Group(); this.wallsGroup.name('wallsGroup');
                this.meshesGroup = new Konva.Group(); this.meshesGroup.name('meshesGroup');
                this.floorsGroup = new Konva.Group(); this.floorsGroup.name('floorsGroup');

                layer.add(this.wallsGroup);
                layer.add(this.meshesGroup);
                layer.add(this.floorsGroup); this.floorsGroup.moveToBottom();
                this.stage.add(layer);

                let street = new Konva.Rect({
                    width: 2000,
                    height: 50,
                    x: -1000,
                    y: -50,
                    fill: '#5F5F5F'
                });
                layer.add(street);
                for(let i =0;i<10; ++i)
                {
                    let temp = new Konva.Rect({
                        width: 30,
                        height: 10,
                        x: -1000 + i*200,
                        y: -30,
                        fill: 'white'
                    })
                    layer.add(temp)
                }

                // scale with wheel section
                let scaleBy = 1.1;
                this.stage.on('wheel', (e) => {
                    // stop default scrolling
                    e.evt.preventDefault();

                    let oldScale = this.stage.scaleX();
                    let pointer = this.stage.getPointerPosition();

                    let mousePointTo = {
                        x: (pointer.x - this.stage.x()) / oldScale,
                        y: (pointer.y - this.stage.y()) / oldScale,
                    };

                    // how to scale? Zoom in? Or zoom out?
                    let direction = e.evt.deltaY > 0 ? -1 : 1;

                    let newScale = direction > 0 ? oldScale * scaleBy : oldScale / scaleBy;
                    this.stage.scale({ x: newScale, y: newScale });
                    let newPos = {
                        x: pointer.x - mousePointTo.x * newScale,
                        y: pointer.y - mousePointTo.y * newScale,
                    };
                    this.stage.position(newPos);
                });


                // TESTING SNAPPING
                layer.on('dragmove', (e) => this.handleLayerDrag(e, layer)  );
                layer.on('dragend', function (e) {
                    // clear all previous lines on the screen
                    layer.find('.guid-line').forEach((l) => l.destroy());
                });

                // ALSO TESTING
                var tr = new Konva.Transformer();
                layer.add(tr);

                // add a new feature, lets add ability to draw selection rectangle
                var selectionRectangle = new Konva.Rect({
                    fill: 'rgba(0,0,255,0.5)',
                    visible: false,
                });
                layer.add(selectionRectangle);
                var x1, y1, x2, y2;

                this.stage.on('mousedown touchstart', (e) => mouseDownStage(e, this.stage));
                function mouseDownStage(e, stage) {
                    // do nothing if we mousedown on any shape
                    if (e.target.id !== stage.id) {
                      return;
                    }

                    e.evt.preventDefault();
                    x1 = stage.getRelativePointerPosition().x;
                    y1 = stage.getRelativePointerPosition().y;
                    x2 = stage.getRelativePointerPosition().x;
                    y2 = stage.getRelativePointerPosition().y;

                    selectionRectangle.visible(true);
                    selectionRectangle.width(0);
                    selectionRectangle.height(0);
                }

                this.stage.on('mousemove touchmove', (e) => mouseMoveStage(e, this.stage));
                function mouseMoveStage(e, stage) {
                    // do nothing if we didn't start selection
                    if (!selectionRectangle.visible()) {
                      return;
                    }
                    e.evt.preventDefault();
                    x2 = stage.getRelativePointerPosition().x;
                    y2 = stage.getRelativePointerPosition().y;

                    selectionRectangle.setAttrs({
                      x: Math.min(x1, x2),
                      y: Math.min(y1, y2),
                      width: Math.abs(x2 - x1),
                      height: Math.abs(y2 - y1),
                    });
                }

                this.stage.on('mouseup touchend', (e) => mouseUpStage(e, this.stage));
                function mouseUpStage(e, stage) {
                    // do nothing if we didn't start selection
                    if (!selectionRectangle.visible()) {
                      return;
                    }
                    e.evt.preventDefault();
                    // update visibility in timeout, so we can check it in click event
                    setTimeout(() => {
                        selectionRectangle.visible(false);
                    });

                    var shapes = stage.find('.transformable');
                    var box = selectionRectangle.getClientRect();
                    var selected = shapes.filter((shape) =>
                      Konva.Util.haveIntersection(box, shape.getClientRect())
                    );
                    tr.nodes(selected);
                }

                // clicks should select/deselect shapes
                this.stage.on('click tap', (e) => mouseClickStage(e, this.stage, this.deleteState));
                function mouseClickStage(e, stage, deleteState) {
                    e.evt.preventDefault();
                    if (e.target.attrs.container == stage.attrs.container) {
                        // if we are on empty place of the stage we will do nothing
                        return;
                    }

                    let currentShape = e.target;
                    if(currentShape.attrs.name == "wall-stuff")
                        currentShape = currentShape.getParent();
                    if(deleteState)
                        currentShape.destroy();

                    // // // // // // // // // // // // // // //

                    // if we are selecting with rect, do nothing
                    if (selectionRectangle.visible()) {
                      return;
                    }

                    // if click on empty area - remove all selections
                    if (e.target.id === stage.id) {
                      tr.nodes([]);
                      return;
                    }

                    // do nothing if clicked NOT on our rectangles
                    if (!e.target.hasName('transformable')) {
                      return;
                    }

                    // do we pressed shift or ctrl?
                    const metaPressed = e.evt.shiftKey || e.evt.ctrlKey || e.evt.metaKey;
                    const isSelected = tr.nodes().indexOf(e.target) >= 0;

                    if (!metaPressed && !isSelected) {
                      // if no key pressed and the node is not selected
                      // select just one
                      tr.nodes([e.target]);
                    } else if (metaPressed && isSelected) {
                      // if we pressed keys and node was selected
                      // we need to remove it from selection:
                      const nodes = tr.nodes().slice(); // use slice to have new copy of array
                      // remove node from array
                      nodes.splice(nodes.indexOf(e.target), 1);
                      tr.nodes(nodes);
                    } else if (metaPressed && !isSelected) {
                      // add the node into selection
                      const nodes = tr.nodes().concat([e.target]);
                      tr.nodes(nodes);
                    }
                }
            },

            // TESTING
            // were can we snap our objects?
            getLineGuideStops(skipShape) {
                // we can snap to stage borders and the center of the stage
                var vertical = [0, this.stage.width() / 2, this.stage.width()];
                var horizontal = [0, this.stage.height() / 2, this.stage.height()];

                // and we snap over edges and center of each object on the canvas
                this.stage.find('.object').forEach((guideItem) => {
                  if (guideItem.id === skipShape.id) {
                    return;
                  }
                  var box = guideItem.getClientRect();
                  // and we can snap to all edges of shapes
                  vertical.push([box.x, box.x + box.width, box.x + box.width / 2]);
                  horizontal.push([box.y, box.y + box.height, box.y + box.height / 2]);
                });
                return {
                  vertical: vertical.flat(),
                  horizontal: horizontal.flat(),
                };
            },
            // what points of the object will trigger to snapping?
            // it can be just center of the object
            // but we will enable all edges and center
            getObjectSnappingEdges(node) {
                var box = node.getClientRect();
                var absPos = node.absolutePosition();

                return {
                  vertical: [
                    {
                      guide: Math.round(box.x),
                      offset: Math.round(absPos.x - box.x),
                      snap: 'start',
                    },
                    {
                      guide: Math.round(box.x + box.width / 2),
                      offset: Math.round(absPos.x - box.x - box.width / 2),
                      snap: 'center',
                    },
                    {
                      guide: Math.round(box.x + box.width),
                      offset: Math.round(absPos.x - box.x - box.width),
                      snap: 'end',
                    },
                  ],
                  horizontal: [
                    {
                      guide: Math.round(box.y),
                      offset: Math.round(absPos.y - box.y),
                      snap: 'start',
                    },
                    {
                      guide: Math.round(box.y + box.height / 2),
                      offset: Math.round(absPos.y - box.y - box.height / 2),
                      snap: 'center',
                    },
                    {
                      guide: Math.round(box.y + box.height),
                      offset: Math.round(absPos.y - box.y - box.height),
                      snap: 'end',
                    },
                  ],
                };
            },
            // find all snapping possibilities
            getGuides(lineGuideStops, itemBounds) {
                var GUIDELINE_OFFSET = 5;

                var resultV = [];
                var resultH = [];

                lineGuideStops.vertical.forEach((lineGuide) => {
                  itemBounds.vertical.forEach((itemBound) => {
                    var diff = Math.abs(lineGuide - itemBound.guide);
                    // if the distance between guild line and object snap point is close we can consider this for snapping
                    if (diff < GUIDELINE_OFFSET) {
                      resultV.push({
                        lineGuide: lineGuide,
                        diff: diff,
                        snap: itemBound.snap,
                        offset: itemBound.offset,
                      });
                    }
                  });
                });

                lineGuideStops.horizontal.forEach((lineGuide) => {
                  itemBounds.horizontal.forEach((itemBound) => {
                    var diff = Math.abs(lineGuide - itemBound.guide);
                    if (diff < GUIDELINE_OFFSET) {
                      resultH.push({
                        lineGuide: lineGuide,
                        diff: diff,
                        snap: itemBound.snap,
                        offset: itemBound.offset,
                      });
                    }
                  });
                });

                var guides = [];

                // find closest snap
                var minV = resultV.sort((a, b) => a.diff - b.diff)[0];
                var minH = resultH.sort((a, b) => a.diff - b.diff)[0];
                if (minV) {
                  guides.push({
                    lineGuide: minV.lineGuide,
                    offset: minV.offset,
                    orientation: 'V',
                    snap: minV.snap,
                  });
                }
                if (minH) {
                  guides.push({
                    lineGuide: minH.lineGuide,
                    offset: minH.offset,
                    orientation: 'H',
                    snap: minH.snap,
                  });
                }
                return guides;
            },
            drawGuides(guides, layer) {
                guides.forEach((lg) => {
                  if (lg.orientation === 'H') {
                    var line = new Konva.Line({
                      points: [-6000, 0, 6000, 0],
                      stroke: 'rgb(0, 161, 255)',
                      strokeWidth: 1,
                      name: 'guid-line',
                      dash: [4, 6],
                    });
                    layer.add(line);
                    line.absolutePosition({
                      x: 0,
                      y: lg.lineGuide,
                    });
                  } else if (lg.orientation === 'V') {
                    var line = new Konva.Line({
                      points: [0, -6000, 0, 6000],
                      stroke: 'rgb(0, 161, 255)',
                      strokeWidth: 1,
                      name: 'guid-line',
                      dash: [4, 6],
                    });
                    layer.add(line);
                    line.absolutePosition({
                      x: lg.lineGuide,
                      y: 0,
                    });
                  }
                });
            },
            handleLayerDrag(e, layer) {
                // clear all previous lines on the screen
                layer.find('.guid-line').forEach((l) => l.destroy());

                // find possible snapping lines
                var lineGuideStops = this.getLineGuideStops(e.target);
                // find snapping points of current object
                var itemBounds = this.getObjectSnappingEdges(e.target);

                // now find where can we snap current object
                var guides = this.getGuides(lineGuideStops, itemBounds);

                // do nothing of no snapping
                if (!guides.length) {
                  return;
                }

                this.drawGuides(guides, layer);

                var absPos = e.target.absolutePosition();
                // now force object position
                guides.forEach((lg) => {
                  switch (lg.snap) {
                    case 'start': {
                      switch (lg.orientation) {
                        case 'V': {
                          absPos.x = lg.lineGuide + lg.offset;
                          break;
                        }
                        case 'H': {
                          absPos.y = lg.lineGuide + lg.offset;
                          break;
                        }
                      }
                      break;
                    }
                    case 'center': {
                      switch (lg.orientation) {
                        case 'V': {
                          absPos.x = lg.lineGuide + lg.offset;
                          break;
                        }
                        case 'H': {
                          absPos.y = lg.lineGuide + lg.offset;
                          break;
                        }
                      }
                      break;
                    }
                    case 'end': {
                      switch (lg.orientation) {
                        case 'V': {
                          absPos.x = lg.lineGuide + lg.offset;
                          break;
                        }
                        case 'H': {
                          absPos.y = lg.lineGuide + lg.offset;
                          break;
                        }
                      }
                      break;
                    }
                  }
                });
                e.target.absolutePosition(absPos);
            },





            addMesh(x, y, mesh, texture, image, size=0.1, rotation=0) {
                let imageObj = new Image();
                imageObj.src = image;
                imageObj.onload = this.handleAddingImage(x, y, mesh, texture, imageObj, size, rotation);
            },
            handleAddingImage(x, y, mesh, texture, imageObj, size, rotation) {
                let node = new Konva.Image({
                    x: x,
                    y: y,
                    image: imageObj,
                    scaleX: size,
                    scaleY: size,
                    rotation: rotation,
                    draggable: true,
                    name: 'object transformable'
                });

                let width = (node.width() * size)/2;
                let height = (node.height() * size)/2;

                let deg = (rotation * Math.PI) / 180;

                node.position({
                    x: x - (width * Math.cos(deg) - height * Math.sin(deg)),
                    y: y - (width * Math.sin(deg) + height * Math.cos(deg))
                })

                node.setAttr('mesh', mesh);
                node.setAttr('texture', texture);

                node.on('dblclick dbltap', function () {
                    this.destroy();
                });

                this.meshesGroup.add(node);                
            },


            drawLine(p1, p2, width=3) {
                p1.x = parseInt(p1.x);p1.y = parseInt(p1.y);
                p2.x = parseInt(p2.x);p2.y = parseInt(p2.y);
                let group = new Konva.Group({
                    draggable: true
                });

                let pnts = [p1.x, p1.y, p2.x, p2.y];
                let line = new Konva.Line({
                    points: pnts,
                    stroke: 'black',
                    strokeWidth: width,
                    name: 'wall-stuff object'
                });
                line.on('mouseover', function () {
                    this.stroke('red');
                });
                line.on('mouseout', function () {
                    this.stroke('black');
                });
                
                for(let i=0;i<2;++i) {
                    let circle = new Konva.Circle({x: pnts[i*2], y: pnts[i*2+1], radius: 5, draggable: true, fill: 'black', name: 'wall-stuff object'});

                    circle.hitStrokeWidth(0);
                    circle.shadowForStrokeEnabled(false);

                    circle.on('dragmove', function(event){
                        pnts[i*2] = circle.getPosition().x;
                        pnts[i*2+1] = circle.getPosition().y;
                    });
                    circle.on('mouseover', function(event){
                        this.fill('red');
                    });
                    circle.on('mouseout', function(event){
                        this.fill('black');
                    });
                    group.add(circle);
                }

                group.on('dragend', function(event){
                    group.children[0].position({
                        x: group.getPosition().x + group.children[0].getPosition().x,
                        y: group.getPosition().y + group.children[0].getPosition().y,
                    });
                    group.children[1].position({
                        x: group.getPosition().x + group.children[1].getPosition().x,
                        y: group.getPosition().y + group.children[1].getPosition().y,
                    });
                    
                    pnts[0] = group.children[0].getPosition().x;
                    pnts[1] = group.children[0].getPosition().y;
                    pnts[2] = group.children[1].getPosition().x;
                    pnts[3] = group.children[1].getPosition().y;
                    group.position({
                        x:0,
                        y:0
                    })
                });
                
                group.on('dblclick dbltap', function () {
                    this.destroy();
                });

                group.add(line);
                this.wallsGroup.add(group);
            },
            initiateWEBGLContainer() {
                // changing the key property of the threejs webgl component will re render it
                this.webgl_key++; 
            },

            regeneratePoints() {
                const url = route('house.regenerate',{
                    id: this.house_id
                });

                this.isLoading = true;
                fetch( url )
                .then(res => res.json())
                .then(res => {
                    this.$swal(
                        'Saved!',
                        'save completed',
                        'success'
                    )
                    this.createWalls(res.walls);
                    this.initiateWEBGLContainer();
                })
                .catch(res => {
                    this.$swal(
                        'Error!',
                        'try again later, we faced some error saving your changes',
                        'error'
                    )
                })
                .finally(()=>{
                    this.isLoading = false;
                });
            },

            addFloor(image_url, p1={x:10, y:10}, p2={x:10, y:30}, p3={x:30, y:30}, p4={x:30, y:10})
            {
                let imageObj = new Image();
                imageObj.onload = this.handleAddingFloor(imageObj, p1, p2, p3, p4)
                imageObj.src = image_url;
                
                // some error here
                // Konva.Image.fromURL(image_url, (imageObj) => this.handleAddingFloor(imageObj, p1, p2, p3, p4));
            },
            handleAddingFloor(imageObj, p1, p2, p3, p4) 
            {
                let pnts = [p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y];
                let poly = new Konva.Line({
                    points: pnts,
                    fillPatternImage: imageObj,
                    stroke: 'black',
                    strokeWidth: 1,
                    closed: true,
                    opacity: 0.9,
                    name: 'wall-stuff object'
                });

                let Group = new Konva.Group({
                    draggable: true
                });

                Group.on('mouseover', function(event) {
                    this.children[4].setOpacity(1);
                });
                Group.on('mouseout', function(event) {                    
                    this.children[4].setOpacity(0.9);
                });

                for(let i=0;i<4;++i) {
                    let circle = new Konva.Circle({x: pnts[i*2], y: pnts[i*2+1], radius: 2, draggable: true, fill: 'black', name: 'wall-stuff object'});
                    circle.hitStrokeWidth(0);
                    circle.shadowForStrokeEnabled(false);

                    circle.on('dragmove', function(event){
                        pnts[i*2] = circle.getPosition().x;
                        pnts[i*2+1] = circle.getPosition().y;
                    });
                    circle.on('mouseover', function(event){
                        this.fill('red');
                    });
                    circle.on('mouseout', function(event){
                        this.fill('black');
                    });

                    Group.add(circle);
                }

                Group.on('dragend', function(event){
                    for(let i=0;i<4;++i) {                    
                        Group.children[i].position({
                            x: Group.getPosition().x + Group.children[i].getPosition().x,
                            y: Group.getPosition().y + Group.children[i].getPosition().y,
                        });
                        pnts[i*2] = Group.children[i].getPosition().x;
                        pnts[i*2+1] = Group.children[i].getPosition().y;
                    }
                    Group.position({
                        x:0,
                        y:0
                    })
                });

                Group.add(poly);

                Group.on('dblclick dbltap', function () {
                    this.destroy();
                });
                
                // add the shape to the layer
                this.floorsGroup.add(Group);
            },

            saveMap() {
                this.$swal({
                    title: 'are you sure you want to save?',
                    text: "You won't be able to revert this!",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Yes, save changes!'
                }).then((result) => {
                    if (result.isConfirmed) {
                        this.saveWallsAndMeshes();
                    }
                });
            },

            saveWallsAndMeshes() {
                let wallsArr = [];
                for(let i=0; i < this.wallsGroup.children.length; ++i) {
                    let circle1 = this.wallsGroup.children[i].children[0].position();
                    let circle2 = this.wallsGroup.children[i].children[1].position();
                    let el = {
                        'start': circle1,
                        'end': circle2
                    }
                    wallsArr.push(el);
                }

                let meshesArr = [];
                for(let i=0; i < this.meshesGroup.children.length; ++i) {

                    let width = ( this.meshesGroup.children[i].width() * this.meshesGroup.children[i].scaleX() ) / 2;
                    let height = ( this.meshesGroup.children[i].height() * this.meshesGroup.children[i].scaleY() ) / 2;

                    let deg = (this.meshesGroup.children[i].getAttr('rotation') * Math.PI) / 180;
                    let oldPos = this.meshesGroup.children[i].position();

                    let pos = {
                        x: oldPos.x + (width * Math.cos(deg) - height * Math.sin(deg)),
                        y: oldPos.y + (width * Math.sin(deg) + height * Math.cos(deg))
                    };

                    let el = {
                        'position': pos,
                        'rotation': this.meshesGroup.children[i].getAttr('rotation'),
                        'mesh': this.meshesGroup.children[i].getAttr('mesh'),
                        'size': this.meshesGroup.children[i].getAttr('scaleX'), // ratio is 1 so scaleX and scaleY is the same
                        'texture': this.meshesGroup.children[i].getAttr('texture'),
                    }
                    meshesArr.push(el);
                }

                let floorArr = [];
                for(let i=0; i < this.floorsGroup.children.length; ++i) {
                    let el = {
                        'p1': this.floorsGroup.children[i].children[0].position(),
                        'p2': this.floorsGroup.children[i].children[1].position(),
                        'p3': this.floorsGroup.children[i].children[2].position(),
                        'p4': this.floorsGroup.children[i].children[3].position(),
                        'image_url': this.floorsGroup.children[i].children[4].fillPatternImage().getAttribute("src"),
                        'house_id': this.house_id
                    }
                    floorArr.push(el);
                }

                this.isLoading = true;
                fetch( route('house.objects.edit', this.house_id) ,{
                    method: 'POST',
                    headers: {
                        "X-CSRF-Token": this.csrfToken
                    },
                    body: JSON.stringify({
                        'walls': wallsArr,
                        'objects': meshesArr,
                        'floors': floorArr
                    })
                })
                .then(res => res.json())
                .then(res => {
                    this.$swal(
                        'Saved!',
                        'save completed',
                        'success'
                    )
                    this.createWalls(res.walls);
                    this.createObjects(res.objects);
                    this.createFloors(res.floors);
                    this.initiateWEBGLContainer();
                })
                .catch(err => {
                    this.$swal(
                        'Error!',
                        'try again later, we faced some error saving your changes',
                        'error'
                    )
                })
                .finally(()=>{
                    this.isLoading = false;
                });
            },


            moveStage() 
            {
                let x = -this.joystick.turn, z = this.joystick.forward;

                // play is how fast you want to move
                var play = '';
                if (x==0 && z==0) {
                    play = 'Idle'
                } else if(Math.abs(x) > 0.5 || Math.abs(z) > 0.5) {
                    play = 'Run'
                } else {
                    play = 'Walk'
                }
                const velocity = play == 'Run' ? 75 : 25;
                
                let pos = this.stage.getAbsolutePosition();
                let step = this.stage.scaleX() * velocity;
                pos.x += x * step;
                pos.y += z * step;

                this.stage.position({
                    x: pos.x,
                    y: pos.y
                });
            },

            test() {
                for(let i=0;i<this.meshesGroup.children.length; ++i) {
                    console.log('here is mesh number ' + i);
                    console.log(this.meshesGroup.children[i].width());
                    console.log({x: this.meshesGroup.children[i].position().x,
                        y: this.meshesGroup.children[i].position().y });
                }
                console.log('================');
                for(let i=0;i<this.wallsGroup.children.length; ++i) {
                    console.log('here is wall number ' + i);
                    console.log(this.wallsGroup.children[i].children[0].position());
                    console.log(this.wallsGroup.children[i].children[1].position());
                }
            }
        },

        created() {
            

        },

        mounted() {

            fetch( route('getMeshs') )
            .then(res => res.json())
            .then(res => {
                this.meshes = res;
            });

            this.initiateKonvaContainer();
            this.isLoading = true;

            fetch( route('house.objects', this.house_id) )
            .then(res => res.json())
            .then(res => {
                this.createWalls(res.walls);
                this.createObjects(res.objects);
                this.createFloors(res.floors);
                this.initiateWEBGLContainer();
            })
            .catch(res => {
                this.$swal(
                    'Error!',
                    'try again later, we faced some error getting data',
                    'error',
                )
            })
            .finally(()=>{
                this.isLoading = false;
            });

            this.joystick = new JoyStick({ });

            let interval;
            document.getElementById('thumb').addEventListener('mousedown', (e) => {
                interval = setInterval(this.moveStage, 40);
            });

            document.getElementById('thumb').addEventListener('joystickup', (e) => {
                clearInterval(interval);
            });
        }
    };
</script>


<style scoped>
    .cursor-pointer:hover{
        cursor: pointer;
    }
    .transition-color {
        transition: color 0.5s;
    }
    #WebGL-output{
        height: 70%;
        border: solid 2px #d1d1d1;
    }
    .expand-hover{
        transition: transform 0.3s;
    }
    .expand-hover:hover{
        transform: scale(1.08);
    }
</style>