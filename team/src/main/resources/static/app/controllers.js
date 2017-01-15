angular.module("UrlShortenerApp.controllers", ["chart.js"])
    .controller("MetricsCtrl", function($scope, $location, MetricsService) {
        $scope.browsersChart = {
            labels: [],
            data: []
        };
        $scope.operatingSystemsChart = {
            labels: [],
            data: []
        };

        var hash = $location.absUrl().split(/[\s/]+/).pop();
        if (hash != "metrics") {
            MetricsService.receive().then(null, null, function (data) {
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
            MetricsService.initialize(hash);
        }
    })
    .controller("URLShortenerCtrl", function($scope, $location, URLShortenerService) {
        $scope.url = "";
        $scope.brandedLink = "";
        $scope.vcard = {
            vcardName: "",
            vcardSurname: "",
            vcardOrganization: "",
            vcardTelephone: "",
            vcardEmail: ""
        };
        $scope.qrErrorLevel = "";
        $scope.shorten = function() {
            $scope.shortURL = "";
            $scope.error = "";
            URLShortenerService.shortenUrl($scope.url, $scope.brandedLink, $scope.vcard, $scope.qrErrorLevel).then(function(data) {
                $scope.shortURL = data;
            }, function(err) {
                $scope.error = "Unexpected error: " + err.data;
            });
        };
        $scope.showVcard = false;
        $scope.showQr = false;
    })
    .controller("UserLinksController", function($scope, UserLinksService) {

        $scope.initRules = function(hash) {
            $scope.hash = hash;
            UserLinksService.getRules($scope.hash).then(function(rules) {
                $scope.rules = rules;
                $scope.newRule = {
                    operation: "none"
                };
            }, function(err) {
                console.log(err);
            });
        };

        $scope.addRule = function() {
            UserLinksService.addRule($scope.hash, $scope.newRule).then(function() {
                $scope.rules.push($scope.newRule);
                $scope.newRule = {
                    operation: "none"
                };
            }, function(err) {
                console.log(err);
            });
        };
    });
