create table ServiceProviderConnection (localUserId bigint not null,
					providerId varchar not null,
					providerUserId varchar,
					rank int not null,
					profileName varchar,
					profileUrl varchar,
					profilePictureUrl varchar,
					accessToken varchar not null,					
					secret varchar,
					refreshToken varchar,
					expireTime bigint,
					primary key (localUserId, providerId, providerUserId));
create index ConnectionsToProviderUser on ServiceProviderConnection(providerId, providerUserId);