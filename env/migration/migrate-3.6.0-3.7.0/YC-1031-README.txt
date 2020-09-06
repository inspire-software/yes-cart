REF: YC-1031 CORS enabled REST API

OVERVIEW:
=========

CORS is required to improve multi-tennant servers whereby it is no longer necessary to create per sales channel
domain names to expose e-commerce APIs. As a consequence small refactoring was done on headers and cookies used by
the platform.

SCOPE OF CHANGES:
=================
- Added CORS filter to the REST API, with support for GET, POST, PUT, DELETE, OPTIONS and HEAD methods. Currently
  all headers are allowed (i.e. "*"). Allowed origins is controlled through Shop attribute SHOP_CORS_ALLOWED_ORIGINS,
  which should hold CSV of allowed originas sent in "Origin" header by the client (e.g. browser).
- Identification of the shop in REST API is not set to use "X-SALES-CHANNEL" HTTP header for all REST APIs, which
  allows to deploy API server under any domain.
- Authentication token is now renamed from "yc" to "X-CW-TOKEN" and hence the cookie
- yccookiepolicy cookie has been renamed to cookiepolicy
- new configuration files are added config-web-cors-*.properties to control build with "api.cors" variable on/off,
  which sets additional configurations in the web.xml for shop resolver filter and adds a new cors filter


SEACH KEYWORDS:
===============
- "yc"
- yccookiepolicy

