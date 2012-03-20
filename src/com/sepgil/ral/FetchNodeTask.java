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

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fetches a node.
 * @author Sebastian Gilits
 *
 */
public class FetchNodeTask extends DrupalOperationTask {
	public enum ErrorState {
		ERROR_PARSE,
		ERROR_HTTP,
	}
	
	private int mNodeID;
	private OnNodeFetchedListener mListener = null;

	/**
	 * Create a new task which should fetch the node with the given node.
	 * @param nodeID The node id of the node, which should be fetched.
	 */
	public FetchNodeTask(int nodeID) {
		mNodeID = nodeID;
	}
	
	/**
	 * Register a callback to be invoked when the node was fetched.
	 * @param listener The callback that will run.
	 */
	public void setOnNodeFetchedListener(OnNodeFetchedListener listener) {
		mListener = listener;
	}

	@Override
	public String getPath() {
		return "/node/" + mNodeID;
	}

	@Override
	public HttpRequestBase getHttpRequest(String url) {
		return new HttpGet(url);
	}

	@Override
	public void onResponse(JSONObject response) {
		if (mListener==null)
			return;
		try {
			mListener.onNodeFetched(new Node(response));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mListener.onError(ErrorState.ERROR_PARSE);
		}
	}

	@Override
	public void onJSONError(JSONException e) {
		if (mListener==null)
			return;
		mListener.onError(ErrorState.ERROR_PARSE);
	}

	@Override
	public void onHttpError(StatusLine statusLine) {
		if (mListener==null)
			return;
		switch (statusLine.getStatusCode()) {
		case HttpStatus.SC_FORBIDDEN:
			mListener.onNoAccess(mNodeID);
			break;
		case HttpStatus.SC_NOT_FOUND:
			mListener.onNotFound(mNodeID);
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
	 * Interface definition for a callback to be invoked when the node was fetched (or not).
	 * @author Sebastian Gilits
	 *
	 */
	public interface OnNodeFetchedListener {
		public void onNodeFetched(Node node);
		public void onNoAccess(int nid);
		public void onNotFound(int nid);
		public void onError(ErrorState error);
	}
}
