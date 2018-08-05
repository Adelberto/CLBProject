package clb.business.objects;

import java.util.ArrayList;
import java.util.List;

import clb.database.entities.AnalyzerEntity;

public class AnalyzerObject implements ClbObject
{
    private String id;

	private String name;

	private List<String> analyzerRegistriesIds;

	public AnalyzerObject(){

	}

	public AnalyzerObject(AnalyzerEntity analyzerEntity){
		this.id = analyzerEntity.getId();
		this.name = analyzerEntity.getName();
		this.analyzerRegistriesIds = analyzerEntity.getAnalyzerRegistriesIds();
	}

	public AnalyzerEntity toEntity(){
		AnalyzerEntity analyzerEntity = new AnalyzerEntity();
		
		if(this.id != null) {
			analyzerEntity.setId( this.id );
		}
		
		analyzerEntity.setName( this.name );
		analyzerEntity.setAnalyzerRegistriesIds(this.analyzerRegistriesIds);
		return analyzerEntity;
	}
	
    public void addAnalyzerRegistry(String analyzerRegId) {
        if(analyzerRegistriesIds == null) {
            analyzerRegistriesIds = new ArrayList<String>();
        }
        
        analyzerRegistriesIds.add(analyzerRegId);
    }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

    public List<String> getAnalyzerRegistriesIds() {
        return analyzerRegistriesIds;
    }

    public void setAnalyzerRegistriesIds( List<String> analyzerRegistriesIds ) {
        this.analyzerRegistriesIds = analyzerRegistriesIds;
    }
	
}
