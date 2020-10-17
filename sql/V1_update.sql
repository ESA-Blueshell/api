-- Add end_time and google_id columns and set their default values
alter table events
add end_time timestamp null;

alter table events
add google_id text null;

-- Default value == original time + 3 hours
update table events
set end_time = DATE_ADD(start_time, INTERVAL 3 HOUR);

-- Default value == id for random event
update events
set google_id='M2ZkcmtrcWxxYnRmOTRqcGZyZGM2NjdkNmQgY19rcXAycnU3OTJwbjdnaG5yYTMyODAyYjNtZ0Bn' where true