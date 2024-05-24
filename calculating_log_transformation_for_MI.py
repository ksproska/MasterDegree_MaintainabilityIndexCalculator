from pprint import pprint

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

metric_name = "Microsoft Maintainability Index (MI)"
metric_name_short = "MI"
log_metric = np.log(data[metric_name])
unique_values, counts = np.unique(log_metric, return_counts=True)
skewness = log_metric.skew()

plt.figure(figsize=(8, 6))

plt.plot(unique_values, counts, alpha=0.6, label="Histogram")
plt.title(f'Log-transformed {metric_name}')
plt.xlabel('Index')
plt.ylabel(f'Log {metric_name}')
plt.grid(True)
mean_log_metric = log_metric.mean()
std_log_metric = log_metric.std()
plt.text(0.02, 0.81,
         f'Skewness: {skewness:.2f}\n'
         f'Mean: {mean_log_metric:.2f}\n'
         f'Standard deviation: {std_log_metric:.2f}',
         transform=plt.gca().transAxes,
         verticalalignment='top',
         horizontalalignment='left',
         fontsize=10,
         color='black',
         backgroundcolor='white'
         )

threshold_log_metrics = [
    mean_log_metric - 3 * std_log_metric,
    mean_log_metric - 2 * std_log_metric,
    mean_log_metric - 1 * std_log_metric
]

counts_threshold_log_metrics = []
for threshold in threshold_log_metrics:
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    counts_threshold_log_metrics.append(count_at_threshold)
plt.plot(threshold_log_metrics, counts_threshold_log_metrics, marker='o', color='red', linestyle='None', label=f'Actual {metric_name_short} threshold values')

for threshold in threshold_log_metrics:
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    plt.annotate(
        f'{np.exp(threshold):.1f}',
        xy=(threshold, count_at_threshold),
        textcoords="offset points",
        xytext=(0, 10),
        ha='center'
        )

plt.axvline(x=mean_log_metric, color='r', linestyle='--', linewidth=2, label='Mean')
plt.legend()

plt.savefig("./plots/plot_" + metric_name_short + ".png")
threshold_log_metrics = np.exp(threshold_log_metrics)
pprint(threshold_log_metrics)
