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
        $body = $request->getContent();
        Storage::append('messages', $body . "\n");
        return '';
    }
}
