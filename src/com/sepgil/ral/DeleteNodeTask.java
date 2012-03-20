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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Deletes a node.
 * @author Sebastian Gilits
 *
 */
public class DeleteNodeTask extends DrupalOperationTask {
	private int mNid;
	private OnNodeDeletedListener mListener = null;
	
	/**
	 * Constructor
	 * @param node The node which should be deleted.
	 * @throws JSONException
	 */
	public DeleteNodeTask(int nid) {
		this.mNid = nid;
	}
	
	/**
	 * Register a callback to be invoked when the node was deleted.
	 * @param listener The callback that will run.
	 */
	public void setOnNodeDeleteListener(OnNodeDeletedListener listener) {
		mListener = listener;
	}
	
	@Override
	public String getPath() {
		return "/node/" + mNid;
	}

	@Override
	public HttpRequestBase getHttpRequest(String url) {
		return new HttpDelete(url);
	}

	@Override
	public void onResponse(JSONObject response) {
		if (mListener==null)
			return;
		mListener.onNodeDeleted();
	}

	@Override
	public void onJSONError(JSONException e) {
		if (mListener==null)
			return;
		mListener.onNodeDeleted();
	}

	@Override
	public void onHttpError(StatusLine statusLine) {
		if (mListener==null)
			return;
		switch (statusLine.getStatusCode()) {
		case HttpStatus.SC_FORBIDDEN:
			mListener.onNoAccess(mNid);
			break;
		case HttpStatus.SC_NOT_FOUND:
			mListener.onNotFound(mNid);
			break;
		default:
			mListener.onError();
		}
		
		
	}

	@Override
	public void onParserError(Exception e) {
		if (mListener==null)
			return;
		mListener.onError();
	}
	
	/**
	 * Interface definition for a callback to be invoked when the node was deleted (or not).
	 * @author Sebastian Gilits
	 *
	 */
	public interface OnNodeDeletedListener {
		/**
		 * Called when the node was updated.
		 */
		public void onNodeDeleted();
		
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
		public void onError();
	}
	
}
