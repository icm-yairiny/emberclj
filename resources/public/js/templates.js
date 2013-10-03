define(
	[
	'ember',
	'text!templates/application.html',
	'text!templates/users.html',
	], 
function(x,
	application,
	users
	)
{
	Ember.TEMPLATES['application'] = Ember.Handlebars.compile(application);
	Ember.TEMPLATES['users'] = Ember.Handlebars.compile(users);
});
