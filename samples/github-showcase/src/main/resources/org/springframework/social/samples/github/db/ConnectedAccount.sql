create table AccountConnection (member bigint,
					provider varchar,
					accessToken varchar not null,					
					secret varchar, 
					refreshToken varchar,
					accountId varchar,	
					profileUrl varchar,
					primary key (member, provider, accessToken));
create index ProviderAccountKey on AccountConnection(accountId);