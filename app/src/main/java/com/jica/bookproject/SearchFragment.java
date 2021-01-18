package com.jica.bookproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //멤버변수 선언
    SearchView search;
    ArrayList<BookItem> items;

    String keyword=null;
    int start;

    RecyclerView recyclerView;
    BookAdapter adapter;

    //검색결과 페이지처리
    LinearLayout pageLayout;

    Button backBtn;
    Button nextBtn;

    TextView curCount;
    TextView totalCount;



    Handler handler = new Handler();


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup searchView = (ViewGroup)inflater.inflate(R.layout.fragment_search, container, false);

        search = searchView.findViewById(R.id.search);
        recyclerView = searchView.findViewById(R.id.recyclerView);
        pageLayout = searchView.findViewById(R.id.pageLayout);
        backBtn = searchView.findViewById(R.id.backBtn);
        nextBtn = searchView.findViewById(R.id.nextBtn);
        curCount = searchView.findViewById(R.id.curCount);
        totalCount = searchView.findViewById(R.id.totalCount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter(getActivity()); //MainActivity의 context

        recyclerView.setAdapter(adapter);

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(search,0);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) { //검색버튼이 눌려졌을 때 이벤트처리
                pageLayout.setVisibility(View.VISIBLE);
                SearchFragment.this.keyword = keyword;

                start = 1;
                //내부에서 네트워크쓰레드 작동
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Api api = new Api(keyword,start);
                        Log.d("TAG",keyword+start);
                        items =  api.parseStart();



                        if( items != null){
                            //화면에 보여주는 작업 - 핸들러 이용
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    try{
                                        totalCount.setText(Integer.toString(items.get(0).getTotal())); //total은 모두 같은 같이 들어가있기때문에 한번만 얻어오면된다. get(0)
                                        //검색버튼을 눌렸을때 total을 얻어옴


                                    if(start+29 < Integer.parseInt(totalCount.getText().toString())){
                                        curCount.setText(Integer.toString(start+29));
                                    }else{
                                        curCount.setText(Integer.toString(items.size())); //파싱한 items의 갯수
                                    }
                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();

                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getContext(),"검색결과가 없습니다.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }else{
                            Log.d("TAG", "다운이나 파싱에서 오류 발생함!");

                        }

                    }
                }).start();


                return false;

            }

            @Override
            public boolean onQueryTextChange(String keyword) { //검색어가 변경되었을 때 이벤트처리

                return false;
            }



        });

        //요청결과 이전페이지
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "Keyword값" + keyword + "start값" + start );

                //Log.d("TAG","START값" + start);

                start =  start-30;


                if(start < 1){ //음수일 경우
                    start = 1;
                    return;
                }




                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Api api = new Api(keyword,start);
                        items =  api.parseStart();
                        if( items != null){
                            //화면에 보여주는 작업 - 핸들러 이용
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d("TAG","START값"+ start);



                                    if(start+29 < items.get(0).getTotal()){
                                        curCount.setText(Integer.toString(start+29));
                                    } else {
                                        curCount.setText(totalCount.getText().toString()); //초과시 토탈값을 띄워줌
                                    }

                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }else{
                            Log.d("TAG", "다운이나 파싱에서 오류 발생함!");

                        }
                    }
                }).start();




            }
        });

        //요청결과 다음페이지
        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("TAG", "Keyword값" + keyword + "start값" + start );

                if(Integer.parseInt(totalCount.getText().toString()) <= 30){ //totalcount가 30보다 작거나 같은 경우 즉, 1페이지일 경우 아무동작도 하지않는다.
                    return;
                }


                if(Integer.parseInt(totalCount.getText().toString()) <= start+30){ //total초과시 동작하지 않는다.
                    return;
                }


                start = start + 30; //30건뒤의 검색결과 31부터 데이터가져옴 //61


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "Keyword값" + keyword + "start값" + start );
                        Api api = new Api(keyword,start);
                        items =  api.parseStart();
                        if( items != null){
                            //화면에 보여주는 작업 - 핸들러 이용
                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    if(start+29<items.get(0).getTotal()){
                                        curCount.setText(Integer.toString(start+29)); //다음페이지를 클릭했을때 값을 얻어옴
                                        //31+29 //61+29
                                    }else{
                                        curCount.setText(totalCount.getText().toString());
                                    }


                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }else{
                            Log.d("TAG", "다운이나 파싱에서 오류 발생함!");

                        }
                    }
                }).start();


            }
        });



        return searchView;
    }

}