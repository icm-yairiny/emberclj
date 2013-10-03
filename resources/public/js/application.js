define(['templates', 'emberdata'],function(){
	window.App = Ember.Application.create({
		LOG_TRANSITIONS: true
	});
	App.deferReadiness();
});
