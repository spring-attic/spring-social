create table ServiceProviderConnection (id identity,
					localUserId bigint not null,
					providerId varchar not null,
					providerUserId varchar,
					profileName varchar,
					profileUrl varchar,
					profilePictureUrl varchar,
					allowSignIn boolean,
					accessToken varchar not null,					
					secret varchar, 
					refreshToken varchar,
					primary key (id));
create unique index ConnectionKey on ServiceProviderConnection(localUserId, providerId, providerUserId);
create index LocalUserProviderConnections on ServiceProviderConnection(localUserId, providerId);					
create index ConnectionsToProviderUser on ServiceProviderConnection(providerId, providerUserId);