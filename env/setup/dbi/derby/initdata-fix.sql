--
-- fixed for init data for Derby
--

-- SET character_set_client=utf8;
-- SET character_set_connection=utf8;

update TATTRIBUTE set REXP = '^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*((\.[A-Za-z]{2,}){1}$)' where CODE in ('default_email1', 'email');

update TATTRIBUTE set REXP = '^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$' where CODE in ('password');