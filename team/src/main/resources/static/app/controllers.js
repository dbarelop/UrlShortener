angular.module("UrlShortenerApp.controllers", ["chart.js"]).controller("MetricsCtrl", function($scope, $location, MetricsService) {
    $scope.browsersChart = {
        labels: [],
        data: []
    };
    $scope.operatingSystemsChart = {
        labels: [],
        data: []
    };

    var hash = $location.absUrl().split(/[\s/]+/).pop();
    MetricsService.getMetrics(hash).then(null, null, function(data) {
        console.log(data);
        $scope.browsersChart.labels = Object.keys(data.clicksByBrowser);
        $scope.browsersChart.data = Object.values(data.clicksByBrowser);
        $scope.operatingSystemsChart.labels = Object.keys(data.clicksByOS);
        $scope.operatingSystemsChart.data = Object.values(data.clicksByOS);
    });
});
