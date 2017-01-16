angular.module("UrlShortenerApp.services")
    .service("MetricsService", function($q, $http, $timeout) {
        var service = {}, listener = $q.defer(), socket = {
            client: null,
            stomp: null
        };

        service.hash;
        service.startDate = null;
        service.endDate = null;

        service.RECONNECT_TIMEOUT = 30000;
        service.SOCKET_URL = "/metrics";
        service.connected = false;

        service.receive = function() {
            return listener.promise;
        };

        service.reconnect = function() {
            socket.stomp.disconnect();
            service.connected = false;
            service.initialize(service.hash);
            /*$timeout(function() {
              service.initialize(hash);
            }, this.RECONNECT_TIMEOUT);*/
        };

        var startListener = function() {
            socket.stomp.subscribe(service.CHAT_TOPIC, function(data) {
                listener.notify(JSON.parse(data.body));
            });
            service.connected = true;
            if (service.connected) {
                socket.stomp.send(service.CHAT_BROKER, { priority: 9 }, JSON.stringify({
                    hash: service.hash,
                    startDate: service.startDate,
                    endDate: service.endDate
                }));
            }
        };

        service.initialize = function(hash) {
            service.hash = hash;
            service.CHAT_TOPIC = "/topic/metrics/" + hash;
            service.CHAT_BROKER = "/app/metrics/" + hash;
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = service.reconnect;
        };

        return service;
    })
    
    .service("SuggestService", function($q, $http) {
    	var branded;

        var service = {}, listener = $q.defer(), socket = {
            client: null,
            stomp: null
        };

        service.SOCKET_URL = "/suggest";
        service.connected = false;
        
        service.getSuggestions = function(shortName) {
            if (service.connected) {
                socket.stomp.send(service.CHAT_BROKER, { priority: 9 }, JSON.stringify({ shortName: shortName }));
                return listener.promise;
            }
        };
        
        var startListener = function() {
            socket.stomp.subscribe(service.CHAT_TOPIC, function(data) {
                listener.notify(JSON.parse(data.body));
            });
            service.connected = true;
        };
                        
        service.initialize = function() {
            service.CHAT_TOPIC = "/topic/suggestions";
            service.CHAT_BROKER = "/app/suggestions";
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
        };

        return service;
    })
    
    .service("URLShortenerService", function($q, $http) {
        var service = {};

        service.shortenUrl = function(url, shortName, vcard, qrErrorLevel) {
            var deferred = $q.defer();
            var path = "/link", params = { params: { url: url, error: qrErrorLevel }};
            if (shortName) {
                path = "/brandedLink";
                params.params.shortName = shortName;
            }
            if (vcard.vcardName)  {
                params.params.vcardname = vcard.vcardName;
                params.params.vcardsurname = vcard.vcardSurname;
                params.params.vcardorganization = vcard.vcardOrganization;
                params.params.vcardtelephone = vcard.vcardTelephone;
                params.params.vcardemail = vcard.vcardEmail;
            }
            $http.post(path, null, params).then(function(data) {
                deferred.resolve(data.data);
            }, function(err) {
                deferred.reject(err.data);
            });
            return deferred.promise;
        };

        return service;
    })
    .service("UserLinksService", function($q, $http) {
        var service = {};

        service.getRules = function(hash) {
            var deferred = $q.defer();
            $http.get("/" + hash + "/rules").then(function(data) {
                deferred.resolve(data.data);
            }, function(err) {
                deferred.reject(err.data);
            });
            return deferred.promise;
        };

        service.addRule = function(hash, rule) {
            var deferred = $q.defer();
            $http.post("/" + hash + "/rules", rule).then(function(data) {
                deferred.resolve(data.data);
            }, function(err) {
                deferred.reject(err.data);
            });
            return deferred.promise;
        };

        service.updateRule = function(hash, rule) {
            var deferred = $q.defer();
            $http.put("/" + hash + "/rules/" + rule.id, rule).then(function() {
                deferred.resolve();
            }, function(err) {
                deferred.error(err.data);
            });
            return deferred.promise;
        };

        service.deleteRule = function(hash, rule) {
            var deferred = $q.defer();
            $http.delete("/" + hash + "/rules/" + rule.id).then(function() {
                deferred.resolve();
            }, function(err) {
                deferred.error(err.data);
            });
            return deferred.promise;
        };

        return service;
    });