from datetime import datetime
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import math


def calculate_maintainability_index(halstead_volume, cc, loc):
    return int((171.0 - 5.2 * math.log(halstead_volume) - 0.23 * cc - 16.2 * math.log(loc)) * (100.0 / 171.0))


metrics = [
    'Halstead Volume',
    'Cyclomatic Complexity (CC)',
    'Lines Of Code (LOC)',
    'Microsoft Maintainability Index (MI)'
]
file_path = 'raport.csv'
quantile_values = [round(q, 5) for q in np.arange(0.95, 1, 0.00001)]
annotation_indices = [i for i in quantile_values if (i * 100) % 1 == 0]

data = pd.read_csv(file_path)
quantile_df = pd.DataFrame(index=quantile_values, columns=metrics)

for metric in metrics:
    for q in quantile_values:
        quantile_df.loc[q, metric] = data[metric].quantile(q)

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

special_index_10 = quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 10].index[0] \
    if not quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 10].empty else None
special_index_20 = quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 20].index[0] \
    if not quantile_df[quantile_df['Maintainability Index (MI) - calculated'] == 20].empty else None

for i, metric in enumerate(metrics_to_display):
    ax[i].plot(quantile_df.index, quantile_df[metric])
    ax[i].set_title(metric)
    ax[i].set_xlabel('Quantile')
    ax[i].set_ylabel(metric)
    ax[i].grid(True)
    for q in annotation_indices:
        ax[i].plot(q, quantile_df.loc[q, metric], marker='o', color='tab:blue')
        ax[i].annotate(
            f'{quantile_df.loc[q, metric]:.1f}',
            (q, quantile_df.loc[q, metric]),
            textcoords="offset points",
            xytext=(0, 5),
            ha='center',
            fontsize=7
        )
    ax[i].plot(special_index_10, quantile_df.loc[special_index_10, metric], marker='o', color='tab:red')
    ax[i].annotate(
        f'{quantile_df.loc[special_index_10, metric]:.1f}',
        (special_index_10, quantile_df.loc[special_index_10, metric]),
        textcoords="offset points",
        xytext=(0, 5),
        ha='center',
        fontsize=7
    )
    ax[i].plot(special_index_20, quantile_df.loc[special_index_20, metric], marker='o', color='tab:orange')
    ax[i].annotate(
        f'{quantile_df.loc[special_index_20, metric]:.1f}',
        (special_index_20, quantile_df.loc[special_index_20, metric]),
        textcoords="offset points",
        xytext=(0, 5),
        ha='center',
        fontsize=7
    )

plt.subplots_adjust(hspace=0.3)
plt.tight_layout()

timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
filename = f'./plots/plot_quantiles_for_components_{timestamp}.png'

plt.savefig(filename)
