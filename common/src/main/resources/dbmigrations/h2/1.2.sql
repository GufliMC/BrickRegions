-- apply alter tables
alter table localities add column region_id uuid;
alter table localities add column group_id uuid;
-- foreign keys and indices
create index ix_localities_region_id on localities (region_id);
alter table localities add constraint fk_localities_region_id foreign key (region_id) references localities (id) on delete restrict on update restrict;

create index ix_localities_group_id on localities (group_id);
alter table localities add constraint fk_localities_group_id foreign key (group_id) references localities (id) on delete restrict on update restrict;

