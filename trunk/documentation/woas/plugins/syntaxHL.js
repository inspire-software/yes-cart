/* shjs binding for WoaS
   @author legolas558
   @version 0.2.0
   @license GPLv2

   works with shjs 0.6
   be sure that you have downloaded and extracted shjs in shjs directory
*/

woas.custom.shjs = {
	
	// we take an unique id for our job
	is_loaded: false,
	_uid: _random_string(8),
	_block: 0,			// current block
	
	init: function() {
		if (!this.is_loaded) {
			this.is_loaded = woas.dom.add_css("shjs_style", "plugins/shjs/sh_style.min.css", true, woas.custom.shjs._render_all) &&
								woas.dom.add_script("lib", "shjs", "plugins/shjs/sh_main.min.js", true, woas.custom.shjs._render_all);
		}
		return this.is_loaded;
	},
	
	_called: 0,
	_rendering: false,
	_render_all: function() {
		if (woas.custom.shjs._rendering) return;
		if (++woas.custom.shjs._called == 2) {
			woas.custom.shjs._rendering = true;
//			woas.log("OK, now rendering");
			// render all blocks by clicking their button
			for(var i=0;i < woas.custom.shjs._block;++i) {
				d$("shjs_postr_btn_"+woas.custom.shjs._uid+"_"+i).click();
			}
			// clear
			woas.custom.shjs._block = 0;
		} else {
//			woas.log("CSS and library not yet ready");
			return;
		}
	},
	
	// this was adapted from shjs' sh_highlightDocument
	_highlight_element: function(element, languages) {
		if (typeof sh_languages == "undefined") {
			woas.log("shjs library does not seem to be loaded");
			return false;
		}
		for (var j = 0; j < languages.length; j++) {
			if (languages[j] in sh_languages) {
				sh_highlightElement(element, sh_languages[languages[j]]);
			} else {
				woas.log("Cannot render language "+languages[j]);
				return false;
			}
			return true;
		}
	},

	// used for post-rendering after library was loaded
	post_render: function(i, languages) {
		var elem = d$("woas_shjs_"+this._uid+"_"+i);
		if (this._highlight_element(elem, languages))
			d$.hide("shjs_postr_btn_"+this._uid+"_"+i);
	},
	
	_macro_hook: function(macro, classes) {
		// shjs library not yet loaded, go into pre-render mode
		var pre_render = (typeof sh_languages == "undefined");

		// rebuild the classes string as a simple JSON array
		var classes_arr = classes.split(' '), classes_v="[", cl = classes_arr.length;
		if (cl) {
			var language;
			for (var i = 0; i < cl; i++) {
				if (classes_arr[i].length > 0) {
					language = woas.trim(classes_arr[i]).toLowerCase();
					if (language.substr(0,3) !== "sh_")
						continue;
					language = language.substr(3);
					// skip this one because already rendered
					if (language === "sourcecode")
						continue;
					classes_v += "'"+woas.js_encode(language)+"',";
					if (!pre_render && !(language in sh_languages)) {
						// load this library
						woas.custom.shjs._queue_load(language);
					}
				}
			}
			// remove final comma
			classes_v = classes_v.substr(0, classes_v.length-1);
		}
		classes_v += "]";

		macro.text = "<"+"pre id=\"woas_shjs_"+woas.custom.shjs._uid+"_"+woas.custom.shjs._block+"\" class=\""+classes+"\">"+macro.text+"<"+"/pre>";
		if (pre_render) {
			macro.text += "<"+"input id=\"shjs_postr_btn_"+woas.custom.shjs._uid+
						"_"+woas.custom.shjs._block+"\" type=\"button\" value=\"Render\" onclick=\"woas.custom.shjs.post_render("+woas.custom.shjs._block+","+classes_v+");\" /"+">";
			macro.reprocess = false;
		} else {
			// inline script for highlighting
			// TODO: check why the script_extension private array does not work
//			woas.parser.script_extension.push(
			macro.text += "<"+"script type=\"text/javascript\">\n"+
					"woas.custom.shjs._highlight_element(d$('woas_shjs_"+woas.custom.shjs._uid+"_"+woas.custom.shjs._block+"'),"+classes_v+");"+
					"<"+"/script>";
//			);
			macro.reprocess = true;
		}
		
		++woas.custom.shjs._block;
		// reset block counter
//		if (!pre_render)	this._block = 0;
		// get the desired languages
		if (!pre_render)
			woas.custom.shjs._queue_flush();
		return true;
	},
	
	// array of languages which we are loading (async)
	_queued_lib: [],
	// array of desired languages (to be loaded) before rendering
	_desired: [],
	_queue_load: function(language) {
		if (typeof sh_languages == "undefined") {
			woas.custom.shjs._desired.push(language);
			return;
		}
		if (!(language in sh_languages) && (woas.custom.shjs._queued_lib.indexOf(language) === -1)) {
			woas.custom.shjs._queued_lib.push(language);
			woas.dom.add_script("lib", "shjs_"+language, "plugins/shjs/lang/sh_"+language+".min.js", true);
		}
	},
	
	_queue_flush: function() {
		// now sh_languages is defined, we flush the queue
		var language;
		while (woas.custom.shjs._desired.length) {
			language = woas.custom.shjs._desired.shift();
			woas.custom.shjs._queue_load(language);
		} // wend
		woas.custom.shjs._desired = [];
	},

};

// initialize the library
woas.custom.shjs.init();

// register the macro
woas.macro.register('woas.shjs', woas.custom.shjs._macro_hook);
