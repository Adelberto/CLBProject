package clb.business;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clb.business.constants.Month;
import clb.business.objects.AnalyzerObject;
import clb.business.objects.AnalyzerRegistryObject;
import clb.business.objects.BuildingObject;
import clb.business.objects.DataLoggerObject;
import clb.business.objects.MonthAverageObject;
import clb.business.objects.UsersystemObject;
import clb.database.ClbDao;
import clb.database.entities.AnalyzerRegistryEntity;
import clb.database.entities.UsersystemEntity;

@Service
public class AnalyzerDataServiceImpl implements AnalyzerDataService, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    private ClbDao<AnalyzerRegistryEntity> clbDaoAnalyzer;
    
    @Autowired
    private ClbDao<UsersystemEntity> clbDaoUsersystem;

    @Autowired
    private TaskExecutor taskExecutor;

    @Value(value = "classpath:documents/fileAnalyzerRegistries.xlsx")
    private Resource dataAnalyzerXls;

    public void init(){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try(ServerSocket s = new ServerSocket(1234)){
                    while(true){
                        Socket clientSocket = s.accept();
                        try(Scanner in = new Scanner(clientSocket.getInputStream())){
                            JSONObject jsonObj = new JSONObject(in.nextLine());
                            System.out.println(jsonObj.toString());
                        }
                        catch(JSONException jsonex){
                            jsonex.printStackTrace();
                        }
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void destroy(){

    }
    
    private List<AnalyzerObject> createDummyUsers(){
        UsersystemObject userObject = new UsersystemObject();
        
        userObject.setUserid( "nobreyeste@hotmail.com" );
        userObject.setName( "Carlos Nobre" );
        userObject.setAddress( "No address at this point" );
        userObject.setUsername( "cnobre" );
        userObject.setPassword( "123" );
        
        UsersystemObject userObject2 = new UsersystemObject();
        
        userObject2.setUserid( "brunocatela@hotmail.com" );
        userObject2.setName( "Bruno Catela" );
        userObject2.setAddress( "No address at this point" );
        userObject2.setUsername( "bcatela" );
        userObject2.setPassword( "123" );
        
        UsersystemObject userObject3 = new UsersystemObject();
        
        userObject3.setUserid( "luissantos@hotmail.com" );
        userObject3.setName( "Luis Santos" );
        userObject3.setAddress( "No address at this point" );
        userObject3.setUsername( "lsantos" );
        userObject3.setPassword( "123" );

        
        BuildingObject buildingObject = new BuildingObject();
        buildingObject.setName( "Amanjena Hotel" );
        buildingObject.setUsersystem(userObject);
        
        BuildingObject buildingObject2 = new BuildingObject();
        buildingObject2.setName( "AquaMirage Hotel" );
        buildingObject.setUsersystem(userObject);
        
        BuildingObject buildingObject3 = new BuildingObject();
        buildingObject3.setName( "Ritz" );
        buildingObject.setUsersystem(userObject2);
        
        BuildingObject buildingObject4 = new BuildingObject();
        buildingObject4.setName( "VASP" );
        buildingObject.setUsersystem(userObject3);
        
        List<AnalyzerObject> listAnalyzerObject = new ArrayList<AnalyzerObject>();
        
        //Create Data Loggers
        for(int i=0 ;i< 100; i++){
        	DataLoggerObject dlObj = new DataLoggerObject();
        	dlObj.setName("Data Logger " + i);
        	
        	for(int j=0;j<100;j++){
        		AnalyzerObject analyzerObject = new AnalyzerObject();
        		analyzerObject.setName("Analyzer "+j);
        		
        		analyzerObject.setDataLogger(dlObj);
        		
        		listAnalyzerObject.add(analyzerObject);
        	}
        	
        	if(i < 25){
        		dlObj.setBuilding(buildingObject);
        	}
        	else if(i >=25 && i < 50){
        		dlObj.setBuilding(buildingObject2);
        	}
        	else if(i >=50 && i < 75){
        		dlObj.setBuilding(buildingObject3);
        	}
        	else{
        		dlObj.setBuilding(buildingObject4);
        	}
        }
        
        clbDaoUsersystem.create( userObject.toEntity() );
        clbDaoUsersystem.create( userObject2.toEntity() );
        clbDaoUsersystem.create( userObject3.toEntity() );
        
        return listAnalyzerObject;
    }
    

    @Override
    @Transactional
    public void fillDatabaseDataWithMoreThenOneYears() {
        
        List<AnalyzerObject> analyzersList = createDummyUsers();
        
        int numberOfYears = 2;
        int startingYear = 2017;
        int lowAl = 200;
        int highAl = 450;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set( startingYear, 0, 1);

        System.out.println( "Starting persist Data.." );

        Random random = new Random();

        int m = 0;
        
        for(int i = 0 ;i<numberOfYears; i++){
            int yearDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
            for(int l = 0; l < yearDays; l++ ){
                for(int j = 0; j < 24 ; j++){
                    for(int k = 0; k < 60; k++){

                        AnalyzerRegistryObject anaRegObj = new AnalyzerRegistryObject();
                        anaRegObj.setCurrenttime( (j < 10 ? "0"+j : ""+j) + ":" + (k < 10 ? "0"+k : ""+k)+ ":00");
                        anaRegObj.setCurrentdate( calendar.getTime() ); 

                        anaRegObj.setAl1(lowAl + (highAl - lowAl) * random.nextDouble());
                        anaRegObj.setAl2(lowAl + (highAl - lowAl) * random.nextDouble());
                        anaRegObj.setAl3(lowAl + (highAl - lowAl) * random.nextDouble());
                        
                        analyzersList.get(m).getAnalyzerRegistries().add(anaRegObj);
                        
                        m++;
                        
                        m = m == analyzersList.size() ? 0 : m;

                        clbDaoAnalyzer.create( anaRegObj.toEntity() );
                        anaRegObj = null;
                    }
                    clbDaoAnalyzer.flush();
                }

                System.out.println( "Persisted Day: " + calendar.get( Calendar.DAY_OF_MONTH ) + ", from month " + calendar.get( Calendar.MONTH ) + ", from year " + calendar.get( Calendar.YEAR ));
                calendar.add(Calendar.DATE, 1);
            }
        }
    }


    @Override
    @Transactional
    public void fillDatabaseData() throws IOException{

        XSSFWorkbook workbook = new XSSFWorkbook(dataAnalyzerXls.getInputStream());
        XSSFSheet worksheet = workbook.getSheet("Sheet1");

        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(new Date());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        List<AnalyzerRegistryEntity> analyzerRegistries = new ArrayList<AnalyzerRegistryEntity>();
        List<AnalyzerRegistryEntity> analyzerRegistriesBeforeCurrentTime = new ArrayList<AnalyzerRegistryEntity>();

        boolean currentHourPassed = false;
        boolean currentMinutesPassed = false;

        for(int i = 1;i<worksheet.getLastRowNum();i++){

            XSSFRow row = worksheet.getRow(i);

            AnalyzerRegistryObject analyzerRegistryObject1 = new AnalyzerRegistryObject();
            analyzerRegistryObject1.setCurrentdate(row.getCell(0).getDateCellValue());

            calendar.setTime(row.getCell(1).getDateCellValue());   // assigns calendar to given date 

            if(calendar.get(Calendar.HOUR_OF_DAY) == currentHour){
                currentHourPassed = true;
                if(calendar.get(Calendar.MINUTE) == currentMinute){
                    currentMinutesPassed = true;
                }
            }

            analyzerRegistryObject1.setCurrenttime((calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY)+"") + ":" + 
                    (calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)+"") +":"+ 
                    (calendar.get(Calendar.SECOND) < 10 ? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND)));

            analyzerRegistryObject1.setAl1(row.getCell(2).getNumericCellValue());
            analyzerRegistryObject1.setAl2(row.getCell(3).getNumericCellValue());
            analyzerRegistryObject1.setAl3(row.getCell(4).getNumericCellValue());
            analyzerRegistryObject1.setHz(row.getCell(5).getNumericCellValue());
            analyzerRegistryObject1.setPfl1(row.getCell(6).getNumericCellValue());
            analyzerRegistryObject1.setPfl2(row.getCell(7).getNumericCellValue());
            analyzerRegistryObject1.setPfl3(row.getCell(8).getNumericCellValue());
            analyzerRegistryObject1.setPfsys(row.getCell(9).getNumericCellValue());
            //analyzerRegistryObject.setPhaseSequence(row.getCell(10).getNumericCellValue());
            analyzerRegistryObject1.setVl1l2(row.getCell(11).getNumericCellValue());
            analyzerRegistryObject1.setVl1n(row.getCell(12).getNumericCellValue());
            analyzerRegistryObject1.setVl2l3(row.getCell(13).getNumericCellValue());
            analyzerRegistryObject1.setVl2n(row.getCell(14).getNumericCellValue());
            analyzerRegistryObject1.setVl3l1(row.getCell(15).getNumericCellValue());
            analyzerRegistryObject1.setVl3n(row.getCell(16).getNumericCellValue());
            analyzerRegistryObject1.setVllsys(row.getCell(17).getNumericCellValue());
            analyzerRegistryObject1.setVlnsys(row.getCell(18).getNumericCellValue());
            analyzerRegistryObject1.setKval1(row.getCell(19).getNumericCellValue());
            analyzerRegistryObject1.setKval2(row.getCell(20).getNumericCellValue());
            analyzerRegistryObject1.setKval3(row.getCell(21).getNumericCellValue());
            analyzerRegistryObject1.setKvasys(row.getCell(22).getNumericCellValue());
            analyzerRegistryObject1.setKwh(row.getCell(23).getNumericCellValue());
            analyzerRegistryObject1.setKwl1(row.getCell(24).getNumericCellValue());
            analyzerRegistryObject1.setKwl2(row.getCell(25).getNumericCellValue());
            analyzerRegistryObject1.setKwl3(row.getCell(26).getNumericCellValue());
            analyzerRegistryObject1.setKwsys(row.getCell(27).getNumericCellValue());
            analyzerRegistryObject1.setKvarh(row.getCell(28).getNumericCellValue());
            analyzerRegistryObject1.setKvarl1(row.getCell(29).getNumericCellValue());
            analyzerRegistryObject1.setKvarl2(row.getCell(30).getNumericCellValue());
            analyzerRegistryObject1.setKvarl3(row.getCell(31).getNumericCellValue());
            analyzerRegistryObject1.setKvarsys(row.getCell(32).getNumericCellValue());

            if(currentHourPassed && currentMinutesPassed)
                analyzerRegistriesBeforeCurrentTime.add(analyzerRegistryObject1.toEntity());
            else analyzerRegistries.add(analyzerRegistryObject1.toEntity());
        }
        analyzerRegistriesBeforeCurrentTime.addAll(analyzerRegistries);

        clbDaoAnalyzer.persistData(analyzerRegistriesBeforeCurrentTime);
    }

    public ClbDao<AnalyzerRegistryEntity> getClbDaoAnalyzer() {
        return clbDaoAnalyzer;
    }

    public void setClbDaoAnalyzer(ClbDao<AnalyzerRegistryEntity> clbDaoAnalyzer) {
        this.clbDaoAnalyzer = clbDaoAnalyzer;
    }

    public Resource getDataAnalyzerXls() {
        return dataAnalyzerXls;
    }

    public void setDataAnalyzerXls(Resource dataAnalyzerXls) {
        this.dataAnalyzerXls = dataAnalyzerXls;
    }

    public List<AnalyzerRegistryObject> getDataByDay(Date day) {

        if(day == null){
            return new ArrayList<AnalyzerRegistryObject>();
        }

        return new ArrayList<AnalyzerRegistryObject>(clbDaoAnalyzer.getAnalyzerRegistriesByDay(day).stream()
                .map( AnalyzerRegistryObject::new ).collect( Collectors.toList() ));
    }

    @Override
    public List<AnalyzerRegistryObject> getDataByDayAndHours( Date day, String hour ) throws IOException {

        if(day == null || hour == null){
            return new ArrayList<AnalyzerRegistryObject>();
        }

        return new ArrayList<AnalyzerRegistryObject>(clbDaoAnalyzer.getAnalyzerRegistriesByDayAndHour(day,hour).stream()
                .map( AnalyzerRegistryObject::new ).collect( Collectors.toList() ));
    }

    @Override
    public List<MonthAverageObject> getDataByYear(Integer year) {

        if(year == null){
            return new ArrayList<MonthAverageObject>();
        }

        else return clbDaoAnalyzer.getYearMonthAverages(year).stream()
                .map(objectArray -> {
                    Object[] object = (Object[]) objectArray;
                    return new MonthAverageObject( (Double) object[0], (Double) object[1], (Double) object[2], (Integer) object[3], Month.getMonthById( (Integer)object[4]),null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthAverageObject> getDataByYearAndMonths( Integer yearSelected, Month monthSelected ) {
        if(yearSelected == null || monthSelected == null){
            return new ArrayList<MonthAverageObject>();
        }

        else return clbDaoAnalyzer.getYearMonthDaysAverages(yearSelected,monthSelected.getMonth()).stream()
                .map(objectArray -> {
                    Object[] object = (Object[]) objectArray;
                    return new MonthAverageObject( (Double) object[0], (Double) object[1], (Double) object[2], (Integer) object[3], 
                            Month.getMonthById( (Integer)object[4]), (Integer) object[5]);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<Integer> getRegistryYears() {
        return clbDaoAnalyzer.getRegistryYears();
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public ClbDao<UsersystemEntity> getClbDaoUsersystem() {
        return clbDaoUsersystem;
    }

    public void setClbDaoUsersystem( ClbDao<UsersystemEntity> clbDaoUsersystem ) {
        this.clbDaoUsersystem = clbDaoUsersystem;
    }
    
    
}
