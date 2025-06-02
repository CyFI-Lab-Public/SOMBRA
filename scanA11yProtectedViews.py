import os
import re
import sys
import json
import xml.etree.ElementTree as ET

def extract_package_name(manifest_path):

    try:
        tree = ET.parse(manifest_path)
        root = tree.getroot()
        package_name = root.attrib.get('package')
        return package_name
    except Exception as e:
        print(f"Error parsing {manifest_path}: {e}")
        return None

def scan_file(file_path, patterns):

    results = []
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            for line_number, line in enumerate(f, 1):
                for key, pattern in patterns.items():
                    if pattern.search(line):
                        results.append({
                            "line_number": line_number,
                            "pattern": key,
                            "line_text": line.strip()
                        })
    except Exception as e:
        print(f"Error reading {file_path}: {e}")
    return results

def scan_app_folder(app_folder, patterns):

    app_results = []
    for root, _, files in os.walk(app_folder):
        for file in files:
            file_path = os.path.join(root, file)
            file_matches = scan_file(file_path, patterns)
            if file_matches:
                app_results.append({
                    "file": file_path,
                    "matches": file_matches
                })
    return app_results

def find_manifest(app_folder):

    resources_manifest = os.path.join(app_folder, "resources", "AndroidManifest.xml")
    if os.path.isfile(resources_manifest):
        return resources_manifest

    manifest_path = os.path.join(app_folder, "AndroidManifest.xml")
    if os.path.isfile(manifest_path):
        return manifest_path

    for root, _, files in os.walk(app_folder):
        if "AndroidManifest.xml" in files:
            return os.path.join(root, "AndroidManifest.xml")
    return None

def main(root_dir, output_file):

    patterns = {
        "accessibilityDataSensitive": re.compile(r'accessibilityDataSensitive'),
        "setAccessibilityDataSensitive": re.compile(r'setAccessibilityDataSensitive'),
        "ACCESSIBILITY_DATA_SENSITIVE_YES": re.compile(r'ACCESSIBILITY_DATA_SENSITIVE_YES')
    }
    
    results = {}

    for app in os.listdir(root_dir):
        app_folder = os.path.join(root_dir, app)
        if os.path.isdir(app_folder):
            print(f"Scanning app folder: {app}")
            manifest_path = find_manifest(app_folder)
            package_name = extract_package_name(manifest_path) if manifest_path else None
            app_results = scan_app_folder(app_folder, patterns)
            results[app] = {
                "package": package_name if package_name else "Unknown",
                "results": app_results
            }

    try:
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump(results, f, indent=4)
        print(f"Results saved to {output_file}")
    except Exception as e:
        print(f"Error writing JSON file: {e}")

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python scanA11yProtectedViews.py <root_directory> <output_json_file>")
        sys.exit(1)
    
    root_directory = sys.argv[1]
    output_file = sys.argv[2]
    
    if not os.path.isdir(root_directory):
        print(f"Error: {root_directory} is not a valid directory.")
        sys.exit(1)
    
    main(root_directory, output_file)
