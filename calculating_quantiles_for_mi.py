from datetime import datetime
from pprint import pprint

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

metric = 'Microsoft Maintainability Index (MI)'
file_path = 'raport.csv'
quantile_values = [round(q, 5) for q in np.arange(0, 0.025, 0.00001)]

data = pd.read_csv(file_path)
quantile_df = pd.DataFrame(index=quantile_values, columns=[metric])

for q in quantile_values:
    quantile_df.loc[q, metric] = data[metric].quantile(q)

pprint(quantile_df)

plt.figure(figsize=(10, 5))
plt.plot(quantile_df.index, quantile_df[metric], linestyle='-')


def annotate_value(value, color):
    quantile_indices = quantile_df.index[quantile_df[metric] == value]
    if not quantile_indices.empty:
        q = quantile_indices[0]
        plt.plot(q, value, marker='o', color=color)
        plt.annotate(
            f'{q:.5f}',
            xy=(q + 0.001, value - 2),
            ha='center'
        )

annotate_value(10, 'tab:red')
annotate_value(20, 'tab:orange')
annotate_value(25, 'tab:blue')
annotate_value(30, 'tab:blue')
annotate_value(35, 'tab:blue')
annotate_value(40, 'tab:blue')


plt.title('Quantile Plot of Microsoft Maintainability Index (MI)')
plt.xlabel('Quantile')
plt.ylabel(metric)
plt.grid(True)
# plt.show()

timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
filename = f'./plots/plot_quantiles_for_mi_{timestamp}.png'

plt.savefig(filename)
