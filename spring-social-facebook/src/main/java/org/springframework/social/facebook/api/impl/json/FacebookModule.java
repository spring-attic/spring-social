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
package org.springframework.social.facebook.api.impl.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Checkin;
import org.springframework.social.facebook.api.CheckinPost;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.EducationEntry;
import org.springframework.social.facebook.api.Event;
import org.springframework.social.facebook.api.EventInvitee;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.GroupMemberReference;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.LinkPost;
import org.springframework.social.facebook.api.Location;
import org.springframework.social.facebook.api.NotePost;
import org.springframework.social.facebook.api.Photo;
import org.springframework.social.facebook.api.Photo.Image;
import org.springframework.social.facebook.api.PhotoPost;
import org.springframework.social.facebook.api.Place;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.StatusPost;
import org.springframework.social.facebook.api.Tag;
import org.springframework.social.facebook.api.UserLike;
import org.springframework.social.facebook.api.Video;
import org.springframework.social.facebook.api.VideoPost;
import org.springframework.social.facebook.api.WorkEntry;
import org.springframework.social.facebook.api.impl.json.PhotoMixin.ImageMixin;

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
