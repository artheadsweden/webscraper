import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class MainScraper {
    public static void main(String[] args) throws IOException {
        // https://newyork.craigslist.org/search/sss?sort=rel&query=iphone
        String baseUrl = "https://newyork.craigslist.org/";
        String searchUrl = "search/sss?sort=rel&query=";
        String searchQuery = "iphone";

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        String url = baseUrl + searchUrl + searchQuery;
        HtmlPage page = client.getPage(url);

        List<HtmlElement> items = page.getByXPath("//li[@class='result-row']");
        if(items.isEmpty()) {
            System.out.println("No items found");
        }
        else {
            for(HtmlElement htmlItem : items) {
                HtmlAnchor itemAnchor = htmlItem.getFirstByXPath(".//p[@class='result-info']/a");
                HtmlElement spanPrice = htmlItem.getFirstByXPath(".//a/span[@class='result-price']");

                String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();
                Item item = new Item();
                item.setTitle(itemAnchor.asText());
                item.setUrl(itemAnchor.getHrefAttribute());
                item.setPrice(Double.parseDouble(itemPrice.replace("$", "")));

                ObjectMapper mapper = new ObjectMapper();
                String itemJson = mapper.writeValueAsString(item);
                System.out.println(itemJson);
            }
        }

    }
}
