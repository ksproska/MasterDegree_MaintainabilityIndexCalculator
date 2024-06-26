import csv
import math
from collections import defaultdict
from datetime import datetime

import numpy as np
import pandas as pd
from matplotlib import pyplot as plt

translation_map = {
    'Halstead Volume': 'Objętość Halsteada',
    'Cyclomatic Complexity (CC)': 'Złożoność Cyklomatyczna (CC)',
    'Lines Of Code (LOC)': 'Liczba Linii Kodu (LOC)',
    'Microsoft Maintainability Index (MI)': 'Indeks Utrzymywalności (MI)',
    'Maintainability Index (MI) - calculated': 'Wyliczony Indeks Utrzymywalności (MI)'
}


def create_histograms_for_all_metrics(csv_filepath, output_filepath):
    data = pd.read_csv(csv_filepath)
    metrics = ['Halstead Volume', 'Cyclomatic Complexity (CC)', 'Lines Of Code (LOC)',
               'Microsoft Maintainability Index (MI)']
    data['Halstead Volume'] = data['Halstead Volume'].round().astype(int)
    fig, axes = plt.subplots(nrows=2, ncols=2, figsize=(8, 6))

    axes = axes.flatten()
    for i, metric in enumerate(metrics):
        unique_values = data[metric].nunique()

        if metric == 'Microsoft Maintainability Index (MI)':
            bin_edges = np.linspace(0, 100, unique_values + 1)
            hist, bin_edges = np.histogram(data[metric], bins=bin_edges)
            colors = ['red' if bin_edge < 10 else 'orange' if bin_edge < 20 else 'green' for bin_edge in bin_edges[:-1]]

            bar_width = [np.diff(bin_edges[j:j + 2])[0] * 0.85 for j in
                         range(len(hist))]
            for j in range(len(hist)):
                axes[i].bar(bin_edges[j], hist[j], width=bar_width[j], align='edge', color=colors[j], alpha=0.6)
        else:
            axes[i].hist(data[metric], bins=unique_values, alpha=0.6, color='blue', edgecolor='black')

        axes[i].set_title(f'{translation_map[metric]}')
        axes[i].set_xlabel('Wartość metryki')
        axes[i].set_ylabel('Częstotliwość')
        axes[i].set_yscale('log')
    plt.tight_layout()

    # fig.suptitle('Histogramy metryk', fontsize=16)
    # plt.subplots_adjust(top=0.9)

    plt.savefig(output_filepath)
    plt.close(fig)
    print(f'Plot saved as {output_filepath}')


def plot_percentage_for_thresholds(csv_filepath, output_filepath):
    data = pd.read_csv(csv_filepath)
    data['Microsoft Maintainability Index (MI)'] = pd.to_numeric(
        data['Microsoft Maintainability Index (MI)'],
        errors='coerce'
    )
    total_records = len(data)
    mi_percentages = {}
    for mi_value in range(10, 71):
        filtered_data = data[data['Microsoft Maintainability Index (MI)'] <= mi_value]
        mi_percentages[mi_value] = (len(filtered_data) / total_records) * 100
    mi_values = list(mi_percentages.keys())
    percentages = list(mi_percentages.values())
    plt.figure(figsize=(10, 4))
    plt.plot(mi_values, percentages, color='blue', marker='.', linestyle='--', linewidth=0.6)
    for mi, percentage in zip(mi_values, percentages):
        if mi % 5 == 0:
            color = 'darkblue'
            if mi == 10:
                color = 'red'
            if mi == 20:
                color = 'orange'
            plt.plot(mi, percentage, color=color, marker='o', linestyle='')
            plt.annotate(f'{percentage:.3f}%',
                         (mi, percentage),
                         textcoords="offset points",
                         xytext=(0, 10),
                         ha='center')
    plt.xlabel(translation_map['Microsoft Maintainability Index (MI)'])
    plt.ylabel('Procent badanych metod (%)')
    # plt.title('Procent badanych metod mających wartość MI poniżej wartości progowej')
    plt.xticks(range(10, 71, 5))
    plt.grid(True, which='both', linestyle='--', linewidth=0.3)
    plt.tight_layout()
    plt.savefig(output_filepath)
    print(f'Plot saved as {output_filepath}')


