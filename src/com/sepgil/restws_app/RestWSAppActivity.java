package com.sepgil.restws_app;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sepgil.ral.CreateNodeTask;
import com.sepgil.ral.CreateNodeTask.OnNodeCreatedListener;
import com.sepgil.ral.DeleteNodeTask;
import com.sepgil.ral.DeleteNodeTask.OnNodeDeletedListener;
import com.sepgil.ral.Endpoint;
import com.sepgil.ral.FetchNodeTask;
import com.sepgil.ral.FetchNodeTask.ErrorState;
import com.sepgil.ral.FetchNodeTask.OnNodeFetchedListener;
import com.sepgil.ral.Node;
import com.sepgil.ral.TestAuthenticationTask;
import com.sepgil.ral.TestAuthenticationTask.OnAuthenticationTaskResult;
import com.sepgil.ral.UpdateNodeTask;
import com.sepgil.ral.UpdateNodeTask.OnNodeUpdatedListener;

public class RestWSAppActivity extends Activity {
	private enum Operation {
		OPERATION_CREATE,
		OPERATION_UPDATE,
	}

	private static final int DIALOG_NODE_SELECT = 0;
	private static final int DIALOG_LOGIN = 1;
	
	
	private Endpoint mEndpoint = null;
	
	private boolean mLoggedIn = false;
	private Menu menu;
	private Button mBtnSave;
	private EditText mTitleEdit;
	private EditText mBodyEdit;
	private Operation mCurrentOperation = Operation.OPERATION_CREATE;
	private Node mNode;
	private int mUID = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEditLayout();
        
    	String uri = Preferences.getPref(this, "uri", "");
    	String uid = Preferences.getPref(this, "uid", "");
    	String usr = Preferences.getPref(this, "username", "");
    	String pwd = Preferences.getPref(this, "password", "");
    	
