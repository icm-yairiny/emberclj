define(['application'], function() {
	App.ApplicationAdapter = DS.RESTAdapter.extend({
		namespace: 'data'
	});

	var attr = DS.attr;

{{#models}}
    App.{{name}} = DS.Model.extend({
        {{#attributes}}
        {{single}}: attr(),
        {{/attributes}}
{{#children}}
{{plural}}: DS.hasMany('{{single}}', {async: true}),
{{/children}}
{{#parents}}
{{single}}: DS.belongsTo('{{single}}'),
{{/parents}}
    });
{{/models}}
});
