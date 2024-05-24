from datetime import datetime
import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

data['Microsoft Maintainability Index (MI)'] = pd.to_numeric(
    data['Microsoft Maintainability Index (MI)'],
    errors='coerce'
)

total_records = len(data)
mi_percentages = {}

for mi_value in range(10, 51):
    filtered_data = data[data['Microsoft Maintainability Index (MI)'] <= mi_value]
    mi_percentages[mi_value] = (len(filtered_data) / total_records) * 100

mi_values = list(mi_percentages.keys())
percentages = list(mi_percentages.values())

plt.figure(figsize=(10, 6))
plt.plot(mi_values, percentages, color='blue', marker='.', linestyle='--', linewidth=0.6)

for mi, percentage in zip(mi_values, percentages):
    if mi % 5 == 0:
        color = 'darkblue'
        if mi == 10:
            color = 'red'
        if mi == 20:
            color = 'orange'
        plt.plot(mi, percentage, color=color, marker='o', linestyle='')
        plt.annotate(f'{percentage:.3f}%',
                     (mi, percentage),
                     textcoords="offset points",
                     xytext=(0, 10),
                     ha='center')

plt.xlabel('Microsoft Maintainability Index (MI)')
plt.ylabel('Percentage of Records below threshold MI value (%)')
plt.title('Cumulative Percentage of Records by MI Value')
plt.xticks(range(10, 51, 5))
plt.grid(True, which='both', linestyle='--', linewidth=0.3)

timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
filename = f'./plots/plot_cumulative_percentage_by_mi_{timestamp}.png'

plt.savefig(filename)
print(f'Plot saved as {filename}')
