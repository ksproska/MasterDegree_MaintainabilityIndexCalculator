import pandas as pd
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import numpy as np

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

metrics_df = pd.read_csv('raport.csv')
change_df = pd.read_csv('change_data.csv')

data = pd.merge(metrics_df, change_df, on='OriginalFilePath')

y = data['was_changed'].values

plt.figure(figsize=(10, 10))

for i, metric in enumerate(metrics, 1):
    X = data[metric].values.reshape(-1, 1)

    fpr, tpr, thresholds = roc_curve(y, X)
    roc_auc = auc(fpr, tpr)

    optimal_idx = np.argmax(tpr - fpr)
    optimal_threshold = thresholds[optimal_idx]

    plt.subplot(2, 2, i)
    plt.plot(fpr, tpr, color='darkorange', lw=2, label=f'ROC curve (area = {roc_auc:.2f})')
    plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
    plt.scatter(fpr[optimal_idx], tpr[optimal_idx], marker='o', color='black',
                label=f'Optimal Threshold = {optimal_threshold:.2f}')
    plt.xlim([0.0, 1.0])
    plt.ylim([0.0, 1.05])
    plt.xlabel('Współczynnik False Positive')
    plt.ylabel('Współczynnik True Positive')
    plt.title(f'Krzywa ROC dla {translation_map[metric]}')
    plt.legend(loc="lower right")

plt.tight_layout()
plt.savefig("plot_roc_curve_for_all.png", bbox_inches='tight')
