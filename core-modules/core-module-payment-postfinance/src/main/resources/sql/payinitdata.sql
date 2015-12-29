
INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15101, 'postFinancePaymentGateway', 'name', 'PostFinance', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15102, 'postFinancePaymentGateway', 'name_en', 'PostFinance', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15103, 'postFinancePaymentGateway', 'name_ru', 'PostFinance', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15104, 'postFinancePaymentGateway', 'name_uk', 'PostFinance', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15105, 'postFinancePaymentGateway', 'name_de', 'PostFinance', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15150, 'postFinancePaymentGateway', 'PF_POST_URL', 'https://e-payment.postfinance.ch/ncol/test/orderstandard.asp', 'Form action',
'In the production (PROD) environment, the URL for the action will be https://e- payment.postfinance.ch/ncol/prod/orderstandard.asp.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15151, 'postFinancePaymentGateway', 'PF_RESULT_URL_HOME', 'http://@domain@/yes-shop/', '(Absolute) URL of your home page',
'(Absolute) URL of your home page. When the transaction has been processed, your customer is requested to return to this URL via a button.
When you send the value "NONE", the button leading back to the merchants site will be hidden.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15152, 'postFinancePaymentGateway', 'PF_RESULT_URL_CATALOG', 'http://@domain@/yes-shop/', '(Absolute) URL of your catalogue',
'(Absolute) URL of your catalogue. When the transaction has been processed, your customer is requested to return to this URL via a button.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15153, 'postFinancePaymentGateway', 'PF_RESULT_URL_ACCEPT', 'http://@domain@/yes-shop/paymentresult?hint=ok', 'URL for accepted payment',
'URL of the web page to display to the customer when the payment has been authorised (status 5), stored (status 4), accepted (status 9) or is waiting to be accepted (pending status 41, 51 or 91).');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15154, 'postFinancePaymentGateway', 'PF_RESULT_URL_DECLINE', 'http://@domain@/yes-shop/paymentresult?hint=declined', 'URL for declined payment',
'URL of the web page to show the customer when the acquirer declines the authorisation (status 2 or 93) more than the maximum permissible number of times.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15155, 'postFinancePaymentGateway', 'PF_RESULT_URL_EXCEPTION', 'http://@domain@/yes-shop/paymentresult?hint=exception', 'URL for error during payment',
'URL of the web page to display to the customer when the payment result is uncertain (status 52 or 92).
If this field is empty, the customer will see the ACCEPTURL instead.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15156, 'postFinancePaymentGateway', 'PF_RESULT_URL_CANCEL', 'http://@domain@/yes-shop/paymentresult?hint=cancel', 'URL for cancelled payment',
'URL of the web page to display to the customer when he cancels the payment (status 1).
If this field is empty, the customer will see the DECLINEURL instead.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15157, 'postFinancePaymentGateway', 'PF_PSPID', '!!!PROVIDE VALUE!!!', 'Your affiliation name',
'Your affiliation name (PSPID)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15158, 'postFinancePaymentGateway', 'PF_SHA_IN', '!!!PROVIDE VALUE!!!', 'SHA-IN signature',
'SHA-IN signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15159, 'postFinancePaymentGateway', 'PF_SHA_OUT', '!!!PROVIDE VALUE!!!', 'SHA-OUT signature',
'SHA-OUT signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15160, 'postFinancePaymentGateway', 'PF_STYLE_TITLE', '', 'Style: Title and header of the page',
'Title and header of the page');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15161, 'postFinancePaymentGateway', 'PF_STYLE_BGCOLOR', '', 'Style: Background colour',
'Style: Background colour, default is white');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15162, 'postFinancePaymentGateway', 'PF_STYLE_TXTCOLOR', '', 'Style: Text colour',
'Style: Text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15163, 'postFinancePaymentGateway', 'PF_STYLE_TBLBGCOLOR', '', 'Style: Table background colour',
'Style: Table background colour, default is white');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15164, 'postFinancePaymentGateway', 'PF_STYLE_TBLTXTCOLOR', '', 'Style: Table text colour',
'Style: Table text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15165, 'postFinancePaymentGateway', 'PF_STYLE_BUTTONBGCOLOR', '', 'Style: Button background colour',
'Style: Button background colour');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15166, 'postFinancePaymentGateway', 'PF_STYLE_BUTTONTXTCOLOR', '', 'Style: Button text colour',
'Style: Button text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15167, 'postFinancePaymentGateway', 'PF_STYLE_FONTTYPE', '', 'Style: Font family',
'Style: Font family, default is Verdana');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15168, 'postFinancePaymentGateway', 'PF_STYLE_LOGO', '', 'Style: Logo',
'Style: Logo
(Absolute HTTPS) URL/filename of the logo next to the title.
If the logo is stored on PostFinance servers, the URL will be: https://e-payment.postfinance.ch/images/merchant/[PSPID]/[image]');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15169, 'postFinancePaymentGateway', 'PF_STYLE_TP', '', 'Style: URL of the merchant’s dynamic template page',
'Style: URL of the merchant’s dynamic template page');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15170, 'postFinancePaymentGateway', 'PF_PM', '', 'Payment: Payment method',
'Payment: Payment method e.g. CreditCard, iDEAL');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15171, 'postFinancePaymentGateway', 'PF_BRAND', '', 'Payment: Credit card brand',
'Payment: Credit card brand e.g. VISA or blank');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15172, 'postFinancePaymentGateway', 'PF_WIN3DS', '', 'Payment: 3-D secure',
'Payment: 3-D secure
"MAINW": to display the identification page in the main window (default value)
"POPUP"": to display the identification page in a POPUP window and return to main window at the end');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15173, 'postFinancePaymentGateway', 'PF_PMLIST', '', 'Payment: List of selected payment methods.',
'Payment: List of selected payment methods and/or credit card brands. Separated by a “;” (semicolon).');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15174, 'postFinancePaymentGateway', 'PF_EXCLPMLIST', '', 'Payment: List of excluded payment methods.',
'Payment: List of payment methods and/or credit card brands that should NOT be shown. Separated by a “;” (semicolon). E.g. "VISA;iDEAL"');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15175, 'postFinancePaymentGateway', 'PF_PMLISTTYPE', '', 'Payment: Layout of the payment methods.',
'The possible values are 0, 1 and 2:
0: Horizontally grouped logos with the group name on the left (default value)
1: Horizontally grouped logos with no group names
2: Vertical list of logos with specific payment method or brand name');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15176, 'postFinancePaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15177, 'postFinancePaymentGateway', 'PF_ITEMISED', 'false', 'Enable itemised data',
  'Itemised data is sent to PF as individual items, if this is set to false then only total is sent');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15178, 'postFinancePaymentGateway', 'LANGUAGE_MAP', 'en=en_US,de=de_DE,ru=ru_RU,uk=ru_RU', 'Language Mapping',
  'Language mapping can be used to map internal locale to PG supported locale
  See "PostFinance > Support > Parameter CookBook" LANGUAGE parameter for more details');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15201, 'postFinanceManualPaymentGateway', 'name', 'PostFinance (Manual Capture)', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15202, 'postFinanceManualPaymentGateway', 'name_en', 'PostFinance (Manual Capture)', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15203, 'postFinanceManualPaymentGateway', 'name_ru', 'PostFinance (Ручная авторизация)', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15204, 'postFinanceManualPaymentGateway', 'name_uk', 'PostFinance (Ручна авторизація)', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15205, 'postFinanceManualPaymentGateway', 'name_de', 'PostFinance (Manuell Datenerfassung)', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15250, 'postFinanceManualPaymentGateway', 'PF_POST_URL', 'https://e-payment.postfinance.ch/ncol/test/orderstandard.asp', 'Form action',
