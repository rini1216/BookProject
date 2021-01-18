package com.jica.bookproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//Fragment 에서 activity 호출 후 호출된 Activity 종료 시 작업 결과를 return받고싶을때 - UserInfoActiivity 종료시 Activity의 data결과를 리턴 (평점, 완독여부)

//1. Fragment를 포함하는 Activity 에 FragmentActivity 를 상속
//2. Fragment 에 onActivityResult 를 오버라이딩하여 구현 //액티비티의 결과코드를 받아 실행할 작업
//3. Fragment 에 startActivityForResult() 메소드를 호출
//4. Fragment 에 의해 실행된 Activity 에서 setReslut 후에 finish
//5. Fragment 의 onActvityResult 가 실행됨.



public class MainActivity extends FragmentActivity {

    //멤버변수 선언
    static HomeFragment homeFragment;
    static SearchFragment searchFragment;
    static ChartFragment chartFragment;
    static ProfileFragment profileFragment;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fragment객체 생성, ui객체 찾기
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        chartFragment = new ChartFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,homeFragment).commit();

        //getSupportFragmentManager()는 FragmentManager객체를 리턴한다.
        //FragmentManager객체는 프래그먼트를 다루는 작업을 해주는 객체로써 프래그먼트를 추가,삭제 또는 교체등의 작업을 할 수 있게 해준다.
        //하지만 이런 작업들은 프래그먼트 변경시 오류가 생기면 다시 원래상태로 돌릴 수 있어야하므로 트랜잭션객체를 만들어 실행한다.
        //트랜젝션 객체는 beginTransaction()메소드를 호출하면 시작되고 commit()메소드를 호출하면 실행된다.
        //replace의 첫번째 인자는 R.id.framelayout에 있는 기존의 Fragment를 제거한 후
        //두 번째 매개인자인 fragment를 R.id.framelayout에 집어 넣는 기능이다.

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //누른 항목의 아이디를 얻어 누른 버튼의 기능을 지정한다
                switch (item.getItemId()){
                    case R.id.m_home :
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,homeFragment).commit();
                        return true;
                    case R.id.m_search :
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,searchFragment).commit();
                        return true;
                    case R.id.m_chart :
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,chartFragment).commit();
                        return true;
                    case R.id.m_profile :
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,profileFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }

}