REF: YC-971 JWT authentication in Admin

OVERVIEW:
=========

Replacement of Spring MVC security (login/logout pages + session) with JWT session-less implementation.

KEY CHANGES:
============

- setting sessionless configurations for spring security
- removal of old MVC config
- added JWT filters to facilitate session-less security
- update to web.xml not to handle any jsclient code, it is now rendered as static resources, but the login mechanism is
  now embeded into the client so the login prompt will appear inside the application. Bonus you do not need page refresh
  anymore, just log back in and resume work. Cons: potentially need to fix some screens as direct access may crash them
  when accessed directly (e.g. hitting /organisation/users directly).

REFACTORING:
============

The following resources re-naming took place:

- Amdin styles moved yc-main.css to cl-main.css
- Changed Cookie in Admin so use prefix ADM_* (affect PROD, existing customer will need to reset their preferences)
- JS Client now deployed under /client path (not /resources path)
