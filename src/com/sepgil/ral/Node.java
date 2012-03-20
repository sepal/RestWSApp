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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Drupal node.
 * Currently only nodes with titles are supported and only pages can be created.
 * @author Sebastian Gilits
 *
 */
public class Node {
	public final static String LANGUAGE_NONE = "und";
	
	private int mNid = -1;
	private String mTitle, mBody = "";
	
	private HashMap<String, Object> defaultProperties = new HashMap<String, Object>();
	
	/**
	 * Creates a Node out of a JSON object.
	 * @param object The JSON object that represents the node.
	 * @throws JSONException Can be thrown when a field is missing.
	 */
	public Node(JSONObject object) throws JSONException {
		mNid = object.getInt("nid");
		mTitle = object.getString("title");
		mBody = object.getJSONObject("body").getString("value");
	}
	
	/**
	 * Creates a new node with default properties.
	 * @param userID The user id of the author of this new node.
	 */
	public Node(int userID) {
		defaultProperties.put("author", userID);
		defaultProperties.put("type", "page");
	}

	/**
	 * Returns a JSON object which represents the node.
	 * @return A JSON object which represents the node.
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	public String getJSON() throws JSONException, UnsupportedEncodingException {
		String res = "";
		JSONObject body = new JSONObject();
		body.put("value", mBody);
		
		JSONObject json = new JSONObject();
		json.put("title", mTitle);
		json.put("body",  body);
		
		for (String key : defaultProperties.keySet()) {
			json.put(key, defaultProperties.get(key));
		}
		
		res = json.toString();
		

		return res;
	}

	/**
	 * Returns the title of the node.
	 * @return the title of the node.
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Returns the body of the node.
	 * @return The body of the node.
	 */
	public String getBody() {
		return mBody;
	}

	/**
	 * Returns the body of the node.
	 * @return The body of the node.
	 */
	public int getID() {
		return mNid;
	}

	/**
	 * Sets the title of the node.
	 * @param title the title of the node.
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * Sets the body of the node.
	 * @param body the body of the node.
	 */
	public void setBody(String body) {
		mBody = body;
	}

	/**
	 * Sets the ID of the node.
	 * @param nid the ID of the node.
	 */
	public void setID(int nid) {
		mNid = nid;
	}
}
