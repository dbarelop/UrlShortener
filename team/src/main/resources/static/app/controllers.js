angular.module("UrlShortenerApp.controllers", ["chart.js"]).controller("MetricsCtrl", function($scope, $location, MetricsService) {
    $scope.browsersChart = {
        labels: [],
        data: []
    };
    $scope.operatingSystemsChart = {
        labels: [],
        data: []
    };

    MetricsService.receive().then(null, null, function(data) {
        if (data.error) {
            $scope.error = data.error;
        } else {
            $scope.metrics = data;
            $scope.uri = $location.protocol() + "://" + $location.host() + ($location.port() != 80 ? ":" + $location.port() : "") + "/" + data.hash;
            $scope.numBrowsers = Object.keys(data.clicksByBrowser).length;
            $scope.numOSs = Object.keys(data.clicksByOS).length;
            $scope.browsersChart.labels = Object.keys(data.clicksByBrowser);
            $scope.browsersChart.data = Object.values(data.clicksByBrowser);
            $scope.operatingSystemsChart.labels = Object.keys(data.clicksByOS);
            $scope.operatingSystemsChart.data = Object.values(data.clicksByOS);
        }
    });
});
