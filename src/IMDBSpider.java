import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IMDBSpider {

  public IMDBSpider() {
  }
  

  /**
   * For each title in file movieListJSON:
   *
   * <pre>
   * You should:
   * - First, read a list of 500 movie titles from the JSON file in 'movieListJSON'.
   *
   * - Secondly, for each movie title, perform a web search on IMDB and retrieve
   * movie’s URL: http://akas.imdb.com/find?q=<MOVIE>&s=tt&ttype=ft
   *
   * - Thirdly, for each movie, extract metadata (actors, budget, description)
   * from movie’s URL and store to a JSON file in directory 'outputDir':
   *    http://www.imdb.com/title/tt0499549/?ref_=fn_al_tt_1 for Avatar - store
   * </pre>
   *
   * @param inputFileName
   *          JSON file containing movie titles
   * @param outputDir
   *          output directory for JSON files with metadata of movies.
   * @throws IOException
   */
  public void fetchIMDBMovies(String movieListJSON, String outputDir)
      throws IOException {
	  
	  JsonArray movieList;
	  
	  try(InputStreamReader stream = new InputStreamReader(new FileInputStream(new File(movieListJSON)),"UTF-8")){
		  try(JsonReader reader = Json.createReader(stream)){
		  
		  movieList = reader.readArray();
		  }
	  }
	  
	  for(int i = 0; i < movieList.size();i++){
		  JsonString name = movieList.getJsonObject(i).getJsonString("movie_name");
		  System.out.println(name);  //wie kriegt man die leerzeichen?
	  }
	  
	  
	  
	  String title = "Fantasia+2000+(IMAX)";

	  
	  Document searchResults = Jsoup.connect("http://akas.imdb.com/find?q="+title+"&s=tt&ttype=ft").get();
//	  System.out.println(searchResults.toString());
	  
	  Element resultCell = searchResults.select(".findList").select("td.result_text").first();
	  String link = resultCell.select("a").first().attr("abs:href");
	  System.out.println(link);
	  
	  
	  Document movieSite = Jsoup.connect(link).get(); //open movie site via extracted direct link
//	  System.out.println(movieSite.toString()); //test
	  
	  //TITLE, YEAR
	  String fullTitle = movieSite.title();
	  String _title = fullTitle.substring(0, fullTitle.length()-14);
	  String _year = fullTitle.substring(fullTitle.length()-13, fullTitle.length()-7);
	  System.out.println("x"+_title+"x"); //test
	  System.out.println("x"+_year+"x"); //test
	  
	  //GENRE_LIST
	  Elements genres = movieSite.select("div[itemprop='genre']").select("a"); //select links with genres
	  List<String> _genres = new ArrayList<String>();
	  for(Element el: genres){
		  _genres.add(el.text()); //extract link/genre names
	  }
	  System.out.println(_genres.toString()); //test
	  
	  //COUNTRY_LIST
	  Elements countries = movieSite.select("div.txt-block:contains(Country:)").select("a"); //select links with genres
	  List<String> _countries = new ArrayList<String>();
	  for(Element el: countries){
		  _countries.add(el.text()); //extract link/country names
	  }
	  System.out.println(_countries.toString()); //test
	  
	  //DESCRIPTION
	  String description = movieSite.select("div[itemprop=description]").first().text(); 
	  System.out.println(description.toString()); //test
	  
	  //BUDGET
	  

	  
	  JsonObject mov;
	  
	  mov = Json.createObjectBuilder().add("title", title).build();
	  
	  
	  
	  
  }

  /**
   * Helper method to remove html and formating from text.
   *
   * @param text
   *          The text to be cleaned
   * @return clean text
   */
  protected static String cleanText(String text) {
    return text.replaceAll("\\<.*?>", "").replace("&nbsp;", " ")
        .replace("\n", " ").replaceAll("\\s+", " ").trim();
  }

  public static void main(String argv[]) throws IOException {
    String moviesPath = "./data/movies.json";
    String outputDir = "./data";

    if (argv.length == 2) {
      moviesPath = argv[0];
      outputDir = argv[1];
    } else if (argv.length != 0) {
      System.out.println("Call with: IMDBSpider.jar <moviesPath> <outputDir>");
      System.exit(0);
    }

    IMDBSpider sp = new IMDBSpider();
    sp.fetchIMDBMovies(moviesPath, outputDir);
  }
}
