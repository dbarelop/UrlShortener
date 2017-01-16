angular.module("UrlShortenerApp.controllers", ["chart.js","ui.bootstrap"] )
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
    .controller("URLShortenerCtrl", function($scope, $location, URLShortenerService, SuggestService) {
    	var needsSuggestion;
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
                needsSuggestion = false;
                $scope.suggestResp = "";
				$scope.synonymResp = "";
				$scope.noResults = "";
            }, function(err) {                                
                if (true) { // TODO compare error receive is for name or url                	
                	needsSuggestion = true;
					getSuggestions($scope.shortName);
					$scope.noResults = "Short Name Already Exist";
				} else {
					$scope.error = "Unexpected error: " + err.data;
				}
                
            });
        };

        $scope.getSuggestions = function(e) {
        	if (needsSuggestion) {
        		getSuggestions($scope.shortName + e.key);
        	}        	
        };

        $scope.backspaceEvent = function(e) {
            if (e.keyCode === 8 && needsSuggestion) {
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
        
    });