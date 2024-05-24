import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv('raport.csv')

data['Microsoft Maintainability Index (MI)'] = pd.to_numeric(
    data['Microsoft Maintainability Index (MI)'],
    errors='coerce'
)

total_records = len(data)
mi_percentages = {}

for mi_value in range(10, 41):
    filtered_data = data[data['Microsoft Maintainability Index (MI)'] <= mi_value]
    mi_percentages[mi_value] = (len(filtered_data) / total_records) * 100

mi_values = list(mi_percentages.keys())
percentages = list(mi_percentages.values())

plt.figure(figsize=(10, 6))
plt.plot(mi_values, percentages, color='blue', marker='.')
plt.xlabel('Microsoft Maintainability Index (MI)')
plt.ylabel('Percentage of Records (%)')
plt.title('Percentage of Records by MI Value')
plt.xticks(range(10, 41, 5))
plt.grid(True, which='both', linestyle='--', linewidth=0.3)
plt.show()