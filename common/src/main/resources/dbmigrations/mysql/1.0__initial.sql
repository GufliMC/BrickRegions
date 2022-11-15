-- apply changes
create table regions (
  dtype                         varchar(31) not null,
  id                            varchar(40) not null,
  world_id                      varchar(40) not null,
  name                          varchar(255) not null,
  priority                      integer default 1 not null,
  area                          varchar(8192) default null,
  constraint uq_regions_world_id_name unique (world_id,name),
  constraint pk_regions primary key (id)
);

create table region_attributes (
  id                            varchar(40) not null,
  region_id                     varchar(40) not null,
  name                          varchar(255) not null,
  value                         varchar(255) not null,
  constraint uq_region_attributes_region_id_name unique (region_id,name),
  constraint pk_region_attributes primary key (id)
);

create table region_rules (
  id                            varchar(40) not null,
  region_id                     varchar(40) not null,
  priority                      integer default 0 not null,
  status                        varchar(5) not null,
  target                        varchar(255) not null,
  type_set                      varchar(255) not null,
  constraint uq_region_rules_region_id_status_target_type_set unique (region_id,status,target,type_set),
  constraint pk_region_rules primary key (id)
);

-- foreign keys and indices
create index ix_region_attributes_region_id on region_attributes (region_id);
alter table region_attributes add constraint fk_region_attributes_region_id foreign key (region_id) references regions (id) on delete cascade on update restrict;

create index ix_region_rules_region_id on region_rules (region_id);
alter table region_rules add constraint fk_region_rules_region_id foreign key (region_id) references regions (id) on delete cascade on update restrict;

