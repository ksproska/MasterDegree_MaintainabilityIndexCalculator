import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('change_data.csv')

commit_map = {
    'e6b43a17099eff099a05572ff0b2724485e54211': '8.13.4->8.13.5',
    '7eae95620b41c8c42a647b059b096703b4d510f4': '8.13.3->8.13.4',
    '95c7c0978020de5bac685802655bfab3f475e628': '8.13.2->8.13.3',
    'f7fedb4d0aec5dc60bf52bb4c460584d08a236ce': '8.13.1->8.13.2',
    '93a21e1b14c6ca611b477360c7c7f65846bd364e': '8.13.0->8.13.1'
}


def extract_id(path):
    parts = path.split('/')
    if len(parts) >= 8:
        return commit_map.get(parts[7])
    return None


df['Numer wersji'] = df['OriginalFilePath'].apply(extract_id)
df['czy była wprowadzona zmiana'] = df['was_changed'].map({1: 'tak', 0: 'nie'})

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))

summary = df.groupby(['Numer wersji', 'czy była wprowadzona zmiana']).size().unstack(fill_value=0)
summary.plot(kind='bar', stacked=True, ax=ax1)

# Add labels to the bars
for i, (idx, row) in enumerate(summary.iterrows()):
    cum_value = 0
    for col in summary.columns:
        value = row[col]
        if col == 'tak':
            ax1.text(i, cum_value + value, int(value), ha='center', va='bottom')  # Place above the bar for 'tak'
        else:
            ax1.text(i, cum_value + value / 2, int(value), ha='center', va='center')  # Place inside the bar for 'nie'
        cum_value += value

ax1.set_xlabel('Numer wersji elasticsearch')
ax1.set_ylabel('Liczba metod')
ax1.set_title('Porównanie danych względem wersji')
ax1.legend(title='Czy była wprowadzona zmiana')
ax1.set_xticklabels(ax1.get_xticklabels(), rotation=0)

pie_data = df['czy była wprowadzona zmiana'].value_counts()
ax2.pie(pie_data, labels=pie_data.index, autopct='%1.1f%%', startangle=140)
ax2.set_title('Porównanie danych dla wszystkich wersji')
ax2.legend(title="Czy była wprowadzona zmiana")

plt.tight_layout()
plt.savefig("plot_change_data_stats.png", bbox_inches='tight')
