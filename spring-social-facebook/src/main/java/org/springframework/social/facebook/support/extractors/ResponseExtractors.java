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
package org.springframework.social.facebook.support.extractors;



public class ResponseExtractors {

	public static final AlbumResponseExtractor ALBUM_EXTRACTOR = new AlbumResponseExtractor();

	public static final CheckinResponseExtractor CHECKIN_EXTRACTOR = new CheckinResponseExtractor();

	public static final CommentResponseExtractor COMMENT_EXTRACTOR = new CommentResponseExtractor();
	
	public static final EducationResponseExtractor EDUCATION_EXTRACTOR = new EducationResponseExtractor();
	
	public static final EventResponseExtractor EVENT_EXTRACTOR = new EventResponseExtractor();
	
	public static final FeedEntryResponseExtractor FEED_ENTRY_EXTRACTOR = new FeedEntryResponseExtractor();
	
	public static final GroupResponseExtractor GROUP_EXTRACTOR = new GroupResponseExtractor();
	
	public static final InviteeResponseExtractor INVITEE_EXTRACTOR = new InviteeResponseExtractor();
	
	public static final PhotoResponseExtractor PHOTO_EXTRACTOR = new PhotoResponseExtractor();
	
	public static final ProfileResponseExtractor PROFILE_EXTRACTOR = new ProfileResponseExtractor();
	
	public static final ReferenceResponseExtractor REFERENCE_EXTRACTOR = new ReferenceResponseExtractor();
	
	public static final TagResponseExtractor TAG_EXTRACTOR = new TagResponseExtractor();
	
	public static final UserEventResponseExtractor USER_EVENT_EXTRACTOR = new UserEventResponseExtractor();
	
	public static final UserLikeResponseExtractor USER_LIKE_EXTRACTOR = new UserLikeResponseExtractor();
	
	public static final VideoResponseExtractor VIDEO_EXTRACTOR = new VideoResponseExtractor();
	
	public static final WorkResponseExtractor WORK_EXTRACTOR = new WorkResponseExtractor();

}
