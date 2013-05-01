package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Color;


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
	
	/*
	public double[] toData(Bitmap bm) throws Exception {
		if(bm.getHeight()!=30||bm.getWidth()!=30)
			throw new Exception("Image size must be 30*30");
		
		List<Double> Data = new ArrayList<Double>();
		
        double[][] temp=new double[30][30];
        int pixel;
        double total_R=0, total_G=0, total_B=0, total_temp=0;
		int r,g,b;
		
        for (int i = 0; i < 30; i++){
            for (int j = 0; j < 30; j++){
            	pixel= bm.getPixel(i, j);
            	
            	r=(int)Color.red(pixel);
            	g=(int)Color.green(pixel);
            	b=(int)Color.blue(pixel);
            	
                total_R += r;
                total_G += g;
                total_B += b;

                temp[i][j] = r * .3 + g * .59 + b * .11;
                total_temp += temp[i][j];
            }
        }
        
      //Calculate the average value;
        total_R = (total_R / 900) / 256; Data.add(total_R);
        total_G = (total_G / 900) / 256; Data.add(total_G);
        total_B = (total_B / 900) / 256; Data.add(total_B);

        double Threshold=(total_temp / 900);
        
      //Calculate the horizontal parameters
        for (int i = 0; i < 30; i++){
            total_temp = 0;
            for (int j = 0; j < 30; j++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
      //Calculate the vertical parameters
        for (int j = 0; j < 30; j++){
            total_temp = 0;
            for (int i = 0; i < 30; i++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
		double result[]=new double[63];
		for (int i = 0; i < 63; i++)
        {
            result[i] = (double)Data.get(i);
        }
		
		return result;
	}
	*/
}