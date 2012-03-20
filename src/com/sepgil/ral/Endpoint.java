package com.sepgil.ral;

/**
 * This file is part of RestWS Android Library (RAL).
 * RAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * RAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RAL.  If not, see <http://www.gnu.org/licenses/>.
 */
import android.util.Base64;

/**
 * Represents the Drupal site and the credentials of the user.
 * @author Sebastian Gilits
 *
 */
public class Endpoint {
	private String mUri = "";
	private String mCredentials = "";
	
	/**
	 * Create a new Endpoint.
	 * @param drupalUri The url to the Drupal site.
	 * @param username The username which should be used to login.
	 * @param password The password which should be used to login.
	 */
	public Endpoint (String drupalUri, String username, String password) {
		mUri = drupalUri;
		mCredentials = Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
	}

	/**
	 * Used by DrupalOperationTask for the HTTP basic authentication.
	 * @return Base64 encoded username:password.
	 */
	public String getAuth() {
		return mCredentials;
	}

	/**
	 * Return the uri to the drupal site.
	 * @return the uri to the drupal site.
	 */
	public String getUri() {
		return mUri;
	}
}
