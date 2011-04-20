/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.social.facebook.comment.Comment;
import org.springframework.social.facebook.comment.CommentMixin;
import org.springframework.social.facebook.event.Event;
import org.springframework.social.facebook.event.EventInvitee;
import org.springframework.social.facebook.event.EventInviteeMixin;
import org.springframework.social.facebook.event.EventMixin;
import org.springframework.social.facebook.event.Invitation;
import org.springframework.social.facebook.event.InvitationMixin;
import org.springframework.social.facebook.feed.CheckinPost;
import org.springframework.social.facebook.feed.CheckinPostMixin;
import org.springframework.social.facebook.feed.LinkPost;
import org.springframework.social.facebook.feed.LinkPostMixin;
import org.springframework.social.facebook.feed.NotePost;
import org.springframework.social.facebook.feed.NotePostMixin;
import org.springframework.social.facebook.feed.PhotoPost;
import org.springframework.social.facebook.feed.PhotoPostMixin;
import org.springframework.social.facebook.feed.Post;
import org.springframework.social.facebook.feed.PostMixin;
import org.springframework.social.facebook.feed.StatusPost;
import org.springframework.social.facebook.feed.StatusPostMixin;
import org.springframework.social.facebook.feed.VideoPost;
import org.springframework.social.facebook.feed.VideoPostMixin;
import org.springframework.social.facebook.group.Group;
import org.springframework.social.facebook.group.GroupMemberReference;
import org.springframework.social.facebook.group.GroupMemberReferenceMixin;
import org.springframework.social.facebook.group.GroupMixin;
import org.springframework.social.facebook.like.UserLike;
import org.springframework.social.facebook.like.UserLikeMixin;
import org.springframework.social.facebook.media.Album;
import org.springframework.social.facebook.media.AlbumMixin;
import org.springframework.social.facebook.media.Photo;
import org.springframework.social.facebook.media.PhotoMixin;
import org.springframework.social.facebook.media.Video;
import org.springframework.social.facebook.media.VideoMixin;
import org.springframework.social.facebook.media.Photo.Image;
import org.springframework.social.facebook.media.PhotoMixin.ImageMixin;
import org.springframework.social.facebook.places.Checkin;
import org.springframework.social.facebook.places.CheckinMixin;
import org.springframework.social.facebook.places.Location;
import org.springframework.social.facebook.places.LocationMixin;
import org.springframework.social.facebook.places.Place;
import org.springframework.social.facebook.places.PlaceMixin;
import org.springframework.social.facebook.shared.Reference;
import org.springframework.social.facebook.shared.ReferenceMixin;
import org.springframework.social.facebook.shared.Tag;
import org.springframework.social.facebook.shared.TagMixin;
import org.springframework.social.facebook.user.EducationEntry;
import org.springframework.social.facebook.user.EducationEntryMixin;
import org.springframework.social.facebook.user.FacebookProfile;
import org.springframework.social.facebook.user.FacebookProfileMixin;
import org.springframework.social.facebook.user.WorkEntry;
import org.springframework.social.facebook.user.WorkEntryMixin;

/**
 * Jackson module for setting up mixin annotations on Facebook model types. This enables the use of Jackson annotations without
 * directly annotating the model classes themselves.
 * @author Craig Walls
 */
public class FacebookModule extends SimpleModule {

	public FacebookModule() {
		super("FacebookModule", new Version(1, 0, 0, null));
	}
	
	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(FacebookProfile.class, FacebookProfileMixin.class);
		context.setMixInAnnotations(WorkEntry.class, WorkEntryMixin.class);
		context.setMixInAnnotations(EducationEntry.class, EducationEntryMixin.class);
		context.setMixInAnnotations(Reference.class, ReferenceMixin.class);
		context.setMixInAnnotations(GroupMemberReference.class, GroupMemberReferenceMixin.class);
		context.setMixInAnnotations(Album.class, AlbumMixin.class);
		context.setMixInAnnotations(UserLike.class, UserLikeMixin.class);
		context.setMixInAnnotations(Group.class, GroupMixin.class);
		context.setMixInAnnotations(Event.class, EventMixin.class);
		context.setMixInAnnotations(Invitation.class, InvitationMixin.class);
		context.setMixInAnnotations(EventInvitee.class, EventInviteeMixin.class);
		context.setMixInAnnotations(Checkin.class, CheckinMixin.class);
		context.setMixInAnnotations(Place.class, PlaceMixin.class);
		context.setMixInAnnotations(Location.class, LocationMixin.class);
		context.setMixInAnnotations(Comment.class, CommentMixin.class);
		context.setMixInAnnotations(Tag.class, TagMixin.class);
		context.setMixInAnnotations(Video.class, VideoMixin.class);
		context.setMixInAnnotations(Photo.class, PhotoMixin.class);
		context.setMixInAnnotations(Image.class, ImageMixin.class);
		context.setMixInAnnotations(Post.class, PostMixin.class);
		context.setMixInAnnotations(CheckinPost.class, CheckinPostMixin.class);
		context.setMixInAnnotations(LinkPost.class, LinkPostMixin.class);
		context.setMixInAnnotations(NotePost.class, NotePostMixin.class);
		context.setMixInAnnotations(PhotoPost.class, PhotoPostMixin.class);
		context.setMixInAnnotations(StatusPost.class, StatusPostMixin.class);
		context.setMixInAnnotations(VideoPost.class, VideoPostMixin.class);
	}
}
