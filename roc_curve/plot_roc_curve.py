import pandas as pd
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt
import numpy as np


metrics_df = pd.read_csv('raport.csv')
change_df = pd.read_csv('change_data.csv')

data = pd.merge(metrics_df, change_df, on='OriginalFilePath')

X = data['Lines Of Code (LOC)'].values.reshape(-1, 1)
y = data['was_changed'].values

fpr, tpr, thresholds = roc_curve(y, X)
roc_auc = auc(fpr, tpr)

optimal_idx = np.argmax(tpr - fpr)
optimal_threshold = thresholds[optimal_idx]


plt.figure()
plt.plot(fpr, tpr, color='darkorange', lw=2, label='ROC curve (area = %0.2f)' % roc_auc)
plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
plt.scatter(fpr[optimal_idx], tpr[optimal_idx], marker='o', color='black', label='Optimal Threshold = %0.2f' % optimal_threshold)
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Receiver Operating Characteristic')
plt.legend(loc="lower right")
plt.show()

print(f"ROC AUC: {roc_auc:.2f}")
print(f"Optimal Threshold for LOC: {optimal_threshold}")
