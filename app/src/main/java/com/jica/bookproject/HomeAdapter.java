package com.jica.bookproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jica.bookproject.database.BookDatabase;
import com.jica.bookproject.database.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<com.jica.bookproject.HomeAdapter.ViewHolder> {
        List<BookEntity> items = new ArrayList<BookEntity>();
        //1) 어댑터에 원본데이터를 따로 설정해서
        //2) MainActivity의 핸들러에서 items를 받아 어댑터의 원본데이터를 변경해주고
        //3) 어댑터에게 알려준다.

        Context mContext2;
        Fragment homeFragment;



        BookDatabase bookDatabase;

        HomeAdapter(Context context,Fragment fragment){ //현재어댑터를 사용하는 프래그먼트가져옴
            mContext2 = context;
            homeFragment = fragment; //homeFragment.startActivityForResult에 필요함으로 프레그먼트를 얻어옴
        }


        @NonNull
        @Override
        public com.jica.bookproject.HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.book_search,parent,false);
            return new com.jica.bookproject.HomeAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull com.jica.bookproject.HomeAdapter.ViewHolder viewHolder, int position) {
            BookEntity item = items.get(position);
            viewHolder.setItem(item,mContext2,homeFragment);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }



        public  void addItem(BookEntity item){
            items.add(item);
            notifyItemInserted(items.size()-1); //데이터 갱신시 알려주는 부분
        }



        public void setItems(List<BookEntity> items){
            this.items = items;
            notifyDataSetChanged(); //어댑터에 변경사항을 알려줌

        }





        public BookEntity getItem(int position){
            return items.get(position);
        }



        static class ViewHolder extends RecyclerView.ViewHolder{
            TextView textView3;
            TextView textView4;
            ImageView imageView2;
            ConstraintLayout itemContainer2;
            RatingBar ratingBar;
            TextView clear;





            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textView3= itemView.findViewById(R.id.textView3);
                textView4 = itemView.findViewById(R.id.textView4);
                imageView2 = itemView.findViewById(R.id.imageView2);
                itemContainer2 = itemView.findViewById(R.id.itemContainer2);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                clear = itemView.findViewById(R.id.clear);



            }

            public void setItem(BookEntity item, Context mContext2, Fragment homeFragment){
                textView3.setText(item.getTitle());
                textView4.setText(item.getAuthor());

                Log.d("TAG","item.getImage() : " + item.getImage());

                if(item.getImage()==null){
                    Glide.with(itemView).load(R.drawable.no_image_found).override(232,271).into(imageView2);
                }else{
                    Glide.with(itemView).load(item.getImage()).override(232,271).into(imageView2);
                    //해당환경의context 혹은 객체.String주소값,받아온이미지를 받을 공간
                }
                ratingBar.setRating(item.getRating());
                
                if(item.isClear()==true){
                    clear.setText("완독");
                }else{
                    clear.setText("읽는중");
                }


                itemContainer2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent2 = new Intent(mContext2,UserInfoActivity.class);


                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bundle",item);
                        intent2.putExtras(bundle);

                        homeFragment.startActivityForResult(intent2,200); //프래그먼트에서 startAcitivityRorResult로 액티비티를 호출하면서 requesetCode 200을 넘겨줌

                    }
                });



            }
        }
    }



