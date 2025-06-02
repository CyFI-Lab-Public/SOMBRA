# SOMBRA

This is the codebase of SOMBRA for 2025 CCS AE. Its main functinality is matching app-side accessibility(a11y)-protected views with their mobile browser-rendered counterparts that are unprotected and visible to a11y services.
SOMBRA consists of an app-side a11y-protected view scanning module and an element matching module that finds the browser-rendered a11y-unprotectd elements that corresponds to those app-side views.

## App-side A11y-Protected View Scanning
This module discovers app-side a11y-protected views for an app both statically and dynamically.

### Static Scanning

#### Software Requirements
1. Python 3. This is tested with version 3.10.13, but any recent versions should also work.

#### Usage
Next, we show an example of running a static a11y-protected view scanning.

1. `cd` to the SOMBRA base directory.
2. `pip install -r requirements.txt`
3. `mkdir result`
4. Scan a11y protected views. Run `python3 scanA11yProtectedViews.py test/com.cvs.launchers.cvs result/a11yProtectedViews_com.cvs.launchers.cvs.json`. The `com.cvs.launchers.cvs` directory contains the [JADX](https://github.com/skylot/jadx) decompiled code of the app `com.cvs.launchers.cvs.apk`.