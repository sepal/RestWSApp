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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tests if the given Endpoint (to the execute() function) is correct.
 * @author Sebastian Gilits
 *
 */
public class TestAuthenticationTask extends DrupalOperationTask {
	private OnAuthenticationTaskResult mListener = null;

	/**
	 * Register a callback to be invoked when the Endpoint was tested.
	 * @param Listener the callback that will run.
	 */
	public void setOnAuthenticationTaskResult(OnAuthenticationTaskResult listener) {
		mListener = listener;
	}
	
	@Override
	public String getPath() {
		return "/user";
	}

	@Override
	public void onResponse(JSONObject response) {
		if (mListener == null)
			return;
		mListener.onSuccess();
	}

	@Override
	public HttpRequestBase getHttpRequest(String url) {
		return new HttpGet(url);
	}

	@Override
	public void onJSONError(JSONException e) {
		if (mListener == null)
			return;
		mListener.onOther();
	}


	@Override
	public void onHttpError(StatusLine statusLine) {
		if (mListener == null)
			return;
		mListener.onOther();
	}

	@Override
	public void onParserError(Exception e) {
		if (mListener == null)
			return;
		mListener.onOther();
	}

	/**
	 * Interface definition for a callback to be invoked when the Endpoint was tested.
	 * @author Sebastian Gilits
	 *
	 */
	public interface OnAuthenticationTaskResult {
		/**
		 * Called when the Endpoint is valid.
		 */
		public void onSuccess();
		/**
		 * Called when the task couldn't log in.
		 */
		public void onLoginError();
		
		/**
		 * Called when some other error happend.
		 */
		public void onOther();
	}
}
