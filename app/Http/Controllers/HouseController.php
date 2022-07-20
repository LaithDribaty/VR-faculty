<?php

namespace App\Http\Controllers;

use App\Models\House;
use Illuminate\Http\Request;

class HouseController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $per_page = $request->input('per_page', 10);
        $q = $request->input('q');
        $price = [
            'min' => $request->input('minPrice', 0),
            'max'=> $request->input('maxPrice', 2000)
        ];
        $size = [
            'min' => $request->input('minSize', 60),
            'max'=> $request->input('maxSize', 300)
        ];

        $house = House::query();
        if($q) {
            $house->where('location', 'LIKE', '%'.$q.'%');
        }

        if($price) {
            $house->whereBetween('price', [ $price['min'], $price['max'] ]);
        }

        if($size) {
            $house->whereBetween('size', [ $size['min'], $size['max'] ]);
        }

        $houses = $house->paginate($per_page)->withQueryString();
        return response()->json($houses);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        //
    }
}
