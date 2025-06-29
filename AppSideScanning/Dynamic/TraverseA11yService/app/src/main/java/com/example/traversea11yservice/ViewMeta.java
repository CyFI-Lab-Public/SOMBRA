package com.example.traversea11yservice;

public class ViewMeta {
    public String traceID;
    public String stepIndex;
    public String currentActivity;
    public String buttonText;
    public String buttonClass;
    public String viewText;
    public String viewA11yText;
    public String viewClass;
    public String viewID;

    public ViewMeta(String traceID, String stepIndex, String currentActivity,
                    String buttonText, String buttonClass, String viewText,
                    String viewA11yText, String viewClass, String viewID) {
        this.traceID = traceID;
        this.stepIndex = stepIndex;
        this.currentActivity = currentActivity;
        this.buttonText = buttonText;
        this.buttonClass = buttonClass;
        this.viewText = viewText;
        this.viewA11yText = viewA11yText;
        this.viewClass = viewClass;
        this.viewID = viewID;
    }
}
