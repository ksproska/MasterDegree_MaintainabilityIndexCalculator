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
    os.makedirs(f"/home/kamilasproska/IdeaProjects/javaParser/roc_curve/{repo_name}/{revision}/{os.path.dirname(filename)}", exist_ok=True)
    output_filename = f"/home/kamilasproska/IdeaProjects/javaParser/roc_curve/{repo_name}/{revision}/{filename}"
    cmd = ["git", "-C", repo_path, "show", file_spec]

    try:
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, check=True)
        with open(output_filename, 'w') as f:
            f.write(result.stdout)
    except subprocess.CalledProcessError as e:
        print(f"Error: {e.stderr}")


def main():
    repo_name = 'elasticsearch'
    repo_path = '/home/kamilasproska/IdeaProjects/' + repo_name
    commits = [
        'e6b43a17099eff099a05572ff0b2724485e54211', # 8.13.4 - Fix BlockHash DirectEncoder (#108283)
        '7eae95620b41c8c42a647b059b096703b4d510f4', # 8.13.3 - [ci] Move multi-node tests from check part2 to part5 (#107553)
        '95c7c0978020de5bac685802655bfab3f475e628', # 8.13.2 - Downgrade the bundled JDK to JDK 21.0.2 (#107140)
        'f7fedb4d0aec5dc60bf52bb4c460584d08a236ce', # 8.13.1 - Fix downsample persistent task params serialization bwc (#106878)
        '93a21e1b14c6ca611b477360c7c7f65846bd364e' # 8.13.0 - AwaitsFix for #106618
    ]

    for i in range(len(commits) - 1):
        commit1 = commits[i]
        commit2 = commits[i + 1]
        java_files_changed = list_java_changes(repo_path, commit1, commit2)

        for filename in java_files_changed:
            save_file_at_revision(repo_name, repo_path, commit1, filename)
            save_file_at_revision(repo_name, repo_path, commit2, filename)


if __name__ == '__main__':
    main()
