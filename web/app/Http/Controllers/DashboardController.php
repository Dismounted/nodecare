<?php

namespace App\Http\Controllers;

use Storage;
use Carbon\Carbon;

class DashboardController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $fallTime = Storage::exists('fall') ? Carbon::createFromTimestamp(trim(Storage::get('fall'))) : null;
        $pillTime = Storage::exists('pill') ? Carbon::createFromTimestamp(trim(Storage::get('pill'))) : null;
        $stoveTime = Storage::exists('temp') ? Carbon::createFromTimestamp(trim(Storage::get('temp'))) : null;
        $doorAlarmTime = Storage::exists('doora') ? Carbon::createFromTimestamp(trim(Storage::get('doora'))) : null;

        if (Storage::exists('door')) {
            $door = explode(',', trim(Storage::get('door')));
            $doorOpen = ($door[0] == 'opened') ? true : false;
            $doorTime = Carbon::createFromTimestamp($door[1]);
        } else {
            $doorOpen = false;
            $doorTime = null;
        }

        return view('dashboard', compact('fallTime', 'pillTime', 'stoveTime', 'doorAlarmTime', 'doorOpen', 'doorTime'));
    }

    /**
     * Dismiss an alarm
     *
     * @return \Illuminate\Http\Response
     */
    public function dismiss($type)
    {
        switch ($type) {
            case 'fall':
                Storage::delete('fall');
                break;
            case 'doora':
                Storage::delete('doora');
                break;
            case 'temp':
                Storage::delete('temp');
                break;
        }

        return redirect('dashboard');
    }
}
