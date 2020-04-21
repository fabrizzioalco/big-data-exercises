/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author fabrizzioalco
 */

public class MovieRecommender {
    private int totalReviews;
    private int totalProducts;
    private int totalUsers;
    
    
       HashMap<String, Integer> Users = new HashMap();
       HashMap<String, Integer> Products = new HashMap();
       HashMap<Integer, String> Products2 = new HashMap();
    
     MovieRecommender(String path) throws FileNotFoundException, IOException{
        
        //writing the txt file
        File movieFile = new File(path);
        BufferedReader bufferInput = new BufferedReader(new FileReader(movieFile));
        
        //Writing the txt to csv
        File newMovieFile = new File("amazonMovies.csv");
        FileWriter fw = new FileWriter(newMovieFile);
        BufferedWriter bufferWriter = new BufferedWriter(fw);
        
        String userID = "";
        String productID = "";
        String userMovieScore = "";
        String line;
        
        while((line = bufferInput.readLine()) != null){
            
            switch(line.split(" ")[0]){
                case "review/userId:": 
                    userID = line.split(" ")[1];
                    
                     if(Users.containsKey(userID) != true){    
                         Users.put(userID, totalUsers);
                         totalUsers++;
                         
                    }
                    break;
                case "product/productId:":
                    productID = line.split(" ")[1];
                    if(Products.containsKey(productID) != true){
                        totalProducts++;
                        Products.put(productID, totalProducts);
                        Products2.put(totalProducts, productID);
                        
                    }
                    break;
                case "review/score:":
                    totalReviews++;
                    userMovieScore = line.split(" ")[1];
                    bufferWriter.write(totalUsers + "," + totalProducts + "," + userMovieScore);
                    break;            
            }
           
        }  
         bufferInput.close();
         bufferWriter.close();
    }
    
    public int getTotalReviews(){
        return this.totalReviews;
    }
     public int getTotalProducts(){
         return this.totalProducts;
     }
     
     public int getTotalUsers(){
         return this.totalUsers;
     }
     
     public List<String> getRecommendationsForUser(String userID) throws IOException, TasteException{
         
        DataModel model = new FileDataModel(new File("amazonMovies.csv"));
        
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        
        UserBasedRecommender recommender  = new GenericUserBasedRecommender(model, neighborhood, similarity);
        
        List<RecommendedItem> recommendations = recommender.recommend(Users.get(userID), 3);
        
        List<String> res = new ArrayList<String>();
        
        for(RecommendedItem recommendation: recommendations){
            res.add(Products2.get((int) recommendation.getItemID()));
        }
       
         return res;
     }
     
     
     
}

