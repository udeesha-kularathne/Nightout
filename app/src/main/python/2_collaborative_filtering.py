import pandas as pd

def calculate_user_similarity(user_data, picked_user, n=10, similarity_threshold=0.3, m=10):
    # Load user interest data
    interest = pd.read_csv(user_data)

    # Create a matrix
    matrix = interest.pivot_table(index=['User'], columns=["Event"], values=["Interest"])

    # Matrix normalization
    matrix_norm = matrix.subtract(matrix.mean(axis=1), axis='rows')

    # User similarity matrix using Pearson correlation
    similarity = matrix_norm.T.corr()

    # Remove the picked user from the candidates list
    similarity.drop(index=picked_user, inplace=True)

    # Get top n similar users based on the threshold
    similar_users = similarity[similarity[picked_user] > similarity_threshold][picked_user].sort_values(ascending=False)[:n]

    # A dictionary to store item scores
    item_score = {}

    for event in matrix_norm.columns:
        event_rating = matrix_norm[("Interest", event)]
        total = 0
        count = 0

        for user in similar_users.index:
            if not pd.isna(event_rating[user]):
                score = similar_users[user] * event_rating[user]
                total += score
                count += 1

        if count > 0:
            item_score[event] = total / count

    # Convert dictionary to pandas dataframe
    item_score_df = pd.DataFrame(item_score.items(), columns=['event', 'event_score'])

    # Sort the items by score
    ranked_item_score = item_score_df.sort_values(by='event_score', ascending=False)

    # Select top m items
    top_items = ranked_item_score.head(m)

    return top_items

# Example usage:
# result = calculate_user_similarity("community.csv", "usr-23")
# print(result)
