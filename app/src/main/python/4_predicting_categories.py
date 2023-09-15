import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score

def train_and_predict_category(data_file, new_data):
    # Load the category dataset
    category = pd.read_csv(data_file)

    # Split dataset into features and target variable
    X = category.iloc[:, :-1].values
    y = category.iloc[:, -1].values

    # Split the dataset into the Training set and Test set
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)

    # Feature Scaling
    sc = StandardScaler()
    X_train = sc.fit_transform(X_train)
    X_test = sc.transform(X_test)

    # Training the Decision Tree Classification model
    classifier = DecisionTreeClassifier(criterion='entropy', random_state=0)
    classifier.fit(X_train, y_train)

    # Predicting the category for new data
    new_result = classifier.predict(new_data)

    # Predicting the Test set results
    y_pred = classifier.predict(X_test)

    # Calculate accuracy
    accuracy = accuracy_score(y_test, y_pred)

    return new_result[0], accuracy

# Example usage:
# data_file = 'category.csv'
# new_data = [[Marketing, Labor, Utilities, Personnel, Venue, Material, Ticket, Sales, Sponsor]]
# result, accuracy = train_and_predict_category(data_file, new_data)
# print("New Result:", result)
# print("Accuracy:", accuracy)
