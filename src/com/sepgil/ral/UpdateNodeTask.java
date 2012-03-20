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

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Updates a node.
 * @author Sebastian Gilits
 *
 */
public class UpdateNodeTask extends DrupalOperationTask {
	public enum ErrorState {
		ERROR_PARSE,
		ERROR_HTTP,
	}
	
	private OnNodeUpdatedListener mListener = null;
	private Node mNode;
	
	/**
	 * Constructor
	 * @param node The node which should be updated.
	 * @throws JSONException
	 */
	public UpdateNodeTask(Node node) throws JSONException {
		mNode = node;
	}
	
	/**
	 * Register a callback to be invoked when the node was updated.
	 * @param listener The callback that will run.
	 */
	public void setOnNodeUpdatedListener(OnNodeUpdatedListener listener) {
		mListener = listener;
	}

	@Override
	public String getPath() {
		return "/node/" + mNode.getID();
	}

	@Override
	public HttpRequestBase getHttpRequest(String url) {
		HttpPost post = new HttpPost(url);
		
		try {
			StringEntity enity = new StringEntity(mNode.getJSON(), HTTP.UTF_8);
			post.setEntity(enity);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return post;
	}

	@Override
	public void onResponse(JSONObject response) {
		if (mListener==null)
			return;
		mListener.onNodeUpdated();
	}

	@Override
	public void onJSONError(JSONException e) {
		if (mListener==null)
			return;
		//mListener.onError(ErrorState.ERROR_PARSE);
		// RestWS generates a wrong JSON as response, so will simply ignore this problem.
		mListener.onNodeUpdated();
	}

	@Override
	public void onHttpError(StatusLine statusLine) {
		if (mListener==null)
			return;
		switch (statusLine.getStatusCode()) {
		case HttpStatus.SC_FORBIDDEN:
			mListener.onNoAccess(mNode.getID());
			break;
		case HttpStatus.SC_NOT_FOUND:
			mListener.onNotFound(mNode.getID());
			break;
		default:
			mListener.onError(ErrorState.ERROR_HTTP);
		}
		
	}

	@Override
	public void onParserError(Exception e) {
		if (mListener==null)
			return;
		mListener.onError(ErrorState.ERROR_PARSE);
	}
	
	/**
	 * Interface definition for a callback to be invoked when the node was updated (or not).
	 * @author Sebastian Gilits
	 *
	 */
	public interface OnNodeUpdatedListener {
		/**
		 * Called when the node was updated.
		 */
		public void onNodeUpdated();
		
		/**
		 * Called when the server returned 403.
		 * @param nid The node id.
		 */
		public void onNoAccess(int nid);
		
		/**
		 * Called when the server returned 404.
		 * @param nid The node id.
		 */
		public void onNotFound(int nid);
		
		/**
		 * Called when the there was an error.
		 * @param error The ErrorState which represents the caus for the error.
		 */
		public void onError(ErrorState error);
	}
}
