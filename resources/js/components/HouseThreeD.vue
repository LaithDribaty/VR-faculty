<template>

    <div class="position-relative">
        <Switch />
    </div>

    <div id="WebGL-output">
    </div>


</template>

<script>
    import route from '../../../vendor/tightenco/ziggy/src/js';
    import * as THREE from 'three';
    import * as OrControl from 'three/examples/jsm/controls/OrbitControls.js';
    import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
    import Switch from './Switch';

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
            Switch
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

                const loader = new GLTFLoader();

                // TO DO
                let objects = res.objects;  
                for(let i=0; i < objects.length; ++i) {
                    if(objects[i].mesh_id) {
                        // something 
                        loader.load(
                            // resource URL
                            '/storage/meshes/bin/'+objects[i].meshName+'.glb',

                            function ( glb ) {
                                objects[i].position = JSON.parse(objects[i].position);
                                glb.scene.children[0].position.set(objects[i].position.x * 0.01, 0, objects[i].position.y * 0.01);
                                glb.scene.children[0].rotation.y = -(objects[i].rotation * Math.PI) / 180;
                                glb.scene.children[0].scale.set(objects[i].size, objects[i].size, objects[i].size);
                                scene.add( glb.scene.children[0] );
                            },

                            // called when loading has errors
                            function ( error ) {
                                console.log( 'An error happened' );
                            }
                        );
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

                let floors = res.floors;
                for(let i=0; i < floors.length; ++i) {
                    
                    floors[i].p1 = JSON.parse(floors[i].p1);
                    floors[i].p2 = JSON.parse(floors[i].p2);
                    floors[i].p3 = JSON.parse(floors[i].p3);
                    floors[i].p4 = JSON.parse(floors[i].p4);

                    const floorShape = new THREE.Shape();
                    floorShape.moveTo( floors[i].p1.x * 0.01, floors[i].p1.y * 0.01);
                    floorShape.lineTo( floors[i].p2.x * 0.01, floors[i].p2.y * 0.01);
                    floorShape.lineTo( floors[i].p3.x * 0.01, floors[i].p3.y * 0.01);
                    floorShape.lineTo( floors[i].p4.x * 0.01, floors[i].p4.y * 0.01);
                    
                    const floorTexture = new THREE.TextureLoader().load( `${floors[i].image_url}` );
                    floorTexture.wrapS = THREE.RepeatWrapping;
                    floorTexture.wrapT = THREE.RepeatWrapping;
                    floorTexture.repeat.set( 1, 1 );

                    const floorGeometry = new THREE.ShapeGeometry( floorShape );
                    const floorMaterial = new THREE.MeshBasicMaterial( { 
                        map: floorTexture,
                        side: THREE.DoubleSide
                    });
                    const floor = new THREE.Mesh( floorGeometry, floorMaterial ) ;
                    floor.rotateX(Math.PI * 0.5);
                    floor.position.y = 0.01;
                    scene.add( floor );
                }


                let THREEx = this.initiateDayLight();

                let SunSphere = THREEx.DayNight.SunSphere
                let SunLight = THREEx.DayNight.SunLight
                let Skydom = THREEx.DayNight.Skydom
                let StarField = THREEx.DayNight.StarField
                let step = -0.04, night =  Math.PI * 1.6, day = Math.PI/2, sunAngle = day; // step if positive then day if negative then to night

                // day and night piece of code
                scene.add( SunSphere.object3d )
                scene.add( SunLight.object3d )
                scene.add( Skydom.object3d )
                scene.add( StarField.object3d )

                SunSphere.update(sunAngle);
                SunLight.update(sunAngle);
                Skydom.update(sunAngle);
                StarField.update(sunAngle);

                render();
                function render() {
                    orbit.update();
                    if(sunAngle + step < night && sunAngle + step > day) {
                        sunAngle += step;
                        SunSphere.update(sunAngle);
                        SunLight.update(sunAngle);
                        Skydom.update(sunAngle);
                        StarField.update(sunAngle);
                    }

                    // render using requestAnimationFrame
                    requestAnimationFrame(render);
                    renderer.render(scene, camera);
                }

                document.getElementById('toggle').addEventListener('change', (e) => {
                    step = (e.target.checked)? 0.04 : -0.04;
                });
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
                let THREEx = THREEx  || {}
                THREEx.DayNight    = {}
                THREEx.DayNight.baseURL    = '../../public/'

                THREEx.DayNight.currentPhase   = function(sunAngle){
                    if( Math.sin(sunAngle) > Math.sin(0) ){
                        return 'day'
                    }else if( Math.sin(sunAngle) > Math.sin(-Math.PI/6) ){
                        return 'twilight'
                    }else{
                        return 'night'
                    }
                }


                //////////////////////////////////////////////////////////////////////////////////
                //      starfield                               //
                //////////////////////////////////////////////////////////////////////////////////
                THREEx.DayNight.StarField = new Object();
                {
                    let texture = new THREE.TextureLoader().load( '/images/galaxy_starfield.png' );
                    let material    = new THREE.MeshBasicMaterial({
                        map : texture,
                        side    : THREE.BackSide,
                        color   : 0x808080,
                    })
                    let geometry    = new THREE.SphereGeometry(100, 32, 32)
                    let mesh    = new THREE.Mesh(geometry, material)
                    THREEx.DayNight.StarField.object3d = mesh;
                    THREEx.DayNight.StarField.update = function(sunAngle){
                        let phase   = THREEx.DayNight.currentPhase(sunAngle)
                        if( phase === 'day' ){
                            mesh.visible    = false
                        }else if( phase === 'twilight' ){
                            mesh.visible    = false
                        } else {
                            mesh.visible    = true
                            mesh.rotation.y = sunAngle / 5
                                var intensity   = Math.abs(Math.sin(sunAngle))
                                material.color.setRGB(intensity, intensity, intensity)
                        }
                    }
                }

                //////////////////////////////////////////////////////////////////////////////////
                //      SunLight                            //
                //////////////////////////////////////////////////////////////////////////////////
                THREEx.DayNight.SunLight = new Object();
                {
                    let light   = new THREE.DirectionalLight( 0xffffff, 1 );
                    THREEx.DayNight.SunLight.object3d  = light
                    
                    THREEx.DayNight.SunLight.update    = function(sunAngle){
                        light.position.x = 0;
                        light.position.y = Math.sin(sunAngle) * 90000;
                        light.position.z = Math.cos(sunAngle) * 90000;

                        let phase   = THREEx.DayNight.currentPhase(sunAngle)
                        if( phase === 'day' ){
                            light.color.set("rgb(255,"+ (Math.floor(Math.sin(sunAngle)*200)+55) + "," + (Math.floor(Math.sin(sunAngle)*200)) +")");
                        }else if( phase === 'twilight' ){
                                light.intensity = 1;
                                light.color.set("rgb(" + (255-Math.floor(Math.sin(sunAngle)*510*-1)) + "," + (55-Math.floor(Math.sin(sunAngle)*110*-1)) + ",0)");
                        } else {
                            light.intensity = 0;
                        }
                    }
                }

                //////////////////////////////////////////////////////////////////////////////////
                //      SunSphere                           //
                //////////////////////////////////////////////////////////////////////////////////
                THREEx.DayNight.SunSphere = new Object();
                {
                    let geometry    = new THREE.SphereGeometry( 20, 30, 30 )
                    let material    = new THREE.MeshBasicMaterial({
                        color       : 0xff0000
                    })
                    let mesh    = new THREE.Mesh(geometry, material)
                    THREEx.DayNight.SunSphere.object3d   = mesh

                    THREEx.DayNight.SunSphere.update = function(sunAngle){
                        mesh.position.x = 0;
                        mesh.position.y = Math.sin(sunAngle) * 400;
                        mesh.position.z = Math.cos(sunAngle) * 400;

                        let phase   = THREEx.DayNight.currentPhase(sunAngle)
                        if( phase === 'day' ){
                            mesh.material.color.set("rgb(255,"+ (Math.floor(Math.sin(sunAngle)*200)+55) + "," + (Math.floor(Math.sin(sunAngle)*200)+5) +")");
                        }else if( phase === 'twilight' ){
                            mesh.material.color.set("rgb(255,55,5)");
                        } else {
                        }
                    }
                }

                //////////////////////////////////////////////////////////////////////////////////
                //      Skydom                              //
                //////////////////////////////////////////////////////////////////////////////////
                THREEx.DayNight.Skydom = new Object();
                THREEx.DayNight.Skydom.Shader  = {
                    uniforms    : {
                        topColor    : { type: "c", value: new THREE.Color().setHSL( 0.6, 1, 0.75 ) },
                        bottomColor : { type: "c", value: new THREE.Color( 0xffffff ) },
                        offset      : { type: "f", value: 400 },
                        exponent    : { type: "f", value: 0.6 },
                    },
                    vertexShader    : [
                        'varying vec3 vWorldPosition;',
                        'void main() {',
                        '   vec4 worldPosition = modelMatrix * vec4( position, 1.0 );',
                        '   vWorldPosition = worldPosition.xyz;',
                        '   gl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );',
                        '}',
                    ].join('\n'),
                    fragmentShader  : [
                        'uniform vec3 topColor;',
                        'uniform vec3 bottomColor;',
                        'uniform float offset;',
                        'uniform float exponent;',

                        'varying vec3 vWorldPosition;',

                        'void main() {',
                        '   float h = normalize( vWorldPosition + offset ).y;',
                        '   gl_FragColor = vec4( mix( bottomColor, topColor, max( pow( h, exponent ), 0.0 ) ), 1.0 );',
                        '}',
                    ].join('\n'),
                }

                {
                    let geometry    = new THREE.SphereGeometry( 700, 32, 15 );
                    let shader  = THREEx.DayNight.Skydom.Shader
                    let uniforms    = THREE.UniformsUtils.clone(shader.uniforms)
                    let material    = new THREE.ShaderMaterial({
                        vertexShader    : shader.vertexShader,
                        fragmentShader  : shader.fragmentShader,
                        uniforms    : uniforms,
                        side        : THREE.BackSide
                    });

                    let mesh    = new THREE.Mesh( geometry, material );
                    THREEx.DayNight.Skydom.object3d   = mesh
                    
                    THREEx.DayNight.Skydom.update = function(sunAngle){
                        let phase   = THREEx.DayNight.currentPhase(sunAngle)
                        if( phase === 'day' ){
                            uniforms.topColor.value.set("rgb(0,120,255)");
                            uniforms.bottomColor.value.set("rgb(255,"+ (Math.floor(Math.sin(sunAngle)*200)+55) + "," + (Math.floor(Math.sin(sunAngle)*200)) +")");
                        } else if( phase === 'twilight' ){
                            uniforms.topColor.value.set("rgb(0," + (120-Math.floor(Math.sin(sunAngle)*240*-1)) + "," + (255-Math.floor(Math.sin(sunAngle)*510*-1)) +")");
                            uniforms.bottomColor.value.set("rgb(" + (255-Math.floor(Math.sin(sunAngle)*510*-1)) + "," + (55-Math.floor(Math.sin(sunAngle)*110*-1)) + ",0)");
                        } else {
                            uniforms.topColor.value.set('black')
                            uniforms.bottomColor.value.set('black');
                        }       
                    }

                    return THREEx;
                }
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
