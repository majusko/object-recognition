package org.object.recognition;

import java.util.List;

import org.object.recognition.Shape;
import org.object.recognition.Color;
import org.object.recognition.Detection;
import org.opencv.core.MatOfPoint;

import android.util.Log;

public class Traffic extends Detection {

	/**
	 * zakazovacie
	 */
	
	public void detectAllSign(){
 		detectRedCircleSign();
 		detectBlueCircleSign();
 		//detectRedTriangleSign();
	}
	
	public void detectRedCircleSign(){
		for(int i=30;i<=145;i=i+10){
			List<MatOfPoint> contours = Color.Red(i);
			if(contours.size()>0){
				Shape.Circle(contours, 0);
			}
			if(signList.size()>0){
				Log.i("DetectionCore_detectRedCircleSign", "Saturation value "+i);
				return;
			}
		}
	}
	
	public void detectBlueCircleSign(){
		List<MatOfPoint> contours = Color.Blue();
		if(contours.size()>0){
			Shape.Circle(contours, 0);
		}
	}
	
	public void detectRedTriangleSign(){
		List<MatOfPoint> contours = Color.Red(60);
		if(contours.size()>0){
			Shape.Triangle(contours, 0);
		}
	}

}
