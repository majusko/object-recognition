package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;

public class Detection {
	protected static Bitmap image;
	protected static List<Mat> signList;
	protected static List<Rect> boxList;
	protected static Mat hierarchy;
	
	private Mat escape_mat;
	
	public Detection(Bitmap image){
		Detection.image=image;
		this.reset();
	}
	
	public Detection(){
		Detection.image=null;
		this.reset();
	}
	
	public void setData(byte[] data,int width, int height){
		Mat mYuv=new Mat(height + height / 2, width, CvType.CV_8UC1);
		Mat mRgba=new Mat();
		mYuv.put(0, 0, data);
		
		Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
		Detection.image=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Utils.matToBitmap(mRgba, Detection.image);
		
		mYuv.release();
		mRgba.release();
		this.reset();
	}
	
	public void setData(Mat mRgba){
		Detection.image=Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mRgba, Detection.image);
		this.reset();
	}
	
	public Mat getData(){
		escape_mat = new Mat();
		Utils.bitmapToMat(Detection.image, escape_mat);
		return escape_mat;
	}
		
	public void reset(){
		signList = new ArrayList<Mat>();
		boxList = new ArrayList<Rect>();
	}
	
	public List<Mat> getSignList(){
		return signList;
	}
	
	public List<Rect> getBoxList(){
		return boxList;
	}
	
}