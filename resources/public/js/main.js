requirejs.config({
	baseUrl: "js",
	paths: {
		jquery: 'lib/jquery-2.0.3.min',
		ember: 'lib/ember',
		emberdata: 'lib/ember-data-beta2',
		handlebars: 'lib/handlebars-1.0.0',
		text: 'lib/text',
		moment: 'lib/moment.min'
	},
	shim: {
		'ember': {
			deps: ['jquery', 'handlebars']
		},
		'emberdata': {
			deps: ['ember']
		},
		'handlebars': {
			deps: ['jquery']
		},
	}
});

define(['routes'], function() {
	App.advanceReadiness();
});

