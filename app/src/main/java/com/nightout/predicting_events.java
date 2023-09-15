//package com.nightout;
//
//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
//
//import java.util.List;
//
//public class predicting_events{
//
//    // Initialize Python (typically in your MainActivity or Application class)
//    public static void initializePython() {
//        if (!Python.isStarted()) {
//            Python.start(new AndroidPlatform(context));
//        }
//    }
//
//    // Insert a record using the Python script
//    public static void insertUserRecord() {
//        Python python = Python.getInstance();
//        PyObject userRecommendationModule = python.getModule("user_recommendation");
//
//        // Call the Python function insert_record
//        userRecommendationModule.callAttr("insert_record");
//    }
//
//    // Get recommendations based on a title using the Python script
//    public static List<String> getUserRecommendations(String title) {
//        Python python = Python.getInstance();
//        PyObject userRecommendationModule = python.getModule("user_recommendation");
//
//        // Call the Python function get_recommendations with the provided title
//        PyObject result = userRecommendationModule.callAttr("get_recommendations", title);
//
//        // Convert the result to a list of strings and return it
//        return result.asList();
//    }
//
//    // Delete the last record using the Python script
//    public static void deleteUserRecord() {
//        Python python = Python.getInstance();
//        PyObject userRecommendationModule = python.getModule("user_recommendation");
//
//        // Call the Python function delete_last_record
//        userRecommendationModule.callAttr("delete_last_record");
//    }
//}
