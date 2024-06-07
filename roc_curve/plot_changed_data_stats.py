import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('change_data.csv')

commit_map = {
        'e6b43a17099eff099a05572ff0b2724485e54211': '8.13.4',
        '7eae95620b41c8c42a647b059b096703b4d510f4': '8.13.3',
        '95c7c0978020de5bac685802655bfab3f475e628': '8.13.2',
        'f7fedb4d0aec5dc60bf52bb4c460584d08a236ce': '8.13.1',
        '93a21e1b14c6ca611b477360c7c7f65846bd364e': '8.13.0'
    }


def extract_id(path):
    parts = path.split('/')
    if len(parts) >= 8:
        return commit_map[parts[7]]
    return None


df['ID'] = df['OriginalFilePath'].apply(extract_id)

summary = df.groupby(['ID', 'was_changed']).size().unstack(fill_value=0)

summary.plot(kind='bar', stacked=True)
# plt.title('Change Summary per ID')
plt.xlabel('Numer wersji')
plt.ylabel('Liczba metod')
plt.savefig("plot_change_data_stats.png", bbox_inches='tight')
