package scrap

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage

class Main {
    private final static WebClient web = new WebClient();

    private final static ArrayList<String> pages = new ArrayList<>()
    private final static ArrayList<String> aircraft = new ArrayList<>()
    private final static ArrayList<String> manufactures = new ArrayList<>()

    static def run() {
        web.getOptions().setCssEnabled(false);
        web.getOptions().setJavaScriptEnabled(false);

        getListOfPages()
        getListOfManufactures()
        getListOfAircraft()
    }

    static def getListOfPages() {
        final HtmlPage page = web.getPage("https://www.airport-data.com/manuf/A.html");
        final HtmlDivision buttonGroup = page.getFirstByXPath("//div[@class='btn-group']");

        final List<HtmlElement> divs = buttonGroup.getByXPath("a[@href]")

        for (HtmlElement element : divs) {
            pages.add(element.getAttribute('href'))
        }
    }

    static def getListOfManufactures() {
        for (String page : pages) {
            final HtmlPage html = web.getPage(page)
            final List<HtmlElement> paragraphs = html.getByXPath('//p[@class="bottom-margin-5"]')

            for (HtmlElement paragraph : paragraphs) {
                manufactures.add(paragraph.getFirstChild().getAttribute('href'))
            }

            // For avoid the block for the host
            Thread.sleep(1100)

            // Debug
            break
        }
    }

    static def getListOfAircraft() {
        for (String manufacture : manufactures) {
            final HtmlPage html = web.getPage(manufacture)
            final List<HtmlElement> rows = html.getByXPath('//tr[@class]')

            for (HtmlElement row : rows) {
                final HtmlElement aRef = row.getFirstChild().getFirstChild()
                aircraft.add(aRef.getAttribute('href'))
            }

            Thread.sleep(1100)

            break
        }
    }
}

new Main().run();