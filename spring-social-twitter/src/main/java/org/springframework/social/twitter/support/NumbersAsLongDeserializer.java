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
package org.springframework.social.twitter.support;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.UntypedObjectDeserializer;

/**
 * Custom Jackson deserializer that always deserializes untyped number objects as Long (as opposed to the default implementation
 * that deserializes into the best fit of Intger, Long, or BigInteger.) 
 * @author Craig Walls
 */
public class NumbersAsLongDeserializer extends UntypedObjectDeserializer {

	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonToken t = jp.getCurrentToken();
    	if(t == JsonToken.VALUE_NUMBER_INT) {
    		return jp.getNumberValue().longValue();
    	}
    	
    	return super.deserialize(jp, ctxt);
    }
	
}
