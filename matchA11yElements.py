import os, re, sys, csv
import pandas as pd
from bs4 import BeautifulSoup

def view_id_keywords(view_id: str) -> list[str]:
    return [p.lower() for p in re.split(r'[_\-\W]+|(?<=[a-z])(?=[A-Z])', view_id) if p]

def label_for(soup, inp_id):
    return soup.find("label", attrs={"for": inp_id})

def helper_for(soup, inp):
    hid = inp.get("aria-describedby", "")
    return soup.find(id=hid) if hid else None

def hit_keywords(kw, *texts):
    blob = " ".join(texts).lower()
    return any(k in blob for k in kw)


def find_matches_in_page(soup: BeautifulSoup, html_name: str,
                         view_id: str, trace_id) -> list[dict]:
    kw = view_id_keywords(view_id)
    rows = []
    for inp in soup.find_all("input"):
        lbl = label_for(soup, inp.get("id", ""))
        helper = helper_for(soup, inp)
        lbl_txt = lbl.get_text(strip=True) if lbl else ""
        helper_txt = helper.get_text(strip=True) if helper else ""
        if hit_keywords(kw, lbl_txt, helper_txt):
            rows.append({
                "trace_id":          trace_id,
                "matched_view_id":   view_id,
                "page":              html_name,
                "input_id":          inp.get("id", ""),
                "input_name":        inp.get("name", ""),
                "input_type":        inp.get("type", ""),
                "label_text":        lbl_txt,
                "helper_text":       helper_txt,
                "aria-label":        inp.get("aria-label", ""),
                "aria-labelledby":   inp.get("aria-labelledby", ""),
                "aria-describedby":  inp.get("aria-describedby", ""),
                "aria-hidden":       inp.get("aria-hidden", "")
            })
    return rows


def main(csv_path: str, html_dir: str, out_csv: str):
    traverse = pd.read_csv(csv_path)
    targets  = traverse.loc[traverse.groupby("trace_id")["step_index"].idxmax()]

    matches: list[dict] = []
    for fname in os.listdir(html_dir):
        if not fname.endswith(".html"):
            continue
        with open(os.path.join(html_dir, fname), encoding="utf-8") as fh:
            soup = BeautifulSoup(fh.read(), "html.parser")
        for _, row in targets.iterrows():
            vid = str(row["view_id"])
            if vid and vid.lower() != "nan":
                matches.extend(find_matches_in_page(soup, fname, vid, row["trace_id"]))

    if matches:
        pd.DataFrame(matches).to_csv(out_csv, index=False, quoting=csv.QUOTE_MINIMAL)
        print(f"{len(matches)} matches written to {out_csv}")
    else:
        print("No matching elements found.")

    if any(m.get("aria-hidden", "").lower() == "true" for m in matches):
        print("Some matched elements have aria-hidden=\"true\" — they’re hidden from generic a11y services.")
    else:
        print("No aria-hidden labels defined, elements are unprotected.")


if __name__ == "__main__":
    if len(sys.argv) != 4:
        sys.exit("Usage:\n  python matchA11yElements.py "
                 "<app_traverse.csv> <html_folder> <output.csv>")
    main(sys.argv[1], sys.argv[2], sys.argv[3])
