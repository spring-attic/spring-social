create table Connection (id identity,
					accountId varchar,
					providerId varchar,
					accessToken varchar not null,					
					secret varchar, 
					refreshToken varchar
					primary key (id));
create index AccessToken on Connection(accountId, providerId, accessToken);