package com.jica.bookproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.BookEntity;
import com.jica.bookproject.database.CategoryEntity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    ImageView ivImageView;
    TextView tvTitle;
    TextView tvAuthor;
    Button btnRead;
    RatingBar ratingBar;
    TextView tvPublisher;
    TextView tvPubdate;
    TextView tvDescription;

    //완독날짜
    Calendar cal = Calendar.getInstance();
    TextView tvReadDate;
    LinearLayout readLayout;

    //접었다 펼치는 메뉴

    LinearLayout linearExpand1;
    LinearLayout linearExpand2;
    ImageView imagePlus1;
    ImageView imagePlus2;
    EditText editText1;
    EditText editText2;


    LinearLayout linearFrame1;
    LinearLayout linearFrame2;
    LinearLayout linearFrame3;
    LinearLayout linearFrame4;

    FrameLayout frameLayout1;
    FrameLayout frameLayout2;

    TextView tvParagraph;
    TextView tvReview;


    // flag = true 접기, flag = false 펼치기
    Boolean flag1 = false;
    Boolean flag2= false;

    //

    //구절,리뷰 저장
    ImageView imageSave1;
    ImageView imageSave2;

    //구절,리뷰 수정
    ImageView imageEdit1;
    ImageView imageEdit2;

    //구절,리뷰 내용삭제
    ImageView imageDelete1;
    ImageView imageDelete2;

    //카메라
    ImageView imageCamera;
    ImageView ivReview_et;
    ImageView ivReview_tv;
    //경로변수
    String mCurrentPhotoPath;

    //카테고리
    Button categorySelect;

    List<CategoryEntity> categoryList; //데이터베이스에서 받아온 값을 넣어줄 공간

    //내 서재에서 삭제
    Button bookDeleteBtn;



    Intent intent2;



    BookEntity bookEntity;
    BookDatabase bookDatabase;



    Context mContext;

    //리스트 선택시 위치를 나타내는 변수
    int mPosition;



    //백버튼
    //UserInfoActivity에서 뒤로가기 버튼을 눌렀을때 업데이트가 반영되어야하기때문에
    //onBackPressed()로 뒤로가기버튼의 동작을 지정해준다.
    //백버튼 눌렀을때 setResult로 응답코드 200에 해당하는 동작을 수행함 getAll()로 조회한 List를 어댑터에 붙임
    //어댑터에 붙여놔야 화면이 전개될때 결과가 뜨기때문
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(200); //사용자가 지정한 결과코드로 메소드를 돌림
        finish(); //백버튼을 누르면서 액티비티를 종료

    }

    //완독날짜를 위한 DatePickerDialog

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {

        //onDateSet() 함수 오버라이딩
        //DatePickerDialog 창이 활성화되고 사용자가 날짜 정보를 입력한 뒤 완료 버튼을 클릭하였을 때 실행되는 함수
        //여기서 데이터베이스를 업데이트 해준다.
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

            cal.set(Calendar.YEAR,year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //String readTime = format.format(cal.getTime());

            Date calDate = new Date(cal.getTimeInMillis());

            bookEntity.setReadDate(calDate);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    bookDatabase.bookDao().update(bookEntity);
                }
            });
            thread.start();

            tvReadDate.setText(format.format(cal.getTime()));
        }
    };




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            //SDK 29이상부터는 아래와같은 코드로 써야함
            switch (requestCode){
                case 1 : {
                    if(resultCode == RESULT_OK){
                        File file = new File(mCurrentPhotoPath); //경로로 파일 객체 생성
                        Bitmap bitmap;

                        if(Build.VERSION.SDK_INT >= 29){
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),Uri.fromFile(file)); //ImageDecoder 이미지 디코딩
                                                                                                        //Uri.fromFile(file)는 Uri객체를 반환한다.
                                                                                                        //경로인 String mCurrentPhotoPath -> Uri로 변경하려면 경로로 파일을 생성한후 Uri.fromFile(file)해준다.
                            try{

                                bitmap = ImageDecoder.decodeBitmap(source);
                                if(bitmap != null){

                                    //ivReview.setImageBitmap(bitmap);
                                    Glide.with(this).asBitmap().load(Uri.fromFile(file)).override(700,900).into(ivReview_et);
                                    Glide.with(this).asBitmap().load(Uri.fromFile(file)).override(700,900).into(ivReview_tv);

                                    Log.d("TAG","Uri.fromFile(file) : " + Uri.fromFile(file) + "mCurrentPhotoPath : " + mCurrentPhotoPath);

                                }

                            }catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }else{
                            try{
                                MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(file));
                            }catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }

                    }
                    break;
                }
            }




        }catch (Exception e){
            e.printStackTrace();
        }


        Log.d("TAG",mCurrentPhotoPath);

            //ivReview.setImageBitmap(imageBitmap);

           // Glide.with(this).asBitmap().load(imageBitmap).into(ivReview);


    }

    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"; //파일이름
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //사진저장용도의 디렉토리를 내어달라
        File image = File.createTempFile(imageFileName,".jpg",storageDir); //디렉토리로 지정된 폴더에 임시파일이 생성됨
                                        //파일이름            확장자   디렉토리

        mCurrentPhotoPath = image.getAbsolutePath();
                            //만든 파일의 절대경로를 경로변수에 넣는다.

        return image; //파일리턴
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mContext = this;


        bookDatabase = BookDatabase.getInstance(this);


        ivImageView = findViewById(R.id.ivImageView);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvPubdate = findViewById(R.id.tvPubdate);
        tvDescription = findViewById(R.id.tvDescription);

        btnRead = findViewById(R.id.btnRead);
        ratingBar = findViewById(R.id.ratingBar);

        //접었다 펼치는 메뉴
        linearExpand1 = findViewById(R.id.linearExpand1);
        linearExpand2 = findViewById(R.id.linearExpand2);
        imagePlus1 = findViewById(R.id.imagePlus1);
        imagePlus2 = findViewById(R.id.imagePlus2);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);

        linearFrame1 = findViewById(R.id.linearFrame1);
        linearFrame2 = findViewById(R.id.linearFrame2);
        linearFrame3 = findViewById(R.id.linearFrame3);
        linearFrame4 = findViewById(R.id.linearFrame4);

        frameLayout1 = findViewById(R.id.frameLayout1);
        frameLayout2 = findViewById(R.id.frameLayout2);

        tvParagraph = findViewById(R.id.tvParagraph);
        tvReview = findViewById(R.id.tvReview);


        //완독날짜
        tvReadDate = findViewById(R.id.tvReadDate);
        readLayout = findViewById(R.id.readLayout);


        //

        //구절,리뷰 저장
        imageSave1 = findViewById(R.id.imageSave1);
        imageSave2 = findViewById(R.id.imageSave2);

        //구절,리뷰 수정
        imageEdit1 = findViewById(R.id.imageEdit1);
        imageEdit2 = findViewById(R.id.imageEdit2);

        //구절,리뷰 내용삭제
        imageDelete1 = findViewById(R.id.imageDelete1);
        imageDelete2= findViewById(R.id.imageDelete2);

        //카메라
        imageCamera = findViewById(R.id.imageCamera);
        ivReview_et = findViewById(R.id.ivReview_et);
        ivReview_tv = findViewById(R.id.ivReview_tv);


        //카테고리
        categorySelect = findViewById(R.id.categorySelect);

        //내 서재에서 삭제
        bookDeleteBtn = findViewById(R.id.bookDeleteBtn);


        intent2 = getIntent(); //인텐트를 받아옴

        bookEntity = (BookEntity)intent2.getSerializableExtra("bundle"); //전체를 받아온다.



        if(bookEntity.getImage() == null){
            Glide.with(this).load(R.drawable.no_image_found).override(232,271).into(ivImageView);
        }else{
            Glide.with(this).load(bookEntity.getImage()).override(232,271).into(ivImageView);
        }
        tvTitle.setText(bookEntity.getTitle());
        tvAuthor.setText(bookEntity.getAuthor());
        tvPublisher.setText(bookEntity.getPublisher());
        tvPubdate.setText(bookEntity.getPubdate());
        ratingBar.setRating(bookEntity.getRating());

        if(bookEntity.getReadDate() != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String readTime = format.format(bookEntity.getReadDate());
            tvReadDate.setText(readTime);
        }else{
            tvReadDate.setText("");
        }


        //버튼을 클릭하지 않은 상태에서도 데이터를 보여줘야하기때문에
        if(bookEntity.isClear()==false){
            btnRead.setText("읽는중");
            readLayout.setVisibility(View.INVISIBLE);
        }else{
            btnRead.setText("완독");
            readLayout.setVisibility(View.VISIBLE);
        }





        //구절,리뷰 보여줌
        //Log.d("TAG","paragraph : " + bookEntity.getParagraph());

        //데이터가 없으면 EditText 있으면 TextView로 보여줌

        if(bookEntity.getParagraph()==null || bookEntity.getParagraph().equals("")){
            linearFrame1.setVisibility(View.VISIBLE);
            linearFrame2.setVisibility(View.GONE);
        }else{
            linearFrame1.setVisibility(View.GONE);
            linearFrame2.setVisibility(View.VISIBLE);
        }

        if(bookEntity.getReview()==null || bookEntity.getReview().equals("")){
            linearFrame3.setVisibility(View.VISIBLE);
            linearFrame4.setVisibility(View.GONE);
        }else{
            linearFrame3.setVisibility(View.GONE);
            linearFrame4.setVisibility(View.VISIBLE);
        }


        editText1.setText(bookEntity.getParagraph());
        tvParagraph.setText(bookEntity.getParagraph());

        editText2.setText(bookEntity.getReview());
        tvReview.setText(bookEntity.getReview());

        if(bookEntity.getPhotoPath() == null || bookEntity.getPhotoPath().equals("")){
            ivReview_tv.setVisibility(View.GONE);
        }else{
            ivReview_tv.setVisibility(View.VISIBLE);
            Glide.with(this).asBitmap().load(bookEntity.getPhotoPath()).override(700,900).into(ivReview_tv);
            Glide.with(this).asBitmap().load(bookEntity.getPhotoPath()).override(700,900).into(ivReview_et);
        }


        //완독날짜 클릭시 DatePickerDialog를 이용하여 날짜설정

        readLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(mContext,myDatePicker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();


            }
        });






        //카테고리

        categorySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        categoryList = bookDatabase.bookDao().getCategoryAll();
                    }
                });

                thread.start();

                try{
                    thread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //Log.d("TAG","카테고리 이름" + categoryList.get(0).getName());


                try{
                    //생성 항목 선택시 새로운 AlerterDialog.Builer를 띄운다.

                    if(categoryList == null || categoryList.size()==0){
                        Toast.makeText(mContext,"카테고리를 먼저 생성해주세요",Toast.LENGTH_LONG).show();
                    }else{
                        AlertDialog.Builder changBuilder = new AlertDialog.Builder(mContext);
                        changBuilder.setTitle("책을 포함시킬 카테고리를 선택해주세요");
                        //수정 String[] categoryArray = categoryList.toArray(new String[categoryList.size()]);

                        //리스트어댑터
                        ArrayAdapter categoryAdapter = new ArrayAdapter(mContext,android.R.layout.select_dialog_singlechoice);

                        for(int i = 0; i <categoryList.size(); i++){
                            categoryAdapter.add(categoryList.get(i).getName());
                        }


                        changBuilder.setSingleChoiceItems(categoryAdapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                mPosition = position;

                            }
                        });

                        //확인버튼
                        changBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //확인버튼을 눌렀을때 수행되어야할 일
                                //1. 책에 category의 id값을 부여한다. (bookEntity - CategoryEntity 참조)
                                //2. HomeFragment의 스피너에서 해당 카테고리 선택시 동일 category의 id값을 가진 책들을 띄운다.

                                bookEntity.setCategoryId(categoryList.get(mPosition).getId());

                                //Log.d("TAG","카테고리 id : " + bookEntity.getCategoryId());

                                //데이터베이스에 카테고리 id를 Update

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bookDatabase.bookDao().update(bookEntity);
                                    }
                                });

                                thread.start();
                                //Log.d("TAG","카테고리 id가 업데이트되었습니다. : " + bookEntity.getCategoryId());
                                Toast.makeText(mContext,"카테고리 id가 업데이트 되었습니다." +bookEntity.getCategoryId(),Toast.LENGTH_LONG).show();


                            }
                        });

                        //취소버튼
                        changBuilder.setNegativeButton("취소",null);

                        //위에서 설정한 대화상자 생성
                        AlertDialog createDialog = changBuilder.create();
                        createDialog.show(); //대화상자를 보여준다.

                    }


                }catch (Exception e){
                    Toast.makeText(mContext,"카테고리를 먼저 생성해주세요",Toast.LENGTH_LONG).show();
                }









            }
        });


        //내 서재에서 삭제 버튼
        bookDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bookDatabase.bookDao().delete(bookEntity);
                    }
                });

                thread.start();

                Toast.makeText(mContext,"내 서재에서 삭제되었습니다.",Toast.LENGTH_LONG).show();


            }
        });


        //별점, 완독여부 데이터베이스에 업데이트


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                //Log.d("TAG",""+rating);

                bookEntity.setRating(rating); //bookEntity에서 rating만 변경

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bookDatabase.bookDao().update(bookEntity);


                    }
                }).start();


            }
        });



        //완독-읽는중


        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bookEntity.isClear()==false){
                    bookEntity.setClear(true);
                    btnRead.setText("완독");
                    //Log.d("TAG","완독"+bookEntity.isClear());

                    //버튼의 텍스트가 완독이 되었을때 클릭한 시점의 날짜를 tvReadDate에 보여준다.
                    cal = Calendar.getInstance(); //다시 현재시간을 받아온다. DatePickerDialog에서 변경시킨 cal이 적용되면 안되므로
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String readTime = format.format(cal.getTime());

                    Date calDate = new Date(cal.getTimeInMillis());


                    bookEntity.setReadDate(calDate);
                    tvReadDate.setText(readTime);

                    readLayout.setVisibility(View.VISIBLE);

                }else{
                    bookEntity.setClear(false);
                    btnRead.setText("읽는중");
                    //Log.d("TAG","읽는중"+bookEntity.isClear());
                    readLayout.setVisibility(View.INVISIBLE);

                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bookDatabase.bookDao().update(bookEntity);
                    }
                }).start();

            }
        });


        //접었다 펼치기
        // flag = true 접기, flag = false 펼치기


        linearExpand1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag1 == true){
                    frameLayout1.setVisibility(View.GONE);
                    imagePlus1.setImageResource(android.R.drawable.ic_input_add);

                    flag1 = false;
                }else{
                    frameLayout1.setVisibility(View.VISIBLE);
                    imagePlus1.setImageResource(android.R.drawable.ic_delete);

                    flag1 = true;
                }
            }
        });

        linearExpand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag2 == true){
                    frameLayout2.setVisibility(View.GONE);
                    imagePlus2.setImageResource(android.R.drawable.ic_input_add);

                    flag2 = false;
                }else{
                    frameLayout2.setVisibility(View.VISIBLE);
                    imagePlus2.setImageResource(android.R.drawable.ic_delete);

                    flag2 = true;
                }

            }
        });

        //구절,리뷰 저장
        imageSave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookEntity.setParagraph(editText1.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bookDatabase.bookDao().update(bookEntity);
                    }
                }).start();



                if(bookEntity.getParagraph()==null || bookEntity.getParagraph().equals("")){
                    Toast.makeText(getApplicationContext(),"텍스트를 입력해주세요.",Toast.LENGTH_LONG).show();
                }else{

                    linearFrame1.setVisibility(View.GONE);
                    linearFrame2.setVisibility(View.VISIBLE);
                    tvParagraph.setText(bookEntity.getParagraph()); //저장하자마자 뜨도록 설정
                    Toast.makeText(getApplicationContext(),bookEntity.getParagraph() + "구절이 저장되었습니다.",Toast.LENGTH_LONG).show();
                }




            }
        });

        imageSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookEntity.setReview(editText2.getText().toString());
                bookEntity.setPhotoPath(mCurrentPhotoPath);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bookDatabase.bookDao().update(bookEntity);
                        bookDatabase.bookDao().update(bookEntity);
                    }
                }).start();

                if(bookEntity.getReview()==null || bookEntity.getReview().equals("")){
                    Toast.makeText(getApplicationContext(),"텍스트를 입력해주세요.",Toast.LENGTH_LONG).show();
                }else{
                    linearFrame3.setVisibility(View.GONE);
                    linearFrame4.setVisibility(View.VISIBLE);
                    tvReview.setText(bookEntity.getReview());

                    ivReview_tv.setVisibility(View.VISIBLE);


                    Toast.makeText(getApplicationContext(),"리뷰가 저장되었습니다.",Toast.LENGTH_LONG).show();
                    //Log.d("TAG","데이터베이스에 저장된 이미지 경로" + bookEntity.getPhotoPath());
                }


            }
        });

        //구절, 리뷰 수정
        imageEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFrame1.setVisibility(View.VISIBLE);
                linearFrame2.setVisibility(View.GONE);

            }
        });

        imageEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFrame3.setVisibility(View.VISIBLE);
                linearFrame4.setVisibility(View.GONE);
                ivReview_et.setVisibility(View.VISIBLE);

            }
        });

        //구절, 리뷰 내용 삭제
        imageDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText1.setText("");
            }
        });

        imageDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText2.setText("");
                ivReview_et.setImageBitmap(null);
                ivReview_et.setVisibility(View.GONE);


            }

        });


        //카메라
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                //resolveActivity()의 호출결과가 null이 아니라면 실행될 수 있는 컴포넌트가 있다는 의미이므로
                //startActivity()가 정상적으로 수행될 것이고, 반대로 null이라면 실행될 컴포넌트가 없으므로 startActivity()를 호출하지 않도록
                // 해당 UI를 비활성 처리해야 할 것


                if(intent.resolveActivity(getPackageManager()) != null){ //인텐트가 정상실행됐을경우
                    File photoFile = null;

                    try {
                        photoFile = createImageFile(); //사용자가 만든 crateImageFile()로 파일을 리턴받아온다.
                    }catch (IOException ex){}

                    if(photoFile != null){
                        Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.jica.bookproject.fileprovider",photoFile);
                        //FileProvider를 이용에 Uri를 꺼내옴            Context                     패키지명                                파일
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        //MediaStore.EXTRA_OUTPUT로 Uri지정
                        startActivityForResult(intent,1); //액티비티를 끝낼때 결과를 함께 보내는 메소드

                    }


                }

                ivReview_et.setVisibility(View.VISIBLE);





            }
        });

    }



}

