import pandas as pd
import numpy as np

data = pd.read_csv('raport.csv')

metrics = [
    'Halstead Volume',
    'Cyclomatic Complexity (CC)',
    'Lines Of Code (LOC)',
    'Microsoft Maintainability Index (MI)'
]

translation_map = {
    'Halstead Volume': 'Objętość Halsteada',
    'Cyclomatic Complexity (CC)': 'Złożoność Cyklomatyczna (CC)',
    'Lines Of Code (LOC)': 'Liczba Linii Kodu (LOC)',
    'Microsoft Maintainability Index (MI)': 'Indeks Utrzymywalności (MI)',
    'Maintainability Index (MI) - calculated': 'Wyliczony Indeks Utrzymywalności (MI)'
}

constant_for_0_values = 0.0001
for metric in metrics:
    skewness_before = data[metric].skew()
    log_metric = np.log(data[metric] + constant_for_0_values)
    unique_values, counts = np.unique(log_metric, return_counts=True)
    skewness_after = log_metric.skew()
    print(f'{translation_map[metric]} & {skewness_before:.2f} & {skewness_after:.2f} \\\\')
