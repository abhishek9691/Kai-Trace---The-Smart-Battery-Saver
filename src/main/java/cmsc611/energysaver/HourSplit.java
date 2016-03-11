package cmsc611.energysaver;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by abhi on 12/3/2015.
 */
public class HourSplit {
    private ArrayList<Double> firstThird;
    private ArrayList<Double> middleThird;
    private ArrayList<Double> lastThird;

    public HourSplit(){
        Double[] initialValues = {new Double(0), new Double(0), new Double(0), new Double(0), new Double(0), new Double(0)};
        firstThird = new ArrayList<Double>(Arrays.asList(initialValues));
        lastThird = new ArrayList<Double>(Arrays.asList(initialValues));
        middleThird = new ArrayList<Double>(Arrays.asList(initialValues));
    }
    /*
    Inserts the data point in the appropriate place. This accounts for the weights for each sensor
    when inserting. Also computes the weight based on which sensor is passed in.
     */
    public boolean setNewValues(int minute, ArrayList<Double> newValues){
        if(minute < 20){
            //first 20 minutes
            firstThird = newValues;
            System.out.println("Entered with the first third");
            return true;
        } else if(minute >= 40){
            //last 20 minutes
            lastThird = newValues;
            System.out.println("Entered with the last third");
            return true;
        } else if(minute >= 20 && minute < 40){
            //middle 20 minutes
            middleThird = newValues;
            System.out.println("Entered with the middle third");
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Double> getSensorList(int minute){
        if(minute < 20){
            //first minutes
            System.out.println("Entered with the first third");
            return firstThird;
        } else if(minute >= 40){
            //last minutes
            System.out.println("Entered with the last third");
            return lastThird;
        } else if(minute >= 20 && minute < 40){
            //middle 20 minutes
            System.out.println("Entered with the middle third");
            return middleThird;
        } else {
            return null;
        }
    }
}
