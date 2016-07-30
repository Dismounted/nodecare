<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="csrf-token" content="{{ csrf_token() }}">

        @stack('meta')

        <title>@yield('title') &middot; NodeCare</title>

        <link rel="icon" type="image/png" href="{{ asset('favicon-32x32.png') }}" sizes="32x32">
        <link rel="icon" type="image/png" href="{{ asset('favicon-16x16.png') }}" sizes="16x16">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootswatch/3.3.6/superhero/bootstrap.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poiret+One">
        <link rel="stylesheet" href="{{ asset('css/app.css') }}">

        @stack('css')

        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <nav class="navbar navbar-default navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-links" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="{{ route('home') }}"><img src="{{ asset('images/logo.png') }}" height="40" width="40" alt="NodeCare Logo" title="NodeCare"></a>
                </div>
                <div class="collapse navbar-collapse" id="navbar-links">
                    <ul class="nav navbar-nav">
                        <li id="nav-brand-name">
                            <a href="#">NodeCare</a>
                        </li>
                        <li @if (starts_with(Request::route()->getName(), 'dashboard')) class="active" @endif>
                            <a href="{{ route('dashboard') }}">Dashboard</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="container">
            @if (Session::has('message'))
                <div class="row">
                    <div class="col-sm-12">
                        <div class="fade in alert alert-dismissable alert-{{ Session::get('message.type', 'info') }}">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                            {{ Session::get('message.text') }}
                        </div>
                    </div>
                </div>
            @endif

            @yield('content')

            <footer class="row">
                <div class="col-sm-12">
                    <hr>
                    <p class="text-center">
                        <em>Smarter care.</em><br>
                        Copyright &copy; <a href="{{ route('home') }}">NodeCare</a> {{ Carbon\Carbon::now('Australia/Melbourne')->year }}
                    </p>
                </div>
            </footer>
        </div>

        <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
        <script src="{{ asset('js/app.js') }}"></script>

        @stack('js')
    </body>
</html>
