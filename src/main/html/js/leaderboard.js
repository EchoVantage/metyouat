angular.module('MetYouAt', ['ngResource']);

function MyaStream($scope) {
	$scope.statuses = [
			{avatar: 'https://pbs.twimg.com/profile_images/378800000241505326/984d58129e98651947cca7ce38bc409b_normal.jpeg',
			 sender: 'Dennis Smolek',
			 content: '#IMet @mikedeck today'},
			{avatar: 'https://pbs.twimg.com/profile_images/378800000506746994/dcef7ed5fbe93860c74b892311f74aa4_normal.jpeg',
			 sender: 'Sarah Kayfus',
			 content: '@fuwjax good to meet you at the Thanksgiving party #CommonDesk #IMet'}
		];
}