def plot_quantile_threshold_for_mi(csv_filepath, output_filepath):
    metric = 'Microsoft Maintainability Index (MI)'
    quantile_values = [round(q, 5) for q in np.arange(0, 0.51, 0.00001)]
    data = pd.read_csv(csv_filepath)
    quantile_df = pd.DataFrame(index=quantile_values, columns=[metric])
    for q in quantile_values:
        quantile_df.loc[q, metric] = data[metric].quantile(q)
    plt.figure(figsize=(8, 4))
    plt.plot(quantile_df.index, quantile_df[metric], linestyle='-')

    annotate_value(10, 'tab:red', quantile_df, metric)
    annotate_value(20, 'tab:orange', quantile_df, metric)
    # annotate_value_for_quantile(0.01, 'tab:blue', quantile_df, metric)
    annotate_value_for_quantile(0.1, 'tab:blue', quantile_df, metric)
    annotate_value_for_quantile(0.2, 'tab:blue', quantile_df, metric)
    annotate_value_for_quantile(0.3, 'tab:blue', quantile_df, metric)
    annotate_value_for_quantile(0.4, 'tab:blue', quantile_df, metric)
    annotate_value_for_quantile(0.5, 'tab:blue', quantile_df, metric)
    # annotate_value(25, 'tab:blue', quantile_df, metric)
    # annotate_value(30, 'tab:blue', quantile_df, metric)
    # annotate_value(35, 'tab:blue', quantile_df, metric)
    # annotate_value(40, 'tab:blue', quantile_df, metric)
    # plt.title('Diagram kwantyli dla metryki MI')
    plt.xlabel('Kwantyl')
    plt.ylabel(translation_map[metric])
    plt.grid(True)
    plt.savefig(output_filepath, bbox_inches='tight')
    print(f'Plot saved as {output_filepath}')


def annotate_value(value, color, quantile_df, metric):
    quantile_indices = quantile_df.index[quantile_df[metric] == value]
    if not quantile_indices.empty:
        q = quantile_indices[0]
        plt.plot(q, value, marker='o', color=color)
        plt.annotate(
            f'{int(value)}',
            xy=(q + 0.015, value - 2),
            ha='center'
        )


def annotate_value_for_quantile(q, color, quantile_df, metric):
    # Ensure that q is a valid index in the DataFrame
    if q in quantile_df.index:
        # Get the value associated with quantile q
        value = quantile_df.loc[q, metric]
        plt.plot(q, value, marker='o', color=color)
        plt.annotate(
            f'{int(value)}',
            xy=(q + 0.015, value - 2),
            ha='center'
        )
    else:
        print(f"Quantile {q} is not a valid index in the DataFrame.")


