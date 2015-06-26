/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

var ctx = {
    url: document.URL,
    root: '/yes-shop', /* This is how it appears from WicketUtil.getHttpServletRequest().getContextPath() for default config, use ServerSideJs Wicket component to inject this. */
    page: 'HomePage',
    resources: {},
    productBuyPanels: function() {

        var _panels = [];

        return {
            addPanel: function(panel) {
                _panels.push(panel);
            },
            getPanel: function(sku) {
                for (var i = 0; i < _panels.length; i++) {
                    if (_panels[i].SKU == sku) {
                        return _panels[i];
                    }
                }
                return null;
            }
        }
    }(),
    showModalWindow: function(){}
};


// -- Logging -----------------------------------------------------------------------

(function() {
    // fn to add blank (noOp) function for all console methods
    var addConsoleNoOp =  function (window) {
        var names = ["log", "debug", "info", "warn", "error",
                "assert", "dir", "dirxml", "group", "groupEnd", "time",
                "timeEnd", "count", "trace", "profile", "profileEnd"],
            i, l = names.length,
            noOp = function () {};
        window.console = {};
        for (i = 0; i < l; i = i + 1) {
            window.console[names[i]] = noOp;
        }
    };

    // call addConsoleNoOp() if console is undefined
    if (!window.console) {
        addConsoleNoOp(window);
    }
}());

