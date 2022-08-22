@extends('base')

@section('main')

    <form method="POST" class="container" enctype="multipart/form-data">
        @csrf
        <div class="my-2">
            <label>name</label>
            <input type="text" name="name">
        </div>

        <div class="my-2">
            front image
            <input type="file" name="front_image">
        </div>

        <div class="my-2">
            up image
            <input type="file" name="up_image">
        </div>

        <div class="my-2">
            glb file
            <input type="file" name="glb">
        </div>

        <input type="submit" value="submit" class="btn btn-primary">
    </form>

@endsection
