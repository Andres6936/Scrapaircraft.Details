package scrap

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable
import com.gargoylesoftware.htmlunit.html.HtmlTableCell
import com.gargoylesoftware.htmlunit.html.HtmlTableRow
import groovy.json.JsonOutput

class Main {
    private final static WebClient web = new WebClient();

    private final static ArrayList<String> pages = new ArrayList<>()
    private final static ArrayList<String> aircraft = new ArrayList<>()
    private final static ArrayList<String> manufactures = new ArrayList<>()

    private final static ArrayList<Aircraft> infoAircraft = new ArrayList<>()

    static def run() {
        web.getOptions().setCssEnabled(false);
        web.getOptions().setJavaScriptEnabled(false);

        getListOfPages()
        getListOfManufactures()
        getListOfAircraft()

        getInformationOfAircraft()

        generateFilesJSON()
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

            // Debug
            break
        }
    }

    static def getInformationOfAircraft() {
        for (String aircraftPage : aircraft) {
            final HtmlPage html = web.getPage(aircraftPage)
            final List<HtmlTable> tables = html.getByXPath('//table[@class]')

            final Aircraft aircraft1 = new Aircraft();

            aircraft1.setName(html.getFirstByXPath('//div[@class="huge_id"]').asText())

            for (HtmlTable table : tables) {
                for (HtmlTableRow row : table.getRows()) {
                    final String description = row.getCell(0).getVisibleText();
                    final String value = row.getCell(1).getVisibleText();

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
                    } else if (description == 'Current Status:') {
                        aircraft1.setCurrentStatus(value)
                    } else if (description == 'Address:') {
                        aircraft1.setAddress(value)
                    } else {
                        println "Unknown attribute: ${description} \n ${value}"
                    }
                }
            }

            infoAircraft.add(aircraft1)

            Thread.sleep(1100)

            // Debug
            break
        }
    }

    static def generateFilesJSON() {
        for (Aircraft aircraft1 : infoAircraft) {
            final String json = JsonOutput.prettyPrint(JsonOutput.toJson(aircraft1))
            final String filenameOutput = 'output/' + aircraft1.getName()
                    .replace(' ', '-')
                    .replace('/', '-') + ".json"

            final File file = new File(filenameOutput)
            file.write(json)

            // Debug
            break
        }
    }
}

new Main().run();