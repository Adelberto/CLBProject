package clb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import clb.beans.enums.AnalysisTypes;
import clb.beans.enums.Hours;
import clb.beans.enums.Months;
import clb.beans.enums.ScaleGraphic;
import clb.beans.pojos.QuickAnalysis;
import clb.business.AnalyzerDataService;
import clb.business.objects.AnalyzerObject;
import clb.business.objects.AnalyzerRegistryObject;
import clb.business.objects.BuildingObject;
import clb.business.objects.DataLoggerObject;
import clb.global.DateUtils;

@ViewScoped
@ManagedBean
public class AnalysisBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{clbHomeLoginBean}")
	private ClbHomeLoginBean clbHomeLoginBean;

	@ManagedProperty("#{analyzerDataService}")
	private AnalyzerDataService analyzerDataService;

	private Date todayDate;
	private Date minDate;
	private Date analysisDate;
	private String analysisDatePrettyFormat;
	private QuickAnalysis analysisDayPojo;

	private List<BuildingObject> buildingsToSelect;
	private BuildingObject tempBuildingSelected;
	private BuildingObject buildingSelected;

	private List<AnalyzerObject> analyzersSelected;
	private AnalyzerObject tempAnalyzerSelected;
	private AnalyzerObject analyzerSelected;

	private AnalysisTypes analysisType;
	private AnalysisTypes[] analysisTypes;

	private ScaleGraphic scaleGraphic;
	private ScaleGraphic[] scalesGraphic;

	private Hours hour;
	private Hours[] hoursValues;

	private Months month;
	private Months[] monthsValues;

	private String year;
	private String[] years;

	private String week;
	private String[] weeks;

	@PostConstruct
	public void init() {
		todayDate = new Date();
		analysisDate = new Date();
		analysisDatePrettyFormat = DateUtils.getInstance().prettyFormat(analysisDate);
		minDate = analyzerDataService.getLowestAnalyzerRegistryDate();

		analysisTypes = AnalysisTypes.values();
		scalesGraphic = ScaleGraphic.values();
		scaleGraphic = ScaleGraphic.DAY;
		hoursValues = Hours.getHoursLimited(DateUtils.getInstance().getHourFromDate(todayDate));
		hour = Hours.getHourByValue(DateUtils.getInstance().getHourFromDate(todayDate));

		monthsValues = Months.getMonthsLimited(DateUtils.getInstance().getMonthFromDate(todayDate));
		month = Months.getMonthByValue(DateUtils.getInstance().getMonthFromDate(todayDate));

		year = "" + DateUtils.getInstance().getYearFromDate(todayDate);
		years = analyzerDataService.getYearsAvailable();

		week = "" + DateUtils.getInstance().getWeekFromDate(todayDate);
		weeks = generateWeeksFromMonth(todayDate);

		//Set Initial Selected Building, DataLogger and Analyzer
		if(clbHomeLoginBean.getUserLoginPojo().getCurrentUser().getBuildings() != null && 
				clbHomeLoginBean.getUserLoginPojo().getCurrentUser().getBuildings().size() > 0 ) {

			buildingsToSelect = clbHomeLoginBean.getUserLoginPojo().getCurrentUser().getBuildings();

			boolean firstTime = false;

			for(BuildingObject bObj: clbHomeLoginBean.getUserLoginPojo().getCurrentUser().getBuildings()) {
				if(bObj.getBuildingMeters() != null && bObj.getBuildingMeters().size() > 0 && bObj.getDataLoggers() != null) {
					for(DataLoggerObject dlObj: bObj.getDataLoggers()) {
						if(dlObj.getAnalyzers() != null) {
							for(AnalyzerObject aObj : dlObj.getAnalyzers()) {
								if(!firstTime) {
									buildingSelected = bObj;
									tempBuildingSelected = buildingSelected;
									analyzersSelected = dlObj.getAnalyzers(); 
									analyzerSelected = aObj;
									tempAnalyzerSelected = analyzerSelected;

									analysisDayPojo = new QuickAnalysis( buildingSelected.getBuildingMeters());

									analysisDayPojo.fillGraphicForData( 
											analyzerDataService.getDayRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate), scaleGraphic );

									firstTime = true;
								}
							}
						}
					}
				}
			}
		}
	}

	private String[] generateWeeksFromMonth(Date todayDate2) {
		int nrOfWeeks = DateUtils.getInstance().getNumberOfWeeksFromDate(todayDate);

		String[] weeksList = new String[nrOfWeeks];

		for(int i=0;i<nrOfWeeks;i++) {
			weeksList[i] = "" + (i+1);
		}

		return weeksList;
	}

	public void selectBuilding() {
		buildingSelected = tempBuildingSelected;
	}

	public void selectAnalyzer() {
		analyzerSelected = tempAnalyzerSelected;
	}

	public void analysisCalendarSelect(SelectEvent event) {
		updateScaleValues();
	}

	public void updateWeekValue() {
		analysisDate = DateUtils.getInstance().setWeekOfDate(this.analysisDate,Integer.parseInt(week));
		List<AnalyzerRegistryObject> registries = analyzerDataService.getWeekRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
		analysisDayPojo.fillGraphicForData( registries, scaleGraphic );
	}

	public void updateMonthValue() {
		
		week = "1";
		
		analysisDate = DateUtils.getInstance().setMonthOfDate(this.analysisDate,month.getValue());
		analysisDate = DateUtils.getInstance().getMonthFirstDayReseted(analysisDate);
		List<AnalyzerRegistryObject> registries = null;

		switch(scaleGraphic) {
		case MONTH:
			registries = analyzerDataService.getMonthRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		case WEEK:
			//If First Day Of Month is Sunday the Calendar just take into account as monday
			analysisDate = DateUtils.getInstance().updateIfIsSundayForMonth(analysisDate);
			registries = analyzerDataService.getWeekRegistriesFromAnalyzer(analyzerSelected.getId(), analysisDate);
			break;
		default:;
		}

		analysisDayPojo.fillGraphicForData( registries, scaleGraphic );
	}

	public void updateYearValue() {

		analysisDate = DateUtils.getInstance().setYearOfDate(this.analysisDate,Integer.parseInt(year));
		week = "1";

		//If year Date is bigger then is allowed
		if(DateUtils.getInstance().isSameYearOfCurrent(analysisDate)) {
			analysisDate = todayDate;
			int monthDate = DateUtils.getInstance().getMonthFromDate(analysisDate);
			monthsValues = Months.getMonthsLimited(monthDate);
			month = Months.getMonthByValue(monthDate);
		}
		else {
			int monthDate = DateUtils.getInstance().getMonthFromDate(analysisDate);
			monthsValues = Months.values();
			month = Months.getMonthByValue(monthDate);
		}

		List<AnalyzerRegistryObject> registries = null;

		switch(scaleGraphic) {
		case MONTH:
			registries = analyzerDataService.getMonthRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		case WEEK:
			registries = analyzerDataService.getWeekRegistriesFromAnalyzer(analyzerSelected.getId(), analysisDate);
			break;
		default:;
		}

		analysisDayPojo.fillGraphicForData( registries, scaleGraphic );
	}

	public void updateScaleValues() {

		List<AnalyzerRegistryObject> registries = new ArrayList<AnalyzerRegistryObject>();

		switch(scaleGraphic) {
		case HOUR:
			updateHoursCombo();
			analysisDate = DateUtils.getInstance().setHourForDate(analysisDate,hour.getValue());
			registries = analyzerDataService.getHourRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		case DAY:
			registries = analyzerDataService.getDayRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		case WEEK:
			registries = analyzerDataService.getWeekRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		case MONTH:
			month = Months.getMonthByValue(DateUtils.getInstance().getMonthFromDate(analysisDate));
			registries = analyzerDataService.getMonthRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		default: 
			registries = analyzerDataService.getDayRegistriesFromAnalyzer( analyzerSelected.getId(), analysisDate);
			break;
		}

		analysisDayPojo.fillGraphicForData( registries, scaleGraphic );
	}

	private void updateHoursCombo() {
		if(DateUtils.getInstance().isToday(analysisDate)) {
			hoursValues = Hours.getHoursLimited(DateUtils.getInstance().getHourFromDate(new Date()));
		}
		else hoursValues = Hours.values();
	}

	public void updateMeterSelection() {
		analysisDayPojo.changeSerie(scaleGraphic);
	}

	public Date getAnalysisDate() {
		return analysisDate;
	}

	public void setAnalysisDate( Date analysisDate ) {
		this.analysisDate = analysisDate;
		this.setAnalysisDatePrettyFormat(DateUtils.getInstance().prettyFormat(analysisDate));
	}

	public QuickAnalysis getAnalysisDayPojo() {
		return analysisDayPojo;
	}

	public void setAnalysisDayPojo( QuickAnalysis analysisDayPojo ) {
		this.analysisDayPojo = analysisDayPojo;
	}

	public AnalysisTypes getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType( AnalysisTypes analysisType ) {
		this.analysisType = analysisType;
	}

	public AnalysisTypes[] getAnalysisTypes() {
		return analysisTypes;
	}

	public void setAnalysisTypes( AnalysisTypes[] analysisTypes ) {
		this.analysisTypes = analysisTypes;
	}

	public ScaleGraphic[] getScalesGraphic() {
		return scalesGraphic;
	}

	public void setScalesGraphic( ScaleGraphic[] scalesGraphic ) {
		this.scalesGraphic = scalesGraphic;
	}

	public ScaleGraphic getScaleGraphic() {
		return scaleGraphic;
	}

	public void setScaleGraphic( ScaleGraphic scaleGraphic ) {
		this.scaleGraphic = scaleGraphic;
	}

	public BuildingObject getBuildingSelected() {
		return buildingSelected;
	}

	public void setBuildingSelected( BuildingObject buildingSelected ) {
		this.buildingSelected = buildingSelected;
	}

	public ClbHomeLoginBean getClbHomeLoginBean() {
		return clbHomeLoginBean;
	}

	public void setClbHomeLoginBean( ClbHomeLoginBean clbHomeLoginBean ) {
		this.clbHomeLoginBean = clbHomeLoginBean;
	}

	public AnalyzerDataService getAnalyzerDataService() {
		return analyzerDataService;
	}

	public void setAnalyzerDataService( AnalyzerDataService analyzerDataService ) {
		this.analyzerDataService = analyzerDataService;
	}

	public AnalyzerObject getAnalyzerSelected() {
		return analyzerSelected;
	}

	public void setAnalyzerSelected( AnalyzerObject analyzerSelected ) {
		this.analyzerSelected = analyzerSelected;
	}

	public BuildingObject getTempBuildingSelected() {
		return tempBuildingSelected;
	}

	public void setTempBuildingSelected( BuildingObject tempBuildingSelected ) {
		this.tempBuildingSelected = tempBuildingSelected;
	}

	public AnalyzerObject getTempAnalyzerSelected() {
		return tempAnalyzerSelected;
	}

	public void setTempAnalyzerSelected( AnalyzerObject tempAnalyzerSelected ) {
		this.tempAnalyzerSelected = tempAnalyzerSelected;
	}

	public List<BuildingObject> getBuildingsToSelect() {
		return buildingsToSelect;
	}

	public void setBuildingsToSelect( List<BuildingObject> buildingsToSelect ) {
		this.buildingsToSelect = buildingsToSelect;
	}

	public List<AnalyzerObject> getAnalyzersSelected() {
		return analyzersSelected;
	}

	public void setAnalyzersSelected( List<AnalyzerObject> analyzersSelected ) {
		this.analyzersSelected = analyzersSelected;
	}

	public Hours getHour() {
		return hour;
	}

	public void setHour(Hours hour) {
		this.hour = hour;
	}

	public Hours[] getHoursValues() {
		return hoursValues;
	}

	public void setHoursValues(Hours[] hoursValues) {
		this.hoursValues = hoursValues;
	}

	public Months getMonth() {
		return month;
	}

	public void setMonth(Months month) {
		this.month = month;
	}

	public Months[] getMonthsValues() {
		return monthsValues;
	}

	public void setMonthsValues(Months[] monthsValues) {
		this.monthsValues = monthsValues;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public String getAnalysisDatePrettyFormat() {
		return analysisDatePrettyFormat;
	}

	public void setAnalysisDatePrettyFormat(String analysisDatePrettyFormat) {
		this.analysisDatePrettyFormat = analysisDatePrettyFormat;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String[] getYears() {
		return years;
	}

	public void setYears(String[] years) {
		this.years = years;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String[] getWeeks() {
		return weeks;
	}

	public void setWeeks(String[] weeks) {
		this.weeks = weeks;
	}


}
