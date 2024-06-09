from io import StringIO
import pandas as pd
import matplotlib.pyplot as plt

data = """
Etykieta,Wyliczanie pośrednie,Wyliczanie bezpośrednie,Ankieta 50%,Ankieta 100%
\ensuremath{\mu + 3\Omega},28,30,,
\ensuremath{\mu + 2\Omega},42,42,,
\ensuremath{\mu + \Omega},55,55,,
Kwantyl 0.1,35,50,,
Kwantyl 0.2,39,57,,
Kwantyl 0.3,41,61,,
Kwantyl 0.4,43,65,,
Kwantyl 0.5,45,69,,
ROC,62,62,,
Ankieta 50%,,,38,
Ankieta 100%,,,,66
"""

df = pd.read_csv(StringIO(data))

df['Etykieta'] = df['Etykieta'].replace({
    r'\ensuremath{\mu + 3\Omega}': 'μ + 3Ω',
    r'\ensuremath{\mu + 2\Omega}': 'μ + 2Ω',
    r'\ensuremath{\mu + \Omega}': 'μ + Ω'
})

ankieta_50_value = df.loc[df['Etykieta'] == 'Ankieta 50%', 'Ankieta 50%'].iloc[0]
ankieta_100_value = df.loc[df['Etykieta'] == 'Ankieta 100%', 'Ankieta 100%'].iloc[0]

results = []
for col in ['Wyliczanie pośrednie', 'Wyliczanie bezpośrednie']:
    for index, row in df.iterrows():
        if pd.notna(row[col]):
            diff_50 = abs(row[col] - ankieta_50_value)
            diff_100 = abs(row[col] - ankieta_100_value)
            results.append({
                'label': row['Etykieta'] + '\n' + col.split(" ")[1],
                'diff_50': diff_50,
                'value_50': row[col],
                'diff_100': diff_100,
                'value_100': row[col]
            })

results.sort(key=lambda x: x['diff_50'], reverse=True)
labels_50 = [x['label'] for x in results]
values_50 = [x['value_50'] for x in results]

results.sort(key=lambda x: x['diff_100'], reverse=True)
labels_100 = [x['label'] for x in results]
values_100 = [x['value_100'] for x in results]

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(20, 10))

ax1.barh(labels_50, values_50, color='skyblue')
ax1.axvline(x=ankieta_50_value, color='orange', linestyle='--')
ax1.set_xlabel('Value')
ax1.set_title('Values sorted by closeness to Ankieta 50%')

ax2.barh(labels_100, values_100, color='skyblue')
ax2.axvline(x=ankieta_100_value, color='red', linestyle='--')
ax2.set_xlabel('Value')
ax2.set_title('Values sorted by closeness to Ankieta 100%')

plt.tight_layout()
plt.savefig("./plots/plot_result_comparison_by_closeness.png")
