define(['application'], function() {
	App.ApplicationAdapter = DS.RESTAdapter.extend({
		namespace: 'data'
	});

	var attr = DS.attr;

	App.User = DS.Model.extend({
		username: attr(),
		accounts: DS.hasMany('account', {async: true})
	});

	App.Account = DS.Model.extend({
		name: attr()
	});


	//HELLO WORLD

});
