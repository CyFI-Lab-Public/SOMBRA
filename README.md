# SOMBRA

This is the codebase of SOMBRA for 2025 CCS AE. Its main functionality is matching app-side accessibility(a11y)-protected views with their mobile browser-rendered counterparts that are unprotected and visible to a11y services.
SOMBRA consists of an app-side a11y-protected view scanning module and an element matching module that finds the browser-rendered a11y-unprotectd elements that corresponds to those app-side views.

## App-side A11y-Protected View Scanning
This module discovers app-side a11y-protected views for an app both statically and dynamically.

### Static Scanning

#### Software Requirements
1. Python 3. This is tested with version 3.10.13, but any recent versions should also work.

#### Usage
Next, we show an example of running a static a11y-protected view scanning.

1. `cd` to the SOMBRA base directory.
2. `mkdir result`
3. Scan a11y protected views. Run `python3 scanA11yProtectedViews.py test/com.cvs.launchers.cvs result/a11yProtectedViews_com.cvs.launchers.cvs.json`. The `com.cvs.launchers.cvs` directory contains the [JADX](https://github.com/skylot/jadx) decompiled code of the app `com.cvs.launchers.cvs.apk`.
4. Scan a11y sensitive views. Run `python3 scanSensitiveViews.py test/com.cvs.launchers.cvs result/a11ySensitiveViews_com.cvs.launchers.cvs.json`.

#### Interpretation of Result
The `result/a11yProtectedViews_com.cvs.launchers.cvs.json` contains the file path, line number and the type of statically-defined app-side a11y protected views, such as ones being protected by `setAccessibilityDataSensitive`.
The `result/a11ySensitiveViews_com.cvs.launchers.cvs.json` contains the file path, line number and the type of statically-defined app-side sensitive views, which in this case are views protected with `filterTouchesWhenObscured` flag.

### (Optional) Dynamic Scanning
The `TraverseA11yService` app utilizing the a11y service could further help extract a11y-protected views that are dynamically defined.

#### Hardware Requirement
1. Google Pixel 5 phone running Android 14.

#### Software Requirement
1. [Android Studio](https://developer.android.com/studio?gad_campaignid=21831783525&gbraid=0AAAAAC-IOZkrbRNAmCAvWmjcp5fLeh09A).

#### Usage
1. Import the `TraverseA11yService` folder into Android Studio as an Android application.
2. Build and install the app on the Google Pixel 5 phone running Android 14.
3. Grant the Accessiblity permission to the app in the Settings.
4. Click start button in app, and navigate an test app.

## Matching Unprotected Browser-Side Elements
This module matches the a11y-exposed browser-rendered elements for the app-side protected views.

#### Software Requirements
1. Python 3. This is tested with version 3.10.13, but any recent versions should also work.

#### Usage
Next, we show an example of matching browser-side elements.
1. `cd` to the SOMBRA base directory.
2. `pip install -r requirements.txt`
3. `mkdir result`
4. Run `python3 matchA11yElements.py test/com.cvs.launchers.cvs_app_traverse.csv test/cvs.com/ result/browserSideUnprotectedElements_cvs.com.csv`. The `test/com.cvs.launchers.cvs_app_traverse.csv` contains 2 `EditText` input boxes on the app-side that are a11y-protected as well as the `Button` clicking sequence from the main page to reach them. The `test/cvs.com/` contains the html for the mobile Chrome-rendered website including the account, payment, and finally the add card page.

#### Interpretation of Result
The `result/browserSideUnprotectedElements_cvs.com.csv` contains the matched browser-side elements to the `security code` field that is a11y-protected on the app-side. It also shows that there is no `aria-hidden` label defined for this element and that it is unprotected.