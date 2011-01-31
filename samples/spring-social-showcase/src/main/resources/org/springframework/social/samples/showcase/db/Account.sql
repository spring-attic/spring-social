create table Account (	id identity,
						username varchar unique,
						password varchar not null,
						firstName varchar not null, 
						lastName varchar not null,
						email varchar(320) not null unique,
						primary key (id));
