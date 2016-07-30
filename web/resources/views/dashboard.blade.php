@extends('app')

@section('title', 'Dashboard')

@push('meta')
    <meta http-equiv="refresh" content="3">
@endpush

@section('content')
    <main class="row dashboard">
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/fall.png') }}">
            </div>
            <div class="alert alert-success">
                <h4>Test</h4>
                <p>Testing!</p>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/pill.png') }}">
            </div>
            <div class="alert alert-warning">
                <h4>Test</h4>
                <p>Testing!</p>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/door.png') }}">
            </div>
            <div class="alert alert-danger">
                <h4>Test</h4>
                <p>Testing!</p>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/stove.png') }}">
            </div>
            <div class="alert alert-info">
                <h4>Test</h4>
                <p>Testing!</p>
            </div>
        </div>
    </main>
@endsection
