define(['application', 'models'], function(){

	App.Router.map(function(){
		this.resource("users", {path: '/users'});
	});

	App.UsersRoute = Ember.Route.extend({
		model: function() {
			return this.get('store').findAll('user');
		}
	});
});
