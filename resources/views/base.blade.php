<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <meta name="csrf-token" content="{{ csrf_token() }}">
        
        <title>Homepage</title>
        <meta name="description" content="">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
        <style>
            .mainbg {
                background: #DAE2F8;
            }
        </style>
        <link rel="shortcut icon" href="/images/home-logo-favicon.png" type="image/x-icon">
    </head>
    <body class="mainbg">

        <div id="app">
            <a href="#headerItem" id="scrollToTopItem" class="position-fixed bottom-0 end-0 me-2 mb-2 bg-dark text-white text-decoration-none p-3 pb-2 border border-light rounded" style="z-index: 1000;">
                ^
            </a>
            <navbar></navbar>

            @yield('main')
        </div>

        @routes
        <script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
        <script src="{{ asset('js/app.js') }}"></script>

        <script type="text/javascript">
            $(document).ready(function() {
                $(window).scroll(function() {
                    if ($(this).scrollTop() > 20) {
                        $('#scrollToTopItem').fadeIn();
                    } else {
                        $('#scrollToTopItem').fadeOut();
                    }
                });
            });
        </script>
    </body>
</html>
