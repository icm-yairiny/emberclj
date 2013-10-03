define(['application'], function() {
	App.ApplicationAdapter = DS.RESTAdapter.extend({
		namespace: 'data'
	});

	var attr = DS.attr;

	App.User = DS.Model.extend({
		firstName: attr(),
		lastName: attr(),
		middleNames: attr(),
		email: attr(),
		userType: attr()
	});

});
