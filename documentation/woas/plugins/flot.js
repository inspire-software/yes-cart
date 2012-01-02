/* flot binding plugin by legolas558 */

woas.custom.flot = {
	series: {},		// data series
	_queue: [],		// plot queue
	
	has_series: function(name) {
		return (typeof this.series[name] != "undefined");
	},
	
	_macro_define_series: function(macro, name, label, hidden) {
		// check that this name is available
		if (woas.custom.flot.has_series(name))
			 return false;
		var lines = macro.text.split("\n"),m, new_series = [], row;
		if (hidden)
			macro.text = "";
		else
			macro.text = "<strong>"+(typeof label == "undefined" ? name : label)+"</strong><br/><table style='border: 1px dotted'>";
		for(var i=0;i<lines.length;++i) {
			m = lines[i].match(/\d+(\.\d+)?/g);
			if (m === null) continue;
			// parse each row as numbers
			row = [];
			if (!hidden)
				macro.text += "<tr>";
			for(var b=0;b < m.length;++b) {
				row.push(new Number(m[b]));
				if (!hidden)
					macro.text += "<td style='border: 1px dotted'>"+m[b]+"</td>";
			}
			macro.text += "</tr>";
			new_series.push(row);
		}
		if (!hidden)
			macro.text += "</table>";
		
		// finally add the series, taking care of label
		if (typeof label != "undefined")
			woas.custom.flot.series[name] = { data: new_series, label: label };
		else
			woas.custom.flot.series[name] = new_series;
		return true;
	},
	
	_prev_bh: null,
	_browse_hook: function(title) {
		// cleanup series and queue defined on previous page
		woas.custom.flot.series = {};
		woas.custom.flot._queue = [];
		return (woas.custom.flot._prev_bh)();
	},

	// to be called when DOM has finished rendering
	_render_one: function(a) {
		var ds = [],
			// expand variables
			series = woas.custom.flot._queue[a][0],
			options = woas.custom.flot._queue[a][1];
		// get the registered series
		for(var i=0;i < series.length;++i) {
			if (!woas.custom.flot.has_series(series[i])) {
				return false;
			}
			ds.push(woas.custom.flot.series[series[i]]);
		}
		// hide the plot button
		d$.hide("woas_plot_btn_"+a);
		// prepare the DOM element
		woas.setHTML(d$("woas_flot_placeholder_"+a), "");
		d$("woas_flot_placeholder_"+a).style.lineHeight = "inherit";
		d$("woas_flot_placeholder_"+a).style.backgroundColor = "inherit";
		var rv = true;
		try {
			$.plot($("#woas_flot_placeholder_"+a), ds, options);
		} catch (e) {
			woas.log(e);
			rv = false;
		}
		return rv;
	},
	
	_macro_graph_canvas: function(macro, width, height, series, options) {
		var a = woas.custom.flot._queue.length;
		woas.custom.flot._queue.push([series, options]);
		macro.text = '<div id="woas_flot_placeholder_'+a+'" style="width:'+width+'px;height:'+height+'px; background-color: #CCCCCC; text-align: center; line-height:'+height+
						'px; color: gray"><em>(plot canvas is empty)</em></div>'+
					'<input id="woas_plot_btn_'+a+'" type="button" onclick="woas.custom.flot._render_one('+a+')" value="Plot" />';
		return true;
	}
	
};

// register the useful macros
woas.macro.register("woas.flot.series", woas.custom.flot._macro_define_series);
woas.macro.register("woas.flot.graph", woas.custom.flot._macro_graph_canvas);

// register the new hook executed when accessing a new page
woas.custom.flot._prev_bh = woas.pager.browse_hook;
woas.pager.browse_hook = woas.custom.flot._browse_hook;
