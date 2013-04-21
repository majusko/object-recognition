package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.object.recognition.Detection;

public class Color extends Detection {

	public static List<MatOfPoint> Red(int i){
		//Init temp variable
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat mRGBA = new Mat();
		Mat mRGB = new Mat();
		Mat mTemp = new Mat();
		List<Mat> lHSV = new ArrayList<Mat>();
		
		//Get data from Bitmap
		Utils.bitmapToMat(image, mRGBA);
		Imgproc.GaussianBlur(mRGBA,mRGBA,new Size(5, 5),3.5,3.5);
	    Imgproc.cvtColor(mRGBA,mRGB,Imgproc.COLOR_RGBA2RGB);
	    Imgproc.cvtColor(mRGB,mTemp,Imgproc.COLOR_RGB2HSV);
	    Core.split(mTemp,lHSV); //split to channels
	    
	    //filtruje sa H kanál
	    mTemp=new Mat();
	    Core.inRange(lHSV.get(0), new Scalar(10), new Scalar(170), mTemp); //check channel for color
	    Core.bitwise_not(mTemp, mTemp);
	    lHSV.set(0, mTemp); //set the result as first chanel of image
	    
	    //filtruje sa S kanál
	    mTemp = new Mat();
	    Imgproc.threshold(lHSV.get(1), mTemp, i, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(1, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(1), mTemp);

	    Imgproc.Canny(mTemp, mTemp, 100, 50);
	    
	    //nájdu sa kontúry nájdených èervených oblastí
	    hierarchy = new Mat();
	    Imgproc.findContours(mTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	    
		return contours;
	}
	
	public static List<MatOfPoint> Blue(){
		//Init temp variable
		List<MatOfPoint> contours=new ArrayList<MatOfPoint>();
		Mat mRGBA=new Mat();
		Mat mRGB=new Mat();
		Mat mTemp=new Mat();
		List<Mat> lHSV=new ArrayList<Mat>();
		
		//Get data from Bitmap
		Utils.bitmapToMat(image, mRGBA);
		Imgproc.GaussianBlur(mRGBA,mRGBA,new Size(5, 5),1.5,1.5);
	    Imgproc.cvtColor(mRGBA,mRGB,Imgproc.COLOR_RGBA2RGB);
	    Imgproc.cvtColor(mRGB,mTemp,Imgproc.COLOR_RGB2HSV);
	    Core.split(mTemp,lHSV);
	    
	    //Filter the 3 channels HSV to get blue mask
	    
	    //Filter H channel
	    mTemp=new Mat();
	    Core.inRange(lHSV.get(0), new Scalar(90), new Scalar(130), mTemp);
	    lHSV.set(0, mTemp);
	    
	    //Filter S channel
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(1), mTemp, 10, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(1, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(1), mTemp);
	    
	    //Filter V channel
	    lHSV.set(0, mTemp);
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(2), mTemp, 100, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(2, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(2), mTemp);
	    
	    Imgproc.Canny(mTemp, mTemp, 100, 50);
	    
	    //Find contour
	    hierarchy = new Mat();
	    Imgproc.findContours(mTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	    
		return contours;
	}
}
