import os
import re
import sys
import json

# find patterns in file
def scan_file(file_path, patterns):

    results = []
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            for line_num, line in enumerate(f, 1):
                for key, pattern in patterns.items():
                    if pattern.search(line):
                        results.append({
                            "line_number": line_num,
                            "pattern": key,
                            "line_text": line.strip()
                        })
    except Exception as e:
        print(f"Error reading {file_path}: {e}")
    return results

# scan all files
def scan_app_folder(app_folder, patterns):

    matches = []
    for root, _, files in os.walk(app_folder):
        for file in files:
            file_path = os.path.join(root, file)
            file_matches = scan_file(file_path, patterns)
            if file_matches:
                matches.append({
                    "file": file_path,
                    "matches": file_matches
                })
    return matches

def main(root_dir, output_file):

    # patterns for sensitive app side views
    patterns = {
        "xml_sensitive": re.compile(r'android:accessibilityDataSensitive="yes"'),
        "set_sensitive": re.compile(r'setAccessibilityDataSensitive\s*\(.*ACCESSIBILITY_DATA_SENSITIVE_YES'),
        "filter_touches": re.compile(r'filterTouchesWhenObscured')
    }

    results = {}

    app_dirs = [d for d in os.listdir(root_dir) if os.path.isdir(os.path.join(root_dir, d))]
    
    if not app_dirs:
        print(f"No app folders found in {root_dir}")
        return

    # scan all apps
    for app in app_dirs:
        app_path = os.path.join(root_dir, app)
        print(f"Scanning app folder: {app}")
        app_results = scan_app_folder(app_path, patterns)
        results[app] = app_results

    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(results, f, indent=4)
        print(f"Results saved to {output_file}")
    except Exception as e:
        print(f"Error writing JSON file: {e}")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python scanSensitiveViews.py <root_directory> <output_json_file>")
        sys.exit(1)
    
    root_directory = sys.argv[1]
    output_file = sys.argv[2]
    if not os.path.isdir(root_directory):
        print(f"Error: {root_directory} is not a valid directory")
        sys.exit(1)
    
    main(root_directory, output_file)
