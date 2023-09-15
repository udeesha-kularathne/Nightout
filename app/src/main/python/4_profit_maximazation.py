import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error

def train_and_predict_profit(data_file, new_data):
    # Importing the dataset
    dataset = pd.read_csv(data_file)
    X = dataset.iloc[:, :-1].values
    y = dataset.iloc[:, -1].values

    # Splitting the dataset into the Training set and Test set
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0)

    # Training the Multiple Linear Regression model on the Training set
    regressor = LinearRegression()
    regressor.fit(X_train, y_train)

    # Predict on new data
    predicted = regressor.predict(new_data)

    # Predict on the test set
    y_pred = regressor.predict(X_test)

    # Calculate Mean Squared Error
    mse = mean_squared_error(y_test, y_pred)

    return predicted[0], mse

# Example usage:
# data_file = 'profit.csv'
# new_data = [[3623, 3840, 2525, 4428, 41821, 2484, 270343, 12578, 252332]]
# predicted_profit, mse = train_and_predict_profit(data_file, new_data)
# print("Predicted Profit:", predicted_profit)
# print("Mean Squared Error:", mse)
