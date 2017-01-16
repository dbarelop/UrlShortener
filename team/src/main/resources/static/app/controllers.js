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
        $scope.from = "";
        $scope.to = "";

        var displayMetrics = function(metrics) {
            if ($scope.metrics) {
                // New metrics
                var startDate = $scope.from ? new Date($scope.from) : null;
                var endDate = $scope.to ? new Date($scope.to) : null;
                if (endDate != null) {
                    endDate.setDate(endDate.getDate() + 1);     // Add one day to include selected
                }
                if ((startDate == null || startDate <= new Date(metrics.date)) && (endDate == null || endDate >= new Date(metrics.date))) {
                    $scope.metrics.clicks++;
                    var i = $scope.browsersChart.labels.indexOf(metrics.browser);
                    if (i == -1) {
                        // New browser
                        $scope.numBrowsers++;
                        $scope.browsersChart.labels.push(metrics.browser);
                        $scope.browsersChart.data.push(1);
                    } else {
                        $scope.browsersChart.data[i]++;
                    }
                    i = $scope.operatingSystemsChart.labels.indexOf(metrics.os);
                    if (i == -1) {
                        // New OS
                        $scope.numOSs++;
                        $scope.operatingSystemsChart.labels.push(metrics.os);
                        $scope.operatingSystemsChart.data.push(1);
                    } else {
                        $scope.operatingSystemsChart.data[i]++;
                    }
                    if (!metrics.lastVisitDate || (startDate != null && startDate > new Date(metrics.lastVisitDate))) {
                        // New visitor (or last visit date was before $scope.from)
                        $scope.metrics.uniqueVisitors++;
                    }
                }
            } else {
                // Initial metrics
                $scope.metrics = metrics;
                $scope.uri = $location.protocol() + "://" + $location.host() + ($location.port() != 80 ? ":" + $location.port() : "") + "/" + $scope.metrics.hash;
                $scope.numBrowsers = Object.keys($scope.metrics.clicksByBrowser).length;
                $scope.numOSs = Object.keys($scope.metrics.clicksByOS).length;
                $scope.browsersChart.labels = Object.keys($scope.metrics.clicksByBrowser);
                $scope.browsersChart.data = Object.values($scope.metrics.clicksByBrowser);
                $scope.operatingSystemsChart.labels = Object.keys($scope.metrics.clicksByOS);
                $scope.operatingSystemsChart.data = Object.values($scope.metrics.clicksByOS);
            }
        };

        var hash = $location.absUrl().split(/[\s/]+/).pop();
        if (hash != "metrics") {
            MetricsService.receive().then(null, null, function (data) {
                if (data.error) {
                    $scope.error = data.error;
                } else {
                    displayMetrics(data);
                }
            });
            MetricsService.initialize(hash, $scope.from, $scope.to);
        }

        $scope.newDateFilter = function() {
            $scope.metrics = null;
            MetricsService.startDate = $scope.from;
            MetricsService.endDate = $scope.to;
            MetricsService.reconnect();
        };
    })
    .controller("URLShortenerCtrl", function($scope, $location, URLShortenerService, SuggestService) {
    	$scope.needsSuggestion;
    	$scope.url = "";
        $scope.shortName = "";
        $scope.vcard = {
            vcardName: "",
            vcardSurname: "",
            vcardOrganization: "",
            vcardTelephone: "",
            vcardEmail: ""
        };
        $scope.qrErrorLevel = "";
        $scope.showVcard = false;
        $scope.showQr = false;

        $scope.shorten = function() {
            $scope.shortURL = "";
            $scope.error = "";
            URLShortenerService.shortenUrl($scope.url, $scope.shortName, $scope.vcard, $scope.qrErrorLevel).then(function(data) {
                $scope.shortURL = data;
                $scope.needsSuggestion = false;
                $scope.suggestResp = "";
				$scope.synonymResp = "";
				$scope.noResults = "";
            }, function(err) {
                $scope.error = "Unexpected error: " + err.error;
                $scope.needsSuggestion = true;
                getSuggestions($scope.shortName);
            });
        };

        $scope.getSuggestions = function(e) {
        	if ($scope.needsSuggestion) {
        		getSuggestions($scope.shortName + e.key);
        	}
        };

        $scope.backspaceEvent = function(e) {
            if (e.keyCode === 8 && $scope.needsSuggestion) {
                var x = $scope.shortName.substr(0, $scope.shortName.length);
                getSuggestions($scope.shortName.substr(0, $scope.shortName.length));
            }
        };

        $scope.setShortName = function(shortName) {
            $scope.shortName = shortName;
        };

        var getSuggestions = function(shortName) {
            if (shortName) {
                SuggestService.getSuggestions(shortName).then(null, null, function (suggestions) {
                    if (suggestions.error) {
                        $scope.suggestions_err = suggestions.error;
                        $scope.suggestions = "";
                    } else {
                        $scope.suggestions_err = "";
                        $scope.suggestions = suggestions;
                    }
                });
            } else {
                $scope.suggestions = "";
                $scope.suggestions_err = "";
            }
        };

        SuggestService.initialize();
    })
    .controller("UserLinksController", function($scope, UserLinksService) {

        $scope.initRules = function (hash) {
            $scope.hash = hash;
            UserLinksService.getRules($scope.hash).then(function (rules) {
                $scope.rules = rules;
                $scope.newRule = {
                    operation: "none"
                };
            }, function (err) {
                console.log(err);
            });
        };

        $scope.addRule = function () {
            UserLinksService.addRule($scope.hash, $scope.newRule).then(function () {
                $scope.rules.push($scope.newRule);
                $scope.newRule = {
                    operation: "none"
                };
            }, function (err) {
                console.log(err);
            });
        };
    });