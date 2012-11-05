package org.object.recognition;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * 
 * class ActivityView
 * - the class who care mainly about android functionalities
 * 
 * @author majko
 *
 */

public class ActivityView extends Activity {
    private static final String TAG = "Sample::Activity";

    private MenuItem            mItemPreviewRGBA;
    private MenuItem            mItemPreviewGray;
    private MenuItem            mItemPreviewCanny;
    private MenuItem            mItemPreviewColor;
    private MenuItem            mItemPreviewCircles;
    private MenuItem            mItemPreviewLines;
    private ActivityFunctionality         mView;

    private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {
    	@Override
    	public void onManagerConnected(int status) {
    		switch (status) {
				case LoaderCallbackInterface.SUCCESS:
				{
					Log.i(TAG, "OpenCV loaded successfully");
					// Create and set View
					mView = new ActivityFunctionality(mAppContext);
					setContentView(mView);
					// Check native OpenCV camera
					if( !mView.openCamera() ) {
						AlertDialog ad = new AlertDialog.Builder(mAppContext).create();
						ad.setCancelable(false); // This blocks the 'BACK' button
						ad.setMessage("Fatal error: can't open camera!");
						ad.setButton("OK", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						    }
						});
						ad.show();
					}
				} break;
				default:
				{
					super.onManagerConnected(status);
				} break;
			}
    	}
	};
    
    public ActivityView() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
	protected void onPause() {
        Log.i(TAG, "onPause");
		super.onPause();
		if (mView != null){
			mView.releaseCamera();
		}
	}

	@Override
	protected void onResume() {
        Log.i(TAG, "onResume");
		super.onResume();
		if( (null != mView) && !mView.openCamera() ) {
			AlertDialog ad = new AlertDialog.Builder(this).create();  
			ad.setCancelable(false); // This blocks the 'BACK' button  
			ad.setMessage("Fatal error: can't open camera!");  
			ad.setButton("OK", new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int which) {  
				dialog.dismiss();
				finish();
			    }  
			});  
			ad.show();
		}
	}

    /** 
     * Called when the activity is first created. 
     */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
        	Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
    }

    /**
     * ---FRONT---
     * add items to menu
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewGray = menu.add("Preview GRAY");
        mItemPreviewCanny = menu.add("Preview Canny");
        mItemPreviewLines = menu.add("Preview Lines");
        mItemPreviewCircles = menu.add("Preview Circles");
        mItemPreviewColor = menu.add("Preview Color");
        return true;
    }
    
    /**
     * ---MENU ONCHANGE---
     * set android menu
     * the values are already set in class activity
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemPreviewRGBA) {
        	//set value on 0
        	mView.setViewMode(ActivityFunctionality.VIEW_MODE_RGBA);
        } else if (item == mItemPreviewGray) {
        	//set value on 1
        	mView.setViewMode(ActivityFunctionality.VIEW_MODE_GRAY);
        } else if (item == mItemPreviewCanny) {
        	//set value on 2
        	mView.setViewMode(ActivityFunctionality.VIEW_MODE_CANNY);
        } else if (item == mItemPreviewLines) {
        	//set value on 3
        	mView.setViewMode(ActivityFunctionality.VIEW_LINES_MODE);
        } else if (item == mItemPreviewColor) {
        	//set value on 3
        	mView.setViewMode(ActivityFunctionality.VIEW_COLOR_MODE);
        } else if (item == mItemPreviewCircles) {
        	//set value on 3
        	mView.setViewMode(ActivityFunctionality.VIEW_CIRCLE_MODE);
        }
        return true;
    }
}
