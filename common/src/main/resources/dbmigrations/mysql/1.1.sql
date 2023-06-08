-- apply alter tables
alter table regions add column region_active tinyint(1) default 1 not null;
