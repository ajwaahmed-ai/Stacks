"""
Core logic module for Model Comparison functionality.
Handles dataset loading, validation, pipeline preprocessing (fitting only on train set),
model training (Logistic Regression, Decision Tree, Random Forest),
metrics calculation, and figure generation.
"""

import os
from typing import Dict, Tuple, Any, Optional, List
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

from sklearn.datasets import load_iris, load_wine, load_breast_cancer
from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    confusion_matrix,
    ConfusionMatrixDisplay
)


AVAILABLE_DATASETS = ["Iris", "Wine", "Breast Cancer", "Titanic"]


def get_available_datasets() -> List[str]:
    """Returns the list of available dataset names in the project."""
    return AVAILABLE_DATASETS.copy()


def load_dataset_by_name(name: str, csv_path: str = "datasets/train.csv") -> Tuple[pd.DataFrame, str]:
    """
    Loads a dataset by name.

    Returns:
        df (pd.DataFrame): DataFrame containing features and target.
        target_col (str): Name of the target column.
    """
    if name == "Iris":
        data = load_iris()
        df = pd.DataFrame(data.data, columns=data.feature_names)
        df["target"] = data.target
        return df, "target"
    elif name == "Wine":
        data = load_wine()
        df = pd.DataFrame(data.data, columns=data.feature_names)
        df["target"] = data.target
        return df, "target"
    elif name == "Breast Cancer":
        data = load_breast_cancer()
        df = pd.DataFrame(data.data, columns=data.feature_names)
        df["target"] = data.target
        return df, "target"
    elif name == "Titanic":
        if not os.path.exists(csv_path):
            raise FileNotFoundError(f"CSV file not found at path: {csv_path}")
        df = pd.read_csv(csv_path)
        target_col = "Survived" if "Survived" in df.columns else df.columns[-1]
        return df, target_col
    else:
        raise ValueError(f"Unknown dataset name: {name}")


def validate_dataset(df: Optional[pd.DataFrame], target_col: str) -> Tuple[bool, str]:
    """
    Validates whether the DataFrame and target column are suitable for classification tasks.

    Returns:
        (is_valid, error_message)
    """
    if df is None or df.empty:
        return False, "The dataset is empty or None."

    if target_col not in df.columns:
        return False, f"Target column '{target_col}' not found in dataset."

    # Drop missing values in target for validation
    y = df[target_col].dropna()
    if len(y) == 0:
        return False, f"Target column '{target_col}' contains only missing values."

    unique_classes = y.nunique()
    if unique_classes < 2:
        return False, f"Target column '{target_col}' has fewer than 2 unique classes ({unique_classes}). Classification requires at least 2 classes."

    # Check if target appears continuous (e.g. float with many distinct values)
    if pd.api.types.is_float_dtype(y) and unique_classes > 20:
        return False, f"Target column '{target_col}' appears to be a continuous numeric variable with {unique_classes} unique values. Model comparison is for classification tasks."

    return True, ""


def preprocess_and_split(
    df: pd.DataFrame,
    target_col: str,
    test_size: float = 0.2,
    random_state: int = 42
) -> Tuple[pd.DataFrame, pd.Series, Any, Any, Any, Any, List[str], List[str]]:
    """
    Prepares features X and target y, drops uninformative identifier columns if present,
    creates numerical and categorical preprocessor transformers,
    and performs a reproducible train_test_split.

    Returns:
        X, y, X_train, X_test, y_train, y_test, num_cols, cat_cols
    """
    df_clean = df.dropna(subset=[target_col]).copy()

    # Drop known non-predictive high-cardinality/identifier columns if present
    cols_to_drop = [target_col]
    for drop_candidate in ["PassengerId", "Name", "Ticket", "Cabin"]:
        if drop_candidate in df_clean.columns:
            cols_to_drop.append(drop_candidate)

    X = df_clean.drop(columns=[c for c in cols_to_drop if c in df_clean.columns])
    y = df_clean[target_col]

    # Identify numerical and categorical columns
    num_cols = X.select_dtypes(include=[np.number]).columns.tolist()
    cat_cols = X.select_dtypes(include=["object", "category", "string"]).columns.tolist()

    # Perform reproducible train-test split
    # Try stratify if target has sufficient sample count per class
    stratify_arg = y if y.value_counts().min() >= 2 else None

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=test_size,
        random_state=random_state,
        stratify=stratify_arg
    )

    return X, y, X_train, X_test, y_train, y_test, num_cols, cat_cols


def build_preprocessor(num_cols: List[str], cat_cols: List[str]) -> ColumnTransformer:
    """
    Builds ColumnTransformer that imputes and scales numerical features,
    and imputes and one-hot encodes categorical features.
    """
    transformers = []
    if num_cols:
        num_pipeline = Pipeline([
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler())
        ])
        transformers.append(("num", num_pipeline, num_cols))

    if cat_cols:
        cat_pipeline = Pipeline([
            ("imputer", SimpleImputer(strategy="most_frequent")),
            ("encoder", OneHotEncoder(handle_unknown="ignore", sparse_output=False))
        ])
        transformers.append(("cat", cat_pipeline, cat_cols))

    preprocessor = ColumnTransformer(transformers=transformers, remainder="drop")
    return preprocessor


