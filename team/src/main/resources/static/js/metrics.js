$(document).ready(
    function() {
        $("#button").click(function(event) {
            event.preventDefault();
            $.get("/metrics/" + $("#hash").val(), function(msg) {
                $("#result").html(
                    "<div class='panel panel-default'>" +
                        "<div class='panel-heading'><h3><a href='" + msg.uri + "'>" + msg.uri + "</a></h3></div>" +
                        "<div class='panel-body'>" +
                            "<ul class='list-group'>" +
                                "<li class='list-group-item'><strong>Clicks</strong> <span class='badge'>" + msg.clicks + "</span></li>" +
                                "<li class='list-group-item'><strong>Creation date:</strong> " + msg.created + "</li>" +
                                "<li class='list-group-item'><strong>Target URL:</strong> <a href='" + msg.target + "'>" + msg.target + "</a></li>" +
                            "</ul>" +
                        "</div>" +
                    "</div>");
            }).fail(function() {
                $("#result").html("<div class='alert alert-danger lead'>ERROR</div>");
            });
        });
    });