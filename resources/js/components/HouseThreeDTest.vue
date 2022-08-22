<template>

    <div class="position-relative">
        <div class="watch">
        <watch />
        </div>
    </div>

    <div id="WebGL-output">
    </div>


</template>

<script>
    import route from '../../../vendor/tightenco/ziggy/src/js';
    import * as THREE from 'three';
    import * as OrControl from 'three/examples/jsm/controls/OrbitControls.js';
    import watch from './Watch'

    export default {
        data() {
            return {
                csrfToken: document.head.querySelector("[name~=csrf-token][content]").content,
            };
        },

        props: [
            'house_id'
        ],

        components: {
            watch
        },

        methods: {
            initiateWEBGLContainer(res) {
                let scene = new THREE.Scene();
                
                let camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1300);
                let renderer = new THREE.WebGLRenderer();
                renderer.setClearColor(new THREE.Color(0xffffff));
                renderer.setSize(window.innerWidth, window.innerHeight);

                camera.position.x = 5;
                camera.position.y = 2;
                camera.position.z = 11;

                // add the output of the renderer to the html element
                renderer.domElement.style.width = "100%";
                renderer.domElement.style.height = "100%";

                document.getElementById("WebGL-output").appendChild(renderer.domElement);

                let orbit = new OrControl.OrbitControls(camera, renderer.domElement);

                let ambiColor = "#1c1c1c";
                let ambientLight = new THREE.AmbientLight(ambiColor);
                scene.add(ambientLight);
                
                const geometry = new THREE.PlaneGeometry( 100, 100 );
                const material = new THREE.MeshBasicMaterial( {color: 0x595959, side: THREE.DoubleSide} );
                const plane = new THREE.Mesh( geometry, material );
                plane.rotation.x = Math.PI/2;
                plane.position.set(5, 0, 5);
                scene.add( plane );


                const streetGeometry = new THREE.PlaneGeometry( 100, 2 );
                const streetTexture = new THREE.TextureLoader().load( "/images/street_texture.jpg" );
                streetTexture.wrapS = THREE.RepeatWrapping;
                streetTexture.wrapT = THREE.RepeatWrapping;
                streetTexture.repeat.set( 30, 1 );
                const streetMaterial = new THREE.MeshBasicMaterial( {side: THREE.BackSide, map: streetTexture} );
                const streetPlane = new THREE.Mesh( streetGeometry, streetMaterial );
                streetPlane.rotation.x = Math.PI/2;
                streetPlane.position.set(5, 0.01, -1);
                scene.add( streetPlane );

                orbit.target = new THREE.Vector3(5, 0, 5);

                let walls = res.walls;
                for(let i=0; i < walls.length; ++i) {
                    let sp = JSON.parse(walls[i].start);
                    let ep = JSON.parse(walls[i].end);
                    this.addWall(sp.x, sp.y, ep.x, ep.y, scene, 0.01);
                }

                // TO DO
                let objects = res.objects;  
                for(let i=0; i < objects.length; ++i) {
                    if(objects[i].mesh_id) {
                        // something 
                        
                    } else {
                        const light = new THREE.PointLight( 0xffffff, objects[i].size, 1 );
                        const position = JSON.parse(objects[i].position);
                        light.position.set( position.x* 0.01, 0.4, position.y*0.01 );
                        scene.add( light );

                        // const sphereSize = 0.1;
                        // const pointLightHelper = new THREE.PointLightHelper( light, sphereSize );
                        // scene.add( pointLightHelper );
                    }
                }

                render();
                function render() {
                    orbit.update();

                    // render using requestAnimationFrame
                    requestAnimationFrame(render);
                    renderer.render(scene, camera);
                }

                
            },
            
            addWall(x1, y1, x2, y2, scene, factor) {
                [x1, x2, y1, y2] = [x1*factor, x2*factor, y1*factor, y2*factor]

                let height = 0.5;
                let thick = 0.03;
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
            
            initiateDayLight() {
                
            }
        },

        created() {
            
        },

        mounted() {

            fetch( route('house.objects', this.house_id) )
            .then(res => res.json())
            .then(res => {
                this.initiateWEBGLContainer(res);
            })
            .catch(res => {
                this.$swal(
                    'Error!',
                    'try again later, we faced some error getting data'+ res,
                    'error'
                )
            })
        }
    };
</script>

<style scoped>
    
    
</style>