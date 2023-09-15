package com.nightout;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

public class PostTFIDF {

    // Function to tokenize text
    private String[] tokenizeText(String text) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        return tokenizer.tokenize(text);
    }

    // Function to remove stopwords (you need to define your list of stopwords)
    private String[] removeStopwords(String[] tokens) {
        List<String> stopwords = Arrays.asList("the", "and", "is"); // Define your stopwords
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopwords.contains(token.toLowerCase())) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens.toArray(new String[0]);
    }

    // Function to perform stemming
    private String[] stemWords(String[] tokens) {
        PorterStemmer stemmer = new PorterStemmer();
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = stemmer.stem(tokens[i]);
        }
        return tokens;
    }

    // Function to calculate term frequencies for a list of tokens
    private Map<String, Integer> calculateTermFrequencies(String[] tokens) {
        Map<String, Integer> termFrequencies = new HashMap<>();
        for (String token : tokens) {
            termFrequencies.put(token, termFrequencies.getOrDefault(token, 0) + 1);
        }
        return termFrequencies;
    }

    // Function to calculate TF-IDF values for a document
    private RealVector calculateTFIDF(Map<String, Integer> termFrequencies, Map<String, Double> idfValues) {
        RealVector tfidfVector = new ArrayRealVector(termFrequencies.size());
        int i = 0;
        for (String term : termFrequencies.keySet()) {
            double tf = termFrequencies.get(term);
            double idf = idfValues.getOrDefault(term, 0.0);
            tfidfVector.setEntry(i, tf * idf);
            i++;
        }
        return tfidfVector;
    }

    // Function to calculate IDF values for terms across all documents
    private Map<String, Double> calculateIDF(List<Map<String, Integer>> allTermFrequencies) {
        Map<String, Integer> documentFrequency = new HashMap<>();
        for (Map<String, Integer> termFrequencies : allTermFrequencies) {
            for (String term : termFrequencies.keySet()) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        Map<String, Double> idfValues = new HashMap<>();
        int numDocuments = allTermFrequencies.size();
        for (String term : documentFrequency.keySet()) {
            int documentFreq = documentFrequency.get(term);
            double idf = Math.log((double) numDocuments / (1 + documentFreq));
            idfValues.put(term, idf);
        }

        return idfValues;
    }

    // Function to calculate cosine similarity between two posts
    private double calculateCosineSimilarity(RealVector vector1, RealVector vector2) {
        return vector1.dotProduct(vector2) / (vector1.getNorm() * vector2.getNorm());
    }

    // Method to perform text similarity calculation
    public void calculateTextSimilarity() {
        // Sample posts (replace with your actual posts)
        String userPost = "This is the user's post.";
        String otherPost1 = "Another post with similar content.";
        String otherPost2 = "A different post.";
        String otherPost3 = "Irrelevant content.";

        // Preprocess the posts and calculate term frequencies for each document
        List<Map<String, Integer>> allTermFrequencies = new ArrayList<>();
        allTermFrequencies.add(calculateTermFrequencies(stemWords(removeStopwords(tokenizeText(userPost)))));
        allTermFrequencies.add(calculateTermFrequencies(stemWords(removeStopwords(tokenizeText(otherPost1)))));
        allTermFrequencies.add(calculateTermFrequencies(stemWords(removeStopwords(tokenizeText(otherPost2)))));
        allTermFrequencies.add(calculateTermFrequencies(stemWords(removeStopwords(tokenizeText(otherPost3)))));

        // Calculate IDF values for terms across all documents
        Map<String, Double> idfValues = calculateIDF(allTermFrequencies);

        // Calculate TF-IDF vectors for all documents
        RealVector userTFIDF = calculateTFIDF(allTermFrequencies.get(0), idfValues);
        RealVector otherTFIDF1 = calculateTFIDF(allTermFrequencies.get(1), idfValues);
        RealVector otherTFIDF2 = calculateTFIDF(allTermFrequencies.get(2), idfValues);
        RealVector otherTFIDF3 = calculateTFIDF(allTermFrequencies.get(3), idfValues);

        // Calculate cosine similarity between user's post and other posts
        double similarity1 = calculateCosineSimilarity(userTFIDF, otherTFIDF1);
        double similarity2 = calculateCosineSimilarity(userTFIDF, otherTFIDF2);
        double similarity3 = calculateCosineSimilarity(userTFIDF, otherTFIDF3);

        // Display or rank the posts based on similarity values
        System.out.println("Similarity1: " + similarity1);
        System.out.println("Similarity2: " + similarity2);
        System.out.println("Similarity3: " + similarity3);
    }

    public static void main(String[] args) {
        PostTFIDF postTFIDF = new PostTFIDF();
        postTFIDF.calculateTextSimilarity();
    }
}
