<?php

namespace App\Http\Controllers;

use App\Models\House;
use App\Models\Wall;
use App\Models\ObjectModel;
use App\Models\Floor;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Http;

class MapController extends Controller
{
    public function index($id) 
    {
        $house = House::find($id);

        return view('map', [
            'house' => $house
        ]);
    }

    public function threeD($id) 
    {
        $house = House::find($id);

        return view('three_d', [
            'house' => $house
        ]);
    }

    public function wallsAndObjects($id)
    {
        $house = House::find($id);

        $objects = $house->objects;
        $walls = $house->walls;
        $floors = $house->floors;

        return response()->json([
            'objects'=> $objects,
            'walls'=> $walls,
            'floors'=> $floors
        ]);
    }

    public function meshes()
    {
        return response()->json(Mesh::all());
    }

    public function uploadImage(Request $request, $id) 
    {   
        $house = House::find($id);
        if ($request->hasFile('image')) {
            $url = 'public/houses_images';
            $url = Storage::put($url, $request->file('image'));
            // if(Storage::exists($house->floor_print_image)) {
            //     Storage::delete($house->floor_print_image);
            // }
            $house->floor_print_image = Storage::url($url);
        }

        $house->save();
        return response()->json('success');
    }


    // delete everything and return walls (only walls) generating walls from Computer vision
    public function regenerate(Request $request, $id) {
        $house = House::find($id);
        $response = Http::get(config('app.java_api_host').'/walls', [
            'binary' => $request->input('binary', 230),
            'mainpoints' => $request->input('mainpoints', 20),
            'tunepoints' => $request->input('tunepoints', 5),
            'createlines' => $request->input('createlines', 5),
            'img' => base_path() . '/public' . $house->floor_print_image
        ])->json()["lines"];
        
        $walls = $house->walls;
        for($i=0; $i<count($walls); ++$i)
            $walls[$i]->delete();
        
        $objects = $house->objects;
        for($i=0; $i<count($objects); ++$i)
            $objects[$i]->delete();

        $floors = $house->floors;
        for($i=0; $i<count($floors); ++$i)
            $floors[$i]->delete();

        for($i=0; $i<count($response); ++$i) {
            $startPnt = [
                'x' => $response[$i]["start"][0],
                'y' => $response[$i]["start"][1]
            ];
            $endPnt = [
                'x' => $response[$i]["end"][0],
                'y' => $response[$i]["end"][1]
            ];
            $wall = Wall::create([
                'thick' => 3,
                'start' => json_encode($startPnt),
                'end'   => json_encode($endPnt),
                'house_id' => $id
            ]);
        }

        return $this->wallsAndObjects($id);
    }

    // delete all breviouse meshes and walls and add new one that was sent
    public function edit(Request $request, $id) {

        $house = House::find($id);
        $houseJson = json_decode($request->getContent());

        // delete everything
        $objects = $house->objects;
        for($i=0; $i<count($objects); ++$i)
            $objects[$i]->delete();

        $walls = $house->walls;
        for($i=0; $i<count($walls); ++$i)
            $walls[$i]->delete();

        $floors = $house->floors;
        for($i=0; $i<count($floors); ++$i)
            $floors[$i]->delete();


        //re-construct everything from bottom up
        $walls = $houseJson->walls;
        for($i=0; $i<count($walls); ++$i) {
            $wall = Wall::create([
                'thick' => 3,
                'start' => json_encode($walls[$i]->start),
                'end'   => json_encode($walls[$i]->end),
                'house_id' => $id
            ]);
        }

        $objects = $houseJson->objects;
        for($i=0; $i<count($objects); ++$i) {
            $object = ObjectModel::create([
                'size' => $objects[$i]->size,
                'position' => json_encode($objects[$i]->position),
                'rotation' => $objects[$i]->rotation,
                'texture_id' => ($objects[$i]->texture == '')? null : $objects[$i]->texture,
                'mesh_id' => ($objects[$i]->mesh == '')? null : $objects[$i]->mesh,
                'house_id' => $id
            ]);
        }

        $floors = $houseJson->floors;
        for($i=0; $i<count($floors); ++$i) {
            $wall = Floor::create([
                'p1' => json_encode($floors[$i]->p1),
                'p2' => json_encode($floors[$i]->p2),
                'p3' => json_encode($floors[$i]->p3),
                'p4' => json_encode($floors[$i]->p4),
                'image_url' => $floors[$i]->image_url,
                'house_id' => $id
            ]);
        }

        return $this->wallsAndObjects($id);
    }


    public function test() {
        return view('test');
    }
}
