package com.jica.bookproject;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.text.UFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.BookEntity;

import android.text.format.DateFormat;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    List<BookEntity> readBookEntity;

    LineChart lineChart;
    BarChart barChart;

    BookDatabase bookDatabase;


    Date startDate = null;
    Date endDate = null;


    LinearLayout yearLayout;
    TextView tvYear;
    
    //List<BookEntity> readBookEntity;






    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        bookDatabase =BookDatabase.getInstance(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup chartView = (ViewGroup) inflater.inflate(R.layout.fragment_chart, container, false);

        lineChart = chartView.findViewById(R.id.lineChart);
        barChart = chartView.findViewById(R.id.barChart);

        yearLayout = chartView.findViewById(R.id.yearLayout);
        tvYear = chartView.findViewById(R.id.tvYear);



        //날짜선택 다이얼로그 띄우기
        yearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();

                //DatePickerDialog(Context context, int themeResId, @Nullable DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
                //                                      테마                          리스너                                      초기에 띄울 날짜
                //다이얼로그의 첫화면에 오늘 날짜를 띄운다.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dp = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            //OnDateSetListener의 onDateSet()오버라이딩
                            @Override
                            public void onDateSet(DatePicker view, int userYear, int userMonthOfYear, int userDayOfMonth) {
                                //Check if selected month/year is in future of today.
                                //확인을 누른후 입력한날짜로 수행할 동작
                                Toast.makeText(getContext(),userYear + "년을 선택했습니다.",Toast.LENGTH_LONG).show();
                                tvYear.setText(userYear+"년도");


                                String strStartDate = userYear + "-01-01";
                                String strEndDate = userYear + "-12-31";

                                try {
                                    startDate = new SimpleDateFormat("yyyy-MM-dd").parse(strStartDate);
                                    endDate = new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        readBookEntity = bookDatabase.bookDao().getReadBookEntity(startDate, endDate);
                                    }
                                });

                                thread.start();

                                try{
                                    thread.join();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                Log.d("TAG",startDate + "," + endDate);
                                Log.d("TAG",strStartDate + "," + strEndDate);


                                drawBarChart(readBookEntity);
                                drawLineChart(readBookEntity);

                            }
                        }, year, month, day);
                            //현재날짜로 설정된다.
                dp.getDatePicker().setMaxDate(c.getTimeInMillis()); //해당년도 이후는 나오지 않도록 설정 //getTimeInMillis() 현재시각을 long으로 얻는다.

                dp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //뒤에 겹치는 네모를 지움
                dp.setTitle("년도를 선택해주세요");

                //DatePicker에서 년도만 뜨도록 설정
                datePickerOnlyYear(dp);

                dp.show();
            }
        });

        //1. 년도에 따라서 y축을 새로받아와야함
        //2. 한달치 권수를 추출해서 y축에 넣어야함 ( between 쿼리문이용 conunt(*)이용)

        Calendar c = Calendar.getInstance();
        // getReadBookEntity(c.get(Calendar.YEAR));

        String strStartDate = c.get(Calendar.YEAR) + "-01-01";
        String strEndDate = c.get(Calendar.YEAR) + "-12-31";


        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(strStartDate);
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                readBookEntity = bookDatabase.bookDao().getReadBookEntity(startDate,endDate);
            }
        });
        thread.start();

        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("TAG",startDate + "," + endDate);
        Log.d("TAG",strStartDate + "," + strEndDate);

        //Log.d("TAG","readBookEntity" + readBookEntity.get(0).getTitle());

        drawBarChart(readBookEntity);
        drawLineChart(readBookEntity);


        return chartView;
    }

    void datePickerOnlyYear(DatePickerDialog dp){
        try {

            Field[] datePickerDialogFields = dp.getClass()
                    .getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {

                if (datePickerDialogField.getName().equals("mDatePicker")) { //Field배열을 돌면서 이름이 "mDatePicker"일때 동작
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dp);
                    Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {

                        //App내부에 저장한 리소스를 불러온다.Resources.getSystem().getIdentifier(String name, String defType, String defPackage);
                        //                                                                         파일명      디렉토리명       패키지명
                        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");

                        if (daySpinnerId != 0) {
                            View daySpinner = datePicker.findViewById(daySpinnerId);

                            if (daySpinner != null) {
                                daySpinner.setVisibility(View.GONE); //일을 보이지않게함
                            }
                        }
                        int MonthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");

                        if (MonthSpinnerId != 0) {
                            View monthSpinner = datePicker.findViewById(MonthSpinnerId);

                            if (monthSpinner != null) {
                                monthSpinner.setVisibility(View.GONE); //월을 보이지 않게함
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    //barChart를 그리는 메소드
    void drawBarChart(List<BookEntity> readBookEntity){

        String days[] = {"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;
        int count6 = 0;
        int count7 = 0;
        int count8 = 0;
        int count9 = 0;
        int count10 = 0;
        int count11 = 0;
        int count12 = 0;

        for(BookEntity tempEntity : readBookEntity){
            Date date = tempEntity.getReadDate();
            String monthNumber = (String)DateFormat.format("MM",date);
            switch (monthNumber){
                case "01" :
                    count1 ++;
                    break;
                case "02" :
                    count2 ++;
                    break;
                case "03" :
                    count3 ++;
                    break;
                case "04" :
                    count4 ++;
                    break;
                case "05" :
                    count5 ++;
                    break;
                case "06" :
                    count6 ++;
                    break;
                case "07" :
                    count7 ++;
                    break;
                case "08" :
                    count8 ++;
                    break;
                case "09" :
                    count9 ++;
                    break;
                case "10" :
                    count10 ++;
                    break;
                case "11" :
                    count11 ++;
                    break;
                case "12" :
                    count12 ++;
                    break;
            }
        }



        //차트의 아래 Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //xAxis의 위치는 아래쪽
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10);

        //차트의 오른쪽 Axis
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); //rightSxis를 비활성화함

        //차트의 왼쪽 Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        leftAxis.setGranularity(1.0f); //YAxis가 정수(또는 원하는 간격)를 항상 표시하도록
        leftAxis.setGranularityEnabled(true);

        List<BarEntry> entries = new ArrayList<>();
        //entries.add(new Entry(x축,y축); 점찍히는 위치
        entries.add(new BarEntry(1,count1));
        entries.add(new BarEntry(2,count2));
        entries.add(new BarEntry(3,count3));
        entries.add(new BarEntry(4,count4));
        entries.add(new BarEntry(5,count5));
        entries.add(new BarEntry(6,count6));
        entries.add(new BarEntry(7,count7));
        entries.add(new BarEntry(8,count8));
        entries.add(new BarEntry(9,count9));
        entries.add(new BarEntry(10,count10));
        entries.add(new BarEntry(11,count11));
        entries.add(new BarEntry(12,count12));

        xAxis.setLabelCount(entries.size(),false); //라벨의갯수,false -> formatter의 정상출력을 위해 고정하지않는다.

        MyValueFormatter myValueFormatter = new MyValueFormatter();
        xAxis.setValueFormatter(myValueFormatter);

        BarDataSet barDataSet = new BarDataSet(entries,"읽은책");
        barDataSet.setValueFormatter(myValueFormatter);


        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10);

        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);
        barChart.invalidate();

    }








    //lineChart를 그리는 메소드
    void drawLineChart(List<BookEntity> readBookEntity){
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;
        int count6 = 0;
        int count7 = 0;
        int count8 = 0;
        int count9 = 0;
        int count10 = 0;
        int count11 = 0;
        int count12 = 0;

        for(BookEntity tempEntity : readBookEntity){
            Date date = tempEntity.getReadDate();
            String monthNumber = (String)DateFormat.format("MM",date);
            switch (monthNumber){
                case "01" :
                    count1 ++;
                    break;
                case "02" :
                    count2 ++;
                    break;
                case "03" :
                    count3 ++;
                    break;
                case "04" :
                    count4 ++;
                    break;
                case "05" :
                    count5 ++;
                    break;
                case "06" :
                    count6 ++;
                    break;
                case "07" :
                    count7 ++;
                    break;
                case "08" :
                    count8 ++;
                    break;
                case "09" :
                    count9 ++;
                    break;
                case "10" :
                    count10 ++;
                    break;
                case "11" :
                    count11 ++;
                    break;
                case "12" :
                    count12 ++;
                    break;
            }


        }

        //차트의 아래 Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //xAxis의 위치는 아래쪽
        xAxis.setLabelCount(12,true); //라벨의 갯수 12개로 고정


        //차트의 오른쪽 Axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); //rightSxis를 비활성화함

        //차트의 왼쪽 Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);

        leftAxis.setGranularity(1.0f); //YAxis가 정수(또는 원하는 간격)를 항상 표시하도록
        leftAxis.setGranularityEnabled(true);


        List<Entry> entries = new ArrayList<>();
        //entries.add(new Entry(x축,y축); 점찍히는 위치
        entries.add(new Entry(1,count1));
        entries.add(new Entry(2,count2));
        entries.add(new Entry(3,count3));
        entries.add(new Entry(4,count4));
        entries.add(new Entry(5,count5));
        entries.add(new Entry(6,count6));
        entries.add(new Entry(7,count7));
        entries.add(new Entry(8,count8));
        entries.add(new Entry(9,count9));
        entries.add(new Entry(10,count10));
        entries.add(new Entry(11,count11));
        entries.add(new Entry(12,count12));

        MyValueFormatter2 myValueFormatter = new MyValueFormatter2();
        xAxis.setValueFormatter(myValueFormatter); //xAxis 라벨설정
        xAxis.setTextSize(10);

        LineDataSet lineDataSet = new LineDataSet(entries,"읽은책");
        lineDataSet.setLineWidth(3); //선굵기

        LineData lineData = new LineData(lineDataSet);
        lineData.setValueFormatter(myValueFormatter); //1권,2권 ...형태로 value표현
        lineData.setValueTextSize(10); //value TextSize

        lineChart.getDescription().setEnabled(false); //descriptionLabel삭제
        lineChart.setData(lineData); //차트에 lineDate 세팅
        lineChart.invalidate();
    }



}

//xAxis에 라벨붙이기위해 BarChart의 ValueFormatter
class MyValueFormatter extends ValueFormatter  {

    private String days[] = {"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        int index = Math.round(value);

        if (index < 0 || index >= days.length || index != (int)value)
            return days[11]; //return "" 하면 마지막이 안보임. days[11] 마지막값을 넣어줌

        return days[index-1];
    }

    @Override
    public String getFormattedValue(float value) {
        if(value == 0){
            return "";
        }
        return ((int) value) + "권";
    }
}

//LineChart의 ValueFormatter
class MyValueFormatter2 extends ValueFormatter {

    private String days[] = {"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        //Log.d("TAG","LineChart value : " + (value-1));

        if(days.length < (int)value){
            return Float.toString(value);
        }

        return days[(int)(value-1)];
    }

    @Override
    public String getFormattedValue(float value) {
        if(value == 0){
            return "";
        }
        return ((int) value) + "권";
    }
}