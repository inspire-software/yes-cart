
var ww = document.body.clientWidth;

$(document).ready(function() {
	$(".nav li a").each(function() {
		if ($(this).next().length > 0) {
			$(this).addClass("parent");
		};
	})
	
	$(".toggleMenu").click(function(e) {
		e.preventDefault();
		$(this).toggleClass("active");
		$(".nav").toggle();
	});

	$(".toggleMenu2").click(function(e) {
		e.preventDefault();
		$(this).toggleClass("active");
		$(".topcatnav").toggle();
	});
	adjustMenu();
})

$(window).bind('resize orientationchange', function() {
	ww = document.body.clientWidth;
	adjustMenu();
});

function adjustMenuSmall(styleName, toggleMenuStyleName) {
    $(toggleMenuStyleName).css("display", "inline-block");
    if (!$(toggleMenuStyleName).hasClass("active")) {
        $(styleName).hide();
    } else {
        $(styleName).show();
    }
    $(styleName + " li").unbind('mouseenter mouseleave');
    $(styleName + " li a.parent").unbind('click').bind('click', function (e) {
        // must be attached to anchor element to prevent bubbling
        e.preventDefault();
        $(this).parent("li").toggleClass("hover");
    });
}

function adjustMenuBig(styleName, toggleMenuStyleName) {
    $(toggleMenuStyleName).css("display", "none");
    $(styleName).show();
    $(styleName + " li").removeClass("hover");
    $(styleName + " li a").unbind('click');
    $(styleName + " li").unbind('mouseenter mouseleave').bind('mouseenter mouseleave', function () {
        // must be attached to li so that mouseleave is not triggered when hover over submenu
        $(this).toggleClass('hover');
    });
}
var adjustMenu = function() {
	if (ww < 768) {
        adjustMenuSmall(".nav", ".toggleMenu");
        adjustMenuSmall(".topcatnav", ".toggleMenu2");
    } else if (ww >= 768) {
        adjustMenuBig(".nav", ".toggleMenu");
        adjustMenuBig(".topcatnav", ".toggleMenu2");
    }
}

