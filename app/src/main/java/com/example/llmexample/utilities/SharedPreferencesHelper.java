package com.example.llmexample.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "LearningAppPrefs";

    public static void saveUsername(Context context, String username) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("username", "Student");
    }

    public static void saveInterests(Context context, String interests) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("interests", interests);
        editor.apply();
    }

    public static String getInterests(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("interests", "Programming,Math");
    }

    public static void saveTopic(Context context, String topic) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("topic", topic);
        editor.apply();
    }

    public static String getTopic(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("topic", "General"); // Default to "General" if not set
    }
    
    public static void saveSubscriptionLevel(Context context, String level) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("subscription_level", level);
        editor.apply();
    }

    public static String getSubscriptionLevel(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("subscription_level", "free");
    }
}
