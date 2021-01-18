package com.jica.bookproject;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.service.autofill.VisibilitySetterAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.CategoryEntity;
import com.jica.bookproject.database.UserEntity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    BookDatabase bookDatabase;

    List<String> categoryNames;
    int countCetegoryRead;

    TextView tvNoCategory;
    PieChart pieChart;

    //프로필 입력 및 수정
    UserEntity userEntity;
    List<UserEntity> userList;


    LinearLayout editLayout;
    LinearLayout textLayout;

    ImageView saveIcon;
    ImageView editIcon;

    EditText etName;
    EditText etAge;
    EditText etMybestbook;

    TextView tvName;
    TextView tvAge;
    TextView tvMybestbook;




    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        bookDatabase = BookDatabase.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup profileView = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        tvNoCategory = profileView.findViewById(R.id.tvNoCategory);
        pieChart = profileView.findViewById(R.id.pieChart);

        //프로필 입력 및 수정
        editLayout = profileView.findViewById(R.id.editLayout);
        textLayout = profileView.findViewById(R.id.textLayout);

        saveIcon = profileView.findViewById(R.id.saveIcon);
        editIcon = profileView.findViewById(R.id.editIcon);

        etName = profileView.findViewById(R.id.etName);
        etAge = profileView.findViewById(R.id.etAge);
        etMybestbook = profileView.findViewById(R.id.etMybestbook);

        tvName = profileView.findViewById(R.id.tvName);
        tvAge = profileView.findViewById(R.id.tvAge);
        tvMybestbook = profileView.findViewById(R.id.tvMybestbook);


        //프로필에 하나라도 입력되어있으면 textLayout, 하나도입력되어있지 않으면 editLayout

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                userEntity = bookDatabase.bookDao().getUserAll();
            }
        });

        thread3.start();
        try{
            thread3.join();
        }catch (Exception e){
            e.printStackTrace();
        }


        if((userEntity.getName().equals("") || userEntity.getName() ==null) && (userEntity.getAge().equals("") || userEntity.getAge() == null) && (userEntity.getMybestbook().equals("") || userEntity.getMybestbook()==null)){
            textLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
        }else{
            textLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            tvName.setText(userEntity.getName());
            tvAge.setText(userEntity.getAge());
            tvMybestbook.setText(userEntity.getMybestbook());
        }







        //프로필

        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userEntity.setName(etName.getText().toString());
                userEntity.setAge(etAge.getText().toString());
                userEntity.setMybestbook(etMybestbook.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bookDatabase.bookDao().userUpdate(userEntity);
                    }
                }).start();


                editLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.VISIBLE);
                tvName.setText(userEntity.getName());
                tvAge.setText(userEntity.getAge());
                tvMybestbook.setText(userEntity.getMybestbook());
                Toast.makeText(getContext(),userEntity.getName() + "," + userEntity.getAge() + "," + userEntity.getMybestbook(),Toast.LENGTH_LONG).show();


            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLayout.setVisibility(View.VISIBLE);
                textLayout.setVisibility(View.GONE);
            }
        });





        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "분야별 독서량";

        //initializing data

        //Map에 put("카테코리명",카테고리별읽은권수)

        //1. 카테코리명 검색 -> List
        //2. 카테코리 이름을 인자로 받아서 완독권수 리턴 -> int




        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                categoryNames = bookDatabase.bookDao().getCategoryName();

            }
        });

        thread.start();

        try{
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }

        if(categoryNames == null){
            tvNoCategory.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
        }else {
            tvNoCategory.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
        }



        Map<String, Integer> typeAmountMap = new HashMap<>();

        for(String name : categoryNames){

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    countCetegoryRead = bookDatabase.bookDao().getCountCetegoryRead(name);
                    Log.d("TAG","카테고리명" + name);
                    Log.d("TAG","카테고리명" + countCetegoryRead);
                }
            });
            thread2.start();
            try{
                thread2.join();
            }catch(Exception e){
                e.printStackTrace();
            }



            typeAmountMap.put(name,countCetegoryRead);
        }

        //initializing colors for the entries


        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            if(typeAmountMap.get(type).floatValue()!=0){ //0인건 띄우지 않음
                pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
            }

        }


        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        MyValueFormatter2 myValueFormatter = new MyValueFormatter2();
        pieDataSet.setValueFormatter(myValueFormatter);

        PieData pieData = new PieData(pieDataSet); //pieDataSet으로 pieData를 만들어준다.
        pieData.setDrawValues(true);




        pieChart.getDescription().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.invalidate();

       return profileView;
    }

}

