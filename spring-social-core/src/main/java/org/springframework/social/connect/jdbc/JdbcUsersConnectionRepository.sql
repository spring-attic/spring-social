create table UserConnection (userId varchar not null,
					providerId varchar not null,
					providerUserId varchar,
					rank int not null,
					displayName varchar,
					profileUrl varchar,
					imageUrl varchar,
					accessToken varchar not null,					
					secret varchar,
					refreshToken varchar,
					expireTime bigint,
					primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

/* MySQL 5.1-compliant schema */
create table UserConnection (
 userId varchar(64) not null,
 providerId varchar(255) not null,
 providerUserId varchar(255),
 rank int not null,
 displayName varchar(1024),
 profileUrl varchar(4096),
 imageUrl varchar(4096),
 accessToken varchar(4096) not null,					
 secret varchar(4096),
 refreshToken varchar(4096),
 expireTime bigint(20),
 primary key (userId, providerId, providerUserId)
);

create unique index UserConnectionRank on UserConnection(userId, providerId, rank);
