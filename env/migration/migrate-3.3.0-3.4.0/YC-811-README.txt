REF: YC-811 Upgrade org.springframework

This upgrade involves upgrading spring from 3.2.x to 4.3.x.

The following changes have taken place during this upgrade:

- Spring Upgrade to version: 3.2.3.RELEASE > 4.3.11.RELEASE, security: 3.1.4.RELEASE > 4.2.3.RELEASE, spring-ws: 2.2.0.RELEASE > 2.4.0.RELEASE
- Jackson upgrade to version 1.9.13 > 2.9.1
- CXF WS upgrade to version 2.5.2 > 3.2.0 (excluded bouncy castle and ehcache)
- Quartz scheduler upgrade to version 1.7.3 > 2.3.0
- c3p0 upgrade to version 0.9.1.2 > com.mchange 0.9.5.2 (added to exclusions for quartz)
- TEST: upgraded JUnit to 4.12 required by spring, StringContains matcher no longer available used hamcrest
- REST-API: Jackson is now upgraded to Jackson2 (MappingJacksonJsonView > MappingJackson2JsonView, MappingJacksonHttpMessageConverter > MappingJackson2HttpMessageConverter)
- Crons (CronTriggerBean > CronTriggerFactoryBean, JobDetailBean > JobDetailFactoryBean)
- Wicket, added exclusion to cglib dependency (because it uses old asm that conflicts with cxf)
- Unified org.bouncycastle:bcprov-jdk15on 1.58 (used by ws-security, cybersource)
- JAM disabled csrf security and forced using default url redirect
