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
            @if (isset($fallTime))
                <div class="alert alert-danger">
                    <h4>Fall Alarm</h4>
                    <p>Grandma fell <strong><em>{{ $fallTime->tz('Australia/Melbourne')->diffForHumans() }}</em></strong>.</p>
                </div>
            @else
                <div class="alert alert-success">
                    <h4>Fall Alarm</h4>
                    <p>Nothing to worry about!</p>
                </div>
            @endif
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/pill.png') }}">
            </div>
            @if (!isset($pillTime) OR $pillTime->lt(Carbon\Carbon::now()->subMinute(1)))
                <div class="alert alert-warning">
                    <h4>Pill Schedule</h4>
                    <p>Grandma hasn't taken her pills today.</p>
                </div>
            @else
                <div class="alert alert-success">
                    <h4>Pill Schedule</h4>
                    <p>Nothing to worry about!</p>
                </div>
            @endif
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/door.png') }}">
            </div>
            @if (isset($doorAlarmTime))
                <div class="alert alert-danger">
                    <h4>Door Alarm</h4>
                    <p>Grandma left the door open at least <strong><em>{{ $doorAlarmTime->tz('Australia/Melbourne')->subHours(1)->diffForHumans() }}</em></strong>.</p>
                </div>
            @elseif (isset($doorOpen) AND $doorOpen)
                <div class="alert alert-info">
                    <h4>Door Status</h4>
                    <p>Grandma opened the door <strong><em>{{ $doorTime->tz('Australia/Melbourne')->diffForHumans() }}</em></strong>.</p>
                </div>
            @else
                <div class="alert alert-success">
                    <h4>Door Status</h4>
                    <p>Door is closed - nothing to worry about!</p>
                </div>
            @endif
        </div>
        <div class="col-sm-3">
            <div class="text-center">
                <img src="{{ asset('images/stove.png') }}">
            </div>
            @if (isset($stoveTime))
                <div class="alert alert-danger">
                    <h4>Stove Alarm</h4>
                    <p>Grandma turned on the stove at least <strong><em>{{ $stoveTime->tz('Australia/Melbourne')->subHours(1)->diffForHumans() }}</em></strong>.</p>
                </div>
            @else
                <div class="alert alert-success">
                    <h4>Stove Alarm</h4>
                    <p>Nothing to worry about!</p>
                </div>
            @endif
        </div>
    </main>
@endsection
