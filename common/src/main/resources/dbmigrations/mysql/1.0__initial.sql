-- apply changes
create table localities (
  dtype                         varchar(31) not null,
  id                            varchar(40) not null,
  world_id                      varchar(40) not null,
  priority                      integer default 1 not null,
  region_name                   varchar(255),
  region_displayname            varchar(2048) default null,
  shaperegion_shape             varchar(2048) default null,
  tileregion_tile_radius        integer default 0 not null,
  tileregion_tile_width         integer default 0 not null,
  tileregion_offset             varchar(255),
  parent_id                     varchar(40),
  tile_position                 varchar(1024),
  tile_polygon                  varchar(2048),
  constraint pk_localities primary key (id)
);

create table locality_attributes (
  id                            varchar(40) not null,
  name                          varchar(255) not null,
  attrvalue                     varchar(255) not null,
  locality_id                   varchar(40) not null,
  constraint uq_locality_attributes_locality_id_name unique (locality_id,name),
  constraint pk_locality_attributes primary key (id)
);

create table locality_rules (
  id                            varchar(40) not null,
  priority                      integer default 0 not null,
  status                        varchar(5) not null,
  target                        varchar(255) not null,
  type_set                      varchar(255) not null,
  locality_id                   varchar(40) not null,
  constraint uq_locality_rules_locality_id_status_target_type_set unique (locality_id,status,target,type_set),
  constraint pk_locality_rules primary key (id)
);

-- foreign keys and indices
create index ix_localities_parent_id on localities (parent_id);
alter table localities add constraint fk_localities_parent_id foreign key (parent_id) references localities (id) on delete restrict on update restrict;

create index ix_locality_attributes_locality_id on locality_attributes (locality_id);
alter table locality_attributes add constraint fk_locality_attributes_locality_id foreign key (locality_id) references localities (id) on delete cascade on update restrict;

create index ix_locality_rules_locality_id on locality_rules (locality_id);
alter table locality_rules add constraint fk_locality_rules_locality_id foreign key (locality_id) references localities (id) on delete cascade on update restrict;

