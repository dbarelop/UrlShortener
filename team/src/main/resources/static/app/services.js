angular.module("UrlShortenerApp.services").service("MetricsService", function($q, $http, $timeout, $location) {
    var service = {}, listener = $q.defer(), socket = {
        client: null,
        stomp: null
    };

    var hash = $location.absUrl().split(/[\s/]+/).pop();

    service.RECONNECT_TIMEOUT = 30000;
    service.SOCKET_URL = "/metrics";
    service.CHAT_TOPIC = "/topic/metrics/" + hash;
    service.CHAT_BROKER = "/app/metrics/" + hash;
    service.connected = false;

    service.receive = function() {
        return listener.promise;
    };

    service.send = function() {
        if (service.connected) {
            socket.stomp.send(service.CHAT_BROKER, { priority: 9 }, JSON.stringify({
                hash: hash,
                startDate: null,
                endDate: null
            }));
        }
    };

    var reconnect = function() {
        service.connected = false;
        $timeout(function() {
            initialize();
        }, this.RECONNECT_TIMEOUT);
    };

    var startListener = function() {
        socket.stomp.subscribe(service.CHAT_TOPIC, function(data) {
            listener.notify(JSON.parse(data.body));
        });
        service.connected = true;
        service.send();
    };

    var initialize = function() {
        socket.client = new SockJS(service.SOCKET_URL);
        socket.stomp = Stomp.over(socket.client);
        socket.stomp.connect({}, startListener);
        socket.stomp.onclose = reconnect;
    };

    initialize();

    return service;
});