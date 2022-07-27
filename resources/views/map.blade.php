@extends('base')

@section('main')

    <hosue-map image="{{ $house->floor_print_image }}" house_id="{{ $house->id }}" />

@endsection
