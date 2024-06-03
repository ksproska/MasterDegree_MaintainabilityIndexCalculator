import math

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


def calculate_maintainability_index(halstead_volume, cc, loc):
    return int(171.0 - 5.2 * math.log(halstead_volume) - 0.23 * cc - 16.2 * math.log(loc)) * (100.0 / 171.0)


def main():
    data = pd.read_csv('raport.csv')

    metrics = [
        'Halstead Volume',
        'Cyclomatic Complexity (CC)',
        'Lines Of Code (LOC)'
    ]

    threshold_log_metrics_names = [
        "1 odchylenia standardowe od średniej",
        "2 odchylenia standardowe od średniej",
        "3 odchylenie standardowe od średniej"
    ]

    threshold_log_metrics_colors = [
        "red",
        "orange",
        "green"
    ]

    constant_for_0_values = 0.0001
    values_for_thresholds = {}

    for threshold_name in threshold_log_metrics_names:
        values_for_thresholds[threshold_name] = []

    log_metric_map = {}
    for metric in metrics:
        log_metric = np.log(data[metric] + constant_for_0_values)
        unique_values, counts = np.unique(log_metric, return_counts=True)
        mean_log_metric = log_metric.mean()
        std_log_metric = log_metric.std()
        threshold_log_metrics = [
            mean_log_metric + 1 * std_log_metric,
            mean_log_metric + 2 * std_log_metric,
            mean_log_metric + 3 * std_log_metric
        ]
        counts_threshold_log_metrics = []
        for threshold, threshold_name in zip(threshold_log_metrics, threshold_log_metrics_names):
            idx = (np.abs(unique_values - threshold)).argmin()
            count_at_threshold = counts[idx]
            counts_threshold_log_metrics.append(count_at_threshold)
            values_for_thresholds[threshold_name].append(np.exp(threshold) - constant_for_0_values)
        log_metric_map[metric] = (unique_values, counts, mean_log_metric, std_log_metric, counts_threshold_log_metrics)

    plt.figure(figsize=(12, 10))

    for i, metric in enumerate(metrics, start=1):
        unique_values, counts, mean, std, counts_threshold_log_metrics = log_metric_map[metric]
        plt.subplot(2, 2, i)
        plt.plot(unique_values, counts)
        plt.axvline(x=mean, color='r', linestyle='--', linewidth=2, label='Średnia')

        threshold_log_metrics = [
            mean + 1 * std,
            mean + 2 * std,
            mean + 3 * std
        ]

        plt.plot(threshold_log_metrics, counts_threshold_log_metrics, marker='o', color='red', linestyle='None',
                 label=f'Wartości MI po odwróceniu transformacji')

        for threshold, threshold_name, threshold_color in zip(threshold_log_metrics, threshold_log_metrics_names,
                                                              threshold_log_metrics_colors):
            plt.axvline(x=threshold, color=threshold_color, linestyle='--', linewidth=1, label=threshold_name)
            idx = (np.abs(unique_values - threshold)).argmin()
            count_at_threshold = counts[idx]
            plt.annotate(
                f'{np.exp(threshold) - constant_for_0_values:.1f}',
                xy=(threshold, count_at_threshold),
                textcoords="offset points",
                xytext=(0, 10),
                ha='center'
            )

        plt.title(metric)
        plt.xlabel('Log of Metric Values')
        plt.ylabel('Counts')
        plt.legend()

    plt.subplot(2, 2, 4)
    unique_values, counts = np.unique(data["Microsoft Maintainability Index (MI)"], return_counts=True)
    plt.plot(unique_values, counts)

    for threshold_name, threshold_color in zip(threshold_log_metrics_names[::-1], threshold_log_metrics_colors[::-1]):
        hv, cc, loc = values_for_thresholds[threshold_name]
        mi = calculate_maintainability_index(hv, cc, loc)
        plt.axvline(x=mi, color=threshold_color, linestyle='--', linewidth=1, label=threshold_name)

    plt.tight_layout()

    plt.title("Microsoft Maintainability Index (MI)")
    plt.xlabel('Log of Metric Values')
    plt.ylabel('Counts')
    plt.legend()
    plt.savefig("./plots/plot_log_transformation_for_components.png")


if __name__ == '__main__':
    main()
