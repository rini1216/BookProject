package com.jica.bookproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.BookEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.jica.bookproject.MainActivity.searchFragment;

public class InfoActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvAuthor;
    TextView tvPublisher;
    TextView tvPubdate;
    TextView tvDescription;
    ImageView ivImageView;
    Intent intent;

    Button addBook;

    BookDatabase bookDatabase; //만든 데이터베이스를 담음


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String title;
        String author;
        String publisher;
        String pubdate;
        String description;
        String image;


        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvPubdate = findViewById(R.id.tvPubdate);
        tvDescription = findViewById(R.id.tvDescription);
        ivImageView = findViewById(R.id.ivImageView);

        addBook = findViewById(R.id.addBook);

        intent = getIntent(); //intent를 받아온다

        //클릭한 항목뷰에서 받아옴
        title = intent.getStringExtra("name");
        author = intent.getStringExtra("author");
        publisher = intent.getStringExtra("publisher");
        pubdate = intent.getStringExtra("pubdate");
        description = intent.getStringExtra("description");
        image = intent.getStringExtra("image");

        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvPublisher.setText(publisher);
        tvPubdate.setText(pubdate);
        tvDescription.setText(description);

        Glide.with(this).load(image).override(232,271).into(ivImageView);


        bookDatabase = BookDatabase.getInstance(this);


        //내서재추가버튼 동작 - 이 시점에서 데이터베이스에 들어가기원하기때문
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BookEntity bookEntity = new BookEntity(title,author,publisher,pubdate,description,image,0,false,null,null, null,0,null); //Entity에 전체를 가져가 생성자 동작시킴

                Runnable runnable = new Runnable() {   //동작을 지정
                    @Override
                    public void run() {
                        bookDatabase.bookDao().insert(bookEntity); //insert에서 데이터베이스 연산이 일어나기때문에 이부분을 쓰레드로 돌려준다.(메인쓰레드에서 돌리는게 막혀있기때문)

                    }
                };

                Executor executor= Executors.newSingleThreadExecutor(); // Executor객체를 만들어서 excute로 runnable을 받으면 위에서 지정한 동작이 실행함
                                                                        // 쓰레드를 실행하는 방식은 다양하다(start 등..)
                executor.execute(runnable);

                Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_LONG).show();


            }
        });

    }
}