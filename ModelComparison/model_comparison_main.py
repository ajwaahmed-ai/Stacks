"""
UI page for Model Comparison functionality.
Matches existing layout style and integrates with main navigation.
"""

import streamlit as st
import pandas as pd

from ModelComparison.comparison_logic import (
    get_available_datasets,
    load_dataset_by_name,
    validate_dataset,
    train_and_evaluate_models,
    plot_comparison_chart,
    plot_confusion_matrix
)


def model_comparison_main():
    """Main rendering function for the Model Comparison page."""
    # Split layout into left control menu and right main display area
    left_col, main_col = st.columns([1, 4])

    with left_col:
        st.subheader("Comparison Settings")
        st.divider()

        # Dataset selection
        dataset_options = get_available_datasets()
        if "dataset" in st.session_state and isinstance(st.session_state["dataset"], pd.DataFrame):
            dataset_options = ["Loaded Dataset (from EDA)"] + dataset_options

        selected_dataset_name = st.selectbox(
            "Select Dataset:",
            options=dataset_options,
            index=0,
            key="model_comp_dataset_select"
        )

        # Load dataset
        try:
            if selected_dataset_name == "Loaded Dataset (from EDA)":
                df = st.session_state["dataset"]
                default_target = "target" if "target" in df.columns else df.columns[-1]
            else:
                df, default_target = load_dataset_by_name(selected_dataset_name)
        except Exception as e:
            st.error(f"Error loading dataset: {e}")
            return

        # Target column selection
        target_col = st.selectbox(
            "Select Target Column:",
            options=list(df.columns),
            index=list(df.columns).index(default_target) if default_target in df.columns else len(df.columns) - 1,
            key="model_comp_target_select"
        )

        st.divider()
        st.subheader("Split Parameters")
        test_size_percent = st.slider(
            "Test Set Size (%)",
            min_value=10,
            max_value=50,
            value=20,
            step=5,
            key="model_comp_test_size"
        )
        test_size = test_size_percent / 100.0

        random_state = st.number_input(
            "Random Seed",
            min_value=0,
            max_value=9999,
            value=42,
            step=1,
            key="model_comp_seed"
        )

    with main_col:
        st.title("Model Comparison")
        st.write(
            "Compare the performance of **Logistic Regression**, **Decision Tree**, and "
            "**Random Forest** classifiers on your selected dataset."
        )
        st.divider()

        # Validate dataset before training
        is_valid, err_msg = validate_dataset(df, target_col)
        if not is_valid:
            st.warning(f"Cannot perform model comparison: {err_msg}")
            return

        try:
            with st.spinner("Training and evaluating models..."):
                metrics_df, results = train_and_evaluate_models(
                    df=df,
                    target_col=target_col,
                    test_size=test_size,
                    random_state=random_state
                )

            # Display Metrics Summary Table
            st.subheader("1. Evaluation Metrics Summary")
            st.dataframe(metrics_df, use_container_width=True)

            st.divider()

            # Display Comparison Chart
            st.subheader("2. Performance Comparison Chart")
            fig_chart = plot_comparison_chart(metrics_df)
            st.pyplot(fig_chart)

            st.divider()

            # Display Confusion Matrix
            st.subheader("3. Confusion Matrix")
            selected_model = st.radio(
                "Select Model for Confusion Matrix:",
                options=["Logistic Regression", "Decision Tree", "Random Forest"],
                horizontal=True,
                key="model_comp_cm_select"
            )

            if selected_model in results:
                res = results[selected_model]
                fig_cm = plot_confusion_matrix(
                    y_test=res["y_test"],
                    y_pred=res["y_pred"],
                    class_labels=res["class_labels"],
                    model_name=selected_model
                )
                st.pyplot(fig_cm)

        except Exception as e:
            st.error(f"An error occurred during model comparison: {str(e)}")
