package scrap

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable
import com.gargoylesoftware.htmlunit.html.HtmlTableRow
import groovy.json.JsonOutput

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
        }
    }

    static def getListOfAircraft() {
        for (String manufacture : manufactures) {
            final HtmlPage html = web.getPage(manufacture)
            final List<HtmlElement> rows = html.getByXPath('//tr[@class]')

            for (HtmlElement row : rows) {
                final HtmlElement aRef = row.getFirstChild().getFirstChild()

                // Get and print the information of aircraft link
                getInformationOfAircraft(aRef.getAttribute('href'))
            }

            Thread.sleep(1100)
        }
    }

    static def getInformationOfAircraft(final String aircraftPage) {

        if (aircraftPage.isEmpty()) return;

        print('Link: ' + aircraftPage + '\n')

        final HtmlPage html = web.getPage(aircraftPage)
        final List<HtmlTable> tables = html.getByXPath('//table[@class]')

        final Aircraft aircraft1 = new Aircraft();

        aircraft1.setName(html.getFirstByXPath('//div[@class="huge_id"]').asText())

        for (HtmlTable table : tables) {
            for (HtmlTableRow row : table.getRows()) {
                final String description = row.getCell(0).getVisibleText();
                String value;

                try {
                    // Invariant: getCell(1) can will thrawn a exception if it not exist.
                    // Produced for problems with the page.
                    value = row.getCell(1).getVisibleText();
                } catch(IndexOutOfBoundsException exception) {
                    System.err.println("The aircraft ${aircraft1.getName()} not have information.");
                    return;
                }

                if (description == 'Manufacturer:') {
                    aircraft1.setManufacture(value)
                } else if (description == 'Model:') {
                    aircraft1.setModel(value)
                } else if (description == 'Year built:') {
                    aircraft1.setYearBuilt(value)
                } else if (description == 'Construction Number (C/N):') {
                    aircraft1.setConstructNumber(value)
                } else if (description == 'Aircraft Type:') {
                    aircraft1.setType(value)
                } else if (description == 'Number of Seats:') {
                    // Invariant, when not exist information about of seats
                    // the value is N/A
                    if (value != 'N/A') {
                        aircraft1.setSeats(value as Integer)
                    } else {
                        // For default, 0 is a value unknown
                        aircraft1.setSeats(0)
                    }
                } else if (description == 'Number of Engines:') {
                    aircraft1.setEngines(value as Integer)
                } else if (description == 'Engine Type:') {
                    aircraft1.setEngineType(value)
                } else if (description == 'Engine Manufacturer and Model:') {
                    aircraft1.setEngineManufacture(value)
                } else if (description == 'Also Registered As:') {
                    aircraft1.setAlsoRegister(value)
                } else if (description == 'Registration Number:') {
                    aircraft1.setRegistrationNumber(value)
                } else if (description == 'Mode S (ICAO24) Code:') {
                    aircraft1.setModelSICAO24Code(value)
                } else if (description == 'Current Status:') {
                    aircraft1.setCurrentStatus(value)
                } else if (description == 'Delivery Date:') {
                    aircraft1.setDeliveryDate(value)
                } else if (description == 'Owner:') {
                    aircraft1.setOwner(value)
                } else if (description == 'Address:') {
                    // The text have a wrong format
                    aircraft1.setAddress(value
                            .replace(',', '')
                            .replace('\n', ''))
                } else {
                    println "Unknown attribute: ${description} \n ${value}"
                }
            }
        }

        generateFilesJSON(aircraft1)

        Thread.sleep(1100)
    }

    static def generateFilesJSON(final Aircraft aircraft) {
        final String json = JsonOutput.prettyPrint(JsonOutput.toJson(aircraft))
        final String filenameOutput = 'output/' + aircraft.getName()
                .replace(' ', '-')
                .replace('/', '-') + ".json"

        final File file = new File(filenameOutput)
        file.write(json)
    }
}

new Main().run();