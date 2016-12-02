angular.module("UrlShortenerApp.services").service("MetricsService", function($q, $http) {
    var service = {}, listener = $q.defer();

    service.getMetrics = function(hash) {
        $http.get('http://localhost:8080/metrics/' + hash).then(function(res) {
            listener.notify(res.data);
        }, function(err) {
            console.log(err);
        });
        return listener.promise;
    };

    return service;
});