from pprint import pprint

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

metric_name = "Microsoft Maintainability Index (MI)"
metric_name_short = "MI"

unique_values, counts = np.unique(data[metric_name], return_counts=True)

plt.figure(figsize=(8, 6))

plt.plot(unique_values, counts, alpha=0.6, label="Liczności dla MI dla różnych odchyleń standardowych")
# plt.title(f'Indeks utrzymywalności (MI)')
plt.xlabel('Liczba wystąpień dla wartości MI')
plt.ylabel(f'Indeks utrzymywalności (MI)')
plt.grid(True)
mean_log_metric = data[metric_name].mean()
std_log_metric = data[metric_name].std()

threshold_log_metrics = [
    mean_log_metric - 3 * std_log_metric,
    mean_log_metric - 2 * std_log_metric,
    mean_log_metric - 1 * std_log_metric
]

threshold_log_metrics_names = [
    "3 odchylenia standardowe od średniej",
    "2 odchylenia standardowe od średniej",
    "1 odchylenie standardowe od średniej"
]

threshold_log_metrics_colors = [
    "red",
    "orange",
    "green"
]

counts_threshold_log_metrics = []
for threshold, threshold_color, threshold_name in zip(threshold_log_metrics, threshold_log_metrics_colors, threshold_log_metrics_names):
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    counts_threshold_log_metrics.append(count_at_threshold)
    plt.plot(threshold, count_at_threshold, marker='o', color=threshold_color, linestyle='None', label=threshold_name)

for threshold, threshold_color in zip(threshold_log_metrics, threshold_log_metrics_colors):
    plt.axvline(x=threshold, color=threshold_color, linestyle='--', linewidth=1)
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    plt.annotate(
        f'{threshold:.1f}',
        xy=(threshold, count_at_threshold),
        textcoords="offset points",
        xytext=(0, 10),
        ha='center'
        )
    print(f' & {threshold:.1f}\\\\')

plt.axvline(x=mean_log_metric, color='r', linestyle='--', linewidth=2, label='Średnia')
plt.legend()
plt.tight_layout()

plt.savefig("./plots/plot_standard_deviation_mi.png")
threshold_log_metrics = threshold_log_metrics
pprint(threshold_log_metrics)
