

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
	 
	public class most1 {
	     
	    public Map<String, Integer> getWordCount(String line){
	        Map<String, Integer> wordMap = new HashMap<String, Integer>();
	        try {
	                StringTokenizer st = new StringTokenizer(line, " ");
	                while(st.hasMoreTokens()){
	                    String tmp = st.nextToken().toLowerCase();
	                    if(wordMap.containsKey(tmp)){
	                        wordMap.put(tmp, wordMap.get(tmp)+1);
	                    } else {
	                        wordMap.put(tmp, 1);
	                    }
	                }
	          
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        return wordMap;
	    }
	    
	    
	     
	    public List<Entry<String, Integer>> sortByValue(Map<String, Integer> wordMap){
	         
	        Set<Entry<String, Integer>> set = wordMap.entrySet();
	        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
	        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
	        {
	            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );
	        return list;
	    }
	     
	    public static void main(String a[]){
	 		String s="hello world #helpmebuzz why you ";
			String yourString = "hi #how are #you";
			Map<String,Integer> m=null;
				most mst=new most();
			List<Entry<String,Integer>> lst=null;
			m=mst.getWordCount(s+" "+yourString);
			lst=mst.sortByValue(m);
			System.out.println(lst.toString());
			
	    	}
	    	
	        }
	    
	
