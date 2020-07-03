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
	public static void main (String[] args) throws IOException
	{
		String base = "https://reporeapers.github.io/results/";
		String address;
		List<String[]> reaperData = new ArrayList<>();
		
		for (int pageNum = 1; pageNum < 4497; pageNum++)
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
		    	Element row = rows.get(i);
		    	Elements cols = row.select("td");
		    	
		    	// only want the Java projects, alter this if want more than Java
		    	if(cols.get(2).text().equals("Java"))
		    	{
		    		String link = ""; 
		    		//convert the unhelpful "web" hyperlink to a meaningful string
		    		Elements links = cols.get(1).select("a[href]");
		    		for (Element element : links)
		    		{
		    			//System.out.println("link = " + element.attr("href") + " element = " + element.text());
		    			link = element.attr("href");	//only want the second one anyway so overwrites
		    		}
		    		
		    		//System.out.println(cols.get(0).text() + " " + cols.get(1).text() + " " + cols.get(2).text());
		    		String[] repo = {cols.get(0).text(), link, cols.get(2).text()};
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
	  		
	  		String[] headerTxt = {"Repository", "Github Link", "Language"};
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
	}  
}
