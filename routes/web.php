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

Route::get('/house/{id}/map', [ App\Http\Controllers\MapController::class, 'index'])->name('map');
Route::post('/house/{id}/map/image', [ App\Http\Controllers\MapController::class, 'uploadImage'])->name('house.map.image');
Route::get('/house/{id}/wall', [ App\Http\Controllers\MapController::class, 'walls'])->name('house.walls');
Route::post('/house/{id}/wall', [ App\Http\Controllers\MapController::class, 'editWalls'])->name('house.walls.edit');
Route::get('/house/{id}/wall/generate', [ App\Http\Controllers\MapController::class, 'generateWalls'])->name('house.walls.generate');


Route::get('/', function () {
    return view('welcome');
});


