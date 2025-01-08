import math

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

translation_map = {
    'Halstead Volume': 'Objętość Halsteada',
    'Cyclomatic Complexity (CC)': 'Złożoność Cyklomatyczna (CC)',
    'Lines Of Code (LOC)': 'Liczba Linii Kodu (LOC)',
    'Microsoft Maintainability Index (MI)': 'Indeks Utrzymywalności (MI)',
    'Maintainability Index (MI) - calculated': 'Wyliczony Indeks Utrzymywalności (MI)'
}


def calculate_maintainability_index(halstead_volume, cc, loc):
    return int((171.0 - 5.2 * math.log(halstead_volume) - 0.23 * cc - 16.2 * math.log(loc)) * (100.0 / 171.0))


def calculate_threshold(metric_name):
    # print(data[metric_name].skew())
    log_metric = np.log(data[metric_name])

    plt.figure(figsize=(8, 6))
    # plt.hist(data_for_metric_log_transformed, bins=data_for_metric_log_transformed.unique().size)
    unique_values, counts = np.unique(log_metric, return_counts=True)
    plt.plot(unique_values, counts, alpha=0.6)  # Adjust alpha as needed for transparency

    plt.title(f'Metryka {translation_map[metric_name]} po transformacji logarytmicznej')
    plt.xlabel('Liczność')
    plt.ylabel(f'Log metryki {metric_name}')
    plt.grid(True)

    mean_log_metric = log_metric.mean()
    std_log_metric = log_metric.std()
    # print(data_for_metric_log_transformed.skew())
    # print(mean_log_metric, std_log_metric)

    threshold_log_metric_lower = mean_log_metric - 3 * std_log_metric
    plt.axvline(x=threshold_log_metric_lower, color='r', linestyle='--', linewidth=2)
    threshold_log_metric_lower = np.exp(threshold_log_metric_lower)

    threshold_log_metric_upper = mean_log_metric + 3 * std_log_metric
    plt.axvline(x=threshold_log_metric_upper, color='r', linestyle='--', linewidth=2)
    threshold_log_metric_upper = np.exp(threshold_log_metric_upper)

    plt.savefig("plot_" + metric_name + ".png")
    return threshold_log_metric_lower, threshold_log_metric_upper


_, threshold_CC = calculate_threshold('Cyclomatic Complexity (CC)')
_, threshold_Halstead_Volume = calculate_threshold('Halstead Volume')
_, threshold_LOC = calculate_threshold('Lines Of Code (LOC)')
threshold_MI, _ = calculate_threshold('Microsoft Maintainability Index (MI)')

print(f'Threshold for Cyclomatic Complexity (CC): {threshold_CC}')
print(f'Threshold for Halstead Volume: {threshold_Halstead_Volume}')
print(f'Threshold for Lines Of Code (LOC): {threshold_LOC}')
print(f'Threshold for Microsoft Maintainability Index (MI): {threshold_MI}')
print(f'MI: {calculate_maintainability_index(threshold_Halstead_Volume, threshold_CC, threshold_LOC)}')
