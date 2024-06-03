import math

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


translation_map = {
    'Halstead Volume': 'Objętość Halsteada',
    'Cyclomatic Complexity (CC)': 'Złożoność Cyklomatyczna (CC)',
    'Lines Of Code (LOC)': 'Liczba Linii Kodu (LOC)',
    'Microsoft Maintainability Index (MI)': 'Indeks Utrzymywalności (MI)',
    'Maintainability Index (MI) - calculated': 'Wyliczony Indeks Utrzymywalności (MI)'
}



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
        "Wartość metryki dla 1 odch. stand. od śred.",
        "Wartość metryki dla 2 odch. stand. od śred.",
        "Wartość metryki dla 3 odch. stand. od śred."
    ]

    threshold_log_metrics_colors = [
        "green",
        "orange",
        "red"
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
            if unique_values[idx] != threshold:
                close_indices = np.where((unique_values >= threshold - 0.3) & (unique_values <= threshold + 0.3))[0]
                if close_indices.size > 0:
                    avg_count = np.mean(counts[close_indices])
                else:
                    avg_count = 0
            else:
                avg_count = counts[idx]

            counts_threshold_log_metrics.append(avg_count)
            values_for_thresholds[threshold_name].append(np.exp(threshold) - constant_for_0_values)

        log_metric_map[metric] = (unique_values, counts, mean_log_metric, std_log_metric, counts_threshold_log_metrics)

    plt.figure(figsize=(12, 10))

    for i, metric in enumerate(metrics, start=1):
        unique_values, counts, mean, std, counts_threshold_log_metrics = log_metric_map[metric]
        plt.subplot(2, 2, i)
        plt.plot(unique_values, counts, label="Wartości metryki")
        plt.axvline(x=mean, color='r', linestyle='--', linewidth=2, label='Średnia')

        threshold_log_metrics = [
            mean + 1 * std,
            mean + 2 * std,
            mean + 3 * std
        ]

        for threshold, threshold_count, threshold_name, threshold_color in zip(
                threshold_log_metrics, counts_threshold_log_metrics, threshold_log_metrics_names, threshold_log_metrics_colors):
            plt.axvline(x=threshold, color=threshold_color, linestyle='--', linewidth=1)
            plt.plot(threshold, threshold_count, marker='o', color=threshold_color, linestyle='None',
                     label=threshold_name)
            plt.annotate(
                f'{np.exp(threshold) - constant_for_0_values:.1f}',
                xy=(threshold, threshold_count),
                textcoords="offset points",
                xytext=(0, 10),
                ha='center'
            )

        plt.title(translation_map[metric])
        plt.xlabel('Transformacja logarytmiczna metryki')
        plt.ylabel('Liczność')
        plt.legend()

    plt.subplot(2, 2, 4)
    unique_values, counts = np.unique(data["Microsoft Maintainability Index (MI)"], return_counts=True)
    plt.plot(unique_values, counts, label="Wartości metryki")

    for threshold_name, threshold_color in zip(threshold_log_metrics_names[::-1], threshold_log_metrics_colors[::-1]):
        hv, cc, loc = values_for_thresholds[threshold_name]
        mi = calculate_maintainability_index(hv, cc, loc)
        plt.axvline(x=mi, color=threshold_color, linestyle='--', linewidth=1)

        idx = (np.abs(unique_values - mi)).argmin()
        plt.plot(unique_values[idx], counts[idx], 'o', color=threshold_color, label=threshold_name)  # Plot the point
        plt.annotate(f'{unique_values[idx]:.1f}', (unique_values[idx], counts[idx]), textcoords="offset points",
                     xytext=(0, 10), ha='center')

    plt.tight_layout()

    plt.title(translation_map["Maintainability Index (MI) - calculated"])
    plt.xlabel('Wartość metryki')
    plt.ylabel('Liczność')
    plt.legend()
    plt.savefig("./plots/plot_log_transformation_for_components.png")


if __name__ == '__main__':
    main()
