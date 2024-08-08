import json
import sys

def read_jsonl(file_path):
    with open(file_path, 'r') as f:
        return [json.loads(line) for line in f]

def compare_jsonl(vectors_file, contents_file):
    vectors_data = read_jsonl(vectors_file)
    contents_data = read_jsonl(contents_file)

    vectors_dict = {entry['docid']: entry['vector'] for entry in vectors_data}
    contents_dict = {entry['docid']: entry for entry in contents_data}  # Keep full entry for accurate comparison

    all_docids = set(vectors_dict.keys()).union(contents_dict.keys())

    differences = []

    for docid in sorted(all_docids):
        vector = vectors_dict.get(docid)
        content_entry = contents_dict.get(docid)
        
        if not vector or not content_entry:
            differences.append(f"Missing entry for docid: {docid}")
            continue

        content_docid = content_entry.get('docid')

        if docid != content_docid:
            differences.append(f"Docid mismatch for docid: {docid}, content docid: {content_docid}")
        else:
            if not vector == content_entry.get('vector'):
                differences.append(f"Vector mismatch for docid: {docid}")

    if differences:
        print("Differences found:")
        for difference in differences:
            print(difference)
    else:
        print("No differences found. The files are identical.")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python compare_jsonl.py <vectors_file.jsonl> <contents_file.jsonl>")
        sys.exit(1)

    vectors_file = sys.argv[1]
    contents_file = sys.argv[2]

    compare_jsonl(vectors_file, contents_file)
