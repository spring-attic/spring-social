create table ServiceProviderConnection (accountId varchar not null,
					providerId varchar not null,
					id int not null,
					providerAccountId varchar,
					profileName varchar,
					profileUrl varchar,
					profilePictureUrl varchar,
					allowSignIn boolean,
					accessToken varchar not null,					
					secret varchar, 
					refreshToken varchar,
					primary key (accountId, providerId, id));
create index ProviderAccount on ServiceProviderConnection(providerId, providerAccountId);