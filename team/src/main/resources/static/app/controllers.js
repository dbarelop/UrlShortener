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
    	var needSuggest;
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
                needSuggest = false;
                $scope.suggestResp = "";
				$scope.synonymResp = "";
				$scope.noResults = "";
            }, function(err) {                                
                if (true) { // TODO compare error receive is for name or url                	
                	needSuggest = true;
					suggest();
					$scope.noResults = "Short Name Already Exist";
				}else{
					$scope.error = "Unexpected error: " + err.data;
				}
                
            });
        };
        $scope.showVcard = false;
        $scope.showQr = false;
        
      
        $scope.suggestion = function() {
        	if (needSuggest) {
        		suggest();
        	}        	
        }
        
        $scope.add = function(val) {
        	$scope.brandedLink = val;    	
        }
        
        $scope.CollectionSuggest= {0: {su: ""} };
        $scope.CollectionSynonyms= {0: {sy: ""} };
              
        var suggest = function() {
        	if ($scope.brandedLink !== "") { 		
        		SuggestService.initialize($scope.brandedLink);
        		SuggestService.receive().then(null, null, function (data) {
        			if (data.error) {
        				$scope.noResults = "No Results Found";
        				$scope.suggestResp = "";
        				$scope.synonymResp = "";
        			} else {
        				$scope.noResults = "";
        				if (data.suggestion.length>0) {
							$scope.suggestResp = "Suggests";
							angular.forEach(data.suggestion, function(value, key){        				           				     
        				    $scope.CollectionSuggest[key] = { "sug": value };    				             				    
							});
						}else{
							$scope.suggestResp = "";
						}
        				if (data.synonyms.length>0) {
        					$scope.synonymResp = "Synonyms";
        					angular.forEach(data.synonyms, function(value, key){
            					$scope.CollectionSynonyms[key] = { "syn": value };
            				});
						}else{
							$scope.synonymResp = "";
						} 				 
        			}
        		});
        	}else{
        		$scope.suggestResp = "";
				$scope.synonymResp = "";
				$scope.noResults = "";
        	}
        } 
               
    });