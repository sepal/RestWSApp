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

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This interface defines functions that will be used by DrupalOperationInterface.
 * New DrupalOperationTasks don't need to implement this interface but have to extend on DrupalOperationTask.
 * @author Sebastian Gilits
 *
 */
public interface DrupalOperationInterface {
	
	/**
	 * Returns the relative path to the web service.
	 * @return The relative path to the web service i.e. /user/123 or /node.
	 */
	public String getPath();
	
	/**
	 * Should create a new HttpRequestBase implementation.
	 * The function also may want to add an HttpEntity or additional HTTP headers.
	 * @param url The full URL to the web service, which should be passed to constructor of the HttpRequestBase implementation.
	 * @return the newly generated HttpRequestBase implementation.
	 */
	public HttpRequestBase getHttpRequest(String url);
	
	/**
	 * Called if the task was a success.
	 * @param response
	 */
	public void onResponse(JSONObject response);
	
	/**
	 * Called when DrupalOperationTask couldn't parse the string to a JSON object.
	 * @param e The exception that was thrown, while parsing the string.
	 */
	public void onJSONError(JSONException e);
	
	/**
	 * Called when the server didn't responed with HttpStatus.SC_CREATED or HttpStatus.SC_OK.
	 * @param statusLine the Statusline, which contains the error code and the description.
	 */
	public void onHttpError(StatusLine statusLine);
	
	/**
	 * Called when the response it self couldn't be parse as a string.
	 * @param e The exception that was thrown, while parsing the response.
	 */
	public void onParserError(Exception e);
}
