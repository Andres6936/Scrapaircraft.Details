package scrap

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "Aircraft")
class Aircraft {

    @DatabaseField(id = true)
    String name

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String type

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String model

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String owner

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String address

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String yearBuilt

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String engineType

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String manufacture

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String alsoRegister

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String deliveryDate

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String currentStatus

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String constructNumber

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String modelSICAO24Code

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String engineManufacture

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING)
    String registrationNumber

    /**
     * For default, an value of 0 is an value unknown
     */
    @DatabaseField(canBeNull = true)
    Integer seats

    @DatabaseField(canBeNull = true)
    Integer engines
}
