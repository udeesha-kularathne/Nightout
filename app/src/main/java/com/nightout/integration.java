package com.nightout;

import android.content.Context; // Import Android Context

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

public class integration {


    // Initialize Python with a Context parameter
    public static void initializePython(Context context) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    // Integration for Python file 1
    // Call the Python function to insert a record
    public static String insertRecord1() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("1_predicting_events");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");

        return null;
    }    // Call the Python function to insert a record
    String record1 = insertRecord1();


    public static String insertRecord2() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("1_identifying_similar_users");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");
        return null;
    }
    String record2 = insertRecord2();
    public static String insertRecord3() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("2_collaborative_filtering");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");

        return null;
    }    // Call the Python function to insert a record
    String record3 = insertRecord3();

    public static String insertRecord4() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("2_organizations_for_communities");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");
        return null;
    }
    String record4 = insertRecord4();

    public static String insertRecord5() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("3_building_communities");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");

        return null;
    }    // Call the Python function to insert a record
    String record5 = insertRecord5();
// Call the Python function to insert a record
    public static String insertRecord6() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("3_identifying_verified_users");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");
        return null;
    }    // Call the Python function to get recommendations
    String record6 = insertRecord6();
    // Call the Python function to get recommendations
    public static String insertRecord7() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("4_predicting_categories");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");

        return null;
    }    // Call the Python function to insert a record

    public static String insertRecord8() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("4_profit_maximazation");

        // Call the Python function insert_record if it exists
        module.callAttr("insert_record");
        return null;
    }    // Call the Python function to get recommendations
    String record8 = insertRecord8();
    // Call the Python function to get recommendations
    // Call the Python function to get recommendations
    public static List<String> getRecommendations(String title) {
        Python python = Python.getInstance();
        PyObject module = python.getModule("1_predicting_events");

        // Call the Python function get_recommendations with the provided title
        PyObject result = module.callAttr("get_recommendations", title);

        // Convert the PyObjects to Strings
        List<String> recommendations = new ArrayList<>();
        for (PyObject pyObject : result.asList()) {
            recommendations.add(pyObject.toString());
        }

        // Return the list of Strings
        return recommendations;
    }

    // Call the Python function to delete the last record
    public static void deleteLastRecord() {
        Python python = Python.getInstance();
        PyObject module = python.getModule("1_predicting_events");

        // Call the Python function delete_last_record if it exists
        module.callAttr("delete_last_record");
    }


}