$(document).ready(function() {

    // -- Search box -----------------------------------------------------------------------

    var isBlank = function isBlank(str) {
        return (!str || /^\s*$/.test(str));
    };

    var doSearch = function() {
        var _val = $('.js-search-input').val();
        console.log('searching for "' + _val + '"');
        if (!isBlank(_val)) {
            var _loc = '';
            if (ctx.page.indexOf('HomePage') != -1 && ctx.url.indexOf('/product/') == -1 && ctx.url.indexOf('/sku/') == -1) {
                if ((window.location.href.lastIndexOf("/") == window.location.href.length - 1)) {
                    _loc = window.location.href + 'query/' + _val;
                } else {
                    _loc = window.location.href + '/query/' + _val;
                }
            } else {
                _loc = ctx.root + '/query/' + _val;
            }
            console.log('location "' + _loc + '"');
            window.location.href = _loc;
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
        $('#mini-cart').addClass("loading");

        console.log('clicking ATB "' + _ajaxATB + '"');

        $.ajax({
            url: _ajaxATB
        }).done(function ( data ) {

            console.log('got ATB response');

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

            var _jsonstr = $(data).filter('.jsajaxresponseobj').html();
            console.log('JSON: ' + _jsonstr);
            var _json = $.parseJSON(_jsonstr);


            $('.js-buy-panel').each(function() {

                var _panel = $(this);
                var _val = _panel.find('.js-qty-value');

                if (_val.length == 0) {
                    return; // picker value input is not enabled
                }

                var _panelObj = ctx.productBuyPanels.getPanel(_json.SKU);

                if (_panelObj != null) {

                    console.log('updating qty picker for ' + _json.SKU);
                    _panelObj.update(_json);

                }

            });

        }).always(function() {
            $('#mini-cart').removeClass("loading");
        });
        return false;
    });

    // -- Quantity picker -----------------------------------------------------------------------

    $('.js-buy-panel').each(function() {

        var _panel = $(this);

        var _remove = _panel.find('.js-qty-remove');
        var _add = _panel.find('.js-qty-add');
        var _val = _panel.find('.js-qty-value');

        if (_val.length == 0) {
            return null; // picker value input is not enabled
        }

        var _sku = _val.attr('yc-data-sku');

        var _logic = function(delta) {

            var _min = Number(_val.attr('yc-data-min'));
            var _max = Number(_val.attr('yc-data-max'));
            var _step = Number(_val.attr('yc-data-step'));

            var _stepStr = _val.attr('yc-data-step');

            var _stepDecimalPoint = _stepStr.indexOf('.') > -1 ? _stepStr.length - _stepStr.indexOf('.') - 1 : 0;

            console.log('quantity changed by ' + delta + ', min: ' + _min + ', max: ' + _max + ', step: ' + _step + ', step d/p: ' + _stepDecimalPoint);

            if (isNaN(_max) || _max <= 0) {
                _min = 0;
                _max = 0;
                _step = 0;
            }


            var _oldVal = Number(_val.val());
            var _newVal = _oldVal + delta * _step;
            if (_newVal == 0 && delta != 0) {
                return;
            }

            _newVal = _step > 0 ? (_min + Math.round((_newVal - _min) / _step) * _step).toFixed(_stepDecimalPoint) : 0;

            console.log('new quantity: ' + _newVal);

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
            _logic(1);
        });
        _remove.click(function(e) {
            e.preventDefault();
            _logic(-1);
        });
        _val.change(function(e) {
            _logic(0);
        });

        _logic(0); // init

        var _this = {

            SKU: _sku,

            min: function() {
                return Number(_val.attr('yc-data-min'));
            },

            max: function() {
                return Number(_val.attr('yc-data-max'));
            },

            step: function() {
                return Number(_val.attr('yc-data-step'));
            },

            update: function(json) {

                _val.attr('yc-data-min', json.min);
                _val.attr('yc-data-max', json.max);
                _val.attr('yc-data-step', json.step);
                _val.val(json.value);
                _val.attr('title', json.title).tooltip('fixTitle');

                _logic(0); // init

            }

        };

        ctx.productBuyPanels.addPanel(_this);

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

    $('.js-wish-remove, .js-wish-visibility').click(function(event) {
        event.preventDefault();

        var _href = $(this).attr('href');

        console.log('wish list click: ' + _href);

        var _msgNode = createAreYouSure('', _href);
        $('body').append(_msgNode);

        _msgNode.modal();

    });


    $('div[data-publickey]').each(function() {

        console.log('loading wish list information, publickey: ' + $(this).data('publickey'));

        var _this = $(this);

        var _tagcloud = function() {

            var _tagcloudtemplate = function(tag, count, significance, index) {
                console.log(count + ' items tagged with "' + tag + '" with significance ' + significance);
                return '<li><a href="#" class="tag' + significance + '" data-tag="' + tag + '" data-index="' + index + '">' + tag + '</a></li>';
            };

            var _tagmap = {
                allwls: [],
                alltags: [],
                bytag: {},
                selected: []
            };

            var _init = function() {

                _tagmap.allwls = [];
                _tagmap.alltags = [];
                _tagmap.bytag = {};
                _tagmap.selected = [];

                var _tagcloud = $('ul.jsWishlistTagCloud');
                _tagcloud.empty();

                _this.find('div.jsWishlist').each(function() {

                    var _wlpod = $(this);
                    var _tagvalue = _wlpod.data('tag');

                    if (_tagvalue != null && _tagvalue.length > 0) {
                        var _tags = _tagvalue.split(' ');
                        $.each(_tags, function(index, tag) {
                            if (!_tagmap.bytag.hasOwnProperty(tag)) {
                                _tagmap.bytag[tag] = [];
                                _tagmap.alltags.push(tag);
                                _tagmap.selected.push(false);
                            }
                            _tagmap.bytag[tag].push(_wlpod);
                        });
                    }
                    _tagmap.allwls.push(_wlpod);

                });

                var _min = 999;
                var _max = 0;
                $.each(_tagmap.alltags, function(index, tag) {
                    var _count = _tagmap.bytag[tag].length;
                    if (_count < _min) {
                        _min = _count;
                    }
                    if (_count > _max) {
                        _max = _count;
                    }
                });

                $.each(_tagmap.alltags, function(index, tag) {

                    var _tagcount = _tagmap.bytag[tag].length;
                    console.log('Count: ' + _tagcount + ', min: ' + _min + ', max: ' + _max);
                    var _tagli = $(_tagcloudtemplate(tag, _tagcount, _tagcount > _min ? Math.round((_tagcount - _min) / (_max - _min) * 10) : 0, index));
                    _tagcloud.append(_tagli);
                    var _taga = _tagli.find('a');
                    _taga.click(function(e) {

                        e.preventDefault();

                        var _a = $(this);
                        var _index = _a.data('index');
                        if (_a.hasClass('selected')) {
                            _a.removeClass('selected');
                            _tagmap.selected[_index] = false;
                            console.log(_a.data('tag') + ' de-selected');
                            $('#jsWishlistTagCloudShare').hide();
                        } else {
                            _a.addClass('selected');
                            _tagmap.selected[_index] = true;
                            console.log(_a.data('tag') + ' selected');
                            $('#jsWishlistTagCloudShareTag').html(_a.data('tag'));
                            var _wlsharelink = ctx.url;
                            if (_wlsharelink.indexOf('?') != -1) {
                                _wlsharelink = _wlsharelink.split('?')[0];
                            }
                            _wlsharelink = _wlsharelink + '?token=' + _this.data('publickey') + '&tag=' + _a.data('tag');
                            $('#jsWishlistTagCloudShareLink').val(_wlsharelink);
                            // Add to any integration
                            $('#jsWishlistTagCloudShareA2A').data('share', _wlsharelink);
                            $('#jsWishlistTagCloudShare').show();
                        }


                        var _atleastoneselected = false;
                        $.each(_tagmap.selected, function(index, selected) {
                            if (selected) {
                                _atleastoneselected = true;
                            }
                        });

                        if (_atleastoneselected) {
                            console.log('at least one tag is selected making all hidden');
                            $.each(_tagmap.allwls, function(index, wls) {
                                $(wls).hide();
                            });
                            $.each(_tagmap.selected, function(index1, selected) {
                                if (selected) {
                                    $.each(_tagmap.bytag[_tagmap.alltags[index1]], function(index2, wls) {
                                        console.log('showing wish list item ' + wls.data('sku') + ' because of tag ' + _tagmap.alltags[index1]);
                                        $(wls).show();
                                    })
                                }
                            });
                        } else {
                            console.log('making all items visible');
                            $.each(_tagmap.allwls, function(index, wls) {
                                $(wls).show();
                            });
                        }

                    });
                    _taga.tooltip({
                        title: function() {
                            if ($(this).hasClass('selected')) {
                                return ctx.resources['wishlistTagLinkOnInfo'];
                            }
                            return ctx.resources['wishlistTagLinkOffInfo'];
                        }
                    })
                });

            };

            return {
                init: function() {
                   _init();
                }
            }

        }();

        _this.find('div.jsWishlist').each(function() {

            var _wlpod = $(this);
            var _tagvalue = _wlpod.data('tag');

            var _tagpod = $('<input class="tags-input" type="text" value=""/>');
            if (_tagvalue != null && _tagvalue.length > 0) {
                _tagpod.val(_tagvalue.replace(/\s+/g, ','));
            }

            console.log(_wlpod.data('sku') + ' item has the following tags: ' + _tagpod.val());

            _wlpod.find('.thumbnail').append(_tagpod);
            _tagpod.tagsinput();
            _tagpod.on('beforeItemAdd', function(event) {

                var _tpa = $(this);
                var _wla = _tpa.closest('div.jsWishlist');

                if (_tpa.hasClass('loading')) {
                    _tpa.removeClass('loading');
                    console.log('Adding tag ' + event.item + ' to pod data: ' + _wla.data('tag'));
                    if (_wla.data('tag') != null && _wla.data('tag').length > 0) {
                        _wla.data('tag', _wla.data('tag') + ' ' + event.item);
                    } else {
                        _wla.data('tag', event.item);
                    }
                    _tagcloud.init();
                    console.log('Added tag ' + event.item + ', pod data: ' + _wla.data('tag'));
                    return;
                }

                event.cancel = true;
                var _tpai = _wla.find('.bootstrap-tagsinput');
                _tpai.addClass('loading');

                var _tia = event.item.replace(/[\s,]/g, '');
                var _tiaall = _tia;
                if (_wla.data('tag') != null && _wla.data('tag').length > 0) {
                    _tiaall = _wla.data('tag') + ' ' + _tia;
                }

                var _ajaxWLA = ctx.root + '/ajaxwl/addToWishListCmd/'
                    + _wla.data('sku') + '/type/' + _wla.data('type') + '/tags/'
                    + _tiaall + '/tagsr/tagsr/qty/0';

                console.log('Sending add to ' + _ajaxWLA);

                $.ajax({
                    url: _ajaxWLA
                }).done(function ( data ) {

                    var _json = $.parseJSON($(data).filter('.jsajaxresponseobj').html());
                    console.log(_json.SKU);
                    _tpa.addClass('loading');
                    _tpa.tagsinput('add', _tia);

                }).always(function() {
                    _tpai.removeClass('loading');
                });

            });
            _tagpod.on('beforeItemRemove', function(event) {


                var _tpr = $(this);
                var _wlr = _tpr.closest('div.jsWishlist');

                if (_tpr.hasClass('loading')) {
                    _tpr.removeClass('loading');
                    console.log('Removing tag ' + event.item + ' from pod data: ' + _wlr.data('tag'));
                    _wlr.data('tag', _wlr.data('tag').replace(event.item, '').replace(/\s{2,}/g, ' ').replace(/^\s+|\s+$/g, ''));
                    _tagcloud.init();
                    console.log('Removed tag ' + event.item + ', pod data: ' + _wlr.data('tag'));
                    return;
                }

                event.cancel = true;
                var _tpri = _wlr.find('.bootstrap-tagsinput');
                _tpri.addClass('loading');

                var _tir = event.item.replace(/\s+|,/g, '');
                var _tirall = _wlr.data('tag').replace(_tir, '').replace(/\s{2,}/g, ' ').replace(/^\s+|\s+$/g, '');

                var _ajaxWL = ctx.root + '/ajaxwl/addToWishListCmd/'
                    + _wlr.data('sku') + '/type/' + _wlr.data('type') + '/tags/'
                    + _tirall + '/tagsr/tagsr/qty/0';

                console.log('Sending remove to ' + _ajaxWL);

                $.ajax({
                    url: _ajaxWL
                }).done(function ( data ) {

                    var _json = $.parseJSON($(data).filter('.jsajaxresponseobj').html());
                    console.log(_json.SKU);
                    _tpr.addClass('loading');
                    _tpr.tagsinput('remove', _tir);

                }).always(function() {
                    _tpri.removeClass('loading');
                });
            });

            _wlpod.find('.bootstrap-tagsinput').tooltip({
                title: ctx.resources['wishlistTagsInfo']
            })
        });


        _tagcloud.init();

    });

    // -- CartList View ----------------------------------------------------------------------------

    $('.js-cart-remove').click(function(event) {
        event.preventDefault();

        var _href = $(this).attr('href');

        console.log('cart click: ' + _href);

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


    console.log('Loaded page: ' + ctx.page + ' with url ' + ctx.url);
});

