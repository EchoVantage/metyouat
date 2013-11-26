angular.module('MetYouAt', []);

function MyaStream($scope, $timeout, $http) {
	$timeout(function(){
		$http.get('/api/feed/CommonDesk').success(function(data){
			$scope.feed = data;
		});
	}, 1000);
}

function MyaLeaderboard($scope, $timeout, $http) {
	$timeout(function(){
		$http.get('/api/leaderboard/CommonDesk').success(function(data){
			$scope.leaderboard = data;
		});
	}, 1000);
}