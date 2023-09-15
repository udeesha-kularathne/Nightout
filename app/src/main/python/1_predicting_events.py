import csv
import pandas as pd
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel

def insert_record():
    ndf = pd.DataFrame({
        'User': ['usr-temp'],
        'Description': ['travel bungalow 1-star indoor outdoor day night']
    })
    ndf.to_csv('users.csv', mode='a', index=False, header=False)

def get_recommendations(title, cosine_sim):
    df = pd.read_csv('users.csv')
    tfidf = TfidfVectorizer(stop_words="english")
    df['Description'] = df['Description'].fillna("")
    tfidf_matrix = tfidf.fit_transform(df['Description'])
    cosine_similarity = linear_kernel(tfidf_matrix, tfidf_matrix)
    indices = pd.Series(df.index, index=df['User']).drop_duplicates()
    idx = indices[title]
    similarity_scores = enumerate(cosine_similarity[idx])
    similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)
    similarity_index = [i[0] for i in similarity_scores]
    return df['User'].iloc[similarity_index].tolist()

def delete_last_record():
    with open('users.csv', 'r', newline='') as csvfile:
        rows = list(csv.reader(csvfile))
    with open('users.csv', 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerows(rows[:-1])
