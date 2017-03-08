# Copyright 2009 Denys Pavlov, Igor Azarnyi
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

Overview
========

Stress test is design to see how YC performs under load. Test are performed
using JMeter and involve 2 major user groups: 1) people browsing the site
and 2) people placing orders

Statistically speaking the ratio is around 5% (5 out of 100 people are buying)
but to be thorough and allow use cases for heavy checkouts it is reasonable to
use 10% conversion ratio thus accounting for double the expectations.

Therefore when configuring the "Number of threads (users)" the ratio for
simultaneous execution of both scenarios should be 1 to 10 (e.g. if "Catalog
Browsing" is set to 500 users then "Checkout" should be set to 50).

Setup considerations:
=====================

Stress test is YC version specific as it relies on some html elements being
present on pages.

The files bundled with YC are valid for Apache JMeter v 2.9.

The tests are written for default shop setup and hence may require some
adjustments if shop configurations are changed. Specifically:
 1) registration step needs to be revisited if supported types or registration
    fields have been adjusted from the defaults;
 2) Shipping address step must be revisited if address form has been
    reconfigured;
 3) Ensure that only one supplier is used, otherwise shipping step must be
    configured to support multi-selection.
 4) payment step also needs adjusting if you reconfigured the payment gateways
    We strongly recommend using something simple like test PG gateway or
    carrier payment.

Recommended scenarios:
======================

Test setup:
Browsing: 19steps + 2s pauses = ~1m per user journey
Checkout: 15steps + 3s pauses = ~1m per user journey
So:
  100 concurrent users =   ~6,000 user journeys per hour
  500 concurrent users =  ~30,000 user journeys per hour
1,000 concurrent users =  ~60,000 user journeys per hour
2,000 concurrent users = ~120,000 user journeys per hour


Small e-commerce site:
Browsing: 90 concurrent users (2s delay)
Checkout: 10 concurrent users (3s delay)
Recommended throughput threshold: 70,000pph (~3,500 users)

Medium shops:
Browsing: 450 concurrent users (2s delay)
Checkout:  50 concurrent users (3s delay)
Recommended throughput threshold: 200,000pph  (~12,000 users)

Large shops:
Browsing: 900 concurrent users (2s delay)
Checkout: 100 concurrent users (3s delay)
Recommended throughput threshold: 300,000pph (~16,000 users)

Large shops (with sales spikes):
Browsing: 1800 concurrent users (2s delay)
Checkout:  200 concurrent users (3s delay)
Recommended throughput threshold: 500,000pph (~28,000 users)

NOTE: Performance testing should not be undertaken on development environment
as it will not give realistic results. Test server must mimic the production
environments spec to produce realistic data. Production environment should only
be tested with "Browsing scenario" so that no garbage accounts and orders are
generated on the live system.

General guidelines for throughput:
==================================

300,000 pph (pages per hour) is a reasonable healthy flow for a large site

Anything beyond 500,000 pph are extreme cases and there are only few real
e-commerce sites that need this kind of throughput continuously.

What is really out there?
=========================

Throughput above 500,000 pph is supported only by a few frameworks and
requires expensive infrastructure with massive clusters.

Most hit the bottleneck at RDBMS level at this point so there is a physical
limit to what can be achieved.

So for users that do require this kind of throughput the choice of platform
is very limited and options are quite expensive.

Where does YC stand?
====================

We aim for a platform that will sustain heavy loads at minimal costs and
simplest infrastructure possible. This not only to support heavy users
but also to decrease latency in general. The faster your site works - the
happier your clients are.

We aim to achieve 500,000 pph on a single box as a standard to meet
the top limit criteria. But we also look into clustering to up the limit
to sustain 1,000,000 pph to satisfy the most demanding users.