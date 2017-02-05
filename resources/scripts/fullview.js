(function() {
	var view = document.querySelector('#fullview-page');
	var header = view.querySelector('#fullview-header');
	var itemImage = header.querySelector('img');
	var itemName = header.querySelector('h1');
	var itemPrice = header.querySelector('#fullview-price');
	var itemDescription = document.querySelector('#fullview-description');
	var itemSpecs = document.querySelector('#fullview-specs');

	window.registerView('fullview');

	if(!window.modules) {
		window.modules = {};
	}

	var rawList;

	window.modules['fullview'] = {
		init: function() {
			console.log('initialisation du module fullview');
		},
		/**
		* Load the login view.
		* @param params An object that contains the parameters
		* mode: 'login' | 'register'
		*/
		load: function(params) {
			view.classList.remove('hide');
			view.style['z-index'] = getNextZIndex();
			modules.help.pushContext('fullview');

			modules.menu.pushBackArrow(function() {
				modules.fullview.unload();
			});

			API.getItem(params).then(function(data) {
				modules.fullview.print(data.data.item);
			}).catch(function(data) {
				modules.fullview.unload();
			});
		},
		unload: function() {
			modules.menu.popBackArrow();
			view.classList.add('hide');
			modules.help.popContext();
		},
		print: function(data) {
			API.getImageUrl(data.image).then(function(data) {
				itemImage.src = data.replace('resources/', '');
			});
			itemName.innerHTML = data.name;
			itemDescription.innerHTML = data.description;
			itemSpecs.innerHTML = '';

			for(var i = 0; i < data.categories.length; ++i) {
				(function(category) {
					var container = document.createElement('li');
					var title = document.createElement('div');
					var content = document.createElement('ul');

					container.classList.add('category');
					title.classList.add('category-name');
					content.classList.add('category-content');

					title.innerHTML = category.name === '_self_' ? 'Miscellaneous' : category.name;

					for(var j = 0; j < category.criteria.length; ++j) {
						(function(criterion) {
							var item = document.createElement('li');
							var name = document.createElement('div');
							var value = document.createElement('div');

							item.classList.add('criterion');
							name.classList.add('criterion-name');
							value.classList.add('criterion-value');

							name.innerHTML = criterion.name + ' : ';
							value.innerHTML = modules.fullview.formatData(criterion.value, modules.fullview.getItemUniverse(criterion.criterionModel));
							if(criterion.name == 'price') {
								itemPrice.innerHTML = value.innerHTML + '€ per day';
							}

							item.appendChild(name);
							item.appendChild(value);

							content.appendChild(item);
						})(category.criteria[j]);
					}

					container.appendChild(title);
					container.appendChild(content);

					itemSpecs.appendChild(container);

				})(data.categories[i]);
			}
		},
		getItemUniverse: function(id) {
			for(var i = 0; i < model.model.length; ++i) {
				for(var j = 0; j < model.model[i].criteria.length; ++j) {
					if(model.model[i].criteria[j]['_id'] === id) {
						return model.model[i].criteria[j];
					}
				}
			}
		},
		formatData: function(data, universe) {
			function findWeightedString(data, id) {
				for(var i = 0; i < data.length; ++i) {
					if(data[i].value == id) {
						return data[i].name;
					}
				}
			}

			switch(universe.itemType) {
				case 0: // Integer
					switch(universe.universeType) {
						case 3: // integer interval
							return data;
						case 6: // weighted string list
							return findWeightedString(universe.universe.values, data);
						default:
							console.log('unhandled integer type : ' + universe.universeType);
					}
					break;
				case 1: // float
					switch(universe.universeType) {
						case 4: // float interval
							return data;
						default:
							console.log('unhandled float type : ' + universe.universeType);
					}
					break;
				case 2: // Boolean
					return data ? 'Yes' : 'No';
				case 3: // Integer interval
					switch(universe.universeType) {
						case 3: // integer interval
							return data.min + ' to ' + data.max;
						case 6: //weighted string list
							return findWeightedString(universe.universe.values, data.min) + ' to ' + findWeightedString(universe.universe.values, data.max);
						default:
							console.log('unhandled integer interval type : ' + universe.universeType);
					}
					break;
				case 4: // float interval
					switch(universe.universeType) {
						case 4: // float interval
							return data.min + ' to ' + data.max;
						default:
							console.log('unhandled float interval type : ' + universe.universeType);
					}
					break;
				case 8: // integer list
					switch(universe.universeType) {
						case 5: // string list
							var output = [];
							for(var i of data) {
								output.push(i.name);
							}
							return output.join(', ');
					}
				case 9: // tree
					return 'it posess some of what you want';
			}
		}
	};
})();