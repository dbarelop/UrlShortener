angular.module("UrlShortenerApp.services")
    .service("MetricsService", function($q, $http, $timeout) {
        var service = {}, listener = $q.defer(), socket = {
            client: null,
            stomp: null
        };

        var hash;

        service.RECONNECT_TIMEOUT = 30000;
        service.SOCKET_URL = "/metrics";
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

        service.initialize = function(hash) {
            service.CHAT_TOPIC = "/topic/metrics/" + hash;
            service.CHAT_BROKER = "/app/metrics/" + hash;
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = reconnect;
        };

        return service;
    })
    .service("URLShortenerService", function($q, $http) {
        var service = {};

        service.shortenUrl = function(url, brandedLink, vcardname, vcardsurname, vcardorganization,
        vcardtelephone, vcardemail) {
            var deferred = $q.defer();
            var path = "/link", params = { params: { url: url }};
            if (brandedLink) {
                path = "/brandedLink";
                params.params.shortName = brandedLink;
            }
            if (vcardname)  {
                params.params.vcardname = vcardname;
                params.params.vcardsurname = vcardsurname;
                params.params.vcardorganization = vcardorganization;
                params.params.vcardtelephone = vcardtelephone;
                params.params.vcardemail = vcardemail;
            }
            $http.post(path, null, params).then(function(data) {
                deferred.resolve(data.data);
            }, function(err) {
                deferred.reject(err);
            });
            return deferred.promise;
        };

        return service;
    });