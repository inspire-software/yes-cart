/* jsMath binding for WoaS
   @author legolas558
   @version 0.1.7
   @license GPLv2

   works with jsMath 3.6e
   be sure that you have downloaded and extracted jsMath in jsMath directory
*/

// this is necessary before loading any jsMath library
jsMath = {
	Controls: {
		cookie: {scale: 133, global: 'never', button:0}
	},
	Font: {
		// override font messages
		Message : function(msg) {woas.log(woas.xhtml_to_text(msg).replace(/\n\n/g, "\n"));}
	},
	Setup: {UserEvent: {
      onload: function () {
         woas.custom.jsMath._after_lib_loaded();
       }
    }},

	noGoGlobal:1,
	noChangeGlobal:1,
	noShowGlobal:1
};

woas.custom.jsMath = {
	init : function() {
		// nothing here
	},
	
	_after_lib_loaded: function() {
		woas.log("jsMath library finished loading");
		woas.custom.jsMath.render_all();
	},
	
	_block: 0,		// number of div tags to render after library finishes loading
	_rendering: false,
	render_all: function() {
		if (woas.custom.jsMath._rendering) return;
		woas.custom.jsMath._rendering = true;
		jsMath.Init();
		var it = woas.custom.jsMath._block;
		// reset so that new blocks can be created meanwhile
		woas.custom.jsMath._block = 0;
		for(var i=0;i < it;++i) {
			woas.custom.jsMath.post_render(i);
		}
		// done rendering
		woas.custom.jsMath._rendering = false;
	},
	
	// used for post-rendering after library was loaded
	post_render: function(i) {
		if (typeof jsMath.Translate == "undefined") {
			woas.log("jsMath library does not seem to be loaded");
			return;
		}
		var elem = d$("jsmath_postrender_"+i);
		woas.setHTML(elem, jsMath.Translate.Parse('T', woas.getHTML(elem)));
		d$.hide("jsmath_postr_btn_"+i);
	},
	
	_macro_hook : function(macro) {
		 // quit if libraries have not yet been loaded and
		 // increase counter for post-rendering
		 if (typeof jsMath.Process == "undefined") {
			macro.text = "<"+"div id=\"jsmath_postrender_"+woas.custom.jsMath._block+"\">"+macro.text+"<"+"/div>"+
						"<"+"input id=\"jsmath_postr_btn_"+woas.custom.jsMath._block+
						"\" type=\"button\" value=\"Render\" onclick=\"woas.custom.jsMath.post_render("+woas.custom.jsMath._block+");\" /"+">";
			++this._block;
			return true;
		 }
// 		this._block = 0;
		jsMath.Init();
		macro.text = jsMath.Translate.Parse('T', macro.text);
		return true;
	}
	
};

// initialize the library
//woas.custom.jsMath.init();

// register the macro
woas.macro.register('woas.jsmath', woas.custom.jsMath._macro_hook);
