import pandas as pd
import matplotlib.pyplot as plt


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

output_filepath = "results.png"
plt.savefig(output_filepath, bbox_inches='tight')
print(f'Plot saved as {output_filepath}')
