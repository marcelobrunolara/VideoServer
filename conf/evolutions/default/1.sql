# --- !Ups

create table s3file (
  
id                        varchar(40) not null,
  
bucket                    varchar(255),
  
name                      varchar(255),
  
rawname                      varchar(255),

constraint pk_s3file primary key (id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists s3file;

SET REFERENTIAL_INTEGRITY TRUE;

