angular.module('MetYouAt', []).filter('fileSuffix',function(){
	return function(text, suffix){
		return text.substring(0,text.lastIndexOf('.')) + '_' + suffix + text.substring(text.lastIndexOf('.'));
	}
});

/*
function MyaStream($scope) {
	$scope.statuses = [
			{avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_normal.jpeg',
			 sender: 'Jeremy Krakowski',
			 content: '#IMet @sarahkayfus #hottie #flexin #smellsgood'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_normal.jpeg',
			 sender: 'Jeremy Krakowski',
			 content: '#IMet @sarahkayfus #hottie #flexin #smellsgood'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_normal.jpeg',
			 sender: 'Jeremy Krakowski',
			 content: '#IMet @sarahkayfus #hottie #flexin #smellsgood'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_normal.jpeg',
			 sender: 'Jeremy Krakowski',
			 content: '#IMet @sarahkayfus #hottie #flexin #smellsgood'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_normal.jpeg',
			 sender: 'Jeremy Krakowski',
			 content: '#IMet @sarahkayfus #hottie #flexin #smellsgood'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'}
		];
}

function MyaLeaderboard($scope) {
	$scope.leaders = [
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000543791541/3ffcc8261669352c5e5992945c8d6014_bigger.jpeg',
			name: 'Mike Deck',
			points: 57,
			connections: 12
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000735535433/13f2f2651c5ce46090dcc7836e75362d_bigger.jpeg',
			name: 'Mike Doberenz',
			points: 53,
			connections: 12
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_bigger.jpeg',
			name: 'Sarah Kayfus',
			points: 43,
			connections: 7
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_bigger.jpeg',
			name: 'Dennis Smolek',
			points: 38,
			connections: 16
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/3318020339/baf83ef0f75702c87bc9b7a73938e886_bigger.jpeg',
			name: 'Elizabeth Adams',
			points: 29,
			connections: 12
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/3114998047/312221bc500181c10a6da9989be04530_bigger.jpeg',
			name: 'Aaron King',
			points: 28,
			connections: 11
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_bigger.jpeg',
			name: 'Jeremy Krakowski',
			points: 10,
			connections: 1
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000244234387/bac76d8acf41e69d3bca999380481d9e_bigger.png',
			name: 'Arturo Torres',
			points: 29,
			connections: 12
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/3114998047/312221bc500181c10a6da9989be04530_bigger.jpeg',
			name: 'Aaron King',
			points: 28,
			connections: 11
		},
		{
			avatar: 'https://pbs.twimg.com/profile_images/378800000723461847/0b527619cc7ed2c0fc74524cc5771b53_bigger.jpeg',
			name: 'Jeremy Krakowski',
			points: 10,
			connections: 1
		}
	];
}

*/
	
function MyaStream($scope, $timeout, $http) {
	var refreshFeed = function(){
		$http.get('/api/feed/CommonDesk').success(function(data){
			$scope.feed = data;
		});
		$timeout(refreshFeed, 10000);
	}
	refreshFeed();
}

function MyaLeaderboard($scope, $timeout, $http) {
	var refreshLeaderboard = function(){
		$http.get('/api/leaderboard/CommonDesk').success(function(data){
			$scope.leaderboard = data;
		});
		$timeout(refreshLeaderboard, 10000);
	}
	refreshLeaderboard();
}