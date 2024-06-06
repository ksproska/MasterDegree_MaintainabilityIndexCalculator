import os
import difflib
import re


def get_directories_and_common_files(base_path):
    directories = [os.path.join(base_path, d) for d in os.listdir(base_path) if
                   os.path.isdir(os.path.join(base_path, d))]

    file_paths = {}

    for directory in directories:
        for root, _, files in os.walk(directory):
            for file in files:
                rel_path = os.path.relpath(os.path.join(root, file), directory)
                if rel_path in file_paths:
                    file_paths[rel_path].add(directory)
                else:
                    file_paths[rel_path] = {directory}

    common_files = [path for path, dirs in file_paths.items() if len(dirs) == len(directories)]

    return directories, common_files


def parse_method_blocks(lines):
    """Parse the lines of a Java file to find method blocks and return their line ranges."""
    method_pattern = re.compile(
        r"^\s*((?:public|protected|private)\s+)?(?:static\s+)?(?:final\s+)?\S+\s+(\w+)\s*\([^)]*\)\s*(throws\s+\w+(?:\s*,\s*\w+)*)?\s*[{]?\s*$"
    )
    methods = {}
    method_name = None
    start_line = None
    brace_count = 0

    for i, line in enumerate(lines):
        if method_name is None:
            match = method_pattern.match(line)
            if match:
                method_name = match.group(2)
                start_line = i
                brace_count = 1
        elif method_name:
            brace_count += line.count('{')
            brace_count -= line.count('}')
            if brace_count == 0:
                methods[method_name] = (start_line, i)
                method_name = None

    return methods


def classify_changes(block1, block2):
    """Classify changes between two blocks of text."""
    sm = difflib.SequenceMatcher(None, block1, block2)
    added = 0
    deleted = 0
    modified = 0

    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == 'replace':
            for line1, line2 in zip(block1[i1:i2], block2[j1:j2]):
                if line1 != line2:
                    modified += 1
            added += max(0, (j2 - j1) - (i2 - i1))
            deleted += max(0, (i2 - i1) - (j2 - j1))
        elif tag == 'delete':
            deleted += (i2 - i1)
        elif tag == 'insert':
            added += (j2 - j1)
        elif tag == 'equal':
            continue

    return added, deleted, modified


def compare_java_files(file1, file2):
    """Compare two Java files and return a dictionary of method changes."""
    with open(file1, 'r') as f1, open(file2, 'r') as f2:
        lines1 = f1.readlines()
        lines2 = f2.readlines()

    methods1 = parse_method_blocks(lines1)
    methods2 = parse_method_blocks(lines2)

    changes = {}

    for method_name, (start1, end1) in methods1.items():
        if method_name in methods2:
            start2, end2 = methods2[method_name]
            block1 = lines1[start1:end1 + 1]
            block2 = lines2[start2:end2 + 1]

            added, deleted, modified = classify_changes(block1, block2)
            changes_occurred = int(added > 0 or deleted > 0 or modified > 0)

            changes[method_name] = [added, deleted, modified, changes_occurred]

    return changes


def main():
    base_path = 'elasticsearch'
    all_records = ["repository,change_from,change_to,file,method,added,deleted,modified,was_changed"]
    directories, common_files = get_directories_and_common_files(base_path)
    for file in common_files:
        files_to_compare = []
        for directory in directories:
            f = f'{directory}/{file}'
            files_to_compare.append(f)
        changes = compare_java_files(files_to_compare[0], files_to_compare[1])
        change1 = directories[0].replace(f"{base_path}/", "")
        change2 = directories[1].replace(f"{base_path}/", "")
        for change in changes:
            added, deleted, modified, was_changed = changes[change]
            record_line = f"{base_path},{change1},{change2},{file},{change},{added},{deleted},{modified},{was_changed}"
            all_records.append(record_line)
    with open(f"changes.csv", mode="w") as w:
        for line in all_records:
            w.write(line + "\n")


if __name__ == '__main__':
    main()