    	if (!uri.equals("") && !uid.equals("") && !usr.equals("") && !pwd.equals("")) {
			mEndpoint = new Endpoint(uri, usr, pwd);
			mUID = Integer.parseInt(uid);
    		mBtnSave.setEnabled(true);
    		mLoggedIn = true;
    	}
		createNode(mUID);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.entity_menu, menu);
    	this.menu = menu;
    	
    	// Since the menu use not created until the menu was hit,
    	// this is our starting point to set the menu properties.
    	if (mLoggedIn) {
    		menu.findItem(R.id.menu_create).setEnabled(true);
    		menu.findItem(R.id.menu_load).setEnabled(true);
    		menu.findItem(R.id.menu_delete).setEnabled(true);
    		menu.findItem(R.id.menu_logout).setTitle(getString(R.string.logout));
    	} else {
    		menu.findItem(R.id.menu_create).setEnabled(false);
    		menu.findItem(R.id.menu_load).setEnabled(false);
    		menu.findItem(R.id.menu_delete).setEnabled(false);
    		menu.findItem(R.id.menu_logout).setTitle(getString(R.string.login));
    	}
    	
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case DIALOG_NODE_SELECT:
        	dialog = generateNodeSelectDialog();
        	break;
        case DIALOG_LOGIN:
        	dialog = generateLoginDialog();
        	break;
        }
        return dialog;
    }

    
    private void setEditLayout() {
    	setContentView(R.layout.node_edit);
    	mBtnSave = (Button)findViewById(R.id.btnSave);
    	mTitleEdit = (EditText)findViewById(R.id.txtTitle);
    	mBodyEdit = (EditText)findViewById(R.id.txtBody);
    	
		mBtnSave.setOnClickListener(onSaveClick);
    }
    
    private OnClickListener onSaveClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!mLoggedIn) {
				return ;
			}
			String title = mTitleEdit.getText().toString();
			String body = mBodyEdit.getText().toString();
			mNode.setTitle(title);
			mNode.setBody(body);
			if (title.equals("")) {
				displayToast("Please enter a title for the node.");
				return;
			}
			switch (mCurrentOperation) {
			case OPERATION_CREATE:
				try {
					CreateNodeTask createOp = new CreateNodeTask(mNode);
					createOp.setOnNodeCreatedListener(onNodeCreated);
					createOp.execute(mEndpoint);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				break;
			case OPERATION_UPDATE:
				try {
					UpdateNodeTask updateOp = new UpdateNodeTask(mNode);
					updateOp.setOnNodeUpdatedListener(onNodeUpdated);
					updateOp.execute(mEndpoint);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create:
			createNode(mUID);
			break;
		case R.id.menu_load:
			if (mLoggedIn) {
				showDialog(DIALOG_NODE_SELECT);
			}
			break;
		case R.id.menu_delete:
			if (mLoggedIn) {
				DeleteNodeTask deleteOp = new DeleteNodeTask(mNode.getID());
				deleteOp.setOnNodeDeleteListener(onNodeDeleted);
				deleteOp.execute(mEndpoint);
			}
			break;
		case R.id.menu_logout:
			if (mLoggedIn) {
				logout();
			} else {
				showDialog(DIALOG_LOGIN);
			}
			break;
		}
		return true;
	}
	
	private OnNodeFetchedListener onNodeFetched = new OnNodeFetchedListener() {
		
		@Override
		public void onNodeFetched(Node node) {
			mNode = node;
			mTitleEdit.setText(mNode.getTitle());
			mBodyEdit.setText(mNode.getBody());
			mCurrentOperation = Operation.OPERATION_UPDATE;
			setTitle(getString(R.string.app_name) + " - NID: " + node.getID());
			displayToast("Succesfully loaded the node.");
		}

		@Override
		public void onNoAccess(int nid) {
			displayToast("You are not allowed to acces this node.");
		}

		@Override
		public void onNotFound(int nid) {
			displayToast("Couldn't find the selected node.");
		}

		@Override
		public void onError(ErrorState error) {
			displayToast("An error happend while trying to fetch the node.");
		}
	};
	
	public OnNodeUpdatedListener onNodeUpdated = new OnNodeUpdatedListener() {
		
		@Override
		public void onNotFound(int nid) {
			displayToast("Couldn't find the selected node.");
		}
		
		@Override
		public void onNodeUpdated() {
			displayToast("Succesfully updated the node.");
		}
		
		@Override
		public void onNoAccess(int nid) {
			displayToast("You are not allowed to acces this node.");
		}
		
		@Override
		public void onError(com.sepgil.ral.UpdateNodeTask.ErrorState error) {
			displayToast("An error happend while trying to update the node.");
		}
	};
	
	public OnNodeCreatedListener onNodeCreated = new OnNodeCreatedListener() {
		
		@Override
		public void onNodeCreated(int nid) {
			displayToast("Node Succesfully created.");
			mNode.setID(nid);
			setTitle(getString(R.string.app_name) + " - NID: " + nid);
			mCurrentOperation = Operation.OPERATION_UPDATE;
		}
		
		@Override
		public void onError(com.sepgil.ral.CreateNodeTask.ErrorState error) {
			displayToast("An error happend while trying to create the node.");
		}
	};
	
	public OnNodeDeletedListener onNodeDeleted = new OnNodeDeletedListener() {
		
		@Override
		public void onNotFound(int nid) {
			displayToast("The current node doesn't exists.");
		}
		
		@Override
		public void onNodeDeleted() {
			displayToast("Node was successfully deleted");
			createNode(1);
		}
		
		@Override
		public void onNoAccess(int nid) {
			displayToast("You don't have permissions to delete this node.");
		}
		
		@Override
		public void onError() {
			displayToast("An error happend while trying to delete the node.");
		}
	};
	
	private void displayToast(final String msg) {
		RestWSAppActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(), msg, 10).show();
            }
        });
	}
	
	private void createNode(int author) {
		mNode = new Node(author);
		mTitleEdit.setText("");
		mBodyEdit.setText("");
		setTitle(getString(R.string.app_name) + " - *New node*");
		mCurrentOperation = Operation.OPERATION_CREATE;
	}

	private void loadNode(int value) {
		FetchNodeTask fetchNode = new FetchNodeTask(value);
		fetchNode.setOnNodeFetchedListener(onNodeFetched);
		fetchNode.execute(mEndpoint);
	}
	
	private void enableLoginStuff() {
		menu.findItem(R.id.menu_logout).setTitle(getString(R.string.logout));
    	mBtnSave.setEnabled(true);
		mLoggedIn = true;
		
		menu.findItem(R.id.menu_create).setEnabled(true);
		menu.findItem(R.id.menu_load).setEnabled(true);
		menu.findItem(R.id.menu_delete).setEnabled(true);
		
    	mBtnSave.setEnabled(true);
		mLoggedIn = true;
	}
	
	private void logout() {
		menu.findItem(R.id.menu_logout).setTitle(getString(R.string.login));
		
		menu.findItem(R.id.menu_create).setEnabled(false);
		menu.findItem(R.id.menu_load).setEnabled(false);
		menu.findItem(R.id.menu_delete).setEnabled(false);
		
		mLoggedIn = false;
		mEndpoint = null;
    	mBtnSave.setEnabled(false);
		
		Preferences.delPref(RestWSAppActivity.this, "uri");
		Preferences.delPref(RestWSAppActivity.this, "uid");
		Preferences.delPref(RestWSAppActivity.this, "username");
		Preferences.delPref(RestWSAppActivity.this, "password");
	}
	
	private Dialog generateNodeSelectDialog() {
		AlertDialog.Builder builder = null;
		AlertDialog selectDialog;

		builder = new AlertDialog.Builder(this);

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		builder.setView(input);
		
		builder.setPositiveButton("Load", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadNode(Integer.parseInt(input.getText().toString()));
				mCurrentOperation = Operation.OPERATION_UPDATE;
			}
		});
		
		builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		
		selectDialog = builder.create();
		return selectDialog;
	}
	
	
	private Dialog generateLoginDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View loginView = factory.inflate(R.layout.authentication, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.login));
		builder.setView(loginView);
		builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
			

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String uri = ((EditText)loginView.findViewById(R.id.login_drupal_url)).getText().toString();
				mUID = Integer.parseInt(((EditText)loginView.findViewById(R.id.login_uid)).getText().toString());
				String username = ((EditText)loginView.findViewById(R.id.login_username)).getText().toString();
				String password = ((EditText)loginView.findViewById(R.id.login_password)).getText().toString();
				
				Preferences.setPref(RestWSAppActivity.this, "uri", uri);
				Preferences.setPref(RestWSAppActivity.this, "uid", ""+mUID);
				Preferences.setPref(RestWSAppActivity.this, "username", username);
				Preferences.setPref(RestWSAppActivity.this, "password", password);
				
				mEndpoint = new Endpoint(uri, username, password);
				TestAuthenticationTask task = new TestAuthenticationTask();
				
				task.setOnAuthenticationTaskResult(new OnAuthenticationTaskResult() {
					
					@Override
					public void onSuccess() {
						displayToast("Successfully authenticated.");
						enableLoginStuff();
					}
					
					@Override
					public void onOther() {
						displayToast("Couldn't login due an server error.");
						logout();
					}
					
					@Override
					public void onLoginError() {
						displayToast("Couldn't authenticate, are you sure username and password match?");
						logout();
					}
				});
				task.execute(mEndpoint);
			}
		});
		
		builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		return builder.create();
	}
}

