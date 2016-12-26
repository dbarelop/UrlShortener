angular.module("UrlShortenerApp.services").service("MetricsService", function($q, $http) {
    var service = {}, listener = $q.defer();

    service.getMetrics = function(hash) {
        var config = { headers: { "Accept": "application/json" }};
        $http.get('http://localhost:8080/api/metrics/' + hash, config).then(function(res) {
            listener.notify(res.data);
        }, function(err) {
            console.log(err);
        });
        return listener.promise;
    };

    return service;
});