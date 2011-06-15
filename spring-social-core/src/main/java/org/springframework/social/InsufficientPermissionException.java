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
package org.springframework.social;

/**
 * Exception thrown when attempting an operation that requires a permission not granted to the caller.
 * To remedy this kind of error, the application should attempt to reauthorize requesting the additional permission scope and then try again. 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class InsufficientPermissionException extends OperationNotPermittedException {
	
	private final String requiredPermission;

	public InsufficientPermissionException(String requiredPermission) {
		super("The operation requires '" + requiredPermission + "' permission.");
		this.requiredPermission = requiredPermission;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}
	
}
