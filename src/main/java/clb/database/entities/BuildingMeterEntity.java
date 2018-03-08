package clb.database.entities;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The persistent class for the BUILDING database table.
 * 
 */

@Document(collection="BuildingsMeters")
public class BuildingMeterEntity implements ClbEntity, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String buildingMeterId;

	private String name;

	@DBRef
	private List<BuildingMeterParameterEntity> buildingMeterParameters;

	public BuildingMeterEntity() {
	}


    public String getBuildingMeterId() {
        return buildingMeterId;
    }


    public void setBuildingMeterId( String buildingMeterId ) {
        this.buildingMeterId = buildingMeterId;
    }


    public String getName() {
        return name;
    }


    public void setName( String name ) {
        this.name = name;
    }


    public List<BuildingMeterParameterEntity> getBuildingMeterParameters() {
        return buildingMeterParameters;
    }


    public void setBuildingMeterParameters( List<BuildingMeterParameterEntity> buildingMeterParameters ) {
        this.buildingMeterParameters = buildingMeterParameters;
    }
	
	
}