package com.jica.bookproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Update;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.BookEntity;
import com.jica.bookproject.database.CategoryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    HomeAdapter adapter;


    //멤버변수 선언
    Spinner spinner;

    ArrayList<String> arrayList; //스피너의 항목을 나타낼 ArrayList
    ArrayAdapter<String> arrayAdapter; //배열로 된 데이터를 아이템으로 추가할 때 사용하는 어댑터
                                       //spinner은 선택위젯이므로 어댑터가 필요하다.

    boolean firstSpinner = false; //최초 1번 자동작동을 하지않기위해 로직에 필요한 변수


    //단일선택목록대화상자 로직에 필요한 변수
    String management[] = {"카테고리 생성","카테고리 수정","카테고리 삭제"};
    int mSelect = 0;

    //ArryaList -> Sring[] 로직에 필요한 변수
    String[] items;



    //데이터베이스
    BookDatabase bookDatabase;



    //데이터베이스에서 받아온 값을 넣어줄 공간
    List<BookEntity> bookList;

    CategoryEntity categoryName;

    List<CategoryEntity> categoryList;

    List<BookEntity> selectCategoryList;



    //서재(Home)내부 검색
    SearchView searchHome;
    List searchHomeList;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //데이터베이스 작업



    //onActivityResult에서 액티비티의 resultCode를 받아 수행할 작업
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Thread thead = new Thread(new Runnable() {
            @Override
            public void run() {
                bookList = bookDatabase.bookDao().getAll(); //데이터베이스의 조회결과를 bookList에 넣는다.
            }
        });

        thead.start();
        try{
            thead.join(); //해당 쓰레드가 종료될때까지 대기

        }catch (Exception e){
            e.printStackTrace();
        }


        adapter.setItems(bookList); //HomeAdapter에 bookList를 set해줌줌

        Toast.makeText(getActivity(),"갱신완료!!",Toast.LENGTH_LONG).show();

    }



    @Override
    public void onCreate(Bundle savedInstanceState) { //준비작업
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        bookDatabase = BookDatabase.getInstance(getActivity());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                bookList = bookDatabase.bookDao().getAll();
                categoryList = bookDatabase.bookDao().getCategoryAll();
            }
        });

        thread.start();

        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
                                                        //리소스          //리소스를 보여줄 부모 컨테이너 //바로붙일것인가
                                                        //여기서 container은 activity_main.xml의 부모GroupView를 가르킨다.
                                                        //inflate()는 View리턴

        searchHome = rootView.findViewById(R.id.searchHome);

        //ui객체 찾기
        //spinner = findViewBy(R.id~) 를 사용하면 에러가 발생한다.
        //이유 :  Fragment View가 inflate하기전에 컴포넌트를 호출하기 때문에 NullPointerException 에러
        //        findViewBy()메소드는 Activity에서 id로 View찾을 때 쓰는 메소드이므로 현재 fragment에서는 그대로 사용할 수 없다.
        spinner = rootView.findViewById(R.id.spinner);
       //전개한 레이아웃에서 UI객체를 찾는다.

        //ArrayList객체 생성
        arrayList = new ArrayList<String>();


        //스피너에 arrayList
        arrayList.add("ALL");


        if(categoryList != null){
            for(int i =0; i<categoryList.size();i++){

                arrayList.add(categoryList.get(i).getName());

            }

        }


        arrayList.add("카테고리 관리");
        //

        //어댑터 생성
        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,arrayList);
                                                //안드로이드에서 제공하는 layout로   //원본데이터가 나타나게 한다.


        spinner.setAdapter(arrayAdapter); //스피너와 어댑터 연결


        //spinner 클릭리스너만들기 (스피너 항목 선택시 동작 지정) // setOnItemClickListener가 아닌 setOnItemSelectedListener를 사용해야함에 주의

        //스피너핸들러를 만들어 스피너작업을 따로 뺀다.
        CategorySpinerHandler categorySpinerHandler = new CategorySpinerHandler();
        spinner.setOnItemSelectedListener(categorySpinerHandler);

        //=========================================================================================

        //서재(Home)내부 검색
        searchHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                Log.d("TAG","String keyword :" + keyword);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchHomeList = bookDatabase.bookDao().getHomeSearch(keyword);
                    }
                });

                thread.start();

                try{
                    thread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }




                adapter.setItems(searchHomeList);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String keyword) {

                return false;
            }
        });



        //리싸이클러뷰

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
                                                                                //3열로 나타남
        recyclerView.setLayoutManager(layoutManager);


        //RecyclerView는 선택위젯이므로 어댑터가 필요함
        adapter = new HomeAdapter(getActivity(),this);

        List list = new ArrayList(bookList); //List형으로 변환

        adapter.setItems(list); //어댑터에 넘겨줌

        recyclerView.setAdapter(adapter);






        return rootView;


    }


    //스피너 작업을 따로 뺀다.
    //spinner 클릭리스너만들기 (스피너 항목 선택시 동작 지정) // setOnItemClickListener가 아닌 setOnItemSelectedListener를 사용해야함에 주의
    class CategorySpinerHandler implements AdapterView.OnItemSelectedListener{

        //스피너 항목 선택시 동작지정
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) { //3번째인자는 위치를 4번째인자는 id를 받는다.

            //all선택시 모든 책들을 보여줌
            if(position ==0){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bookList = bookDatabase.bookDao().getAll();

                    }
                });

                thread.start();
                try{
                    thread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }

                adapter.setItems(bookList);
            }


            //카테고리 선택시 해당 카테고리id를 가진 책들을 보여줌
            if(position != arrayList.size()-1 && position != 0){ //ALL,카테고리관리 외의 스피너항목클릭시
                String text = spinner.getSelectedItem().toString();
                //Log.d("TAG","text값" + text);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        selectCategoryList = bookDatabase.bookDao().getSelectCategory(text);

                    }
                });

                thread.start();


                try{
                    thread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }


                adapter.setItems(selectCategoryList);


            }


            if(position == arrayList.size()-1){

                //카테고리관리를 눌렀을때 AlertDialog창 생성 ==>AlertDialog.Builder로 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                mSelect = 0;
                builder.setSingleChoiceItems(management, 0, new DialogInterface.OnClickListener() {
                    //단일선택목록대화상자  //나타낼배열, 기본선택index, 클릭리스너

                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        //String management[] = {"카테고리 생성","카테고리 수정","카테고리 삭제"};
                        //int mSelect = 0;
                        mSelect = position; //항목선택시 멤버변수 mSelect에 선택한 항목의 위치 index저장
                    }
                });

                //확인버튼을 만들고 , 버튼 클릭시 수행할 기능작성
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (mSelect){
                            case 0 :                   //생성

                                //생성 항목 선택시 새로운 AlerterDialog.Builer를 띄운다.
                                AlertDialog.Builder createBuilder = new AlertDialog.Builder(getContext());
                                createBuilder.setTitle("새로 만들 카테고리 이름을 입력해주세요.");               //타이틀 설정

                                //사용자 정의 카테고리 작성

                                
                                LayoutInflater inflater = getLayoutInflater(); ////사용할 inflater가 없으므로 얻어온다.
                                ViewGroup creatLayout = (ViewGroup) inflater.inflate(R.layout.dialog_create,null);
                                createBuilder.setView(creatLayout);

                                //대화상자버튼 클릭시에만 대화상자가 종료되도록 설정
                                createBuilder.setCancelable(false);

                                //생성버튼
                                createBuilder.setPositiveButton("생성", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //여기서 카테고리 추가
                                        EditText etCreate = creatLayout.findViewById(R.id.etEdit);

                                        String category = etCreate.getText().toString().trim(); //arrayList에 붙여야하므로 String형으로 만들어준다.


                                        String tempCategory = null;
                                        for(int k = 0; k < categoryList.size(); k++){
                                            tempCategory = categoryList.get(k).getName();
                                            if(tempCategory.equals(category)){
                                                Toast.makeText(getContext(),"이미 카테고리가 존재합니다.",Toast.LENGTH_LONG).show();
                                                spinner.setSelection(0);
                                                break;
                                            }
                                        }

                                        if(tempCategory==null || !tempCategory.equals(category)){
                                            CategoryEntity categoryEntity = new CategoryEntity(category);
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bookDatabase.bookDao().categoryInsert(categoryEntity);
                                                    categoryList = bookDatabase.bookDao().getCategoryAll(); //중복추가되지않기위해

                                                }
                                            });

                                            thread.start();
                                            Toast.makeText(getContext(),categoryEntity.getName()+"가 추가되었습니다.",Toast.LENGTH_LONG).show();
                                            //Log.d("TAG",categoryEntity.getName()+"가 추가되었습니다.");

                                            try{
                                               thread.join();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                            if (category != null && !category.equals("")){ //null이 아니고 공백이 아닌경우 추가
                                                arrayList.add(arrayList.size()-1, categoryEntity.getName());

                                                arrayAdapter.notifyDataSetChanged(); //ArrayList를 들고있는 어댑터에 변경사항을 알려준다.
                                            }else{
                                                Toast.makeText(getContext(),"생성 실패 : 카테고리명을 입력해주세요.",Toast.LENGTH_LONG).show();
                                            }

                                        }


                                    }
                                });

                                //취소버튼
                                createBuilder.setNegativeButton("취소",null);

                                //위에서 설정한 대화상자 생성
                                AlertDialog createDialog = createBuilder.create();
                                createDialog.show(); //대화상자를 보여준다.

                                break;
                            case 1 :                   //수정
                                if( arrayList.size() <= 2){
                                    Toast.makeText(getContext(), " 수정할 카테고리가 없습니다. ", Toast.LENGTH_SHORT).show();
                                    spinner.setSelection(0);
                                    return;
                                }

                                AlertDialog.Builder changBuilder = new AlertDialog.Builder(getContext());
                                changBuilder.setTitle("수정할 카테고리를 선택해주세요.");

                                ArrayList<String> tempArrayList1 = new ArrayList<>();
                                tempArrayList1.addAll(arrayList);

                                tempArrayList1.remove(0);
                                tempArrayList1.remove(tempArrayList1.size()-1);

                                items = tempArrayList1.toArray(new String[tempArrayList1.size()]);

                                mSelect = 0;
                                changBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        mSelect = position;
                                    }
                                });

                                changBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //새로운 Dialog를 띄운다.
                                        AlertDialog.Builder editBuilder = new AlertDialog.Builder(getContext());
                                        editBuilder.setTitle("카테고리 이름을 수정해주세요.");

                                        LayoutInflater inflater = getLayoutInflater();
                                        ViewGroup editLayout = (ViewGroup)inflater.inflate(R.layout.dialog_edit,null);
                                        editBuilder.setView(editLayout);

                                        editBuilder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int position) {
                                                //수정로직

                                                EditText etEdit = editLayout.findViewById(R.id.etEdit);
                                                String category = etEdit.getText().toString().trim();

                                                int index = arrayList.indexOf(items[mSelect]);
                                                //public int indexOf(Object obj) : ArrayList안에 있는 원소에 해당하는 index를 반환한다.

                                                for(int i = 0; i < categoryList.size(); i++){
                                                    if(categoryList.get(i).getName().equals(arrayList.get(index))){
                                                        categoryName = categoryList.get(i);
                                                        categoryName.setName(category);
                                                        break;
                                                    }
                                                }


                                                Thread thread = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bookDatabase.bookDao().categoryUpdate(categoryName);
                                                    }
                                                });

                                                thread.start();


                                                if(category != null && !category.equals("")){
                                                    arrayList.set(index,category); //첫번째 인자의 위치에 있는 값을 두번째 인자값으로 변경
                                                    arrayAdapter.notifyDataSetChanged(); //어댑터에 알린다.
                                                }else{
                                                    Toast.makeText(getContext(),"수정 실패 : 수정할 이름을 입력해주세요.",Toast.LENGTH_LONG).show();
                                                }

                                                spinner.setSelection(index);
                                            }
                                        });

                                        editBuilder.setNegativeButton("취소",null);

                                        editBuilder.setCancelable(false);
                                        AlertDialog editDialog = editBuilder.create();
                                        editDialog.show();

                                    }
                                });

                                changBuilder.setNegativeButton("취소",null);

                                changBuilder.setCancelable(false);
                                AlertDialog changDialog = changBuilder.create();
                                changDialog.show();

                                break;

                            case 2 :                   //삭제
                                if( arrayList.size() <= 2){
                                    Toast.makeText(getContext(), " 삭제할 카테고리가 없습니다. ", Toast.LENGTH_SHORT).show();
                                    spinner.setSelection(0);
                                    return;
                                }

                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());
                                deleteBuilder.setTitle("삭제할 카테고리를 선택해주세요.");


                                //아래는 얕은 복사이므로 tempArrayList와 arrayList가 연결되어버린다.
                                //ArrayList<String> tempArrayList = new ArrayList<String>();
                                //tempArrayList = arrayList;

                                //깊은 복사 tempArrayList의 항목을 삭제해도 arrayList에는 영향이 없다.
                                ArrayList<String> tempArrayList2 = new ArrayList<>();
                                tempArrayList2.addAll(arrayList); //addAll()메소드로 깊은 복사를 할 수 있다.

                                tempArrayList2.remove(0); //ALL 항목을 보이지 않게한다.
                                tempArrayList2.remove(tempArrayList2.size()-1); //카테고리 관리항목을 보이지 않게한다.


                                //setSingleChoiceItem의 첫번째인자에 어댑터를 넣을 수도 있지만 정상적으로 출력되지 않는다.
                                //String배열을 넣어주면 정상적으로 출력되는데
                                //ArrayList를 String형으로 변경시켜야할 필요성이 있다.
                                items = tempArrayList2.toArray(new String[tempArrayList2.size()]);
                                //toArray()메소드는 컬렉션 형태로 되어있는 것들을 객체배열로 반환해준다.



                                mSelect = 0; //변수를 초기화해주자 초기화해주지않으면 잘못된값이 나올수도있다.
                                deleteBuilder.setSingleChoiceItems(items,0, new DialogInterface.OnClickListener() {
                                    //-1지정시 아무것도 선택되어있지 않음 //항목을 선택할때마다 두번째 인자값을 0으로 초기화해주자.
                                    //디폴트 상태

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) { //2
                                        mSelect = position;
                                        //Log.d("TAG", "선택한 순번 : " + mSelect);
                                    }
                                });


                                deleteBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        //삭제로직 - position이 아닌 이름으로 검색해보자.
                                        Log.d("TAG", "삭제할때의 순번 : " + mSelect);
                                        Log.d("TAG","원본의 데이타 갯수: " + items.length);
                                        String curItem = items[mSelect];

                                        //데이터베이스에서 제거

                                        for(int i = 0; i < categoryList.size(); i++){
                                            if(categoryList.get(i).getName().equals(curItem)){
                                                categoryName = categoryList.get(i);
                                                break;
                                            }
                                        }

                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                bookDatabase.bookDao().categoryDelete(categoryName);
                                            }
                                        });

                                        thread.start();


                                        //스피너에서 제거
                                        arrayList.remove(curItem);
                                        arrayAdapter.notifyDataSetChanged();

                                        spinner.setSelection(0);

                                    }
                                });


                                deleteBuilder.setNegativeButton("취소",null);

                                deleteBuilder.setCancelable(false);

                                AlertDialog deleteDialog = deleteBuilder.create();
                                deleteDialog.show();


                                break;
                        }
                    }
                });

                builder.setNegativeButton("취소",null);
                builder.setCancelable(false); //대화상자 버튼 클릭시에만 대화상자가 종료되도록 설정

                AlertDialog dialog = builder.create(); //위에서 설정한 대화상자 생성
                dialog.show(); //대화상자를 보여준다.
            }else if(position == 0){
                //처음 1번 자동동작하지 않게하기
                if(firstSpinner == false){
                    firstSpinner = true;
                    return;
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { //아무것도 선택하지 않은 경우

        }
    }

}