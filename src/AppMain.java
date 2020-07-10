import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
//import org.jsoup.helper.HttpConnection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppMain
{
	// function to remove formatting from string numbers
	public static String convert(String str)
	{
		StringBuffer sb = new StringBuffer(str);
		for(int c = 0; c < sb.length(); c++)
		{
			if(sb.charAt(c) < 48 || sb.charAt(c) > 57)
			{
				sb.deleteCharAt(c);
				c--;
			}
		}
		return sb.toString();
	}
	
	// this code will selectively parse projects based on langauge, size, and committers
	public static void main (String[] args) throws IOException
	{
		long numTotalProjects = 0;
		long numJavaProjects = 0;
		long numNontrivialProjects = 0;
		long numMultiCommitterProjects = 0;
		String base = "https://reporeapers.github.io/results/";
		String address;
		List<String[]> reaperData = new ArrayList<>();
		
		// 4496 total html pages of results from Repo Reaper
		// only 3707 pages have size data
		for (int pageNum = 1; pageNum < 3708; pageNum++)
		{
			address = base + Integer.toString(pageNum) + ".html";
			//URL url = new URL(address);
			System.out.println(address);
			// first scrape website URL for 6 seconds, then find the table and get its rows
		    //Document doc = Jsoup.parse(url, 6000);
			Response execute = Jsoup.connect(address).execute();
			Document doc = Jsoup.parse(execute.body());
			
			Element table = doc.select("table").get(0);
		    Elements rows = table.select("tr");
		    
		    //loops through the rows of the table, skipping the labels (two rows of labels)
		    for(int i = 2; i < rows.size(); i++)
		    {
		    	numTotalProjects++;
		    	Element row = rows.get(i);
		    	Elements cols = row.select("td");
		    	
		    	// only want the Java projects, alter this if want more than Java
		    	if(cols.get(2).text().equals("Java"))
		    	{
		    		numJavaProjects++;
		    		String formattedLoc = cols.get(10).text();
		    		String newLoc = convert(formattedLoc);		
		    		long loc = Long.valueOf(newLoc);
		    		
		    		// selects only projects >= 5,000 LOC
		    		if(loc >= 5000)
		    		{
		    			numNontrivialProjects++;
		    			int communitySize = Integer.valueOf(cols.get(4).text());
		    			
		    			// selects only projects with > 1 committer
		    			if(communitySize > 1)
		    			{
		    				numMultiCommitterProjects++;
		    				String link = ""; 
				    		//convert the unhelpful "web" hyperlink to a meaningful string
				    		Elements links = cols.get(1).select("a[href]");
				    		for (Element element : links)
				    		{
				    			//System.out.println("link = " + element.attr("href") + " element = " + element.text());
				    			link = element.attr("href");	//only want the second one anyway so overwrites
				    			link = link + ".git";
				    		}
				    		
				    		//System.out.println(cols.get(0).text() + " " + cols.get(1).text() + " " + cols.get(2).text());
				    		//String[] repo = {cols.get(0).text(), link, cols.get(2).text()};
				    		String[] repo = {link, cols.get(0).text(), cols.get(2).text(), cols.get(4).text(), cols.get(10).text()};
				    		reaperData.add(repo);
		    			}
		    		}
		    	}
		    }
		}
		
		// prints to CSV
		File file = new File("ExprInput.csv");
	  	try 
	    {
	  		//CSVWriter writer = new CSVWriter(new FileWriter(file, true));	//the true means append
	  		CSVWriter writer = new CSVWriter(new FileWriter(file));
	  		
	  		String[] headerTxt = {"Github Link", "Repository", "Language", "Community", "Size"};
	  		writer.writeNext(headerTxt);
	  		
	  		for(String[] newRepo: reaperData)
	  		{
	  			writer.writeNext(newRepo);
	  		}
	  		
	  		writer.close();	
	    }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	  	
	  	//prints captured project metrics to console
	  	System.out.println("Processing Completed!");
	  	System.out.println("Total Projects: " + numTotalProjects);
	  	System.out.println("Total Java Projects: " + numJavaProjects);
	  	System.out.println("Total Java Projects >= 5,000 LOC: " + numNontrivialProjects);
	  	System.out.println("Total Java Projects >= 5,000 LOC && Community > 1: " + numMultiCommitterProjects);
	  	System.out.println("Total Java Projects < 5,000 LOC:  " + (numJavaProjects - numNontrivialProjects));
	}  
	
	// this code just grabs all java projects
	/*
	public static void main (String[] args) throws IOException
	{
		long numTotalProjects = 0;
		long numJavaProjects = 0;
		String base = "https://reporeapers.github.io/results/";
		String address;
		List<String[]> reaperData = new ArrayList<>();
		
		// 4496 total html pages of results from Repo Reaper
		// only 3707 pages have size data
		for (int pageNum = 1; pageNum < 3708; pageNum++)
		{
			address = base + Integer.toString(pageNum) + ".html";
			//URL url = new URL(address);
			System.out.println(address);
			// first scrape website URL for 6 seconds, then find the table and get its rows
		    //Document doc = Jsoup.parse(url, 6000);
			Response execute = Jsoup.connect(address).execute();
			Document doc = Jsoup.parse(execute.body());
			
			Element table = doc.select("table").get(0);
		    Elements rows = table.select("tr");
		    
		    //loops through the rows of the table, skipping the labels (two rows of labels)
		    for(int i = 2; i < rows.size(); i++)
		    {
		    	numTotalProjects++;
		    	Element row = rows.get(i);
		    	Elements cols = row.select("td");
		    	
		    	// only want the Java projects, alter this if want more than Java
		    	if(cols.get(2).text().equals("Java"))
		    	{
		    		numJavaProjects++;
		    		//String formattedLoc = cols.get(10).text();
	    			//int communitySize = Integer.valueOf(cols.get(4).text());
		    		
    				String link = ""; 
		    		//convert the unhelpful "web" hyperlink to a meaningful string
		    		Elements links = cols.get(1).select("a[href]");
		    		for (Element element : links)
		    		{
		    			//System.out.println("link = " + element.attr("href") + " element = " + element.text());
		    			link = element.attr("href");	//only want the second one anyway so overwrites
		    			link = link + ".git";
		    		}
		    		
		    		//System.out.println(cols.get(0).text() + " " + cols.get(1).text() + " " + cols.get(2).text());
		    		//String[] repo = {cols.get(0).text(), link, cols.get(2).text()};
		    		String[] repo = {link, cols.get(0).text(), cols.get(2).text(), cols.get(4).text(), cols.get(10).text()};
		    		reaperData.add(repo);
		    	}
		    }
		}
		
		// prints to CSV
		File file = new File("ExprInput.csv");
	  	try 
	    {
	  		//CSVWriter writer = new CSVWriter(new FileWriter(file, true));	//the true means append
	  		CSVWriter writer = new CSVWriter(new FileWriter(file));
	  		
	  		String[] headerTxt = {"Github Link", "Repository", "Language", "Community", "Size"};
	  		writer.writeNext(headerTxt);
	  		
	  		for(String[] newRepo: reaperData)
	  		{
	  			writer.writeNext(newRepo);
	  		}
	  		
	  		writer.close();	
	    }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	  	
	  	//prints captured project metrics to console
	  	System.out.println("Processing Completed!");
	  	System.out.println("Total Projects: " + numTotalProjects);
	  	System.out.println("Total Java Projects: " + numJavaProjects);
	} 
	*/
}
