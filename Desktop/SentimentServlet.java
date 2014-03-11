

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Query.FilterOperator;
//import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;


import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import com.google.appengine.labs.repackaged.org.json.JSONArray;





import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterException;

@SuppressWarnings("serial")
public class SentimentServlet extends HttpServlet {
	public static DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	public static ArrayList<String> check=new ArrayList<String>();
	public static ArrayList<String> hash=new ArrayList<String>();
	public List<Entry<String,Integer>> lst=new ArrayList<Entry<String,Integer>>();
 	public static Map<String,Integer> map=null;
	public ArrayList tmp=new ArrayList<String>();
	public static String forsort="";
	public static ArrayList<Status> tweetdata=new ArrayList<Status>();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		  PrintWriter out = resp.getWriter();
		  try
		  { 
	          
			  System.out.println("1");
	          String cat= req.getParameter("category");
	          String reg = req.getParameter("region");
	          System.out.println("2");
	          List<Status>  tweets;
	          String chk=cat+reg;
	          int temp=0;
	
	          if(check.contains(chk)){
	        	 temp=1;
	        	 }
	
	          if(temp==0){
	        	  System.out.println("-------------- Fetch from Twitter-----------");
	        	  tweets=gettweet(cat,reg);
	        	  createOrUpdateItem(tweets,cat,reg);
	         	  try{
	        	  com.google.appengine.api.datastore.Query q=new com.google.appengine.api.datastore.Query("tweet");

	        	  Filter catfilter= new FilterPredicate("category",FilterOperator.EQUAL,cat);
	        	  Filter regfilter= new FilterPredicate("region",FilterOperator.EQUAL,reg);
	        	
	        	  Filter comb =CompositeFilterOperator.and(catfilter, regfilter);
	        	  q.setFilter(comb);
	        	  
	        	  PreparedQuery pq = datastore.prepare(q);	  
	        	  String sen="";
	        	  hash.clear();
	        	  for (Entity result : pq.asIterable()) {
	        		  if(!result.getProperty("hash_tag").toString().trim().equalsIgnoreCase("blankhashtag"))
	        		  hash.add(result.getProperty("hash_tag").toString().trim());
	        		  }
	        	  System.out.println("hash:"+hash.toString());
	        		  countfreq(hash);
	        		

		        	  out.println("<html>");
				      out.println("<body>");
				      System.out.println("lst.size: "+lst.size());
				      int size,cnt;
				      if(lst.size()>3)
				    	  size=3;
				      else {
				    	  size=lst.size();
				      }
				      
	        		  for(int i=0;i<=size;i++)
				      {    sen="";
				      tmp.clear();
	        		  for (Entity result : pq.asIterable()) {
		        		
		        		  if(result.getProperty("hash_tag").toString().trim().equalsIgnoreCase((lst.get(i).getKey()))){
			        			 sen=(sen+" "+(String)result.getProperty("sentiment"));	
		        		  }
		        		  }
		        		
	        		  most m=new most();
		        	   List<Entry<String,Integer>> ls=new ArrayList<Entry<String,Integer>>();
		        	  Map<String,Integer> mp=null;
		        		System.out.println("sen: "+sen);
		        	  mp=m.getWordCount(sen);
		        	  System.out.println("mp: "+mp.toString());
		        	  ls=m.sortByValue(mp);
		        	  //out.println("\n");
				      cnt=0;
				      if(i==3)
				      break;
				      System.out.println("ls: "+ls.toString());
				      out.print("<h2>Buzzing Now: "+lst.get(i).getKey().trim()+" -----> Sentiment: "+ls.get(0).getKey().trim()+"</h2> \n\n");
				      out.print("<table border=1>");
				      out.print("<tr><th>User</th><th>Image</th><th>Tweet</th><th>User Name</th></tr>");
				      for (Entity result : pq.asIterable()) {
				    	 
				    	  if(cnt>5)
			        	   {
			        		   break;
			        	   }
		        		   if(result.getProperty("hash_tag").toString().trim().equalsIgnoreCase(lst.get(i).getKey()) && result.getProperty("sentiment").toString().equalsIgnoreCase(ls.get(0).getKey()))
		        			   {
		        			   System.out.println("I: "+i+" cnt: "+cnt);
		        			   if(!tmp.contains(result.getProperty("from_user"))){
		        			   cnt++;
		        			   out.print("<tr>");
		        			   out.print("<td>" +result.getProperty("from_user")+"</td>");
		        			   System.out.print("|| "+result.getProperty("from_user"));
		        			   out.print("<td><image src="+result.getProperty("image")+"></image></td>");
		        			   System.out.print("|| "+result.getProperty("image"));
		        			   System.out.print("|| "+result.getProperty("text"));
		        			   out.print("<td>" + result.getProperty("text")+"</td>");
		        			   System.out.print("|| "+result.getProperty("from_user_name")+"||\n\n");
		        			   out.print("<td>" + result.getProperty("from_user_name")+"</td>");
		        			 
		        			   out.print("</tr>");  
		        			 
		        			   }
			              	  }
		        	   
		        	 }
				      out.print("</table>");
				      }
				      out.println("</body>");
				      out.println("</html>"); 
		        	 }
	        	  catch(Exception e)
	        	  {
	        		  e.printStackTrace();
	        	  }
	        	  if(!check.contains(chk)){
	        	  check.add(chk);}
	        	  System.out.println("---->buzz array"+hash.toString());
	        	  
	          }
	          else if(temp==1){
	        	  System.out.println("-------------- Fetch form datastore-----------");
	
	        	
	        	  String sen="";
	        	 try{
	        	  com.google.appengine.api.datastore.Query q=new com.google.appengine.api.datastore.Query("tweet");
	        	
	        	  Filter catfilter= new FilterPredicate("category",FilterOperator.EQUAL,cat);
	        	  Filter regfilter= new FilterPredicate("region",FilterOperator.EQUAL,reg);
	              Filter comb =CompositeFilterOperator.and(catfilter, regfilter);
	             
	        	  q.setFilter(comb);
	        	  System.out.println("q: "+q.toString());
	        	  PreparedQuery pq = datastore.prepare(q);
	        	  System.out.println("pq: "+pq.toString());
	        	System.out.println("LST: "+lst.toString());
	        	sen="";
	        	 hash.clear();
	        	  for (Entity result : pq.asIterable()) {
	        		  if(!result.getProperty("hash_tag").toString().trim().equalsIgnoreCase("blankhashtag"))
	        		  hash.add(result.getProperty("hash_tag").toString().trim());
	        		  }
	        	  System.out.println("hash:"+hash.toString());
	        		  countfreq(hash);
	        		  System.out.println("lst:"+lst.toString());
	               	  out.println("<html>");
				      out.println("<body>");
				      int size;
				      if(lst.size()>3)
				    	  size=3;
				      else {
				    	  size=lst.size();
				      }
				      
	        		  for(int i=0;i<=size;i++)
				      {
	        			  sen="";
	        			  tmp.clear();
	        	  for (Entity result : pq.asIterable()) {
	        		 
	        	    if(result.getProperty("hash_tag").toString().trim().equalsIgnoreCase((lst.get(i).getKey()))){
	        			 sen=(sen+" "+(String)result.getProperty("sentiment"));	
	        			 }
	        	  }
	        	
	        	  most m=new most();
	        	   List<Entry<String,Integer>> ls=new ArrayList<Entry<String,Integer>>();
	        	  Map<String,Integer> mp=null;
	        		System.out.println("sen: "+sen);
	        	  mp=m.getWordCount(sen);
	        	  System.out.println("mp: "+mp.toString());
	        	  ls=m.sortByValue(mp);
	                  	  int cnt=0;
	                  	  if(i==3)
	                  		  break;
	               
	                  	 out.print("<h2>Buzzing Now: "+lst.get(i).getKey().trim()+" -----> Sentiment: "+ls.get(0).getKey().trim()+"</h2> \n\n");
					      out.print("<table border=1>");
					      out.print("<tr><th>User</th><th>Image</th><th>Tweet</th><th>User Name</th></tr>");
					      for (Entity result : pq.asIterable()) {
					    	  if(cnt>5)
				        	   {
				        		   break;
				        	   }
			        		   if(result.getProperty("hash_tag").toString().trim().equalsIgnoreCase(lst.get(i).getKey()) && result.getProperty("sentiment").toString().equalsIgnoreCase(ls.get(0).getKey()))
			        			   {
			        			   if(!tmp.contains(result.getProperty("from_user"))){
			        				  
			        			   cnt++;
			        			   tmp.add(result.getProperty("from_user"));
			        			   out.print("<tr>");
			        			   out.print("<td>" +result.getProperty("from_user")+"</td>");
			        			   System.out.print("|| "+result.getProperty("from_user"));
			        			   out.print("<td><image src="+result.getProperty("image")+"></image></td>");
			        			   System.out.print("|| "+result.getProperty("image"));
			        			   System.out.print("|| "+result.getProperty("text"));
			        			   out.print("<td>" + result.getProperty("text")+"</td>");
			        			   System.out.print("|| "+result.getProperty("from_user_name")+"||\n\n");
			        			   out.print("<td>" + result.getProperty("from_user_name")+"</td>");
			        			 
			        			   out.print("</tr>"); 
			        			   
				              	  }
			        			
			        			   }
			        	   
			        	 }
					      out.print("</table>");
					      }

			      out.println("</body>");
			      out.println("</html>"); 
	        	 }
	        	 catch(Exception e)
	        	 {
	        		 e.printStackTrace();
	        	 }
	          }
	         
		
		
		
	}
		  catch(Exception ex)
		  {
			
		  }
	} 
	 public  void createOrUpdateItem(List<Status> tweets,String cat,String reg) throws FileNotFoundException {
		 Entity twe=null;
		 System.out.println("--------------In CReate Or Update-------------");
		
		 for(Status tweet : tweets){
			 try{
			
			String t=tweet.getText();
			String sent=sentiment(t);
			String bz="";
			bz=buzzextract(t);
			
			twe=new Entity("tweet",tweet.getId());
	    	twe.setProperty("category",cat);
			twe.setProperty("region",reg);
			twe.setProperty("text", tweet.getText());
			twe.setProperty("from_user",tweet.getUser().getScreenName());
			twe.setProperty("from_user_name",tweet.getUser().getName());
			twe.setProperty("image",tweet.getUser().getProfileImageURL());
			twe.setProperty("created_at",tweet.getCreatedAt());
			twe.setProperty("sentiment",sent);
			if(bz=="---"){
				bz="blankhashtag";
			}
			
			twe.setProperty("hash_tag",bz);
			
			 datastore.put(twe);

			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
		 }
	  }
	 
	 
	 public String buzzextract(String tweet){
	 
	 Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(tweet);
		String buzz="---";
		while (matcher.find()) {
	
			buzz="";
			buzz=buzz+" "+matcher.group(1);
	
		}
		return buzz;	
	 }
	 public void countfreq(ArrayList<String> list){
		
 	forsort="";
		 for(int i=0;i<list.size();i++){
		 forsort=forsort+" "+list.get(i);
	 	}
 	most m=new most();
 	System.out.println("forsort: "+forsort);
 	map=m.getWordCount(forsort);
 	lst.clear();
	lst=m.sortByValue(map);
	
 	 }
	 public String sentiment(String twt){
		 String sent="";
		 Scanner scanner=null;
		 int score=0,sum=0;
		 try{
		 File file = new File("dicts.txt");
		    String[] spl=twt.split("\\s");
			String[] txt=new String[2];	
			for(int i=0;i<spl.length;i++){
				scanner= new Scanner(file);	
			while (scanner.hasNextLine()) {
			    String line = scanner.nextLine();
			    if(line.contains((spl[i]))) {
			    	txt=line.split("\\s");
			    	if(spl[i].equalsIgnoreCase(txt[0]))
			    	{
			    		try{
			        score=Integer.valueOf(txt[1]);}
			    		catch(Exception e)
			    		{
			    			
			    		}
			 
			        sum+=score;
			        continue;
			    	}
			    }
			    
			}
			
			}
			scanner.close();    
			
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 	
			if(sum>0)
			{
				if(sum<=3)
					sent="Possitive";
				
				else
					sent="Very-Possitive";
				
			}
			else if(sum<0){
				if(sum>=-3)
				sent="Negative";
				
				else
					sent="Very-Negative";
				
			}
			else if(sum==0)
				sent="Neutral";
		
			
		return sent;
		
	 }
   	  	
	public List<Status> gettweet(String cat,String reg) throws TwitterException
	{
        Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("BrrP3VBsabHudki1tJQQ", "PJJnS6eCiwWf7cJ1Zt67orwTI8TyUM0We1cBw80jWw");
        twitter.setOAuthAccessToken(new AccessToken("1355786214-vXljuYoRNBlJbuD1AN8Jw5OfeUWaH7MYHAnif5S", "nixPPxzPStp3upmgxV6qV7XB5XcarhAv9RPb7hGc"));
      
        List<Status> tweets=null;
             
        double lat;
        double lon;
        
        String inputLine;
        StringBuilder builder = new StringBuilder();
       
        Query query=null;
        QueryResult result=null;
        try {
             query= new Query(cat);
             String adr[]=reg.split("\\s");
             String a="";
             for(int i=0;i<adr.length;i++){
            	 a=a+adr[i];
             }
             URL yahoo = new URL("http://maps.googleapis.com/maps/api/geocode/json?"+"address="+a+"&sensor=false");
             URLConnection yc = yahoo.openConnection();
             BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
             while ((inputLine = in.readLine()) != null) 
             {              
            	 builder.append(inputLine); 
             }
             in.close();
             JSONObject json = new JSONObject(builder.toString());
             lat = ((JSONArray)json.get("results")).getJSONObject(0)
                     .getJSONObject("geometry").getJSONObject("location")
                     .getDouble("lat");
             lon = ((JSONArray)json.get("results")).getJSONObject(0)
                     .getJSONObject("geometry").getJSONObject("location")
                     .getDouble("lng");
            GeoLocation geo = new GeoLocation(lat,lon);
     		query.setGeoCode(geo, 10.0, Query.KILOMETERS);
     		tweetdata.clear();
            int cnt=0;
           do {
                result = twitter.search(query);
                tweets=result.getTweets();
                query=result.nextQuery();
                tweetdata.addAll(tweets);
                cnt++;
           } while(cnt!=9 &&(query=result.nextQuery())!=null);
           
        }
        catch(Exception e)
        {
        e.printStackTrace();
        e.getCause();
        }
        return tweetdata;
        
	} 
	}
