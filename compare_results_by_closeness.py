from io import StringIO
import pandas as pd
import matplotlib.pyplot as plt

data = """
Etykieta,Wyliczanie pośrednie,Wyliczanie bezpośrednie,Ankieta 50%,Ankieta 0%
\ensuremath{\mu + 3\Omega},28,30,,
\ensuremath{\mu + 2\Omega},42,42,,
\ensuremath{\mu + \Omega},55,55,,
Percentyl 1,35,50,,
Percentyl 2,39,57,,
Percentyl 3,41,61,,
Percentyl 4,43,65,,
Percentyl 5,45,69,,
ROC,62,62,,
Ankieta 50%,,,66,
Ankieta 0%,,,,38
"""

df = pd.read_csv(StringIO(data))

df['Etykieta'] = df['Etykieta'].replace({
    r'\ensuremath{\mu + 3\Omega}': 'μ + 3Ω',
    r'\ensuremath{\mu + 2\Omega}': 'μ + 2Ω',
    r'\ensuremath{\mu + \Omega}': 'μ + Ω'
})

ankieta_50_value = df.loc[df['Etykieta'] == 'Ankieta 50%', 'Ankieta 50%'].iloc[0]
ankieta_100_value = df.loc[df['Etykieta'] == 'Ankieta 0%', 'Ankieta 0%'].iloc[0]

color_map = {
    'μ': 'pink',
    'Percentyl': 'lightgreen',
    'ROC': 'skyblue'
}


def get_color(label_col):
    for key in color_map:
        if key in label_col:
            return color_map[key]
    return 'gray'


results = []
for col in ['Wyliczanie pośrednie', 'Wyliczanie bezpośrednie']:
    for index, row in df.iterrows():
        if pd.notna(row[col]):
            diff_50 = abs(row[col] - ankieta_50_value)
            diff_100 = abs(row[col] - ankieta_100_value)
            label = row['Etykieta'] + '\n' + col.split(" ")[1]
            results.append({
                'label': label,
                'diff_50': diff_50,
                'value_50': row[col],
                'diff_100': diff_100,
                'value_100': row[col],
                'color': get_color(row['Etykieta'])
            })

results.sort(key=lambda x: x['diff_50'], reverse=True)
labels_50 = [x['label'] for x in results]
values_50 = [x['value_50'] for x in results]
colors_50 = [x['color'] for x in results]

results.sort(key=lambda x: x['diff_100'], reverse=True)
labels_100 = [x['label'] for x in results]
values_100 = [x['value_100'] for x in results]
colors_100 = [x['color'] for x in results]

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 10))

ax1.barh(labels_50, values_50, color=colors_50)
ax1.axvline(x=ankieta_50_value, color='orange', linestyle='--', linewidth=2)
ax1.set_xlabel('Wartość progowa MI')
ax1.set_title('Wartości MI posortowane względem odległości do wyniku ankiety dla 0%')

ax2.barh(labels_100, values_100, color=colors_100)
ax2.axvline(x=ankieta_100_value, color='red', linestyle='--', linewidth=2)
ax2.set_xlabel('Wartość progowa MI')
ax2.set_title('Wartości MI posortowane względem odległości do wyniku ankiety dla 50%')

plt.tight_layout()
plt.savefig("./plots/plot_result_comparison_by_closeness.png")