'In the production (PROD) environment, the URL for the action will be https://e- payment.postfinance.ch/ncol/prod/orderstandard.asp.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15251, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_HOME', 'http://@domain@/yes-shop/', '(Absolute) URL of your home page',
'(Absolute) URL of your home page. When the transaction has been processed, your customer is requested to return to this URL via a button.
When you send the value "NONE", the button leading back to the merchants site will be hidden.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15252, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_CATALOG', 'http://@domain@/yes-shop/', '(Absolute) URL of your catalogue',
'(Absolute) URL of your catalogue. When the transaction has been processed, your customer is requested to return to this URL via a button.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15253, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_ACCEPT', 'http://@domain@/yes-shop/paymentresult?hint=ok', 'URL for accepted payment',
'URL of the web page to display to the customer when the payment has been authorised (status 5), stored (status 4), accepted (status 9) or is waiting to be accepted (pending status 41, 51 or 91).');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15254, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_DECLINE', 'http://@domain@/yes-shop/paymentresult?hint=declined', 'URL for declined payment',
'URL of the web page to show the customer when the acquirer declines the authorisation (status 2 or 93) more than the maximum permissible number of times.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15255, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_EXCEPTION', 'http://@domain@/yes-shop/paymentresult?hint=exception', 'URL for error during payment',
'URL of the web page to display to the customer when the payment result is uncertain (status 52 or 92).
If this field is empty, the customer will see the ACCEPTURL instead.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15256, 'postFinanceManualPaymentGateway', 'PF_RESULT_URL_CANCEL', 'http://@domain@/yes-shop/paymentresult?hint=cancel', 'URL for cancelled payment',
'URL of the web page to display to the customer when he cancels the payment (status 1).
If this field is empty, the customer will see the DECLINEURL instead.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15257, 'postFinanceManualPaymentGateway', 'PF_PSPID', '!!!PROVIDE VALUE!!!', 'Your affiliation name',
'Your affiliation name (PSPID)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15258, 'postFinanceManualPaymentGateway', 'PF_SHA_IN', '!!!PROVIDE VALUE!!!', 'SHA-IN signature',
'SHA-IN signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15259, 'postFinanceManualPaymentGateway', 'PF_SHA_OUT', '!!!PROVIDE VALUE!!!', 'SHA-OUT signature',
'SHA-OUT signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15260, 'postFinanceManualPaymentGateway', 'PF_STYLE_TITLE', '', 'Style: Title and header of the page',
'Title and header of the page');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15261, 'postFinanceManualPaymentGateway', 'PF_STYLE_BGCOLOR', '', 'Style: Background colour',
'Style: Background colour, default is white');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15262, 'postFinanceManualPaymentGateway', 'PF_STYLE_TXTCOLOR', '', 'Style: Text colour',
'Style: Text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15263, 'postFinanceManualPaymentGateway', 'PF_STYLE_TBLBGCOLOR', '', 'Style: Table background colour',
'Style: Table background colour, default is white');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15264, 'postFinanceManualPaymentGateway', 'PF_STYLE_TBLTXTCOLOR', '', 'Style: Table text colour',
'Style: Table text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15265, 'postFinanceManualPaymentGateway', 'PF_STYLE_BUTTONBGCOLOR', '', 'Style: Button background colour',
'Style: Button background colour');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15266, 'postFinanceManualPaymentGateway', 'PF_STYLE_BUTTONTXTCOLOR', '', 'Style: Button text colour',
'Style: Button text colour, default is black');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15267, 'postFinanceManualPaymentGateway', 'PF_STYLE_FONTTYPE', '', 'Style: Font family',
'Style: Font family, default is Verdana');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15268, 'postFinanceManualPaymentGateway', 'PF_STYLE_LOGO', '', 'Style: Logo',
'Style: Logo
(Absolute HTTPS) URL/filename of the logo next to the title.
If the logo is stored on PostFinance servers, the URL will be: https://e-payment.postfinance.ch/images/merchant/[PSPID]/[image]');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15269, 'postFinanceManualPaymentGateway', 'PF_STYLE_TP', '', 'Style: URL of the merchant’s dynamic template page',
'Style: URL of the merchant’s dynamic template page');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15270, 'postFinanceManualPaymentGateway', 'PF_PM', '', 'Payment: Payment method',
'Payment: Payment method e.g. CreditCard, iDEAL');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15271, 'postFinanceManualPaymentGateway', 'PF_BRAND', '', 'Payment: Credit card brand',
'Payment: Credit card brand e.g. VISA or blank');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15272, 'postFinanceManualPaymentGateway', 'PF_WIN3DS', '', 'Payment: 3-D secure',
'Payment: 3-D secure
"MAINW": to display the identification page in the main window (default value)
"POPUP"": to display the identification page in a POPUP window and return to main window at the end');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15273, 'postFinanceManualPaymentGateway', 'PF_PMLIST', '', 'Payment: List of selected payment methods.',
'Payment: List of selected payment methods and/or credit card brands. Separated by a “;” (semicolon).');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15274, 'postFinanceManualPaymentGateway', 'PF_EXCLPMLIST', '', 'Payment: List of excluded payment methods.',
'Payment: List of payment methods and/or credit card brands that should NOT be shown. Separated by a “;” (semicolon). E.g. "VISA;iDEAL"');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15275, 'postFinanceManualPaymentGateway', 'PF_PMLISTTYPE', '', 'Payment: Layout of the payment methods.',
'The possible values are 0, 1 and 2:
0: Horizontally grouped logos with the group name on the left (default value)
1: Horizontally grouped logos with no group names
2: Vertical list of logos with specific payment method or brand name');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15276, 'postFinanceManualPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15277, 'postFinanceManualPaymentGateway', 'PF_ITEMISED', 'false', 'Enable itemised data',
  'Itemised data is sent to PF as individual items, if this is set to false then only total is sent');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (15278, 'postFinanceManualPaymentGateway', 'LANGUAGE_MAP', 'en=en_US,de=de_DE,ru=ru_RU,uk=ru_RU', 'Language Mapping',
  'Language mapping can be used to map internal locale to PG supported locale
  See "PostFinance > Support > Parameter CookBook" LANGUAGE parameter for more details');




