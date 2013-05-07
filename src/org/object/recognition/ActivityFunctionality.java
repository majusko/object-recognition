package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.object.recognition.Traffic;

/**
 * 
 * class ActivityFunctionality
 * - class have many methodes, which are needed in class ActivityView
 * 
 * @author majko
 *
 */

class ActivityFunctionality extends BaseClass {

	public static final int     VIEW_MODE_RGBA  = 0;
    public static final int     VIEW_MODE_GRAY  = 1;
    public static final int     VIEW_MODE_CANNY = 2;
    public static final int     VIEW_LINES_MODE = 3;
    public static final int     DETECT_RED_CIRCLE_TS = 4;
    public static final int     DETECT_RED_TRIANGLE_TS = 5;
    public static final int     DETECT_BLUE_CIRCLE_TS = 6;
    public static final int     DETECT_ALL_TS = 7;
    
    
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;
    private Mat lines;
    List<MatOfPoint> contours;
	List<Rect> boxList;
	List<Mat>  signList;
	private Bitmap mBitmap;
	private int mViewMode;
	private int lineGap;
	private int minLineSize;
	private int threshold;
	private Traffic traffic;


    public ActivityFunctionality(Context context) {
        super(context);
        mViewMode = VIEW_MODE_RGBA;
    }

	@Override
	protected void onPreviewStarted(int previewWidth, int previewHeight) {
	    synchronized (this) {
        	// incializuju sa matice pred zacatim
        	mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
        	mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());
        	
        	mRgba = new Mat();
        	mIntermediateMat = new Mat();
        	lines = new Mat();

        	mBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888); 
        	
        	contours = new ArrayList<MatOfPoint>();
        	boxList = new ArrayList<Rect>();
        	signList = new ArrayList<Mat>();
        	
        	traffic = new Traffic();
        	
    	    }
	}

	@Override
	protected void onPreviewStopped() {
		if(mBitmap != null) {
			mBitmap.recycle();
		}

		synchronized (this) {
            // Explicitly deallocate Mats
            if (mYuv != null)
                mYuv.release();
            if (mRgba != null)
                mRgba.release();
            if (mGraySubmat != null)
                mGraySubmat.release();
            if (mIntermediateMat != null)
                mIntermediateMat.release();

            mYuv = null;
            mRgba = null;
            mGraySubmat = null;
            mIntermediateMat = null;
        }
    }

	
	/**
	 * hlavna funkcionalita
	 * - zavisla na vybere v menu
	 */
	
    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);

        final int viewMode = mViewMode;

        switch (viewMode) {
        //spusti sa farebny mod
        case VIEW_MODE_GRAY:
            Imgproc.cvtColor(mGraySubmat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
            //spusti sedu
        case VIEW_MODE_RGBA:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
            //spusti canny
        case VIEW_MODE_CANNY:
            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            break;
            //vyhlada ciary
        case VIEW_LINES_MODE:
        	threshold = 50;
        	minLineSize = 10;
        	lineGap = 5;
        	Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
        	Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
        	Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);
        	for (int x = 0; x < lines.cols(); x++){
        		double[] vector = lines.get(0, x);
                Point start = new Point(vector[0], vector[1]);
                Point end = new Point(vector[2], vector[3]);
                Core.line(mRgba, start, end, new Scalar(255,0,0,255), 3);
            }
            break;
            //vyhlada zakazove znacky
        case DETECT_RED_CIRCLE_TS:
        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
	        traffic.setData(mRgba);
	        traffic.prohibSign();
		    boxList.clear();
	    	boxList = traffic.getBoxList();
	    	signList.clear();
	    	signList = traffic.getSignList();
		    //draw 
			for(int i = 0; i < boxList.size(); i++){
			  Rect r=boxList.get(i);
			  Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
			}
            break;
            //vyhlada prikazove znacky
        case DETECT_BLUE_CIRCLE_TS:
        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
        	traffic.setData(mRgba);
	        traffic.commandSign();
		    boxList.clear();
	    	boxList = traffic.getBoxList();
	    	signList.clear();
	    	signList = traffic.getSignList();
		    //draw 
			for(int i = 0; i < boxList.size(); i++){
			  Rect r=boxList.get(i);
			  Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
			}
            break;
            //vyhlada  vsetky znacky
    	case DETECT_ALL_TS:
    		Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
    		traffic.setData(mRgba);
	        traffic.detectAllSign();
		    boxList.clear();
	    	boxList = traffic.getBoxList();
	    	signList.clear();
	    	signList = traffic.getSignList();
		    //draw 
			for(int i = 0; i < boxList.size(); i++){
			  Rect r=boxList.get(i);
			  Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
			}
    		break;
        }

        Bitmap bmp = mBitmap;
        
        try {
            Utils.matToBitmap(mRgba, bmp);
        } catch(Exception e) {
            Log.e("org.object.recognition", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            bmp = null;
        }
        return bmp;
    }

    public void setViewMode(int viewMode) {
    	mViewMode = viewMode;
    }

}
