<?php

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/test', [ App\Http\Controllers\MapController::class, 'test']); // here

Route::get('/mesh/add', [ App\Http\Controllers\MeshController::class, 'addPage'])->name('addmeshpage');
Route::post('/mesh/add', [ App\Http\Controllers\MeshController::class, 'add'])->name('addmesh');
Route::get('/mesh/all', [ App\Http\Controllers\MeshController::class, 'all'])->name('getMeshs');

Route::get('/house/{id}/map', [ App\Http\Controllers\MapController::class, 'index'])->name('map');
Route::get('/house/{id}/three-d', [ App\Http\Controllers\MapController::class, 'threeD'])->name('three-d');
Route::post('/house/{id}/map/image', [ App\Http\Controllers\MapController::class, 'uploadImage'])->name('house.map.image');

Route::get('/house/{id}/object', [ App\Http\Controllers\MapController::class, 'wallsAndObjects'])->name('house.objects');
Route::post('/house/{id}/object', [ App\Http\Controllers\MapController::class, 'edit'])->name('house.objects.edit');
Route::get('/house/{id}/regenerate', [ App\Http\Controllers\MapController::class, 'regenerate'])->name('house.regenerate');

Route::get('/', function () {
    return view('welcome');
});

