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

        // llamado inicialmente para probar la respuesta con websockets
        $scope.suggest = function() {  	
        	if ($scope.brandedLink !== "") {        		
        		SuggestService.receive().then(null, null, function (data) {
        			if (data.error) {
        				$scope.error = data.error;
        			} else {        				
        				$scope.shortURL  = data;        			
        			}
        		});
        		SuggestService.initialize($scope.brandedLink);
        	}else{
        		$scope.shortURL = "";
        	}
        } 
        
        //con este estaba probando el typeahead
        $scope.getSuggest = function(val){
        	SuggestService.initialize(val);
        	SuggestService.receive().then(null, null, function (data) {
    			return data;
    		});
        }
        
    });