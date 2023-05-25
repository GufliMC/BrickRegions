-- apply changes
create table localities (
  dtype                         varchar(31) not null,
  id                            uuid not null,
  locality_world_id             uuid not null,
  locality_priority             integer default 1 not null,
  region_name                   varchar(255),
  shaperegion_shape             varchar(2048) default null,
  tileregion_tile_radius        integer default 0 not null,
  tileregion_tile_offset        varchar(255),
  tilegroup_parent_region_id    uuid,
  constraint pk_localities primary key (id)
);

create table locality_attributes (
  id                            uuid not null,
  name                          varchar(255) not null,
  attrvalue                     varchar(255) not null,
  locality_id                   uuid not null,
  constraint uq_locality_attributes_locality_id_name unique (locality_id,name),
  constraint pk_locality_attributes primary key (id)
);

create table locality_rules (
  id                            uuid not null,
  priority                      integer default 0 not null,
  status                        varchar(5) not null,
  target                        varchar(255) not null,
  type_set                      varchar(255) not null,
  locality_id                   uuid not null,
  constraint ck_locality_rules_status check ( status in ('DENY','ALLOW')),
  constraint uq_locality_rules_locality_id_status_target_type_set unique (locality_id,status,target,type_set),
  constraint pk_locality_rules primary key (id)
);

create table tiles (
  id                            uuid not null,
  group_id                      uuid not null,
  position                      varchar(255),
  shape                         varchar(255),
  constraint pk_tiles primary key (id)
);

-- foreign keys and indices
create index ix_localities_tilegroup_parent_region_id on localities (tilegroup_parent_region_id);
alter table localities add constraint fk_localities_tilegroup_parent_region_id foreign key (tilegroup_parent_region_id) references localities (id) on delete restrict on update restrict;

create index ix_locality_attributes_locality_id on locality_attributes (locality_id);
alter table locality_attributes add constraint fk_locality_attributes_locality_id foreign key (locality_id) references localities (id) on delete cascade on update restrict;

create index ix_locality_rules_locality_id on locality_rules (locality_id);
alter table locality_rules add constraint fk_locality_rules_locality_id foreign key (locality_id) references localities (id) on delete cascade on update restrict;

create index ix_tiles_group_id on tiles (group_id);
alter table tiles add constraint fk_tiles_group_id foreign key (group_id) references localities (id) on delete cascade on update restrict;

