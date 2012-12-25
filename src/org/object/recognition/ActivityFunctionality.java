package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

import org.object.recognition.DetectionCore;

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
    public static final int     VIEW_COLOR_MODE = 4;
    public static final int     VIEW_CIRCLE_MODE = 5;
    
    private Mat mYuv;
    private Mat mHSV;
    private Mat mRgba;
    private Mat mRgba2;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;
    private Mat lines;
    private Mat circles;
    private Mat hierarchy;
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	private Bitmap mBitmap;
	private int mViewMode;
	private int lineGap;
	private int minLineSize;
	private int threshold;
	private Mat mHSVThreshed;
	private int MinRadius;
	private int MaxRadius;
	private int iLineThickness;
	private double dp;
	private double minDist;
	private double param1;
	private double param2;

    public ActivityFunctionality(Context context) {
        super(context);
        mViewMode = VIEW_MODE_RGBA;
    }

	@Override
	protected void onPreviewStarted(int previewWidth, int previewHeight) {
	    synchronized (this) {
        	// initialize Mats before usage
        	mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
        	mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());
        	
        	mRgba = new Mat();
        	mRgba2 = new Mat();
        	mIntermediateMat = new Mat();
        	lines = new Mat();
        	mHSV = new Mat();
        	mHSVThreshed = new Mat();
        	hierarchy = new Mat();

        	mBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888); 
        	
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
	 * major functionality
	 * processFrame method
	 * - it depends on selected value from menu
	 * - work intime, with camera view
	 * lines detect :
	 * http://docs.opencv.org/modules/imgproc/doc/feature_detection.html#houghlinesp
	 */
	
    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);

        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_GRAY:
            Imgproc.cvtColor(mGraySubmat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            //Core.putText(mRgba, "GRAY mode", new Point(10, 100), 3/* CV_FONT_HERSHEY_COMPLEX */, 2, new Scalar(255, 0, 0, 255), 3);
            break;
        case VIEW_MODE_RGBA:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            //Core.putText(mRgba, "RGB mode", new Point(10, 100), 3/* CV_FONT_HERSHEY_COMPLEX */, 2, new Scalar(255, 0, 0, 255), 3);
            break;
        case VIEW_MODE_CANNY:
            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            //Core.putText(mRgba, "CANNY mode", new Point(10, 100), 3/* CV_FONT_HERSHEY_COMPLEX */, 2, new Scalar(255, 0, 0, 255), 3);
            break;
        case VIEW_LINES_MODE:
        	threshold = 50;
        	minLineSize = 10;
        	lineGap = 5;
        	Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
        	Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
        	Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);
        	for (int x = 0; x < lines.cols(); x++){
        		double[] vec = lines.get(0, x);
                double x1 = vec[0];
                double y1 = vec[1];
                double x2 = vec[2];
                double y2 = vec[3];
                Point start = new Point(x1, y1);
                Point end = new Point(x2, y2);
                Core.line(mRgba, start, end, new Scalar(255,0,0,255), 3);
            }
            break;
        case VIEW_COLOR_MODE:
        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
        	DetectionCore detect = new DetectionCore();
        	List<Rect> boxList = new ArrayList<Rect>();
        	List<Mat>  signList = new ArrayList<Mat>();
	        detect.setData(mRgba);
		    detect.detectAllSign();
		    boxList.clear();
	    	boxList=detect.getBoxList();
	    	signList.clear();
	    	signList=detect.getSignList();
		    	  	    
		    //draw 
			int n=boxList.size();
			      	
			for(int i=0;i<n;i++){
			  Rect r=boxList.get(i);
			  Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
			}
        	
            //Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_BGR2HSV,3);
        	/*
            Core.inRange(mRgba, new Scalar(0, 100, 30), new Scalar(5, 255, 255), mHSVThreshed);
            Imgproc.cvtColor(mHSVThreshed, mRgba, Imgproc.COLOR_GRAY2RGB, 4);
            Imgproc.cvtColor(mRgba, mRgba2, Imgproc.COLOR_BGR2RGBA, 4);
            Bitmap bmp = Bitmap.createBitmap(mRgba2.cols(), mRgba2.rows(), Bitmap.Config.ARGB_8888);
            */
       
            break;
        case VIEW_CIRCLE_MODE:
        	param1 = 200;
        	param2 = 100;
        	MinRadius = 0;
        	MaxRadius = 0;
        	dp = 2;
        	Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
        	Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
        	//Imgproc.GaussianBlur(mRgba, mIntermediateMat, new Size(9,9), 2, 2); //fungujuca funkcia na jemne rozmazanie obrazu (zlepsi kontury)
        	//Imgproc.findContours(mRgba, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        	Imgproc.findContours(mIntermediateMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //	Imgproc.Canny(mIntermediateMat, mIntermediateMat, 80, 100);
        //	Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
        	//nefungujuca funkcia na hladanie kruhov
        	/*
        	Imgproc.HoughCircles(mIntermediateMat, circles, Imgproc.CV_HOUGH_GRADIENT, 
        			dp, mIntermediateMat.rows() / 4, param1, param2,  
        			MinRadius, MaxRadius);
        	*/
        	/*	
        	
        	if (circles.cols() > 0){
        	    for(int x = 0; x < circles.cols(); x++){
        	        double vCircle[] = circles.get(0,x);
        	        
        	        if (vCircle == null){
        	            break;
        	        }
        	        Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
        	        int radius = (int)Math.round(vCircle[2]);
        	        // draw the found circle
        	        Core.circle(mRgba, pt, radius, new Scalar(0,255,0), iLineThickness);
        	        Core.circle(mRgba, pt, 3, new Scalar(0,0,255), iLineThickness);
        	    }
        	}*/
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
