/* Copyright 2006-2010 the original author or authors.
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
package org.osehra.cpe.auth

/**
 * Auth tags.
 */
class AuthTagLib {

	static namespace = 'auth'

	/** Dependency injection for userContext. */
	UserContext userContext

	/**
	 * Renders a property (specified by the 'field' attribute) from the principal.
	 *
	 * @attr field REQUIRED the field name
	 */
	def loggedInUserInfo = { attrs, body ->

		// TODO support 'var' and 'scope' and set the result instead of writing it
		String field = assertAttribute('field', attrs, 'loggedInUserInfo')

		def source
		if (userContext.isLoggedIn()) {
			source = determineSource()
			for (pathElement in field.split('\\.')) {
				source = source."$pathElement"
				if (source == null) {
					break
				}
			}
		}

		if (source) {
			out << source.encodeAsHTML()
		}
		else {
			out << body()
		}
	}

	/**
	 * Renders the user's username if logged in.
	 */
	def username = { attrs ->
		if (userContext.isLoggedIn()) {
			out << userContext.currentUser.name.encodeAsHTML()
		}
	}

    def userId = {attrs ->
        if (userContext.isLoggedIn()) {
			out << userContext.currentUser.DUZ.encodeAsHTML()
		}
    }
	
	def timeoutSeconds = {
		if (userContext.isLoggedIn()) {
			out << userContext.currentUser.getTimeoutSeconds()
		}
	}

    def timeoutCountDownSeconds = {
		if (userContext.isLoggedIn()) {
			out << userContext.currentUser.getTimeoutCountdownSeconds()
		}
	}

    /**
	 * Renders the user's display name if logged in.
	 */
	def userDisplayName = { attrs ->
		if (userContext.isLoggedIn()) {
			out << userContext.currentUser.displayName.encodeAsHTML()
		} else {
            out << 'Guest'.encodeAsHTML()
        }
	}

	/**
	 * Renders the body if the user is authenticated.
	 */
	def ifLoggedIn = { attrs, body ->
		if (userContext.isLoggedIn()) {
			out << body()
		}
	}

	/**
	 * Renders the body if the user is not authenticated.
	 */
	def ifNotLoggedIn = { attrs, body ->
		if (!userContext.isLoggedIn()) {
			out << body()
		}
	}
}
