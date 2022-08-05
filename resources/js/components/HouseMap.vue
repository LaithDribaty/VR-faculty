<template>

    <div class="p-3">
        <div class="row">
            <div class="col-9">
                <div>
                    bellow is a map of the house and its walls according to the floorplan on the right
                    if you want to change it please change it and re evaluate the result and
                    alter it as you wish 
                </div>
                <div id="WebGL-output">

                </div>
                <!-- <house-three-d :house_id="house_id" /> -->

            </div>
            <div class="col-3 bg-dark text-white vld-parent">
                
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
                        <button @click="regeneratePoints" class="btn btn-warning"> re generate </button>
                    </div>

                    <div class="row" v-for="item in img">
                        <div class="col">
                            {{ item.index }} {{ item.value }}
                        </div>
                        <div class="col">
                            <input v-model="item.value" :name="item.index" type="range" min="item.min" max="item.max">
                        </div>
                        <div class="w-100">

                        </div>
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
                <span class="mx-1">
                    <button @click="saveWalls" class="btn btn-primary">
                        save walls
                    </button>
                </span>
                <span class="mx-1">
                    <button class="btn btn-primary" @click="this.drawLine({x: 10, y: 10}, {x: 60, y: 10})">
                        add wall
                    </button>
                </span>

                <!-- debug clear this -->
                <!-- <span class="mx-1">
                    <button class="btn btn-primary" id="test">
                        test
                    </button>
                </span> -->
                <!-- debug -->

                <span class="mx-1">
                    <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-trash cursor-pointer transition-color" viewBox="0 0 16 16" @click="this.deleteState = !this.deleteState" v-bind:class="{'text-danger': this.deleteState == true, 'text-light': this.deleteState == false}">
                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"></path>
                        <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"></path>
                    </svg>
                </span>
            </div>
        </nav>
        <div id="paint"></div>
    </div>
        

</template>

