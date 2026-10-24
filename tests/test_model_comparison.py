"""
Unit tests for Model Comparison module (ModelComparison/comparison_logic.py).
"""

import pytest
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

from ModelComparison.comparison_logic import (
    get_available_datasets,
    load_dataset_by_name,
    validate_dataset,
    preprocess_and_split,
    build_preprocessor,
    get_classification_models,
    train_and_evaluate_models,
    plot_comparison_chart,
    plot_confusion_matrix
)


def test_get_available_datasets():
    datasets = get_available_datasets()
    assert isinstance(datasets, list)
    assert "Iris" in datasets
    assert "Wine" in datasets
    assert "Breast Cancer" in datasets
    assert "Titanic" in datasets


def test_load_dataset_by_name():
    for ds_name in ["Iris", "Wine", "Breast Cancer", "Titanic"]:
        df, target_col = load_dataset_by_name(ds_name)
        assert isinstance(df, pd.DataFrame)
        assert not df.empty
        assert target_col in df.columns


def test_validate_dataset():
    # Valid classification dataset
    df_valid = pd.DataFrame({
        "feature1": [1.0, 2.0, 3.0, 4.0, 5.0, 6.0],
        "feature2": ["A", "B", "A", "B", "A", "B"],
        "target": [0, 1, 0, 1, 0, 1]
    })
    is_valid, msg = validate_dataset(df_valid, "target")
    assert is_valid is True
    assert msg == ""

    # Empty dataset
    is_valid, msg = validate_dataset(pd.DataFrame(), "target")
    assert is_valid is False
    assert "empty" in msg

    # Missing target column
    is_valid, msg = validate_dataset(df_valid, "non_existent_col")
    assert is_valid is False
    assert "not found" in msg

    # Single class dataset
    df_single_class = pd.DataFrame({"f1": [1, 2, 3], "target": [1, 1, 1]})
    is_valid, msg = validate_dataset(df_single_class, "target")
    assert is_valid is False
    assert "fewer than 2 unique classes" in msg

    # Continuous float target with many unique values
    df_continuous = pd.DataFrame({"f1": range(30), "target": [i * 0.5 for i in range(30)]})
    is_valid, msg = validate_dataset(df_continuous, "target")
    assert is_valid is False
    assert "continuous numeric variable" in msg


def test_preprocess_and_split():
    df, target_col = load_dataset_by_name("Iris")
    X, y, X_train, X_test, y_train, y_test, num_cols, cat_cols = preprocess_and_split(
        df, target_col, test_size=0.2, random_state=42
    )

    assert len(X_train) + len(X_test) == len(df)
    assert len(y_train) + len(y_test) == len(df)
    assert len(num_cols) > 0
    assert isinstance(cat_cols, list)


def test_train_and_evaluate_models_iris():
    df, target_col = load_dataset_by_name("Iris")
    metrics_df, results = train_and_evaluate_models(df, target_col, test_size=0.3, random_state=42)

    assert isinstance(metrics_df, pd.DataFrame)
    assert len(metrics_df) == 3
    expected_models = {"Logistic Regression", "Decision Tree", "Random Forest"}
    assert set(metrics_df["Model"]) == expected_models

    for col in ["Accuracy", "Precision", "Recall", "F1-Score"]:
        assert col in metrics_df.columns
        for val in metrics_df[col]:
            assert 0.0 <= val <= 1.0

    for model_name in expected_models:
        assert model_name in results
        assert "pipeline" in results[model_name]
        assert "y_test" in results[model_name]
        assert "y_pred" in results[model_name]


def test_train_and_evaluate_models_titanic_with_missing_values():
    """Tests that missing values in Titanic dataset are handled seamlessly."""
    df, target_col = load_dataset_by_name("Titanic")
    # Verify Titanic contains missing values
    assert df.isna().sum().sum() > 0

    metrics_df, results = train_and_evaluate_models(df, target_col, test_size=0.2, random_state=42)

    assert len(metrics_df) == 3
    for col in ["Accuracy", "Precision", "Recall", "F1-Score"]:
        for val in metrics_df[col]:
            assert 0.0 <= val <= 1.0


def test_plotting_functions():
    df, target_col = load_dataset_by_name("Wine")
    metrics_df, results = train_and_evaluate_models(df, target_col, test_size=0.2, random_state=42)

    # Comparison chart
    fig_chart = plot_comparison_chart(metrics_df)
    assert isinstance(fig_chart, plt.Figure)
    plt.close(fig_chart)

    # Confusion matrix chart
    res = results["Logistic Regression"]
    fig_cm = plot_confusion_matrix(res["y_test"], res["y_pred"], res["class_labels"], "Logistic Regression")
    assert isinstance(fig_cm, plt.Figure)
    plt.close(fig_cm)


def test_incompatible_dataset_handling():
    df_invalid = pd.DataFrame({"f1": [1, 2, 3], "target": [1, 1, 1]})
    with pytest.raises(ValueError) as exc_info:
        train_and_evaluate_models(df_invalid, "target")
    assert "fewer than 2 unique classes" in str(exc_info.value)
