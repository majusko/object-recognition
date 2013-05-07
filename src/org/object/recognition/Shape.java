package org.object.recognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.object.recognition.Detection;

public class Shape extends Detection {

	public static void Circle(List<MatOfPoint> contours, int index){
		int i = index;
		Mat mRGBA = new Mat();
	    Utils.bitmapToMat(image, mRGBA);
	    //cyklus s podmienkou na konci
    	do{
    		int buff[] = new int[4];
		    hierarchy.get(0, i, buff);
		    
		    //Get contour form list
		    Mat contour = contours.get(i);
		    
		    //id kont�ry
		    int id = i;
		    
		    //dostaneme �a��ie id kont�ry
		    i = buff[0];
		    
		    //zis�ujeme �i m�me dostato�ne ve�k� kont�ru aby sme sa �ou v�bec zaoberali
		    if(Imgproc.contourArea(contour) > 500){
		    	
		    	List<Point> points = new ArrayList<Point>();
		    	
		    	//dostaneme celkov� po�et kont�r
		    	int num = (int)contour.total();
		    	
		    	//vytvor�me si pole o dvojn�sobnej ve�kosti samotnej kontury
		    	int temp[] = new int[num * 2];
		    	
		    	//na��tame si kont�ru do do�asnej premennej
		    	contour.get(0, 0, temp);
		    	
		    	//konvertujeme  List<Point> do MatOfPoint2f pre pou�itie fitEllipse
		    	for(int j = 0; j < num * 2; j = j + 2){
		    		points.add(new Point(temp[j], temp[j+1]));
		    	}
		    	MatOfPoint2f specialPointMtx = new MatOfPoint2f(points.toArray(new Point[0])); 
		    	
		    	//do premennej bound uklad�me dokonal� elipsu
		    	RotatedRect bound = Imgproc.fitEllipse(specialPointMtx);
		    	
		    	//Vypo��ta sa hodnota pi
		    	double pi = Imgproc.contourArea(contour) / ((bound.size.height / 2) * (bound.size.width / 2));
		    	
		    	//zis�ujeme toleranciu pi - zaoplenie
                if (Math.abs(pi - 3.14) > 0.03){
                	int k = buff[2];
                	//zis�ujeme �i existuje nejak� rodi� kont�ry
                	if (k != -1){
                        Circle(contours, k);
                	}
                    continue;
                }
                
                //konvertujeme MatOfPoint2f do MatOfPoint  pre funckiu fitEllipse - rozdie� je len v 32-bit float a 32-bit int 
		    	MatOfPoint  NewMtx = new MatOfPoint(specialPointMtx.toArray());
                //dostaneme s�radnice najmen�ieho mo�n�ho �tvorca
                Rect box = Imgproc.boundingRect(NewMtx);
                //nacita obrazok znova
                Mat mat_for_count = new Mat();
                Utils.bitmapToMat(image, mat_for_count);
                //vytvori sa klon stvorca - dobry kandidat pre vyhladanie
                Mat candidate = ((mat_for_count).submat(box)).clone();
        	    //napln maticu binarnou ciernou
        	    Mat mask = new Mat(box.size(), candidate.type(), new Scalar(0,0,0));
        	    //naplni ciernu plochu bielimi konturami	    
                Imgproc.drawContours(mask, contours, id, new Scalar(255,255,255), -1 , 8, hierarchy, 0, new Point(-box.x,-box.y));
                //ulozi sa kandidat
                Mat roi = new Mat(candidate.size(), candidate.type(), new Scalar(255,255,255));
        	    //ulozia sa len informacie o kandidatovi
                candidate.copyTo(roi, mask);
        	    
		    	double longAxis;
                double shortAxis;
                //ziska dve osy elipsy
	            if (bound.size.height < bound.size.width){
	            	shortAxis = bound.size.height / 2;
	                longAxis = bound.size.width / 2;
	            } else {
	                shortAxis = bound.size.width / 2;
	                longAxis = bound.size.height / 2;
	            }
	            
	            //zastavi sa vyhladavanie pokial je elipsa prilis ovalna
	            if ((longAxis / shortAxis) < 2.0){
	            	signList.add(roi);
	                boxList.add(box);
	            }
	            
            }
		 //zis�uje sa �i je tam e�te �al�� kandid�t
	    }while(i != -1);
	}
	
	/**
	 * metoda Triangle vyhladava trojuholniky
	 * - zatial nefunkcne
	 */
	
	/*
	public static void Triangle(List<MatOfPoint> contours, int index){
		int i=index;
    	do
	    {
    		int buff[] = new int[4];
		    hierarchy.get(0, i, buff);
		    
		    //ziska konturu zo zoznamu
		    Mat contour = contours.get(i);
		    MatOfPoint contour_spec = contours.get(i);
		    int id=i;
		    
		    //Get all the point of this contour
	    	List<Point> points = new ArrayList<Point>();
	    	int num = (int) contour.total(); 
	    	int temp[] = new int[num*2]; 
	    	contour.get(0, 0, temp);
	    	
	    	for(int j=0;j<num*2;j=j+2)
	    		points.add(new Point(temp[j], temp[j+1]));
		    
	    	//Approximate the contour
	    	MatOfPoint2f aprox_contour = new MatOfPoint2f();
	    	MatOfPoint2f  converted_countours = new MatOfPoint2f(contour_spec.toArray());
	    	MatOfPoint2f special_point = new MatOfPoint2f(points.toArray(new Point[0]));
		    
	    	Imgproc.approxPolyDP(converted_countours, aprox_contour, Imgproc.arcLength(special_point, true)* 0.03, true);
		    
		    //Get the next id contour
		    i=buff[0];
		    
		    //Check if this is a triangle
		    if(Imgproc.contourArea(aprox_contour)>200){
		    	if(aprox_contour.total()==3){
		    		
		    		//Get the bound of contour
		    		points = new ArrayList<Point>();
			    	temp = new int[6]; 
			    	//Mat something = new Mat();
			    	aprox_contour.get(0, 0, temp);
			    	
			    	
			    	for(int j=0;j<6;j=j+2){
			    		points.add(new Point(temp[j], temp[j+1]));
			    	}
			    	
			    	MatOfPoint special_point_2 = new MatOfPoint(points.toArray(new Point[0]));
		    		Rect box=Imgproc.boundingRect(special_point_2);
		    		Mat ret_val = new Mat();
		    		Utils.bitmapToMat(image, ret_val);
	                Mat candidate=((ret_val).submat(box)).clone();
	                                  
	        	    //ziska masku kuntur
	        	    Mat mask=new Mat(box.size(),candidate.type(), new Scalar(0,0,0));
	        	    
	        	    //nakresli konturu
	                Imgproc.drawContours(mask, contours, id, new Scalar(255,255,255), -1 , 8, hierarchy, 0, new Point(-box.x,-box.y));
	                
	                Mat roi=new Mat(candidate.size(), candidate.type(), new Scalar(255,255,255));
	                
	        	    candidate.copyTo(roi, mask);
	        	    
	        	    signList.add(roi);
                	boxList.add(box);
                	
		    	}
		    	else{
		    		int k=buff[2];
                	if (k!=-1)
                		Triangle(contours,k);
                }
		    }
		    
	    }while(i!=-1);
	}
	*/

}
