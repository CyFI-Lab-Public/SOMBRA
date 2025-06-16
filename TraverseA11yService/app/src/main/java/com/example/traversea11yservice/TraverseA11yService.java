package com.example.traversea11yservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class TraverseA11yService extends AccessibilityService {

    private void traverseAndLogSensitiveViews(AccessibilityNodeInfo node) {
        if (node == null) return;

        boolean isSensitive = node.isAccessibilityDataSensitive();
        boolean isPassword = node.isPassword();
        boolean notVisible = !node.isVisibleToUser();
        boolean notClickable = !node.isClickable();
        boolean notFocusable = !node.isFocusable();
        boolean notEnabled = !node.isEnabled();
        StringBuilder flags = new StringBuilder();

        if (isSensitive) {
            flags.append("Sensitive ");
        }
        if (isPassword) {
            flags.append("Password ");
        }
        if (notVisible) {
            flags.append("NotVisible ");
        }
        if (notClickable) {
            flags.append("NotClickable ");
        }
        if (notFocusable) {
            flags.append("NotFocusable ");
        }
        if (notEnabled) {
            flags.append("NotEnabled ");
        }

        if (isSensitive || isPassword || notVisible || notClickable || notFocusable || notEnabled) {
            String className = (node.getClassName() != null) ? node.getClassName().toString() : "null";
            String viewId = (node.getViewIdResourceName() != null) ? node.getViewIdResourceName() : "null";
            String contentDescription = (node.getContentDescription() != null) ? node.getContentDescription().toString() : "null";
            String hintText = (node.getHintText() != null) ? node.getHintText().toString() : "null";
            if (viewId.contains(getString(R.string.target_view))) {
                MyLogger logger = (MyLogger) getApplicationContext();
                if (!logger.viewIDs.contains(viewId)) {
                    logger.viewIDs.add(viewId);
                    logger.addViewMeta(new ViewMeta("1", "0", "", "", "", hintText, "", className, viewId));
                    Toast.makeText(this,"Sensitive view found: " + className + " with ID: " + viewId + " Text: " + hintText + " Description: " + contentDescription, Toast.LENGTH_SHORT).show();
                }
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            traverseAndLogSensitiveViews(child);
            if (child != null) {
                child.recycle();
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            traverseAndLogSensitiveViews(root);
        }

//        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
//            AccessibilityNodeInfo curNode = event.getSource();
//            return;
//        }
//        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
//            AccessibilityNodeInfo curNode = event.getSource();
//            return;
//        }
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//            AccessibilityNodeInfo curNode = event.getSource();
//            return;
//        }
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            AccessibilityNodeInfo curNode = event.getSource();
//            return;
//        }
        return;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {
        return;
    }
}
