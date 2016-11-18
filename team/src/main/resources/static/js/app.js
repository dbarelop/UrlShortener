$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                        $("#resultQR").html(
                            "<button id='buttonQR' class='btn btn-lg btn-primary' type='submit'>QR Code</button>"
                            );

                        $('#buttonQR').click(function() {
                             $("#QRImage").html(
                                 "<img src='https://chart.googleapis.com/chart?cht=qr&chl="+msg.uri+"&choe=UTF-8&chs=177x177'></img>"
                                 );
                        });

                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>"
                                );
                    }
                });
            });
    });