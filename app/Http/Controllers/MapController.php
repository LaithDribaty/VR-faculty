<?php

namespace App\Http\Controllers;

use App\Models\House;
use App\Models\Wall;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Http;

class MapController extends Controller
{
    //
    public function index($id) 
    {
        $house = House::find($id);

        return view('map', [
            'house' => $house
        ]);
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

    public function walls($id)
    {
        $walls = House::find($id)->walls;
        return response()->json($walls);
    }

    public function editWalls(Request $request, $id)
    {
        $walls = House::find($id)->walls;
        for($i=0; $i<count($walls); ++$i)
            $walls[$i]->delete();

        $walls = json_decode($request->getContent())->walls;
        for($i=0; $i<count($walls); ++$i) {
            $wall = Wall::create([
                'thick' => 3,
                'start' => json_encode($walls[$i]->start),
                'end'   => json_encode($walls[$i]->end),
                'house_id' => $id
            ]);
        }

        return $this->walls($id);
    }

    public function generateWalls(Request $request, $id)
    {
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

        return $this->walls($id);
    }
}
