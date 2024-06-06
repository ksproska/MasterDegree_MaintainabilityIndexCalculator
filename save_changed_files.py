import subprocess
import os


def list_java_changes(repo_path, commit1, commit2):
    os.chdir(repo_path)
    cmd = ["git", "diff", "--name-only", commit1, commit2]
    result = subprocess.run(cmd, stdout=subprocess.PIPE, text=True, check=True)
    changed_files = [line for line in result.stdout.splitlines() if line.endswith('.java')]
    return changed_files


def save_file_at_revision(repo_name, repo_path, revision, filepath):
    file_spec = f"{revision}:{filepath}"
    filename = filepath.replace("benchmarks/", "")
    os.makedirs(f"/home/kamilasproska/IdeaProjects/javaParser/{repo_name}/{revision}/{os.path.dirname(filename)}", exist_ok=True)
    output_filename = f"/home/kamilasproska/IdeaProjects/javaParser/{repo_name}/{revision}/{filename}"
    cmd = ["git", "-C", repo_path, "show", file_spec]

    try:
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, check=True)
        with open(output_filename, 'w') as f:
            f.write(result.stdout)
        print(f"File saved as {output_filename}")
    except subprocess.CalledProcessError as e:
        print(f"Error: {e.stderr}")


def main():
    repo_name = 'elasticsearch'
    repo_path = '/home/kamilasproska/IdeaProjects/' + repo_name
    commit1 = 'e6b43a17099eff099a05572ff0b2724485e54211'
    commit2 = '7eae95620b41c8c42a647b059b096703b4d510f4'
    java_files_changed = list_java_changes(repo_path, commit1, commit2)
    print(java_files_changed)

    for filename in java_files_changed:
        save_file_at_revision(repo_name, repo_path, commit1, filename)
        save_file_at_revision(repo_name, repo_path, commit2, filename)


if __name__ == '__main__':
    main()
