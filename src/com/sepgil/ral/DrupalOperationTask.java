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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Basic implementation of an operation for a Drupal site.
 * The class itself is extends on AsyncTask, so it takes care of performing operations in background.
 * @author Sebastian Gilits
 *
 */
public abstract class DrupalOperationTask extends AsyncTask<Endpoint, Void, HttpResponse> implements DrupalOperationInterface {
	
	@Override
	protected HttpResponse doInBackground(Endpoint... endpoints) {
		HttpResponse response = null;
		// If there is no Endpoint or we don't need to continue.
		if (endpoints.length == 0 && endpoints[0] == null) {
			onHttpError(null);
			return response;
		}
		// We only use the first endpoint, since we only want to perform the task on one site.
		Endpoint endpoint = endpoints[0];
		
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		
		// Request a new HTTPRequest and generate a path.
		HttpRequestBase request = getHttpRequest(endpoint.getUri() + getPath());
		// Add authentication and content type headers.
		request.setHeader("Authorization", "Basic " + endpoint.getAuth());
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");

		try {
			// and try to execute the request.
			response = client.execute(request);
		} catch (IOException e) {
			onHttpError(null);
		}
		// AndroidHttpClient's need to be closed.
		client.close();
		return response;
	}	

	@Override
	protected void onPostExecute(HttpResponse response) {
		// If there was no response, something may be wrong, since RestWS always returns something.
		if (response == null ) {
			Log.w("DrupalCom", "Didn't get any response.");
			onHttpError(null);
			return;
		}
		
		int state = response.getStatusLine().getStatusCode();
		
		switch (state) {
		case HttpStatus.SC_CREATED:
		case HttpStatus.SC_OK:
			parseResponse(response.getEntity());
			return;
		default:
			onHttpError(response.getStatusLine());
			Log.w("DrupalCom", "Status code not ok: " + response.getStatusLine().getReasonPhrase());
			return;
		}
	}

	/**
	 * Tries to parse the response to a JSON object.
	 * @param httpEntity
	 */
	private void parseResponse(HttpEntity httpEntity){
		BufferedReader reader;
		String line, rawJson = "";
		// Read the input from the httpEntity and save it to a string.
		try {
			reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
			while((line = reader.readLine()) != null) {
				rawJson += line;
			}
		} catch (IllegalStateException stateException) {
			Log.w("DrupalCom", "IllegalStateException while reading input stream.");
			onParserError(stateException);
		} catch (IOException ioException) {
			Log.w("DrupalCom", "IO Error while reading input stream.");
			onParserError(ioException);
		}
		// Check if the content type is correct.
		String contentType = httpEntity.getContentType().getValue();
		try {
			if (contentType.equals("application/json")) {
				// Parse the string to a json object.
				JSONObject response = new JSONObject(rawJson);
				onResponse(response);
			} else {
				Log.w("DrupalCom", "Got not json but " + contentType);
				onJSONError(null);
			}
		} catch (JSONException e) {
			onJSONError(e);
		}
	}
}
