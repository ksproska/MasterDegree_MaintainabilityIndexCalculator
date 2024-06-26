from pprint import pprint

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

metric_name = "Microsoft Maintainability Index (MI)"
metric_name_short = "MI"
constant_for_0_values = 0.0001

log_metric = np.log(data[metric_name] + constant_for_0_values)
unique_values, counts = np.unique(log_metric, return_counts=True)
skewness = log_metric.skew()

plt.figure(figsize=(8, 6))
plt.xlim(left=3.2, right=4.6)

plt.plot(unique_values, counts, alpha=0.6, label="Liczności dla danej wartości MI po transformacji")
plt.title(f'Indeks utrzymywalności (MI) po transformacji logarytmicznej')
plt.xlabel('Liczba wystąpień dla wartości MI')
plt.ylabel(f'Indeks utrzymywalności (MI) po transformacji logarytmicznej')
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
for threshold in threshold_log_metrics:
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    counts_threshold_log_metrics.append(count_at_threshold)
plt.plot(threshold_log_metrics, counts_threshold_log_metrics, marker='o', color='red', linestyle='None', label=f'Wartości MI po odwróceniu transformacji')

for threshold, threshold_name, threshold_color in zip(threshold_log_metrics, threshold_log_metrics_names, threshold_log_metrics_colors):
    plt.axvline(x=threshold, color=threshold_color, linestyle='--', linewidth=1, label=threshold_name)
    idx = (np.abs(unique_values - threshold)).argmin()
    count_at_threshold = counts[idx]
    plt.annotate(
        f'{np.exp(threshold) - constant_for_0_values:.2f}',
        xy=(threshold, count_at_threshold),
        textcoords="offset points",
        xytext=(0, 10),
        ha='center'
        )

plt.axvline(x=mean_log_metric, color='r', linestyle='--', linewidth=2, label='Średnia')
plt.legend()

plt.savefig("./plots/plot_" + metric_name_short + ".png")
threshold_log_metrics = np.exp(threshold_log_metrics) - constant_for_0_values
pprint(threshold_log_metrics)
