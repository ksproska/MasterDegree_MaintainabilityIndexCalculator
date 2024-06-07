import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

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

from io import StringIO
df = pd.read_csv(StringIO(data))

df['Etykieta'] = df['Etykieta'].replace({
    r'\ensuremath{\mu + 3\Omega}': 'μ + 3Ω',
    r'\ensuremath{\mu + 2\Omega}': 'μ + 2Ω',
    r'\ensuremath{\mu + \Omega}': 'μ + Ω'
})

index = np.arange(len(df['Etykieta']))

additional_spacing = 1
index[3:] += additional_spacing
index[8:] += additional_spacing
index[9:] += additional_spacing

bar_width = 0.35

fig, ax = plt.subplots(figsize=(8, 5))
bar1 = ax.bar(index - bar_width/2, df['Wyliczanie pośrednie'], bar_width, label='Wyliczanie pośrednie', color='blue')
bar2 = ax.bar(index + bar_width/2, df['Wyliczanie bezpośrednie'], bar_width, label='Wyliczanie bezpośrednie', color='green')
bar3 = ax.bar(index + bar_width/2, df['Ankieta 50%'], bar_width, color='red')
bar4 = ax.bar(index + bar_width/2, df['Ankieta 100%'], bar_width, color='orange')

if not df['Ankieta 50%'].isna().all():
    ankieta_50_value = df['Ankieta 50%'].dropna().iloc[0]
    ax.axhline(y=ankieta_50_value, color='red', linestyle='--')
if not df['Ankieta 100%'].isna().all():
    ankieta_100_value = df['Ankieta 100%'].dropna().iloc[0]
    ax.axhline(y=ankieta_100_value, color='orange', linestyle='--')

ax.set_xlabel('Metody wyliczania metryk')
ax.set_ylabel('Wartości brzegowe MI')
ax.set_xticks(index)
ax.set_xticklabels(df['Etykieta'])
ax.legend()
plt.xticks(rotation=45)
plt.tight_layout()

plt.savefig("./plots/plot_result_comparison.png")
