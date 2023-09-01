package scrap

import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.table.TableUtils
import groovy.json.JsonSlurper

def connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:Aircraft.db")
TableUtils.createTableIfNotExists(connectionSource, Aircraft.class)

def reader = new JsonSlurper()
for (final def file in new File("./output").listFiles()) {
    def payload = reader.parse(file)
    def repository = DaoManager.createDao(connectionSource, Aircraft.class)
    def aircraft = new Aircraft(
            name: payload['name'],
            type: payload['type'],
            model: payload['model'],
            owner: payload['owner'],
            address: payload['address'],
            yearBuilt: payload['yearBuilt'],
            engineType: payload['engineType'],
            manufacture: payload['manufacture'],
            alsoRegister: payload['alsoRegister'],
            deliveryDate: payload['deliveryDate'],
            currentStatus: payload['currentStatus'],
            constructNumber: payload['constructNumber'],
            modelSICAO24Code: payload['modelSICAO24Code'],
            engineManufacture: payload['engineManufacture'],
            registrationNumber: payload['registrationNumber'],
            seats: payload['seats'],
            engines: payload['engines'],
    )
    repository.create(aircraft)
}

connectionSource.close()
