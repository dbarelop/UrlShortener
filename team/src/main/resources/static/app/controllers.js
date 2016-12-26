angular.module("UrlShortenerApp.controllers", ["chart.js"]).controller("MetricsCtrl", function($scope, MetricsService) {
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
            $scope.browsersChart.labels = Object.keys(data.clicksByBrowser);
            $scope.browsersChart.data = Object.values(data.clicksByBrowser);
            $scope.operatingSystemsChart.labels = Object.keys(data.clicksByOS);
            $scope.operatingSystemsChart.data = Object.values(data.clicksByOS);
        }
    });
});
