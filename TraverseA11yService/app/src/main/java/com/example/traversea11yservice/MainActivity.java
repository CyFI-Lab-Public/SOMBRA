package com.example.traversea11yservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonGrant = findViewById(R.id.buttonGrant);
        buttonGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
                List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
                if (enabledServices.isEmpty()) {
                    System.out.println("No enabled accessibility services.");
                }
                else {
                    for (AccessibilityServiceInfo service : enabledServices) {
                        if (service.getId().contains("com.example.traversea11yservice/.TraverseA11yService")) {
                            Toast.makeText(MainActivity.this, "TraverseA11yService is already enabled.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                List<AccessibilityServiceInfo> enabledServices =
                        am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

                for (AccessibilityServiceInfo service : enabledServices) {
                    if (service.getId().contains("com.example.traversea11yservice/.TraverseA11yService")) {
                        String[] packages = service.packageNames;
                        if (packages == null || packages.length == 0) {
                            Toast.makeText(MainActivity.this, "No app to traverse, please declare the app to traverse in accessibility_service_config.xml", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (packages.length != 1) {
                            Toast.makeText(MainActivity.this, "Please only declare one app to traverse in accessibility_service_config.xml", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String pkg = packages[0];
                        Toast.makeText(MainActivity.this, "Starting to traverse: "+pkg, Toast.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            PackageManager pm = getPackageManager();
                            Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid app to traverse.", Toast.LENGTH_SHORT).show();
                            }
                        }, 1000);
                    }
                }
            }
        });

        Button buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(getExternalFilesDir(null), "dumps");
                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, "protected_views.csv");

                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("trace_id,step_index,current_activity,button_text,button_class,view_text,view_a11y_text,view_class,view_id\n");

                    for (ViewMeta meta : ((MyLogger) getApplication()).getProtectedViews()) {
                        writer.append(meta.traceID).append(",");
                        writer.append(meta.stepIndex).append(",");
                        writer.append(meta.currentActivity).append(",");
                        writer.append(meta.buttonText).append(",");
                        writer.append(meta.buttonClass).append(",");
                        writer.append(meta.viewText).append(",");
                        writer.append(meta.viewA11yText).append(",");
                        writer.append(meta.viewClass).append(",");
                        writer.append(meta.viewID).append("\n");
                    }

                    Toast.makeText(MainActivity.this, "Result Saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    System.out.println(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed to save Result", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}