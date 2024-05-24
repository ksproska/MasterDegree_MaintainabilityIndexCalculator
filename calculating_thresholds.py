import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

file_path = 'raport.csv'
data = pd.read_csv(file_path)

metrics = [
    'Halstead Volume',
    'Cyclomatic Complexity (CC)',
    'Lines Of Code (LOC)',
    'Microsoft Maintainability Index (MI)'
]

quantile_values = [round(q, 2) for q in np.arange(0.01, 0.6, 0.05)]
quantile_df = pd.DataFrame(index=quantile_values, columns=metrics)

for metric in metrics:
    for q in quantile_values:
        quantile_df.loc[q, metric] = data[metric].quantile(q)

output_path = 'quantiles.csv'
quantile_df.reset_index().rename(columns={'index': 'Quantil'}).to_csv(output_path, index=False)


fig, ax = plt.subplots(2, 1, figsize=(10, 12))  # 2 rows, 1 column

for column in quantile_df.columns:
    print(column)
    if column != 'Microsoft Maintainability Index (MI)':  # Exclude MI from this plot
        ax[0].plot(quantile_df.index, quantile_df[column], marker='o', label=column)
ax[0].set_title('All Metric Quantiles from 0.1 to 0.6 (Excluding MI)')
ax[0].set_xlabel('Quantile')
ax[0].set_ylabel('Metric Values')
ax[0].legend(title='Metrics')
ax[0].grid(True)

ax[1].plot(quantile_df.index, quantile_df['Microsoft Maintainability Index (MI)'], marker='o', linestyle='-', color='red')
ax[1].set_title('Microsoft Maintainability Index (MI) Quantiles from 0.01 to 0.6')
ax[1].set_xlabel('Quantile')
ax[1].set_ylabel('MI Values')
ax[1].grid(True)

plt.tight_layout()
plt.show()