def plot_calculated_mi_from_component_quantile_thresholds(csv_filepath, output_filepath):
    metrics = [
        'Halstead Volume',
        'Cyclomatic Complexity (CC)',
        'Lines Of Code (LOC)',
        'Microsoft Maintainability Index (MI)'
    ]
    quantile_values = [round(q * 100, 5) for q in np.arange(0.95, 1, 0.00001)]
    annotation_indices = [i for i in quantile_values if i % 1 == 0]
    data = pd.read_csv(csv_filepath)
    quantile_df = pd.DataFrame(index=quantile_values, columns=metrics)
    for metric in metrics:
        for q in quantile_values:
            actual_quantile = q / 100  # Convert back to the range [0.95, 1.0) for calculation
            quantile_df.loc[q, metric] = data[metric].quantile(actual_quantile)
    quantile_df['Maintainability Index (MI) - calculated'] = quantile_df.apply(
        lambda row: calculate_maintainability_index(
            row['Halstead Volume'],
            row['Cyclomatic Complexity (CC)'],
            row['Lines Of Code (LOC)']
        ),
        axis=1
    )
    output_path = 'quantiles.csv'
    quantile_df.reset_index().rename(columns={'index': 'Quantil'}).to_csv(output_path, index=False)
    fig, ax = plt.subplots(2, 2, figsize=(8, 6))
    ax = ax.flatten()
    metrics_to_display = [
        'Halstead Volume',
        'Cyclomatic Complexity (CC)',
        'Lines Of Code (LOC)',
        'Maintainability Index (MI) - calculated'
    ]
    # special_index_10 = quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 10].index[0] \
    #     if not quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 10].empty else None
    # special_index_20 = quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 20].index[0] \
    #     if not quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 20].empty else None

    tolerance = 0.5

    special_index_10 = quantile_df[
        (quantile_df['Maintainability Index (MI) - calculated'] >= 10 - tolerance) &
        (quantile_df['Maintainability Index (MI) - calculated'] <= 10 + tolerance)
        ].index.min() if not quantile_df[
        (quantile_df['Maintainability Index (MI) - calculated'] >= 10 - tolerance) &
        (quantile_df['Maintainability Index (MI) - calculated'] <= 10 + tolerance)
        ].empty else None

    special_index_20 = quantile_df[
        (quantile_df['Maintainability Index (MI) - calculated'] >= 20 - tolerance) &
        (quantile_df['Maintainability Index (MI) - calculated'] <= 20 + tolerance)
        ].index.min() if not quantile_df[
        (quantile_df['Maintainability Index (MI) - calculated'] >= 20 - tolerance) &
        (quantile_df['Maintainability Index (MI) - calculated'] <= 20 + tolerance)
        ].empty else None

    for i, metric in enumerate(metrics_to_display):
        ax[i].plot(quantile_df.index, quantile_df[metric])
        ax[i].set_title(translation_map[metric])
        ax[i].set_xlabel('Percentyl')
        ax[i].set_ylabel('Wartość metryki')
        ax[i].grid(True)
        for q in annotation_indices:
            ax[i].plot(q, quantile_df.loc[q, metric], marker='o', color='tab:blue')
            ax[i].annotate(
                f'{quantile_df.loc[q, metric]:.1f}',
                (q, quantile_df.loc[q, metric]),
                textcoords="offset points",
                xytext=(0, 5),
                ha='center',
                fontsize=8
            )
        ax[i].plot(special_index_10, quantile_df.loc[special_index_10, metric], marker='o', color='tab:red')
        ax[i].annotate(
            f'{quantile_df.loc[special_index_10, metric]:.1f}',
            (special_index_10, quantile_df.loc[special_index_10, metric]),
            textcoords="offset points",
            xytext=(0, 5),
            ha='center',
            fontsize=8
        )
        ax[i].plot(special_index_20, quantile_df.loc[special_index_20, metric], marker='o', color='tab:orange')
        ax[i].annotate(
            f'{quantile_df.loc[special_index_20, metric]:.1f}',
            (special_index_20, quantile_df.loc[special_index_20, metric]),
            textcoords="offset points",
            xytext=(0, 5),
            ha='center',
            fontsize=8
        )
    plt.subplots_adjust(hspace=0.3)
    plt.tight_layout()
    plt.savefig(output_filepath)
    print(f'Plot saved as {output_filepath}')


def calculate_maintainability_index(halstead_volume, cc, loc):
    return max(0, (171.0 - 5.2 * math.log(halstead_volume) - 0.23 * cc - 16.2 * math.log(loc)) * (100.0 / 171.0))


def plot_original_projects_percentage(csv_filepath, output_filepath):
    folder_counts = defaultdict(int)
    with open(csv_filepath, mode='r') as file:
        csv_reader = csv.DictReader(file)

        for row in csv_reader:
            original_path = row['OriginalFilePath']
            folder_name = original_path.split('/')[4]
            folder_counts[folder_name] += 1
    sorted_folders = sorted(folder_counts.items(), key=lambda x: x[1], reverse=True)
    folders, counts = zip(*sorted_folders)
    fig, ax = plt.subplots(figsize=(8, 7))
    ax.pie(counts, labels=folders, autopct='%1.1f%%', startangle=90, textprops={'size': 20})
    ax.axis('equal')
    # plt.title('Źródła pochodzenia analizowanych metod')
    plt.savefig(output_filepath, bbox_inches='tight')
    print(f'Plot saved as {output_filepath}')


if __name__ == '__main__':
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    csv_file = 'raport.csv'
    create_histograms_for_all_metrics(csv_file, f'./plots/plot_histograms_for_all_metrics.png')
    plot_percentage_for_thresholds(csv_file, f'./plots/plot_cumulative_percentage_by_mi.png')
    plot_calculated_mi_from_component_quantile_thresholds(csv_file,f'./plots/plot_quantiles_for_components.png')
    plot_quantile_threshold_for_mi(csv_file, f'./plots/plot_quantiles_for_mi.png')
    plot_original_projects_percentage(csv_file, f'./plots/plot_original_project_percentage.png')
