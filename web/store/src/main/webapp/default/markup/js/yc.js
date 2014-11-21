var ctx = {
    url: document.URL,
    root: '/yes-shop', /* This is how it appears from WicketUtil.getHttpServletRequest().getContextPath() for default config, use ServerSideJs Wicket component to inject this. */
    page: 'HomePage',
    resources: {},
    showModalWindow: function(){}
};

$(document).ready(function() {

    // -- Search box -----------------------------------------------------------------------

    var isBlank = function isBlank(str) {
        return (!str || /^\s*$/.test(str));
    };

    var doSearch = function() {
        var _val = $('.js-search-input').val();
        if (!isBlank(_val)) {
            if (ctx.page.indexOf('HomePage') != -1) {
                if ((window.location.href.lastIndexOf("/") == window.location.href.length - 1)) {
                    window.location.href = window.location.href + 'query/' + _val;
                } else {
                    window.location.href = window.location.href + '/query/' + _val;
                }
            } else {
                window.location.href = ctx.root + '/query/' + _val;
            }
        }
    };

    $('.js-search-input').keydown(function(event) {
        if (event.keyCode == 13) {
            event.preventDefault();
            doSearch();
        }
    });

    $('.js-search-button').click(function(event) {
        doSearch();
    });

    // -- AJAX buy with minicart -----------------------------------------------------------------------

    $('a.js-buy').click(function(event) {
        event.preventDefault();

        var _href = $(this).attr('href');
        var _ajaxATB = ctx.root + '/ajaxatb/' + _href.slice(_href.indexOf("addToCartCmd"));

        $('html, body').animate({ scrollTop: 0 }, 300);
        $('body').append($('<div class="modal js-modal-spinner">&nbsp;</div>'));
        $('body').addClass("loading");

        $.ajax({
            url: _ajaxATB
        }).done(function ( data ) {

                // update minicart for desktop
                var _newminicart = $(data).filter('.jsajaxresponseminicart');
                if (_newminicart.length > 0) {
                    $('#mini-cart').html(_newminicart.html());
                }

                // display update message
                var _message = $(data).filter('.jsajaxresponsemessage');
                if (_message.length > 0) {
                    ctx.showModalWindow(_message);
                }
            }).always(function() {
                $('div.js-modal-spinner').remove();
                $('body').removeClass("loading");
            });
        return false;
    });

    // -- Quantity picker -----------------------------------------------------------------------

    $('.js-buy-panel').each(function() {

        var _panel = $(this);
        var _remove = _panel.find('.js-qty-remove');
        var _add = _panel.find('.js-qty-add');
        var _val = _panel.find('.js-qty-value');

        var _min = Number(_val.attr('yc-data-min'));
        var _max = Number(_val.attr('yc-data-max'));
        var _step = Number(_val.attr('yc-data-step'));
        var _stepStr = _val.attr('yc-data-step');
        var _stepDecimalPoint = _stepStr.indexOf('.') > -1 ? _stepStr.length - _stepStr.indexOf('.') - 1 : 0;

        if (isNaN(_max) || _max <= 0) {
            _min = 0;
            _max = 0;
            _step = 0;
        }

        var _logic = function(delta) {

            var _oldVal = Number(_val.val());
            var _newVal = _oldVal + delta;
            if (_newVal == 0 && delta != 0) {
                return;
            }

            _newVal = _step > 0 ? (_min + Math.round((_newVal - _min) / _step) * _step).toFixed(_stepDecimalPoint) : 0;


            if (_newVal == 0) {
                _add.addClass('disabled');
                _remove.addClass('disabled');
            } else {
                if (_newVal <= _min) {
                    _newVal = _min;
                    _remove.addClass('disabled');
                } else {
                    if (_newVal >= _max) {
                        _newVal = _max;
                        _add.addClass('disabled');
                    } else {
                        _add.removeClass('disabled');
                    }
                    _remove.removeClass('disabled');
                }
            }
            if (_oldVal != _newVal) {
                _val.val(_newVal);
            }
            _panel.find('.js-buy').each(function() {
                var _btn = $(this);
                var _href = _btn.attr('href');
                var _qtyPos = _href.indexOf('/qty/');
                if (_qtyPos > -1) {
                    _href = _href.substring(0, _qtyPos) + '/qty/' + _newVal;
                } else {
                    _href += '/qty/' + _newVal;
                }
                _btn.attr('href', _href);
            });

        };

        _add.click(function(e) {
            e.preventDefault();
            _logic(_step);
        });
        _remove.click(function(e) {
            e.preventDefault();
            _logic(-_step);
        });
        _val.change(function(e) {
            _logic(0);
        });

        _logic(0); // init

    });

    // -- Product Image View -----------------------------------------------------------------------

    $('.js-product-image-view').each(function() {

        var _imgView = $(this);

        var _defaultImg = _imgView.find('.js-product-image-default');
        if (_defaultImg.length == 0) {
            return;
        }

        var _defaultImageUrl = _defaultImg.attr('src');
        var _defaultImageWidth = _defaultImg.attr('width');
        var _defaultImageHeight = _defaultImg.attr('height');

        var _imgAlt = _imgView.find('.js-product-image-alt');
        _imgAlt.each(function() {

            var _altImage = $(this);

            _altImage.mouseenter(function() {
                var _altImageUrl = _altImage.attr('src');
                _altImageUrl = _altImageUrl.substring(0, _altImageUrl.indexOf('?')) + "?w="+_defaultImageWidth+"&h="+_defaultImageHeight;
                _defaultImg.attr('src', _altImageUrl);
            }).mouseleave(function() {
                    _defaultImg.attr('src', _defaultImageUrl);
                });


        })
    });

    // -- WishList View ----------------------------------------------------------------------------

    $('.js-wish-remove').click(function(event) {
        event.preventDefault();

        var _href = $(this).attr('href');

        var _msgNode = createAreYouSure('', _href);
        $('body').append(_msgNode);

        _msgNode.modal();

    });

    // -- CartList View ----------------------------------------------------------------------------

    $('.js-cart-remove').click(function(event) {
        event.preventDefault();

        var _href = $(this).attr('href');

        var _msgNode = createAreYouSure('', _href);
        $('body').append(_msgNode);

        _msgNode.modal();

    });

    // -- Utility functions ------------------------------------------------------------------------

    // modal messages
    var createModalMsg = function(node) {
        var _inner = $('<div class="modal-message-inner"/>');
        _inner.append(node);
        var _outer = $('<div class="modal-message"/>');
        _outer.append(_inner);
        return _outer;
    };

    var createAreYouSure = function(node, callback) {
        var _inner = $('<div class="modal-dialog modal-sm">\
            <div class="modal-content modal-dialog-inner">\
                <div class="pull-left">' + ctx.resources['areYouSure'] + '</div>\
                <div class="pull-right">\
                    <a class="btn btn-default" data-dismiss="modal">' + ctx.resources['no'] + '</a>\
                    <a class="btn btn-primary" href="' + callback + '">' + ctx.resources['yes'] + '</a>\
                </div>\
                <div class="clearfix"></div>\
            </div>\
        </div>');
        _inner.append(node);
        var _outer = $('<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true"/>');
        _outer.append(_inner);
        return _outer;
    };

    ctx.showModalWindow = function(node) {
        var _msgNode = createModalMsg(node.html());
        $('body').append(_msgNode);
        setTimeout(function () {
            _msgNode.fadeOut(1000, function(){
                _msgNode.remove();
            });
        }, 2500);
    };

    // Enable tooltips
    $("[data-toggle=\"tooltip\"]").tooltip();
});

