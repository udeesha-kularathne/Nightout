import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import accuracy_score

def train_naive_bayes_classifier(data_file, new_data):
    # Importing the dataset
    dataset = pd.read_csv(data_file)
    X = dataset.iloc[:, :-1].values
    y = dataset.iloc[:, -1].values

    # Splitting the dataset into the Training set and Test set
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)

    # Feature Scaling
    sc = StandardScaler()
    X_train = sc.fit_transform(X_train)
    X_test = sc.transform(X_test)

    # Training the Naive Bayes model on the Training set
    classifier = GaussianNB()
    classifier.fit(X_train, y_train)

    # Predicting a new result
    new_result = classifier.predict(sc.transform([new_data]))

    # Predicting the Test set results
    y_pred = classifier.predict(X_test)

    # Calculate accuracy
    accuracy = accuracy_score(y_test, y_pred)

    return new_result[0], accuracy

# Example usage:
# data_file = '../assets/behavior.csv'
# new_data = [12750, 5666, 22026, 14859, 7588]
# result, accuracy = train_naive_bayes_classifier(data_file, new_data)
# print("New Result:", result)
# print("Accuracy:", accuracy)
