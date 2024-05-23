from datetime import datetime
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

file_path = 'raport.csv'
data = pd.read_csv(file_path)

metrics = ['Halstead Volume', 'Cyclomatic Complexity (CC)', 'Lines Of Code (LOC)', 'Microsoft Maintainability Index (MI)']
data['Halstead Volume'] = data['Halstead Volume'].round().astype(int)

fig, axes = plt.subplots(nrows=2, ncols=2, figsize=(12, 10))
axes = axes.flatten()

for i, metric in enumerate(metrics):
    unique_values = data[metric].nunique()

    if metric == 'Microsoft Maintainability Index (MI)':
        # Define custom bin edges to include 0 to 100
        bin_edges = np.linspace(0, 100, unique_values + 1)  # This creates evenly spaced bins from 0 to 100
        hist, bin_edges = np.histogram(data[metric], bins=bin_edges)
        colors = ['red' if bin_edge < 10 else 'orange' if bin_edge < 20 else 'green' for bin_edge in bin_edges[:-1]]

        # Plot each bin as a bar with thinner width
        bar_width = [np.diff(bin_edges[j:j + 2])[0] * 0.85 for j in range(len(hist))]  # 70% of the original bin width
        for j in range(len(hist)):
            axes[i].bar(bin_edges[j], hist[j], width=bar_width[j], align='edge', color=colors[j], alpha=0.6)
    else:
        # Normal histogram for other metrics
        axes[i].hist(data[metric], bins=unique_values, alpha=0.6, color='blue', edgecolor='black')

    axes[i].set_title(metric)
    axes[i].set_xlabel('Value Range')
    axes[i].set_ylabel('Frequency')
    axes[i].set_yscale('log')

plt.tight_layout()

timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
filename = f'./plots/plot_output_{timestamp}.png'

plt.savefig(filename)
plt.close(fig)

print(f'Plot saved as {filename}')
