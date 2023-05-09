-- apply changes
create table dtile (
  id                            uuid not null,
  group_id                      uuid not null,
  position                      varchar(255),
  shape                         varchar(255),
  constraint pk_dtile primary key (id)
);

-- foreign keys and indices
create index ix_dtile_group_id on dtile (group_id);
alter table dtile add constraint fk_dtile_group_id foreign key (group_id) references localities (id) on delete cascade on update restrict;