def get_classification_models(random_state: int = 42) -> Dict[str, Any]:
    """
    Returns a dictionary of classification model instances.
    """
    return {
        "Logistic Regression": LogisticRegression(random_state=random_state, max_iter=1000),
        "Decision Tree": DecisionTreeClassifier(random_state=random_state),
        "Random Forest": RandomForestClassifier(random_state=random_state)
    }


def train_and_evaluate_models(
    df: pd.DataFrame,
    target_col: str,
    test_size: float = 0.2,
    random_state: int = 42
) -> Tuple[pd.DataFrame, Dict[str, Any]]:
    """
    Validates dataset, builds preprocessing pipeline, fits pipelines ONLY on X_train,
    evaluates models on X_test, and returns comparison metrics DataFrame and results dictionary.

    Returns:
        metrics_df (pd.DataFrame): Table comparing Accuracy, Precision, Recall, F1-Score for each model.
        results (Dict): Detailed results containing fitted pipeline, predictions, y_test, y_train, class labels.
    """
    is_valid, err_msg = validate_dataset(df, target_col)
    if not is_valid:
        raise ValueError(err_msg)

    X, y, X_train, X_test, y_train, y_test, num_cols, cat_cols = preprocess_and_split(
        df, target_col, test_size=test_size, random_state=random_state
    )

    preprocessor = build_preprocessor(num_cols, cat_cols)
    models = get_classification_models(random_state=random_state)

    results = {}
    metrics_records = []

    # Get sorted unique class labels
    class_labels = np.unique(y)

    for model_name, model in models.items():
        # Fit pipeline ONLY on training data
        pipeline = Pipeline([
            ("preprocessor", preprocessor),
            ("classifier", model)
        ])
        pipeline.fit(X_train, y_train)

        # Predict on test set
        y_pred = pipeline.predict(X_test)

        acc = accuracy_score(y_test, y_pred)
        prec = precision_score(y_test, y_pred, average="weighted", zero_division=0)
        rec = recall_score(y_test, y_pred, average="weighted", zero_division=0)
        f1 = f1_score(y_test, y_pred, average="weighted", zero_division=0)

        metrics_records.append({
            "Model": model_name,
            "Accuracy": round(float(acc), 4),
            "Precision": round(float(prec), 4),
            "Recall": round(float(rec), 4),
            "F1-Score": round(float(f1), 4)
        })

        results[model_name] = {
            "pipeline": pipeline,
            "y_test": y_test,
            "y_pred": y_pred,
            "class_labels": class_labels,
            "metrics": {
                "Accuracy": acc,
                "Precision": prec,
                "Recall": rec,
                "F1-Score": f1
            }
        }

    metrics_df = pd.DataFrame(metrics_records)
    return metrics_df, results


def plot_comparison_chart(metrics_df: pd.DataFrame) -> plt.Figure:
    """
    Generates a grouped bar chart figure comparing metrics across models.
    """
    sns.set_theme(style="darkgrid")
    df_melted = metrics_df.melt(id_vars=["Model"], var_name="Metric", value_name="Score")

    fig, ax = plt.subplots(figsize=(10, 5))
    palette = sns.color_palette("deep", len(metrics_df["Model"].unique()))
    chart = sns.barplot(
        data=df_melted,
        x="Metric",
        y="Score",
        hue="Model",
        ax=ax,
        palette="Blues_r"
    )

    ax.set_ylim(0, 1.1)
    ax.set_ylabel("Score", fontsize=12)
    ax.set_xlabel("Metric", fontsize=12)
    ax.set_title("Model Performance Comparison", fontsize=14, fontweight="bold")
    ax.legend(title="Model", loc="lower right")

    # Annotate bars with values
    for p in chart.patches:
        height = p.get_height()
        if not np.isnan(height) and height > 0:
            ax.annotate(
                f"{height:.2f}",
                (p.get_x() + p.get_width() / 2., height),
                ha="center", va="bottom",
                fontsize=9,
                xytext=(0, 2),
                textcoords="offset points"
            )

    plt.tight_layout()
    return fig


def plot_confusion_matrix(y_test: Any, y_pred: Any, class_labels: Any, model_name: str) -> plt.Figure:
    """
    Generates a Matplotlib figure for the Confusion Matrix of a selected model.
    """
    sns.set_theme(style="white")
    cm = confusion_matrix(y_test, y_pred, labels=class_labels)
    disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=class_labels)

    fig, ax = plt.subplots(figsize=(6, 5))
    disp.plot(ax=ax, cmap="Blues", values_format="d", colorbar=False)
    ax.set_title(f"Confusion Matrix - {model_name}", fontsize=13, fontweight="bold")
    plt.tight_layout()
    return fig
