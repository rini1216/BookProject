package com.jica.bookproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jica.bookproject.database.BookEntity;

import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    ArrayList<BookItem> items = new ArrayList<BookItem>();
    //1) 어댑터에 원본데이터를 따로 설정해서
    //2) MainActivity의 핸들러에서 items를 받아 어댑터의 원본데이터를 변경해주고
    //3) 어댑터에게 알려준다.

    Context mContext; //Book

    BookAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.book_search2,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        BookItem item = items.get(position);
        viewHolder.setItem(item,mContext);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public  void addItem(BookItem item){
        items.add(item);
    }

    public void setItems(ArrayList<BookItem> items){
        this.items = items;
    }

    public BookItem getItem(int position){
        return items.get(position);
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView2;
        ImageView imageView;
        LinearLayout itemContainer;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);
            itemContainer = itemView.findViewById(R.id.itemContainer);

        }

        public void setItem(BookItem item, Context mContext){

            textView.setText(item.title);
            textView2.setText(item.author);

            if(item.getImage()==null){
                Glide.with(itemView).load(R.drawable.no_image_found).override(232,271).into(imageView);
            }else{
                Glide.with(itemView).load(item.image).override(232,271).into(imageView);
                //해당환경의context 혹은 객체.String주소값,받아온이미지를 받을 공간
            }

            itemContainer.setOnClickListener(new View.OnClickListener() { //항목뷰의 클릭리스너
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,InfoActivity.class); //MainActivity의 context

                    intent.putExtra("name",item.getTitle());
                    intent.putExtra("author",item.getAuthor());
                    intent.putExtra("publisher",item.getPublisher());
                    intent.putExtra("pubdate",item.getPubdate());
                    intent.putExtra("description",item.getDescription());
                    intent.putExtra("image",item.getImage());

                    mContext.startActivity(intent);


                }
            });


        }
    }
}
