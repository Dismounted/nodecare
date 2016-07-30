<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Storage;

class TriggerController extends Controller
{
    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $body = trim($request->getContent());

        switch ($body) {
            case 'fall':
                Storage::put('fall', time());
                break;
            case 'pill':
                Storage::put('pill', time());
                break;
            case 'door0':
                Storage::put('door', 'opened,' . time());
                break;
            case 'door1':
                Storage::put('door', 'closed,' . time());
                break;
            case 'doora':
                Storage::put('doora', time());
                break;
            case 'temp':
                Storage::put('temp', time());
                break;
        }

        return '';
    }
}
