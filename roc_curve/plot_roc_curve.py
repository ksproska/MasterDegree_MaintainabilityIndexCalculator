import math

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
    'Halstead Volume': 'Objętości Halsteada',
    'Cyclomatic Complexity (CC)': 'Złożoności Cyklomatycznej (CC)',
    'Lines Of Code (LOC)': 'Liczby Linii Kodu (LOC)',
    'Microsoft Maintainability Index (MI)': 'Indeksu Utrzymywalności (MI) (odwrócona)'
}


def calculate_maintainability_index(halstead_volume, cc, loc):
    return int(max(0, (171 - 5.2 * math.log(halstead_volume) - 0.23 * cc - 16.2 * math.log(loc)) * (100 / 171)))


def main():
    metrics_df = pd.read_csv('raport.csv')
    change_df = pd.read_csv('change_data.csv')

    data = pd.merge(metrics_df, change_df, on='OriginalFilePath')

    y = data['was_changed'].values

    plt.figure(figsize=(10, 10))

    calculated_thresholds = {}
    for i, metric in enumerate(metrics, 1):
        X = data[metric].values.reshape(-1, 1)

        if metric == 'Microsoft Maintainability Index (MI)':
            X = -X  # Reverse the scoring for MI, as higher scores are better

        fpr, tpr, thresholds = roc_curve(y, X)
        roc_auc = auc(fpr, tpr)

        optimal_idx = np.argmax(tpr - fpr)
        optimal_threshold = abs(thresholds[optimal_idx])
        calculated_thresholds[metric] = optimal_threshold

        plt.subplot(2, 2, i)
        plt.plot(fpr, tpr, color='darkorange', lw=2, label=f'AUC = {roc_auc:.2f}')
        plt.plot([0, 1], [0, 1], color='black', lw=1, linestyle='--')
        plt.scatter(fpr[optimal_idx], tpr[optimal_idx], marker='o', color='black',
                    label=f'Wartość brzegowa = {optimal_threshold:.1f}')
        plt.xlim([0.0, 1.0])
        plt.ylim([0.0, 1.05])
        plt.xlabel('FPR')
        plt.ylabel('TPR')
        plt.title(f'Krzywa ROC dla {translation_map[metric]}')
        plt.legend(loc="lower right")

    plt.tight_layout()
    plt.savefig("plot_roc_curve_for_all.png", bbox_inches='tight')

    cc = calculated_thresholds['Cyclomatic Complexity (CC)']
    hv = calculated_thresholds['Halstead Volume']
    loc = calculated_thresholds['Lines Of Code (LOC)']
    mi_calculated = calculate_maintainability_index(hv, cc, loc)
    print(mi_calculated, calculated_thresholds['Microsoft Maintainability Index (MI)'])


if __name__ == '__main__':
    main()
