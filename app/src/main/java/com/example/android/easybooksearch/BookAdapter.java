package com.example.android.easybooksearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by JOAO on 24-Apr-18.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public static final String LOG_TAG = MainActivity.class.getName();

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        if (position < getCount()){
            Book currentBook = getItem(position);

            TextView tv_author = (TextView) listItemView.findViewById(R.id.book_author);
            tv_author.setText(currentBook.getAuthor());

            TextView tv_title = (TextView) listItemView.findViewById(R.id.book_title);
            tv_title.setText(currentBook.getTitle());

            ImageView imageView = (ImageView) listItemView.findViewById(R.id.book_image);
            new bookImageAsyncTask (imageView).execute(currentBook.getImageUrl());
        }
        return listItemView;
    }

    private class bookImageAsyncTask extends AsyncTask<String, Void, Bitmap>{
        ImageView image;

        public bookImageAsyncTask(ImageView btmpImage) {
            this.image = btmpImage;
        }

        protected Bitmap doInBackground(String... urls){
            String url = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in); //this is used for decode the InputStream to image
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            //set the image obtained
            image.setImageBitmap(result);
        }
    }
}

