import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


file_path = 'results.csv'
df = pd.read_csv(file_path)

df_filtered = df[[col for col in df.columns if col.split(':')[0].isdigit()]]
df_filtered.columns = [int(col.split(':')[0]) for col in df_filtered.columns]
df_filtered = df_filtered.sort_index(axis=1)

new_column_names = [9, 10, 11, 19, 20, 21, 25, 30, 35, 40, 45, 50, 55, 60]
column_mapping = {old: new for old, new in zip(sorted(df_filtered.columns), new_column_names)}
df_filtered.rename(columns=column_mapping, inplace=True)
df_filtered = df_filtered.sort_index(axis=1, ascending=False)

counts = df_filtered.apply(pd.Series.value_counts).fillna(0).loc[[1, 2, 3]]

fig, ax = plt.subplots(figsize=(10, 6))
counts.T.plot(kind='barh', stacked=True, ax=ax, color=['green', 'orange', 'red'])

ax.set_xlabel('Liczba odpowiedzi')
ax.set_ylabel('Wartość MI funkcji')
ax.legend(title='Odpowiedzi', labels=['Łatwy', 'Średni', 'Trudny'])

output_filepath = "form_results.png"
plt.savefig(output_filepath, bbox_inches='tight')
print(f'Plot saved as {output_filepath}')

weighted_counts = counts.copy()
weighted_counts.loc[1] *= 0
weighted_counts.loc[2] *= 0.5
weighted_counts.loc[3] *= 1

final_values = weighted_counts.sum()

x = np.array([int(i) for i in final_values.index])
y = 100 * (final_values.values / float(len(x)))

fit = np.polyfit(x, y, 1)
fit_fn = np.poly1d(fit)

target_y_values = [0, 50, 100]
colors = ['green', '#cb8300', 'red']

x_values_for_target_y = [(target - fit[1]) / fit[0] for target in target_y_values]

plt.figure(figsize=(10, 6))
plt.plot(x, y, 'o', label='Procent odpowiedzi')
plt.plot(x, fit_fn(x), 'black', label='Linia trendu', linewidth=0.5)

plt.plot(x_values_for_target_y, target_y_values, "-", color='black', linewidth=0.5)
for x_val, y_val, color in zip(x_values_for_target_y, target_y_values, colors):
    plt.plot(x_val, y_val, "o", color=color)
    plt.annotate(f'{x_val:.1f}', (x_val, y_val), textcoords="offset points", xytext=(8, 8), ha='center', color=color)
plt.xlabel('Indeks Utrzymywalności (MI)')
plt.ylabel('Procent odpowiedzi (%)')
plt.xticks(np.arange(0, 100 + 1, 10))
plt.yticks(np.arange(0, 100 + 1, 25))
plt.grid(True)
plt.legend()

output_filepath = "form_results_trend_line.png"
plt.savefig(output_filepath, bbox_inches='tight')
print(f'Plot saved as {output_filepath}')
