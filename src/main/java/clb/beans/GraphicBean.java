package clb.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import clb.beans.enums.ScaleGraphic;
import clb.business.AnalyzerDataService;

@ViewScoped
@ManagedBean
public class GraphicBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{analyzerDataService}")
	private AnalyzerDataService analyzerDataService;

	private GraphicPojo graphicDayPojo;
	private GraphicPojo graphicMonthPojo;
	private GraphicPojo graphicYearPojo;

	private boolean aL1Check;
	private boolean aL2Check;
	private boolean aL3Check;

	private ScaleGraphic scaleSelected;

	private ScaleGraphic[] values;

	private Date dayDate;

	private List<SelectItem> hours;
	private String hourSelected;

	private List<SelectItem> months;
	private String monthSelected;

	private List<Integer> years;
	private String yearSelected;

	private boolean enableDayGraphic;
	private boolean enableDayHoursGraphic;
	private boolean enableYearGraphic;
	private boolean enableMonthGraphic;

	@PostConstruct
	public void init(){

		try{
			dayDate = new Date();
			scaleSelected = ScaleGraphic.DAY;

			graphicDayPojo = new GraphicPojo(analyzerDataService.getDataByDay(dayDate),scaleSelected);
			//graphicMonthPojo = new GraphicPojo(analyzerDataService.getDataByMonth(dayDate),scaleSelected);
			graphicYearPojo = new GraphicPojo(analyzerDataService.getDataByYear(dayDate),scaleSelected);

			aL1Check = true;
			aL2Check = true;
			aL3Check = true;

			hours = getLoadHours();
			months = getLoadMonths();
			years = analyzerDataService.getRegistryYears();

			values = ScaleGraphic.values();

			enableDayGraphic = true;
			enableDayHoursGraphic = false;
			enableMonthGraphic = false;
			enableYearGraphic = false;
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void changeScale(){
		switch(scaleSelected){
		case DAY:
			enableDayGraphic = true;
			enableDayHoursGraphic = false;
			enableMonthGraphic = false;
			enableYearGraphic = false;
			break;
		case HOUR:
			enableDayHoursGraphic = true;
			enableDayGraphic = true;
			enableMonthGraphic = false;
			enableYearGraphic = false;
			break;
		case MONTH:
			enableMonthGraphic = true;
			enableYearGraphic = false;
			enableDayHoursGraphic = false;
			enableDayGraphic = false;
			break;
		case YEAR:
			enableYearGraphic = true;
			enableMonthGraphic = false;
			enableDayHoursGraphic = false;
			enableDayGraphic = false;
			break;
		default: 
		}
	}

	private List<SelectItem> getLoadHours() {
		List<SelectItem> hours = new ArrayList<SelectItem>();

		hours.add(new SelectItem(0, "00:00"));
		hours.add(new SelectItem(1, "01:00"));
		hours.add(new SelectItem(2, "02:00"));
		hours.add(new SelectItem(3, "03:00"));
		hours.add(new SelectItem(4, "04:00"));
		hours.add(new SelectItem(5, "05:00"));
		hours.add(new SelectItem(6, "06:00"));
		hours.add(new SelectItem(7, "07:00"));
		hours.add(new SelectItem(8, "08:00"));
		hours.add(new SelectItem(9, "09:00"));
		hours.add(new SelectItem(10, "10:00"));
		hours.add(new SelectItem(11, "11:00"));
		hours.add(new SelectItem(12, "12:00"));
		hours.add(new SelectItem(13, "13:00"));
		hours.add(new SelectItem(14, "14:00"));
		hours.add(new SelectItem(15, "15:00"));
		hours.add(new SelectItem(16, "16:00"));
		hours.add(new SelectItem(17, "17:00"));
		hours.add(new SelectItem(18, "18:00"));
		hours.add(new SelectItem(19, "19:00"));
		hours.add(new SelectItem(20, "20:00"));
		hours.add(new SelectItem(21, "21:00"));
		hours.add(new SelectItem(22, "22:00"));
		hours.add(new SelectItem(23, "23:00"));

		return hours;
	}

	private List<SelectItem> getLoadMonths() {
		List<SelectItem> months = new ArrayList<SelectItem>();

		months.add(new SelectItem(0, "January"));
		months.add(new SelectItem(1, "February"));
		months.add(new SelectItem(2, "March"));
		months.add(new SelectItem(3, "April"));
		months.add(new SelectItem(4, "May"));
		months.add(new SelectItem(5, "June"));
		months.add(new SelectItem(6, "July"));
		months.add(new SelectItem(7, "August"));
		months.add(new SelectItem(8, "September"));
		months.add(new SelectItem(9, "October"));
		months.add(new SelectItem(10, "November"));
		months.add(new SelectItem(11, "December"));

		return months;
	}

	public void updateChartValues(){

	}

	public void updateChartSeries1(){
		aL1Check = graphicDayPojo.updateSeries1(aL1Check);
	}

	public void updateChartSeries2(){
		aL2Check = graphicDayPojo.updateSeries2(aL2Check);
	}

	public void updateChartSeries3(){
		aL3Check = graphicDayPojo.updateSeries3(aL3Check);
	}

	public void onDaySelect(SelectEvent event) throws IOException {
		graphicDayPojo.fillGraphicForData(analyzerDataService.getDataByDay(dayDate),scaleSelected);
	}

	public void fillDatabase() throws IOException{
		analyzerDataService.fillDatabaseData();
	}

	public void fillDatabaseScript() throws IOException{
		analyzerDataService.fillDatabaseDataWithMoreThenOneYears();
	}

	public AnalyzerDataService getAnalyzerDataService() {
		return analyzerDataService;
	}


	public void setAnalyzerDataService(AnalyzerDataService analyzerDataService) {
		this.analyzerDataService = analyzerDataService;
	}

	public boolean isaL1Check() {
		return aL1Check;
	}


	public void setaL1Check(boolean aL1Check) {
		this.aL1Check = aL1Check;
	}


	public boolean isaL2Check() {
		return aL2Check;
	}


	public void setaL2Check(boolean aL2Check) {
		this.aL2Check = aL2Check;
	}


	public boolean isaL3Check() {
		return aL3Check;
	}


	public void setaL3Check(boolean aL3Check) {
		this.aL3Check = aL3Check;
	}

	public ScaleGraphic getScaleSelected() {
		return scaleSelected;
	}

	public void setScaleSelected( ScaleGraphic scaleSelected ) {
		this.scaleSelected = scaleSelected;
	}

	public ScaleGraphic[] getValues() {
		return values;
	}

	public void setValues( ScaleGraphic[] values ) {
		this.values = values;
	}

	public Date getDayDate() {
		return dayDate;
	}

	public void setDayDate( Date dayDate ) {
		this.dayDate = dayDate;
	}

	public boolean isEnableDayGraphic() {
		return enableDayGraphic;
	}

	public void setEnableDayGraphic(boolean enableDayGraphic) {
		this.enableDayGraphic = enableDayGraphic;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public List<SelectItem> getHours() {
		return hours;
	}

	public void setHours(List<SelectItem> hours) {
		this.hours = hours;
	}

	public List<SelectItem> getMonths() {
		return months;
	}

	public void setMonths(List<SelectItem> months) {
		this.months = months;
	}

	public String getHourSelected() {
		return hourSelected;
	}

	public void setHourSelected(String hourSelected) {
		this.hourSelected = hourSelected;
	}

	public boolean isEnableDayHoursGraphic() {
		return enableDayHoursGraphic;
	}

	public void setEnableDayHoursGraphic(boolean enableDayHoursGraphic) {
		this.enableDayHoursGraphic = enableDayHoursGraphic;
	}

	public String getMonthSelected() {
		return monthSelected;
	}

	public void setMonthSelected(String monthSelected) {
		this.monthSelected = monthSelected;
	}

	public String getYearSelected() {
		return yearSelected;
	}

	public void setYearSelected(String yearSelected) {
		this.yearSelected = yearSelected;
	}

	public boolean isEnableYearGraphic() {
		return enableYearGraphic;
	}

	public void setEnableYearGraphic(boolean enableYearGraphic) {
		this.enableYearGraphic = enableYearGraphic;
	}

	public boolean isEnableMonthGraphic() {
		return enableMonthGraphic;
	}

	public void setEnableMonthGraphic(boolean enableMonthGraphic) {
		this.enableMonthGraphic = enableMonthGraphic;
	}

	public GraphicPojo getGraphicDayPojo() {
		return graphicDayPojo;
	}

	public void setGraphicDayPojo(GraphicPojo graphicDayPojo) {
		this.graphicDayPojo = graphicDayPojo;
	}

	public GraphicPojo getGraphicMonthPojo() {
		return graphicMonthPojo;
	}

	public void setGraphicMonthPojo(GraphicPojo graphicMonthPojo) {
		this.graphicMonthPojo = graphicMonthPojo;
	}

	public GraphicPojo getGraphicYearPojo() {
		return graphicYearPojo;
	}

	public void setGraphicYearPojo(GraphicPojo graphicYearPojo) {
		this.graphicYearPojo = graphicYearPojo;
	}
	
	
}
