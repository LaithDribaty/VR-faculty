<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Mesh;
use Illuminate\Support\Facades\Storage;

class MeshController extends Controller
{
    //
    public function addPage()
    {
        return view('addmeshpage');
    }

    public function add(Request $request)
    {

        if ($request->hasFile('front_image') && $request->hasFile('up_image') && $request->hasFile('glb')) {
            $name = $request->input('name');
            Storage::putFileAs('/public/meshes/images', $request->file('front_image'), $name.'front.png');
            Storage::putFileAs('/public/meshes/images', $request->file('up_image'), $name.'up.png');
            $url = Storage::putFileAs('/public/meshes/bin', $request->file('glb'), $name.'.glb');

            Mesh::create([
                'name' => $name,
                'size' => $request->file('glb')->getSize(),
                'url' => $url
            ]);

        }

        return redirect('addmeshpage');
    }


    public function all() 
    {
        $meshs = Mesh::all();
        return response()->json($meshs);
    }
}
