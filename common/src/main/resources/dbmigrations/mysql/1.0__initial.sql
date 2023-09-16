-- apply changes
create table `regions` (
  `dtype`                       varchar(31) not null,
  `id`                          varchar(40) not null,
  `region_world_id`             varchar(40) not null,
  `region_name`                 varchar(255),
  `region_priority`             integer default 0 not null,
  `region_active`               tinyint(1) default 1 not null,
  `shaperegion_shape`           varchar(2048) default null,
  `tileregion_tile_radius`      integer default 0 not null,
  `tileregion_tile_offset`      varchar(255),
  `tilegroup_tileregion_id`     varchar(40),
  `tilegroup_tiles`             varchar(255),
  constraint "pk_regions" primary key (`id`)
);

create table `region_attributes` (
  `id`                          varchar(40) not null,
  `name`                        varchar(255) not null,
  `attrvalue`                   varchar(255) not null,
  `region_id`                   varchar(40) not null,
  constraint "uq_region_attributes_region_id_name" unique (`region_id`,`name`),
  constraint "pk_region_attributes" primary key (`id`)
);

create table `region_rules` (
  `id`                          varchar(40) not null,
  `priority`                    integer default 0 not null,
  `status`                      varchar(5) not null,
  `target`                      varchar(255) not null,
  `types`                       varchar(255) not null,
  `region_id`                   varchar(40) not null,
  constraint "uq_region_rules_region_id_status_target_types" unique (`region_id`,`status`,`target`,`types`),
  constraint "pk_region_rules" primary key (`id`)
);

-- foreign keys and indices
create index "ix_regions_tilegroup_tileregion_id" on `regions` (`tilegroup_tileregion_id`);
alter table `regions` add constraint "fk_regions_tilegroup_tileregion_id" foreign key (`tilegroup_tileregion_id`) references `regions` (`id`) on delete restrict on update restrict;

create index "ix_region_attributes_region_id" on `region_attributes` (`region_id`);
alter table `region_attributes` add constraint "fk_region_attributes_region_id" foreign key (`region_id`) references `regions` (`id`) on delete cascade on update restrict;

create index "ix_region_rules_region_id" on `region_rules` (`region_id`);
alter table `region_rules` add constraint "fk_region_rules_region_id" foreign key (`region_id`) references `regions` (`id`) on delete cascade on update restrict;