<script>
    import Konva from 'konva';
    import route from '../../../vendor/tightenco/ziggy/src/js';
    import * as THREE from 'three';
    import * as OrControl from 'three/examples/jsm/controls/OrbitControls.js'

    import Loading from 'vue-loading-overlay';
    import 'vue-loading-overlay/dist/vue-loading.css';

    export default {
        data() {
            return {
                csrfToken: document.head.querySelector("[name~=csrf-token][content]").content,
                img: [
                    {'index': 'binary', 'value': 230, 'min': 1, 'max': 250},
                    {'index': 'mainpoints', 'value': 10, 'min': 1, 'max': 20},
                    {'index': 'tunepoints', 'value': 5, 'min': 1, 'max': 20},
                    {'index': 'createlines', 'value': 5, 'min': 1, 'max': 20}
                ],
                stage: null,
                wallsGroup: null,
                deleteState: false,
                isLoading: false
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
            initiateKonvaContainer(){
                this.stage = new Konva.Stage({
                    container: 'paint',
                    width: window.innerWidth,
                    height: window.innerHeight,
                    draggable: true,
                });

                // add canvas element
                let layer = new Konva.Layer();
                this.wallsGroup = new Konva.Group(); this.wallsGroup.name('wallsGroup');
                
                var box = new Konva.Rect({
                    x: 0,
                    y: 0,
                    width: 1000,
                    height: 1000,
                    fill: 'black',
                    name: 'bg'
                });
                layer.add(box);

                layer.add(this.wallsGroup);
                this.stage.add(layer); 

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
                
                this.stage.on('click', (e)=> {
                    e.evt.preventDefault();
                    if (e.target.attrs.container == this.stage.attrs.container) {
                        // if we are on empty place of the stage we will do nothing
                        return;
                    }

                    let currentShape = e.target;
                    if(currentShape.attrs.name == "bg")
                        return ;
                    if(currentShape.attrs.name == "wall-stuff")
                        currentShape = currentShape.getParent();
                    if(this.deleteState) 
                        currentShape.destroy();
                });
            },
            drawLine(p1, p2, width=3) {
                p1.x = parseInt(p1.x);p1.y = parseInt(p1.y);
                p2.x = parseInt(p2.x);p2.y = parseInt(p2.y);
                let group = new Konva.Group({
                    draggable: true
                });

                let line = new Konva.Line({
                    points: [p1.x, p1.y, p2.x, p2.y],
                    stroke: '#FFF4F4',
                    strokeWidth: width,
                    name: 'wall-stuff'
                });
                line.on('mouseover', function () {
                    this.stroke('red');
                });
                line.on('mouseout', function () {
                    this.stroke('#FFF4F4');
                });

                let circle1 = new Konva.Circle({x: p1.x, y: p1.y, radius: 5, draggable: true, fill: '#FFF4F4', name: 'wall-stuff'});
                circle1.hitStrokeWidth(0);
                circle1.shadowForStrokeEnabled(false);
                
                circle1.on('mouseover', function () {
                    this.fill('red');
                });
                circle1.on('mouseout', function () {
                    this.fill('#FFF4F4');
                });

                circle1.on('dragmove', function(event){
                    const circle1Pos = circle1.getPosition(), circle2Pos = circle2.getPosition();
                    line.position(circle1Pos);
                    line.points([0, 0, circle2Pos.x - circle1Pos.x, circle2Pos.y - circle1Pos.y])
                });
                let circle2 = circle1.clone({x: p2.x, y: p2.y});
                circle2.on('dragmove', function(event){
                    const linePos = line.getPosition(), circle2Pos = circle2.getPosition(); 
                    line.points([0, 0, circle2Pos.x - linePos.x, circle2Pos.y - linePos.y]);
                });

                group.add(line, circle1, circle2);
                this.wallsGroup.add(group);
            },
            initiateWEBGLContainer() {
                let scene = new THREE.Scene();
                
                let camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1300);
                let renderer = new THREE.WebGLRenderer();
                renderer.setClearColor(new THREE.Color(0xffffff));
                renderer.setSize(window.innerWidth, window.innerHeight);

                camera.position.x = 500;
                camera.position.y = 200;
                camera.position.z = 1100;

                // add the output of the renderer to the html element
                renderer.domElement.style.width = "100%";
                renderer.domElement.style.height = "100%";

                let myWebGL = document.getElementById("WebGL-output");
                while (myWebGL.firstChild) {
                    myWebGL.removeChild(myWebGL.lastChild);
                }
                myWebGL.appendChild(renderer.domElement);

                let orbit = new OrControl.OrbitControls(camera, renderer.domElement);

                let ambiColor = "#1c1c1c";
                let ambientLight = new THREE.AmbientLight(ambiColor);
                scene.add(ambientLight);

                let directionalLight = new THREE.DirectionalLight(0xf3f3f3);
                directionalLight.position.set(20, 40, 20);

                directionalLight.shadow.camera.near = 5;
                directionalLight.shadow.camera.far = 20;
                directionalLight.shadow.camera.left = -20;
                directionalLight.shadow.camera.right = 20;
                directionalLight.shadow.camera.top = 20;
                directionalLight.shadow.camera.bottom = -20;
                directionalLight.intensity = 1;
                scene.add(directionalLight);
                
                const geometry = new THREE.PlaneGeometry( 1000, 1000 );
                const material = new THREE.MeshBasicMaterial( {color: 0x090909, side: THREE.DoubleSide} );
                const plane = new THREE.Mesh( geometry, material );
                plane.rotation.x = Math.PI/2;
                plane.position.set(500, 0, 500);
                scene.add( plane );

                orbit.target = new THREE.Vector3(500, 0, 500);

                for(let i=0; i < this.wallsGroup.children.length; ++i) {
                    let circle1 = this.wallsGroup.children[i].children[1].absolutePosition();
                    let circle2 = this.wallsGroup.children[i].children[2].absolutePosition();
                    this.addWall(circle1.x, circle1.y, circle2.x, circle2.y, scene);
                }
                
                render();
                function render() {
                    orbit.update();
                    // render using requestAnimationFrame
                    requestAnimationFrame(render);
                    renderer.render(scene, camera);
                }
            },
            addWall(x1, y1, x2, y2, scene) {
                let height = 50;
                let thick = 3;
                // calc length
                let length = Math.sqrt( Math.abs(x1 - x2)*Math.abs(x1 - x2) + Math.abs(y1 - y2)*Math.abs(y1 - y2) );

                const columnGeometry = new THREE.BoxGeometry(length, height, thick);
                const columnMaterial = new THREE.MeshLambertMaterial({color: '#FFF4F4'});
                const column = new THREE.Mesh(columnGeometry, columnMaterial);

                // calc position
                let position = { 
                    'x': (x1 + x2)/2,
                    'y': (y1 + y2)/2
                };
                column.position.set(position.x, height/2 , position.y);

                // calc rotation
                let teta = Math.atan2((x2-x1), (y2-y1));
                column.rotation.set(0, teta-Math.PI/2, 0);

                scene.add(column);
            },

            regeneratePoints() {
                const url = route('house.walls.generate',{
                    id: this.house_id,
                    binary: this.img[0].value,
                    mainpoints: this.img[1].value,
                    tunepoints: this.img[2].value,
                    createlines: this.img[3].value
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
                    this.createWalls(res);
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

            saveWalls() {
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
                        let wallsArr = [];
                        for(let i=0; i < this.wallsGroup.children.length; ++i) {
                            let circle1 = this.wallsGroup.children[i].children[1].absolutePosition();
                            let circle2 = this.wallsGroup.children[i].children[2].absolutePosition();
                            let el = {
                                'start': circle1,
                                'end': circle2
                            }
                            wallsArr.push(el);
                        }

                        this.isLoading = true;
                        fetch( route('house.walls.edit', this.house_id) ,{
                            method: 'POST',
                            headers: {
                                "X-CSRF-Token": this.csrfToken
                            },
                            body: JSON.stringify({
                                'walls': wallsArr
                            })
                        })
                        .then(res => res.json())
                        .then(res => {
                            this.$swal(
                                'Saved!',
                                'save completed',
                                'success'
                            )
                            this.createWalls(res);
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
                    }
                });
            }
        },

        created() {
            
        },

        mounted() {

            this.initiateKonvaContainer();
            this.isLoading = true;

            fetch( route('house.walls', this.house_id) )
            .then(res => res.json())
            .then(res => {
                this.createWalls(res);
                this.initiateWEBGLContainer();
            })
            .catch(res => {
                this.$swal(
                    'Error!',
                    'try again later, we faced some error getting data',
                    'error'
                )
            })
            .finally(()=>{
                this.isLoading = false;
            });

            // document.getElementById("test").addEventListener("click", (e) => {
            //     // debug reasons remember to remove
            // });
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
</style>